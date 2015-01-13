
package com.yunluo.android.arcadehub.download;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.yunluo.android.arcadehub.GameListActivity;
import com.yunluo.android.arcadehub.R;

public class DownLoadActivity extends Activity {
    private LinearLayout mMainLayout;
    
    private Resources res = null;
    
    private List<DownLoadObj> mList = new ArrayList<DownLoadObj>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        res = this.getResources();
        
        mMainLayout = new LinearLayout(this);
        mMainLayout.setOrientation(LinearLayout.VERTICAL);
        mMainLayout.setBackgroundColor(Color.WHITE);
        LinearLayout.LayoutParams mLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        setContentView(mMainLayout,mLayout);
        
        init();
        initTitle();
        initListView();
    }
    
    private void init() {
    	String[] titles = this.getResources().getStringArray(R.array.dl_url_name);
    	String[] urls = this.getResources().getStringArray(R.array.dl_url_item);
    	
    	int length = titles.length;
    	for (int i = 0; i < length; i++) {
			DownLoadObj obj = new DownLoadObj();
			obj.setTitle(titles[i]);
			obj.setUrl(urls[i]);
			mList.add(obj);
		}
    }

    private void initTitle() {
    	LinearLayout.LayoutParams mTitleLp = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int)(50*GameListActivity.SCREEN_DENSITY));
		TextView mTitle = new TextView(this);
		mTitle.setText(res.getString(R.string.download_title));
		mTitle.setTextSize(16);
		mTitle.setGravity(Gravity.CENTER);
		mTitle.setBackgroundResource(R.drawable.app_title_bg);
		mTitle.setLayoutParams(mTitleLp);
		
		
		mMainLayout.addView(mTitle);
    }
    
    private void initListView() {
    	DownLoadAdapter mAdapter = new DownLoadAdapter(this, mList);
    	ListView mListView = new ListView(this);
    	mListView.setCacheColorHint(0x0000);
    	mListView.setOnItemClickListener(mOnItemClickListener);
    	mListView.setAdapter(mAdapter);
    	mMainLayout.addView(mListView);
    }
    
    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			DownLoadObj obj = mList.get(arg2);
			if(null != obj) {
				showSearch(0, obj.getUrl());
			}
		}
    };
    
    private void showSearch(int type, String url) {
    	Bundle bun = new Bundle();
    	bun.putString("URL", url);
    	bun.putInt("TYPE", type);
    	
    	Intent intent = new Intent(DownLoadActivity.this, SearchActivity.class);
    	intent.putExtras(bun);
    	startActivity(intent);
    	overridePendingTransition(R.anim.activity_enter_action, R.anim.activity_exit_action);
    }
    
    
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        finish();
    }

}
