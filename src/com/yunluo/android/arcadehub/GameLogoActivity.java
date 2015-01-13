package com.yunluo.android.arcadehub;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.umeng.analytics.MobclickAgent;
import com.yunluo.android.arcadehub.async.RomInfo;
import com.yunluo.android.arcadehub.async.UpdateAnsyncTask;
import com.yunluo.android.arcadehub.sort.ComparatorRomInfo;
import com.yunluo.android.arcadehub.utils.Debug;
import com.yunluo.android.arcadehub.utils.FileUtil;
import com.yunluo.android.arcadehub.utils.SharePreferenceUtil;
import com.yunluo.android.arcadehub.utils.Utils;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;

public class GameLogoActivity extends Activity {

	public static final int MSG_TO_FINISH = 898;
	public static final int MSG_TO_LOADDATA = 899;
	public static final int MSG_TO_SHOWGAMES = 900;

    private List<RomInfo> mRomList = null;
	
	private BaseApplication mApp = null;

	private UpdateAnsyncTask mSearchTask = null;

	private String mRomDir = null;
	
    private Resources res = null;
    
    private boolean isPush = false;
    
    private String name = null;
    
    private String url = null;
    
    private String checksum = null;
    
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			switch (what) {
			case MSG_TO_LOADDATA:
				load();
				break;
			case MSG_TO_FINISH:
				mSearchTask = new UpdateAnsyncTask(null, mRomList, null);
				mSearchTask.execute(Utils.getRootPath(GameLogoActivity.this));
				
				
    			Message message = new Message();
    			message.what = MSG_TO_SHOWGAMES;
    			mHandler.sendMessageDelayed(message, 3*1000);
				break;
			case MSG_TO_SHOWGAMES:
				showGames();
				break;
			default:
				break;
			}
		};
	};

	private void load() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				loadData();
				runMAME4droid();
			}
		}).start();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.loading);
		
		getPushView();
		
		init();

        if (false == FileUtil.ensureROMsDir(mRomDir)) {
            showErrDialog(res.getString(R.string.LOGO_NO_SD_MSG));
            return;
        }

		if(false == Utils.checkAvailableStore() && true == Utils.isExistSdcard()) {
			showErrDialog(res.getString(R.string.MSG_REFRESH_LOADING));
			return;
		}
	}
	
	private void getPushView() {
		Bundle bun = this.getIntent().getExtras();
		
		if(null != bun) {
			checksum = bun.getString("checksum");
			if(false == checksum.equals(SharePreferenceUtil.loadCheckSum(this))) {
				isPush = bun.getBoolean("push", false);
				name = bun.getString("name");
				url = bun.getString("url");
			}
			bun.clear();
			bun = null;
		}
	}
	
	private void init() {
		res = this.getResources();
		
		FileUtil.setGameFilePath(this);
		
		mRomDir = FileUtil.getDefaultROMsDIR();
		
		mApp = (BaseApplication) this.getApplication();

		mRomList = mApp.getRomList();
		
		Emulator.setLogoActivity(this);
		
		mHandler.obtainMessage(MSG_TO_LOADDATA).sendToTarget();
		
		saveBtState();
	}
	
	private void loadData() {
		mRomList = (List<RomInfo>) FileUtil.read(this);

		if(null == mRomList) {
			mRomList = new ArrayList<RomInfo>();
		} else {
			ComparatorRomInfo mComparator = new ComparatorRomInfo();
			Collections.sort(mRomList, mComparator);
		}
		mApp.setRomList(mRomList);
	}
	
	private void runMAME4droid() {
		String libPath = FileUtil.getLibDir(this);
		FileUtil.copyFiles(this, mRomDir);
		Emulator.emulate(Utils.getRootPath(this), libPath, mRomDir);
	}

    private void showErrDialog(String msg) {
        new AlertDialog.Builder(this)
                .setTitle(res.getString(R.string.LOGO_TIP))
                .setMessage(msg)
                .setPositiveButton(res.getString(R.string.BTN_COMMON_OK),
                        new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						android.os.Process.killProcess(android.os.Process.myPid());
					}
				}).show();
	}

    private void showGames() {
    	Bundle bun = new Bundle();
    	bun.putBoolean("push", isPush);
		bun.putString("name", name);
		bun.putString("url", url);
		bun.putString("checksum", checksum);
		
		Intent intent = new Intent(GameLogoActivity.this, GameListActivity.class);
		intent.putExtras(bun);
		startActivity(intent);
		
		finish();
	}
    
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		super.onKeyDown(keyCode, event);
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void saveBtState() {
		BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
		if(null != mAdapter) {
			boolean boo = mAdapter.isEnabled();
			SharePreferenceUtil.saveBtState(this, boo);
		}
	}
	
	public Handler getHandler() {
		return mHandler;
	}

	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
}
