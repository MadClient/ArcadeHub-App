package com.yunluo.android.arcadehub.push;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import com.yunluo.android.arcadehub.utils.ContentValue;
import com.yunluo.android.arcadehub.utils.SharePreferenceUtil;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;

class PushUtils {

    //release
	private static final String GET_URL = "ahr.php";
	
	private String[] NetworkTypes = { "UNKNOWN", "GPRS", "EDGE", "UMTS", "CDMA","EVDO_0", "EVDO_A", "1xRTT", "HSDPA", "HSUPA", "HSPA", "IDEN","EVDO_B", "LTE", "EHRPD", "HSPAP" };
	private final int NETWORK_NONE = -1,NETWORK_2G = 0,NETWORK_3G = 2,NETWORK_WIFI = 3,SENSOR_GRAVITY = 9,SENSOR_LINEAR_ACCELERATION = 10,SENSOR_ROTATION =11; 

	private String KEY_OS = null;
	private String KEY_OSV = null;
	private String KEY_OID = null; 
	private String KEY_MOD = null;
	private String KEY_DID = null;
	private String KEY_JB = null;
	private String KEY_CPU = null;
	private String KEY_RAM = null;
	private String KEY_ROM = null;
	private String KEY_LNG = null;
	private String KEY_GMT = null;
	private String KEY_NT = null;
	private String KEY_CN = null;
	private String KEY_MCC = null;
	private String KEY_MNC = null;
	private String KEY_LAC = null;
	private String KEY_AV = null;
	private String KEY_APN = null;
	private String KEY_MEM = null;
	private String KEY_NCS = null;
	private String KEY_DS = null;
	private int KEY_GR = 0;
	
	private Context mContext = null;
	private TelephonyManager tm = null;
	
	public PushUtils(Context context) {
		mContext = context;
		 if(tm==null)
             tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	}

	public String getWholeUrl() {
		StringBuffer buffer = new StringBuffer(GET_URL);
		format(buffer, true, true, false, "os", getKeyOs());// √
		format(buffer, false, true, false, "osv", getKeyOsv());// √
		format(buffer, false, true, false, "oid", getKeyOid());// √
		format(buffer, false, true, false, "mod", getKeyMod());// √
		format(buffer, false, true, false, "did", getKeyDid());// √
		format(buffer, false, true, false, "jb", getKeyJb());// √
		format(buffer, false, true, false, "de", getKeyDe());// √
		format(buffer, false, true, false, "cpu", getKeyCpu());// √
		format(buffer, false, true, false, "ram", getKeyRam());// √
		format(buffer, false, true, false, "rom", getKeyRom());// √
		format(buffer, false, true, false, "lng", getKeyLng());// √
		format(buffer, false, true, false, "gmt", getKeyGmt());// √
		format(buffer, false, true, false, "nt", getKeyNt());// √
		format(buffer, false, true, false, "cn", getKeyCn());// √
		format(buffer, false, true, false, "mcc", getKeyMcc());// √
		format(buffer, false, true, false, "mnc", getKeyMnc());// √
		format(buffer, false, true, false, "lac", getKeyLac());// √
		format(buffer, false, true, false, "av", getKeyAv());// √
		format(buffer, false, true, false, "mem", getKeyMem());// √
		format(buffer, false, true, false, "cid", ContentValue.CHANNAL_ID);// √
		format(buffer, false, true, false, "ncs", getKeyNcs());// √
		format(buffer, false, true, false, "ds", getKeyDs());// √
		format(buffer, false, true, false, "gr", getKeyGr());// √
		
		return buffer.toString();
	}

	String getKeyOs() {
		KEY_OS = String.valueOf(0);
		return KEY_OS;
	}

	String getKeyOsv() {
		if (KEY_OSV == null) {
			KEY_OSV = String.valueOf(android.os.Build.VERSION.RELEASE);
		}
		return KEY_OSV;
	}

	// DEVICE ID
	String getKeyOid() {
		if (KEY_OID == null) {
			KEY_OID = Secure.getString(mContext.getContentResolver(),
					Secure.ANDROID_ID);
			if (KEY_OID == null) {
				KEY_OID = "000000000000000";
			}
		}
		return KEY_OID;
	}
	
    //DEVICE MODE
    String getKeyMod() {
        if(KEY_MOD==null){
            KEY_MOD = android.os.Build.BRAND + " " + android.os.Build.MODEL;  
        }
        return KEY_MOD;
    }
    
  //DEVICE ID
    String getKeyDid() {
        if(KEY_DID==null){
            KEY_DID = Secure.getString(mContext.getContentResolver(), Secure.ANDROID_ID);
            if(KEY_DID==null) {
                KEY_DID = "000000000000000";
            }
        }	 
        return KEY_DID;
    }
    
