package com.yunluo.android.arcadehub.views;

import com.yunluo.android.arcadehub.Emulator;
import com.yunluo.android.arcadehub.GameListActivity;
import com.yunluo.android.arcadehub.R;
import com.yunluo.android.arcadehub.interfac.OnOptionListener;
import com.yunluo.android.arcadehub.popup.OptionPopup;
import com.yunluo.android.arcadehub.sliding.adapter.PluginListAdapter;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

public class OptionView extends LinearLayout {

	private LinearLayout mMainLayout = null;
	private Context mContext = null;
	private OptionPopup mOptionPopup = null;
	private boolean mFlag = false;

	public OptionView(OptionPopup optionPopup, Context context, boolean flag) {
		super(context);
		this.mContext = context;
		this.mOptionPopup = optionPopup;
		this.mFlag = flag;
		this.setOrientation(LinearLayout.VERTICAL);
		
		mMainLayout  = new LinearLayout(context);
		mMainLayout.setBackgroundColor(((ContextWrapper)mContext).getBaseContext().getResources().getColor(R.color.optionview_bg_color));
		mMainLayout.setOrientation(true==flag?LinearLayout.VERTICAL:LinearLayout.HORIZONTAL);
		init();
		
		if(false == flag) {
			horizontalLayout();
		} else {
			verticalLayout();
		}
	}
	
	private void horizontalLayout() {
		HorizontalScrollView mHorizontalLayout = new HorizontalScrollView(mContext);
		mHorizontalLayout.setFadingEdgeLength(0);
		mHorizontalLayout.setScrollBarStyle(GONE);
		mHorizontalLayout.setFillViewport(true);
		LinearLayout.LayoutParams mlayLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		this.addView(mHorizontalLayout);
		mHorizontalLayout.addView(mMainLayout, mlayLp);
		mHorizontalLayout.setHorizontalScrollBarEnabled(false);
	}
	
	private void verticalLayout() {
		ScrollView mScrollView = new ScrollView(mContext);
		mScrollView.setFadingEdgeLength(0);
		mScrollView.setScrollBarStyle(GONE);
		mScrollView.setFillViewport(true);
		this.addView(mScrollView);
		LinearLayout.LayoutParams rightLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
		mScrollView.addView(mMainLayout, rightLp);
		mScrollView.setVerticalScrollBarEnabled(false);
	}
	
	private void init() {
		String btnList[] = this.getResources().getStringArray(R.array.dialoghelper_option_list);
		String imgNameList[ ]= this.getResources().getStringArray(R.array.dialoghelper_option_list_imgname);
		TypedArray imgs = this.getResources().obtainTypedArray(R.array.dialoghelper_option_img);
		TypedArray ids = this.getResources().obtainTypedArray(R.array.dialoghelper_option_id);
		
		int len = btnList.length;
		for (int i = 0; i < len; i++) {
			int icon = imgs.getResourceId(i, 0);
			int id = ids.getResourceId(i, i);
			initButton(i, id, btnList[i],imgNameList[i], icon);
		}
	}
	
	private void initButton(int id, int btn_id, String desc , String imgName, int icon) {

		LinearLayout.LayoutParams mLp = new LinearLayout.LayoutParams((int)(GameListActivity.SCREEN_DENSITY*80), (int)(GameListActivity.SCREEN_DENSITY*80));
		LinearLayout mLayout  = new LinearLayout(mContext);
		mLayout.setBackgroundColor(0x00000000);
		mLayout.setLayoutParams(mLp);
		mLayout.setId(600+id);
		mLayout.setWeightSum(3);
		
		LinearLayout.LayoutParams imgLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT,1);
		imgLp.topMargin = 8;
		ImageButton btn = new ImageButton(mContext);
        btn.setScaleType(ScaleType.FIT_CENTER);
		btn.setId(btn_id);
		btn.setBackgroundColor(0x00000000);
		btn.setOnClickListener(mListener);
		btn.setLayoutParams(imgLp);
		btn.setImageResource(icon);
		
		LinearLayout.LayoutParams mTvLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT,2);
		mTvLp.gravity = Gravity.CENTER_VERTICAL;
		TextView tv= new TextView(mContext);
		int i = desc.length();
		if(10 < i){
		    tv.setTextSize(10);
		}
		tv.setText(desc);
		tv.setTextSize(12);
		tv.setTextColor(Color.WHITE);
		tv.setBackgroundColor(0x00000000);
		btn.setLayoutParams(mTvLp);
	    mLayout.setGravity(Gravity.CENTER_VERTICAL);
	    mLayout.setOrientation(LinearLayout.VERTICAL);
		tv.setGravity(Gravity.CENTER_HORIZONTAL);

		mLayout.addView(btn,imgLp);
		mLayout.addView(tv,mTvLp);
		
		mMainLayout.addView(mLayout);
		
		addLine();
	}
	
	public void addLine() {
		LinearLayout.LayoutParams vLp = null;
		if(true == mFlag) {
			vLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2);
		} else {
			vLp = new LinearLayout.LayoutParams(2, LinearLayout.LayoutParams.MATCH_PARENT);
		}
		View v = new View(mContext);
		v.setLayoutParams(vLp);
		int color = mContext.getResources().getColor(R.color.popup_line);
		v.setBackgroundColor(color);
		mMainLayout.addView(v);
		
	}
	
	public void update() {
		if(PluginListAdapter.TYPE_GAMES == 1) {
			mMainLayout.findViewById(600).setVisibility(View.VISIBLE);
			mMainLayout.findViewById(601).setVisibility(View.VISIBLE);
			mMainLayout.findViewById(602).setVisibility(View.VISIBLE);
			mMainLayout.findViewById(603).setVisibility(View.VISIBLE);
		} else {
			mMainLayout.findViewById(600).setVisibility(View.GONE);
			mMainLayout.findViewById(601).setVisibility(View.GONE);
			mMainLayout.findViewById(602).setVisibility(View.GONE);
			mMainLayout.findViewById(603).setVisibility(View.GONE);
		}
	}
	
	//-------------------------listener--------------------------------
	private OnClickListener mListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			
			if(null != mOptionPopup) {
				mOptionPopup.dismiss();
			}
			if(null == mOptionListener) {
				return;
			}
			Emulator.pause();
			
			int id = v.getId();
			switch(id) {
	            case R.id.option_save://goldfinger
	                mOptionListener.doSaveGame();
	                break;
	            case R.id.option_load:
	            	mOptionListener.doLoadGame();
	            	break;
	            case R.id.option_suspend://pause/resume
	                mOptionListener.doFrezee();
	                break;
	            case R.id.option_cheat://cheat
	                mOptionListener.doCheat();
	                break;
	            case R.id.option_multipress: 
                    mOptionListener.doKeyCombination();
	                break;
	            case R.id.option_multikey://按键宏
	            	mOptionListener.doKeyMacro();
	                break;
	            case R.id.option_setting: //setting
                    mOptionListener.doSettings();
	                break;
	          case R.id.option_hiscore: 
	              break;
	            case R.id.option_about://about
                    mOptionListener.doAbout();
	                break;
	                default:
	                    break; 
	            }
			
		}
		
	};
	
	private OnOptionListener mOptionListener = null;
	
	public void setOnOptionListener(OnOptionListener listener) {
		this.mOptionListener = listener;
	}
	
	
}
