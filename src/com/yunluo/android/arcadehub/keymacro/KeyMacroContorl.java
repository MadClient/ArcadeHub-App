package com.yunluo.android.arcadehub.keymacro;

import java.util.ArrayList;
import java.util.List;
import com.yunluo.android.arcadehub.Emulator;
import com.yunluo.android.arcadehub.GamePlayActivity;
import com.yunluo.android.arcadehub.R;
import com.yunluo.android.arcadehub.combination.wheel.OnWheelChangedListener;
import com.yunluo.android.arcadehub.combination.wheel.OnWheelScrollListener;
import com.yunluo.android.arcadehub.combination.wheel.WheelView;
import com.yunluo.android.arcadehub.input.InputHandler;
import com.yunluo.android.arcadehub.utils.Debug;
import com.yunluo.android.arcadehub.utils.SharePreferenceUtil;
import com.yunluo.android.arcadehub.views.EmptyViewForListView;
import com.yunluo.android.arcadehub.views.InputView;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * @author Administrator
 *
 */
public class KeyMacroContorl implements IKeyMacro {

	//Handler MSG
	private static final int MSG_RESET = 1;
	private static final int MSG_DELETE = 2;
	private static final int MSG_SAVE = 3;
	private static final int MSG_BACK = 4;
	private static final int MSG_TOP = 5;
	private static final int MSG_BOTTOM = 6;
	private static final int MSG_CLOSE = 7;
	
	public static int KEY_SELECTED = KEY_A;
	
	private static String SPLIT_ALARM = "#";
	
	private static String SPLIT_COMMA = ",";
	
	private List<KeyMacroObj> mList = new ArrayList<KeyMacroObj>();
	
	private GridView mGridView = null;
	
	private KeyMacroAdapter mAdapter = null;
	
	private RelativeLayout mRelativeLayout = null;
	
	private KeyMacro mKeyMacro = null;

	private InputView mInputView = null;
	
	private InputHandler mInputHandler = null;

	private GamePlayActivity mActivity = null;
	
	private ImageView mImgUp = null;

	private ImageView mImgDown = null;
	
	private Resources mRes = null;
	
	private WheelView mWheelView = null;
	
	private WheelAdapter mWheelAdapter = null;
	
	private boolean isScrolling = false;
	
	private int CUR_SELECT_ITEM = 0;
	
	private int key = -1;
	
	private int values[] = null;

