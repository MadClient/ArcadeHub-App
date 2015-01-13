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
import java.util.HashMap;

import com.adsmogo.adview.AdsMogoLayout;
import com.adsmogo.controller.listener.AdsMogoListener;
import com.umeng.analytics.MobclickAgent;
import com.yunluo.android.arcadehub.ad.ADBanner;
import com.yunluo.android.arcadehub.ad.ADFullInterstitial;
import com.yunluo.android.arcadehub.ad.OnAdListener;
import com.yunluo.android.arcadehub.combination.Combination;
import com.yunluo.android.arcadehub.data.CheatListData;
import com.yunluo.android.arcadehub.helpers.DialogHelper;
import com.yunluo.android.arcadehub.helpers.MainHelper;
import com.yunluo.android.arcadehub.helpers.MenuHelper;
import com.yunluo.android.arcadehub.helpers.PrefsHelper;
import com.yunluo.android.arcadehub.input.ControlCustomizer;
import com.yunluo.android.arcadehub.input.InputHandler;
import com.yunluo.android.arcadehub.input.InputHandlerFactory;
import com.yunluo.android.arcadehub.interfac.OnClickStartListener;
import com.yunluo.android.arcadehub.interfac.OnCombineListener;
import com.yunluo.android.arcadehub.interfac.OnOptionListener;
import com.yunluo.android.arcadehub.keymacro.KeyMacro;
import com.yunluo.android.arcadehub.keymacro.KeyMacroContorl;
import com.yunluo.android.arcadehub.popup.OptionPopup;
import com.yunluo.android.arcadehub.save.ArchiveSubView;
import com.yunluo.android.arcadehub.utils.Debug;
import com.yunluo.android.arcadehub.utils.FileUtil;
import com.yunluo.android.arcadehub.utils.SharePreferenceUtil;
import com.yunluo.android.arcadehub.views.FilterView;
import com.yunluo.android.arcadehub.views.IEmuView;
import com.yunluo.android.arcadehub.views.InputView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class GamePlayActivity extends Activity implements OnOptionListener {

	public static final int MSG_TO_DISPLAYGAMES = 529;

	public static final int MSG_TO_DISPLAYCONTROL = 528;

	public static final int MSG_TO_SHOWGAMES = 527;

	public static final String TAG = "GamesPlayActivity";

	protected View emuView = null;

	protected InputView inputView = null;

	protected FilterView filterView = null;

	protected MainHelper mainHelper = null;

	protected MenuHelper menuHelper = null;

	protected PrefsHelper prefsHelper = null;

	protected DialogHelper dialogHelper = null;

	protected InputHandler inputHandler = null;

	protected FileExplorer fileExplore = null;

	private boolean full = false;

	private ArchiveSubView mArchiveSubView = null;

	public static boolean flag = false;

	private BaseApplication mApp;

	private OptionPopup mOptionPopup;

	private RelativeLayout mRadioLayout;

	private KeyMacroContorl mRadioGroupContorl;

	private KeyMacro mKeyMacro;

	private Combination mCombination;

	private boolean isRequest = true;

	private ADFullInterstitial mAd = null;

	private ADBanner mADBanner = null;

	private boolean isShowAd = false;

	private Handler mHandler = new Handler() {
		public void handleMessage(final Message msg) {
			int arg1 = msg.arg1;
			switch (arg1) {
			case MSG_TO_DISPLAYCONTROL:
				Emulator.startGames();
				break;
			case MSG_TO_DISPLAYGAMES:
				Emulator.dimissLoading();
				if (null != mainHelper) {
					mainHelper.updateMAME4droid();
				}
				if (null != mArchiveSubView) {
					if (true == mArchiveSubView.isLoad()) {
						mArchiveSubView.setLoad(false);
						Emulator.loadGame(mArchiveSubView.getFileName());
					}
				}
				break;
			case MSG_TO_SHOWGAMES:
				if (null != mAd) {
					mAd.setShow(false);
				}
				break;
			default:
				break;
			}
		};
	};

	public Handler getHandler() {
		return mHandler;
	}

	public FileExplorer getFileExplore() {
		return fileExplore;
	}

	public MenuHelper getMenuHelper() {
		return menuHelper;
	}

	public PrefsHelper getPrefsHelper() {
		return prefsHelper;
	}

	public MainHelper getMainHelper() {
		return mainHelper;
	}

	public DialogHelper getDialogHelper() {
		return dialogHelper;
	}

	public View getEmuView() {
		return emuView;
	}

	public InputView getInputView() {
		return inputView;
	}

	public FilterView getFilterView() {
		return filterView;
	}

	public InputHandler getInputHandler() {
		return inputHandler;
	}

	public KeyMacro getKeyMacro() {
		return mKeyMacro;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intentAd = getIntent();
		isRequest = intentAd.getBooleanExtra("isRequestAd", false);
		if (true == isRequest) {
			mAd = new ADFullInterstitial(this);
		}

		Emulator.dimissLoading();

		Emulator.setValue(Emulator.EXIT_GAME, 1);

		overridePendingTransition(0, 0);
		getWindow().setWindowAnimations(0);

		init();

		initGameGui();

		execGames();

		new Thread(new Runnable() {

			@Override
			public void run() {
				Emulator.initGames();
			}
		}).start();
		mCombination = new Combination(this, R.style.myDialogTheme, false);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}

	class GamesTask extends AsyncTask<Void, Void, Void> {

		protected Void doInBackground(Void... args) {
			runMAME4droid();
			return null;
		}

	}

	private void init() {
		mApp = (BaseApplication) this.getApplication();
		prefsHelper = new PrefsHelper(this);
		mArchiveSubView = mApp.getArchiveSubView();
		CheatListData mCheatListData = CheatListData.getInstance();
		mCheatListData.setContext(this);
		mCheatListData.clear();

		mOptionPopup = new OptionPopup(this);

		mOptionPopup.setOnOptionListener(this);

		dialogHelper = new DialogHelper(this, mOptionPopup);

		mainHelper = new MainHelper(this);
		mApp.setMainHelper(mainHelper);

		mainHelper.detectOUYA();

		fileExplore = new FileExplorer(this);

		menuHelper = new MenuHelper(this);

		inputHandler = InputHandlerFactory.createInputHandler(this);

		inputHandler.setOnCombineListener(mOnCombineListener);

		inputHandler.setOnClickStartListener(mOnClicKlistener);

		mKeyMacro = new KeyMacro(this);
	}

	private void initGameGui() {
		full = false;
		if (null == prefsHelper || null == mainHelper) {
			finish();
		}
		setContentView(R.layout.main);
		addAdBanner();

		FrameLayout fl = (FrameLayout) this.findViewById(R.id.EmulatorFrame);
		if (prefsHelper.getVideoRenderMode() == PrefsHelper.PREF_RENDER_SW) {
			this.getLayoutInflater().inflate(R.layout.emuview_sw, fl);
			emuView = this.findViewById(R.id.EmulatorViewSW);
		} else {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
				this.getLayoutInflater().inflate(R.layout.emuview_gl_ext, fl);
			else
				this.getLayoutInflater().inflate(R.layout.emuview_gl, fl);

			emuView = this.findViewById(R.id.EmulatorViewGL);
		}

		if (full && prefsHelper.isPortraitTouchController()) {
			FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) emuView
					.getLayoutParams();
			lp.gravity = Gravity.TOP | Gravity.CENTER;
		}

		inputView = (InputView) this.findViewById(R.id.InputView);

		((IEmuView) emuView).setMAME4droid(this);

		inputView.setMAME4droid(this);

		Emulator.setMAME4droid(this);

		fl.setOnTouchListener(inputHandler);

		if ((prefsHelper.getPortraitOverlayFilterValue() != PrefsHelper.PREF_OVERLAY_NONE && mainHelper
				.getscrOrientation() == Configuration.ORIENTATION_PORTRAIT)
				|| (prefsHelper.getLandscapeOverlayFilterValue() != PrefsHelper.PREF_OVERLAY_NONE && mainHelper
						.getscrOrientation() == Configuration.ORIENTATION_LANDSCAPE)) {
			String value;

			if (mainHelper.getscrOrientation() == Configuration.ORIENTATION_PORTRAIT)
				value = prefsHelper.getPortraitOverlayFilterValue();
			else
				value = prefsHelper.getLandscapeOverlayFilterValue();

			if (value != PrefsHelper.PREF_OVERLAY_NONE) {
				getLayoutInflater().inflate(R.layout.filterview, fl);
				filterView = (FilterView) this.findViewById(R.id.FilterView);

				String fileName = getPrefsHelper().getROMsDIR()
						+ File.separator + "overlays" + File.separator + value;

				Bitmap bmp = BitmapFactory.decodeFile(fileName);
				BitmapDrawable bitmapDrawable = new BitmapDrawable(bmp);
				bitmapDrawable.setTileModeXY(Shader.TileMode.REPEAT,
						Shader.TileMode.REPEAT);

				int alpha = 0;
				switch (getPrefsHelper().getEffectOverlayIntensity()) {
				case 1:
					alpha = 25;
					break;
				case 2:
					alpha = 50;
					break;
				case 3:
					alpha = 55;
					break;
				case 4:
					alpha = 60;
					break;
				case 5:
					alpha = 65;
					break;
				case 6:
					alpha = 70;
					break;
				case 7:
					alpha = 75;
					break;
				case 8:
					alpha = 80;
					break;
				case 9:
					alpha = 100;
					break;
				case 10:
					alpha = 125;
					break;
				}

				bitmapDrawable.setAlpha(alpha);
				filterView.setBackgroundDrawable(bitmapDrawable);

				if (full && prefsHelper.isPortraitTouchController()) {
					FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) filterView
							.getLayoutParams();
					lp.gravity = Gravity.TOP | Gravity.CENTER;
				}

				filterView.setMAME4droid(this);
			}

		}

		inputHandler.setInputListeners();

		mainHelper.updateMAME4droid();

		addKeymacroView();

	}

	private void addKeymacroView() {
		FrameLayout.LayoutParams mKeyLp = new FrameLayout.LayoutParams(
				(int) (320 * GameListActivity.SCREEN_DENSITY),
				(int) (240 * GameListActivity.SCREEN_DENSITY));
		mKeyLp.gravity = Gravity.CENTER_HORIZONTAL;
		mKeyLp.topMargin = 8;

		LinearLayout mKeyLayout = new LinearLayout(this);
		mKeyLayout.setGravity(Gravity.CENTER_HORIZONTAL);

		mRadioLayout = (RelativeLayout) LayoutInflater.from(this).inflate(
				R.layout.key_macro_layout, null);

		mKeyLayout.addView(mRadioLayout);
		this.addContentView(mKeyLayout, mKeyLp);

		mRadioGroupContorl = new KeyMacroContorl(this, mRadioLayout);

		mRadioGroupContorl.load();
	}

	private void execGames() {
		if (!Emulator.isEmulating()) {
			String romsDir = FileUtil.getDefaultROMsDIR();
			if (prefsHelper.getROMsDIR() == null) {
				Debug.d("romsDir", romsDir);
				getPrefsHelper().setROMsDIR(romsDir);
			}
		}
	}

	// ACTIVITY
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (mainHelper != null) {
			mainHelper.activityResult(requestCode, resultCode, data);
		}
	}

	// LIVE CYCLE
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		if (prefsHelper != null)
			prefsHelper.resume();

		if (DialogHelper.savedDialog != -1)
			showDialog(DialogHelper.savedDialog);
		else if (!ControlCustomizer.isEnabled())
			Emulator.resume();

		if (inputHandler != null) {
			if (inputHandler.getTiltSensor() != null)
				inputHandler.getTiltSensor().enable();
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		if (prefsHelper != null)
			prefsHelper.pause();
		if (!ControlCustomizer.isEnabled())
			Emulator.pause();
		if (inputHandler != null) {
			if (inputHandler.getTiltSensor() != null)
				inputHandler.getTiltSensor().disable();
		}

		if (dialogHelper != null) {
			dialogHelper.removeDialogs();
		}
	}

	@Override
	protected void onStart() {
		Debug.d(TAG, "onStart");
		super.onStart();
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("type", SharePreferenceUtil.loadName(this));
		MobclickAgent.onEvent(this, "EventId_Play_Game_Des", map);
	}

	@Override
	protected void onStop() {
		Debug.d(TAG, "onStop");
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Debug.d(TAG, "onDestroy()");

		if (true == isRequest) {
			if (null != mADBanner) {
				mADBanner.clearThread();
			}
			if (null != mOnFullAdListener) {
				mOnFullAdListener.exitAdDestroy();
			}
		}

		View frame = this.findViewById(R.id.EmulatorFrame);
		if (frame != null)
			frame.setOnTouchListener(null);

		if (inputHandler != null) {
			inputHandler.unsetInputListeners();

			if (inputHandler.getTiltSensor() != null)
				inputHandler.getTiltSensor().disable();
		}
		clean();
	}

	private void clean() {
		mOptionPopup = null;
		dialogHelper = null;
		mainHelper = null;
		fileExplore = null;
		menuHelper = null;
		mKeyMacro = null;
	}

	// Dialog Stuff
	@Override
	protected Dialog onCreateDialog(int id) {

		if (dialogHelper != null) {
			Dialog d = dialogHelper.createDialog(id);
			if (d != null)
				return d;
		}
		return super.onCreateDialog(id);
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if (dialogHelper != null)
			dialogHelper.prepareDialog(id, dialog);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		overridePendingTransition(0, 0);

		if (null != mOptionPopup && mOptionPopup.isShown()) {
			mOptionPopup.change();
		}

		if (false == full) {
			RelativeLayout frame = (RelativeLayout) this
					.findViewById(R.id.emulator_frame_layout);
			if (null == frame) {
				return;
			}
			RelativeLayout nullView = (RelativeLayout) this
					.findViewById(R.id.ad_view);
			if (mainHelper.getscrOrientation() == Configuration.ORIENTATION_PORTRAIT) {
				if (frame.getParent() instanceof RelativeLayout) {
					RelativeLayout.LayoutParams relatly = new RelativeLayout.LayoutParams(
							frame.getLayoutParams());
					relatly.addRule(RelativeLayout.ABOVE, R.id.InputView);
					frame.setLayoutParams(relatly);
					if (null != nullView) {
						nullView.setVisibility(View.VISIBLE);
					}
				}

			} else {
				if (null != nullView) {
					nullView.setVisibility(View.GONE);
				}

				if (null != frame) {
					RelativeLayout.LayoutParams relatly = new RelativeLayout.LayoutParams(
							frame.getLayoutParams());
					relatly.addRule(RelativeLayout.CENTER_IN_PARENT,
							RelativeLayout.TRUE);
					frame.setLayoutParams(relatly);

				}

			}
		}
		mainHelper.updateMAME4droid();
		overridePendingTransition(0, 0);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	}

	@Override
	public void doCheat() {
		mainHelper.showCheat();
	}

	private static boolean isPause = false;

	@Override
	public void doFrezee() {
		if (false == isPause) {
			Emulator.pauseGame();
			Toast.makeText(this, "Game Pause...", Toast.LENGTH_LONG).show();
		} else {
			Emulator.resumeGame();
			Toast.makeText(this, "Game Start...", Toast.LENGTH_LONG).show();
		}
		isPause = !isPause;
		Emulator.resume();
	}

	@Override
	public void doLoadGame() {
		mainHelper.showArchiveSubView();
	}

	public void doSaveGame() {
		mainHelper.showArchiveSaveView();
	}

	@Override
	public void doSettings() {
		mainHelper.showSettings();
	}

	@Override
	public void doHelp() {
		mainHelper.showHelp();
	}

	@Override
	public void doAbout() {
		mainHelper.showAbout();
	}

	@Override
	public void doKeyMacro() {
		if (null != mRadioLayout) {
			mRadioLayout.setVisibility(View.VISIBLE);
			if (null != mRadioGroupContorl) {
				mRadioGroupContorl.refreshLoad();
			}
		}
		if (null != inputHandler) {
			inputHandler.setCombinedKey(true);
		}
		doAnim(true);

	}

	public void doAnim(boolean boo) {
		if (null == mRadioLayout) {
			return;
		}
		if (boo == true) {
			Animation mAnimation = AnimationUtils.loadAnimation(
					GamePlayActivity.this, R.anim.anim_keymacro_enter);
			mRadioLayout.startAnimation(mAnimation);
		} else {
			Animation mAnimation = AnimationUtils.loadAnimation(
					GamePlayActivity.this, R.anim.anim_keymacro_exit);
			mAnimation.setAnimationListener(mExitAnimListener);
			mRadioLayout.startAnimation(mAnimation);
		}
	}

	@Override
	public void doKeyCombination() {
		// TODO Auto-generated method stub
		mainHelper.showCombination();
	}

	private AnimationListener mExitAnimListener = new AnimationListener() {

		@Override
		public void onAnimationEnd(Animation animation) {
			if (null == mRadioLayout) {
				return;
			}
			if (View.VISIBLE == mRadioLayout.getVisibility()) {
				mRadioLayout.setVisibility(View.GONE);
			}
		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}

		@Override
		public void onAnimationStart(Animation animation) {

		}

	};

	public OnCombineListener mOnCombineListener = new OnCombineListener() {

		@Override
		public void onCombineKey(int key) {
			if (null != mRadioGroupContorl) {
				mRadioGroupContorl.add(key);
			}
		}

	};

	private OnClickStartListener mOnClicKlistener = new OnClickStartListener() {

		@Override
		public void doRemoveAd() {
			removeBanner();
		}

		@Override
		public void doAddAd() {
			isShowAd = addBanner();
		}

	};
}