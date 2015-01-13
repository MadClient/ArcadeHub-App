package com.yunluo.android.arcadehub.save;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.umeng.analytics.MobclickAgent;
import com.yunluo.android.arcadehub.GamePlayActivity;
import com.yunluo.android.arcadehub.R;
import com.yunluo.android.arcadehub.ad.ADBanner;
import com.yunluo.android.arcadehub.utils.Debug;

public class ArchiveSaveActivity extends Activity {

	private String romName = null;
	private ArchiveSaveView mArchiveSaveView = null;
	private ADBanner mADBanner = null;
	private RelativeLayout mailLayout = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bun = this.getIntent().getExtras();
		romName = bun.getString("ROM_NAME");
		init();
	}

	private void init() {
		mArchiveSaveView = new ArchiveSaveView(this, romName);
		mArchiveSaveView.setOrientation(LinearLayout.VERTICAL);
		int color = this.getResources().getColor(R.color.transparent_half);
		mArchiveSaveView.setBackgroundColor(color);
		
		mailLayout = new RelativeLayout(this);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		mailLayout.setLayoutParams(lp);
		mailLayout.addView(mArchiveSaveView,lp);
		addBanner();
		setContentView(mailLayout);
	}

	public void destroy() {
		if(null != mADBanner) {
	        mADBanner.clearThread();
	        mADBanner = null;
	    }
		Intent intent = new Intent();
		setResult(235, intent);
		finish();
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}
}
