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
import java.nio.ByteBuffer;
import java.util.List;
import com.yunluo.android.arcadehub.async.RomInfo;
import com.yunluo.android.arcadehub.helpers.DialogHelper;
import com.yunluo.android.arcadehub.helpers.PrefsHelper;
import com.yunluo.android.arcadehub.input.InputHandler;
import com.yunluo.android.arcadehub.interfac.OnAddCheatListener;
import com.yunluo.android.arcadehub.interfac.OnAddRomListener;
import com.yunluo.android.arcadehub.interfac.OnNetPlayListener;
import com.yunluo.android.arcadehub.netplay.gamekit.GameKit;
import com.yunluo.android.arcadehub.netplay.skt.GameWifiSkt;
import com.yunluo.android.arcadehub.utils.Debug;
import com.yunluo.android.arcadehub.utils.FileUtil;
import com.yunluo.android.arcadehub.utils.SharePreferenceUtil;
import com.yunluo.android.arcadehub.utils.Utils;
import com.yunluo.android.arcadehub.views.CustomProgressDialog;
import com.yunluo.android.arcadehub.views.EmulatorViewGL;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Paint.Style;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Toast;

public class Emulator 
{
    final static public int FPS_SHOWED_KEY = 1;
    final static public int EXIT_GAME_KEY = 2;  
    final static public int INFOWARN_KEY = 8;
    final static public int EXIT_PAUSE = 9;
    final static public int IDLE_WAIT = 10;
    final static public int PAUSE = 11;
    final static public int FRAME_SKIP_VALUE = 12;
    final static public int SOUND_VALUE = 13;

    final static public int THROTTLE = 14;
    final static public int CHEAT = 15;
    final static public int AUTOSAVE = 16;
    final static public int SAVESTATE = 17;
    final static public int LOADSTATE = 18;
    final static public int IN_MENU = 19;
    final static public int EMU_RESOLUTION = 20;
    final static public int FORCE_PXASPECT = 21;    
    final static public int THREADED_VIDEO = 22;
    final static public int DOUBLE_BUFFER = 23;
    final static public int PXASP1 = 24;
    final static public int NUMBTNS = 25;
    final static public int NUMWAYS = 26;
    final static public int FILTER_FAVORITES = 27;
    final static public int RESET_FILTER = 28;  
    final static public int LAST_GAME_SELECTED = 29;    
    final static public int EMU_SPEED = 30; 
    final static public int AUTOFIRE = 31;
    final static public int VSYNC = 32;
    final static public int HISCORE = 33;
    final static public int VBEAN2X = 34;
    final static public int VANTIALIAS = 35;
    final static public int VFLICKER = 36;
    final static public int FILTER_NUM_YEARS = 37;
    final static public int FILTER_NUM_MANUFACTURERS = 38;
    final static public int FILTER_NUM_DRIVERS_SRC = 39;
    final static public int FILTER_NUM_CATEGORIES = 40; 
    final static public int FILTER_CLONES = 41; 
    final static public int FILTER_NOTWORKING = 42; 
    final static public int FILTER_MANUFACTURER = 43;   
    final static public int FILTER_GTE_YEAR = 44;   
    final static public int FILTER_LTE_YEAR = 45;   
    final static public int FILTER_DRVSRC = 46;     
    final static public int FILTER_CATEGORY = 47;   
    final static public int SOUND_DEVICE_FRAMES = 48;       
    final static public int SOUND_DEVICE_SR = 49;
    final static public int SOUND_ENGINE = 50;      

    final static public int EXIT_GAME = 51;

    final static public int FILTER_YEARS_ARRAY = 0;
    final static public int FILTER_MANUFACTURERS_ARRAY = 1;
    final static public int FILTER_DRIVERS_SRC_ARRAY = 2;
    final static public int FILTER_CATEGORIES_ARRAY = 3;
    final static public int FILTER_KEYWORD = 4;

    private static GamePlayActivity mGamePlay = null;

    private static GameLogoActivity mGameLogo = null;

    private static GameListActivity mGameList = null;

    private static boolean isEmulating = false;

    public static boolean isDisplay = false;

    private static ProgressDialog mPauseDialog;

