package com.yunluo.android.arcadehub;

import com.umeng.analytics.MobclickAgent;
import com.yunluo.android.arcadehub.interfac.OnShareListener;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AboutActivity extends Activity {

    private Resources mRes = null;
    private LinearLayout mHelpLyt = null;
    private LinearLayout mLicenseLyt = null;
    private ImageView mBack = null;
    private TextView mTvVersion = null; 

    private LinearLayout mScrollLyt = null;
    
    private final static int STATE_NONE = -1;
    private final static int STATE_SINA = 1;
    private final static int STATE_TWITTER = 2;
    private final static int STATE_FACEBOOK = 3;
    private static int STATE = STATE_NONE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRes = this.getResources();
        setContentView(R.layout.about);
        init();
    }

    private void init() {
        mHelpLyt = (LinearLayout) this.findViewById(R.id.about_help);
        mLicenseLyt = (LinearLayout) this.findViewById(R.id.about_license);
        mBack = (ImageView) this.findViewById(R.id.about_back);
        mTvVersion = (TextView) this.findViewById(R.id.about_version);
        String tmpVersion = getVersion();
        mTvVersion.setText(this.getResources().getString(R.string.ABOUT_VERSION_DEFAULT)+tmpVersion);
        
        mHelpLyt.setOnClickListener(mOnClickListener);
        mLicenseLyt.setOnClickListener(mOnClickListener);
        mBack.setOnClickListener(mOnClickListener);
    }
    
    private String getVersion() {
        String version = "1.0.0";
        PackageManager pm = getPackageManager();
        try {
            PackageInfo pkgInfo = pm.getPackageInfo("com.yunluo.android.arcadehub", 0);
            if(null != pkgInfo) {
                version = pkgInfo.versionName;
            }
        } catch (NameNotFoundException e) {
        }
        return version;
    }

    private OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            int id = v.getId();
            switch (id) {
            case R.id.about_help:
                help();
                break;
            case R.id.about_license:
                license();
                break;
            case R.id.about_back:
                finish();
                break;
            case R.id.share_sina_id:
                STATE = STATE_SINA;
                authSina();
                break;
            case R.id.share_wechat_id:
                authWechat();
                break;
            case R.id.share_facebook_id:
                STATE = STATE_FACEBOOK;
                break;
            case R.id.share_twitter_id:
                STATE = STATE_TWITTER;
                break;
            }

        }

    };

    private void help() {
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }

    private void license() {
        Intent intent = new Intent(this, LicenseActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    };

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);

    };

    private void initPopup() {
    }

    private void initScrollLyt() {
        mScrollLyt = (LinearLayout) this.findViewById(R.id.share_scroll_lyt);
        initData();
    }

    private void initData() {
        TypedArray imgs = mRes.obtainTypedArray(R.array.share_img);
        TypedArray ids = mRes.obtainTypedArray(R.array.share_id);

        for (int i = 0; i < 4; i++) {
            int resId = imgs.getResourceId(i, 0);
            int id = ids.getResourceId(i, i + 1);
            addView(id, resId);
        }

    }

    private void addView(int id, int resId) {
        LinearLayout.LayoutParams ivLp = new LinearLayout.LayoutParams(72, 72);
        ivLp.setMargins(10, 10, 10, 10);
        ImageView iv = new ImageView(this);
        iv.setId(id);
        iv.setImageResource(resId);
        iv.setOnClickListener(mOnClickListener);
        mScrollLyt.addView(iv, ivLp);
    }

    private void initShare() {
    }

    private void authSina() {
    }

    private void authWechat() {
    }

    private void authFacebook() {

    }

    private void authTwitter() {

    }

    public void share(String msg) {
        switch(STATE) {
        case STATE_SINA:
            break;
        case STATE_FACEBOOK:
            break;
        case STATE_TWITTER:
            break;
        }
    }
    
    public void showShareView() {
    }
    
    private void dismiss() {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private OnShareListener mOnShareListener = new OnShareListener() {

        @Override
        public void doClose() {
            dismiss();
        }

        @Override
        public void doSendMsg(String msg) {
            share(msg);
            dismiss();
        }

        @Override
        public void doShowShare() {
            showShareView();
        }

    };

}
