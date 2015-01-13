package com.yunluo.android.arcadehub.utils;

import android.util.Log;

public class Debug {
	
	private static boolean DEBUG = false;
	
	public static void d(String target, Object info) {
		if(DEBUG) {
			Log.d("ArcadeHub-Debug", target+": " + info);
		}
	}
	
	public static void e(String target, Object info) {
		if(DEBUG) {
			Log.e("ArcadeHub-Debug", target+": " + info);
		}
	}
	public static void i(String target, Object info) {
		if(DEBUG) {
			Log.i("ArcadeHub-Debug", target+": " + info);
		}
	}
}
