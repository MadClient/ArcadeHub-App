
package com.yunluo.android.arcadehub.netplay.skt;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.yunluo.android.arcadehub.BaseApplication;
import com.yunluo.android.arcadehub.Emulator;
import com.yunluo.android.arcadehub.GameListActivity;
import com.yunluo.android.arcadehub.R;
import com.yunluo.android.arcadehub.interfac.OnNpListener;
import com.yunluo.android.arcadehub.netplay.Category;
import com.yunluo.android.arcadehub.netplay.NetPlayActivity;
import com.yunluo.android.arcadehub.netplay.adapter.NetDeviceAdapter;
import com.yunluo.android.arcadehub.netplay.obj.DeviceInfo;
import com.yunluo.android.arcadehub.utils.Debug;
import com.yunluo.android.arcadehub.utils.FileUtil;
import com.yunluo.android.arcadehub.utils.SharePreferenceUtil;
import com.yunluo.android.arcadehub.utils.Utils;

public class GameWifiSkt {

    public static boolean DEBUG = true;

    private static ProgressDialog progressDialog;

    public static final int SKT_INIT_GAMES = 1001;

    public static final int SKT_JONI_GAMES = 1002;

    public static final int SKT_SEND_BROADCAST = 1003;

    public static final int SKT_IP_LIST = 1004;

    public static final int SKT_REFUSE_GAMES = 1005;

    public static final int SKT_MISS_ROM = 1006;

    public static final int ACTION_SKT_START = 1;

    public static final int ACTION_SKT_JOIN = 2;

    public static final int ACTION_SKT_BC_SEND = 3;

    public static final int ACTION_SKT_JOIN_ACK = 4;

    public static final int ACTION_SKT_JOIN_ACK_AGREE = 5;

    public static final int ACTION_SKT_JOIN_ACK_REFUSE = 6;

    public static final int ACTION_SKT_JOIN_ACK_MISSROM = 7;

    public static final int ACTION_SKT_JOIN_ACK_TRANSFER_AGREE = 8;

    public static final int ACTION_SKT_JOIN_ACK_TRANSFER_REFUTE = 9;

    public static final int RECEIVER_FINISH = 0;

    public static final int SEND_FINISH = 1;

    public static final int RECEIVER_ERROR = 2;

    public static final int SEND_ERROR = 3;

    public static final int REQUEST_SERVER = 1;

    public static final int REQUEST_CLIENT = 2;

    public static final int RESPONSE_AGREE = 1;

    public static final int RESPONSE_REFUSE = 2;
    public static final int CONNECT_STATE_NONE = 0;

    public static final int CONNECT_STATE_CONNECTING = 1;

    public static int CONNECT_STATE = CONNECT_STATE_NONE;

    private static NetPlayActivity mContext = null;

    private GameListActivity mGameListContext = null;

    private static Timer mTimer = null;

    private static Timer mBcTimer = null;

    private NetDeviceAdapter mSameAdapter;
    private NetDeviceAdapter mDifAdapter;

    private static ProgressDialog mServerDialog;

    private String mLocalAddress = null;

    private String serverAddress = null;

    private Resources res = null;

    public ArrayList<Category> mSameCategoryList = new ArrayList<Category>();
    public ArrayList<Category> mDifCategoryList = new ArrayList<Category>();

    public Category curList = null;

    public Category otherList = null;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SKT_INIT_GAMES: 
                    String romName = SharePreferenceUtil.loadName(mContext);
                    if(null == mGameListContext || null == romName){
                        return;
                    }
                    Emulator.broadcastDeviceInformation(1, Utils.getWFAddress(mContext),
                            mGameListContext.getCpu(), mGameListContext.getRam(), romName);
                    Emulator.setWIFIGame(romName);
                    initLsv();
                    start();
                    sendBroadcast();
                    break;
                case SKT_JONI_GAMES:
                    dismissListDialog(false);
                    serverAddress = (String)msg.obj;
                    Emulator.setDeviceIP(serverAddress);
                    String msgInfo = mLocalAddress + "\n"
                            + res.getString(R.string.network_request_join);
                    join();
                    startTimer();
                    initProgressDialog(msgInfo);
                    break;
                case SKT_SEND_BROADCAST:
                    send();
                    startBcTimer();
                    break;
                case SKT_IP_LIST:
                    Bundle bun = msg.getData();
                    String ip = bun.getString("ip");
                    String name = bun.getString("name");                    
                    
