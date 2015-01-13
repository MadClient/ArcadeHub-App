/**
 * ArcadeHub
 * @filename: TiltleView.java
 */
package com.yunluo.android.arcadehub.sliding.view;

import com.yunluo.android.arcadehub.GameListActivity;
import com.yunluo.android.arcadehub.R;

import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TitleView extends RelativeLayout {

    private GameListActivity mContext = null;
    private float mDensity = 1;
    
    public TitleView(GameListActivity context, AttributeSet attrs) {
        super(context, attrs);
        int color = context.getResources().getColor(R.color.clr_green_light);
        this.setBackgroundColor(color);
//        this.setBackgroundResource(R.drawable.blue_bg);
        mContext = context;
        
        init();
    }
    
    private void init() {
        mDensity = GameListActivity.SCREEN_DENSITY;
        if(0 >=mDensity ){
            mDensity = 1;
        }
        RelativeLayout.LayoutParams mainLp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int)(50*mDensity));
        
        RelativeLayout mainLayout = new RelativeLayout(mContext);
        mainLayout.setLayoutParams(mainLp);
        
        
        RelativeLayout.LayoutParams imgLp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        
        ImageView img = new ImageView(mContext);
        img.setId(234567);
        imgLp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        imgLp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        imgLp.setMargins(12, 2, 2, 2);
        img.setImageResource(R.drawable.ic_btn_sliding);
        img.setLayoutParams(imgLp);
        img.setOnClickListener(imgListener);
        
        mainLayout.addView(img);
        
        RelativeLayout.LayoutParams titleLp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        titleLp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        
        TextView title = new TextView(mContext);
        title.setGravity(Gravity.CENTER);
        title.setText(mContext.getResources().getString(R.string.MSG_GAMELIST_TITLE));
        title.setTextAppearance(mContext, R.style.all_title_style);
        title.setLayoutParams(titleLp);
        
        this.addView(title);
        this.addView(mainLayout);
    }
    
    boolean boo = false;
    private OnClickListener imgListener = new OnClickListener() {
        
        @Override
        public void onClick(View v) {
        	boo = !boo;
        	if(true == boo) {
        		mContext.showMenu();
        	} else {
        		mContext.hideMenu();
        	}
        	mContext.closeOpenedItems();
        }
    };

}
