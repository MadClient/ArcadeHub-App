
package com.yunluo.android.arcadehub.combination;

import java.util.ArrayList;
import java.util.List;
import com.yunluo.android.arcadehub.Emulator;
import com.yunluo.android.arcadehub.GamePlayActivity;
import com.yunluo.android.arcadehub.R;
import com.yunluo.android.arcadehub.combination.wheel.OnWheelChangedListener;
import com.yunluo.android.arcadehub.combination.wheel.OnWheelScrollListener;
import com.yunluo.android.arcadehub.combination.wheel.WheelView;
import com.yunluo.android.arcadehub.combination.wheel.adapter.AbstractWheelTextAdapter;
import com.yunluo.android.arcadehub.keymacro.KeyMacro;
import com.yunluo.android.arcadehub.utils.Debug;
import com.yunluo.android.arcadehub.utils.SharePreferenceUtil;
import com.yunluo.android.arcadehub.views.InputView;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;

public class Combination extends Dialog implements OnCheckedChangeListener, OnClickListener {

    // Scrolling flag
    private boolean scrolling = false;

    protected GamePlayActivity mGPActivity = null;

    protected KeyMacro mKeyMacro = null;

    private InputView mInputView = null;

    private String romsName = null;

    public Combination(GamePlayActivity activity, int theme, boolean showKey) {
        super(activity, theme);
        // TODO Auto-generated constructor stub
        mGPActivity = activity;
        if(null ==  mGPActivity){
            return;
        }
        romsName = SharePreferenceUtil.loadName(mGPActivity);
        if(null ==  romsName){
            return;
        }
        this.setTitle(mGPActivity.getResources().getString(R.string.BTN_OPTIONS_COMPOSEKEY));
        mKeyMacro = mGPActivity.getKeyMacro();
        if (null != mKeyMacro) {
            keyNumber = mKeyMacro.getCount();
            mKeyMacro.setOnCombinationListener(mOnCombinationListener);
        }

        mKeyMacro = mGPActivity.getKeyMacro();
        mInputView = mGPActivity.getInputView();
        if (true == showKey) {
            this.getWindow().setWindowAnimations(R.style.CustomDialog);
            this.setContentView(R.layout.combination_layout);
            init();
            final WheelView country = (WheelView)findViewById(R.id.country);
            country.setVisibleItems(3);
            country.setViewAdapter(new CountryAdapter(mGPActivity));
            country.addChangingListener(new OnWheelChangedListener() {
                public void onChanged(WheelView wheel, int oldValue, int newValue) {
                    if (!scrolling) {
                        upDateBindingKey(newValue);
                    }
                }
            });
            country.addScrollingListener(new OnWheelScrollListener() {
                public void onScrollingStarted(WheelView wheel) {
                    scrolling = true;
                }

                public void onScrollingFinished(WheelView wheel) {
                    scrolling = false;
                    upDateBindingKey(wheel.getCurrentItem());
                }
            });

            country.setCurrentItem(1);
        }
        // get value
        getDbDate(mGPActivity, romsName, showKey);
        if (true == showKey) {
            this.setCanceledOnTouchOutside(false);
            Combination.this.show();
        }
    }

    /**
     * Adapter for countries
     */
    private class CountryAdapter extends AbstractWheelTextAdapter {
        // Countries names
        private String countries[] = new String[] {
                "a", "y", "r", "l", "x", "b"
        };

        // Countries flags
        // 012345 ayrlxb
        private int flags[] = new int[] {
                R.drawable.ic_btn_cyan_a, R.drawable.ic_btn_cyan_y, R.drawable.ic_btn_cyan_r,
                R.drawable.ic_btn_cyan_l, R.drawable.ic_btn_cyan_x, R.drawable.ic_btn_cyan_b
        };

        /**
         * Constructor
         */
        protected CountryAdapter(Context context) {
            super(context, R.layout.combination_wheel_layout, NO_RESOURCE);

            // setItemTextResource(R.id.country_name);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            View view = super.getItem(index, cachedView, parent);

            ImageView img = (ImageView)view.findViewById(R.id.flag);
            img.setImageResource(flags[index]);
            return view;
        }

