
package com.yunluo.android.arcadehub.save;

import java.util.ArrayList;
import java.util.List;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView.ScaleType;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.yunluo.android.arcadehub.Emulator;
import com.yunluo.android.arcadehub.GameListActivity;
import com.yunluo.android.arcadehub.R;
import com.yunluo.android.arcadehub.utils.ContentValue;
import com.yunluo.android.arcadehub.utils.FileUtil;
import com.yunluo.android.arcadehub.views.EmptyViewForListView;

public class ArchiveView extends LinearLayout {

    private ArchiveActivity mContext; 
    private List<ArchiveObj> mFileList = new ArrayList<ArchiveObj>();
    private ArchiveSubAdapter mAdapter = null;
    private Resources res = null;

    public ArchiveView(ArchiveActivity context) {
    	super(context);
        this.mContext = context;
        res = mContext.getResources();
        initDialog();
    }

    // show address list
    public void initDialog() {
        String path = ContentValue.STA_PATH;
        if (null != mFileList) {
            mFileList.clear();
        }
        FileUtil.staTraverse(mFileList, path);

        mAdapter = new ArchiveSubAdapter(mContext, mFileList);
        this.setOrientation(LinearLayout.VERTICAL);
        int color = mContext.getResources().getColor(R.color.transparent_half);
        this.setBackgroundColor(color);
        
        LinearLayout.LayoutParams mTitleLp = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int)(50*GameListActivity.SCREEN_DENSITY));
		TextView mTitle = new TextView(mContext);
		mTitle.setText(res.getString(R.string.BTN_OPTIONS_UNARCHIVE));
		mTitle.setGravity(Gravity.CENTER);
		mTitle.setBackgroundResource(R.drawable.app_title_bg);
		mTitle.setTextAppearance(mContext,  R.style.all_title_style);
		mTitle.setLayoutParams(mTitleLp);
		
		RelativeLayout.LayoutParams imglayoutLp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int)(50*GameListActivity.SCREEN_DENSITY));
		RelativeLayout titlelayout = new RelativeLayout(mContext);
        titlelayout.setLayoutParams(imglayoutLp);
        RelativeLayout.LayoutParams imgLp = new RelativeLayout.LayoutParams((int)(32*GameListActivity.SCREEN_DENSITY), RelativeLayout.LayoutParams.WRAP_CONTENT);
        ImageView imgback = new ImageView(mContext);
        imgback.setImageResource(R.drawable.ic_back);
        imgback.setBackgroundColor(0x00000000);
        imgback.setScaleType(ScaleType.FIT_CENTER);
        int i =(int)(9*GameListActivity.SCREEN_DENSITY);
        imgLp.setMargins( i, i, i, i);
        imgback.setLayoutParams(imgLp);
        imgback.setOnClickListener(mOnClickListener);
        titlelayout.addView(mTitle);
        titlelayout.addView(imgback,imgLp);
        
        this.addView(titlelayout);
        
        LinearLayout.LayoutParams mLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        ListView mListView = new ListView(mContext);
        mListView.setCacheColorHint(0x00000000);
        mListView.setBackgroundColor(Color.TRANSPARENT);
        mListView.setLayoutParams(mLp);
        this.addView(mListView);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(mOnItemClickListener);
        addEmpytView(mListView);
    }
    
	private void addEmpytView(ListView listView) {
		EmptyViewForListView emptyView = new EmptyViewForListView(mContext, false);  
		emptyView.setText(res.getString(R.string.MSG_RELOAD_NO_ARCHIVE_LOADED));
		emptyView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));  
		emptyView.setVisibility(View.GONE);  
		emptyView.setGravity(Gravity.CENTER);
		((ViewGroup)listView.getParent()).addView(emptyView);  
		listView.setEmptyView(emptyView); 
	}

    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            String romName = mFileList.get(arg2).getName();
            mContext.showArchiveSubView(romName);
        }

    };
    
    OnClickListener mOnClickListener = new OnClickListener(){

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Emulator.resume();
            mContext.finish();
        }
        
    };

    
}
