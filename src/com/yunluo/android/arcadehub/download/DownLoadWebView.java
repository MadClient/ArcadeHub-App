
package com.yunluo.android.arcadehub.download;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.yunluo.android.arcadehub.utils.ContentValue;
import com.yunluo.android.arcadehub.utils.Debug;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.AttributeSet;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.webkit.DownloadListener;

public class DownLoadWebView extends LinearLayout {

	private Context mContext = null;

    private WebView mWebView = null;
    
    private Handler mHandler = new Handler();
    
    boolean isDown = false;

    private List<String> downName = new ArrayList<String>();

    public DownLoadWebView(Context context) {
        super(context);
        this.mContext = context;
        this.setOrientation(LinearLayout.VERTICAL);
        init();
    }
    
    public DownLoadWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
        this.setOrientation(LinearLayout.VERTICAL);
        init();
	}

    private void init() {
        mWebView = new WebView(mContext);
        mWebView.setWebChromeClient(new MyWebChromeClient());
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.setDownloadListener(new MyDownloadListener());
        this.addView(mWebView);

        WebSettings setting = mWebView.getSettings();
        setting.setJavaScriptEnabled(true);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void load(String url) {
    	if(null == mWebView) {
    		return;
    	}
        mWebView.loadUrl(url);
    }
    
    public void loadData(String summary, String mimeType, String encoding) {
    	if(null == mWebView) {
    		return;
    	}
    	mWebView.loadData(summary, mimeType, encoding);
    }

    class MyWebChromeClient extends WebChromeClient {

    }

    class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
            super.shouldOverrideUrlLoading(view, url);
            if (isDown == false) {
                new Thread() {
                    public void run() {
                        String fileName = getFileName(url);
                        if (null != fileName && 0 != fileName.length()) {
                            b: for (String str : downName) {
                                if (fileName == str || fileName.equals(str)) {
                                    isDown = true;
                                    break b;
                                }
                            }
                            if (isDown == false) {
                                downName.add(fileName);
                                Intent intent = new Intent();
                                intent.setAction(ContentValue.DOWNLOAD_ACTION);
                                intent.putExtra("url", url);
                                intent.putExtra("name", fileName);
                                mContext.sendBroadcast(intent);

                            } else {
                                isDown = false;
                            }
                        } else {
                            mHandler.post(new Runnable() {

                                @Override
                                public void run() {
                                    // TODO Auto-generated method stub
                                    view.loadUrl(url);
                                }

                            });
                        }
                    }
                }.start();
            }else{
                isDown = false;     
            }
            return true;
        }
    }

    class MyDownloadListener implements DownloadListener {

        @Override
        public void onDownloadStart(final String url, String userAgent, String contentDisposition,
                String mimetype, long contentLength) {
            isDown = true;
            new Thread(){
                public void run(){
                    String fileName = getFileName(url);
                    if (null != fileName && 0 != fileName.length()) {
                        boolean mDouble = false;
                        b: for (String str : downName) {
                            if (fileName == str || fileName.equals(str)) {
                                mDouble = true;
                                break b;
                            }
                        }
                        if (mDouble == false) {
                            Intent intent = new Intent();
                            intent.setAction(ContentValue.DOWNLOAD_ACTION);
                            intent.putExtra("url", url);
                            intent.putExtra("name", fileName);
                            mContext.sendBroadcast(intent);
                        }
                    }
                }
            }.start();
        }

    }

    public String getStringName(String url) {
        String fileName = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("Referer", url);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            // syd start
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                Header[] headers = httpResponse.getAllHeaders();
                for (Header header : headers) {
                    if ("Content-Disposition".equalsIgnoreCase(header.getName())) {
                        fileName = header.getValue();
                        String sign = "=\"";
                        String zip = ".zip\"";
                        int start = fileName.lastIndexOf(sign) + sign.length();
                        fileName = fileName.substring(start, fileName.length() - zip.length());
                    }
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
        }
        return fileName;
    }

    public String getFileName(String url) {
        Debug.i("getFileName url= ", url);
        String filename = null;
        boolean isok = false;
        if (url.contains(".zip")) {
            filename = url.substring(url.lastIndexOf("/") + 1, url.length() - 4);
        }
        if (null != filename && filename.length() != 0) {
            return filename;
        }else{
            filename = getStringName(url);
        }
        if (null != filename && filename.length() != 0) {
            return filename;
        }
        try {
            URL myURL = new URL(url);

            URLConnection conn = myURL.openConnection();
            if (conn == null) {
                return null;
            }
            Map<String, List<String>> hf = conn.getHeaderFields();
            if (hf == null) {
                return null;
            }
            Set<String> key = hf.keySet();
            if (key == null) {
                return null;
            }

            for (String skey : key) {
                List<String> values = hf.get(skey);
                for (String value : values) {
                    String result;
                    try {
                        result = new String(value.getBytes("UTF-8"), "GBK");
                        int location = result.indexOf("filename");
                        if (location >= 0) {
                            result = result.substring(location + "filename".length());
                            filename = result.substring(result.indexOf("=") + 1,
                                    result.length() - 4);
                            isok = true;
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                if (isok) {
                    break;
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filename;
    }

}
