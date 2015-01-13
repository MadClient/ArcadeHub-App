package com.yunluo.android.arcadehub.cheat;

import com.umeng.analytics.MobclickAgent;
import com.yunluo.android.arcadehub.ad.ADBanner;
import com.yunluo.android.arcadehub.utils.SharePreferenceUtil;

import android.app.Activity;
import android.os.Bundle;

public class CheatActivity extends Activity {

	private CheatDialog mCheatDialog;
	private int save_hashcode = -1;
	private int hashcode = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bun = this.getIntent().getExtras();
		hashcode = bun.getInt("HSAH_CODE");
		save_hashcode = SharePreferenceUtil.loadHashcode(this);
		init();
	}
	
	private void init() {
		mCheatDialog = new CheatDialog(this);
		setContentView(mCheatDialog);
		if(save_hashcode != hashcode) {
			mCheatDialog.clear();
			SharePreferenceUtil.saveHashcode(this, hashcode);
		}
		mCheatDialog.updateData();
	}
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        }
    
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        }

    @Override
    protected void onStart() {
        super.onStart();
        MobclickAgent.onEvent(this, "EventId_Enable_Cheat","Enable_Cheat");
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != mCheatDialog) {
            ADBanner mADBanner = mCheatDialog.getADBanner();
            if(null != mADBanner) {
                mADBanner.clearThread();
            }
        }
    }
    

}
