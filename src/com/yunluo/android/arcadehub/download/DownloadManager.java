
package com.yunluo.android.arcadehub.download;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import com.yunluo.android.arcadehub.interfac.OnDownloadListener;
import com.yunluo.android.arcadehub.utils.ContentValue;
import com.yunluo.android.arcadehub.utils.Debug;

public class DownloadManager {

    // Download URL
    private String mUrl;

    private ExecutorService mExecutorService;

    public DownloadManager() {
        super();
    }

    public void addTask(String url, String id) {
        this.mUrl = url;
        Debug.d("DownloadManager", "mUrl = " + mUrl + " , id = " + id);
        mExecutorService.submit(new DownloadThread(id, url));
    }

    public void stop() {
        mExecutorService.shutdown();
    }

    public void start(int i) {
        mExecutorService = Executors.newFixedThreadPool(i);
    }

    public boolean isStop() {
        return mExecutorService.isShutdown();
    }

    class DownloadThread implements Runnable {
        private long finished = 0;
        private long fileLength;
        private String fileId;
        private File downloadFile;
        private String downUrl = null;

        public DownloadThread(String id, String url) {
            fileId = id;
            downUrl = url;
            Debug.d("ArcadeHub.DOWNLOAD_PATH", ContentValue.DOWNLOAD_PATH);
            File dir = new File(ContentValue.DOWNLOAD_PATH);
            if (!dir.exists()) {
                boolean boo = dir.mkdirs();
                Debug.d("create down file dir", (boo == true) ? "succeed" : "failure");
            }
            
            downloadFile = new File(ContentValue.DOWNLOAD_PATH + File.separator + fileId
                    + ".zip");
            if (!downloadFile.exists()) {
                try {
                    downloadFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    Debug.d("create new file failure", "");
                }
            }
        }

        @Override
        public void run() {
        	Debug.d("DownloadThread", "run");
        	HttpParams httpParameters = new BasicHttpParams();
        	HttpConnectionParams.setConnectionTimeout(httpParameters, 20000);
        	HttpConnectionParams.setSoTimeout(httpParameters, 20000);

        	DefaultHttpClient httpClient = new DefaultHttpClient();
        	httpClient.setParams(httpParameters);
        	
        	HttpGet httpGet = new HttpGet(downUrl);
        	httpGet.setHeader("Referer", downUrl);

        	HttpResponse httpResponse = null;
        	HttpEntity httpEntity = null;
        	try {
        		httpResponse = httpClient.execute(httpGet);
        		httpEntity = httpResponse.getEntity();
        		fileLength = httpEntity.getContentLength();
        	} catch (ClientProtocolException e) {
        		e.printStackTrace();
        		httpResponse = null;
        		httpEntity = null;
        		fileLength = 0; 
        	} catch (IOException e) {
        		e.printStackTrace();
        		httpResponse = null;
        		httpEntity = null;
        		fileLength = 0; 
        	}
        	
        	if(null == httpEntity) {
        		if(null != mDownloadListener) {
        			mDownloadListener.downloadError(fileId);
        		}
        		return;
        	}

        	FileOutputStream outputStream = null;
        	int count = 0;
        	int deltaCount = 0;
        	byte[] tmp = new byte[8 * 1024];
        	Debug.d("tag", "查询得到文件大小为： " + fileLength);
        	try {
        		outputStream = new FileOutputStream(downloadFile, true);
        	} catch (FileNotFoundException e) {
        		e.printStackTrace();
        		outputStream = null;
        	}
        	if(null == outputStream) {
        		if(null != mDownloadListener) {
        			mDownloadListener.downloadError(fileId);
        		}
        		Debug.d("DownloadThread", "error");
        		return;
        	}
        	try{
        		InputStream is = httpEntity.getContent();
        		while (finished < fileLength) {
        			if (DownloadManager.this.mExecutorService.isShutdown()) {
        				return;
        			}
        			count = is.read(tmp);
        			finished += count;
        			deltaCount += count;
        			outputStream.write(tmp, 0, count);

        			if (Float.valueOf(deltaCount) / Float.valueOf(fileLength) > 0.05) {
        				deltaCount = 0;
        				int percent = (int)(Float.valueOf(finished) / Float.valueOf(fileLength) * 100);
        				Debug.d("DownloadThread percent", percent);
        				if(null != mDownloadListener) {
        					mDownloadListener.downloading(fileId, percent, fileLength);
        				}
        			}

        		}
        		outputStream.flush();
        		outputStream.close();
        		httpClient.getConnectionManager().shutdown();
        		if(null != mDownloadListener) {
        			mDownloadListener.downloaded(fileId);
        		}
        	} catch (Exception e) {
        		Debug.d("DownloadThread", "error");
        		if(null != mDownloadListener) {
        			mDownloadListener.downloadError(fileId);
        		}
        	} finally {
        		if(null != outputStream) {
        			try {
        				outputStream.close();
        			} catch (IOException e) {
        				e.printStackTrace();
        			}
        			outputStream = null;
        		}
        	}
        }
    }

    public static Map<String, String> getHttpResponseHeader(HttpURLConnection http) {
        Map<String, String> header = new LinkedHashMap<String, String>();
        for (int i = 0;; i++) {
            String mine = http.getHeaderField(i);
            if (mine == null)
                break;
            header.put(http.getHeaderFieldKey(i), mine);
        }
        return header;
    }
    
    private OnDownloadListener mDownloadListener = null;
    
    public void setOnDownloadListener(OnDownloadListener listener) {
    	this.mDownloadListener = listener;
    }

}
