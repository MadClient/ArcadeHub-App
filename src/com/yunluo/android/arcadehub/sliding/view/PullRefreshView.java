package com.yunluo.android.arcadehub.sliding.view;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;

import com.yunluo.android.arcadehub.R;
import com.yunluo.android.arcadehub.utils.SharePreferenceUtil;

public class PullRefreshView extends LinearLayout {

	enum Status {
		NORMAL, DRAGGING, REFRESHING,
	}

	private Status status = Status.NORMAL;
	private final static float MIN_MOVE_DISTANCE = 5.0f;

	private int mRefreshTargetTop = -50;
	private int mLastY;
	private Scroller mScroller = null;
	private View mRefreshView = null;
	private ImageView mRefreshIndicatorView = null;
	private ImageView mRefreshImg = null;
	private ProgressBar mProgressBar = null;
	private TextView mContentTv = null;
	private TextView mDepthTv = null;
	private TextView mTimeTv = null;
	private RefreshListener mRefreshListener = null;
	private PullScrollListener mPullScrollListener = null;
	private Context mContext = null;
	private Resources mRes = null;
	
	private static int COUNT = 0;
	
	private SimpleDateFormat mSdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			
			int what = msg.what;
			if(123 == what) {
				LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mRefreshView.getLayoutParams();
				int i = lp.topMargin;
				mScroller.startScroll(0, i, 0, mRefreshTargetTop);
				invalidate();
				status = Status.NORMAL;

			}
		};
	};

	public PullRefreshView(Context context) {
		super(context);
		this.mContext = context;
		init();
	}

	public PullRefreshView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init();
	}

	private void init() {
		this.setOrientation(LinearLayout.VERTICAL);
		
		mRes = mContext.getResources();
		
		mRefreshTargetTop = getPixelByDip(mContext, mRefreshTargetTop);
		mScroller = new Scroller(mContext);
		mRefreshView = LayoutInflater.from(mContext).inflate(R.layout.pull_refresh_layout, null);
		mRefreshIndicatorView = (ImageView) mRefreshView.findViewById(R.id.pull_indicator);
		mProgressBar = (ProgressBar) mRefreshView.findViewById(R.id.pull_progress);
		mContentTv = (TextView) mRefreshView.findViewById(R.id.pull_tv_refresh_title);
		mDepthTv = (TextView) mRefreshView.findViewById(R.id.pull_tv_refresh_depth);
		mTimeTv = (TextView) mRefreshView.findViewById(R.id.pull_tv_refresh_time);   
		mRefreshImg = (ImageView) mRefreshView.findViewById(R.id.pull_img_finish);
		LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, -mRefreshTargetTop);
		lp.topMargin = mRefreshTargetTop;
		lp.gravity = Gravity.CENTER;
		addView(mRefreshView, lp);
	}
	
	public void setDepthTxt(String txt) {
	}
	
	public static final int TYPE_END = -1;
	public static final int TYPE_START = -2;

	public void setRefreshText(int cur, int total) {
	    String target = mRes.getString(R.string.MSG_REFRESH_COUNT);
	    switch(cur) {
	    case TYPE_END:
	        cur = COUNT;
	        break;
	    case TYPE_START:
	        cur = 0;
	        break;
	    }
	    target = String.format(target, cur, total);
	    mTimeTv.setText(target);
	    COUNT = cur;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		if (status == Status.REFRESHING)
			return false;

		int y = (int) event.getRawY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLastY = y;
			break;
		case MotionEvent.ACTION_MOVE:
			int m = y - mLastY;
			doMovement(m);
			this.mLastY = y;
			break;
		case MotionEvent.ACTION_UP:
			fling();
			break;
		}
		return true;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent e) {
		int action = e.getAction();
		int y = (int) e.getRawY();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mLastY = y;
			break;
		case MotionEvent.ACTION_MOVE:
			int m = y - mLastY;
			this.mLastY = y;
			if (m > MIN_MOVE_DISTANCE && canScroll()) {
				return true;
			}
			break;
		case MotionEvent.ACTION_UP:
			break;
		case MotionEvent.ACTION_CANCEL:
			break;
		}
		return super.onInterceptTouchEvent(e);
	}

	private void fling() {
		LinearLayout.LayoutParams lp = (LayoutParams) mRefreshView
				.getLayoutParams();

		if (lp.topMargin > 0) {
			status = Status.REFRESHING;
			refresh();
		} else {
			status = Status.NORMAL;
			returnInitState();
		}
	}

	public void firstRefresh() {
	    if(null == mRefreshView) {
	        return;
	    }
	    LinearLayout.LayoutParams lp = (LayoutParams) mRefreshView.getLayoutParams();
        lp.topMargin = 50;
        mRefreshView.setLayoutParams(lp);
        
	    status = Status.REFRESHING;
        refresh();
	}
	
	private void returnInitState() {
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.mRefreshView.getLayoutParams();
		int i = lp.topMargin;
		mScroller.startScroll(0, i, 0, mRefreshTargetTop);
		invalidate();
	}

	private void refresh() {
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.mRefreshView.getLayoutParams();
		int i = lp.topMargin;
		mRefreshIndicatorView.setVisibility(View.GONE);
		mProgressBar.setVisibility(View.VISIBLE);
		mRefreshImg.setVisibility(View.GONE);
		mContentTv.setText(mRes.getString(R.string.PULL_REFRESHING));
		mScroller.startScroll(0, i, 0, 0 - i);
		invalidate();
		if (mRefreshListener != null) {
			mRefreshListener.onRefresh(this);
		}
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			int i = this.mScroller.getCurrY();
			LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.mRefreshView.getLayoutParams();
			int k = Math.max(i, mRefreshTargetTop);
			lp.topMargin = k;
			this.mRefreshView.setLayoutParams(lp);
			postInvalidate();
		}
	}

	private void doMovement(int moveY) {
		status = Status.DRAGGING;
		LinearLayout.LayoutParams lp = (LayoutParams) mRefreshView.getLayoutParams();
		float f1 = lp.topMargin;
		float f2 = moveY * 0.3F;
		int i = (int) (f1 + f2);
		lp.topMargin = i;
		mRefreshView.setLayoutParams(lp);
		mRefreshView.invalidate();
		invalidate();

		mContentTv.setVisibility(View.VISIBLE);
		mRefreshImg.setVisibility(View.GONE);
		mRefreshIndicatorView.setVisibility(View.VISIBLE);
		mProgressBar.setVisibility(View.GONE);
		if (lp.topMargin > 0) {
			mContentTv.setText(mRes.getString(R.string.MSG_REFRESH_RELEASE));
			mRefreshIndicatorView.setImageResource(R.drawable.pull_refresh_up);
		} else {
			mContentTv.setText(mRes.getString(R.string.PULL_DOWN_REFRESH));
			mRefreshIndicatorView.setImageResource(R.drawable.pull_refresh_down);
		}

	}

	public void finishRefresh() {
		if(status == Status.NORMAL) {
			return;
		}
		
		mContentTv.setVisibility(View.VISIBLE);
		mRefreshImg.setVisibility(View.VISIBLE);
		
		mContentTv.setText(mRes.getString(R.string.MSG_REFRESH_FINISH));
		
		mProgressBar.setVisibility(GONE);
		
		String time = mSdf.format(new Date());
		SharePreferenceUtil.saveRefreshTime(mContext, time);
		
		Message msg = new Message();
		msg.what = 123;
		mHandler.sendMessageDelayed(msg, 1000);
	}

	private boolean canScroll() {
		View childView;
		if (getChildCount() > 1) {
			childView = this.getChildAt(1);
			if (childView instanceof ListView) {
				int top = ((ListView) childView).getChildAt(0).getTop();
				int pad = ((ListView) childView).getListPaddingTop();
				if ((Math.abs(top - pad)) < 3 && ((ListView) childView).getFirstVisiblePosition() == 0) {
					if(null != mPullScrollListener) {
						mPullScrollListener.onPullScroll();
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
					}
					return true;
				} else {
					return false;
				}
			} else if (childView instanceof ScrollView) {
				if (((ScrollView) childView).getScrollY() == 0) {
					if(null != mPullScrollListener) {
						mPullScrollListener.onPullScroll();
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
					}
					return true;
				} else {
					return false;
				}
			}

		}
		return false;
	}

	private int getPixelByDip(Context c, int pix) {
		float f1 = c.getResources().getDisplayMetrics().density;
		float f2 = pix;
		return (int) (f1 * f2 + 0.5F);
	}

	public interface RefreshListener {
		public void onRefresh(PullRefreshView view);
	}
	
	public void setRefreshListener(RefreshListener listener) {
		this.mRefreshListener = listener;
	}
	
	public interface PullScrollListener {
		public void onPullScroll();
	}
	
	public void setPullScrollListener(PullScrollListener listener) {
		this.mPullScrollListener = listener;
	}

}
