
package com.yunluo.android.arcadehub.netplay;

import com.umeng.analytics.MobclickAgent;
import com.yunluo.android.arcadehub.BaseApplication;
import com.yunluo.android.arcadehub.Emulator;
import com.yunluo.android.arcadehub.GameListActivity;
import com.yunluo.android.arcadehub.R;
import com.yunluo.android.arcadehub.interfac.OnNetPlayListener;
import com.yunluo.android.arcadehub.interfac.OnNpListener;
import com.yunluo.android.arcadehub.netplay.gamekit.GameKit;
import com.yunluo.android.arcadehub.netplay.skt.GameWifiSkt;
import com.yunluo.android.arcadehub.utils.Debug;
import com.yunluo.android.arcadehub.utils.SharePreferenceUtil;
import com.yunluo.android.arcadehub.utils.Utils;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

public class NetPlayActivity extends Activity implements OnNetPlayListener {

	// Gamekit
    private boolean mkey = true;
	private View mNtView = null;
	private View mSameLine = null;
	private View mDifLine = null;
	private TextView mTitle = null;
	private GameKit mGameKit = null;
	private ListView mListView = null;
	private BaseApplication mApp = null;
	private LinearLayout mMainLayout = null;
	private GameWifiSkt mGameWifiSkt = null;
	private OnNpListener mOnNpListener = null;
	private WifiManager.MulticastLock mLock = null;
	private GameListActivity mGameListActivity = null;
	private Handler mHandler = new Handler();
    
    // Gamekit
    public GameKit getGameKit() {
        return mGameKit;
    }

    public GameWifiSkt getGameWifiSkt() {
        return mGameWifiSkt;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
        Emulator.setOnNetPlayListener(this);
    }

    /**
     * init layout
     */
    private void init() {

        mMainLayout = new LinearLayout(this);
        int color = this.getResources().getColor(R.color.transparent_half);
        mMainLayout.setBackgroundColor(color);
        mMainLayout.setOrientation(LinearLayout.VERTICAL);
        
        mApp = (BaseApplication) this.getApplication();

        mGameListActivity = mApp.getGameListActivity();
        Bundle bundle = this.getIntent().getExtras();
        mkey = bundle.getBoolean("Name");

        initTitle();
        
        initNtLyt();
        
        startNetplayGame();
        
        setContentView(mMainLayout);
    }
    
    /**
     * init title
     */
    private void initTitle() {
        LinearLayout.LayoutParams mTitleLp = new LinearLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, (int)(50 * GameListActivity.SCREEN_DENSITY));
        mTitle = new TextView(this);
        mTitle.setGravity(Gravity.CENTER);
        mTitle.setBackgroundResource(R.drawable.app_title_bg);
        mTitle.setTextAppearance(this, R.style.all_title_style);
        mTitle.setLayoutParams(mTitleLp);
        
