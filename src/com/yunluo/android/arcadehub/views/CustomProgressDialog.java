package com.yunluo.android.arcadehub.views;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

public class CustomProgressDialog extends ProgressDialog {

	private Activity mActivity;
	public CustomProgressDialog(Context context) {
		super(context);
		mActivity = (Activity) context;
	}
	
	@Override
	public void dismiss() {
		if(null != mActivity && false == mActivity.isFinishing()) {
			super.dismiss();
		}
	}
	

}
