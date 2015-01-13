package com.yunluo.android.arcadehub.sliding.view;

import java.util.List;
import com.yunluo.android.arcadehub.GameListActivity;
import com.yunluo.android.arcadehub.ad.ADBanner;
import com.yunluo.android.arcadehub.async.RomInfo;

import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

public class RightView extends RelativeLayout implements android.view.View.OnTouchListener {

    private GameListActivity mContext;
    private List<RomInfo> mPluginList = null;
    private TitleView mTitleView;
    private RelativeLayout mHomePage;
    private ListShowView mListShowView;

    public static final int TITLE_ID = 985;
    public static final int HOMEPAGE_ID = 986;
    public static final int BANNER_ID = 987;

    private ADBanner mADBanner;

    public RightView(GameListActivity context, List<RomInfo> pluginList) {
        super(context);
        this.mContext = context;
        this.mPluginList = pluginList;

        this.setOnTouchListener(this);

        this.setFocusable(true);

        initTitle();

        init();

        initList();

        addBanner();
    
    }

    private void addBanner() {
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        mADBanner = new ADBanner(mContext, null);
        mADBanner.setBackgroundColor(0x00000000);
        mADBanner.setId(BANNER_ID);
        mADBanner.setLayoutParams(lp);
        this.addView(mADBanner);
    }

    public void clear() {
        if(null != mADBanner) {
            mADBanner.clearThread();
        }
    }
    
    private void init() {
        RelativeLayout.LayoutParams wsLp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);

        mHomePage = new RelativeLayout(mContext);
        wsLp.addRule(RelativeLayout.BELOW, TITLE_ID); 
        mHomePage.setLayoutParams(wsLp);
        mHomePage.setId(HOMEPAGE_ID);
        mHomePage.setBackgroundColor(Color.WHITE);
        this.addView(mHomePage);

    }

    private void initTitle() {
        mTitleView = new TitleView(mContext, null);
        mTitleView.setId(TITLE_ID);
        this.addView(mTitleView);
    }

    private void initList() {
        RelativeLayout.LayoutParams mListSVLp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        mListShowView = new ListShowView(mContext, mPluginList);
        mHomePage.addView(mListShowView, mListSVLp);
    }

    public ListShowView getListShowView() {

        return mListShowView;

    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.View.OnTouchListener#onTouch(android.view.View,
     * android.view.MotionEvent)
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // TODO Auto-generated method stub
        return false;
    }

}