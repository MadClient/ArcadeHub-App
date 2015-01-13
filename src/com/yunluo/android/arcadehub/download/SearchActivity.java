package com.yunluo.android.arcadehub.download;

import com.yunluo.android.arcadehub.utils.Debug;
import com.yunluo.android.arcadehub.views.ClearEditText;
import com.yunluo.android.arcadehub.views.ClearEditText.OnSearchListener;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;

public class SearchActivity extends Activity {
	
	private DownLoadWebView mWebView;
	private ClearEditText mSearchEt;
	private LinearLayout mMainLayout;

	private String BASE_URL = "http://kmamek.iptime.org/mamev/index.php?mid=rom_all_mame&page=8&document_srl=";
	
	private String mUrl = null;
	
	private int mType = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = this.getIntent();
		if(null != intent) {
			Bundle bun = intent.getExtras();
			mType = bun.getInt("TYPE");
			mUrl = bun.getString("URL");
		}
		
		init();
		initSearch();
		initWebView();
	}

	private void init() {
		mMainLayout = new LinearLayout(this);
		mMainLayout.setOrientation(LinearLayout.VERTICAL);
		mMainLayout.setBackgroundColor(Color.WHITE);
		LinearLayout.LayoutParams mLayout = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		setContentView(mMainLayout, mLayout);
	}

	private void initSearch() {
		mSearchEt = new ClearEditText(this);
		LinearLayout.LayoutParams mSearchLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		mSearchLp.topMargin = 8;
		mSearchEt.setOnSearchListener(mOnSearchListener);
		Debug.d("mMainLayout: ", mMainLayout);
		Debug.d("mSearchEt: ", mSearchEt);
		mMainLayout.addView(mSearchEt, mSearchLp);
	}

	private void initWebView() {
		mWebView = new DownLoadWebView(this);
		mWebView.load(BASE_URL+mUrl);
		mMainLayout.addView(mWebView);
	}
	
    private void doUrl(String url) {
        if (null == mSearchEt) {
            return;
        }

        if (false == url.startsWith("http://") && false == url.startsWith("https://")) {
            url = "http://" + url;
        }
        if (checkUrl(url)) {
            load(url);
        }

    }
    
    private void load(String url) {
        if (null == mWebView) {
            return;
        }
        mWebView.load(url);
    }

    /**
     * check
     * @param url
     * @return
     */
    private boolean checkUrl(String url) {
        String regEx = "^(http|https|ftp)\\://([a-zA-Z0-9\\.\\-]+(\\:[a-zA-"
                + "Z0-9\\.&%\\$\\-]+)*@)?((25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{"
                + "2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}"
                + "[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|"
                + "[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-"
                + "4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])|([a-zA-Z0"
                + "-9\\-]+\\.)*[a-zA-Z0-9\\-]+\\.[a-zA-Z]{2,4})(\\:[0-9]+)?(/"
                + "[^/][a-zA-Z0-9\\.\\,\\?\\'\\\\/\\+&%\\$\\=~_\\-@]*)*$";
        return url.matches(regEx);

    }
	
    private OnSearchListener mOnSearchListener = new OnSearchListener() {

		@Override
		public void doSearchListener(String url) {
			doUrl(url);
		}
		
    };
    
	@Override
	protected void onDestroy() {
		super.onDestroy();
		finish();
	}
}
