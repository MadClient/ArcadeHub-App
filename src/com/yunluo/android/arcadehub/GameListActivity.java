/*
 * This file is part of MAME4droid.
 *
 * Copyright (C) 2013 David Valdeita (Seleuco)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 * Linking MAME4droid statically or dynamically with other modules is
 * making a combined work based on MAME4droid. Thus, the terms and
 * conditions of the GNU General Public License cover the whole
 * combination.
 *
 * In addition, as a special exception, the copyright holders of MAME4droid
 * give you permission to combine MAME4droid with free software programs
 * or libraries that are released under the GNU LGPL and with code included
 * in the standard release of MAME under the MAME License (or modified
 * versions of such code, with unchanged license). You may copy and
 * distribute such a system following the terms of the GNU GPL for MAME4droid
 * and the licenses of the other code concerned, provided that you include
 * the source code of that other code when and as the GNU GPL requires
 * distribution of source code.
 *
 * Note that people who make modified versions of MAME4idroid are not
 * obligated to grant this special exception for their modified versions; it
 * is their choice whether to do so. The GNU General Public License
 * gives permission to release a modified version without this exception;
 * this exception also makes it possible to release a modified version
 * which carries forward this exception.
 *
 * MAME4droid is dual-licensed: Alternatively, you can license MAME4droid
 * under a MAME license, as set out in http://mamedev.org/
 */

