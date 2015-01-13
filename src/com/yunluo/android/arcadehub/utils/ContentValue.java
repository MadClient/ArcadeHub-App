package com.yunluo.android.arcadehub.utils;

import java.io.File;

import android.os.Environment;

public interface ContentValue {
	
    public final static String PATH = "path";
    
    public final static String NAME = "name";
    
    public final static String STA = "sta";
    
    //down load action
    public static final String DOWNLOAD_ACTION = "com.yunluo.android.arcadehub.download.broadcast";
    
    public static final String GAMES_PATH = "ArcadeHub"+File.separator;
    
    public static String STA_PATH = FileUtil.getDefaultROMsDIR()+STA+File.separator;
	
    public static String ROOT_PAHT = Environment.getExternalStorageDirectory().getAbsolutePath();
    
    public static String TRANSFER_PATH = "transfer";
    
    public static String DOWNLOAD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "test"+File.separator+"roms";
    
    public static String SDCADE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;

    public static String BMP_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "ArcadeHub"+File.separator+"bmp"+File.separator;

    public static String CHANNAL_ID = "googleplay";
    
}
