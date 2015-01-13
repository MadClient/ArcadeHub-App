package com.yunluo.android.arcadehub.utils;

import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePreferenceUtil {
	public static String SAVE_GAMES_INFO = "savegamesinfo";

	public static String RUN_ROM_NAME = "rom_name";

	public static String RUN_ROM_DESC = "rom_desc";

	public static String RUN_ROM_PATH = "rom_path";

	public static String BLUETOOTH_STATE = "bluetooth_state";
	
	private static final String LOCAL_LNG = "Local_lng";
	
	private static final String HASH_CODE = "hash_code";
	
	private static final String UPDATE_TIME = "update_time";
	
	private static final String REFRESH_TIME = "refresh_time";
	
	private static final String CHECK_SUM = "check_sum";
	
	private static final String DOWNLOAD_STATE = "download_state";
	
	private static final String GAME_RESOURCE = "game_resource";

	public static void saveName(Context context, String name) {
		SharedPreferences preference = context.getSharedPreferences(
				SAVE_GAMES_INFO, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preference.edit();
		editor.putString(RUN_ROM_NAME, name);
		editor.commit();
	}

	public static String loadName(Context context) {
		SharedPreferences preference = context.getSharedPreferences(
				SAVE_GAMES_INFO, Context.MODE_PRIVATE);
		String name = preference.getString(RUN_ROM_NAME, null);
		return name;
	}

	public static void saveDesc(Context context, String name) {
		SharedPreferences preference = context.getSharedPreferences(
				SAVE_GAMES_INFO, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preference.edit();
		editor.putString(RUN_ROM_DESC, name);
		editor.commit();
	}

	public static String loadDesc(Context context) {
		SharedPreferences preference = context.getSharedPreferences(
				SAVE_GAMES_INFO, Context.MODE_PRIVATE);
		String name = preference.getString(RUN_ROM_DESC, null);
		return name;

	}

	public static void savePath(Context context, String path) {
		SharedPreferences preference = context.getSharedPreferences(
				SAVE_GAMES_INFO, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preference.edit();
		editor.putString(RUN_ROM_PATH, path);
		editor.commit();
	}

	public static String loadPath(Context context) {
		SharedPreferences preference = context.getSharedPreferences(
				SAVE_GAMES_INFO, Context.MODE_PRIVATE);
		String path = preference.getString(RUN_ROM_PATH, null);
		return path;
	}

	public static void saveBtState(Context context, boolean boo) {
		SharedPreferences preference = context.getSharedPreferences(
				SAVE_GAMES_INFO, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preference.edit();
		editor.putBoolean(BLUETOOTH_STATE, boo);
		editor.commit();
	}

	public static boolean loadBtState(Context context) {
		SharedPreferences preference = context.getSharedPreferences(
				SAVE_GAMES_INFO, Context.MODE_PRIVATE);
		return preference.getBoolean(BLUETOOTH_STATE, false);
	}
	
	public static void saveLng(Context context) {
		String language = Locale.getDefault().getLanguage().substring(0, 2);
		SharedPreferences preference = context.getSharedPreferences(
				SAVE_GAMES_INFO, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preference.edit();
		editor.putString(LOCAL_LNG, language);
		editor.commit();
	}

	public static String loadLng(Context context) {
		SharedPreferences preference = context.getSharedPreferences(
				SAVE_GAMES_INFO, Context.MODE_PRIVATE);
		return preference.getString(LOCAL_LNG, "en");
	}
	
	public static void saveHashcode(Context context, int hashcode) {
		SharedPreferences preference = context.getSharedPreferences(
				SAVE_GAMES_INFO, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preference.edit();
		editor.putInt(HASH_CODE, hashcode);
		editor.commit();
	}

	public static int loadHashcode(Context context) {
		SharedPreferences preference = context.getSharedPreferences(
				SAVE_GAMES_INFO, Context.MODE_PRIVATE);
		return preference.getInt(HASH_CODE, -1);
	}
	
	//save push time
	public static void saveTime(Context context, long time) {
		SharedPreferences preference = context.getSharedPreferences(
				SAVE_GAMES_INFO, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preference.edit();
		editor.putLong(UPDATE_TIME, time);
		editor.commit();
	}
	
	//load push time
	public static long loadTime(Context context) {
		SharedPreferences preference = context.getSharedPreferences(
				SAVE_GAMES_INFO, Context.MODE_PRIVATE);
		return preference.getLong(UPDATE_TIME, 0);
	}
	
	// save checksum
	public static void saveCheckSum(Context context, String cs) {
		SharedPreferences preference = context.getSharedPreferences(
				SAVE_GAMES_INFO, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preference.edit();
		editor.putString(CHECK_SUM, cs);
		editor.commit();
	}
	
	// save checksum
	public static String loadCheckSum(Context context) {
		SharedPreferences preference = context.getSharedPreferences(
				SAVE_GAMES_INFO, Context.MODE_PRIVATE);
		return preference.getString(CHECK_SUM, null);
	}
	
	// save checksum
	public static void saveDownloadState(Context context, String ds) {
		SharedPreferences preference = context.getSharedPreferences(
				SAVE_GAMES_INFO, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preference.edit();
		editor.putString(DOWNLOAD_STATE, ds);
		editor.commit();
	}
	
	// save checksum
	public static String loadDownloadState(Context context) {
		SharedPreferences preference = context.getSharedPreferences(
				SAVE_GAMES_INFO, Context.MODE_PRIVATE);
		return preference.getString(DOWNLOAD_STATE, null);
	}

	public static void saveKeysOne(Context context, String name, String target) {
		SharedPreferences preference = context.getSharedPreferences(
				SAVE_GAMES_INFO, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preference.edit();
		editor.putString(name+"_1", target);
		editor.commit();
	}
	
	public static void saveKeysTwo(Context context, String name, String target) {
		SharedPreferences preference = context.getSharedPreferences(
				SAVE_GAMES_INFO, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preference.edit();
		editor.putString(name+"_2", target);
		editor.commit();
	}
	
	public static void saveKeysThree(Context context, String name, String target) {
		SharedPreferences preference = context.getSharedPreferences(
				SAVE_GAMES_INFO, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preference.edit();
		editor.putString(name+"_3", target);
		editor.commit();
	}
	
	public static void saveKeysFour(Context context, String name, String target) {
		SharedPreferences preference = context.getSharedPreferences(
				SAVE_GAMES_INFO, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preference.edit();
		editor.putString(name+"_4", target);
		editor.commit();
	}
	
	public static String loadKeysOne(Context context, String name) {
		SharedPreferences preference = context.getSharedPreferences(
				SAVE_GAMES_INFO, Context.MODE_PRIVATE);
		return preference.getString(name+"_1", null);
	}
	
	public static String loadKeysTwo(Context context, String name) {
		SharedPreferences preference = context.getSharedPreferences(
				SAVE_GAMES_INFO, Context.MODE_PRIVATE);
		return preference.getString(name+"_2", null);
	}
	
	public static String loadKeysThree(Context context, String name) {
		SharedPreferences preference = context.getSharedPreferences(
				SAVE_GAMES_INFO, Context.MODE_PRIVATE);
		return preference.getString(name+"_3", null);
	}
	
	public static String loadKeysFour(Context context, String name) {
		SharedPreferences preference = context.getSharedPreferences(
				SAVE_GAMES_INFO, Context.MODE_PRIVATE);
		return preference.getString(name+"_4", null);
	}
	
	//save refresh time
    public static void saveRefreshTime(Context context, String time) {
        SharedPreferences preference = context.getSharedPreferences(
                SAVE_GAMES_INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preference.edit();
        editor.putString(REFRESH_TIME, time);
        editor.commit();
    }
    
    //load refresh time
    public static String loadRefreshTime(Context context) {
        SharedPreferences preference = context.getSharedPreferences(
                SAVE_GAMES_INFO, Context.MODE_PRIVATE);
        return preference.getString(REFRESH_TIME, null);
    }

    public static int loadGameResource(Context context) {
        SharedPreferences preference = context.getSharedPreferences(SAVE_GAMES_INFO, Context.MODE_PRIVATE);
        return preference.getInt(GAME_RESOURCE, 0);
    }
    
    public static void saveGameResource(Context context, int state) {
        SharedPreferences preference = context.getSharedPreferences(SAVE_GAMES_INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preference.edit();
        editor.putInt(GAME_RESOURCE, state);
        editor.commit();
    }
}