    private static CustomProgressDialog mLoadingDialog;

    private final static int TYPE_CONNECTED_REQUST = 1;
    private final static int TYPE_CONNECTED_REFUTE = 2;
    private final static int TYPE_TRANFER_REQUEST = 3;
    private final static int TYPE_TRANFER_AGREE = 4;
    private final static int TYPE_TRANFER_REFUTE = 5;
    private final static int TYPE_TRANFER_FINISH = 6;
    private final static int TYPE_START_WIFI_GAME = 7;
    private final static int TYPE_START_BT_GAME = 8;

    public static boolean isEmulating() {
        return isEmulating;
    }

    private static Object lock1 = new Object();

    private static SurfaceHolder holder = null;
    private static Bitmap emuBitmap = Bitmap.createBitmap(320, 240, Bitmap.Config.RGB_565);
    private static ByteBuffer screenBuff = null;

    private static int []screenBuffPx = new int[640*480*3]; 
    public  static int[] getScreenBuffPx() {
        return screenBuffPx;
    }

    private static boolean frameFiltering = false;  
    public static boolean isFrameFiltering() {
        return frameFiltering;
    }

    private static Paint emuPaint = null;
    private static Paint debugPaint = new Paint();

    private static Matrix mtx = new Matrix();

    private static int window_width = 320;
    public static int getWindow_width() {
        return window_width;
    }

    private static int window_height = 240;
    public static int getWindow_height() {
        return window_height;
    }

    private static int emu_width = 320;
    private static int emu_height = 240;
    private static int emu_vis_width = 320;
    private static int emu_vis_height = 240;

    private static AudioTrack audioTrack = null;

    private static boolean isThreadedSound  = false;
    private static boolean isDebug = false;
    private static int videoRenderMode  =  PrefsHelper.PREF_RENDER_SW;
    private static boolean inMAME = false;
    private static boolean inMenu = false;
    private static boolean oldInMenu = false;
    public static boolean isInMAME() {
        return inMAME;
    }
    public static boolean isInMenu() {
        return inMenu;
    }   
    private static String overlayFilterValue  =  PrefsHelper.PREF_OVERLAY_NONE;

    public static String getOverlayFilterValue() {
        return overlayFilterValue;
    }

    public static void setOverlayFilterValue(String value) {
        Emulator.overlayFilterValue = value;
    }

    private static boolean needsRestart = false;

    public static void setNeedRestart(boolean value){
        needsRestart = value;
    }

    public static boolean isRestartNeeded(){
        return needsRestart;
    }

    private static boolean warnResChanged = false;

    public static boolean isWarnResChanged() {
        return warnResChanged;
    }

    public static void setWarnResChanged(boolean warnResChanged) {
        Emulator.warnResChanged = warnResChanged;
    }

    private static boolean paused = true;

    public static boolean isPaused() {
        return paused;
    }

    private static boolean portraitFull = false;

    public static boolean isPortraitFull() {
        return portraitFull;
    }

    static long j = 0;
    static int i = 0;
    static int fps = 0;
    static long millis;

    private static SoundThread soundT = new SoundThread();
    private static Thread nativeVideoT = null;

    static
    {
        try
        {       
            System.loadLibrary("arcadehub-jni");         
        }
        catch(java.lang.Error e)
        {  
        }

        debugPaint.setARGB(255, 255, 255, 255);
        debugPaint.setStyle(Style.STROKE);      
        debugPaint.setTextSize(16);
    }

    public static int getEmulatedWidth() {
        return emu_width;
    }

    public static int getEmulatedHeight() {
        return emu_height;
    }

    public static int getEmulatedVisWidth() {
        return emu_vis_width;
    }

    public static int getEmulatedVisHeight() {
        return emu_vis_height;
    }   

    public static boolean isThreadedSound() {
        return isThreadedSound;
    }

    public static void setThreadedSound(boolean isThreadedSound) {
        Emulator.isThreadedSound = isThreadedSound;
    }

    public static boolean isDebug() {
        return isDebug;
    }

    public static void setDebug(boolean isDebug) {
        Emulator.isDebug = isDebug;
    }

