package com.yunluo.android.arcadehub.push;

import com.yunluo.android.arcadehub.R;
import com.yunluo.android.arcadehub.download.DownLoadWebView;
import com.yunluo.android.arcadehub.utils.Utils;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PushDialog extends Dialog {

	private Context mContext = null;
	private RelativeLayout mMainLayout = null;
	private DownLoadWebView mWebView = null;
	private RelativeLayout mTxtLayout = null;
	private TextView mTxtTitle = null;
	private TextView mTxtMsg = null;
	
	public PushDialog(Context context, boolean boo) {
//		super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
		super(context, R.style.PushDialog);
		this.mContext = context;

		init();
		if(true == boo) {
			htmlDialog();
		} else {
			txtDialog();
		}
	}

	private void init() {
		mMainLayout  = new RelativeLayout(mContext);
		this.setContentView(mMainLayout );
		this.setCancelable(false);
	}

	/**
	 * 文本
	 */
	private void txtDialog() {
		int width = Utils.dip2px(mContext, 300);
		int height = Utils.dip2px(mContext, 250);
		
		RelativeLayout.LayoutParams childLp = new RelativeLayout.LayoutParams(width, height);
		childLp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		
		mTxtLayout = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.push_txt_dialog, null);
		mTxtLayout.setLayoutParams(childLp);
		
		mMainLayout.addView(mTxtLayout);

		mTxtTitle = (TextView) mTxtLayout.findViewById(R.id.push_txt_title);
		mTxtMsg = (TextView) mTxtLayout.findViewById(R.id.push_txt_msg);
		mTxtImg = (ImageView) mTxtLayout.findViewById(R.id.push_txt_img);
		
		mTxtLayout.findViewById(R.id.push_txt_ok).setOnClickListener(mOnClickListener);
		mTxtLayout.findViewById(R.id.push_txt_cancel).setOnClickListener(mOnClickListener);
	}
	
	/**
	 * 富媒体文本
	 */
	private void htmlDialog() {

		int width = Utils.dip2px(mContext, 320);
		int height = Utils.dip2px(mContext, 250);
		
		RelativeLayout.LayoutParams htmlLp = new RelativeLayout.LayoutParams(width, height);
		htmlLp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		
		RelativeLayout mHtmlLayout = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.push_html_dialog, null);
		mHtmlLayout.setLayoutParams(htmlLp);
		
		mMainLayout.addView(mHtmlLayout);
		
		mWebView = (DownLoadWebView) mHtmlLayout.findViewById(R.id.push_html_webview);
		
		mHtmlLayout.findViewById(R.id.push_html_close).setOnClickListener(mOnClickListener);

	}

	/**
	 * 载入富媒体数据
	 * 
	 * @param summary
	 * @param mimeType
	 * @param encoding
	 */
	public void loadData(String summary, String mimeType, String encoding) {
		if(null != mWebView) {
			mWebView.loadData(summary, "text/html", null);
		}
	}

	/**
	 * 载入网址
	 * @param url
	 */
	public void loadUrl(String url) {
		if(null != mWebView) {
			mWebView.load(url);
		}
	}

	/**
	 * TxtDialog 添加标题
	 * 
	 * @param title
	 */
	public void setTxtTitle(String title) {
		if(null == mTxtTitle) {
			return;
		}
		mTxtTitle.setText(title);
	}
	
	/**
	 * TxtDialog 添加内容
	 * 
	 * @param message
	 */
	public void setTxtMessage(String message) {
		if(null == mTxtMsg) {
			return;
		}
		mTxtMsg.setText(message);
	}
	
	/**
	 * TxtDialog 内容中添加图片
	 * 
	 * @param bmp
	 */
	public void setTxtBitmap(Bitmap bmp) {
		if(null == mTxtImg || null == bmp) {
			return;
		}
		mTxtImg.setImageBitmap(bmp);
	}
	
	@Override
	public void show() {
		super.show();
//		Animation mAnimation = AnimationUtils.loadAnimation(mContext, 0);
//		mMainLayout.startAnimation(mAnimation);
	}
	
	public void exit() {
	    this.dismiss();
//		Animation mAnimation = AnimationUtils.loadAnimation(mContext, 0);
//		mAnimation.setAnimationListener(mExitAnimListener);
//		mMainLayout.startAnimation(mAnimation);
	}
	
	//按键监听
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			int id = v.getId();
			switch(id) {
			case R.id.push_txt_ok:
				if(null != mOnTxtPushListener) {
					mOnTxtPushListener.doOk();
				}
				break;
			case R.id.push_txt_cancel:
				if(null != mOnTxtPushListener) {
					mOnTxtPushListener.doCancel();
				}
				break;
			case R.id.push_html_close:
				if(null != mOnHtmlPushListener) {
					mOnHtmlPushListener.doClose();
				}
				break;
			}
			exit();
		}
	};

	//dialog exit anim
	private AnimationListener mExitAnimListener = new AnimationListener() {

		@Override
		public void onAnimationEnd(Animation animation) {
			dismiss();
		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}

		@Override
		public void onAnimationStart(Animation animation) {

		}

	};

	public interface OnTxtPushListener {
		public void doOk();
		public void doCancel();
	}
	
	public interface OnHtmlPushListener {
		public void doClose();
	}
	
	private OnTxtPushListener mOnTxtPushListener = null;
	public void setOnTxtPushListener(OnTxtPushListener listener) {
		this.mOnTxtPushListener = listener;
	}
	
	private OnHtmlPushListener mOnHtmlPushListener = null;
	private ImageView mTxtImg;
	public void setOnHtmlPushListener(OnHtmlPushListener listener) {
		this.mOnHtmlPushListener = listener;
	}
}
