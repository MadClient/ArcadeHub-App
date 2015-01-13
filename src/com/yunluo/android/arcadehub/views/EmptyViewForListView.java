package com.yunluo.android.arcadehub.views;

import com.yunluo.android.arcadehub.GameListActivity;
import com.yunluo.android.arcadehub.R;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class EmptyViewForListView extends RelativeLayout {

	private Context mContext = null;
	private TextView mTv;
	private boolean mFlag = false;
	private static int IMG_ID = 258;
	private int width = 30;
	
	public EmptyViewForListView(Context context, boolean flag) {
		super(context);
		this.mContext = context;
		this.mFlag = flag;
		
		if(width<GameListActivity.SCREEN_WIDTH){
			width = GameListActivity.SCREEN_WIDTH/10;
		}
		 
		init();
	}
	
	private void init() {
		if(true == mFlag) {
		    addBg();
		}

		RelativeLayout.LayoutParams mTvImgLp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		mTvImgLp.setMargins(width, 0, width, 0);
		mTvImgLp.topMargin = 20;
		
		mTv = new TextView(mContext);
		if(true == mFlag) {
		    mTvImgLp.addRule(RelativeLayout.BELOW, IMG_ID);
		}
		mTv.setLayoutParams(mTvImgLp);
		mTv.setTextSize(16);
		mTv.setGravity(Gravity.CENTER);
		this.addView(mTv);
	}
	
	public void addBg() {
	    RelativeLayout.LayoutParams mImgLp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
	   int i = width*2-2;
	    mImgLp.setMargins(i, 0, i, 0);
	    ImageView mDefImg = new ImageView(mContext);
        mDefImg.setId(IMG_ID);
	    mDefImg.setImageResource(R.drawable.ic_usb_transfer);
	    mImgLp.topMargin = 100;
        this.addView(mDefImg, mImgLp);
	}
	
	public void setText(String info) {
		mTv.setText(info);
	}
	
	public void setBackground(int id) {
		this.setBackgroundResource(id);
	}
}