	private Handler mHandler = new Handler() {
		
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch(what) {
			case MSG_RESET:
				keyReset();
				break;
			case MSG_DELETE:
				keyDelete();
				break;
			case MSG_SAVE:
				keySave();
				break;
			case MSG_BACK:
				keyBack();
				break;
			case MSG_TOP:
				keyTop();
				break;
			case MSG_BOTTOM:
				keyBottom();
				break;
			case MSG_CLOSE:
				keyClose();
				break;
			}
			
		};
	};
	
	public KeyMacroContorl(GamePlayActivity context, RelativeLayout relativeLayout) {
		this.mActivity = context;
		this.mRelativeLayout = relativeLayout;
		this.mRes = mActivity.getResources();
		init();
	}

	private void init() {
		
		mInputHandler = mActivity.getInputHandler();
		mKeyMacro = mActivity.getKeyMacro();
		mInputView = mActivity.getInputView();
		
		initWheel();
		initGridView();
		initBtn();

		refreshLoad();
		
		addEmpytView();
	}
	
	public void refreshLoad() {
		initLoad();
		updateLoad();
	}
	
	private void initLoad() {
		String name = SharePreferenceUtil.loadName(mActivity);
		String target = SharePreferenceUtil.loadKeysFour(mActivity, name);
		if(null == target || target.length() == 0) {
			key = -1;
			values = null;	
			return;
		}
		
		Debug.e("target = ", target);
		String items[] = target.split(SPLIT_ALARM);
		if(2 == items.length ) {
			if(null != items[0]) {
				key = Integer.valueOf(items[0]);
			} 
			if(null != items[1]) {
				String valStr[] = items[1].split(SPLIT_COMMA);
				int len = valStr.length;
				values = new int[len];
				for (int i = 0; i < len; i++) {
					values[i] = Integer.valueOf(valStr[i]);
				}
			}
			Debug.e("initLoad KEY_SELECTED = ", KEY_SELECTED);
			Debug.e("initLoad key = ", key);
			KEY_SELECTED = key;
		}
	}
	
	private void updateLoad() {
		if(null == mList) {
			return;
		}
		if(mList.size() > 0) {
			mList.clear();
		}
		if(-1 == key && null == values) {
			return;
		}
		mWheelView.setCurrentItem(getCurrentItem(key));
		int len = values.length;
		for (int i = 0; i < len; i++) {
			int k = Integer.valueOf(values[i]);
			int type = getType(k);
			KeyMacroObj obj = new KeyMacroObj();
			obj.setKey(k);
			obj.setType(type);
			mList.add(obj);
		}
		if(null != mAdapter) {
			mAdapter.notifyDataSetChanged();
		}
	}
	
	private int getCurrentItem(int key) {
		switch(key) {
		case KEY_A:
			CUR_SELECT_ITEM = 0;
			break;
		case KEY_B:
			CUR_SELECT_ITEM = 1;
			break;
		case KEY_X:
			CUR_SELECT_ITEM = 2;
			break;
		case KEY_Y:
			CUR_SELECT_ITEM = 3;
			break;
		case KEY_R:
			CUR_SELECT_ITEM = 4;
			break;
		case KEY_L:
			CUR_SELECT_ITEM = 5;
			break;
		}
		return CUR_SELECT_ITEM;
	}
	
	private void initWheel() {
		mWheelAdapter = new WheelAdapter(mActivity);
		mWheelView = (WheelView) mRelativeLayout.findViewById(R.id.wheel_center);
		mWheelView.setVisibleItems(1);
		mWheelView.setViewAdapter(mWheelAdapter);
		mWheelView.setCurrentItem(CUR_SELECT_ITEM);
		
		mImgUp = (ImageView) mRelativeLayout.findViewById(R.id.wheel_top);
		mImgDown = (ImageView) mRelativeLayout.findViewById(R.id.wheel_bottom);
		mImgDown.setEnabled(false);
		
		mWheelView.addChangingListener(mOnWheelChangedListener);
		mWheelView.addScrollingListener(mOnWheelScrollListener);
		
		mImgUp.setOnClickListener(mBtnListener);
		mImgDown.setOnClickListener(mBtnListener);
	}
	
	private void initGridView() {
		mAdapter = new KeyMacroAdapter(mActivity, mList);
		mGridView = (GridView) mRelativeLayout.findViewById(R.id.keymacro_gridview);
		mGridView.setAdapter(mAdapter);
	}
	
	private void addEmpytView() {
		EmptyViewForListView emptyView = new EmptyViewForListView(mActivity, false);
		emptyView.setText(mRes.getString(R.string.MSG_KEYMACRO_DEF_EMPTY_SUMMARY));
		android.view.ViewGroup.LayoutParams mEmptyLp = mGridView.getLayoutParams();
		emptyView.setLayoutParams(mEmptyLp);  
		emptyView.setVisibility(View.GONE);  
		emptyView.setGravity(Gravity.CENTER);
		((ViewGroup)mGridView.getParent()).addView(emptyView);  
		mGridView.setEmptyView(emptyView); 
	}

	private void initBtn() {
		ImageView mCloseImg = (ImageView) mRelativeLayout.findViewById(R.id.keymacro_close);
		ImageView mResetImg = (ImageView) mRelativeLayout.findViewById(R.id.keymacro_reset);
		ImageView mDelImg = (ImageView) mRelativeLayout.findViewById(R.id.keymacro_delete);

		mResetImg.setOnClickListener(mBtnListener);
		mDelImg.setOnClickListener(mBtnListener);
		mCloseImg.setOnClickListener(mBtnListener);
	}
	
	public void add(int key) {
		if(null == mList) {
			return;
		}
		if(key == KEY_SELECTED) {
			Toast.makeText(mActivity, mRes.getString(R.string.MSG_KEY_COMPOSE_MACRO_USED), Toast.LENGTH_SHORT).show();
			return;
		}
		if(mList.size() > 60) {
			toast(mRes.getString(R.string.MSG_KEYMACRO_LIMIT));
			return;
		}
		int type = getType(key);
		KeyMacroObj obj = new KeyMacroObj();
		obj.setKey(key);
		obj.setType(type);
		mList.add(obj);
		if(null != mAdapter) {
			mAdapter.notifyDataSetChanged();
		}
		
	}
	
	private int getType(int key) {
		int type = -1;
		switch(key) {
		case KEY_LEFT:
			type = 0;
			break;
		case KEY_UP:
			type = 1;
			break;
		case KEY_RIGHT:
			type = 2;
			break;
		case KEY_DOWN:
			type = 3;
			break;
		case KEY_LEFT_UP:
			type = 4;
			break;
		case KEY_LEFT_DOWN:
			type = 5;
			break;
		case KEY_RIGHT_UP:
			type = 6;	
			break;
		case KEY_RIGHT_DOWN:
			type = 7;
			break;
		case KEY_A:
			type = 8;
			break;
		case KEY_B:
			type = 9;
			break;
		case KEY_X:
			type = 10;
			break;
		case KEY_Y:
			type = 11;
			break;
		case KEY_AB:
			type = 12;
			break;
		case KEY_AX:
			type = 13;
			break;
		case KEY_AY:
			type = 14;
			break;
		case KEY_BX:
			type = 15;
			break;
		case KEY_BY:
			type = 16;
			break;
		case KEY_XY:
			type = 17;
			break;
		case KEY_ABX:
			type = 18;
			break;
		case KEY_ABY:
			type = 19;
			break;
		case KEY_AXY:
			type = 20;
			break;
		case KEY_BXY:
			type = 21;
			break;
		case KEY_ABXY:
			type = 22;
			break;
			default:
				type = 0;
				break;
		}
		return type;
	}
	
	private void selected(int id) {
		switch (id) {
		case 0:
			KEY_SELECTED = KEY_A;
			break;
		case 1:
			KEY_SELECTED = KEY_B;
			break;
		case 2:
			KEY_SELECTED = KEY_X;
			break;
		case 3:
			KEY_SELECTED = KEY_Y;
			break;
		case 4:
			KEY_SELECTED = KEY_L;
			break;
		case 5:
			KEY_SELECTED = KEY_R;
			break;
		default:
			break;
		}
	}
	
	private OnClickListener mBtnListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch(id) {
			case R.id.keymacro_reset:
				mHandler.obtainMessage(MSG_RESET).sendToTarget();
				break;
			case R.id.keymacro_delete:
				mHandler.obtainMessage(MSG_DELETE).sendToTarget();
				break;
			case R.id.wheel_top:
				mHandler.obtainMessage(MSG_TOP).sendToTarget();
				break;
			case R.id.wheel_bottom:
				mHandler.obtainMessage(MSG_BOTTOM).sendToTarget();
				break;
			case R.id.keymacro_close:
				mHandler.obtainMessage(MSG_CLOSE).sendToTarget();
				break;
			}
		}
		
	};
	
	private OnWheelChangedListener mOnWheelChangedListener = new OnWheelChangedListener() {

		@Override
		public void onChanged(WheelView wheel, int oldValue, int newValue) {
			if (!isScrolling) {
			}
		}
		
	};
	
	private OnWheelScrollListener mOnWheelScrollListener = new OnWheelScrollListener() {

		@Override
		public void onScrollingStarted(WheelView wheel) {
			isScrolling = true;
		}

		@Override
		public void onScrollingFinished(WheelView wheel) {
			isScrolling = false;
			CUR_SELECT_ITEM = wheel.getCurrentItem();
			selected(CUR_SELECT_ITEM);
			switch(CUR_SELECT_ITEM) {
			case 0:
				mImgUp.setEnabled(true);
				mImgDown.setEnabled(false);
				break;
			case 5:
				mImgUp.setEnabled(false);
				mImgDown.setEnabled(true);
				break;
			default:
				mImgUp.setEnabled(true);
				mImgDown.setEnabled(true);
				break;
			}
		}
		
	};

	private void keyReset() {
		if(null == mList) {
			return;
		}
		Emulator.restMultiKey();
		mList.clear();
		if(mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		} 
		
		String name = SharePreferenceUtil.loadName(mActivity);
		SharePreferenceUtil.saveKeysFour(mActivity, name, "");
		reset();
		
	}

	private void keyDelete() {
		if(null == mList) {
			return;
		}
		int size = mList.size();
		if(size > 0) {
			mList.remove(size - 1);
		}
		if(mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
	}
	
	private void keySave() {
		if(-1 == KEY_SELECTED) {
			keyBack();
			return;
		}
		
		int keys[] = getKey();
		int len = keys.length;
		if(0 == len) {
			keyBack();
			return;
		}
		
		
		for (int i = 0; i < len; i++) {
			if(KEY_SELECTED == keys[i]) {
				toast("Don't add self");
				return;
			}
		}
		
		mActivity.doAnim(false);
		Emulator.resume();
		
		if(null != mInputHandler) {
			mInputHandler.setCombinedKey(false);
		}
		update(KEY_SELECTED, keys);
		save(KEY_SELECTED, keys);
	}

	private void keyBack() {
		if(null != mInputHandler) {
			mInputHandler.setCombinedKey(false);
		}
		
		mActivity.doAnim(false);
		Emulator.resume();
	}
	
	private void keyTop() {
		if(null == mWheelAdapter) {
			return;
		} 
		
		CUR_SELECT_ITEM++;
		
		int count = mWheelAdapter.getItemsCount()-1;
		if(CUR_SELECT_ITEM >= count) {
			CUR_SELECT_ITEM = count;
			mImgUp.setEnabled(false);
			mImgDown.setEnabled(true);
		} else {
			mImgUp.setEnabled(true);
			mImgDown.setEnabled(true);
		}
		
		if(null != mWheelView) {
			mWheelView.setCurrentItem(CUR_SELECT_ITEM);
		}
	}
	
	private void keyBottom() {
		CUR_SELECT_ITEM--;
		if(CUR_SELECT_ITEM <= 0) {
			CUR_SELECT_ITEM = 0;
			mImgDown.setEnabled(false);
			mImgUp.setEnabled(true);
		} else {
			mImgDown.setEnabled(true);
			mImgUp.setEnabled(true);
		}
		if(null != mWheelView) {
			mWheelView.setCurrentItem(CUR_SELECT_ITEM);
		}
		
	}
	
	private void keyClose() {	
		keySave();
	}
	
	private int[] getKey() {
		if(null == mList) {
			return null;
		}
		int size = mList.size();
		int keys[] = new int[size];
		for (int i = 0; i < size; i++) {
			KeyMacroObj obj = mList.get(i);
			if(null == obj) {
				continue;
			}
			keys[i] = obj.getKey();
		}
		return keys;
	}

	private void reset() {
		if(null == mKeyMacro) {
			return;
		}
		if(false == mKeyMacro.isKeyA()) {
			mKeyMacro.setKeyA(true);
		} 
		if(false == mKeyMacro.isKeyB()) {
			mKeyMacro.setKeyB(true);
		}
		if(false == mKeyMacro.isKeyX()) {
			mKeyMacro.setKeyX(true);
		}
		if(false == mKeyMacro.isKeyY()) {
			mKeyMacro.setKeyY(true);
		}
		if(false == mKeyMacro.isKeyL()) {
			mKeyMacro.setKeyL(true);
		}
		if(false == mKeyMacro.isKeyR()) {
			mKeyMacro.setKeyR(true);
		}
		
		KEY_SELECTED = KEY_A;
		CUR_SELECT_ITEM = 0;
		if(null != mWheelView) {
			mWheelView.setCurrentItem(CUR_SELECT_ITEM);
		}
		
		if(null != mInputView) {
			mInputView.setVisiable(-1, true);
			mInputView.resetKey(mKeyMacro.isA(), mKeyMacro.isB(), mKeyMacro.isX(), mKeyMacro.isY(), mKeyMacro.isL(), mKeyMacro.isR());	
			mInputView.update(-1, true);
		}
		
	}
	
	public void save(int key, int values[]) {
		String target = "";
		int len = values.length;
		for (int i = 0; i < len; i++) {
			target += (i == 0 ? key+SPLIT_ALARM : SPLIT_COMMA)+values[i];
		}
		System.out.println("--> target = " + target);
		String name = SharePreferenceUtil.loadName(mActivity);
		SharePreferenceUtil.saveKeysFour(mActivity, name, target);
	}
	
	public void load() {
		if(-1 == key && null == values) {
			return;
		}
		update(key, values);
	}
	
	public void update(int key, int values[]) {
		if(null != mKeyMacro) {
			mKeyMacro.setSelected(key, true);
		}
		
		if(null != mInputView) {
			mInputView.setVisiable(key, true);
			mInputView.resetKey(mKeyMacro.isA(), mKeyMacro.isB(), mKeyMacro.isX(), mKeyMacro.isY(), mKeyMacro.isL(), mKeyMacro.isR());
			mInputView.update(key, true);
		}
		Emulator.setMultiKey(key, values, values.length);
	}
	
	private void toast(String info) {
		Toast.makeText(mActivity, info, Toast.LENGTH_LONG).show();
	}
	
}
