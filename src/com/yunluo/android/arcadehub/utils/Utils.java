
package com.yunluo.android.arcadehub.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import taobe.tec.jcc.JChineseConvertor;

import com.yunluo.android.arcadehub.R;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.StatFs;
import android.util.DisplayMetrics;

public class Utils {
	
    public static boolean isChinese() {
        String language = Locale.getDefault().getLanguage().substring(0, 2);
        return "zh".equalsIgnoreCase(language);
    }

    public static boolean isCN() {
    	String language = Locale.getDefault().getCountry(); 
    	return "CN".equals(language);
    }
    
    public static String getWFAddress(Context context) {
        WifiManager wifimanage = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);// 获取WifiManager
        if (!wifimanage.isWifiEnabled()) {
            wifimanage.setWifiEnabled(true);

        }

        WifiInfo wifiinfo = wifimanage.getConnectionInfo();

        String ip = intToIp(wifiinfo.getIpAddress());
        return ip;
    }

    private static String intToIp(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "."
                + ((i >> 24) & 0xFF);
    }

    public static synchronized String getCpu() {
        String key_cpu = null;
        String path[] = new String[] { "/system/bin/cat", "/proc/cpuinfo" };
        String target = "/system/bin/";
        if (key_cpu == null) {
            try {
                String[] cpu = getCpuInfo(path, target).split("\n");
                if (null != cpu && cpu.length > 0) {
                    for (int n = 0; n < cpu.length; n++) {
                        String[] arrayOfString = cpu[n].split("\t: ");
                        if(arrayOfString != null && arrayOfString.length == 2){
                            if (!arrayOfString[0].equals("BogoMIPS")) {
                                continue;
                            }
                            key_cpu = arrayOfString[1];
                        }
                    }
                }
            } catch (Exception e) {
            }
        }
        
        return key_cpu;
    }

    public static synchronized String getCpuInfo(String[] ArrayOfString, String paramString) {
        String str = null;
        try {
            if (ArrayOfString != null) {
                ProcessBuilder bulider = new ProcessBuilder(ArrayOfString);
                if (paramString != null) {
                    bulider.directory(new File(paramString));
                }
                bulider.redirectErrorStream(true);
                Process process = bulider.start();
                InputStream in = process.getInputStream();
                byte[] re = new byte[1024];
                while (in.read(re) != -1) {
                    str = str + new String(re);
                }
                if (in != null) {
                    in.close();
                }
            }
        } catch (Exception localException) {
        }
        return str;
    }

    public static String getRam() {
        String key_ram = null;
        String infopath = "/proc/meminfo";
        String infostr = "";
        FileReader fr = null;
        BufferedReader localBufferedReader = null;
        try {
            fr = new FileReader(infopath);
            localBufferedReader = new BufferedReader(fr, 8192);

            while ((infostr = localBufferedReader.readLine()) != null) {
                if (infostr.contains("MemTotal")) {
                    key_ram = infostr.split(":")[1].replace("+", "").toLowerCase()
                            .replace("kb", "").trim();
                    if (null != key_ram) {
                        String rams[] = FileUtil.fileSize(Long.valueOf(key_ram));
                        key_ram = rams[0];
                    }
                }
            }
        } catch (IOException e) {
        } finally {
            try {
                if (localBufferedReader != null) {
                    localBufferedReader.close();
                }
                if (fr != null) {
                    fr.close();
                }
            } catch (IOException e) {
            }
        }
        return key_ram;
    }

    public static String formatTime(long time) {
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS E");
        Date date = new Date(time);
        String target = null;
        try{
            target = mFormat.format(date);
        } catch(Exception e) {}
        return target;
    }
    
	public static boolean isExistSdcard() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return true;
		}
		return false;
	}
	
    public static boolean checkInternet(Context context) {
    	ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    	NetworkInfo info = cm.getActiveNetworkInfo();
    	if (info != null && info.isConnected()) {
    		return true;
    	} else {
    		return false;
    	}
    }
    
	public static boolean checkAvailableStore() {
		StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
		long blocSize = statFs.getBlockSize();
		long availaBlock = statFs.getAvailableBlocks();
		int size = (int) (availaBlock * blocSize / 1024 / 1024);
		
		Debug.d("Available Space：", ""+size);
		if(size < 20 ) {
			return false;
		} else {
			return true;
		}
	}  
	
	public static void disableBt() {
		BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
		if(null != mAdapter && mAdapter.isEnabled()) {
			mAdapter.disable();
		}
	}
	
	public static boolean isPortrait(Context context) {
		DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
		Boolean isPortrait = dm.widthPixels < dm.heightPixels;
		return isPortrait;
	}
	  
    public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  
 
    public static int px2dip(Context context, float pxValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    } 

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = bitmap.getWidth() / 2;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}
	
	public static Bitmap getImageFromAssetsFile(Context context, String fileName) {   
	    Bitmap image = null;   
	    AssetManager am = context.getResources().getAssets();   
	    try {   
	        InputStream is = am.open("icon/"+fileName+".png");   
	        image = BitmapFactory.decodeStream(is);   
	        is.close();   
	    } catch (IOException e) {    
	    	image = BitmapFactory.decodeResource(context.getResources(), R.drawable.default0);
	    }   
	    return image;   
	} 
	
	public static String getMD5(String val) {    
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			md5 = null;
			return null;
		}    
		md5.update(val.getBytes());    
		byte[] m = md5.digest();
		return getString(m);
	}

	private static String getString(byte[] b){    
		StringBuffer sb = new StringBuffer();    
		for(int i = 0; i < b.length; i ++){    
			sb.append(b[i]);    
		}    
		return sb.toString();    
	}
	
	private static int compare(String str, String target) {
		int d[][]; 
		int n = str.length();
		int m = target.length();
		int i; 
		int j; 
		char ch1; 
		char ch2; 
		int temp; 
		if (n == 0) {
			return m;
		}
		if (m == 0) {
			return n;
		}
		d = new int[n + 1][m + 1];
		for (i = 0; i <= n; i++) { 
			d[i][0] = i;
		}

		for (j = 0; j <= m; j++) { 
			d[0][j] = j;
		}

		for (i = 1; i <= n; i++) { 
			ch1 = str.charAt(i - 1);
			for (j = 1; j <= m; j++) {
				ch2 = target.charAt(j - 1);
				if (ch1 == ch2) {
					temp = 0;
				} else {
					temp = 1;
				}
				d[i][j] = min(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + temp);
			}
		}
		return d[n][m];
	}

	private static int min(int one, int two, int three) {
		return (one = one < two ? one : two) < three ? one : three;
	}

	public static float getSimilarityRatio(String str, String target) {
		return 1 - (float)compare(str, target)/Math.max(str.length(), target.length());
	}

	public static String changeTxt(String str) {
		if(true == isCN()) {
			return Utils.changeCN(str);
		} else {
			return Utils.changeTW(str);
		}
	}
	
	private static String changeTW(String changeText) {
		if(null == changeText) {
			return null;
		}
		try {
			JChineseConvertor jChineseConvertor = JChineseConvertor.getInstance();
			changeText = jChineseConvertor.s2t(changeText);

		} catch (IOException e) {
		}
		return changeText;
	}

	private static String changeCN(String changeText) {
		if(null == changeText) {
			return null;
		}
		try {
			JChineseConvertor jChineseConvertor = JChineseConvertor.getInstance();
			changeText = jChineseConvertor.t2s(changeText);

		} catch (IOException e) {
		}
		return changeText;
	}
	
	public static String getRootPath(Context context) {
	    if(null == context) {
	        return ContentValue.ROOT_PAHT;
	    }
	    String tmp = null;
	    if(true == isExistSdcard()) {
	        tmp = ContentValue.ROOT_PAHT;
	    } else {
	        tmp = context.getFilesDir().getAbsolutePath();
	    }
	    return tmp;
	}
	
	public static String getTransferPath(Context context) {
	    if(null == context) {
            return ContentValue.ROOT_PAHT+File.separator+ContentValue.TRANSFER_PATH;
        } 
	    String tmp = null;
        if(true == isExistSdcard()) {
            tmp = ContentValue.TRANSFER_PATH;
        } else {
            tmp = context.getFilesDir().getAbsolutePath()+File.separator+ContentValue.TRANSFER_PATH;
            chmodeFile(tmp);
        }
        return tmp;
	}

    public static void chmodeFile(String path) {
        File file = new File(path);

        if (!file.exists()) {
            file.mkdirs();
        }

        Runtime runtime = Runtime.getRuntime();
        String s1 = (new StringBuilder()).append("chmod 777 ")
                .append(file.getAbsolutePath()).toString();
        Process process = null;
        
        try {
            process = runtime.exec(s1);
        } catch (IOException e) {
        }

        try {
            if(process!=null)
                process.waitFor();
        } catch (InterruptedException e) {
        }
    }
}
