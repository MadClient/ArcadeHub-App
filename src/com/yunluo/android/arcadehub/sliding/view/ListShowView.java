package com.yunluo.android.arcadehub.sliding.view;

import java.util.List;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.FrameLayout.LayoutParams;

import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.yunluo.android.arcadehub.BaseApplication;
import com.yunluo.android.arcadehub.Emulator;
import com.yunluo.android.arcadehub.GameListActivity;
import com.yunluo.android.arcadehub.R;
import com.yunluo.android.arcadehub.async.RomInfo;
import com.yunluo.android.arcadehub.async.UpdateAnsyncTask;
import com.yunluo.android.arcadehub.data.RomListData;
import com.yunluo.android.arcadehub.interfac.OnObserverListener;
import com.yunluo.android.arcadehub.sliding.adapter.PluginListAdapter;
import com.yunluo.android.arcadehub.sliding.view.PullRefreshView.PullScrollListener;
import com.yunluo.android.arcadehub.sliding.view.PullRefreshView.RefreshListener;
import com.yunluo.android.arcadehub.utils.Debug;
import com.yunluo.android.arcadehub.utils.Utils;
import com.yunluo.android.arcadehub.views.EmptyViewForListView;

/**
 * @classname: ListShowView
 * @author: siyadong
 */
public class ListShowView extends RelativeLayout implements RefreshListener, PullScrollListener {

	public static String TAG = "ListShowView";
	public static boolean STATE = true;

	public static final int SEARCH_FINISH = 123;
	
