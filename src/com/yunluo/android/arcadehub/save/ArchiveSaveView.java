
package com.yunluo.android.arcadehub.save;

import java.io.File;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView.ScaleType;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yunluo.android.arcadehub.Emulator;
import com.yunluo.android.arcadehub.GameListActivity;
import com.yunluo.android.arcadehub.R;
import com.yunluo.android.arcadehub.utils.ContentValue;
import com.yunluo.android.arcadehub.utils.Debug;
import com.yunluo.android.arcadehub.utils.FileUtil;
import com.yunluo.android.arcadehub.utils.SharePreferenceUtil;
import com.yunluo.android.arcadehub.utils.Utils;

public class ArchiveSaveView extends LinearLayout {

    private final static int SAVE_FILE = 3002;
    private final static int DELETE_FILE = 3003;
    
    private final static int SAVE_GAMES_ID = 9001;

    private String path = null;
    private ArchiveSaveActivity mContext = null;
    private ArchiveAdapter mAdapter = null;
    private ListView mListView = null;
    private Button mSaveBtn = null;
    private ArrayList<ArchiveObj> mList = new ArrayList<ArchiveObj>();

    private String fileName = null;
    private String mRomName = null;
    private Resources res = null;
    
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            int what = msg.what;
            switch (what) {
                case SAVE_FILE:
                	int position = (Integer) msg.obj;
                	save(true, position);
                	break;
                case DELETE_FILE:
                    ArchiveObj mObj = (ArchiveObj)msg.obj;
                    delete(mObj);
                    break;
            }
        }
    };

    public ArchiveSaveView(ArchiveSaveActivity context, String name) {
        super(context);
        this.mContext = context;
        this.mRomName = name;
        this.setOrientation(LinearLayout.VERTICAL);
        res = context.getResources();
        init();

    }

    private void init() {
        path = ContentValue.STA_PATH + mRomName + File.separator;
        FileUtil.traverse(mList, path);

        initTitle();
        
        initButton(res.getString(R.string.MSG_RELOAD_ARCHIVE_SAVE), SAVE_GAMES_ID);

        LinearLayout.LayoutParams lvLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        lvLp.weight = 1;
        mListView = new ListView(mContext);
        mListView.setCacheColorHint(0x00000000);
        mListView.setLayoutParams(lvLp);
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        mListView.addFooterView(mSaveBtn);
        
        mAdapter = new ArchiveAdapter(mContext, mList);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(mOnItemClickListener);
        this.addView(mListView);

    }
    
    private void initTitle() {
        LinearLayout.LayoutParams mTitleLp = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int)(50*GameListActivity.SCREEN_DENSITY));
		TextView mTitle = new TextView(mContext);
		mTitle.setText(res.getString(R.string.LISTITEM_MENU_ARCHIVE));
		mTitle.setGravity(Gravity.CENTER);
		mTitle.setBackgroundResource(R.drawable.app_title_bg);
		mTitle.setTextAppearance(mContext, R.style.all_title_style);
		mTitle.setLayoutParams(mTitleLp);
		
		RelativeLayout.LayoutParams imglayoutLp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int)(50*GameListActivity.SCREEN_DENSITY));
        RelativeLayout titlelayout = new RelativeLayout(mContext);
        titlelayout.setLayoutParams(imglayoutLp);
        RelativeLayout.LayoutParams imgLp = new RelativeLayout.LayoutParams((int)(32*GameListActivity.SCREEN_DENSITY), RelativeLayout.LayoutParams.MATCH_PARENT);
        ImageView imgback = new ImageView(mContext);
        imgback.setImageResource(R.drawable.ic_back);
        imgback.setBackgroundColor(0x00000000);
        imgback.setScaleType(ScaleType.FIT_CENTER);
        int i =(int)(9*GameListActivity.SCREEN_DENSITY);
        imgLp.setMargins( i, i, i, i);
        imgback.setLayoutParams(imgLp);
        imgback.setOnClickListener(mOnClickListener);
        titlelayout.addView(mTitle);
        titlelayout.addView(imgback,imgLp); 
		this.addView(titlelayout);
    }

    private Button initButton(String text, int id) {
    	mSaveBtn = new Button(mContext);
        mSaveBtn.setId(id);
        mSaveBtn.setText(text);
        mSaveBtn.setBackgroundColor(0x00000000);
        int btnColor = mContext.getResources().getColor(R.color.black);
        mSaveBtn.setTextColor(btnColor);
        mSaveBtn.setOnClickListener(mBtnListener);
        mSaveBtn.setVisibility(View.VISIBLE);
        mSaveBtn.setGravity(Gravity.CENTER);
        return mSaveBtn;
    }

    private OnClickListener mBtnListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case SAVE_GAMES_ID:
                    save(false, -1);
                    break;
            }
        }
    };
    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
            if (null == mList) {
                return;
            }
            showDeleteDialog(position);
        }

    };
    
    private void save(boolean boo, int position) {
        long time = System.currentTimeMillis();
        String name = Utils.formatTime(time);
        String rom = SharePreferenceUtil.loadName(mContext);
        String desc = Emulator.getGameDesc(rom);

        int pos = mList.size();
        desc = desc+" - "+(pos++);
        Debug.d("format time:", name);
        Debug.d("rom name", rom);
        Debug.d("descritpition", desc);

        if(true == boo) {
        	ArchiveObj mArchiveObj = mList.get(position);
        	Message msg = new Message();
        	msg.what = DELETE_FILE;
        	msg.obj = mArchiveObj;
        	mHandler.sendMessage(msg);
        } else {
        	ArchiveObj mSave = new ArchiveObj();
            mSave.setDesc(desc);
            mSave.setTime(name);
            mSave.setDate(time);
        	mList.add(mSave);
        	mAdapter.notifyDataSetChanged();
        	Emulator.saveGame(desc + FileUtil.SPLIT + time);
        	Emulator.resume();
        	
        	MobclickAgent.onEvent(mContext, "EventId_Use_Save","Use_Save");
        }
        
        if(null != mContext) {
        	mContext.finish();
        }
    }
    
    private void showDeleteDialog(final int position) {
    	new AlertDialog.Builder(mContext)
    	.setTitle(res.getString(R.string.ARCHIVE_DIALOG_TIP))
    	.setMessage(res.getString(R.string.MSG_RELOAD_ARCHIVE_REPLACE))
    	.setPositiveButton(res.getString(R.string.BTN_COMMON_OK), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Message msg = new Message();
				msg.what = SAVE_FILE;
                msg.obj = position;
                mHandler.sendMessage(msg);
			}
		})
		.setNegativeButton(res.getString(R.string.BTN_COMMON_CANCEL), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		})
		.show();
    }

    private void delete(ArchiveObj mObj) {

        if (null == mObj) {
            return;
        }

        if (null == mList) {
            return;
        }

        final String name = mObj.getDesc() + FileUtil.SPLIT + mObj.getDate();

        Debug.d("delete path", path);
        Debug.d("delete file name", name);

        File file = new File(path + name + "." + ContentValue.STA);

        if (file.exists()) {
            boolean boo = file.delete();
            Debug.d("deletd file", "" + ((boo == true) ? "success" : "failure"));
        }

        long t = System.currentTimeMillis();
        String n = Utils.formatTime(t);
        mObj.setDate(t);
        mObj.setTime(n);
        
    	Emulator.saveGame(mObj.getDesc() + FileUtil.SPLIT + t);
    	Emulator.resume();
        mAdapter.notifyDataSetChanged();

    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    //back 
    private OnClickListener mOnClickListener = new OnClickListener(){

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Emulator.resume();
            mContext.finish();
        }
        
    };
}
