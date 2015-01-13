package com.yunluo.android.arcadehub.keymacro;

import android.app.Activity;
import com.yunluo.android.arcadehub.Emulator;
import com.yunluo.android.arcadehub.combination.OnCombinationListener;
import com.yunluo.android.arcadehub.utils.Debug;
import com.yunluo.android.arcadehub.utils.SharePreferenceUtil;

public class KeyMacro implements IKeyMacro {
	
	public static int NEXT_MULTIPRESS = -1;

	public static int NEXT_MULTIKEY = -1;

	private boolean isKeyA = true;
	
	private boolean isKeyB = true;
	
	private boolean isKeyX = true;
	
	private boolean isKeyY = true;
	
	private boolean isKeyL = true;
	
	private boolean isKeyR = true;
	
	private boolean isPressA = true;
	
	private boolean isPressB = true;
	
	private boolean isPressX = true;
	
	private boolean isPressY = true;
	
	private boolean isPressL = true;
	
	private boolean isPressR = true;
	
	private Activity mActivity = null;
	
	public KeyMacro(Activity activity) {
		this.mActivity = activity;
	}
	
	public boolean isA() {
		return (true == isKeyA && true == isPressA);
	}
	
	public boolean isB() {
		return (true == isKeyB && true == isPressB);
	}
	
	public boolean isX() {
		return (true == isKeyX && true == isPressX);
	}

	public boolean isY() {
		return(true == isKeyY && true == isPressY);
	}
	
	public boolean isL() {
		return (true == isKeyL && true == isPressL);
	}
	
	public boolean isR() {
		return (true == isKeyR && true == isPressR);
	}
	
	public boolean isKeyA() {
		return isKeyA;
	}

	public void setKeyA(boolean isKeyA) {
		this.isKeyA = isKeyA;
	}
	
	public boolean isKeyB() {
		return isKeyB;
	}

	public void setKeyB(boolean isKeyB) {
		this.isKeyB = isKeyB;
	}

	public boolean isKeyX() {
		return isKeyX;
	}

	public void setKeyX(boolean isKeyX) {
		this.isKeyX = isKeyX;
	}
	
	public boolean isKeyY() {
		return isKeyY;
	}

	public void setKeyY(boolean isKeyY) {
		this.isKeyY = isKeyY;
	}

	public boolean isKeyL() {
		return isKeyL;
	}

	public void setKeyL(boolean isKeyL) {
		this.isKeyL = isKeyL;
	}

	public boolean isKeyR() {
		return isKeyR;
	}

	public void setKeyR(boolean isKeyR) {
		this.isKeyR = isKeyR;
	}

	public boolean isPressA() {
		return isPressA;
	}

	public void setPressA(boolean isPressA) {
		this.isPressA = isPressA;
	}
	
	public boolean isPressB() {
		return isPressB;
	}

	public void setPressB(boolean isPressB) {
		this.isPressB = isPressB;
	}

	public boolean isPressX() {
		return isPressX;
	}

	public void setPressX(boolean isPressX) {
		this.isPressX = isPressX;
	}

	public boolean isPressY() {
		return isPressY;
	}

	public void setPressY(boolean isPressY) {
		this.isPressY = isPressY;
	}

	public boolean isPressL() {
		return isPressL;
	}

	public void setPressL(boolean isPressL) {
		this.isPressL = isPressL;
	}

	public boolean isPressR() {
		return isPressR;
	}

	public void setPressR(boolean isPressR) {
		this.isPressR = isPressR;
	}
	
	public int getCount() {
		int n = Emulator.getValue(Emulator.NUMBTNS);
		Debug.d("按键个数", " "+n+"个");
		return n;
	}

	public void setSelected(int type, boolean flag) {
		Debug.d("setSelected = ", " " + flag);
		if(true == flag) {
			resetSelectedPress(type);
			resetMultikey();
			selectedKey(type);
			NEXT_MULTIKEY = type;
		} else {
			resetSelectedKey(type);
			selectedPress(type);
			NEXT_MULTIPRESS = type;
		}
	}