 // root 1  unroot 0
    String getKeyJb() {
        if(KEY_JB==null){
            File binSu = new File("/system/bin/su");
            File xbinSu = new File("/system/xbin/su");
            if (binSu.exists() || xbinSu.exists()) {
                KEY_JB = String.valueOf(1);
            } else {
                KEY_JB = String.valueOf(0);
            }
        }
        return KEY_JB;
    }
    
    int getKeyDe() {
        String deviceId = Secure.getString(mContext.getContentResolver(), Secure.ANDROID_ID);
        if("000000000000000".equalsIgnoreCase(deviceId)) {
            return 0;
        }
        return 1;
    }
    
    //DEVICE CUP(MHz)
    String getKeyCpu() {
        if(KEY_CPU==null){
            File file =  new File("/sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq");
            if(file.exists()&&file.canRead()){
                ByteArrayOutputStream os = null;
                FileInputStream fis = null;

                try {
                    fis = new FileInputStream(file);
                    os = new ByteArrayOutputStream(1024);
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = fis.read(buffer)) >= 0) {
                        os.write(buffer, 0, len);
                    }

                    byte[] b =  os.toByteArray();
                    String info = new String(b);
                    String cpu = info.split("\n")[0];

                    if(cpu!=null&&cpu.trim().length()>0){
                        KEY_CPU = String.valueOf(Integer.parseInt(cpu)/1000);
                    }
                } catch (Exception e) {
//                    e.printStackTrace();
                }finally{
                    try{
                        if(os!=null){
                            os.close();
                        }

                        if(fis!=null){
                            fis.close();
                        }

                    }catch(IOException e){
//                        e.printStackTrace();
                    }
                }	
            }
        }

        return KEY_CPU;
    }
	
    //DEVICE RAM
    String getKeyRam() {
        if(KEY_RAM==null){
            String infopath = "/proc/meminfo";  
            String infostr="";  
            FileReader fr = null;
            BufferedReader localBufferedReader = null;
            try {  
                fr = new FileReader(infopath);  
                localBufferedReader = new BufferedReader(fr, 8192);  

                while ((infostr = localBufferedReader.readLine()) != null) {  
                    if(infostr.contains("MemTotal")){
                        KEY_RAM = infostr.split(":")[1].replace("+", "").toLowerCase().replace("kb", "").trim(); 
                    }
                }
            } catch (IOException e) {  
//                e.printStackTrace();
            }finally{
                try {
                    if(localBufferedReader!=null){
                        localBufferedReader.close();
                    }
                    if(fr!=null){
                        fr.close();
                    }
                } catch (IOException e) {
//                    e.printStackTrace();
                }
            }
        }
        return KEY_RAM;
    }

    //DEVICE ROM
    String getKeyRom() {
        if(KEY_ROM==null){
            long selfSize = getRomSize();
            KEY_ROM =String.valueOf(selfSize/1024);  	       
        }
        return KEY_ROM;
    }
    
    /** get device mem byte */
    private long getRomSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }
    
    String getKeyLng() {
        if(KEY_LNG==null){
            KEY_LNG = Locale.getDefault().getLanguage() + "_" + Locale.getDefault().getCountry();
        }
        return KEY_LNG;
    }
    
    
    //Greenwich Mean Time
    String getKeyGmt() {
        if(KEY_GMT==null){
            Calendar cal = Calendar.getInstance();
            TimeZone zone = cal.getTimeZone();

            String gmtStr = zone.getDisplayName(true, TimeZone.SHORT);
            if(gmtStr.contains("+")){
                KEY_GMT = "+"+gmtStr.split("\\+")[1];
            }
            if(gmtStr.contains("-")){
                KEY_GMT = "-"+gmtStr.split("\\-")[1];
            }
        }
        return KEY_GMT;
    }
    
    //NET TYPE   0：GPRS/EDGE(2.5G/2.75G) 1：WWAN(GPRS/EDGE/3G) 2：3G(UMTS/HSDPA/EVDO) 3：WIFI
    String getKeyNt() {
        if(KEY_NT==null&&isSupportedPerimission(mContext,android.Manifest.permission.ACCESS_NETWORK_STATE)){ 
            ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = cm.getActiveNetworkInfo();
            if (ni == null || !ni.isConnected()) {
                KEY_NT = String.valueOf(NETWORK_NONE);
            } else if (ni.getType() == ConnectivityManager.TYPE_WIFI) {
                KEY_NT = String.valueOf(NETWORK_WIFI);
            } else if (ni.getType() == ConnectivityManager.TYPE_MOBILE) {
                int type = tm.getNetworkType();
                if (type < 0 || type > NetworkTypes.length - 1) {
                	KEY_NT = String.valueOf(NETWORK_WIFI); 
                } else {
                	String NetworkType = NetworkTypes[type];
                	if ("GPRS".equals(NetworkType) || "EDGE".equals(NetworkType)) {
                		KEY_NT = String.valueOf(NETWORK_2G);
                	} else if ("UMTS".equals(NetworkType)|| "EVDO_0".equals(NetworkType)|| "EVDO_A".equals(NetworkType)|| "HSDPA".equals(NetworkType)|| "HSUPA".equals(NetworkType)|| "HSPA".equals(NetworkType) || "IDEN".equals(NetworkType)|| "EVDO_B".equals(NetworkType)|| "LTE".equals(NetworkType) || "EHRPD".equals(NetworkType)|| "HSPAP".equals(NetworkType)) {
                		KEY_NT =  String.valueOf(NETWORK_3G);
                	}
                }
            }
        }
        return KEY_NT;
    }
    
    /** perimission */
    private boolean isSupportedPerimission(Context context,
            String permissionStr) {
        try {
            context.enforceCallingOrSelfPermission(permissionStr,
                    "The Smartmad SDK need " + permissionStr);
            return true;
        } catch (Throwable se) {
            return false;
        }
    }
    
    String getKeyCn() {
        if(KEY_CN == null) {
            if(tm!=null&&isSupportedPerimission(mContext,android.Manifest.permission.READ_PHONE_STATE)){
                KEY_CN = tm.getNetworkOperatorName();
            }
        }

        if("Android".equals(KEY_CN)) {
            return null;
        }

        return KEY_CN;
    }
    
    //COUNTRY CODE
    String getKeyMcc() {
        if(KEY_MCC==null&&isSupportedPerimission(mContext,android.Manifest.permission.READ_PHONE_STATE)){
            if(tm!=null){
                KEY_MCC = String.valueOf(mContext.getResources().getConfiguration().mcc);
            }
        }
        return KEY_MCC;
    }

    //NETWORK CODE
    String getKeyMnc() {
        if(KEY_MNC==null&&isSupportedPerimission(mContext,android.Manifest.permission.READ_PHONE_STATE)){
            if(tm!=null){
                KEY_MNC = String.valueOf(mContext.getResources().getConfiguration().mnc);
            }
        }
        return KEY_MNC;
    }
    
    //LOCAL AREA CODE
    String getKeyLac() {
        if(KEY_LAC==null&&isSupportedPerimission(mContext,android.Manifest.permission.ACCESS_COARSE_LOCATION)){
            if(tm!=null){
                try {
                    GsmCellLocation location = (GsmCellLocation) tm.getCellLocation();
                    if (location!=null){
                        KEY_LAC = String.valueOf(location.getLac());
                    }				 
                } catch (Throwable e) {
//                    e.printStackTrace();
                }
            }
        }
        return KEY_LAC;
    }
    
    //Application Version  version name(version code)
    String getKeyAv() {
        if(KEY_AV==null){
            PackageManager pm = mContext.getPackageManager();
            try {
                PackageInfo	pinfo = pm.getPackageInfo(getKeyApn(),PackageManager.GET_CONFIGURATIONS);
                KEY_AV = pinfo.versionName+"("+pinfo.versionCode+")"; 
            } catch (NameNotFoundException e) {
//                e.printStackTrace();
            }
        }
        return KEY_AV;
    }

  //get package name
    String getKeyApn() {
        if(KEY_APN==null){
            KEY_APN = mContext.getPackageName();
        }
        return KEY_APN;
    }
    
  //Memory
    String getKeyMem() {
        if(KEY_MEM==null){
            long max = Runtime.getRuntime().maxMemory();
            long cur = Runtime.getRuntime().totalMemory();
            KEY_MEM = String.valueOf((max-cur)/1024);
        } 
        return KEY_MEM;
    }
    
    
   String getKeyNcs() {
	   if(KEY_NCS == null) {
		   KEY_NCS = SharePreferenceUtil.loadCheckSum(mContext);
	   }
	   return KEY_NCS;
   }
   
   String getKeyDs() {
	   if(KEY_DS == null) {
		   KEY_DS = SharePreferenceUtil.loadDownloadState(mContext);
	   }
	   return KEY_DS;
   }
   
   int getKeyGr() {
       KEY_GR = SharePreferenceUtil.loadGameResource(mContext);
       return KEY_GR;
   }
    
	void format(StringBuffer sb, boolean isFirst, boolean isRequired,
			boolean isEncode, String key, Object value) {
		if (value == null) {
			if (isRequired) {
				sb.append(isFirst ? "?" : "&").append(key + "=");
			}
			return;
		}

		String mValue = String.valueOf(value);
		if (mValue.length() == 0) {
			if (isRequired) {
				sb.append(isFirst ? "?" : "&").append(key + "=");
			}
			return;
		} else {
			String param = "";
			try {
				String estr =URLEncoder.encode(mValue, "UTF-8");
				param = URLEncoder.encode(key, "UTF-8") + "=" + estr;
			} catch (Throwable e) {
				param = "";
			}
			if (param == null || param.length() == 0) {
				return;
			}
			sb.append(isFirst ? "?" : "&").append(param);
		}
	}
}
