
package com.yunluo.android.arcadehub.netplay.gamekit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.yunluo.android.arcadehub.BaseApplication;
import com.yunluo.android.arcadehub.GameListActivity;
import com.yunluo.android.arcadehub.Emulator;
import com.yunluo.android.arcadehub.R;
import com.yunluo.android.arcadehub.interfac.OnNpListener;
import com.yunluo.android.arcadehub.netplay.Category;
import com.yunluo.android.arcadehub.netplay.NetPlayActivity;
import com.yunluo.android.arcadehub.netplay.adapter.BtDeviceAdapter;
import com.yunluo.android.arcadehub.netplay.obj.DeviceInfo;
import com.yunluo.android.arcadehub.utils.Debug;
import com.yunluo.android.arcadehub.utils.FileUtil;
import com.yunluo.android.arcadehub.utils.SharePreferenceUtil;
import com.yunluo.android.arcadehub.utils.Utils;

public class GameKit {
    private static final boolean DEBUG = false;

    private static final int ACTION_START_GAMEKIT = 1;

    private static final int ACTION_JION_GAMEKIT = 2;

    private static final int ACTION_CONNECTED = 3;

    private static final int ACTION_SEND_JION = 4;

    private static final int ACTION_SEND_START = 5;

    private static final int ACTION_TEARDOWN = 6;

    private static final int SERVER_REQUEST_ENABLE_BT = 1001;

    private static final int CLIENT_REQUEST_ENABLE_BT = 1002;

    private static final int SERVER_REQUEST_DISCOVERABLE_BT = 1003;

    public static final int MESSAGE_STATE_CHANGE = 1;

    public static final int MESSAGE_READ = 2;

    public static final int MESSAGE_WRITE = 3;

    public static final int MESSAGE_DEVICE_NAME = 4;

    public static final int MESSAGE_CONNECTION_FAILED = 5;

    public static final int MESSAGE_CONNECTION_LOST = 6;

    public static final int MESSAGE_STOP = 7;

    public static final int MESSAGE_NATIVE_START = 8;

    public static final int MESSASGE_STARTGAME = 9;

    public static final int MESSAGE_WRITE_ERROR = 10;

    public static final int MESSAGE_READ_ERROR = 11;

    public static final int MESSAGE_WRITE_FINISH = 12;

    public static final int MESSAGE_READ_FINISH = 13;

    public static int STATE_NONE = -1;

    public static final int STATE_PREPARE = 1;

    public static final int STATE_TRANFER = 2;

    public static final int STATE_GAMEPLAY = 3;

    public static final int PREPARE_CLENT_NOTIFY = 0;

    public static final int PREPARE_NOT_FOUND = 1;

    public static final int PREPARE_START_SERVER = 2;

    public static final int PREPARE_START_CLENT = 3;

    public static final int PREPARE_GAMEPACK_ASK = 4;

    public static final int PREPARE_GAMEPACK_REFUSE = 5;

    public static final int PREPARE_SHARED_REFUSE = 6;

    public static final int PREPARE_SHARED_AGREE = 7;

    public static final int PREPARE_TRANSFER_FINISH = 8;

    public static final int PREPARE_SEND_ROM = 9;

    public static final int PREPARE_SEND_JOIN = 10;

    public static final int PREPARE_SEND_JOIN_AGREE = 11;

    public static final int PREPARE_SEND_JOIN_REFUTE = 12;

    private NetPlayActivity mm;

    private GameListActivity mGameListContext;

    private BluetoothAdapter mAdapter;
    private BtDeviceAdapter mSameDevicesArrayAdapter;
    private BtDeviceAdapter mDifDevicesArrayAdapter;

    private static GameKitService mGameKitService = null;

    private boolean isServer = false;

    private boolean isRegisterReceiver;

    private ProgressDialog progressDialog = null;
    private ProgressDialog mLoadDialog = null;

