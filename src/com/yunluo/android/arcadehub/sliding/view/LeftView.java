/**
 * ArcadeHub
 * @filename: LeftView.java
 */
package com.yunluo.android.arcadehub.sliding.view;

import com.yunluo.android.arcadehub.GameListActivity;
import com.yunluo.android.arcadehub.R;
import com.yunluo.android.arcadehub.sliding.OnMenuListener;
import com.yunluo.android.arcadehub.utils.Utils;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class LeftView extends RelativeLayout {

    public GameListActivity mContext = null;
    private Resources mRes = null;
    private LinearLayout mMenuLayout = null;
    private LinearLayout mMainLayout = null;
    private ScrollView mScrollView = null;
	private float mDensity = 1;

    public LeftView(GameListActivity context, AttributeSet attrs) {

        super(context, attrs);
        this.mContext = context;
        mRes = mContext.getResources();

        init();
        
        initTitle();

        initScrollView();

        initChild();
        
    }
    
    private void init() {
    	mDensity = GameListActivity.SCREEN_DENSITY;
    	if(mDensity <= 0){
    		mDensity = 1;
    	}
    	
    	mMainLayout = new LinearLayout(mContext);
    	mMainLayout.setOrientation(LinearLayout.VERTICAL);
    	mMainLayout.setBackgroundColor(mRes.getColor(R.color.clr_blue_light));
    }
    
    private void initScrollView() {

    	mScrollView = new ScrollView(mContext);
    	mMenuLayout = new LinearLayout(mContext);
    	mMenuLayout.setOrientation(LinearLayout.VERTICAL);
    	mMenuLayout.setGravity(Gravity.CENTER_VERTICAL);
    	mMenuLayout.setBackgroundColor(mRes.getColor(R.color.clr_blue_light));
    	
    	mScrollView.addView(mMenuLayout);
    	
    	RelativeLayout.LayoutParams mainLp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
    	LinearLayout.LayoutParams scrollLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
    	mMainLayout.addView(mScrollView, scrollLp);
    	this.addView(mMainLayout, mainLp);

    }
    
    private void initTitle() {
        
        RelativeLayout titleLayout = new RelativeLayout(mContext);
        titleLayout.setBackgroundColor(mRes.getColor(R.color.clr_blue_light_1));
        
        RelativeLayout.LayoutParams imgLp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        imgLp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        imgLp.addRule(RelativeLayout.ALIGN_PARENT_LEFT); 
        ImageView mImg = new ImageView(mContext);
        mImg.setImageResource(R.drawable.logo_title);
        mImg.setLayoutParams(imgLp);
        mImg.setScaleType(ScaleType.FIT_START);
        titleLayout.addView(mImg,imgLp);
        mMainLayout.addView(titleLayout, imgLp);
    }
    
    private void initChild() {

    	String[] titles = mRes.getStringArray(R.array.sliding_title);
    	TypedArray imgs = mRes.obtainTypedArray(R.array.sliding_img);
    	TypedArray ids = mRes.obtainTypedArray(R.array.sliding_id);
    	
    	int size = titles.length;
    	for (int i = 0; i < size; i++) {
    		int icon = imgs.getResourceId(i, 0);
    		int id = ids.getResourceId(i, i+1);
    		initView(titles[i], icon, id);
		}
    	
    }
    
    private void initView(String title,int imgId, int id) {
    	int height = Utils.dip2px(mContext, 60);
    	LinearLayout.LayoutParams childLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
        LinearLayout childLayout = new LinearLayout(mContext);
        childLayout.setBackgroundResource(R.drawable.left_item_bg);
        childLayout.setLayoutParams(childLp);
        childLayout.setId(id);
        childLayout.setFocusable(true);
        
        childLayout.setOnClickListener(mOnClickListener);
        
        int size = Utils.dip2px(mContext, 36);
        LinearLayout.LayoutParams imgLp = new LinearLayout.LayoutParams(size, size);
        imgLp.setMargins(30, 8, 30, 8);
        imgLp.gravity = Gravity.CENTER_VERTICAL;
        
        ImageView modeImg = new ImageView(mContext);
        modeImg.setLayoutParams(imgLp);
        modeImg.setImageResource(imgId);
        childLayout.addView(modeImg);
        
        int color = mContext.getResources().getColor(R.color.clr_left_title);
        LinearLayout.LayoutParams tvLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tvLp.gravity = Gravity.CENTER;
        TextView tv = new TextView(mContext);
        tvLp.setMargins(20,12,12,12);
        tv.setText(title);
        tv.setTextSize(20);
        tv.setTextColor(color);
        tv.setLayoutParams(tvLp);
        childLayout.addView(tv);
        
        mMenuLayout.addView(childLayout);
        addLine(2);
    }
    
    public void addLine(int lineHeight) {
    	LinearLayout.LayoutParams viewLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, lineHeight);
        View view = new View(mContext);
        view.setLayoutParams(viewLp);
        view.setBackgroundColor(mRes.getColor(R.color.clr_left_line_bg));
        mMenuLayout.addView(view);
    }
    
    private OnClickListener mOnClickListener = new OnClickListener() {
        
        @Override
        public void onClick(View view) {
        	if(null == mListener) {
        		Toast.makeText(mContext, "mListener = null", Toast.LENGTH_SHORT).show();
        		return;
        	}
        	mContext.hideMenu();
            int id = view.getId();
            switch (id) {
            case 3456789:
            	break;
            case 1:
            	mListener.doSearch();
                break;
            case R.id.sliding_save:
            	mListener.doSaveFile();
                break;
            case 3:
            	mListener.doRecommend();
            	break;
            case R.id.sliding_setting:
            	mListener.doSettings();
                break;
            case R.id.sliding_about:
            	mListener.doAbout();
            	break;
            case R.id.sliding_exit: 
            	mListener.doExit();
            	break;
            default:
                break;
            }
        }
    };
    
    private OnMenuListener mListener = null;
    
    public void setOnMenuListener(OnMenuListener listener) {
    	this.mListener = listener;
    }
}
