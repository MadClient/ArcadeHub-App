package com.yunluo.android.arcadehub.views;

import com.yunluo.android.arcadehub.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.Toast;

public class ClearEditText extends EditText implements  
        OnFocusChangeListener, TextWatcher { 

    private Drawable mClearDrawable = null;
    private Drawable mSearchDrawable = null;
 
    public ClearEditText(Context context) { 
    	this(context, null); 
    } 
 
    public ClearEditText(Context context, AttributeSet attrs) { 
    	this(context, attrs, android.R.attr.editTextStyle); 
    } 
    
    public ClearEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    
    private void init() { 
    	
    	setHint("Please enter the address.");
    	setSingleLine(true);
    	setTextSize(15);
    	setBackgroundResource(R.drawable.search_bar_edit_selector);
    	
    	mSearchDrawable = getCompoundDrawables()[0];
    	if(null == mSearchDrawable) {
    		mSearchDrawable = getResources().getDrawable(R.drawable.search_bar_icon_normal); 
    	}
    	mSearchDrawable.setBounds(0, 0, mSearchDrawable.getIntrinsicWidth(), mSearchDrawable.getIntrinsicHeight());
    	mClearDrawable = getCompoundDrawables()[2]; 
        if (mClearDrawable == null) { 
        	mClearDrawable = getResources().getDrawable(R.drawable.emotionstore_progresscancelbtn); 
        } 
        mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(), mClearDrawable.getIntrinsicHeight()); 
        setClearIconVisible(false); 
        setOnFocusChangeListener(this); 
        addTextChangedListener(this); 
    } 

    @Override 
    public boolean onTouchEvent(MotionEvent event) { 
        if (getCompoundDrawables()[2] != null) { 
            if (event.getAction() == MotionEvent.ACTION_UP) { 
            	boolean touchable = event.getX() > (getWidth() - getPaddingRight() - mClearDrawable.getIntrinsicWidth()) && (event.getX() < ((getWidth() - getPaddingRight())));
                if (touchable) { 
                    this.setText(""); 
                } 
            } 
        } 
        if (getCompoundDrawables()[0] != null) {
        	if (event.getAction() == MotionEvent.ACTION_UP) { 
        		boolean touch = (event.getX() > getPaddingLeft()) && (event.getX() < getPaddingLeft()+mSearchDrawable.getIntrinsicWidth());
        		if(true == touch) {
        			if(null != mOnSearchListener) {
        				String content = this.getText().toString();
        				mOnSearchListener.doSearchListener(content);
        			}
        		}
        	}
        }
 
        return super.onTouchEvent(event); 
    } 
 
    @Override 
    public void onFocusChange(View v, boolean hasFocus) { 
        if (hasFocus) { 
            setClearIconVisible(getText().length() > 0); 
        } else { 
            setClearIconVisible(false); 
        } 
    } 

    protected void setClearIconVisible(boolean visible) { 
        Drawable right = visible ? mClearDrawable : null; 
        setCompoundDrawables(mSearchDrawable, getCompoundDrawables()[1], right, getCompoundDrawables()[3]); 
    } 
     
    @Override 
    public void onTextChanged(CharSequence s, int start, int count, 
            int after) { 
        setClearIconVisible(s.length() > 0); 
    } 
 
    @Override 
    public void beforeTextChanged(CharSequence s, int start, int count, 
            int after) { 
         
    } 
 
    @Override 
    public void afterTextChanged(Editable s) { 
         
    } 
   
    public void setShakeAnimation(){
    	this.setAnimation(shakeAnimation(5));
    }
    
    public static Animation shakeAnimation(int counts){
    	Animation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
    	translateAnimation.setInterpolator(new CycleInterpolator(counts));
    	translateAnimation.setDuration(1000);
    	return translateAnimation;
    }
 
    private OnSearchListener mOnSearchListener = null;
    
    public void setOnSearchListener(OnSearchListener listener) {
    	this.mOnSearchListener = listener;
    }
    public interface OnSearchListener {
    	public void doSearchListener(String content);
    }
 
}
