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
import com.yunluo.android.arcadehub.GameLogoActivity;
import com.yunluo.android.arcadehub.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

public class PushNotify {

	private Context mContext = null;
	
	private PushParse mPushParse = null;
	
//	private Bitmap bmp = null;

	public PushNotify(Context context) {
		this.mContext = context;
	}
	
	public void setPushParse(PushParse pushParse) {
		this.mPushParse = pushParse;
	}

	public void showNotification() {
		if(null == mPushParse) {
			return;
		}
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon);
				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext).setSmallIcon(R.drawable.icon)
						.setContentTitle(mPushParse.getTnt())
						.setContentText(mPushParse.getTnc());
				mBuilder.setTicker("New message");/
				mBuilder.setLargeIcon(bmp);
				mBuilder.setAutoCancel(true);
				mBuilder.setDefaults(Notification.DEFAULT_ALL); 

				Bundle bun = new Bundle();
				bun.putBoolean("push", true);
				bun.putString("name", mPushParse.getName());
				bun.putString("url", mPushParse.getTdu());
				bun.putString("checksum", mPushParse.getCs());
				Intent resultIntent = new Intent(mContext, GameLogoActivity.class);
				resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				resultIntent.putExtras(bun);
				PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
				mBuilder.setContentIntent(resultPendingIntent);
				NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
				try {
                    mNotificationManager.notify(0, mBuilder.getNotification());
                } catch (Exception e) {
                    // TODO Auto-generated catch block
//                    e.printStackTrace();
                }
			}
			
		}).start();
		
	}
	
	/**
	 * Get Network Image
	 * 
	 * @return net image
 	 */
	private Bitmap getBitmap() {
		Bitmap bmp = null;
		if(null == mPushParse) {
			bmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon);
			return bmp;
		}
		
		String imgUrl = mPushParse.getTni();
		if(null == imgUrl) {
			bmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon);
			return bmp;
		}
		
		HttpGet httpRequest = new HttpGet(imgUrl);  
		HttpClient httpclient = new DefaultHttpClient();  
		HttpResponse httpResponse;
		try {
			httpResponse = httpclient.execute(httpRequest);
			if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){  
				HttpEntity httpEntity = httpResponse.getEntity();  
				InputStream is = httpEntity.getContent();  
				bmp = BitmapFactory.decodeStream(is);  
			}  
		} catch (ClientProtocolException e) {
//			e.printStackTrace();
			bmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon);
		} catch (IOException e) {
//			e.printStackTrace();
			bmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon);
		}  
		
		return bmp;
	}
	
}
