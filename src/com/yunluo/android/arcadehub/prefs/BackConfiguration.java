package com.yunluo.android.arcadehub.prefs;

import com.yunluo.android.arcadehub.Emulator;
import com.yunluo.android.arcadehub.GameListActivity;
import com.yunluo.android.arcadehub.R;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

public class BackConfiguration extends LinearLayout{

    public BackConfiguration(Context context) {
        super(context);
        init(context);
    }
    public BackConfiguration(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    private void init(final Context context) {
        //
        if(null == context){
            return;
        }
        LinearLayout.LayoutParams mTitleLp = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int)(50*GameListActivity.SCREEN_DENSITY));
        TextView mTitle = new TextView(context);
        mTitle.setText(context.getResources().getString(R.string.LISTITEM_MENU_SETTING));
        mTitle.setGravity(Gravity.CENTER);
        mTitle.setBackgroundResource(R.drawable.app_title_bg);
        mTitle.setTextAppearance(context, R.style.all_title_style);
        mTitle.setLayoutParams(mTitleLp);
        
        RelativeLayout.LayoutParams imglayoutLp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int)(50*GameListActivity.SCREEN_DENSITY));
        RelativeLayout titlelayout = new RelativeLayout(context);
        titlelayout.setLayoutParams(imglayoutLp);
        RelativeLayout.LayoutParams imgLp = new RelativeLayout.LayoutParams((int)(32*GameListActivity.SCREEN_DENSITY), RelativeLayout.LayoutParams.WRAP_CONTENT);
        ImageView imgback = new ImageView(context);
        imgback.setImageResource(R.drawable.ic_back);
        imgback.setBackgroundColor(0x00000000);
        imgback.setScaleType(ScaleType.FIT_CENTER);
        int i =(int)(9*GameListActivity.SCREEN_DENSITY);
        imgLp.setMargins( i, i, i, i);
        imgback.setLayoutParams(imgLp);
       
        imgback.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Emulator.resume();
                ((Activity)context).finish();
            }
            
        });
        titlelayout.addView(mTitle);
        titlelayout.addView(imgback,imgLp);
        
        this.addView(titlelayout);

    }    
}