	private PullRefreshView mPullRefreshView = null;
	private GameListActivity mContext = null;
	private List<RomInfo> mPluginList = null;
	private SwipeListView mListView = null;    
	private PluginListAdapter mListAdapter = null; 
	private int mWidth = 0; 
	private RomListData mRomListData = null;
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			int arg1 = msg.arg1;
			switch(arg1) {
			case SEARCH_FINISH:
				finishRefresh();
				break;
			}
		};
	};
	
	public void finishRefresh() {
		if(null != mPullRefreshView) {
			mPullRefreshView.finishRefresh();
			Toast.makeText(mContext, mContext.getResources().getString(R.string.MSG_REFRESH_FINISH), Toast.LENGTH_SHORT).show();
		}
		
		if(null != mPluginList) {
		    mOnObserverListener.doObserver(-1, mPluginList.size());
		}
		
		if(null != mRomListData) {
		    mRomListData.reset();
		}
	} 

	public void firstRefresh() {
	   if(null != mPullRefreshView) {
	       mPullRefreshView.firstRefresh();
	   } 
	}
	
	public ListShowView(GameListActivity context, List<RomInfo> pluginList) {
		super(context, null);
		mContext = context;
		mPluginList = pluginList;

		if(null == mPluginList) {
			return;
		}
		
		BaseApplication mApp = (BaseApplication) context.getApplication();
		if(null != mApp) {
		    mApp.setOnObserverListener(mOnObserverListener);
		    mRomListData = mApp.getRomListData();
		}

		Debug.e(TAG, " mPluginList.size: " + mPluginList.size());
		this.setVisibility(View.VISIBLE);
		mWidth = GameListActivity.SCREEN_WIDTH;
		
		initList();
	}
	
	public ListShowView(GameListActivity context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private void initList() {

		mPullRefreshView = new PullRefreshView(mContext);
		mPullRefreshView.setRefreshListener(this);
		mPullRefreshView.setPullScrollListener(this);
		int color = this.getResources().getColor(R.color.clr_pull_bg);
		mPullRefreshView.setBackgroundColor(color);
		
		mListAdapter = new PluginListAdapter(mContext, mPluginList);
		mListView = new SwipeListView(mContext,R.id.plugin_list_game_bottom, R.id.plugin_list_game_top);// new ListView(mContext);
		mListView.setId(123);
		mListView.setDivider(this.getResources().getDrawable(R.drawable.plugin_list_divider));
		mListView.setDividerHeight(2);
		mListView.setCacheColorHint(0x0000);
		
		addFooterView();
		
		mListView.setAdapter(mListAdapter);
		RelativeLayout.LayoutParams listLp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
		
		RelativeLayout.LayoutParams pullLp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);

		mPullRefreshView.addView(mListView, listLp);
		this.addView(mPullRefreshView, pullLp);
		
		mListAdapter.setListView(mListView);
		

		RelativeLayout.LayoutParams viewLp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, 2);
		View view = new View(mContext);
		view.setLayoutParams(viewLp);
		viewLp.addRule(RelativeLayout.BELOW, 123);
		view.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.plugin_list_divider));
		this.addView(view);
		mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		}

		((SwipeListView) mListView).setSwipeListViewListener(new BaseSwipeListViewListener() {
			@Override
			public void onOpened(int position, boolean toRight) {
				if(null != mListAdapter) {
					mListAdapter.setClicked(position, toRight);
				}
			}

			@Override
			public void onClosed(int position, boolean fromRight) {
				Debug.d("onClosed","position"+fromRight);
			}

			@Override
			public void onListChanged() {
			}

			@Override
			public void onMove(int position, float x) {
				Debug.d("swipe", String.format("onMove"+position+"="+x, position, x));
			}

			@Override
			public void onStartOpen(int position, int action, boolean right) {
				Debug.d("swipe", String.format("onStartOpen %d - action %d", position, action));
				Emulator.stopScan();
			}

			@Override
			public void onStartClose(int position, boolean right) {
				Debug.d("swipe", String.format("onStartClose %d", position));
			}

			@Override
			public void onClickFrontView(int position) {
				Debug.d("swipe", String.format("onClickFrontView %d", position));
			}

			@Override
			public void onClickBackView(int position) {
				Debug.d("swipe", String.format("onClickBackView %d", position));
			}

			@Override
			public void onDismiss(int[] reverseSortedPositions) {
			}

		});
		
		reload(); 
		
		addEmpytView();

	}

	private void reload() {
		int mleft = mWidth*4/5;
		mListView.setSwipeMode(SwipeListView.SWIPE_MODE_BOTH);
		mListView.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_REVEAL);
		mListView.setSwipeActionRight(SwipeListView.SWIPE_ACTION_REVEAL);
		mListView.setOffsetLeft(mleft);
		mListView.setOffsetRight(mWidth/5);
		mListView.setAnimationTime(0);
		mListView.setSwipeOpenOnLongPress(true);
	}

	private void addFooterView() {
	    View mView = View.inflate(mContext, R.layout.list_footer_layout, null);
	    mListView.addFooterView(mView,null,false);
	}

	EmptyViewForListView emptyView = null;
	private void addEmpytView() {
		emptyView = new EmptyViewForListView(mContext, true);  
		RelativeLayout.LayoutParams mTvImgLp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		emptyView.setLayoutParams(mTvImgLp);  
		emptyView.setVisibility(View.GONE);  
		emptyView.setGravity(Gravity.CENTER);
		emptyView.setBackgroundColor(Color.TRANSPARENT);
		emptyView.setText(mContext.getResources().getString(R.string.MSG_GAMELIST_BLANK_SUMMARY)); 
		((ViewGroup)mListView.getParent()).addView(emptyView,mTvImgLp);  
		mListView.setEmptyView(emptyView); 
	}

	public void listCloseAnim(ViewGroup parent, boolean flag) {
	}

	public void update() {
	    mListView.requestLayout();
		mListAdapter.notifyDataSetChanged();
		closeOpenedItems();
	}
	
	public void upDateProgress(int i){
		mListAdapter.updateView(i);
	}

	public void clearAnim() {
		listCloseAnim(mListView,false);
	}

	public void closeOpenedItems() {
	    if(null != mListView){
	        mListView.closeOpenedItems();
	    }
	}
	
	@Override
	public void onRefresh(PullRefreshView view) {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
			    if(null != mPluginList) {
			        mOnObserverListener.doObserver(-2, mPluginList.size());
			    }
				UpdateAnsyncTask mSearchTask = new UpdateAnsyncTask(mContext, mPluginList, mHandler);
				mSearchTask.execute(Utils.getRootPath(mContext));
				
			}
			
		});		
	}

	@Override
	public void onPullScroll() {
		if(null != mListView) {
			mListView.closeOpenedItems();
		}
	}
	
	private OnObserverListener mOnObserverListener = new OnObserverListener() {

        @Override
        public void doObserver(int current, int total) {
            if(null != mPullRefreshView) {
                mPullRefreshView.setRefreshText(current, total);
            }
        }
	    
	};

}
