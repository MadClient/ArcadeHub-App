package com.yunluo.android.arcadehub.sliding;

import android.os.Bundle;
import android.support.v4.app.ListFragment;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnClosedListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenedListener;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.yunluo.android.arcadehub.R;
import com.yunluo.android.arcadehub.utils.ContentValue;

public class BaseActivity extends SlidingFragmentActivity {

	protected ListFragment mFrag;
    private SlidingMenu mSlidingMenu;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mSlidingMenu = getSlidingMenu();
		mSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
		mSlidingMenu.setShadowDrawable(R.drawable.shadow);
		mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		mSlidingMenu.setFadeDegree(0.35f);
		mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		getSupportActionBar().hide();
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		mSlidingMenu.setOnClosedListener(closedListener);
		mSlidingMenu.setOnOpenedListener(openedListener);
	}
	
	private OnClosedListener closedListener = new OnClosedListener() {

		@Override
		public void onClosed() {
			slidingDisable();
		}
		
	};
	
	private OnOpenedListener openedListener = new OnOpenedListener() {

		@Override
		public void onOpened() {
			slidingEnable();
		}
		
	};
	public void showMenu() {
	    mSlidingMenu.showMenu();
	}
	
	public void hideMenu() {
	    toggle();
	}
	
	public void slidingEnable() {
	    mSlidingMenu.setSlidingEnabled(true);
	}

	public void slidingDisable() {
	    mSlidingMenu.setSlidingEnabled(false);
	}
	
	public void setTouchMode(int position) {
	    switch (position) {
        case 0:
            getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
            break;
        default:
            getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
            break;
        }
	}
}
