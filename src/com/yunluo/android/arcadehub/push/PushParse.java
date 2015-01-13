package com.yunluo.android.arcadehub.push;

import java.io.IOException;
import java.io.InputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.yunluo.android.arcadehub.utils.Debug;

public class PushParse {

	private String name = null;
	private int ec = 0;
	private String tnc = null;
	private String tdu = null;
	private String cs = null;
	private int nt = 1;
	private String tnt = null;
	private String tni = null;
	private Bitmap bmp = null;
	
	private String KEY_ALP = "alp";
	private String KEY_CONF = "conf";
	private String KEY_EC = "ec"; // Event Code
	private String KEY_INFO = "info";
	private String KEY_NT = "nt"; // Notification Type
	private String KEY_TNT = "tnt"; // Text Notification Title
	private String KEY_TNI = "tni"; // Text Notification Icon
	private String KEY_TNC = "tnc"; // Text Notification Contents
	private String KEY_TDU = "tdu"; // Text Download URL
	private String KEY_CS = "cs"; // Check Sum


 	public void parse(String target) {
 		Debug.d("target ", target);
		JSONTokener tokener = new JSONTokener(target);
		JSONObject obj = null;
		try {
			obj = (JSONObject) tokener.nextValue();
		} catch (JSONException e) {
//			e.printStackTrace();
			obj = null;
		}
		
		Debug.d("obj ", obj);
		
		if(obj == null)
			return;
		
		if(false == obj.has(KEY_ALP)) {
			return;
		}
		JSONObject alpObj = null;
		try {
			alpObj = obj.getJSONObject(KEY_ALP);
		} catch (JSONException e1) {
//			e1.printStackTrace();
			alpObj = null;
		}
		if(null == alpObj) {
			return;
		}
		
		if(alpObj.has(KEY_CONF)) {
			try {
				JSONObject confObj = alpObj.getJSONObject(KEY_CONF);
				Debug.d("confObj ", confObj);
				if(confObj.has(KEY_EC)) {
					int ec = confObj.optInt(KEY_EC, 0);
					setEc(ec);
				}
			} catch (JSONException e) {
//				e.printStackTrace();
			}
		}
		
		if(alpObj.has(KEY_INFO)) {
			JSONObject infoObj = null;
			try {
				infoObj = alpObj.getJSONObject(KEY_INFO);
			} catch (JSONException e) {
//				e.printStackTrace();
				infoObj = null;
			}
			if(null == infoObj) {
				return;
			}
			if(infoObj.has(KEY_NT)) {
				int nt = infoObj.optInt(KEY_NT, 1);
				setNt(nt);
			}
			if(infoObj.has(KEY_TNT)) {
				String tnt = infoObj.optString(KEY_TNT, null);
				setTnt(tnt);
			}
			if(infoObj.has(KEY_TNI)) {
				String tni = infoObj.optString(KEY_TNI, null);
				setTni(tni);
				getBitmap(tni);
			}
			if(infoObj.has(KEY_TNC)) {
				String nc = infoObj.optString(KEY_TNC, null);
				setTnc(nc);
			}
			if(infoObj.has(KEY_TDU)) {
				String du = infoObj.optString(KEY_TDU, null);
				setTdu(du);
				if(null != du && du.length() != 0) {
					if(du.endsWith(".zip")) {
						int end = du.lastIndexOf(".");
						int start = du.lastIndexOf("/")+1;
						String name = du.substring(start, end);
						setName(name);
					}
				}
			}
			if(infoObj.has(KEY_CS)) {
				String cs = infoObj.optString(KEY_CS, null);
				setCs(cs);
			}
		}
		
	}
 	
 	public void parseBody(String body) {
 		//setTdu
 		//setName
 	}
 	
	public String getName() {
		return name;
	}

	private void setName(String name) {
		this.name = name;
	}
	
	public int getEc() {
		return ec;
	}

	private void setEc(int ec) {
		this.ec = ec;
	}

	public String getTnc() {
		return tnc;
	}

	private void setTnc(String nc) {
		this.tnc = nc;
	}

	public String getTdu() {
		return tdu;
	}

	private void setTdu(String du) {
		this.tdu = du;
	}

	public String getCs() {
		return cs;
	}

	private void setCs(String cs) {
		this.cs = cs;
	}
	
	public int getNt() {
		return nt;
	}

	private void setNt(int nt) {
		this.nt = nt;
	}

	public String getTnt() {
		return tnt;
	}

	private void setTnt(String tnt) {
		this.tnt = tnt;
	}

	public String getTni() {
		return tni;
	}

	private void setTni(String tni) {
		this.tni = tni;
	}

	/**
	 * get network image 
	 * 
	 * @return Bitmap
 	 */
	private void getBitmap(final String imgUrl) {
		if(null == imgUrl) {
			return;
		}
		new Thread(new Runnable() {

			@Override
			public void run() {
				HttpGet httpRequest = new HttpGet(imgUrl);  
				HttpClient httpclient = new DefaultHttpClient();  
				HttpResponse httpResponse;
				try {
					httpResponse = httpclient.execute(httpRequest);
					if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){  
						HttpEntity httpEntity = httpResponse.getEntity();  
						InputStream is = httpEntity.getContent();  
						setBmp(BitmapFactory.decodeStream(is));  
					}  
				} catch (ClientProtocolException e) {
//					e.printStackTrace();
				} catch (IOException e) {
//					e.printStackTrace();
				}  
			}
			
		}).start();
		
	}
	
	@Override
	public String toString() {
		return "PushParse [name=" + name + ", ec=" + ec + ", tnc=" + tnc
				+ ", tdu=" + tdu + ", cs=" + cs + ", nt=" + nt + ", tnt=" + tnt
				+ ", tni=" + tni + ", bmp=" + bmp + ", KEY_ALP=" + KEY_ALP
				+ ", KEY_CONF=" + KEY_CONF + ", KEY_EC=" + KEY_EC
				+ ", KEY_INFO=" + KEY_INFO + ", KEY_NT=" + KEY_NT
				+ ", KEY_TNT=" + KEY_TNT + ", KEY_TNI=" + KEY_TNI
				+ ", KEY_TNC=" + KEY_TNC + ", KEY_TDU=" + KEY_TDU + ", KEY_CS="
				+ KEY_CS + "]";
	}

	public Bitmap getBmp() {
		return bmp;
	}

	private void setBmp(Bitmap bmp) {
		this.bmp = bmp;
	}

}