                    ArrayList<DeviceInfo> list = new ArrayList<DeviceInfo>(curList.getList());
                    for (DeviceInfo info : list) {
                        if (info.getIp().equals(ip)) {
                            if(info.getName().equals(name)) {
                                return;
                            } else {
                                if(curList.contains(info)) {
                                    curList.remove(info);
                                }
                                notifyData();
                                mSameAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                    
                    ArrayList<DeviceInfo> difList = new ArrayList<DeviceInfo>(otherList.getList());
                    for (DeviceInfo info : difList) {
                        if (info.getIp().equals(ip)) {
                            if(info.getName().equals(name)) {
                                return;
                            } else {
                                if(otherList.contains(info)) {
                                    otherList.remove(info);
                                }
                                mDifAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                    

                    String desc = bun.getString("desc");
                    int os = bun.getInt("os");
                    int cpu = bun.getInt("cpu");
                    int ram = bun.getInt("ram");

                    DeviceInfo mDeviceInfo = new DeviceInfo();
                    mDeviceInfo.setName(name);
                    mDeviceInfo.setDesc(desc);
                    mDeviceInfo.setOs(os);
                    mDeviceInfo.setIp(ip);
                    mDeviceInfo.setCpu(cpu);
                    mDeviceInfo.setRam(ram);

                    Debug.d("DeviceInfo", mDeviceInfo);
                    String defaultName = SharePreferenceUtil.loadName(mContext);
                    if (defaultName.equals(name)) {
                        curList.addItem(mDeviceInfo);
                        int id = curList.getId(mDeviceInfo)+1;
                        mDeviceInfo.setId(id);
                    } else {
                        otherList.addItem(mDeviceInfo);
                        int id = otherList.getId(mDeviceInfo)+1;
                        mDeviceInfo.setId(id);
                    }
                    mSameAdapter.notifyDataSetChanged();
                    mDifAdapter.notifyDataSetChanged();
                    break;
                case SKT_REFUSE_GAMES:
                    reset();
                    stopGameover();
                    break;
                case SKT_MISS_ROM:
                    dismissListDialog(false);
                    serverAddress = (String)msg.obj;
                    showMissRom();
                    break;
                default:
                    break;
            }

        }

    };

    BaseApplication mApp = null;
    
    private ListView mSameListView = null;
    private ListView mDifListView = null;

    public GameWifiSkt(NetPlayActivity context, View mNtView) {
        mContext = context;
        
        context.setOnNpListener(mOnNpListener);
        
        mSameListView = (ListView) mNtView.findViewById(R.id.same_lsv);
        
        mDifListView = (ListView) mNtView.findViewById(R.id.dif_lsv);
        
        mApp = (BaseApplication)mContext.getApplication();
        if (null == mApp) {
            mContext.finish();
        }
        mGameListContext = mApp.getGameListActivity();
        if (null == mGameListContext) {
            mContext.finish();
        }
        mLocalAddress = Utils.getWFAddress(mContext);
        res = mContext.getResources();

        curList = new Category(res.getString(R.string.TAB_GAMEROOMS_CURRENT));

        otherList = new Category(res.getString(R.string.TAB_GAMESROOMS_OTHER));

        mSameCategoryList.add(curList);

        mDifCategoryList.add(otherList);
    }

    // get iplist
    public ArrayList<DeviceInfo> getIpList() {
        return curList.getList();
    }

    // init ProgressDialog
    private void initProgressDialog(String msg) {
        if (null == mContext && false == mContext.isFinishing()) {
            return;
        }
        progressDialog = ProgressDialog.show(mContext,
                res.getString(R.string.BTN_TEXT_GAMETYPE_NETWORK), msg, true, true);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                dimissDialog();
                stopTimer();
                stopGameover();
                stopBcTimer();
            }
        });
    }

    public Handler getHandler() {
        return mHandler;
    }

    public void startWifiGame() {
        mHandler.sendMessage(createMessage(SKT_INIT_GAMES, ""));
    }

    public void sendBroadcast() {
        mHandler.sendMessage(createMessage(SKT_SEND_BROADCAST, ""));
    }

    private void initDialog() {
    }
    
