package com.yunluo.android.arcadehub.popup;

import com.yunluo.android.arcadehub.Emulator;
import com.yunluo.android.arcadehub.GamePlayActivity;
import com.yunluo.android.arcadehub.R;
import com.yunluo.android.arcadehub.input.InputHandler;
import com.yunluo.android.arcadehub.interfac.OnOptionListener;
import com.yunluo.android.arcadehub.utils.Debug;
import com.yunluo.android.arcadehub.utils.Utils;
import com.yunluo.android.arcadehub.views.OptionView;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.Gravity;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

public class OptionPopup {
	
	private GamePlayActivity mContext = null;
	private PopupWindow mPopup = null;
	private OptionView mVerOptionView = null;  
	private OptionView mHorOptionView = null;
	private boolean isShowAd = false;

	public OptionPopup(GamePlayActivity context) {
		mContext = context;
		init();
	}

	@SuppressWarnings("deprecation")
	private void init() {
		mHorOptionView = new OptionView(this, mContext, false);
		mVerOptionView = new OptionView(this, mContext, true);
		mPopup = new PopupWindow(mVerOptionView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		mPopup.setFocusable(true);
		mPopup.setBackgroundDrawable(new BitmapDrawable());
		mPopup.setOutsideTouchable(true);
		mPopup.setAnimationStyle(R.style.PopupAnimation);
		mPopup.setOnDismissListener(mOnDismissListener);
		ColorDrawable dw = new ColorDrawable(-00000);
		mPopup.setBackgroundDrawable(dw);
		mPopup.update();
	}

	private OnDismissListener mOnDismissListener = new OnDismissListener() {

		@Override
		public void onDismiss() {
			deleteAd();
			Emulator.resume();
		}

	};
	
	public boolean isShown() {
		if(null == mPopup) {
			return false;
		}
		return mPopup.isShowing();
	}

	public void show() {
	    addAd();
		if(true == Utils.isPortrait(mContext)) {
			horizontal();
		} else {
			vertical();
		}
	}
	
	public void dismiss() {
		if(null != mPopup) {
			if(true == mPopup.isShowing()) {
			    deleteAd();
				mPopup.dismiss();
			}
		}
	}

	public void change() {
		dismiss();
		show();
	}
	
	private void vertical() {
		if(null == mPopup) {
			return;
		}
		mPopup.setWidth(LayoutParams.WRAP_CONTENT);
		mPopup.setHeight(LayoutParams.WRAP_CONTENT);
		mPopup.setContentView(mVerOptionView);
		mPopup.setAnimationStyle(R.style.VerticalPopupAnim);
		mVerOptionView.update();
		if(false == mPopup.isShowing()) {
			mPopup.showAtLocation(mVerOptionView, Gravity.LEFT|Gravity.TOP, 0, 0);
		}
	}
	
	private void horizontal() {
		if(null == mPopup) {
			return;
		}
		mPopup.setWidth(LayoutParams.MATCH_PARENT);
		mPopup.setHeight(LayoutParams.WRAP_CONTENT);
		mPopup.setContentView(mHorOptionView);
		mPopup.setAnimationStyle(R.style.PopupAnimation);
		mHorOptionView.update();
		if(false == mPopup.isShowing()) {
			mPopup.showAtLocation(mHorOptionView, Gravity.LEFT|Gravity.TOP, 0, 0);
		}
	}
	
	public void setOnOptionListener(OnOptionListener listener) {
		if(null != mVerOptionView) {
			mVerOptionView.setOnOptionListener(listener);
		}
		if(null != mHorOptionView) {
			mHorOptionView.setOnOptionListener(listener);
		}
	}

}
