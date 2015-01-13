package com.yunluo.android.arcadehub;

import java.util.List;

import com.google.api.GoogleAPI;
import com.yunluo.android.arcadehub.async.RomInfo;
import com.yunluo.android.arcadehub.data.RomListData;
import com.yunluo.android.arcadehub.download.DownLoadBroadCastRecevier;
import com.yunluo.android.arcadehub.download.DownLoadBroadCastRecevier.BroadcastReceiverListener;
import com.yunluo.android.arcadehub.helpers.MainHelper;
import com.yunluo.android.arcadehub.interfac.OnObserverListener;
import com.yunluo.android.arcadehub.interfac.OnUpdateListener;
import com.yunluo.android.arcadehub.save.ArchiveSubView;
import com.yunluo.android.arcadehub.utils.Utils;

import android.app.Application;
import android.view.View;

public class BaseApplication extends Application implements BroadcastReceiverListener {

	private int version = 0;
	
	private View mView = null;
	private MainHelper mMainHelper = null;
	private GameListActivity mGameList = null;
	private ArchiveSubView mArchiveSubView = null;
	private RomListData mRomListData;
	
	@Override
	public void onCreate() {
		super.onCreate();
		mRomListData = new RomListData();
		mRomListData.setContext(this.getApplicationContext());
		if(true == Utils.checkInternet(this)) {
            GoogleAPI.setHttpReferrer("http://code.google.com/p/google-api-translate-java/");
        }
	}
	
	public void setVersion(int version) {
		this.version = version;
	}

	public int getVersion() {
		return version;
	}

	public void setGameView(View mMainLayout) {
		mView = mMainLayout;
	}
	
	public View getGameView() {
		return mView;
	}

	public void setMainHelper(MainHelper mainHelper) {
		this.mMainHelper = mainHelper;
	}

	public MainHelper getMainHelper() {
		return mMainHelper;
	}

	public void setRomList(List<RomInfo> list) {
		if(null != mRomListData) {
			mRomListData.setRomList(list);
		}
	}

	public List<RomInfo> getRomList() {
		return mRomListData == null ? null : mRomListData.getRomList();
	}
	
	// down load start
	public void register() {
		DownLoadBroadCastRecevier mDlBroadcast = DownLoadBroadCastRecevier.instance(this);
		mDlBroadcast.setBroadcastReceiverListener(this);
	}

	public void unregister() {
		if(DownLoadBroadCastRecevier.instance(this).isRegister()) {
			DownLoadBroadCastRecevier.instance(this).unregister();
		}
	}

	public void setGameListActivity(GameListActivity gameList) {
		this.mGameList = gameList;
	}
	
	public GameListActivity getGameListActivity() {
        return mGameList;
    }
	
	@Override
	public void download(String url, String name, String cs) {
		if(null != mGameList) {
			mGameList.download(url, name, cs);
		}
	}

	public RomListData getRomListData() {
		return mRomListData;
	}

	public void setOnUpdateListener(OnUpdateListener listener) {
		if(null != mRomListData) {
			mRomListData.setOnUpdateListener(listener);
		}
	}
	
	public void setOnObserverListener(OnObserverListener listener) {
	    if(null != mRomListData) {
            mRomListData.setOnObserverListener(listener);
        }
	}

	public ArchiveSubView getArchiveSubView() {
		return mArchiveSubView;
	}

	public void setArchiveSubView(ArchiveSubView mArchiveSubView) {
		this.mArchiveSubView = mArchiveSubView;
	}

}
