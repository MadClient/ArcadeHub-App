package com.yunluo.android.arcadehub.async;

import java.io.File;
import java.util.List;
import android.os.AsyncTask;
import android.os.Handler;
import com.yunluo.android.arcadehub.GameListActivity;
import com.yunluo.android.arcadehub.Emulator;
import com.yunluo.android.arcadehub.utils.Debug;
import com.yunluo.android.arcadehub.utils.FileUtil;

public class UpdateAnsyncTask extends AsyncTask<String, Integer, List<RomInfo>> {

	private GameListActivity mGameList = null;
	
	private List<RomInfo> mRomList = null;
	
	private Handler mHandler = null;
	
	public static boolean flag = false;
	
	public UpdateAnsyncTask(GameListActivity gameList, List<RomInfo> romList, Handler handler) {
		this.mGameList = gameList;
		this.mRomList = romList;
		this.mHandler = handler;
	}

	@Override
	protected List<RomInfo> doInBackground(String... arg0) {
		
		checkFile();
		scan(arg0[0]);
		return mRomList;
	}
	
	@Override
	protected void onPostExecute(List<RomInfo> result) {
		super.onPostExecute(result);
		if(null == mHandler) {
			return;
		}
	}
	
	private void scan(String path) {
		Emulator.startScan(path);
		Debug.d("start scan", "");
	}
	
	public void checkFile() {
		if(null == mRomList) {
			return;
		}

		RomInfo remove = null;
		for(int i = 0; i < mRomList.size(); i++) {
			if(false == FileUtil.exists(mRomList.get(i).getPath(), mRomList.get(i).getName())) {
				remove = mRomList.remove(i);
				Debug.e("remove items", ""+i);
			}	
		}

		if(null == mGameList) {
			return;
		}
		
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				mGameList.update();
			}

		});
	}

	//find rom
    private void romPaths(String path) {
		if(true == flag) {
			return;
		}
    	File file = new File(path);
		File[] files = file.listFiles();
		if(null == files) {
			return;
		}
		
		for (File f : files) {
			if(true == flag) {
				return;
			}
			if(f.isDirectory()) {
				String name = f.getName();
				if(name == null) {
					continue;
				}
				
				if(true == name.startsWith(".")) {
					continue;
				}
				
				String mPath = f.getAbsolutePath();
				if(null == mPath) {
					continue;
				}
				name = name.toLowerCase();
				if((true == name.startsWith("roms")) || (true == name.startsWith("rom"))) {
					Emulator.startScan(mPath);
				}
				
				romPaths(mPath);
			}
			else{}
		}
    }
}