        RelativeLayout.LayoutParams imglayoutLp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int)(50*GameListActivity.SCREEN_DENSITY));
        RelativeLayout titlelayout = new RelativeLayout(this);
        titlelayout.setLayoutParams(imglayoutLp);
        RelativeLayout.LayoutParams imgLp = new RelativeLayout.LayoutParams((int)(32*GameListActivity.SCREEN_DENSITY), RelativeLayout.LayoutParams.MATCH_PARENT);
        ImageView imgback = new ImageView(this);
        imgback.setId(81);
        imgback.setImageResource(R.drawable.ic_back);
        imgback.setBackgroundColor(0x00000000);
        imgback.setScaleType(ScaleType.FIT_CENTER);
        int i =(int)(9*GameListActivity.SCREEN_DENSITY);
        imgLp.setMargins( i, i, i, i);
        imgback.setLayoutParams(imgLp);
        imgback.setOnClickListener(mOnClickListener);
        titlelayout.addView(mTitle);
        titlelayout.addView(imgback,imgLp); 
        mMainLayout.addView(titlelayout);
    }
    
    /**
     * init ListView
     */
    private void initListView() {
        LinearLayout.LayoutParams mLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        mListView = new ListView(this);
        mListView.setCacheColorHint(0x00000000);
        mListView.setBackgroundColor(Color.TRANSPARENT);
        mListView.setLayoutParams(mLp);
        mMainLayout.addView(mListView);
    }
    
    /**
     * init player list
     */
    private void initNtLyt() {
        mNtView = View.inflate(this, R.layout.netplay_layout, null);
        mMainLayout.addView(mNtView);

        TextView mSameTv = (TextView) mNtView.findViewById(R.id.np_tv_same);
        mSameTv.setOnClickListener(mOnClickListener);
        TextView mDifTv = (TextView) mNtView.findViewById(R.id.np_tv_dif);
        mDifTv.setOnClickListener(mOnClickListener);
        
        mSameLine = mNtView.findViewById(R.id.np_tv_same_line);
        mDifLine = mNtView.findViewById(R.id.np_tv_dif_line);
    }
    
    /**
     * add header
     */
    private void addHeader() {
        View view = LayoutInflater.from(this).inflate(R.layout.netplay_info_header, null);
        mListView.addHeaderView(view, null, false);
        updateHeader(view);
    }
    
    /**
     * update header
     */
    public void updateHeader(View view) {
        String rom = SharePreferenceUtil.loadName(this);
        String desc = Emulator.getGameDesc(rom);
        String ip = Utils.getWFAddress(this);
        int cpu = 0;
        int ram = 0;
        if(null != mGameListActivity) {
            cpu = mGameListActivity.getCpu();
            ram = mGameListActivity.getRam();
        }

        TextView descTv = (TextView) view.findViewById(R.id.npy_device_desc);
        TextView ipTv = (TextView) view.findViewById(R.id.npy_device_info);
        TextView cpuTv = (TextView) view.findViewById(R.id.npy_device_cpu);
        TextView ramTv = (TextView) view.findViewById(R.id.npy_device_ram);
        
        descTv.setText(desc);
        if(false == mkey) {
            BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
            String name = mAdapter.getName();
            int index = name.indexOf("##");
            name = name.substring(0, index);
            ipTv.setText(name);
        } else {
            ipTv.setText("IP: "+ip);
        }
        cpuTv.setText("\tCPU: "+cpu);
        ramTv.setText("\tRAM: "+ram);
    }
    
    /**
     * start game
     */
    private void startNetplayGame() {
        Resources res = getResources();
        if (mkey == false) {
            mTitle.setText(res.getString(R.string.BTN_TEXT_GAMETYPE_BLUETOOTH));
            mGameKit = new GameKit(this, mNtView);
            // Gamekit end
            mGameKit.joinGamekit();
        } else {
            mTitle.setText(res.getString(R.string.BTN_TEXT_GAMETYPE_NETWORK));
            mGameWifiSkt = new GameWifiSkt(this, mNtView);
            mGameWifiSkt.startWifiGame();
        }
    }

    // stop bluetooth
    private void stopBtGame() {
        if (null != mGameKit) {
            mGameKit.stopGameKit();
        }
    }

    // LAN note start game
    private void startWifiGame(final String name) {

        Debug.d("network play notify rom name", name);
        GameWifiSkt.reset();
        GameWifiSkt.dismissListDialog(false);
        GameWifiSkt.dismissServerDialog();
        Emulator.startGames(name);

    }

    // BT note start game
    private void startBtGame(final String name) {

        Debug.d("bluetooth play notify rom name", name);
        if (null == mGameKit) {
            return;
        }
        if (true == mGameKit.isRunning()) {
            mGameKit.setRunning(false);
            Emulator.startGames(name);
        }
    }

    // ACTIVITY
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Gamekit begin
        if (mGameKit.OnActivityResult(requestCode, resultCode, data)) {
            return;
        }
        // Gamekit end
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK) {
    		if (mkey == false && null != mGameKit) {
    			mGameKit.exitGameKit();
    		} else if (null != mGameWifiSkt) {
    			mGameWifiSkt.exitGameWifi();
    		}
    		finish();
    	}
    	return super.onKeyDown(keyCode, event);
    }
    
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		WifiManager mMgr = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		mLock = mMgr.createMulticastLock("test wifi");
		mLock.acquire();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		if (null != mLock) {
			mLock.release();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		finish();
	}

    @Override
    public void onRequestConnected(String info) {
        if (null == mGameWifiSkt) {
            return;
        }
        mGameWifiSkt.showConnect(info);
    }

    @Override
    public void onRefuteConnected(String info) {
        if (null == mGameWifiSkt) {
            return;
        }
        mGameWifiSkt.showRefuseDialog(info);
    }

    @Override
    public void onRequestTransfer(String info) {
        if (null == mGameWifiSkt) {
            return;
        }
        mGameWifiSkt.showSharedRom(info);
    }

    @Override
    public void onAgreeTransfer() {
        if (null == mGameWifiSkt) {
            return;
        }
        mGameWifiSkt.showAgreeRom();
    }

    @Override
    public void onRefuteTransfer() {
        if (null == mGameWifiSkt) {
            return;
        }
        mGameWifiSkt.showRefuteRom();
    }

    @Override
    public void onFinishTransfer(String info) {
        if (null == mGameWifiSkt) {
            return;
        }
        mGameWifiSkt.showTransferFinish(info);
    }

    @Override
    public void onStartWIFIGame(final String info) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                startWifiGame(info);
            }

        });
    }

    @Override
    public void onStartBTGame(final String info) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                startBtGame(info);
                Debug.d("onStartBTGame", "Start BT Game");
            }
        });
    }

    @Override
    public void onAddNetPlayInformation(int os, String ip, int cpu, int ram, String rom) {
        if (null == mGameWifiSkt) {
            return;
        }
        if (null != mGameWifiSkt) {

            Handler mHandler = mGameWifiSkt.getHandler();
            if (null != mHandler) {
                String desc = Emulator.getGameDesc(rom);
                Bundle bun = new Bundle();
                bun.putString("name", rom);
                bun.putString("desc", desc);
                bun.putInt("os", os);
                bun.putString("ip", ip);
                bun.putInt("cpu", cpu);
                bun.putInt("ram", ram);

                Message msg = new Message();
                msg.what = GameWifiSkt.SKT_IP_LIST;
                msg.setData(bun);
                mHandler.sendMessage(msg);
                Debug.d("send msg to list", "");
            }
        }
    }

	@Override
	public void onFinish() {
		stopBtGame();
		finish();
	}
	
	 
    /**
     * 按键监听 back/change
     */
    private OnClickListener mOnClickListener = new OnClickListener(){

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch(id) {
            case 81:
                if (mkey == false && null != mGameKit) {
                    mGameKit.exitGameKit();
                } else if (null != mGameWifiSkt) {
                    mGameWifiSkt.exitGameWifi();
                }
                finish();
                break;
            case R.id.np_tv_same:
                if(null != mOnNpListener) {
                    mOnNpListener.onChange(false);
                }
                setVis(false);
                break;
            case R.id.np_tv_dif:
                if(null != mOnNpListener) {
                    mOnNpListener.onChange(true);
                } 
                setVis(true);
                break;
            }
        }
        
    };
    
    private void setVis(boolean visable) {
        mSameLine.setVisibility(true == visable ? View.INVISIBLE : View.VISIBLE);
        mDifLine.setVisibility(true == visable ? View.VISIBLE : View.INVISIBLE);
    }
    
    public void setOnNpListener(OnNpListener listener) {
        this.mOnNpListener = listener;
    }
}