        @Override
        public int getItemsCount() {
            return countries.length;
        }

        @Override
        protected CharSequence getItemText(int index) {
            return countries[index];
        }
    }

    private CheckBox cBox_0, cBox_1, cBox_2, cBox_3;

    private TextView signtv2, signtv3;

    private ImageView imageClose ,imgWheelTop,imgWheelBottom;

    private Button but_ok/* , but_cance */;

    private LinearLayout linearLayout;

    private int keyNumber = 4;

    List<Integer> sequencearyList = new ArrayList<Integer>();

    List<Integer> combinationList = new ArrayList<Integer>();

    List<String> dbSaveList = new ArrayList<String>();

    private final static int KEY_A = 4096;

    private final static int KEY_B = 8192;

    private final static int KEY_X = 16384;

    private final static int KEY_Y = 32768;

    public final static int KEY_L = 1024;

    public final static int KEY_R = 2048;

    public int bindingKey = 32768;

    /**
     * TODO
     * 
     * @return void TODO
     * @throws Exception TODO
     */
    private void init() {
        cBox_0 = (CheckBox)findViewById(R.id.cbx_0);
        cBox_1 = (CheckBox)findViewById(R.id.cbb_1);
        cBox_2 = (CheckBox)findViewById(R.id.cby_2);
        cBox_3 = (CheckBox)findViewById(R.id.cba_3);
        cBox_0.setOnCheckedChangeListener(this);
        cBox_1.setOnCheckedChangeListener(this);
        cBox_2.setOnCheckedChangeListener(this);
        cBox_3.setOnCheckedChangeListener(this);

        signtv2 = (TextView)findViewById(R.id.tv_2);
        signtv3 = (TextView)findViewById(R.id.tv_3);

        but_ok = (Button)findViewById(R.id.but_ok);
        but_ok.setOnClickListener(this);

        linearLayout = (LinearLayout)findViewById(R.id.show_ok_key_layout);
        if (2 == keyNumber) {
            isJudgeUsable();
        }

        imageClose = (ImageView)findViewById(R.id.img_close);
        imageClose.setOnClickListener(this);
        
        imgWheelTop = (ImageView)findViewById(R.id.cbt_wheel_top);
        imgWheelBottom = (ImageView)findViewById(R.id.cbt_wheel_bottom);
    }

    /**
     * Determine whether the rest of the keys is occupied four key game
     * 
     * @return null
     * @throws
     */
    private void isJudgeUsable() {
        cBox_2.setVisibility(View.GONE);
        cBox_3.setVisibility(View.GONE);
        signtv2.setVisibility(View.GONE);
        signtv3.setVisibility(View.GONE);
    }

    float mDensity = (float)1.0;

