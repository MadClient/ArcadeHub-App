package com.yunluo.android.arcadehub.download;

import java.lang.ref.WeakReference;
import com.yunluo.android.arcadehub.R;
import com.yunluo.android.arcadehub.utils.ContentValue;
import com.yunluo.android.arcadehub.utils.Debug;
import com.yunluo.android.arcadehub.utils.Utils;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

public class DownLoadBroadCastRecevier {

	private static DownLoadBroadCastRecevier mInstance = null;
	private WeakReference<Context> mContextRef = null;
	private BroadcastReceiverListener mListener = null;
	private boolean isRegister = false;

	/**
	 * Registration broadcast
	 * @param context
	 */
	private DownLoadBroadCastRecevier(Context context) {
		mContextRef = new WeakReference<Context>(context.getApplicationContext());

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ContentValue.DOWNLOAD_ACTION);
		context.getApplicationContext().registerReceiver(mBroadcast, intentFilter);

		isRegister = true;
	}

	/**
	 * @param context
	 * @return
	 */
	public static final synchronized DownLoadBroadCastRecevier instance(Context context) {
		if (mInstance == null)
			if (context != null) {
				mInstance = new DownLoadBroadCastRecevier(context.getApplicationContext());
			}

		return mInstance;
	}

	/**
	 *  Logout broadcast
	 */
	public void unregister() {
		isRegister = false;

		if (null != mContextRef) {
			Context context = mContextRef.get();
			if (null != context) {
				if(null != mBroadcast) {
					context.unregisterReceiver(mBroadcast);
					mBroadcast = null;
				}
			}
		}

	}

	/**
	 * @return broadcast stuts
	 */
	public boolean isRegister() {
		return isRegister;
	}

	private BroadcastReceiver mBroadcast = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ContentValue.DOWNLOAD_ACTION.equals(action)) {
				String url = intent.getStringExtra("url");
				String name = intent.getStringExtra("name");
				String cs = intent.getStringExtra("checksum");
				Debug.d("BroadcastReceiver url", url);
				if (false == Utils.checkAvailableStore()) {
					Toast.makeText(context, context.getResources().getString(R.string.download_no_enough_space), 
							Toast.LENGTH_SHORT).show();
					return;
				}
				if (null != mListener) {
					mListener.download(url, name, cs);
				}

			}
		}
	};

	public void setBroadcastReceiverListener(BroadcastReceiverListener listener) {
		this.mListener = listener;
	}

	public interface BroadcastReceiverListener {
		public void download(String url, String name, String cs);
	}

}
