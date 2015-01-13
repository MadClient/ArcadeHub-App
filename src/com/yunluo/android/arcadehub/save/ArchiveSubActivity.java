package com.yunluo.android.arcadehub.save;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.yunluo.android.arcadehub.BaseApplication;
import com.yunluo.android.arcadehub.R;
import com.yunluo.android.arcadehub.ad.ADBanner;
import com.yunluo.android.arcadehub.utils.Debug;

public class ArchiveSubActivity extends Activity {
	
	private boolean inGames = false;
	private String romName = null;
	private ArchiveSubView mArchiveSubView = null;
	private BaseApplication mApp = null;
	private ADBanner mADBanner = null;
	private RelativeLayout mailLayout = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bun = this.getIntent().getExtras();
		inGames = bun.getBoolean("IN_GAME");
		romName = bun.getString("ROM_NAME");
		mApp = (BaseApplication) this.getApplication();
		
		init();
	}
	
	private void init() {
		mArchiveSubView = new ArchiveSubView(this, romName);
		mApp.setArchiveSubView(mArchiveSubView);
        mArchiveSubView.setInGames(inGames);
        if(true == inGames) {
        }
        mArchiveSubView.setOrientation(LinearLayout.VERTICAL);
        int color = this.getResources().getColor(R.color.transparent_half);
        mArchiveSubView.setBackgroundColor(color);
        
        mailLayout = new RelativeLayout(this);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		mailLayout.setLayoutParams(lp);
		mailLayout.addView(mArchiveSubView,lp);
		addBanner();
		setContentView(mailLayout);
	}
}