    private BluetoothDevice mDevice = null;

    public final static int SIZE = 1024 * 8;

    public final static String SPLIT = "##";

    public final static String BT_SPLIT = "##";

    private OutputStream mOs;

    private boolean TRANSFER = true;
    private boolean isRunning = false;

    private Resources res = null;

    private String mRemoteName = null;
    private String mTmpName = null;

    public ArrayList<Category> mSameCategoryList = new ArrayList<Category>();
    public ArrayList<Category> mDifCategoryList = new ArrayList<Category>();

    public Category curList = null;

    public Category otherList = null;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE: {
                    int state = msg.arg1;
                    gamekitServiceStateChange(state);
                }
                    break;
                case MESSAGE_READ: {

                }
                    break;
                case MESSAGE_WRITE: {

                }
                    break;
                case MESSAGE_DEVICE_NAME: {

                }
                    break;
                case MESSAGE_CONNECTION_FAILED: {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }

                    dismissLoading();

                    STATE_NONE = STATE_PREPARE;

                    Toast.makeText(mm, res.getString(R.string.gamekit_connetted_fail),
                            Toast.LENGTH_LONG).show();

                    if (mGameKitService != null) {
                        mGameKitService.stop();
                        mGameKitService = null;
                    }
                }
                    break;
                case MESSAGE_CONNECTION_LOST: {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }

                    dismissLoading();

                    STATE_NONE = STATE_PREPARE;

                    Toast.makeText(mm, res.getString(R.string.gamekit_dis_connectted),
                            Toast.LENGTH_LONG).show();
                }
                    break;
                case MESSAGE_STOP: {
                    Emulator.gamekitAction(ACTION_TEARDOWN);
                }
                    break;
                case MESSAGE_NATIVE_START: {

                }
                    break;
                case MESSASGE_STARTGAME: {
                    String rom = (String)msg.obj;
                    Emulator.startGames(rom);
                }
                    break;
                case MESSAGE_READ_FINISH:
                    dismissLoading();
                    play();
                    break;
                case MESSAGE_WRITE_FINISH:
                    dismissLoading();
                    break;
                case MESSAGE_READ_ERROR:
                    Debug.d("Load Error..", "");
                    dismissLoading();
                    break;
                case MESSAGE_WRITE_ERROR:
                    Debug.d("Write Error..", "");
                    dismissLoading();
                    break;
                default:
                    break;
            }
        }
    };

    BaseApplication mApp = null;

    private ListView mSameListView = null;
    private ListView mDifListView = null;

    public GameKit(NetPlayActivity activity, View view) {
        this.mm = activity;
        activity.setOnNpListener(mOnNpListener);
        //        this.mListView = listView;
        mSameListView = (ListView) view.findViewById(R.id.same_lsv);
        mDifListView = (ListView) view.findViewById(R.id.dif_lsv);

        mApp = (BaseApplication)mm.getApplication();
        if (null == mApp) {
            mm.finish();
        }
        mGameListContext = mApp.getGameListActivity();
        if (null == mGameListContext) {
            mm.finish();
        }
        res = mm.getResources();
        this.mAdapter = BluetoothAdapter.getDefaultAdapter();
        if(null == mAdapter) {
            Toast.makeText(mm, "BT not use", Toast.LENGTH_SHORT).show();
            mm.finish();
        }
        STATE_NONE = STATE_PREPARE;

        curList = new Category(res.getString(R.string.TAB_GAMEROOMS_CURRENT));
        otherList = new Category(res.getString(R.string.TAB_GAMESROOMS_OTHER));

        mSameCategoryList.add(curList);

        mDifCategoryList.add(otherList);
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

    private void prepare(String rec) {
        final String items[] = rec.split(SPLIT);
        int state = 0;
        try {
            state = Integer.valueOf(items[0]);
            Debug.d("received state::", state);
        } catch (NumberFormatException e) {
//            e.printStackTrace();
        }
        switch (state) {
            case PREPARE_CLENT_NOTIFY:
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        SharePreferenceUtil.saveName(mm, items[1]);
                        Debug.d("received name::", items[1]);
                        if (false == mGameListContext.isFound(items[1])) {
                            showClientAsk();
                        } else {
                            showLoading(res.getString(R.string.gamekit_progress_request_join),
                                    false);
                            sendMsg(PREPARE_SEND_JOIN + SPLIT);
                        }
                    }
                });
                break;
            case PREPARE_NOT_FOUND:
                break;
            case PREPARE_START_SERVER:
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        play();
                        sendMsg(PREPARE_START_CLENT + SPLIT);
                    }
                });
                break;
            case PREPARE_START_CLENT:
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {

                        SharePreferenceUtil.saveName(mm, mRemoteName);

                        play();
                    }
                });
                break;
            case PREPARE_GAMEPACK_ASK:
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        showServerAsk(items[1]);
                    }
                });
                break;
            case PREPARE_GAMEPACK_REFUSE:
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        stopGameKit();
                    }
                });
                break;
            case PREPARE_SHARED_REFUSE:
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        showRefuse();
                    }
                });
                break;
            case PREPARE_SHARED_AGREE:
                STATE_NONE = STATE_TRANFER;
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        showLoading(res.getString(R.string.gamekit_saving), true);
                    }
                });
                break;
            case PREPARE_TRANSFER_FINISH:
                STATE_NONE = STATE_PREPARE;
                break;
            case PREPARE_SEND_ROM:
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        mRemoteName = items[1];
                    }
                });
                break;
            case PREPARE_SEND_JOIN:
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        showJoin();
                    }
                });
                break;
            case PREPARE_SEND_JOIN_REFUTE:

                STATE_NONE = STATE_PREPARE;

                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        showJoinRefute();
                    }
                });
                break;
            default:
                break;
        }
    }

    public void received(final byte[] data, final int length) {
        switch (STATE_NONE) {
            case STATE_PREPARE:
                dismissLoading();
                String rec = new String(data, 0, length);
                Debug.e("receiverd", "" + rec);
                Debug.e("receiverd length", length);

                if (false == isTransfer()) {
                    return;
                }

                if (length == 40) {
                    return;
                }

                if (rec != null && rec.length() != 0) {
                    prepare(rec);
                }
                break;
            case STATE_TRANFER:
                final byte[] recDate = new byte[length];
                System.arraycopy(data, 0, recDate, 0, length);
                write(recDate, length);
                break;
            case STATE_GAMEPLAY:
                byte[] received = new byte[length];
                System.arraycopy(data, 0, received, 0, length);
                Emulator.gamekitReceivedData(received);
                log("received data=" + received[0] + " " + received[0] + " " + received[1] + " "
                        + received[2] + " " + received[3] + " " + received[4] + " " + received[5]
                        + " " + received[6] + " " + received[8] + " ");
                break;
        }
    }

    private void gamekitServiceStateChange(int state) {
        log("gamekitServiceStateChange state=" + state);

        if (GameKitService.STATE_LISTEN == state) {

        } else if (GameKitService.STATE_CONNECTING == state) {

        } else if (GameKitService.STATE_CONNECTED == state) {

            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }
            showLoading(res.getString(R.string.MSG_REFRESH_LOADING), false);
            if (isServer) {
                String name = SharePreferenceUtil.loadName(mm);
                sendMsg(PREPARE_CLENT_NOTIFY + SPLIT + name);
            } else {
                Toast.makeText(mm, "This is the Client.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // play game
    private void play() {
        STATE_NONE = STATE_GAMEPLAY;

        Emulator.resume();

        if (isServer) {
            Emulator.gamekitAction(ACTION_START_GAMEKIT);
            Emulator.gamekitAction(ACTION_CONNECTED);

            Emulator.showLoading();
            mGameListContext.showGamesPlay(false);
            Emulator.startGames();

        } else {

            Emulator.gamekitAction(ACTION_JION_GAMEKIT);
            Emulator.gamekitAction(ACTION_CONNECTED);

            Emulator.gamekitAction(ACTION_SEND_JION);

            setRunning(true);
        }
        Emulator.gamekitAction(ACTION_SEND_START);
    }

    private void showClientAsk() {
        new AlertDialog.Builder(mm)
                .setTitle(res.getString(R.string.gamekit_dialog_title))
                .setMessage(res.getString(R.string.gamekit_dialog_client_msg))
                .setPositiveButton(res.getString(R.string.gamekit_dialog_yes),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (false == Utils.checkAvailableStore()) {
                                    Toast.makeText(mm, res.getString(R.string.no_enough_space),
                                            Toast.LENGTH_SHORT).show();
                                    sendMsg(PREPARE_GAMEPACK_REFUSE + SPLIT);
                                    stopGameKit();
                                    return;
                                }
                                initTransfer();

                                sendMsg(PREPARE_GAMEPACK_ASK + SPLIT + mAdapter.getAddress());

                                showLoading(res.getString(R.string.gamekit_dialog_ask_gamepack),
                                        false);

                            }
                        })
                .setNegativeButton(res.getString(R.string.gamekit_dialog_no),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sendMsg(PREPARE_GAMEPACK_REFUSE + SPLIT);
                                stopGameKit();
                                mm.finish();
                            }
                        }).show();
    }

    private void showServerAsk(final String address) {
        new AlertDialog.Builder(mm)
                .setTitle(res.getString(R.string.gamekit_dialog_title))
                .setMessage(res.getString(R.string.gamekit_dialog_server_msg))
                .setPositiveButton(res.getString(R.string.gamekit_dialog_yes),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                STATE_NONE = STATE_TRANFER;

                                dismissDialog(false);

                                sendMsg(PREPARE_SHARED_AGREE + SPLIT);

                                Debug.d("agree shared file", "");

                                showLoading(res.getString(R.string.gamekit_transfer_file), true);

                                new Thread(new Runnable() {

                                    @Override
                                    public void run() {
                                        try {
                                            Thread.sleep(200);
                                        } catch (InterruptedException e) {
//                                            e.printStackTrace();
                                        }
                                        read();
                                    }
                                }).start();

                            }
                        })
                .setNegativeButton(res.getString(R.string.gamekit_dialog_no),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sendMsg(PREPARE_SHARED_REFUSE + SPLIT);
                                stopGameKit();
                                dismissDialog(true);
                            }
                        }).show();
    }

    private void showRefuse() {
        new AlertDialog.Builder(mm)
                .setTitle(res.getString(R.string.gamekit_dialog_title))
                .setMessage(res.getString(R.string.gamekit_dialog_refuse_msg))
                .setPositiveButton(res.getString(R.string.BTN_COMMON_OK),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                stopGameKit();
                            }
                        }).show();
    }

    private void showJoin() {
        new AlertDialog.Builder(mm)
                .setTitle(res.getString(R.string.gamekit_join_title))
                .setMessage(res.getString(R.string.gamekit_join_msg))
                .setPositiveButton(res.getString(R.string.BTN_COMMON_OK),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();

                                dismissDialog(false);

                                play();
                                sendMsg(PREPARE_START_CLENT + SPLIT);
                            }
                        })
                .setNegativeButton(res.getString(R.string.gamekit_join_cancel),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                sendMsg(PREPARE_SEND_JOIN_REFUTE + SPLIT);
                            }
                        }).show();
    }

    private void showJoinRefute() {
        new AlertDialog.Builder(mm)
                .setTitle(res.getString(R.string.gamekit_join_refute_title))
                .setMessage(res.getString(R.string.gamekit_join_refute_msg))
                .setPositiveButton(res.getString(R.string.BTN_COMMON_OK),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                stopGameKit();

                            }
                        }).show();
    }

    private void showLoading(final String msg, boolean flag) {
        if (null == mLoadDialog) {
            mLoadDialog = new ProgressDialog(mm);
            mLoadDialog.setMessage(msg);
            if (true == flag) {
                mLoadDialog.setCancelable(true);
                mLoadDialog.setCanceledOnTouchOutside(false);
                mLoadDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        setTransfer(false);
                        stopGameKit();
                    }
                });
            } else {
                mLoadDialog.setCancelable(false);
            }
            mLoadDialog.show();
        }
    }

    private void dismissLoading() {
        if (null != mLoadDialog) {
            mLoadDialog.dismiss();
            mLoadDialog = null;
        }
    }

    private void sendMsg(Object msg) {
        if (null == mGameKitService) {
            return;
        }
        byte[] buf = msg.toString().getBytes();
        mGameKitService.write(buf, buf.length);
        Debug.e("send msg", "" + (new String(buf)));
    }

    private void initTransfer() {
        mTmpName = UUID.randomUUID().toString();
        String transferPath = Utils.getTransferPath(mm);
        File dir = new File(transferPath);

        if (false == dir.exists()) {
            boolean boo = dir.mkdirs();
            Debug.d("create save dir", (boo == true) ? "scucess" : "failure");
        }

        SharePreferenceUtil.savePath(mm, transferPath + File.separator);

        File file = new File(transferPath + File.separator + mTmpName + ".zip");
        Utils.chmodeFile(file.getAbsolutePath());

        if (false == file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
//                e.printStackTrace();
                mHandler.obtainMessage(GameKit.MESSAGE_WRITE_ERROR).sendToTarget();
            }
        }

        try {
            mOs = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
//            e.printStackTrace();
            mHandler.obtainMessage(GameKit.MESSAGE_WRITE_ERROR).sendToTarget();
        }
    }

    private void read() {
        String path = SharePreferenceUtil.loadPath(mm);
        String name = SharePreferenceUtil.loadName(mm);
        File file = new File(path + File.separator + name + ".zip");
        if (false == file.exists()) {
            mHandler.obtainMessage(GameKit.MESSAGE_READ_ERROR).sendToTarget();
            return;
        }

        Debug.d("read file total length", file.length());

        try {
            InputStream is = new FileInputStream(file);
            byte[] buf = new byte[SIZE];
            int len = 0;
            int total = 0;
            while ((len = is.read(buf)) != -1) {

                if (false == isTransfer()) {
                    break;
                }

                if (mGameKitService != null) {
                    mGameKitService.write(buf, len);
                }
                total += len;
                Debug.d("read length", len);
            }

            STATE_NONE = STATE_PREPARE;

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
//                e.printStackTrace();
            }
            sendMsg(PREPARE_TRANSFER_FINISH + SPLIT);

            Debug.d("read file end total length", total);

            mHandler.obtainMessage(GameKit.MESSAGE_READ_FINISH).sendToTarget();

        } catch (FileNotFoundException e) {
            mHandler.obtainMessage(GameKit.MESSAGE_READ_ERROR).sendToTarget();
//            e.printStackTrace();
        } catch (IOException e) {
            mHandler.obtainMessage(GameKit.MESSAGE_READ_ERROR).sendToTarget();
//            e.printStackTrace();
        }

    }

    private void write(final byte[] buf, final int length) {
        Debug.d("write length", length);

        if (length == 3) {
            finish();
            mHandler.obtainMessage(GameKit.MESSAGE_WRITE_FINISH).sendToTarget();
            return;
        }

        if (length == 40) {
            finish();
            mHandler.obtainMessage(GameKit.MESSAGE_WRITE_FINISH).sendToTarget();
            return;
        }

        if (false == isTransfer()) {
            close();
            return;
        }

        mHandler.post(new Runnable() {

            @Override
            public void run() {

                if (null == mOs) {
                    return;
                }

                try {
                    mOs.write(buf, 0, length);
                } catch (IOException e) {
//                    e.printStackTrace();
                    mHandler.obtainMessage(GameKit.MESSAGE_WRITE_ERROR).sendToTarget();
                }
            }
        });

    }

    private void close() {
        STATE_NONE = STATE_PREPARE;

        if (null == mOs) {
            return;
        }
        try {
            mOs.flush();
            mOs.close();
            mOs = null;
        } catch (IOException e) {
//            e.printStackTrace();
        }
    }

    private void finish() {
        close();

        mHandler.post(new Runnable() {

            @Override
            public void run() {
                add();
                play();
            }
        });
        Debug.d("save file finish", "");
    }

    private void add() {
        final String name = SharePreferenceUtil.loadName(mm);
        String transPath = Utils.getTransferPath(mm);
        String pathS = transPath + File.separator + mTmpName + ".zip";
        String newPath = transPath + File.separator + name + ".zip";
        File file = new File(pathS);
        if (file.exists()) {
            boolean boo = file.renameTo(new File(newPath));
            Debug.d("file rename", (boo == true) ? "scuceed" : "failure");
        }

        Utils.chmodeFile(file.getAbsolutePath());
        final String romSize = FileUtil.formatFileSize(file.length());
        mGameListContext.addItem(name, romSize, transPath);
    }

    public void disableBt() {
        if (mAdapter.isEnabled()) {
            mAdapter.disable();
        }
    }

    public void stopGameKit() {
        STATE_NONE = STATE_PREPARE;

        if (mAdapter.isDiscovering()) {
            mAdapter.cancelDiscovery();
        }

        if (isRegisterReceiver) {
            isRegisterReceiver = false;
            if (null != mm && null != mReceiver) {
                try {
                    mm.unregisterReceiver(mReceiver);
                } catch (Exception e) {
                }
            }
        }

        if (mGameKitService != null) {
            mGameKitService.stop();
            mGameKitService = null;
        }

        resetDeviceName();
    }

    public void joinGamekit() {

        Debug.d("joinGamekit", "--");

        setTransfer(true);

        if (mAdapter == null) {
            Toast.makeText(mm, res.getString(R.string.gamekit_toast_bt_notuse), Toast.LENGTH_LONG)
                    .show();
            Emulator.resume();
            return;
        }
        if (!mAdapter.isEnabled()) {
            boolean flag = mAdapter.enable();
            Debug.d("Bluetooth", (flag == true) ? "Enable" : "disable");
        }

        setDeviceName();

        if (mAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {

            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            mm.startActivityForResult(discoverableIntent, SERVER_REQUEST_DISCOVERABLE_BT);

            return;
        }

        startBluetoothService(true, null);

        searchDevice();
        curList.clear();
        otherList.clear();
        mSameDevicesArrayAdapter = new BtDeviceAdapter(mm, mSameCategoryList);
        mDifDevicesArrayAdapter = new BtDeviceAdapter(mm, mDifCategoryList);
        setLsv();
        
        Emulator.dimissLoading();
    }
    
    private void setLsv() {
        if (null == mSameListView || null == mDifListView) {
            mm.finish();
            return;
        }
        mSameListView.setAdapter(mSameDevicesArrayAdapter);
        mSameListView.setOnItemClickListener(mSameOnItemClickListener);
        
        mDifListView.setAdapter(mDifDevicesArrayAdapter);
        mDifListView.setOnItemClickListener(mDifOnItemClickListener);
        
        
    }

    private void setDeviceName() {
        String romName = SharePreferenceUtil.loadName(mm);
        String deviceName = mAdapter.getName();
        if (null != deviceName) {
            if (deviceName.contains(BT_SPLIT)) {
                int index = deviceName.indexOf(BT_SPLIT);
                deviceName = deviceName.substring(0, index);
            }
            String tmpName = deviceName + BT_SPLIT + romName;
            mAdapter.setName(tmpName);
            Debug.e("discoverable tmp name: ", mAdapter.getName());
        }
    }

    private void resetDeviceName() {
        if (null == mAdapter) {
            return;
        }
        String deviceName = mAdapter.getName();
        if (null != deviceName) {
            if (deviceName.contains(BT_SPLIT)) {
                int index = deviceName.indexOf(BT_SPLIT);
                deviceName = deviceName.substring(0, index);
                mAdapter.setName(deviceName);
            }
        }
    }

    private OnItemClickListener mSameOnItemClickListener = new OnItemClickListener() {
        
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
            if (null == mGameKitService) {
                return;
            }
            
            mGameKitService.stopAccept();
            
            dismissDialog(false);
            
            if (mAdapter.isDiscovering()) {
                mAdapter.cancelDiscovery();
            }
            if(null == mSameListView) {
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
            mDevice = mInfo.getDevice();
            
            joinGamekit(mDevice);
        }
        
    };
    
    private OnItemClickListener mDifOnItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
            if (null == mGameKitService) {
                return;
            }

            mGameKitService.stopAccept();

            dismissDialog(false);

            if (mAdapter.isDiscovering()) {
                mAdapter.cancelDiscovery();
            }
            if(null == mDifListView) {
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
            mDevice = mInfo.getDevice();
            
            joinGamekit(mDevice);
        }

    };

    public void dismissDialog(boolean finishKey) {
        if (null != mm && finishKey == true) {
            mm.finish();
        }
    }

    private void joinGamekit(BluetoothDevice device) {

        startBluetoothService(false, device);
    }

    private void startServer(boolean server, BluetoothDevice device) {
        String name = SharePreferenceUtil.loadName(mm);
        Emulator.setBluetoochGame(name);
        isServer = server;
        mGameKitService = new GameKitService(this, mAdapter, mHandler);
        mGameKitService.start();
        sendMsg(PREPARE_SEND_ROM + SPLIT + name);
    }

    private void startBluetoothService(boolean server, BluetoothDevice device) {

        Debug.d("mm", mm);
        Debug.d("mm.isrunning", mm.isFinishing());
        String name = SharePreferenceUtil.loadName(mm);
        Emulator.setBluetoochGame(name);

        isServer = server;
        mGameKitService = new GameKitService(this, mAdapter, mHandler);
        String msg = "";
        if (server) {
            mGameKitService.start();

            sendMsg(PREPARE_SEND_ROM + SPLIT + name);
            return;
        } else {
            mGameKitService.connect(device);
            String deviceName = device.getName();
            if(null != deviceName && false =="".equals(deviceName)){
                int index = deviceName.indexOf(BT_SPLIT);
                if (null != deviceName && index >= 0 && index <= deviceName.length()) {
                    deviceName = deviceName.substring(0, index);
                    Debug.d("connecting name: ", deviceName);
                    msg = res.getString(R.string.gamekit_dialog_connecting) + deviceName;
                }
            }
        }

        progressDialog = new ProgressDialog(mm);
        progressDialog.setTitle(res.getString(R.string.BTN_TEXT_GAMETYPE_BLUETOOTH));
        progressDialog.setMessage(msg);

        if (server) {
            progressDialog.setCancelable(true);
            progressDialog.setButton(res.getString(R.string.BTN_COMMON_OK),
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (null != progressDialog) {
                                progressDialog.dismiss();
                                progressDialog = null;
                            }
                            stopGameKit();
                            Emulator.resume();

                            Emulator.showLoading();
                            mGameListContext.showGamesPlay(false);
                            Emulator.startGames();
                        }
                    });
        } else {
            progressDialog.setCancelable(false);
        }
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                if (null != progressDialog) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
                stopGameKit();
                Emulator.resume();

            }
        });
        progressDialog.show();
    }

    private boolean isValidDevice(BluetoothDevice device) {
        if (device == null)
            return false;
        if (BluetoothAdapter.checkBluetoothAddress(device.getAddress()))
            return true;
        return false;
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (isValidDevice(device)) {
                    String blName = device.getName();
                    if (null != blName) {
                        if (blName.contains(BT_SPLIT)) {
                            String items[] = blName.split(BT_SPLIT);
                            DeviceInfo info = new DeviceInfo();
                            info.setDevice(device);
                            info.setDeviceName(items[0]);
                            String desc = Emulator.getGameDesc(items[1]);
                            info.setDesc(desc);
                            if( null != mGameListContext ){
                                info.setCpu(mGameListContext.getCpu());
                                info.setRam(mGameListContext.getRam());
                            }else{
                            }
                            String defaultName = SharePreferenceUtil.loadName(mm);
                            if (defaultName.equals(items[1])) {
                                curList.addItem(info);
                                int id = curList.getId(info)+1;
                                info.setId(id);
                            } else {
                                otherList.addItem(info);
                                int id = otherList.getId(info)+1;
                                info.setId(id);
                            }
                            mSameDevicesArrayAdapter.notifyDataSetChanged();
                        }
                    }
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Toast.makeText(mm, "discovery finish", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void searchDevice() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        mm.registerReceiver(mReceiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        mm.registerReceiver(mReceiver, filter);
        isRegisterReceiver = true;

        mAdapter.cancelDiscovery();
        mAdapter.startDiscovery();
    }

    public boolean OnActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SERVER_REQUEST_ENABLE_BT) {
            Emulator.dimissLoading();
            if (resultCode == Activity.RESULT_OK) {
            } else {
                Toast.makeText(mm, res.getString(R.string.gamekit_toast_bt_notuse),
                        Toast.LENGTH_LONG).show();
            }
            return true;
        } else if (requestCode == SERVER_REQUEST_DISCOVERABLE_BT) {
            Emulator.dimissLoading();
            if (mAdapter.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                joinGamekit();

            } else {
                Toast.makeText(mm, res.getString(R.string.gamekit_toast_bt_not_found),
                        Toast.LENGTH_LONG).show();
                mm.finish();
            }
            return true;
        } else if (requestCode == CLIENT_REQUEST_ENABLE_BT) {
            Emulator.dimissLoading();
            if (resultCode == Activity.RESULT_OK) {
                joinGamekit();
            } else {
                Toast.makeText(mm, res.getString(R.string.gamekit_toast_bt_notuse),
                        Toast.LENGTH_LONG).show();
                mm.finish();
            }
            return true;
        }
        return false;
    }

    public static void send(byte[] b, int sz) {
        if (mGameKitService != null) {
            byte[] out = new byte[sz];
            System.arraycopy(b, 0, out, 0, sz);
            mGameKitService.write(out, sz);
            log("send data=" + out[0] + " " + out[0] + " " + out[1] + " " + out[2] + " " + out[3]
                    + " " + out[4] + " " + out[5] + " " + out[6] + " " + out[8] + " ");
        }
    }

    public static void log(String msg) {
        if (DEBUG)
            Log.d("ArcadeHub", msg);
    }

    public static void log(String msg, Throwable e) {
        if (DEBUG)
            Log.e("ArcadeHub", msg, e);
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public void setTransfer(boolean transfer) {
        TRANSFER = transfer;
    }

    public boolean isTransfer() {
        return TRANSFER;
    }

    /**
     * Activity 退出时调用 （注销广播接收器等）
     */
    public void exitGameKit() {
        mAdapter.cancelDiscovery();
        stopGameKit();
        if (isRegisterReceiver) {
            // isRegisterReceiver = false;
            if (null != mm && null != mReceiver) {
                mm.unregisterReceiver(mReceiver);
            }
            Emulator.resume();
        }
    }

}
