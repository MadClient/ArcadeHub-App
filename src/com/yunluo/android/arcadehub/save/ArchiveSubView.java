
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
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView.ScaleType;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.yunluo.android.arcadehub.Emulator;
import com.yunluo.android.arcadehub.GameListActivity;
import com.yunluo.android.arcadehub.R;
import com.yunluo.android.arcadehub.utils.ContentValue;
import com.yunluo.android.arcadehub.utils.Debug;
import com.yunluo.android.arcadehub.utils.FileUtil;
import com.yunluo.android.arcadehub.utils.SharePreferenceUtil;
import com.yunluo.android.arcadehub.utils.Utils;

public class ArchiveSubView extends LinearLayout {

    private final static int GAMES_STATE_LOAD = 3002;

    private final static int DELETE_FILE = 3003;

    private final static int SAVE_GAMES_ID = 9001;

    private final static int LOAD_GAMES_ID = 9002;

    private String path = null;

    private ArchiveSubActivity mContext = null;

    private ArchiveAdapter mAdapter = null;

    private ListView mListView = null;

    private Button mSaveBtn = null;

    private ArrayList<ArchiveObj> mList = new ArrayList<ArchiveObj>();

    private String fileName = null;

    private boolean isLoad = false;

    private String mRomName = null;

    private boolean inGames = false;

    private Resources res = null;
    
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            int what = msg.what;
            switch (what) {
                case GAMES_STATE_LOAD:
                    load();
                    MobclickAgent.onEvent(mContext, "EventId_Use_Reload","Use_Reload");
                    break;
                case DELETE_FILE:
                    ArchiveObj mObj = (ArchiveObj)msg.obj;
                    delete(mObj);
                    break;
            }
        }
    };

    public ArchiveSubView(ArchiveSubActivity context, String name) {
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

        LinearLayout.LayoutParams mTitleLp = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int)(50*GameListActivity.SCREEN_DENSITY));
		TextView mTitle = new TextView(mContext);
		mTitle.setText(res.getString(R.string.BTN_OPTIONS_UNARCHIVE));
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
        mSaveBtn = initButton(res.getString(R.string.MSG_RELOAD_ARCHIVE_SAVE), SAVE_GAMES_ID);
        this.addView(mSaveBtn);

        LinearLayout.LayoutParams lvLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        lvLp.weight = 1;
        mListView = new ListView(mContext);
        this.addView(mListView);
        mListView.setCacheColorHint(0x00000000);
        mAdapter = new ArchiveAdapter(mContext, mList);

        mListView.setAdapter(mAdapter);
        mListView.setLayoutParams(lvLp);
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mListView.setOnItemClickListener(mOnItemClickListener);
        mListView.setOnItemLongClickListener(mOnItemLongClickListener);

    }

    private Button initButton(String text, int id) {
        Button mBtn = new Button(mContext);
        mBtn.setId(id);
        mBtn.setText(text);
        int btnColor = mContext.getResources().getColor(R.color.black);
        mBtn.setTextColor(btnColor);
        mBtn.setOnClickListener(mBtnListener);
        mBtn.setVisibility(View.GONE);
        return mBtn;
    }

    private OnClickListener mBtnListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case SAVE_GAMES_ID:
                    mSaveBtn.setEnabled(false);
                    mSaveBtn.setText(res.getString(R.string.onfile_btn_save_ok));
                    int i = mList.size();
                    if(i > 9){
                        Message msg = new Message();
                        msg.what = DELETE_FILE;
                        msg.obj = mList.get(0);
                        mHandler.sendMessage(msg);
                    }
                    //
                    long time = System.currentTimeMillis();
                    String name = Utils.formatTime(time);
                    String rom = SharePreferenceUtil.loadName(mContext);
                    String desc = Emulator.getGameDesc(rom);

                    Debug.d("format time:", name);
                    Debug.d("rom name", rom);
                    Debug.d("descritpition", desc);

                    Emulator.saveGame(desc + FileUtil.SPLIT + time);
                    Emulator.resume();

                    ArchiveObj mSave = new ArchiveObj();
                    mSave.setDesc(desc);
                    mSave.setTime(name);
                    mSave.setDate(time);
                    mList.add(mSave);
                    mAdapter.notifyDataSetChanged();
                    break;
                case LOAD_GAMES_ID:
                    determine();
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
            String name = mList.get(position).getDesc() + FileUtil.SPLIT + mList.get(position).getDate();
            setFileName(name);
            determine();
        }

    };

    private OnItemLongClickListener mOnItemLongClickListener = new OnItemLongClickListener() {

        @Override
        public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {
            if (null == mList) {
                return false;
            }

            new AlertDialog.Builder(mContext)
                    .setTitle(res.getString(R.string.ARCHIVE_DIALOG_TIP))
                    .setMessage(res.getString(R.string.UNARCHIVE_DIALOG_MSG))
                    .setPositiveButton(res.getString(R.string.BTN_COMMON_OK),
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Message msg = new Message();
                                    msg.what = DELETE_FILE;
                                    msg.obj = mList.get(position);
                                    mHandler.sendMessage(msg);
                                }
                            })
                    .setNegativeButton(res.getString(R.string.BTN_COMMON_CANCEL),
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();
            return false;
        }
    };

    private void load() {
    	mContext.destroy();
        if (null == mList || mList.size() <= 0) {
            return;
        }

        if (null == getFileName()) {
            Toast.makeText(mContext, "Please select Load Game option.", Toast.LENGTH_LONG).show();
            Emulator.resume();
            return;
        }

        if (true == isInGames()) {
            Emulator.dimissLoading();
            Emulator.loadGame(fileName);
        } else {
            Emulator.startGames(mRomName);
            setLoad(true);
        }
        Emulator.resume();
        
    }

    private void delete(ArchiveObj mObj) {

        if (null == mObj) {
            return;
        }

        if (null == mList) {
            return;
        }

        final String name = mObj.getDesc() + FileUtil.SPLIT + mObj.getDate();;

        File file = new File(path + name + "." + ContentValue.STA);

        if (file.exists()) {
            boolean boo = file.delete();
        }

        mList.remove(mObj);
        mAdapter.notifyDataSetChanged();

    }

    public void determine() {
        if (null == mHandler) {
            return;
        }

        mHandler.obtainMessage(GAMES_STATE_LOAD, -1, -1).sendToTarget();
    }

    public boolean isLoad() {
        return isLoad;
    }

    public void setLoad(boolean isLoad) {
        this.isLoad = isLoad;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setInGames(boolean inGames) {
        this.inGames = inGames;
    }

    public boolean isInGames() {
        return inGames;
    }

    public void setBtnVisiable() {
        mSaveBtn.setVisibility(View.VISIBLE);
    }
    //back 
    OnClickListener mOnClickListener = new OnClickListener(){

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Emulator.resume();
            mContext.finish();
        }
        
    };
}