    public static int getVideoRenderMode() {
        return Emulator.videoRenderMode;
    }

    public static void setVideoRenderMode(int videoRenderMode) {
        Emulator.videoRenderMode = videoRenderMode;
    }

    public static Paint getEmuPaint() {
        return emuPaint;
    }

    public static Paint getDebugPaint() {
        return debugPaint;
    }

    public static Matrix getMatrix() {
        return mtx;
    }

    //synchronized
    public static SurfaceHolder getHolder(){
        return holder;
    }

    //synchronized 
    public static Bitmap getEmuBitmap(){
        return emuBitmap;
    }

    //synchronized 
    public static ByteBuffer getScreenBuffer(){
        return screenBuff;
    }


    public static void setHolder(SurfaceHolder value) {

        synchronized(lock1)
        {
            if(value!=null)
            {
                holder = value;
                try{
                    holder.setFormat(PixelFormat.OPAQUE);
                } catch(Exception e) {}
                holder.setKeepScreenOn(true);
            }
            else
            {
                holder=null;
            }
        }       
    }

    public static void setMAME4droid(GamePlayActivity mm) {
        Emulator.mGamePlay = mm;    
    }

    public static void setLogoActivity(GameLogoActivity logo) {
        Emulator.mGameLogo = logo;
    }

    public static void setArcadeHub(GameListActivity arcadeHub) {
        Emulator.mGameList = arcadeHub;
    }

    //VIDEO
    public static void setWindowSize(int w, int h) {

        window_width = w;
        window_height = h;

        if(videoRenderMode == PrefsHelper.PREF_RENDER_GL)
            return;             

        mtx.setScale((float)(window_width / (float)emu_width), (float)(window_height / (float)emu_height));
    }

    public static void setFrameFiltering(boolean value) {
        frameFiltering = value;         
        if(value)
        {
            emuPaint = new Paint();
            emuPaint.setFilterBitmap(true);
        }
        else
        {
            emuPaint = null;
        }
    }

    //synchronized 
    static void bitblt(ByteBuffer sScreenBuff, boolean inMAME) {
        synchronized(lock1){               
            screenBuff = sScreenBuff;
            Emulator.inMAME = inMAME;
            Emulator.inMenu = Emulator.getValue(Emulator.IN_MENU)==1;

            if (Emulator.inMAME && !isDisplay) {
                isDisplay = true;
                Message message = Message.obtain();
                message.arg1 = GamePlayActivity.MSG_TO_DISPLAYGAMES;
                mGamePlay.getHandler().sendMessageDelayed(message, 1000);
            }

            if(inMenu != oldInMenu)
            {
                final View v = mGamePlay.getInputView();
                if(v!=null)
                {
                    mGamePlay.runOnUiThread(new Runnable() {
                        public void run() {

                            if(!inMenu && Emulator.inMAME && 
                                    ( (mGamePlay.getPrefsHelper().isLightgun() && mGamePlay.getInputHandler().getInputHandlerState() != InputHandler.STATE_SHOWING_NONE) || mGamePlay.getPrefsHelper().isTiltSensor()))
                            {
                                CharSequence text = mGamePlay.getPrefsHelper().isTiltSensor() ? "Tilt sensor is enabled!" : "Touch lightgun is enabled!";
                                int duration = Toast.LENGTH_SHORT;
                                Toast toast = Toast.makeText(mGamePlay, text, duration);
                                toast.show();
                            }
                            v.invalidate();

                            if(mGamePlay.getFilterView()!=null)
                            {
                                mGamePlay.getFilterView().setVisibility(Emulator.isInMAME() ? View.VISIBLE : View.INVISIBLE);
                            }                            
                        }
                    });
                }       
            }
            oldInMenu = inMenu;

            if(videoRenderMode == PrefsHelper.PREF_RENDER_GL){
                ((EmulatorViewGL)mGamePlay.getEmuView()).requestRender();
            }
            else
            {                               
                if (holder==null)
                    return;

                Canvas canvas = holder.lockCanvas();        
                sScreenBuff.rewind();           
                emuBitmap.copyPixelsFromBuffer(sScreenBuff);                                                
                i++;
                canvas.concat(mtx);         
                canvas.drawBitmap(emuBitmap, 0, 0, emuPaint);
                if(isDebug)
                {   
                    canvas.drawText("Normal fps:"+fps+ " "+inMAME, 5,  40, debugPaint);
                    if(System.currentTimeMillis() - millis >= 1000) {fps = i; i=0;millis = System.currentTimeMillis();}
                }
                holder.unlockCanvasAndPost(canvas);             
            }
        }

    }

