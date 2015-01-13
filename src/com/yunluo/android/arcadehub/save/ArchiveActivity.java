package com.yunluo.android.arcadehub.save;

import com.umeng.analytics.MobclickAgent;
import com.yunluo.android.arcadehub.utils.Debug;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class ArchiveActivity extends Activity {

	private ArchiveView mArchiveView = null;

	public static final int REQUEST_CODE = 123;
	public static final int RESULT_CODE = 234;
	public static final int RESULT_FINSH_CODE = 235;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	private void init() {
		mArchiveView = new ArchiveView(this);
		setContentView(mArchiveView);
	}

	public void showArchiveSubView(String romName) {
		Bundle bun = new Bundle();
		bun.putString("ROM_NAME", romName);
		bun.putBoolean("IN_GAME", false);
		Intent intent = new Intent(this, ArchiveSubActivity.class);
		intent.putExtras(bun);
		startActivityForResult(intent, REQUEST_CODE);;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(REQUEST_CODE == requestCode) {
			Debug.e("resultCode = ", resultCode);
			if(RESULT_CODE == resultCode) {

			} else if(RESULT_FINSH_CODE == resultCode) {
				this.finish();
			}
		}
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
		// TODO Auto-generated method stub
		super.onStart();
	}

}