    /**
     * According to the resolution of the mobile phone will become a unit from dp px (pixels)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dpValue * scale + 0.5f);
    }

    private View initImageView(int id) {
        RelativeLayout.LayoutParams titleimgLp = new RelativeLayout.LayoutParams(dip2px(
                mGPActivity, 40), dip2px(mGPActivity, 40));
        titleimgLp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        titleimgLp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        ImageView image = new ImageView(mGPActivity);
        image.setImageResource(id);
        image.setLayoutParams(titleimgLp);
        image.setScaleType(ScaleType.FIT_START);
        return image;
    }

    private void combinationLayout(LinearLayout cblayout, final int mKey, List<Integer> childList) {
        List<Integer> mChildList = new ArrayList<Integer>();
        mChildList = childList;
        if (null == mChildList || 0 == mChildList.size()) {
            return;
        }
        LinearLayout.LayoutParams mainLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mainLP.gravity = Gravity.CENTER_VERTICAL;
        final LinearLayout mainlayout = new LinearLayout(mGPActivity);
        mainlayout.setLayoutParams(mainLP);
        mainlayout.setOrientation(0);
        mainlayout.setWeightSum(11);

        LinearLayout.LayoutParams childLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 2);
        childLP.leftMargin = 6;
        LinearLayout layout = new LinearLayout(mGPActivity);
        layout.setLayoutParams(childLP);
        layout.setOrientation(0);
        layout.addView(initImageView(keyId(mKey)));
        layout.addView(signText("="));
        int i = mChildList.size();
        for (int x = 0; x < i; x++) {
            int id = mChildList.get(x);

            layout.addView(initImageView(keyId(id)));
            if (x < i - 1) {
                layout.addView(signText("+"));
            }
        }
        ImageView deleteb = new ImageView(mGPActivity);
        LinearLayout.LayoutParams butLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dip2px(mGPActivity, 25), 9);
        butLp.gravity = Gravity.CENTER_VERTICAL;
        deleteb.setImageResource(R.drawable.ic_delete_white);
        deleteb.setScaleType(ScaleType.FIT_CENTER);
        deleteb.setLayoutParams(butLp);
        deleteb.setOnClickListener(new android.view.View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    // TODO Auto-generated method stub
                    removeLayout(mainlayout);
                    for (int j = 0; j < combinationList.size(); j++) {
                        int key = combinationList.get(j);
                        if (key == mKey) {

                        	mInputView.setResetVisiable(combinationList.get(j), true);
                            mKeyMacro.resetSelectedPress(key);
                            mInputView.resetKey(mKeyMacro.isA(), mKeyMacro.isB(), mKeyMacro.isX(), mKeyMacro.isY(), mKeyMacro.isL(), mKeyMacro.isR());	
                            Emulator.restMultiPress(key);
                            mInputView.update(-1, false);
                            combinationList.remove(j);
                            dbSaveList.remove(j);
                        }
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
//                    e.printStackTrace();
                }
            }

        });
        mainlayout.addView(layout);
        mainlayout.addView(deleteb);
        cblayout.addView(mainlayout);
    }

    private void removeLayout(LinearLayout mLayout) {
        linearLayout.removeView(mLayout);
    };

    private int keyId(int key) {
        int mKeyId = 0;
        switch (key) {
            case KEY_A:
                mKeyId = R.drawable.ic_btn_cyan_a;
                break;
            case KEY_Y:
                mKeyId = R.drawable.ic_btn_cyan_y;
                break;
            case KEY_R:
                mKeyId = R.drawable.ic_btn_cyan_r;
                break;
            case KEY_L:
                mKeyId = R.drawable.ic_btn_cyan_l;
                break;
            case KEY_X:
                mKeyId = R.drawable.ic_btn_cyan_x;
                break;
            case KEY_B:
                mKeyId = R.drawable.ic_btn_cyan_b;
                break;
            default:
                break;
        }
        return mKeyId;
    }

    private TextView signText(String sign) {
        TextView tv = new TextView(mGPActivity);
        LinearLayout.LayoutParams tvLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, dip2px(mGPActivity, 40));
        tv.setLayoutParams(tvLp);
        tv.setText(sign);
        tv.setTextColor(Color.WHITE);
        tv.setGravity(Gravity.CENTER_VERTICAL);
        return tv;
    }

    // 012345 ayrlxb
    private void upDateBindingKey(int i) {
        switch (i) {
            case 0:
                bindingKey = KEY_A;
                imgWheelTop.setImageResource(R.drawable.keymacro_up_disable);
                imgWheelBottom.setImageResource(R.drawable.keymacro_down);
                break;
            case 1:
                bindingKey = KEY_Y;
                break;
            case 2:
                bindingKey = KEY_R;
                break;
            case 3:
                bindingKey = KEY_L;
                break;
            case 4:
                bindingKey = KEY_X;
                break;
            case 5:
                bindingKey = KEY_B;
                imgWheelTop.setImageResource(R.drawable.keymacro_up);
                imgWheelBottom.setImageResource(R.drawable.keymacro_down_disable);
                break;
            default:
                break;
        }
        if(0 != i && 5 != i){
            imgWheelTop.setImageResource(R.drawable.keymacro_up);
            imgWheelBottom.setImageResource(R.drawable.keymacro_down);
        }
    }
    

    // int dbInt = 0;
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {

            case R.id.but_ok:
                StringBuffer strDB = new StringBuffer(String.valueOf(bindingKey));
                boolean isKey = false;
                int i = sequencearyList.size();
                if (0 == i) {
                    showThost(mGPActivity.getResources().getString(R.string.MSG_COMPOSEKEY_SELECT));
                    return;
                }
                int x = combinationList.size();
                for (int j = 0; j < x; j++) {
                    int key = combinationList.get(j);
//                    Debug.e("===========", key+"="+bindingKey);
                    if (key == bindingKey && key != 0 ) {
                        isKey = true;
                        showThost(mGPActivity.getResources().getString(R.string.MSG_COMPOSEKEY_RESELECT));
                    }
                }
                if (null != linearLayout && x < 3 && false == isKey) {
                    int[] array = new int[i];
                    for (int y = 0; y < i; y++) {
                        strDB.append("*");
                        strDB.append(sequencearyList.get(y));
                        array[y] = sequencearyList.get(y);
                    }
                    if (null != strDB && 0 != strDB.length()) {
                        dbSaveList.add(strDB.toString());
                    }
                    // Setting key combination
                    bindingKeyCombination(bindingKey);
                    Emulator.setMultiPress(bindingKey, array, array.length);
                    // Display the determined column
                    combinationLayout(linearLayout, bindingKey, sequencearyList);
                    combinationList.add(bindingKey);

                } else if (false == isKey) {
                    showThost(mGPActivity.getResources().getString(R.string.composekey_toast_many));
                }
                break;
            case R.id.img_close:
            	deleteKeys();
                Emulator.resume();
                this.setCanceledOnTouchOutside(true);
                sequencearyList = null;
                combinationList = null;
                dbSaveList = null;
               
                Combination.this.dismiss();
                break;
        }
    }
    
    private void deleteKeys() {
    	if (null == dbSaveList) {
    		return;
    	}

    	int size = dbSaveList.size();

    	for (int i = 0; i < size; i++) {
    		switch(i) {
    		case 0:
    			SharePreferenceUtil.saveKeysOne(mGPActivity, romsName, dbSaveList.get(i));
    			break;
    		case 1:
    			SharePreferenceUtil.saveKeysTwo(mGPActivity, romsName, dbSaveList.get(i));
    			break;
    		case 2:
    			SharePreferenceUtil.saveKeysThree(mGPActivity, romsName, dbSaveList.get(i));
    			break;
    		}
    	}
    	
    	switch(size) {
    	case 0:
    		SharePreferenceUtil.saveKeysOne(mGPActivity, romsName, "null");
    		SharePreferenceUtil.saveKeysTwo(mGPActivity, romsName, "null");
    		SharePreferenceUtil.saveKeysThree(mGPActivity, romsName, "null");
    		break;
    	case 1:
    		SharePreferenceUtil.saveKeysTwo(mGPActivity, romsName, "null");
    		SharePreferenceUtil.saveKeysThree(mGPActivity, romsName, "null");
    		break;
    	case 2:
    		SharePreferenceUtil.saveKeysThree(mGPActivity, romsName, "null");
    		break;
    	}
    }

    //test delete db method 
    public void getDbDate(Activity mActivity, String romname, Boolean showKey) {
    	if (null == mActivity || null == romname) {
    		return;
    	}

    	String one = SharePreferenceUtil.loadKeysOne(mActivity, romname);
    	add(one, showKey);

    	String two = SharePreferenceUtil.loadKeysTwo(mActivity, romname);
    	add(two, showKey);

    	String three = SharePreferenceUtil.loadKeysThree(mActivity, romname);
    	add(three, showKey);


    	bindingKey = KEY_Y;
    };

    //test delete db method 
    private void add(String name, boolean showKey) {
    	if (null != name && "null".equals(name) == false) {
    		showdbDate(name, showKey);
            dbSaveList.add(name);
    	}
    }
    
    // Setting key combination by database values
    private void showdbDate(String combinationStr, boolean showKey) {

        try {
            List<Integer> dbChildList = new ArrayList<Integer>();
            String[] name = combinationStr.split("\\*");
            int x = name.length;
            if (x > 1) {
                int[] array = new int[x - 1];
                for (int i = 0; i < x; i++) {
                    if (0 == i) {
                        bindingKey = Integer.parseInt(name[0]);
                    } else {
                        int j = Integer.parseInt(name[i]);
                        dbChildList.add(j);
                        array[i - 1] = j;
                    }
                }
                bindingKeyCombination(bindingKey);
                Emulator.setMultiPress(bindingKey, array, array.length);
                
                if (true == showKey) {
                    combinationLayout(linearLayout, bindingKey, dbChildList);
                    combinationList.add(bindingKey);
                }
            }
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
//            e.printStackTrace();
        }

    }

    /**
     * Record Setting Binding current key combination
     * 
     * @param mKey
     */
    private void bindingKeyCombination(int mKey) {
        mKeyMacro.setSelected(mKey, false);
        mInputView.setVisiable(mKey, false);
        mInputView.update(mKey, false);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // TODO Auto-generated method stub
        switch (buttonView.getId()) {
            case R.id.cbx_0:
                if (true == isChecked) {
                    sequencearyList.add(KEY_X);
                } else {
                    for (int j = 0; j < sequencearyList.size(); j++) {
                        int key = sequencearyList.get(j);
                        if (key == KEY_X) {
                            sequencearyList.remove(j);
                        }
                    }
                }
                break;
            case R.id.cbb_1:
                if (true == isChecked) {
                    sequencearyList.add(KEY_B);
                } else {
                    for (int j = 0; j < sequencearyList.size(); j++) {
                        int key = sequencearyList.get(j);
                        if (key == KEY_B) {
                            sequencearyList.remove(j);
                        }
                    }
                }
                break;
            case R.id.cby_2:
                if (true == isChecked) {
                    sequencearyList.add(KEY_Y);
                } else {
                    for (int j = 0; j < sequencearyList.size(); j++) {
                        int key = sequencearyList.get(j);
                        if (key == KEY_Y) {
                            sequencearyList.remove(j);
                        }
                    }
                }
                break;
            case R.id.cba_3:
                if (true == isChecked) {
                    sequencearyList.add(KEY_A);
                } else {
                    for (int j = 0; j < sequencearyList.size(); j++) {
                        int key = sequencearyList.get(j);
                        if (key == KEY_A) {
                            sequencearyList.remove(j);
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    private void showThost(String msg) {
        if (null != msg && null != mGPActivity) {
            Toast.makeText(mGPActivity, msg, Toast.LENGTH_SHORT).show();
        }
    }

    /*
     * Interception return key events
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return false;
    }

    public OnCombinationListener mOnCombinationListener = new OnCombinationListener() {

        public void onDelete(int key) {
            Emulator.restMultiPress(key);
            if (null == mGPActivity) {
                return;
            }
            delete(key);
        }

    };
    
    //test delete db method 
    private void delete(int key) {
    	String name = SharePreferenceUtil.loadName(mGPActivity);
    	
    	String one = SharePreferenceUtil.loadKeysOne(mGPActivity, name);
    	String two = SharePreferenceUtil.loadKeysTwo(mGPActivity, name);
    	String three = SharePreferenceUtil.loadKeysThree(mGPActivity, name);
    	
    	reset(name, one, key, 1);    
    	reset(name, two, key, 2);    
    	reset(name, three, key, 3);    
    }
    
  //test delete db method
    private void reset(String name, String target, int key, int type) {
    	if(null != target) {
    		String spStr[] = target.split("\\*");
    		boolean isContains = String.valueOf(key).equals(spStr[0]);
    		if (true == isContains) {
    			switch(type) {
    			case 1:
    				SharePreferenceUtil.saveKeysOne(mGPActivity, name, "null");
    				break;
    			case 2:
    				SharePreferenceUtil.saveKeysTwo(mGPActivity, name, "null");
    				break;
    			case 3:
    				SharePreferenceUtil.saveKeysThree(mGPActivity, name, "null");
    				break;
    			}
    		}
    	}
    }
}