    //synchronized 
    static public void changeVideo(int newWidth, int newHeight, int newVisWidth, int newVisHeight){ 
        synchronized(lock1){

            mGamePlay.getInputHandler().resetInput();

            warnResChanged = emu_width!=newWidth || emu_height!=newHeight || emu_vis_width != newVisWidth || emu_vis_height != newVisHeight;

            emu_width = newWidth;
            emu_height = newHeight;
            emu_vis_width = newVisWidth;
            emu_vis_height = newVisHeight;

            emuBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.RGB_565);
            mtx.setScale((float)(window_width / (float)emu_width), (float)(window_height / (float)emu_height));             

            if(videoRenderMode == PrefsHelper.PREF_RENDER_GL)
            {
                GLRenderer r = (GLRenderer)((EmulatorViewGL)mGamePlay.getEmuView()).getRender();                
                if(r!=null)r.changedEmulatedSize(); 
            }

            mGamePlay.getMainHelper().updateEmuValues();

            mGamePlay.runOnUiThread(new Runnable() {
                public void run() {
                    if(warnResChanged && videoRenderMode == PrefsHelper.PREF_RENDER_GL)
                        mGamePlay.getEmuView().setVisibility(View.INVISIBLE);
                    mGamePlay.getMainHelper().updateMAME4droid();
                    if(mGamePlay.getEmuView().getVisibility()!=View.VISIBLE)
                        mGamePlay.getEmuView().setVisibility(View.VISIBLE);
                }
            });      
        }

