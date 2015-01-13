
package com.yunluo.android.arcadehub.cheat;

import java.util.ArrayList;
import com.yunluo.android.arcadehub.Emulator;
import com.yunluo.android.arcadehub.GameListActivity;
import com.yunluo.android.arcadehub.R;
import com.yunluo.android.arcadehub.ad.ADBanner;
import com.yunluo.android.arcadehub.data.CheatListData;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CheatDialog extends RelativeLayout {

	private CheatActivity mContext = null;

    private Resources res = null;
    
    private final int AD_ID = 888;

    public ArrayList<CheatObject> mCheatList = null;

    private CheatAdapter mAdapter;

    private ListView mListView;
    
    private ADBanner mADBanner = null;
    
    public CheatDialog(CheatActivity context) {
    	super(context);
        this.mContext = context;
        res = mContext.getResources();

        init();
        
        addBanner();
    }

    public ADBanner getADBanner() {
        return mADBanner;
    }
    
    private void init() {
    	mCheatList = CheatListData.getInstance().getCheatList();
        mAdapter = new CheatAdapter(mContext, mCheatList);
        int color = mContext.getResources().getColor(R.color.transparent_half);
        this.setBackgroundColor(color);

        LinearLayout.LayoutParams mTitleLp = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int)(50*GameListActivity.SCREEN_DENSITY));
		TextView mTitle = new TextView(mContext);
		mTitle.setText(res.getString(R.string.BTN_OPTIONS_GOLDFINGER));
		mTitle.setGravity(Gravity.CENTER);
		mTitle.setBackgroundResource(R.drawable.app_title_bg);
		mTitle.setTextAppearance(mContext, R.style.all_title_style);
		mTitle.setLayoutParams(mTitleLp);
		mTitle.setId(789);
		this.addView(mTitle);
		
		
		LinearLayout.LayoutParams imglayoutLp = new LinearLayout.LayoutParams((int)(50*GameListActivity.SCREEN_DENSITY), (int)(50*GameListActivity.SCREEN_DENSITY));
		LinearLayout imglayout = new LinearLayout(mContext);
        imglayout.setLayoutParams(imglayoutLp);
        LinearLayout.LayoutParams imgLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		ImageView imgback = new ImageView(mContext);
		imgback.setImageResource(R.drawable.ic_back);
		imgback.setBackgroundColor(0x00000000);
		imgback.setScaleType(ScaleType.FIT_CENTER);
		int i =(int)(9*GameListActivity.SCREEN_DENSITY);
        imgLp.setMargins( i, i, i, i);
		imgback.setLayoutParams(imgLp);
		imgback.setOnClickListener(mOnClickListener);
		imglayout.addView(imgback);
		this.addView(imglayout);
		
        RelativeLayout.LayoutParams lvLp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        // init listview
        mListView = new ListView(mContext);
        lvLp.addRule(RelativeLayout.BELOW, 789);
        lvLp.addRule(RelativeLayout.ABOVE, AD_ID);
        mListView.setDividerHeight(2);
        mListView.setCacheColorHint(00000000);
        mListView.setBackgroundColor(Color.TRANSPARENT);
        mListView.setAdapter(mAdapter);
        mListView.setItemsCanFocus(true);
        mListView.setLayoutParams(lvLp);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mListView.setOnItemClickListener(mOnItemClickListener);
        this.addView(mListView);
    }
      
    /**
     * add date
     * @param obj
     */
    public void add(CheatObject obj) {
    	if(null != mCheatList) {
    		mCheatList.add(obj);
    		update();
    	}
    }

    /**
     * update date
     */
    private void update() {
        if (null != mAdapter) {
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * setting select date
     */
    public void updateData() {
        if (null != mAdapter) {
            mAdapter.updateData();
        }
    }
    
    /**
     * clean cheat option item
     */
    public void clear() {
    	if (null != mAdapter) {
            mAdapter.clear();
        }
    }
    
    // listener
    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

            CheckBox cb = (CheckBox)arg1.findViewById(R.id.cheat_item_check);
            cb.toggle();
            boolean boo = cb.isChecked();
            mAdapter.selectedItems.put(arg2, boo);
            int id = mCheatList.get(arg2).getId();
            if(true == boo) {
            	Emulator.openCheatItem(id);
            } else {
            	Emulator.closeCheatItem(id);
            }
         }

    };
    
    //back 
    OnClickListener mOnClickListener = new OnClickListener(){

        @Override
        public void onClick(View v) {
            Emulator.resume();
            mContext.finish();
        }
        
    };

}