package com.yunluo.android.arcadehub;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.umeng.analytics.MobclickAgent;
import com.yunluo.android.arcadehub.async.RomInfo;
import com.yunluo.android.arcadehub.async.UpdateAnsyncTask;
import com.yunluo.android.arcadehub.download.DownloadManager;
import com.yunluo.android.arcadehub.download.DownLoadActivity;
import com.yunluo.android.arcadehub.helpers.MainHelper;
import com.yunluo.android.arcadehub.interfac.OnDownloadListener;
import com.yunluo.android.arcadehub.interfac.OnUpdateListener;
import com.yunluo.android.arcadehub.netplay.NetPlayActivity;
import com.yunluo.android.arcadehub.push.OnPushResponseListener;
import com.yunluo.android.arcadehub.push.PushControl;
import com.yunluo.android.arcadehub.push.PushDialog;
import com.yunluo.android.arcadehub.push.PushDialog.OnHtmlPushListener;
import com.yunluo.android.arcadehub.push.PushDialog.OnTxtPushListener;
import com.yunluo.android.arcadehub.push.PushNotify;
import com.yunluo.android.arcadehub.push.PushParse;
import com.yunluo.android.arcadehub.save.ArchiveActivity;
import com.yunluo.android.arcadehub.sliding.BaseActivity;
import com.yunluo.android.arcadehub.sliding.OnMenuListener;
import com.yunluo.android.arcadehub.sliding.view.LeftView;
import com.yunluo.android.arcadehub.sliding.view.ListShowView;
import com.yunluo.android.arcadehub.sliding.view.RightView;
import com.yunluo.android.arcadehub.utils.ContentValue;
import com.yunluo.android.arcadehub.utils.Debug;
import com.yunluo.android.arcadehub.utils.FileUtil;
import com.yunluo.android.arcadehub.utils.SharePreferenceUtil;
import com.yunluo.android.arcadehub.utils.Utils;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class GameListActivity extends BaseActivity implements OnMenuListener,
		OnUpdateListener {

	public final static String TAG = "GamesListActivity";

	public static int SCREEN_WIDTH = 0;

	public static float SCREEN_DENSITY = 1;

	private ListShowView mListShowView;

	// download start
	public static final String PERCENT = "percent";
	public static final String FILE_ID = "file_id";
	public static final String FILE_NAME = "file_name";
	public static final String FILE_PATH = "file_path";
	public static final String FILE_LENGTH = "file_length";

	//
	public static final int DOWNLOAD_FINISH = 800;
	public static final int DOWNLOAD_LOADING = 801;
	public static final int DOWNLOAD_ERROR = 802;

	public static final int SCAN_FINISH = 901;

	public static final int FILE_SEARCH_FINISH = 8003;

	private DownloadManager mDownloadManager = null;

	private PushDialog mHtmlDialog = null;

	private Handler mHandler = new Handler() {
		public void handleMessage(final Message msg) {
			int arg1 = msg.arg1;
			Bundle bun = msg.getData();
			if (null == bun) {
				return;
			}
			String id = bun.getString(FILE_ID);
			switch (arg1) {
			case DOWNLOAD_LOADING:
				Debug.d("download_loading", "");
				int precent = bun.getInt(PERCENT);
				long length = bun.getLong(FILE_LENGTH);
				downloading(id, precent, length);
				break;
			case DOWNLOAD_FINISH:
				Debug.d("download_finish", "");
				downloaded(id);
				break;
			case DOWNLOAD_ERROR:
				Debug.d("download_error", "");
				downloadError(id);
				SharePreferenceUtil.saveDownloadState(GameListActivity.this,
						"0");
				break;
			case SCAN_FINISH:
				if (null != mListShowView) {
					mListShowView.finishRefresh();
				}

				callPush();
				break;
			default:
				break;
			}
		};
	};

	private List<RomInfo> mRomList = null;

	private BaseApplication mApp = null;

	public static int SCREEN_HEIGHT = 0;

	private DisplayMetrics mMetrics = null;

	private LeftView mLeftView = null;

	private RightView mRightView = null;

	private FrameLayout mFrameLayout = null;

	private RelativeLayout mMainLayout = null;

	private UpdateAnsyncTask mSearchTask = null;

	private PushControl mPushResources = null;

	private PushParse mPushParse = null;

	private PushNotify mPushNotify = null;

	private int mCpu = 0;

	private int mRam = 0;

	private boolean isPush = false;

	public static int TIME = 24 * 60 * 60 * 1000;

	public Handler getHandler() {
		return mHandler;
	}

	public List<RomInfo> getRomList() {
		return mRomList;
	}

	public int getCpu() {
		return mCpu;
	}

	public int getRam() {
		return mRam;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		getScreenSize();

		init();

		initSliding();

		addSearchView();

		mApp.register();

		initPush();

		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

	}

	private void initSliding() {

		slidingDisable();

		initLeftView();

		mMainLayout = new RelativeLayout(this);
		mMainLayout.setGravity(Gravity.CENTER_HORIZONTAL);

		mFrameLayout = new FrameLayout(this);

		setContentView(mFrameLayout);

		initRightView();

		mListShowView = mRightView.getListShowView();

		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

		if (null != mListShowView) {
			mListShowView.firstRefresh();
		}

	}

	private void initLeftView() {
		FrameLayout.LayoutParams leftLp = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.MATCH_PARENT);
		mLeftView = new LeftView(this, null);
		mLeftView.setLayoutParams(leftLp);
		mLeftView.setOnMenuListener(this);

		setBehindContentView(mLeftView);
	}

	private void initRightView() {
		FrameLayout.LayoutParams rightLp = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.MATCH_PARENT);
		mRightView = new RightView(this, mRomList);
		mRightView.setBackgroundColor(Color.WHITE);
		mFrameLayout.addView(mRightView, rightLp);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	@Override
	public void doSearch() {

		mHandler.post(new Runnable() {

			@Override
			public void run() {
				mSearchTask = new UpdateAnsyncTask(GameListActivity.this,
						mRomList, mHandler);
				mSearchTask.execute(Utils.getRootPath(GameListActivity.this));
			}

		});
	}

	@Override
	public void doRecommend() {
		showRecommend();
	}

	@Override
	public void doSaveFile() {
		showArchive();
	}

	@Override
	public void doSettings() {
		showSetting();
	}

	@Override
	public void doGamesPlay() {
		showGamesPlay(true);
	}

	@Override
	public void doAbout() {
		showAbout();
	}

	@Override
	public void doDefaultLanguage() {
		Configuration config = getResources().getConfiguration();
		DisplayMetrics dm = getResources().getDisplayMetrics();
		config.locale = Locale.ENGLISH;
		getResources().updateConfiguration(config, dm);

		finish();
		Intent myIntent = new Intent(GameListActivity.this,
				GameListActivity.class);
		startActivity(myIntent);

		defaultLanguage();
		update();
	}

	@Override
	public void doExit() {
		exit();
	}

	private void showRecommend() {
		Intent recommend = new Intent(GameListActivity.this,
				DownLoadActivity.class);
		startActivityForResult(recommend, MainHelper.SUBACTIVITY_RECOMMEND);
	}

	private void showSetting() {
		Intent i = new Intent(GameListActivity.this, GameSettingActivity.class);
		startActivityForResult(i, MainHelper.SUBACTIVITY_USER_PREFS);
	}

	public void showGamesPlay(boolean isRequeatAd) {
		Intent intent = new Intent(GameListActivity.this,
				GamePlayActivity.class);
		intent.putExtra("isRequestAd", isRequeatAd);
		startActivity(intent);
	}

	private void showArchive() {
		Intent intent = new Intent(GameListActivity.this, ArchiveActivity.class);
		startActivity(intent);
	}

	private void showAbout() {
		Intent intent = new Intent(GameListActivity.this, AboutActivity.class);
		startActivity(intent);
	}

	public void startNetplay() {
		if (false == Utils.checkInternet(this)) {
			showNetwork();
			Emulator.dimissLoading();
			return;
		}
		Intent mIntent = new Intent();
		mIntent.setClass(GameListActivity.this, NetPlayActivity.class);
		mIntent.putExtra("Name", true);
		startActivity(mIntent);
	}

	public void startBtPlay() {
		Intent mIntent = new Intent();
		mIntent.setClass(GameListActivity.this, NetPlayActivity.class);
		mIntent.putExtra("Name", false);
		startActivity(mIntent);
	}

	private void addSearchView() {
	}

	public void addItem(String name, String size, String path) {
		RomInfo mRomInfo = new RomInfo();
		mRomInfo.setDesc(Emulator.getGameDesc(name));
		mRomInfo.setName(name);
		mRomInfo.setSize(size);
		mRomInfo.setPath(path);
		if (null != mRomList) {
			mRomList.add(mRomInfo);
			update();
		}
	}

	public void update() {
		if (null == mRightView) {
			return;
		}

		if (null == mListShowView) {
			return;
		}

		mListShowView.update();
	}

	public void closeOpenedItems() {
		if (null != mListShowView) {
			mListShowView.closeOpenedItems();
		}
	}

	private void init() {

		mApp = (BaseApplication) this.getApplication();

		mApp.setGameListActivity(this);

		mRomList = mApp.getRomList();

		mDownloadManager = new DownloadManager();

		mDownloadManager.setOnDownloadListener(mOnDownloadListener);

		mDownloadManager.start(2);

		Emulator.setArcadeHub(this);

		mApp.setOnUpdateListener(this);

		String cpu = Utils.getCpu();
		if (null != cpu) {
			float fCpu = Float.valueOf(cpu);
			int tmp = (int) fCpu;
			mCpu = tmp;
		}
		String ram = Utils.getRam();
		if (null != ram) {
			mRam = Integer.valueOf(ram);
		}

	}

	private void initPush() {
		mPushResources = new PushControl(this);
		mPushParse = new PushParse();
		mPushNotify = new PushNotify(this);

		mPushResources.setOnPushResponseListener(mOnPushResponseListener);
	}

	private void callPush() {
		if (false == Utils.checkInternet(this)) {
			SharePreferenceUtil.saveTime(this, System.currentTimeMillis());
			return;
		}

		push();
	}

	private void push() {
		Bundle bun = this.getIntent().getExtras();
		if (null == bun) {
			return;
		}
		isPush = bun.getBoolean("push", false);
		String name = bun.getString("name");
		String url = bun.getString("url");
		String cs = bun.getString("checksum");

		if (null != name && null != url) {
			pushDownload(name, url, cs);
		}

		if (false == isPush && isStamp()) {
			mPushResources.get();
		}

	}

	private boolean isStamp() {
		long saveTime = SharePreferenceUtil.loadTime(this);
		long curTime = System.currentTimeMillis();
		long timing = curTime - saveTime;
		if (timing > TIME) {
			return true;
		} else {
			return false;
		}
	}

	private OnPushResponseListener mOnPushResponseListener = new OnPushResponseListener() {

		@Override
		public void doPushHeaderResponse(byte data[]) {
			if (null == mPushParse) {
				return;
			}

			if (null == mHandler) {
				return;
			}

			mPushParse.parse(new String(data, 0, data.length));

			final int eventCode = mPushParse.getEc();

			mHandler.post(new Runnable() {

				@Override
				public void run() {
					switch (eventCode) {
					case 200: 
						String name = mPushParse.getName();
						String cs = mPushParse.getCs();
						if (true == isContainCs(cs)) {
							SharePreferenceUtil.saveGameResource(
									GameListActivity.this, 1);
							SharePreferenceUtil.saveTime(GameListActivity.this,
									System.currentTimeMillis());
							SharePreferenceUtil.saveCheckSum(
									GameListActivity.this, cs);
							return;
						} else {
							SharePreferenceUtil.saveGameResource(
									GameListActivity.this, 0);
						}

						pushShowDialog(mPushParse);

						break;
					case 400:
						SharePreferenceUtil.saveTime(GameListActivity.this,
								System.currentTimeMillis());
						break;
					}
				}

			});
		}

		@Override
		public void doErrorResponse() {
			SharePreferenceUtil.saveTime(GameListActivity.this,
					System.currentTimeMillis());
		}

		@Override
		public void doPushBodyResponse(byte[] data) {
			String body = new String(data, 0, data.length);
			if (null == mPushParse) {
				return;
			}

			mPushParse.parseBody(body);
		}

	};

	private void pushShowDialog(final PushParse pushParse) {
		if (null == pushParse) {
			return;
		}
		int nt = mPushParse.getNt();
		switch (nt) {
		case 1:
			pushText(pushParse);
			break;
		case 2:
			pushHtml(pushParse);
			break;
		}
	}

	private void pushText(final PushParse pushParse) {

		PushDialog mTxtDialog = new PushDialog(this, false);
		mTxtDialog.setOnTxtPushListener(mOnTxtPushListener);
		mTxtDialog.setTxtTitle(mPushParse.getTnt());
		mTxtDialog.setTxtMessage(mPushParse.getTnc());
		mTxtDialog.setTxtBitmap(mPushParse.getBmp());
		mTxtDialog.show();
	}

	private void pushHtml(PushParse pushParse) {
		mHtmlDialog = new PushDialog(this, true);
		mHtmlDialog.setOnHtmlPushListener(mOnHtmlPushListener);
		mHtmlDialog
				.loadUrl("http://kmamek.iptime.org/mamev/index.php?mid=rom_all_mame&page=8&document_srl=65489");
		mHtmlDialog.show();
	}

	private void pushDownload(String name, String url, String checksum) {
		Intent intent = new Intent();
		intent.setAction(ContentValue.DOWNLOAD_ACTION);
		intent.putExtra("name", name);
		intent.putExtra("url", url);
		intent.putExtra("checksum", checksum);
		this.sendBroadcast(intent);
	}

	private void pushNotify() {
		if (null == mPushNotify) {
			return;
		}
		mPushNotify.setPushParse(mPushParse);
		mPushNotify.showNotification();
	}

	private OnTxtPushListener mOnTxtPushListener = new OnTxtPushListener() {

		@Override
		public void doOk() {
			String name = mPushParse.getName();
			String url = mPushParse.getTdu();
			String cs = mPushParse.getCs();
			pushDownload(name, url, cs);
		}

		@Override
		public void doCancel() {
			pushNotify();
		}

	};

	private OnHtmlPushListener mOnHtmlPushListener = new OnHtmlPushListener() {

		@Override
		public void doClose() {
			pushNotify();
		}

	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		super.onKeyDown(keyCode, event);
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			exit();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	// LIVE CYCLE
	@Override
	protected void onResume() {
		super.onResume();
		new UpdateLngTask().execute();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
		Emulator.stopScan();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (null != mApp) {
			mApp.unregister();
		}
		if (null != mRightView) {
			mRightView.clear();
		}
	}

	public void deleteGame(String rom_path, String rom_name) {

	}

	class UpdateLngTask extends AsyncTask<String, Void, String> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected String doInBackground(String... params) {
			updateLanguage();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			update();
		}

	}

	private void updateLanguage() {
		if (null == mRomList) {
			return;
		}
		String historyLng = SharePreferenceUtil.loadLng(this);
		String localLng = Locale.getDefault().getLanguage().substring(0, 2);
		if (historyLng.equals(localLng) && false == "zh".equals(localLng)) {
			return;
		}

		boolean isChinese = Utils.isChinese();

		for (RomInfo info : mRomList) {
			info.setDesc(isChinese ? Utils.changeTxt(info.getCname()) : info
					.getEname());
		}

		SharePreferenceUtil.saveLng(this);
	}

	private void defaultLanguage() {
		if (null == mRomList) {
			return;
		}

		for (RomInfo info : mRomList) {
			info.setDesc(info.getEname());
		}

		SharePreferenceUtil.saveLng(this);

	}

	private void getScreenSize() {
		mMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
		SCREEN_WIDTH = mMetrics.widthPixels;
		SCREEN_HEIGHT = mMetrics.heightPixels;
		if (SCREEN_WIDTH > SCREEN_HEIGHT) {
			SCREEN_WIDTH = SCREEN_HEIGHT;
		}
		SCREEN_DENSITY = getResources().getDisplayMetrics().density;
	}

	public boolean isFound(String name) {

		if (null == mRomList) {
			Toast.makeText(this, "mRomList == null", Toast.LENGTH_LONG).show();
			return false;
		}
		if (null == name) {
			return false;
		}

		for (RomInfo rom : mRomList) {
			if (name.equals(rom.getName())) {
				return true;
			}
		}
		return false;
	}

	private void showNetwork() {
		Resources res = this.getResources();
		new AlertDialog.Builder((FragmentActivity) GameListActivity.this)
				.setTitle(res.getString(R.string.gamelist_network_title))
				.setMessage(res.getString(R.string.gamelist_network_msg))
				.setPositiveButton(res.getString(R.string.BTN_COMMON_OK),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
							}
						}).show();
	}

	private void exit() {
		Resources res = this.getResources();
		AlertDialog dialog = new AlertDialog.Builder(
				(FragmentActivity) GameListActivity.this)
				.setTitle(res.getString(R.string.MSG_COMMON_ALERT_TITLE))
				.setMessage(res.getString(R.string.MSG_COMMON_ALERT_EXIT))
				.setPositiveButton(res.getString(R.string.BTN_COMMON_OK),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Emulator.resetGame();
								mApp.unregister();
								FileUtil.write(GameListActivity.this, mRomList);
								if (false == SharePreferenceUtil
										.loadBtState(GameListActivity.this)) {
									Utils.disableBt();
								}
								android.os.Process
										.killProcess(android.os.Process.myPid());
							}
						})
				.setNegativeButton(res.getString(R.string.BTN_COMMON_CANCEL),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

							}
						}).show();
	}

	/**
	 * rom list is contains current name
	 * 
	 * @param name
	 * @return
	 */
	private boolean isContainsName(String name) {
		if (null == name) {
			return true;
		}
		boolean mNameKey = false;
		if (null == mRomList) {
			Debug.e("mRomList = ", "mRomList null");
			return true;
		} else {
			b: for (RomInfo str : mRomList) {
				String m = str.getName();
				if (m == name || m.equals(name)) {
					mNameKey = true;
					break b;
				}
			}
		}
		return mNameKey;
	}

	private boolean isContainCs(String cs) {
		if (null == cs) {
			return true;
		}
		boolean isContain = false;
		if (null == mRomList) {
			return true;
		} else {
			b: for (RomInfo info : mRomList) {
				String m = info.getCheckSum();
				if (m == cs || m.equals(cs)) {
					isContain = true;
					break b;
				}
			}
		}
		return isContain;
	}

	public void download(final String url, final String name,
			final String checkSum) {

		if (null != mHtmlDialog && mHtmlDialog.isShowing()) {
			mHtmlDialog.exit();
			;
		}

		if (true == isContainsName(name)) {
			return;
		}

		if (null == url) {
			return;
		}

		mHandler.post(new Runnable() {

			@Override
			public void run() {
				String id = UUID.randomUUID().toString();
				String desc = null;
				if (null != name) {
					desc = Emulator.getGameDesc(name);
				}
				RomInfo mRomInfo = new RomInfo();
				mRomInfo.setId(id);
				mRomInfo.setPrecent(0);
				mRomInfo.setName(name);
				mRomInfo.setDesc(desc);
				mRomInfo.setCname(Emulator.getGame(name, 0));
				mRomInfo.setEname(Emulator.getGame(name, 1));
				mRomInfo.setPath(ContentValue.DOWNLOAD_PATH);
				mRomInfo.setIcon(name);
				mRomInfo.setSuffix(".zip");
				mRomInfo.setCheckSum(checkSum);
				mRomList.add(0, mRomInfo);
				if (null != mListShowView) {
					mListShowView.update();
				}

				mDownloadManager.addTask(url, id);
			}
		});
	}

	private void downloading(String id, int precent, long length) {
		int size = mRomList.size();

		for (int i = 0; i < size; i++) {
			RomInfo mRomInfo = mRomList.get(i);

			if (mRomInfo.getId() == null) {
				continue;
			}

			if (id.equals(mRomInfo.getId())) {
				if (false == mRomInfo.isUsing()) {
					mRomInfo.setUsing(true);
					String romSize = FileUtil.formatFileSize(length);
					mRomInfo.setSize(romSize);
				}

				mRomInfo.setPrecent(precent);

				if (null != mListShowView) {
					mListShowView.upDateProgress(i);
				}

				return;
			}
		}
	}

	private void downloaded(String id) {
		int size = mRomList.size();
		for (int i = 0; i < size; i++) {
			RomInfo mRomInfo = mRomList.get(i);
			String mId = mRomInfo.getId();
			if (mId == null) {
				continue;
			}
			if (id.equals(mId)) {
				mRomInfo.setPrecent(100);
				String pathS = ContentValue.DOWNLOAD_PATH + File.separator
						+ mRomInfo.getId() + mRomInfo.getSuffix();
				String newPath = mRomInfo.getPath() + File.separator
						+ mRomInfo.getName() + mRomInfo.getSuffix();
				File file = new File(pathS);
				file.renameTo(new File(newPath));
				mRomInfo.setId(null);

				if (null != mListShowView) {
					mListShowView.upDateProgress(i);
				}

				SharePreferenceUtil.saveDownloadState(GameListActivity.this,
						"1");
				SharePreferenceUtil.saveTime(GameListActivity.this,
						System.currentTimeMillis());
				SharePreferenceUtil.saveCheckSum(GameListActivity.this,
						mRomInfo.getCheckSum());

				return;
			}
		}
	}

	private void downloadError(String id) {
		int size = mRomList.size();
		for (int i = 0; i < size; i++) {
			RomInfo mRomInfo = mRomList.get(i);

			if (mRomInfo.getId() == null) {
				return;
			}

			if (id.equals(mRomInfo.getId())) {
				String pathS = mRomInfo.getPath() + File.separator
						+ mRomInfo.getId() + mRomInfo.getSuffix();
				File file = new File(pathS);
				if (file.exists()) {
					boolean boo = file.delete();
				} else {
					Debug.d("This file is not found.", "");
				}

				mRomList.remove(mRomInfo);

				if (null != mListShowView) {
					mListShowView.update();
				}
				return;
			}
		}
	}

	private OnDownloadListener mOnDownloadListener = new OnDownloadListener() {

		@Override
		public void downloaded(String id) {
			Message msg = new Message();
			msg.arg1 = DOWNLOAD_FINISH;
			Bundle bun = new Bundle();
			bun.putString(FILE_ID, id);
			msg.setData(bun);
			mHandler.sendMessage(msg);
		}

		@Override
		public void downloading(String id, int precent, long length) {
			Message msg = new Message();
			msg.arg1 = DOWNLOAD_LOADING;
			Bundle bun = new Bundle();
			bun.putString(FILE_ID, id);
			bun.putInt(PERCENT, precent);
			bun.putLong(FILE_LENGTH, length);
			msg.setData(bun);
			mHandler.sendMessage(msg);
		}

		@Override
		public void downloadError(String id) {
			Message msg = new Message();
			msg.arg1 = DOWNLOAD_ERROR;
			Bundle bun = new Bundle();
			bun.putString(FILE_ID, id);
			msg.setData(bun);
			mHandler.sendMessage(msg);
		}

	};

	// down load end
	@Override
	public void onUpdate() {
		update();
	}

}