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
import java.io.FilenameFilter;

import com.umeng.analytics.MobclickAgent;
import com.yunluo.android.arcadehub.R;
import com.yunluo.android.arcadehub.ad.ADBanner;
import com.yunluo.android.arcadehub.helpers.PrefsHelper;
import com.yunluo.android.arcadehub.input.ControlCustomizer;
import com.yunluo.android.arcadehub.input.InputHandler;
import com.yunluo.android.arcadehub.input.InputHandlerExt;
import com.yunluo.android.arcadehub.prefs.DefineKeys;
import com.yunluo.android.arcadehub.save.ArchiveSaveActivity;
import com.yunluo.android.arcadehub.utils.Debug;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

public class GameSettingActivity extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {

	private SharedPreferences settings;

	protected ListPreference mPrefGlobalVideoRenderMode;
	protected ListPreference mPrefResolution;
	protected ListPreference mPrefSpeed;
	protected ListPreference mPrefPortraitMode;
	protected ListPreference mPrefLandsMode;
	protected ListPreference mPrefPortraitOverlay;
	protected ListPreference mPrefLandsOverlay;
	protected ListPreference mPrefControllerType;
	protected ListPreference mPrefExtInput;
	protected ListPreference mPrefAutomap;
	protected ListPreference mPrefAnalogDZ;
	protected ListPreference mPrefGamepadDZ;
	protected ListPreference mPrefTiltDZ;
	protected ListPreference mPrefTiltNeutral;
	protected ListPreference mPrefFrameSkip;
	protected ListPreference mPrefSound;
	protected ListPreference mPrefStickType;
	protected ListPreference mPrefNumButtons;
	protected ListPreference mPrefSizeButtons;
	protected ListPreference mPrefSizeStick;
	protected ListPreference mPrefVideoThPr;
	protected ListPreference mPrefMainThPr;
	protected ListPreference mPrefSoundEngine;
	protected ListPreference mPrefAutofire;
	protected ListPreference mPrefVSync;
	protected ListPreference mPrefFilterCat;
	protected ListPreference mPrefFilterDrvSrc;
	protected ListPreference mPrefFilterManuf;
	protected ListPreference mPrefFilterYGTE;
	protected ListPreference mPrefFilterYLTE;
	protected EditTextPreference mPrefFilterkeyword;
	protected ListPreference mPrefOverlayInt;
	protected ListPreference mPrefForcPX;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		final boolean isCustom = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.userpreferences);

		Debug.e("isCustom = ", "" + isCustom);

		if (isCustom) {
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
					R.layout.game_setting_back);
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}

		settings = PreferenceManager.getDefaultSharedPreferences(this);

		mPrefGlobalVideoRenderMode = (ListPreference) getPreferenceScreen()
				.findPreference(PrefsHelper.PREF_GLOBAL_VIDEO_RENDER_MODE);
		mPrefResolution = (ListPreference) getPreferenceScreen()
				.findPreference(PrefsHelper.PREF_GLOBAL_RESOLUTION);
		mPrefSpeed = (ListPreference) getPreferenceScreen().findPreference(
				PrefsHelper.PREF_GLOBAL_SPEED);
		mPrefPortraitMode = (ListPreference) getPreferenceScreen()
				.findPreference(PrefsHelper.PREF_PORTRAIT_SCALING_MODE);
		mPrefLandsMode = (ListPreference) getPreferenceScreen().findPreference(
				PrefsHelper.PREF_LANDSCAPE_SCALING_MODE);

		mPrefPortraitOverlay = (ListPreference) getPreferenceScreen()
				.findPreference(PrefsHelper.PREF_PORTRAIT_OVERLAY);
		populateOverlayList(mPrefPortraitOverlay);

		mPrefLandsOverlay = (ListPreference) getPreferenceScreen()
				.findPreference(PrefsHelper.PREF_LANDSCAPE_OVERLAY);
		populateOverlayList(mPrefLandsOverlay);

		mPrefControllerType = (ListPreference) getPreferenceScreen()
				.findPreference(PrefsHelper.PREF_CONTROLLER_TYPE);
		mPrefExtInput = (ListPreference) getPreferenceScreen().findPreference(
				PrefsHelper.PREF_INPUT_EXTERNAL);
		mPrefAutomap = (ListPreference) getPreferenceScreen().findPreference(
				PrefsHelper.PREF_AUTOMAP_OPTIONS);
		mPrefAnalogDZ = (ListPreference) getPreferenceScreen().findPreference(
				PrefsHelper.PREF_ANALOG_DZ);
		mPrefGamepadDZ = (ListPreference) getPreferenceScreen().findPreference(
				PrefsHelper.PREF_GAMEPAD_DZ);
		mPrefTiltDZ = (ListPreference) getPreferenceScreen().findPreference(
				PrefsHelper.PREF_TILT_DZ);
		mPrefTiltNeutral = (ListPreference) getPreferenceScreen()
				.findPreference(PrefsHelper.PREF_TILT_NEUTRAL);
		mPrefFrameSkip = (ListPreference) getPreferenceScreen().findPreference(
				PrefsHelper.PREF_GLOBAL_FRAMESKIP);
		mPrefSound = (ListPreference) getPreferenceScreen().findPreference(
				PrefsHelper.PREF_GLOBAL_SOUND);
		mPrefStickType = (ListPreference) getPreferenceScreen().findPreference(
				PrefsHelper.PREF_STICK_TYPE);
		mPrefNumButtons = (ListPreference) getPreferenceScreen()
				.findPreference(PrefsHelper.PREF_NUMBUTTONS);
		mPrefSizeButtons = (ListPreference) getPreferenceScreen()
				.findPreference(PrefsHelper.PREF_BUTTONS_SIZE);
		mPrefSizeStick = (ListPreference) getPreferenceScreen().findPreference(
				PrefsHelper.PREF_STICK_SIZE);
		mPrefVideoThPr = (ListPreference) getPreferenceScreen().findPreference(
				PrefsHelper.PREF_VIDEO_THREAD_PRIORITY);
		mPrefMainThPr = (ListPreference) getPreferenceScreen().findPreference(
				PrefsHelper.PREF_MAIN_THREAD_PRIORITY);
		mPrefSoundEngine = (ListPreference) getPreferenceScreen()
				.findPreference(PrefsHelper.PREF_SOUND_ENGINE);
		mPrefAutofire = (ListPreference) getPreferenceScreen().findPreference(
				PrefsHelper.PREF_AUTOFIRE);
		mPrefVSync = (ListPreference) getPreferenceScreen().findPreference(
				PrefsHelper.PREF_GLOBAL_VSYNC);

		mPrefFilterCat = (ListPreference) getPreferenceScreen().findPreference(
				PrefsHelper.PREF_FILTER_CATEGORY);
		populateFilterList(Emulator.FILTER_NUM_CATEGORIES,
				Emulator.FILTER_CATEGORIES_ARRAY, mPrefFilterCat);

		mPrefFilterDrvSrc = (ListPreference) getPreferenceScreen()
				.findPreference(PrefsHelper.PREF_FILTER_DRVSRC);
		populateFilterList(Emulator.FILTER_NUM_DRIVERS_SRC,
				Emulator.FILTER_DRIVERS_SRC_ARRAY, mPrefFilterDrvSrc);

		mPrefFilterManuf = (ListPreference) getPreferenceScreen()
				.findPreference(PrefsHelper.PREF_FILTER_MANUF);
		populateFilterList(Emulator.FILTER_NUM_MANUFACTURERS,
				Emulator.FILTER_MANUFACTURERS_ARRAY, mPrefFilterManuf);

		mPrefFilterYGTE = (ListPreference) getPreferenceScreen()
				.findPreference(PrefsHelper.PREF_FILTER_YGTE);
		populateFilterList(Emulator.FILTER_NUM_YEARS,
				Emulator.FILTER_YEARS_ARRAY, mPrefFilterYGTE);
		mPrefFilterYLTE = (ListPreference) getPreferenceScreen()
				.findPreference(PrefsHelper.PREF_FILTER_YLTE);
		populateFilterList(Emulator.FILTER_NUM_YEARS,
				Emulator.FILTER_YEARS_ARRAY, mPrefFilterYLTE);

		mPrefFilterkeyword = (EditTextPreference) getPreferenceScreen()
				.findPreference(PrefsHelper.PREF_FILTER_KEYWORD);

		mPrefOverlayInt = (ListPreference) getPreferenceScreen()
				.findPreference(PrefsHelper.PREF_OVERLAY_INTENSITY);
		mPrefForcPX = (ListPreference) getPreferenceScreen().findPreference(
				PrefsHelper.PREF_GLOBAL_FORCE_PXASPECT);
		getListView().setBackgroundColor(Color.WHITE);
		addBanner();

	}

	protected void populateFilterList(int key1, int key2, ListPreference lp) {
		Resources res = getResources();
		int i = 0;
		int n = 0;
		CharSequence[] cs = null;
		CharSequence[] csv = null;

		n = Emulator.getValue(key1);
		if (-1 == n) {
			n = 0;
		}
		cs = new String[n + 1];
		csv = new String[n + 1];
		cs[0] = res.getString(R.string.user_current_all);
		csv[0] = "-1";
		while (i < n) {
			i++;
			cs[i] = Emulator.getValueStr(key2, i);
			csv[i] = i + "";
		}
		lp.setEntries(cs);
		lp.setEntryValues(csv);
	}

	protected void populateOverlayList(ListPreference lp) {

		CharSequence[] cs = null;
		CharSequence[] csv = null;

		String romDir = getPreferenceScreen().getSharedPreferences().getString(
				PrefsHelper.PREF_ROMsDIR, "");
		romDir += File.separator + "overlays";

		File path = new File(romDir);

		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				File sel = new File(dir, filename);
				return sel.isFile() && !sel.isHidden()
						&& filename.toLowerCase().endsWith(".png");
			}
		};

		String[] fList = null;

		if (path.exists())
			fList = path.list(filter);

		if (fList == null)
			fList = new String[0];

		int n = fList.length;

		cs = new String[n + 1];
		csv = new String[n + 1];

		cs[0] = "None";
		csv[0] = PrefsHelper.PREF_OVERLAY_NONE;

		int i = 0;
		while (i < n) {
			File f = new File(fList[i]);
			i++;
			csv[i] = f.getName();
			cs[i] = f.getName().toLowerCase().replace(".png", "");
		}
		lp.setEntries(cs);
		lp.setEntryValues(csv);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		boolean enable;
		Resources res = getResources();
		String mCurrentValue = res.getString(R.string.user_current_value);
		if (null == mCurrentValue) {
			mCurrentValue = "Current value is '";
		}

		mPrefGlobalVideoRenderMode.setSummary(mCurrentValue
				+ mPrefGlobalVideoRenderMode.getEntry() + "'");
		enable = Integer.valueOf(mPrefGlobalVideoRenderMode.getValue())
				.intValue() == PrefsHelper.PREF_RENDER_GL;
		getPreferenceScreen().findPreference(PrefsHelper.PREF_FORCE_GLES10)
				.setEnabled(enable);
		mPrefResolution.setSummary(mCurrentValue + mPrefResolution.getEntry()
				+ "'");
		mPrefSpeed.setSummary(mCurrentValue + mPrefSpeed.getEntry() + "'");
		mPrefPortraitMode.setSummary(mCurrentValue
				+ mPrefPortraitMode.getEntry() + "'");
		mPrefLandsMode.setSummary(mCurrentValue + mPrefLandsMode.getEntry()
				+ "'");
		mPrefPortraitOverlay.setSummary(mCurrentValue
				+ mPrefPortraitOverlay.getEntry() + "'");
		mPrefLandsOverlay.setSummary(mCurrentValue
				+ mPrefLandsOverlay.getEntry() + "'");
		mPrefControllerType.setSummary(mCurrentValue
				+ mPrefControllerType.getEntry() + "'");
		mPrefExtInput
				.setSummary(mCurrentValue + mPrefExtInput.getEntry() + "'");

		enable = Integer.valueOf(mPrefExtInput.getValue()).intValue() == PrefsHelper.PREF_INPUT_USB_AUTO;
		getPreferenceScreen().findPreference(PrefsHelper.PREF_AUTOMAP_OPTIONS)
				.setEnabled(enable);
		getPreferenceScreen().findPreference(
				PrefsHelper.PREF_DISABLE_RIGHT_STICK).setEnabled(enable);

		mPrefAutomap.setSummary(mCurrentValue + mPrefAutomap.getEntry() + "'");
		mPrefAnalogDZ
				.setSummary(mCurrentValue + mPrefAnalogDZ.getEntry() + "'");
		mPrefGamepadDZ.setSummary(mCurrentValue + mPrefGamepadDZ.getEntry()
				+ "'");
		mPrefTiltDZ.setSummary(mCurrentValue + mPrefTiltDZ.getEntry() + "'");
		mPrefTiltNeutral.setSummary(mCurrentValue + mPrefTiltNeutral.getEntry()
				+ "'");
		mPrefFrameSkip.setSummary(mCurrentValue + mPrefFrameSkip.getEntry()
				+ "'");
		mPrefSound.setSummary(mCurrentValue + mPrefSound.getEntry() + "'");
		mPrefStickType.setSummary(mCurrentValue + mPrefStickType.getEntry()
				+ "'");
		mPrefNumButtons.setSummary(mCurrentValue + mPrefNumButtons.getEntry()
				+ "'");
		mPrefSizeButtons.setSummary(mCurrentValue + mPrefSizeButtons.getEntry()
				+ "'");
		mPrefSizeStick.setSummary(mCurrentValue + mPrefSizeStick.getEntry()
				+ "'");
		mPrefVideoThPr.setSummary(mCurrentValue + mPrefVideoThPr.getEntry()
				+ "'");
		mPrefMainThPr
				.setSummary(mCurrentValue + mPrefMainThPr.getEntry() + "'");
		mPrefSoundEngine.setSummary(mCurrentValue + mPrefSoundEngine.getEntry()
				+ "'");
		mPrefAutofire
				.setSummary(mCurrentValue + mPrefAutofire.getEntry() + "'");
		mPrefVSync.setSummary(mCurrentValue + mPrefVSync.getEntry() + "'");
		mPrefFilterCat.setSummary(mCurrentValue + mPrefFilterCat.getEntry()
				+ "'");
		mPrefFilterDrvSrc.setSummary(mCurrentValue
				+ mPrefFilterDrvSrc.getEntry() + "'");
		mPrefFilterManuf.setSummary(mCurrentValue + mPrefFilterManuf.getEntry()
				+ "'");
		mPrefFilterYGTE.setSummary(mCurrentValue + mPrefFilterYGTE.getEntry()
				+ "'");
		mPrefFilterYLTE.setSummary(mCurrentValue + mPrefFilterYLTE.getEntry()
				+ "'");
		mPrefFilterkeyword.setSummary(mCurrentValue
				+ mPrefFilterkeyword.getText() + "'");
		mPrefOverlayInt.setSummary(mCurrentValue + mPrefOverlayInt.getEntry()
				+ "'");
		mPrefForcPX.setSummary(mCurrentValue + mPrefForcPX.getEntry() + "'");
		// Set up a listener whenever a key changes
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);

		// Unregister the listener whenever a key changes
		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// Let's do something a preference values changes
		Resources res = getResources();
		String mCurrentValue = res.getString(R.string.user_current_value);
		if (null == mCurrentValue) {
			mCurrentValue = "Current value is '";
		}
		if (key.equals(PrefsHelper.PREF_PORTRAIT_SCALING_MODE)) {
			mPrefPortraitMode.setSummary(mCurrentValue
					+ mPrefPortraitMode.getEntry() + "'");
		} else if (key.equals(PrefsHelper.PREF_LANDSCAPE_SCALING_MODE)) {
			mPrefLandsMode.setSummary(mCurrentValue + mPrefLandsMode.getEntry()
					+ "'");
		}
		if (key.equals(PrefsHelper.PREF_PORTRAIT_OVERLAY)) {
			mPrefPortraitOverlay.setSummary(mCurrentValue
					+ mPrefPortraitOverlay.getEntry() + "'");
		} else if (key.equals(PrefsHelper.PREF_LANDSCAPE_OVERLAY)) {
			mPrefLandsOverlay.setSummary(mCurrentValue
					+ mPrefLandsOverlay.getEntry() + "'");
		} else if (key.equals(PrefsHelper.PREF_CONTROLLER_TYPE)) {
			mPrefControllerType.setSummary("Current values is '"
					+ mPrefControllerType.getEntry() + "'");
		} else if (key.equals(PrefsHelper.PREF_GLOBAL_VIDEO_RENDER_MODE)) {
			mPrefGlobalVideoRenderMode.setSummary(mCurrentValue
					+ mPrefGlobalVideoRenderMode.getEntry() + "'");
			boolean enable = Integer.valueOf(
					mPrefGlobalVideoRenderMode.getValue()).intValue() == PrefsHelper.PREF_RENDER_GL;
			getPreferenceScreen().findPreference(PrefsHelper.PREF_FORCE_GLES10)
					.setEnabled(enable);
		} else if (key.equals(PrefsHelper.PREF_GLOBAL_RESOLUTION)) {
			mPrefResolution.setSummary(mCurrentValue
					+ mPrefResolution.getEntry() + "'");
		} else if (key.equals(PrefsHelper.PREF_GLOBAL_SPEED)) {
			mPrefSpeed.setSummary(mCurrentValue + mPrefSpeed.getEntry() + "'");
		} else if (key.equals(PrefsHelper.PREF_INPUT_EXTERNAL)) {
			try {
				InputHandlerExt.resetAutodetected();
			} catch (Error e) {
			}
			;
			mPrefExtInput.setSummary(mCurrentValue + mPrefExtInput.getEntry()
					+ "'");
			boolean enable = Integer.valueOf(mPrefExtInput.getValue())
					.intValue() == PrefsHelper.PREF_INPUT_USB_AUTO;
			getPreferenceScreen().findPreference(
					PrefsHelper.PREF_AUTOMAP_OPTIONS).setEnabled(enable);
			getPreferenceScreen().findPreference(
					PrefsHelper.PREF_DISABLE_RIGHT_STICK).setEnabled(enable);
		} else if (key.equals(PrefsHelper.PREF_AUTOMAP_OPTIONS)) {
			try {
				InputHandlerExt.resetAutodetected();
			} catch (Error e) {
			}
			;
			mPrefAutomap.setSummary(mCurrentValue + mPrefAutomap.getEntry()
					+ "'");
		} else if (key.equals(PrefsHelper.PREF_ANALOG_DZ)) {
			mPrefAnalogDZ.setSummary(mCurrentValue + mPrefAnalogDZ.getEntry()
					+ "'");
		} else if (key.equals(PrefsHelper.PREF_GAMEPAD_DZ)) {
			mPrefGamepadDZ.setSummary(mCurrentValue + mPrefGamepadDZ.getEntry()
					+ "'");
		} else if (key.equals(PrefsHelper.PREF_TILT_DZ)) {
			mPrefTiltDZ
					.setSummary(mCurrentValue + mPrefTiltDZ.getEntry() + "'");
		} else if (key.equals(PrefsHelper.PREF_TILT_NEUTRAL)) {
			mPrefTiltNeutral.setSummary(mCurrentValue
					+ mPrefTiltNeutral.getEntry() + "'");
		} else if (key.equals(PrefsHelper.PREF_GLOBAL_FRAMESKIP)) {
			mPrefFrameSkip.setSummary(mCurrentValue + mPrefFrameSkip.getEntry()
					+ "'");
		} else if (key.equals(PrefsHelper.PREF_GLOBAL_SOUND)) {
			mPrefSound.setSummary(mCurrentValue + mPrefSound.getEntry() + "'");
		} else if (key.equals(PrefsHelper.PREF_STICK_TYPE)) {
			mPrefStickType.setSummary(mCurrentValue + mPrefStickType.getEntry()
					+ "'");
		} else if (key.equals(PrefsHelper.PREF_NUMBUTTONS)) {
			mPrefNumButtons.setSummary(mCurrentValue
					+ mPrefNumButtons.getEntry() + "'");
		} else if (key.equals(PrefsHelper.PREF_BUTTONS_SIZE)) {
			mPrefSizeButtons.setSummary(mCurrentValue
					+ mPrefSizeButtons.getEntry() + "'");
		} else if (key.equals(PrefsHelper.PREF_STICK_SIZE)) {
			mPrefSizeStick.setSummary(mCurrentValue + mPrefSizeStick.getEntry()
					+ "'");
		} else if (key.equals(PrefsHelper.PREF_VIDEO_THREAD_PRIORITY)) {
			mPrefVideoThPr.setSummary(mCurrentValue + mPrefVideoThPr.getEntry()
					+ "'");
		} else if (key.equals(PrefsHelper.PREF_MAIN_THREAD_PRIORITY)) {
			mPrefMainThPr.setSummary(mCurrentValue + mPrefMainThPr.getEntry()
					+ "'");
		} else if (key.equals(PrefsHelper.PREF_SOUND_ENGINE)) {
			mPrefSoundEngine.setSummary(mCurrentValue
					+ mPrefSoundEngine.getEntry() + "'");
		} else if (key.equals(PrefsHelper.PREF_AUTOFIRE)) {
			mPrefAutofire.setSummary(mCurrentValue + mPrefAutofire.getEntry()
					+ "'");
		} else if (key.equals(PrefsHelper.PREF_GLOBAL_VSYNC)) {
			mPrefVSync.setSummary(mCurrentValue + mPrefVSync.getEntry() + "'");
		} else if (key.equals(PrefsHelper.PREF_FILTER_CATEGORY)) {
			mPrefFilterCat.setSummary(mCurrentValue + mPrefFilterCat.getEntry()
					+ "'");
		} else if (key.equals(PrefsHelper.PREF_FILTER_DRVSRC)) {
			mPrefFilterDrvSrc.setSummary(mCurrentValue
					+ mPrefFilterDrvSrc.getEntry() + "'");
		} else if (key.equals(PrefsHelper.PREF_FILTER_MANUF)) {
			mPrefFilterManuf.setSummary(mCurrentValue
					+ mPrefFilterManuf.getEntry() + "'");
		} else if (key.equals(PrefsHelper.PREF_FILTER_YGTE)) {
			mPrefFilterYGTE.setSummary(mCurrentValue
					+ mPrefFilterYGTE.getEntry() + "'");
		} else if (key.equals(PrefsHelper.PREF_FILTER_YLTE)) {
			mPrefFilterYLTE.setSummary(mCurrentValue
					+ mPrefFilterYLTE.getEntry() + "'");
		} else if (key.equals(PrefsHelper.PREF_FILTER_KEYWORD)) {
			mPrefFilterkeyword.setSummary(mCurrentValue
					+ mPrefFilterkeyword.getText() + "'");
		} else if (key.equals(PrefsHelper.PREF_OVERLAY_INTENSITY)) {
			mPrefOverlayInt.setSummary(mCurrentValue
					+ mPrefOverlayInt.getEntry() + "'");
			Emulator.setOverlayFilterValue(PrefsHelper.PREF_OVERLAY_NONE);// forces
																			// reload
		} else if (key.equals(PrefsHelper.PREF_GLOBAL_FORCE_PXASPECT)) {
			mPrefForcPX
					.setSummary(mCurrentValue + mPrefForcPX.getEntry() + "'");
		}
	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference pref) {
		Resources res = getResources();
		if (pref instanceof PreferenceScreen) {
			PreferenceScreen screen = (PreferenceScreen) pref;
			if (null != screen) {
				Dialog d = screen.getDialog();
				if (null != d) {
					Window w = d.getWindow();
					if (null != w) {
						w.setBackgroundDrawableResource(android.R.drawable.screen_background_light);
						w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
								WindowManager.LayoutParams.FLAG_FULLSCREEN);
						w.setTitleColor(Color.WHITE);
					}
				}
			}
		}
		if (pref.getKey().equals("defineKeys")) {
			startActivityForResult(new Intent(this, DefineKeys.class), 1);
		} else if (pref.getKey().equals("changeRomPath")) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(res.getString(R.string.user_message))
					.setCancelable(false)
					.setPositiveButton(res.getString(R.string.user_yes),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
								}
							})
					.setNegativeButton(res.getString(R.string.user_no),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			Dialog dialog = builder.create();
			dialog.show();
		} else if (pref.getKey().equals("defaultsKeys")) {

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(res.getString(R.string.user_restore_message))
					.setCancelable(false)
					.setPositiveButton(res.getString(R.string.user_yes),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									SharedPreferences.Editor editor = settings
											.edit();

									StringBuffer definedKeysStr = new StringBuffer();

									for (int i = 0; i < InputHandler.defaultKeyMapping.length; i++) {
										InputHandler.keyMapping[i] = InputHandler.defaultKeyMapping[i];
										definedKeysStr
												.append(InputHandler.defaultKeyMapping[i]
														+ ":");
									}
									editor.putString(
											PrefsHelper.PREF_DEFINED_KEYS,
											definedKeysStr.toString());
									editor.commit();
								}
							})
					.setNegativeButton(res.getString(R.string.user_no),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			Dialog dialog = builder.create();
			dialog.show();
		} else if (pref.getKey().equals("customControlLayout")) {
			ControlCustomizer.setEnabled(true);
			finish();
		} else if (pref.getKey().equals("defaultControlLayout")) {

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(res.getString(R.string.user_restore_message))
					.setCancelable(false)
					.setPositiveButton(res.getString(R.string.user_yes),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									SharedPreferences.Editor editor = settings
											.edit();
									editor.putString(
											PrefsHelper.PREF_DEFINED_CONTROL_LAYOUT,
											null);
									editor.putString(
											PrefsHelper.PREF_DEFINED_CONTROL_LAYOUT_P,
											null);
									editor.commit();
								}
							})
					.setNegativeButton(res.getString(R.string.user_no),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			Dialog dialog = builder.create();
			dialog.show();
		} else if (pref.getKey().equals("restoreFilters")) {

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(res.getString(R.string.user_restore_message))
					.setCancelable(false)
					.setPositiveButton(res.getString(R.string.user_yes),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									SharedPreferences.Editor editor = settings
											.edit();
									editor.putBoolean(
											PrefsHelper.PREF_FILTER_FAVORITES,
											false);
									editor.putBoolean(
											PrefsHelper.PREF_FILTER_CLONES,
											false);
									editor.putBoolean(
											PrefsHelper.PREF_FILTER_NOTWORKING,
											false);
									editor.putString(
											PrefsHelper.PREF_FILTER_YGTE, "-1");
									editor.putString(
											PrefsHelper.PREF_FILTER_YLTE, "-1");
									editor.putString(
											PrefsHelper.PREF_FILTER_MANUF, "-1");
									editor.putString(
											PrefsHelper.PREF_FILTER_CATEGORY,
											"-1");
									editor.putString(
											PrefsHelper.PREF_FILTER_DRVSRC,
											"-1");
									editor.putString(
											PrefsHelper.PREF_FILTER_KEYWORD, "");
									editor.commit();
									finish();
									startActivity(getIntent());
								}
							})
					.setNegativeButton(res.getString(R.string.user_no),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			Dialog dialog = builder.create();
			dialog.show();
		}

		return super.onPreferenceTreeClick(preferenceScreen, pref);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK && requestCode == 0) {
			setResult(RESULT_OK, data);
		} else if (requestCode == 1) {
			SharedPreferences.Editor editor = settings.edit();

			StringBuffer definedKeysStr = new StringBuffer();

			for (int i = 0; i < InputHandler.keyMapping.length; i++)
				definedKeysStr.append(InputHandler.keyMapping[i] + ":");

			editor.putString(PrefsHelper.PREF_DEFINED_KEYS,
					definedKeysStr.toString());
			editor.commit();
			return;
		}
		finish();
	}
}
