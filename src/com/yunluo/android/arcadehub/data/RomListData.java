package com.yunluo.android.arcadehub.data;

import java.util.ArrayList;
import java.util.List;

import com.yunluo.android.arcadehub.Emulator;
import com.yunluo.android.arcadehub.async.RomInfo;
import com.yunluo.android.arcadehub.interfac.OnAddRomListener;
import com.yunluo.android.arcadehub.interfac.OnObserverListener;
import com.yunluo.android.arcadehub.interfac.OnUpdateListener;
import com.yunluo.android.arcadehub.utils.Debug;
import com.yunluo.android.arcadehub.utils.TranslateUtils;
import com.yunluo.android.arcadehub.utils.Utils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

public class RomListData implements OnAddRomListener {

	private Handler mHandler = new Handler();

	private List<RomInfo> mRomList = new ArrayList<RomInfo>();

	private boolean isChinese = false;
	
	private OnUpdateListener mOnUpdateListener = null;
	
	private OnObserverListener mOnObserverListener = null;
	
	private Context mContext = null;

	private int count = 1;
	
	public RomListData() {
		isChinese = Utils.isChinese();
		Emulator.setOnAddRomListener(this);
	}

	@Override
	public void onAddRom(final String name, final String ename, final String path, final String size, final String year, final String cname, final String filepath) {
	 Debug.d("romName", ": " + name);
	 Debug.d("gameName", ": " + ename);
	 Debug.d("path", ": " + path);
	 Debug.d("size", ": " + size);
	 Debug.d("filepath", ": " + filepath);
		
		mHandler.post(new Runnable() {

			@Override
			public void run() {
			    new AddAsyncTask().execute(name, ename, path, size, year, cname, filepath);
			}
		});
	}
	
	/**
	 * Add rom info
	 */
	private RomInfo add(String name, String ename, String path, String size, String year, String cname, String filepath) {
	    if (null == mRomList) {
            return null;
        }
        for (RomInfo rom : mRomList) {
            if (rom != null && rom.getName().equals(name)) {
                return null;
            }
        }
        RomInfo mRomInfo = new RomInfo();
        mRomInfo.setName(name);
        mRomInfo.setCname(cname);
        mRomInfo.setEname(ename);
        
        
        if(null != mContext && true == Utils.checkInternet(mContext)) {
            TranslateUtils.addTranslateLng(mRomInfo, cname, ename);
            String tmp = mRomInfo.getDesc(); 
            if(null == tmp || tmp.length() == 0) {
                mRomInfo.setDesc(ename);
            }
        } else {
            mRomInfo.setDesc(isChinese ? Utils.changeTxt(cname) : ename);
        }
        
        if(path.startsWith("/data/data/")) {
            int index = path.indexOf("/files");
            path = path.substring(index);
        }
        
        mRomInfo.setPath(path);
        mRomInfo.setSize(size);
        mRomInfo.setIcon(name);
        String cs = Utils.getMD5(name);
        mRomInfo.setCheckSum(cs);
        mRomInfo.setFilepath(filepath);
        return mRomInfo;
	}
	
	class AddAsyncTask extends AsyncTask<String, Void, RomInfo> {

        /* (non-Javadoc)
         * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
         */
        @Override
        protected RomInfo doInBackground(String... params) {
            RomInfo mRomInfo = add(params[0], params[1], params[2], params[3], params[4], params[5], params[6]);
            return mRomInfo;
        }

        @Override
        protected void onPostExecute(RomInfo mRomInfo) {
            super.onPostExecute(mRomInfo);
            if(null == mRomInfo) {
                return;
            }
            List<RomInfo> tmpList = getRomList();
            if(tmpList == null) {
                return;
            }
            tmpList.add(mRomInfo);
            if(null != mOnUpdateListener) {
                mOnUpdateListener.onUpdate();
            }
            if(null != mOnObserverListener) {
                mOnObserverListener.doObserver(count, tmpList.size());
                count++;
            }
        }
        
	}

	public void reset() {
	    count = 1;
	}
	
	public void setOnUpdateListener(OnUpdateListener listener) {
		this.mOnUpdateListener = listener;
	}
	
	public List<RomInfo> getRomList() {
		return mRomList;
	}

	public void setRomList(List<RomInfo> mRomList) {
		this.mRomList = mRomList;
	}

    public void setContext(Context context) {
        this.mContext = context;
    }

    public void setOnObserverListener(OnObserverListener listener) {
       this.mOnObserverListener = listener;
    }

}