    private void initLsv() {
        
        curList.clear();
        otherList.clear();
        
        mSameAdapter = new NetDeviceAdapter(mContext, mSameCategoryList);
        mDifAdapter = new NetDeviceAdapter(mContext, mDifCategoryList);
        
        if (null == mSameListView || null == mDifListView) {
            mContext.finish();
        }
        mSameListView.setAdapter(mSameAdapter);
        mSameListView.setOnItemClickListener(mSameOnItemClickListener);
        
        mDifListView.setAdapter(mDifAdapter);
        mDifListView.setOnItemClickListener(mDifOnItemClickListener);

        Emulator.dimissLoading();
    }
    
    private OnItemClickListener mSameOnItemClickListener = new OnItemClickListener() {
        
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            if(arg2 == 0) {
                return;
            }
            ListAdapter adapter = mSameListView.getAdapter();
            if(null == adapter) {
                return;
            }
            
            DeviceInfo mInfo = (DeviceInfo)adapter.getItem(arg2);
            if (null == mInfo) {
                return;
            }
            String name = mInfo.getName();
            String address = mInfo.getIp();
            
            if (true == mGameListContext.isFound(name)) {
                mHandler.sendMessage(createMessage(SKT_JONI_GAMES, address));
            } else {
                Emulator.setWIFIGame(name);
                SharePreferenceUtil.saveName(mContext, name);
                mHandler.sendMessage(createMessage(SKT_MISS_ROM, address));
            }
        }
        
    };
    
    private OnItemClickListener mDifOnItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

            if(arg2 == 0) {
                return;
            }
            ListAdapter adapter = mDifListView.getAdapter();
            if(null == adapter) {
                return;
            }
            
            DeviceInfo mInfo = (DeviceInfo)adapter.getItem(arg2);
            if (null == mInfo) {
                return;
            }
            String name = mInfo.getName();
            String address = mInfo.getIp();

            Debug.d("server wifi address", address);
            Debug.d("server wifi name", name);
            if (true == mGameListContext.isFound(name)) {
                mHandler.sendMessage(createMessage(SKT_JONI_GAMES, address));
            } else {
                Emulator.setWIFIGame(name);
                SharePreferenceUtil.saveName(mContext, name);
                mHandler.sendMessage(createMessage(SKT_MISS_ROM, address));
            }
        }

    };

    @SuppressWarnings("deprecation")
    private void initServerDialog() {
        mServerDialog = new ProgressDialog(mContext);
        mServerDialog.setTitle(res.getString(R.string.BTN_TEXT_GAMETYPE_NETWORK));
        mServerDialog.setMessage(res.getString(R.string.network_waiting_join));
        mServerDialog.setCancelable(true);
        mServerDialog.setButton(res.getString(R.string.network_dialog_single),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        stopTimer();
                        stopGameover();
                        stopBcTimer();
                        mGameListContext.showGamesPlay(false);
                        Emulator.startGames();
                    }
                });
        mServerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {

                Debug.e("mServerDialog.setOnCancelListener", "");
                dismissServerDialog();
                stopTimer();
                stopGameover();
                stopBcTimer();
            }
        });
        mServerDialog.show();
    }

    private void startTimer() {
        if (mTimer == null) {
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {

                @Override
                public void run() {
                    mGameListContext.getHandler().post(new Runnable() {

                        @Override
                        public void run() {
                            dimissDialog();
                            stopTimer();
                            stopGameover();
                            stopBcTimer();
                        }
                    });
                }

            }, 30 * 1000);
        }
    }

    private void startBcTimer() {
        if (mBcTimer == null) {
            mBcTimer = new Timer();
            mBcTimer.schedule(new TimerTask() {

                @Override
                public void run() {
                    Debug.d("send broadcast", "");
                    send();
                }
            }, 3000, 1000);
        }
    }

    public static void stop() {
        stopTimer();
        stopBroadcast();
    }

    public static void reset() {
        dimissDialog();
        stopTimer();
        stopBroadcast();
    }

    private static  boolean isDimiss = true;
    public static void dimissDialog() {
        if(true == isDimiss){
            isDimiss = false;
            if(null != mContext && false == mContext.isFinishing()) {
                if (null != progressDialog && progressDialog.isShowing()) {
                   progressDialog.dismiss();
                   progressDialog = null;
               }
           }
           isDimiss = true;
        }
    }

    public static void dismissListDialog(boolean finishKey) {
        if (null != mContext && finishKey == true) {
            mContext.finish();
        }
    }

    public static void dismissServerDialog() {
        if (null != mContext && false == mContext.isFinishing()) {
            if (null != mServerDialog && mServerDialog.isShowing()) {
                    mServerDialog.dismiss();
                    mServerDialog = null;
            }
        }
    }

    private static void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    private static void stopBcTimer() {
        if (null != mBcTimer) {
            mBcTimer.cancel();
            mBcTimer = null;
        }
    }

    public void showRefuseDialog(String ipAddress) {
        new AlertDialog.Builder(mContext)
                .setTitle(res.getString(R.string.network_dialog_warning))
                .setMessage(serverAddress + "\n" + res.getString(R.string.network_dialog_refuse))
                .setCancelable(false)
                .setPositiveButton(res.getString(R.string.BTN_COMMON_OK),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dimissDialog();
                                stopTimer();
                                stopGameover();
                                stopBcTimer();
                            }
                        }).show();
    }

    public void showConnect(String ipAddress) {
        if (CONNECT_STATE == CONNECT_STATE_CONNECTING) {
            Debug.d("GameWifiSkt", "MSG");
            return;
        }
        CONNECT_STATE = CONNECT_STATE_CONNECTING;
        dismissListDialog(false);

        Emulator.setDeviceIP(ipAddress);

        String msg = ipAddress + "\n" + res.getString(R.string.network_request_allow);
        Debug.d("clent ipAddress", ipAddress + " request join games.");

        new AlertDialog.Builder(mContext)
                .setTitle(res.getString(R.string.network_dialog_warning))
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(res.getString(R.string.BTN_COMMON_OK),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Emulator.sendMessageToSocket(ACTION_SKT_JOIN_ACK_AGREE);
                            }
                        })
                .setNegativeButton(res.getString(R.string.BTN_COMMON_CANCEL),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Emulator.sendMessageToSocket(ACTION_SKT_JOIN_ACK_REFUSE);
                                CONNECT_STATE = CONNECT_STATE_NONE;
                                stopGameover();
                                stopBcTimer();
                            }
                        }).show();
    }

    public void showMissRom() {
        new AlertDialog.Builder(mContext)
                .setTitle(res.getString(R.string.network_dialog_missrom_title))
                .setMessage(res.getString(R.string.network_dialog_missrom_msg))
                .setCancelable(false)
                .setPositiveButton(res.getString(R.string.BTN_COMMON_OK),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (false == Utils.checkAvailableStore()) {
                                    Toast.makeText(mContext,
                                            res.getString(R.string.no_enough_space),
                                            Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                initProgressDialog(res
                                        .getString(R.string.network_dialog_missrom_waiting));

                                Emulator.setDeviceIP(serverAddress);

                                Emulator.sendMessageToSocket(ACTION_SKT_JOIN_ACK_MISSROM);
                            }
                        })
                .setNegativeButton(res.getString(R.string.BTN_COMMON_CANCEL),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CONNECT_STATE = CONNECT_STATE_NONE;
                                stopTimer();
                                stopGameover();
                                stopBcTimer();
                            }
                        }).show();
    }

    public void showSharedRom(final String ipAddress) {

        new AlertDialog.Builder(mContext)
                .setTitle(res.getString(R.string.network_dialog_sharerom_title))
                .setMessage(res.getString(R.string.network_dialog_sharerom_msg))
                .setCancelable(false)
                .setPositiveButton(res.getString(R.string.BTN_COMMON_OK),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                initProgressDialog(res
                                        .getString(R.string.network_dialog_sharerom_transfering));
                                new Thread(new Runnable() {

                                    @Override
                                    public void run() {
                                        dismissListDialog(false);
                                        String path = SharePreferenceUtil.loadPath(mContext);
                                        if (false == path.endsWith(File.separator)) {
                                            path = path + File.separator;
                                        }
                                        String name = SharePreferenceUtil.loadName(mContext);
                                        Debug.d("rom path:", path + name + ".zip");
                                        Emulator.hintTransferSendRomCB(path + name + ".zip");
                                    }
                                }).start();
                                Emulator.setDeviceIP(ipAddress);
                                Emulator.sendMessageToSocket(ACTION_SKT_JOIN_ACK_TRANSFER_AGREE);
                            }
                        })
                .setNegativeButton(res.getString(R.string.BTN_COMMON_CANCEL),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Emulator.setDeviceIP(ipAddress);
                                Emulator.sendMessageToSocket(ACTION_SKT_JOIN_ACK_TRANSFER_REFUTE);

                                dimissDialog();
                                stopTimer();
                                stopGameover();
                                stopBcTimer();
                            }
                        }).show();
    }

    public void showAgreeRom() {
        dimissDialog();
        initProgressDialog(res.getString(R.string.network_progress_loading));
    }

    public void showRefuteRom() {
        new AlertDialog.Builder(mContext)
                .setTitle(res.getString(R.string.network_dialog_refuterom_title))
                .setMessage(
                        serverAddress + "\n" + res.getString(R.string.network_dialog_refuterom_msg))
                .setCancelable(false)
                .setPositiveButton(res.getString(R.string.BTN_COMMON_OK),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dimissDialog();
                                stopTimer();
                                stopGameover();
                                stopBcTimer();
                            }
                        }).show();
    }

    public void showTransferFinish(String isServer) {
        dimissDialog();

        CONNECT_STATE = CONNECT_STATE_NONE;
        stopTimer();
        stopGameover();
        stopBcTimer();

        String name = SharePreferenceUtil.loadName(mContext);

        Debug.d("showTransferFinish isServer", isServer);

        if (null == isServer || isServer.length() <= 0) {
            return;
        }

        int type = Integer.parseInt(isServer);
        switch (type) {
            case RECEIVER_FINISH:
                Toast.makeText(mContext, "文件接受完毕.", Toast.LENGTH_LONG).show();

                mGameListContext.addItem(name, "", FileUtil.getDefaultROMsDIR() + "roms/");

                Debug.d("showTransferFinish serverAddress", serverAddress);
                startWifiGame();
                
                mHandler.sendMessage(createMessage(SKT_JONI_GAMES, serverAddress));
                break;
            case SEND_FINISH:
                Toast.makeText(mContext, "文件传输完毕.", Toast.LENGTH_LONG).show();

                String romName = SharePreferenceUtil.loadName(mContext);
                Emulator.broadcastDeviceInformation(1, Utils.getWFAddress(mContext),
                        mGameListContext.getCpu(), mGameListContext.getRam(), romName);
                Emulator.setWIFIGame(romName);

                start();

                sendBroadcast();
                initServerDialog();
                break;
            case RECEIVER_ERROR:
                Toast.makeText(mContext, "文件接受失败.", Toast.LENGTH_LONG).show();
                break;
            case SEND_ERROR:
                Toast.makeText(mContext, "文件传输失败.", Toast.LENGTH_LONG).show();
                break;
        }
    }

    public void start() {
        Emulator.sendMessageToSocket(ACTION_SKT_START);
    }

    public void join() {
        Emulator.sendMessageToSocket(ACTION_SKT_JOIN);
    }

    private void send() {
        Emulator.sendMessageToSocket(ACTION_SKT_BC_SEND);
    }

    private static void stopBroadcast() {
        CONNECT_STATE = CONNECT_STATE_NONE;
        Emulator.stopBroadcast();
        stopBcTimer();
    }

    private static void stopGameover() {
        Emulator.resetGame();
        dismissListDialog(true);
    }

    private Message createMessage(int what, Object object) {

        Message message = new Message();
        message.what = what;
        message.obj = object;
        return message;
    }

    public void exitGameWifi() {
        stopGameover();
        stopBcTimer();
    }
    private OnNpListener mOnNpListener = new OnNpListener() {

        @Override
        public void onChange(boolean isChange) {
            changeView(isChange);
        }
        
    };
    
    private void changeView(boolean boo) {
        if(boo == true) {
            if(mSameListView.getVisibility() == View.VISIBLE) {
                mSameListView.setVisibility(View.GONE);
                mDifListView.setVisibility(View.VISIBLE);
            }
        } else {
            if(mDifListView.getVisibility() == View.VISIBLE) {
                mDifListView.setVisibility(View.GONE);
                mSameListView.setVisibility(View.VISIBLE);
            }
        }
    }
}

