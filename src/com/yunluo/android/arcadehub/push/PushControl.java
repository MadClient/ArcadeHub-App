package com.yunluo.android.arcadehub.push;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

import com.yunluo.android.arcadehub.utils.Debug;

import android.content.Context;

public class PushControl {

	private static final String POST_URL = "http://www.baidu.com";

	private static final String METHORD_POST = "POST";

	private static final String METHORD_GET = "GET";

	private Context mContext = null;

	private PushUtils mPushUtils = null;

	public PushControl(Context context) {
		this.mContext = context;
		mPushUtils = new PushUtils(mContext);
	}

	public void post() {

		new Thread(new Runnable() {

			@Override
			public void run() {

				HttpURLConnection mHttpConn = getHttpURLConnection(true);
				if(null == mHttpConn) {
					return;
				}
				initPost(mHttpConn);
				initParams(mHttpConn);
				int respCode;
				try {
					respCode = mHttpConn.getResponseCode();
					if(respCode == HttpURLConnection.HTTP_OK) {
						//读取内容
						read(mHttpConn);
					}
				} catch (IOException e) {
//					e.printStackTrace();
				} finally {
					if(null != mHttpConn) {
						mHttpConn.disconnect();
						mHttpConn = null;
					}
				}
			}

		}).start();
	}

	public void get() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				HttpURLConnection mHttpConn = getHttpURLConnection(false);
				if(null == mHttpConn) {
					return;
				}
				initGet(mHttpConn);

				int respCode;
				try {
					respCode = mHttpConn.getResponseCode();
					Debug.e("respCode = ", respCode);
					if(respCode == 200) {
						String data = mHttpConn.getHeaderField("x-alp-conf");
						Debug.e("data = ", data);
						if(null != mOnPushResponseListener && null != data) {
							mOnPushResponseListener.doPushHeaderResponse(data.getBytes());
						}
						read(mHttpConn);
					} else {
						if(null != mOnPushResponseListener) {
							mOnPushResponseListener.doErrorResponse();
						}
					}
				} catch (IOException e) {
//					e.printStackTrace();
				} finally {
					if(null != mHttpConn) {
						mHttpConn.disconnect();
						mHttpConn = null;
					}
				}
			}
		}).start();
	}

	private void read(HttpURLConnection mHttpConn) {
		if(null == mHttpConn) {
			return;
		}
		HttpURLConnection conn = mHttpConn;
		int len = 0;
		byte[] buf = new byte[1024*8];
		ByteArrayOutputStream baos = null;
		InputStream is = null;
		try {
			baos = new ByteArrayOutputStream();
			is = conn.getInputStream();
			while((len = is.read()) != -1) {
				baos.write(buf, 0, len);
			}

			if(null != mOnPushResponseListener) {
			}
			mOnPushResponseListener.doPushBodyResponse(baos.toByteArray());
			baos.flush();
			baos.close();
		} catch (IOException e1) {
//			e1.printStackTrace();
		} finally {
			if(null != baos) {
				try {
					baos.close();
				} catch (IOException e) {
//					e.printStackTrace();
				}
				baos = null;
			}
		}

	}

	/**
	 * @return HttpURLConnection
	 */
	private HttpURLConnection getHttpURLConnection(boolean flag) {
		HttpURLConnection conn = null;
		URL mUrl = getUrl();
		if(true == flag) {
			mUrl = getPostUrl();
		}
		if(null == mUrl) {
			return conn;
		}
		try {
			conn = (HttpURLConnection) mUrl.openConnection();
		} catch (IOException e) {
			conn = null;
//			e.printStackTrace();
		}
		return conn;
	}

	private URL getUrl() {
		String url = mPushUtils.getWholeUrl();
		Debug.e("-> url: ", url);
		URL mUrl = null;
		try {
			mUrl = new URL(url);
		} catch (MalformedURLException e) {
			mUrl = null;
//			e.printStackTrace();
		}
		return mUrl;
	}

	/**
	 * @return post URL
	 */
	private URL getPostUrl() {
		URL mUrl = null;
		try {
			mUrl = new URL(POST_URL);
		} catch (MalformedURLException e) {
			mUrl = null;
//			e.printStackTrace();
		}
		return mUrl;
	}

	/**
	 * Added post
	 * @param httpConn HttpURLConnection
	 */
	private void initParams(HttpURLConnection httpConn) {
		if(null == httpConn) {
			return;
		}
		HttpURLConnection conn = httpConn;
		DataOutputStream mDos = null;
		try {
			mDos = new DataOutputStream(conn.getOutputStream());
			// The URL-encoded contend
			String content = "firstname=" + URLEncoder.encode("一个大肥人", "utf-8");
			mDos.writeBytes(content); 
			mDos.flush();
			mDos.close(); // flush and close
		} catch (IOException e) {
//			e.printStackTrace();
			mDos = null;
		} finally {
			if(null != mDos) {
				try {
					mDos.flush();
					mDos.close();
					mDos = null;
				} catch (IOException e) {
//					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * init post
	 * @param conn
	 */
	private void initPost(HttpURLConnection httpConn) {
		if(null == httpConn) {
			return;
		}
		HttpURLConnection conn = httpConn;
		conn.setDoOutput(true); 
		conn.setDoInput(true);  
		conn.setUseCaches(false); 
		conn.setInstanceFollowRedirects(true);
		conn.setConnectTimeout(20*1000);  
		conn.setRequestProperty("Content-type", "application/x-java-serialized-object"); 
		try {
			conn.setRequestMethod(METHORD_POST);
			conn.connect(); 
		} catch (ProtocolException e) {
//			e.printStackTrace();
		} catch (IOException e) {
//			e.printStackTrace();
		} 
	}

	/**
	 * init get
	 * @param httpConn
	 */
	private void initGet(HttpURLConnection httpConn) {
		if(null == httpConn) {
			return;
		}
		HttpURLConnection conn = httpConn;
		conn.setConnectTimeout(20*1000);
		try {
			conn.setRequestMethod(METHORD_GET);
			conn.connect();
		} catch (ProtocolException e) {
//			e.printStackTrace();
		} catch (IOException e) {
//			e.printStackTrace();
		}

	}

	private OnPushResponseListener mOnPushResponseListener = null;

	public void setOnPushResponseListener(OnPushResponseListener listener) {
		this.mOnPushResponseListener = listener;
	}

}
