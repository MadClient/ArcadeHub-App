package com.yunluo.android.arcadehub;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class LicenseActivity extends Activity {

	private ImageView mBack = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.license);
		init();
	}
	
	private void init() {
		TextView v = (TextView)this.findViewById(R.id.license_tv);
		try {
			v.setText(this.convertStreamToString(getResources().openRawResource(R.raw.readme)));
		} catch (NotFoundException e) {
		} catch (IOException e) {
		}
		
		mBack = (ImageView) this.findViewById(R.id.license_back);
		
		mBack.setOnClickListener(mOnClickListener);
	}
	
	public String convertStreamToString(InputStream is) throws IOException {

		if (is != null) {
			StringBuilder sb = new StringBuilder();
			String line;

			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is, "UTF-8"));
				while ((line = reader.readLine()) != null) {
					sb.append(line).append("\n");
				}
			} finally {
				is.close();
			}
			return sb.toString();
		} else {
			return "";
		}
	}
	
	private OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
		
			int id = v.getId();
			switch(id) {
			case R.id.license_back:
				finish();
				break;
			}
		}
		
	};
	
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	};
	
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);

	};

}