        if(nativeVideoT==null)
        {
            nativeVideoT = new Thread(new Runnable(){
                public void run() {

                    Emulator.setValue(Emulator.THREADED_VIDEO,mGamePlay.getPrefsHelper().isThreadedVideo() ? 1 : 0 );

                    if( mGamePlay.getPrefsHelper().isThreadedVideo())                    
                        runVideoT();                    
                }           
            },"emulatorNativeVideo-Thread");

            if(mGamePlay.getPrefsHelper().getVideoThreadPriority()==PrefsHelper.LOW)
            {   
                nativeVideoT.setPriority(Thread.MIN_PRIORITY);
            }   
            else if(mGamePlay.getPrefsHelper().getVideoThreadPriority()==PrefsHelper.NORMAL)
            {
                nativeVideoT.setPriority(Thread.NORM_PRIORITY);
            }   
            else
                nativeVideoT.setPriority(Thread.MAX_PRIORITY);
            nativeVideoT.start();
        }
    }

    //SOUND
    static public void initAudio(int freq, boolean stereo)  
    {       
        int sampleFreq = freq;

        int channelConfig = stereo ? AudioFormat.CHANNEL_CONFIGURATION_STEREO : AudioFormat.CHANNEL_CONFIGURATION_MONO;
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;

        int bufferSize = AudioTrack.getMinBufferSize(sampleFreq, channelConfig, audioFormat);

        if (mGamePlay.getPrefsHelper().getSoundEngine()==PrefsHelper.PREF_SNDENG_AUDIOTRACK_HIGH)
            bufferSize *= 2;

        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleFreq,
                channelConfig,
                audioFormat,
                bufferSize,
                AudioTrack.MODE_STREAM);

        audioTrack.play();              
    }

    public static void endAudio(){
        audioTrack.stop();
        audioTrack.release();   
        audioTrack = null;
    }

    public static void writeAudio(byte[] b, int sz)
    {
        if(audioTrack!=null)
        {

            if(isThreadedSound && soundT!=null)
            {
                soundT.setAudioTrack(audioTrack);
                soundT.writeSample(b, sz);
            }
            else
            {
                audioTrack.write(b, 0, sz);
            }  
        }   
    }   


    //LIVE CYCLE
    public static void pause(){

        if(isEmulating)
        {           
            Emulator.setValue(Emulator.PAUSE, 1);
            paused = true;
        }   


        try{
        	if(audioTrack!=null)
        		audioTrack.pause();
        }catch(Exception e){}

        try {
            Thread.sleep(60);//ensure threads stop
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static void resume(){

        if(isRestartNeeded())
            return;

        if(audioTrack!=null){
            try {
                audioTrack.play();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }

        if(isEmulating)
        {               
            Emulator.setValue(Emulator.PAUSE, 0);
            Emulator.setValue(Emulator.EXIT_PAUSE, 1);  
            paused = false;
        }    
    }

    //EMULATOR
    public static void emulate(final String rootPath, final String libPath, final String resPath){

        if (isEmulating)return;

        Thread t = new Thread(new Runnable(){
            public void run() {
                isEmulating = true;
                init(rootPath, libPath, resPath);
            }           
        },"emulatorNativeMain-Thread");

        if(getMainThreadPriority() == PrefsHelper.LOW)
        {   
            t.setPriority(Thread.MIN_PRIORITY);
        }   
        else if(getMainThreadPriority() == PrefsHelper.NORMAL)
        {
            t.setPriority(Thread.NORM_PRIORITY);
        }   
        else
            t.setPriority(Thread.MAX_PRIORITY);

        t.start();      

    }
    
    protected static int getMainThreadPriority() {
    	return Integer.valueOf(getSharedPreferences().getString("PREF_MAIN_THREAD_PRIORITY","2")).intValue();
    }
    
	protected static SharedPreferences getSharedPreferences(){
		Context context = mGameLogo.getApplicationContext();
		return PreferenceManager.getDefaultSharedPreferences(context);
	}

    public static int getValue(int key){
        return getValue(key,0);
    }

    public static String getValueStr(int key){
        return getValueStr(key,0);
    }

    public static void setValue(int key, int value){
        setValue(key,0,value);
    }

    public static void setValueStr(int key, String value){
        setValueStr(key,0,value);
    }


    // native
    public static native void initGames();

    protected static native void runVideoT();

    synchronized public static native void setPadData(int i, long data);

    synchronized public static native void setAnalogData(int i, float v1, float v2);

    public static native int getValue(int key, int i);

    public static native String getValueStr(int key, int i);

    public static native void setValue(int key, int i, int value);

    public static native void setValueStr(int key, int i, String value);

    // Gamekit begin
    public static void gamekitSendData(byte[] b, int sz) {
        GameKit.send(b, sz);
    }

    public static native void gamekitReceivedData(byte[] data);

    public static native void gamekitAction(int action);

    public static native int gamekitQueryState(int query);

    public static native String gamekitGetGameName(boolean isServer);

    // Gamekit end

    public static void exitGames() {
        Emulator.resume();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Emulator.setValue(Emulator.EXIT_GAME_KEY, 1);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Emulator.setValue(Emulator.EXIT_GAME_KEY, 0);
        if(null != mNetPlayListener) {
        	mNetPlayListener.onFinish();
        }
        Emulator.isDisplay = false;
        Emulator.closeGame();
    }

    public static void startGames(String name) {

        Emulator.showLoading(); 

        List<RomInfo> mList = mGameList.getRomList();
        String path = null;
        boolean isHaved = false;
        for(RomInfo rom : mList) {
            if(name.equals(rom.getName())) {
                path = rom.getPath();
                SharePreferenceUtil.saveName(mGameList, rom.getName());
                SharePreferenceUtil.saveDesc(mGameList, rom.getDesc());
                SharePreferenceUtil.savePath(mGameList, rom.getPath());
                isHaved = true;
                break;
            }
        }

        if(false == isHaved) {
            dimissLoading();
            Toast.makeText(mGameList, "The game does not exist.", Toast.LENGTH_LONG).show();
            return;
        }

        if(false == FileUtil.exists(path, name)) {
            dimissLoading();
            Toast.makeText(mGameList, "this the rom is not found.", Toast.LENGTH_LONG).show();
            return;
        }

        mGameList.showGamesPlay(false);

        startGames();
    }

    public static void startGames() {
        String path = SharePreferenceUtil.loadPath(mGameList);
        String name = SharePreferenceUtil.loadName(mGameList);

        if(false == FileUtil.exists(path, name)) {
            Toast.makeText(mGameList, "this the rom is not found.", Toast.LENGTH_LONG).show();
            return;
        }

        Debug.d("path+/+name.zip", path+File.separator+name+".zip");
        Emulator.setStartGame(path+File.separator+name+".zip", name);
    }
    
    private static void dismiss() {
        if (null != mPauseDialog) {
            if (mPauseDialog.isShowing()) {
            	if(null != mGamePlay && false == mGamePlay.isFinishing()) {
            		mPauseDialog.dismiss();
            		mPauseDialog = null;
            	}
            }
        }
    }

    public static void sktNetplayHangup() {
        Debug.d("--> sktNetplay Hangup", "");
        mGamePlay.getHandler().post(new Runnable() {

            @Override
            public void run() {
                mGamePlay.showDialog(DialogHelper.DIALOG_GAMES_HANGUP);
            }
        });
    }

    public static void showLoading() {
        if (null == mLoadingDialog) {
        	mLoadingDialog = new CustomProgressDialog(mGameList);
        	mLoadingDialog.setMessage(mGameList.getResources().getString(R.string.MSG_REFRESH_LOADING));
        	mLoadingDialog.setIndeterminate(true);
        	mLoadingDialog.setCancelable(false);
        	mLoadingDialog.show();
        }
    }

    public static void dimissLoading() {
        if(null == mLoadingDialog) {
            return;
        }
        Debug.d("dismissLoading", mLoadingDialog);
        if(true == mLoadingDialog.isShowing()) {
        	if(null != mGameList && false == mGameList.isFinishing()) {
        		Debug.d("dismissLoading isShowing() ", mLoadingDialog.isShowing());
        		mLoadingDialog.dismiss();
        		mLoadingDialog = null;
        	}
        }
    }

    public static String getGameDesc(String name) {
    	int type = (Utils.isChinese() == true ? 0 : 1);
    	return getGame(name, type);
    }
    
    public static native String getGame(String name, int type);
    public static native void test();
    //transfer games pack
    public static native void hintTransferSendRomCB(String path);
    public static native void setStartGame(String rom_path, String rom_name); 
    public static native void sendMessageToSocket(int state); 
    public static native void broadcastDeviceInformation(int os, String ip, int cpu, int ram, String rom);
    // StopBroadcast
    public static native void stopBroadcast();
    // wifi ip address
    public static native void setDeviceIP(String ip);
    public static native void setWIFIGame(String name);
    public static native void setBluetoochGame(String name);
    public static native void startScan(String path);
    public static native void stopScan();

    public static void scanFinish() {
    	if(null != mGameList) {
    		Handler handler = mGameList.getHandler();
    		Message msg = new Message();
    		Bundle bun = new Bundle();
    		msg.arg1 = GameListActivity.SCAN_FINISH;
    		msg.setData(bun);
    		if(null != handler) {
    			handler.sendMessage(msg);
    		}
    	}
    }

    protected static native void init(String rootPath, String libPath, String resPath);

    public static native void pauseGame();

    public static native void resumeGame();

    public static native void closeGame();

    public static native void resetGame();

    public static native void setMultiKey(int pressedBtn, int[] multiKeyArray, int length);

    public static native void restMultiKey();

    public static native void setMultiPress(int pressedBtn, int[] multiPressArray, int length);

    public static native void restMultiPress(int pressedBtn);

    public static native void saveGame(String name);

    public static native void loadGame(String name);

    public static native String getArchivePath();

    public static native String enableCheat();

    public static native void openCheatItem(int index);

    public static native void closeCheatItem(int index);

    public static native void updateHighScore();
    
    public static native void getVaildBtn(int inputKeyArray[], int length);

    public static void onHighScore(int highscore) {
    }

    public static void onInsertGameInfomation(final String name, final String desc, final String path, final String size, final String year, final String cname, final String filepath) {
        if(null != mRomListener) {
            mRomListener.onAddRom(name, desc, path, size, year, cname, filepath);
        }
    }
    
    public static void gameLoaded() {
        Message message = Message.obtain();
        message.arg1 = GamePlayActivity.MSG_TO_DISPLAYCONTROL;
        mGamePlay.getHandler().sendMessageDelayed(message, 500);
    }

    public static void gameStoped() {
        if(null == mGamePlay) {
            return;
        }
        mGamePlay.getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (null == mPauseDialog) {
                    mPauseDialog = ProgressDialog.show(mGamePlay, mGamePlay.getResources().getString(R.string.emulator_dialog_pause_title), mGamePlay.getResources().getString(R.string.emulator_dialog_pause_msg), true, true);
                    mPauseDialog.setCancelable(false);
                }
            }

        });
    }

    public static void gameResumed() {
        if(null == mGamePlay) {
            return;
        }
        Debug.d("--> gameResumed()", "");
        mGamePlay.getHandler().post(new Runnable() {

            @Override
            public void run() {
                dismiss();
            }
        });
    }

    public static void onCheatList(int index, String text) {
        if ("---".equals(text)) {
            return;
        }

        if(null != mCheatListener) {
            mCheatListener.onAddCheat(index, text);
        }
    }

    public static void onNetPlayInformation(final int os, final String ip, final int cpu, final int ram, final String rom) {

        if(null == mGameList) {
            return;
        }
        mGameList.getHandler().post(new Runnable() {

            @Override
            public void run() {
            	if(null == mNetPlayListener) {
    				return;
    			}
            	mNetPlayListener.onAddNetPlayInformation(os, ip, cpu, ram, rom);
            }
        });
    }

    public static void onNetPlayStatus(final int type, final String info) {
    	
    	if(null == mGameList) {
    		return;
    	}

    	mGameList.getHandler().post(new Runnable() {

    		@Override
    		public void run() {
    			GameWifiSkt.dismissServerDialog();
    			if(null == mNetPlayListener) {
    				return;
    			}
    			switch(type) {
    			case TYPE_CONNECTED_REQUST:
    				mNetPlayListener.onRequestConnected(info);
    				break;
    			case TYPE_CONNECTED_REFUTE:
    				mNetPlayListener.onRefuteConnected(info);
    				break;
    			case TYPE_TRANFER_REQUEST:
    				mNetPlayListener.onRequestTransfer(info);
    				break;
    			case TYPE_TRANFER_AGREE:
    				mNetPlayListener.onAgreeTransfer();
    				break;
    			case TYPE_TRANFER_REFUTE:
    				mNetPlayListener.onRefuteTransfer();
    				break;
    			case TYPE_TRANFER_FINISH:
    				mNetPlayListener.onFinishTransfer(info);
    				break;
    			case TYPE_START_WIFI_GAME:
    				mNetPlayListener.onStartWIFIGame(info);
    				break;
    			case TYPE_START_BT_GAME:
    				mNetPlayListener.onStartBTGame(info);
    				break;
    			default:
    				break;
    			}
    		}
    	});
    }
    
    public static void loadLibFinish() {
    	if(null != mGameLogo) {
    		Handler handler = mGameLogo.getHandler();
    		if(null != handler) {
    			handler.obtainMessage(GameLogoActivity.MSG_TO_FINISH).sendToTarget();
    		}
    	}
    }
    
    public static void playGames() {
    	if(null != mGamePlay) {
    		Handler handler = mGamePlay.getHandler();
    		if(null != handler) {
    			handler.obtainMessage(0, GamePlayActivity.MSG_TO_SHOWGAMES, 0).sendToTarget();
    		}
    	}
    }
    
    private static OnAddRomListener mRomListener = null;

    public static void setOnAddRomListener(OnAddRomListener listener) {
        mRomListener = listener;
    }

    private static OnAddCheatListener mCheatListener = null;

    public static void setOnAddCheatListener(OnAddCheatListener listener) {
        mCheatListener = listener;
    }
    
    private static OnNetPlayListener mNetPlayListener = null;
    
    public static void setOnNetPlayListener(OnNetPlayListener listener) {
    	mNetPlayListener = listener;
    }

}