	private void resetSelectedKey(int key) {
		switch(key) {
		case KEY_A:
			if(false == isKeyA()) {
				setKeyA(true);
				deleteKey();
			}
			break;
		case KEY_B:
			if(false == isKeyB()) {
				setKeyB(true);
				deleteKey();
			}
			break;
		case KEY_X:
			if(false == isKeyX()) {
				setKeyX(true);
				deleteKey();
			}
			break;
		case KEY_Y:
			if(false == isKeyY()) {
				setKeyY(true);
				deleteKey();
			}
			break;
		case KEY_L:
			if(false == isKeyL()) {
				setKeyL(true);
				deleteKey();
			}
			break;
		case KEY_R:
			if(false == isKeyR()) {
				setKeyR(true);
				deleteKey();
			}
			break;
		}
	}
	
	public void resetSelectedPress(int key) {
		switch(key) {
		case KEY_A:
			if(false == isPressA()) {
				setPressA(true);
				deletePress(key);
			}
			break;
		case KEY_B:
			if(false == isPressB()) {
				setPressB(true);
				deletePress(key);
			}
			break;
		case KEY_X:
			if(false == isPressX()) {
				setPressX(true);
				deletePress(key);
			}
			break;
		case KEY_Y:
			if(false == isPressY()) {
				setPressY(true);
				deletePress(key);
			}
			break;
		case KEY_L:
			if(false == isPressL()) {
				setPressL(true);
				deletePress(key);
			}
			break;
		case KEY_R:
			if(false == isPressR()) {
				setPressR(true);
				deletePress(key);
			}
			break;
		}
	}
	
	private void selectedPress(int type) {
		switch(type) {
		case KEY_A:
			setPressA(false);
			break;
		case KEY_B:
			setPressB(false);
			break;
		case KEY_X:
			setPressX(false);
			break;
		case KEY_Y:
			setPressY(false);
			break;
		case KEY_L:
			setPressL(false);
			break;
		case KEY_R:
			setPressR(false);
			break;
		}
	}
	
	private void selectedKey(int type) {
		switch(type) {
		case KEY_A:
			setKeyA(false);
			break;
		case KEY_B:
			setKeyB(false);
			break;
		case KEY_X:
			setKeyX(false);
			break;
		case KEY_Y:
			setKeyY(false);
			break;
		case KEY_L:
			setKeyL(false);
			break;
		case KEY_R:
			setKeyR(false);
			break;
		}
	}
	
	private void deleteKey() {
		if(null == mActivity) {
			return;
		}
		Emulator.restMultiKey();
		String name = SharePreferenceUtil.loadName(mActivity);
		SharePreferenceUtil.saveKeysFour(mActivity, name, "");
	}
	
    private void deletePress(int key) {
        if (null == mActivity) {
            return;
        }
        
        if(null == mOnCombinationListener) {
        	return;
        }
        mOnCombinationListener.onDelete(key);
    }
	
	public void resetMultipress(int key) {
		resetPress(key);
	}
	
	private void resetMultikey() {
		resetKey(NEXT_MULTIKEY);
	}

	private void resetKey(int type) {
		if(-1 != type) {
			switch(type) {
			case KEY_A:
				setKeyA(true);
				break;
			case KEY_B:
				setKeyB(true);
				break;
			case KEY_X:
				setKeyX(true);
				break;
			case KEY_Y:
				setKeyY(true);
				break;
			case KEY_L:
				setKeyL(true);
				break;
			case KEY_R:
				setKeyR(true);
				break;
			}
		}
	}
	
	private void resetPress(int type) {
		if(-1 != type) {
			switch(type) {
			case KEY_A:
				setPressA(true);
				break;
			case KEY_B:
				setPressB(true);
				break;
			case KEY_X:
				setPressX(true);
				break;
			case KEY_Y:
				setPressY(true);
				break;
			case KEY_L:
				setPressL(true);
				break;
			case KEY_R:
				setPressR(true);
				break;
			}
		}
	}
	
	private OnCombinationListener mOnCombinationListener = null;
	
	public void setOnCombinationListener(OnCombinationListener listener) {
		this.mOnCombinationListener = listener;
	}
}
