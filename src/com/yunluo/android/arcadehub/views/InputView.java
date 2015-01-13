/*
 * This file is part of MAME4droid.
 *
 * Copyright (C) 2013 David Valdeita (Seleuco)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 * Linking MAME4droid statically or dynamically with other modules is
 * making a combined work based on MAME4droid. Thus, the terms and
 * conditions of the GNU General Public License cover the whole
 * combination.
 *
 * In addition, as a special exception, the copyright holders of MAME4droid
 * give you permission to combine MAME4droid with free software programs
 * or libraries that are released under the GNU LGPL and with code included
 * in the standard release of MAME under the MAME License (or modified
 * versions of such code, with unchanged license). You may copy and
 * distribute such a system following the terms of the GNU GPL for MAME4droid
 * and the licenses of the other code concerned, provided that you include
 * the source code of that other code when and as the GNU GPL requires
 * distribution of source code.
 *
 * Note that people who make modified versions of MAME4idroid are not
 * obligated to grant this special exception for their modified versions; it
 * is their choice whether to do so. The GNU General Public License
 * gives permission to release a modified version without this exception;
 * this exception also makes it possible to release a modified version
 * which carries forward this exception.
 *
 * MAME4droid is dual-licensed: Alternatively, you can license MAME4droid
 * under a MAME license, as set out in http://mamedev.org/
 */

package com.yunluo.android.arcadehub.views;

import java.util.ArrayList;
import java.util.Locale;
import com.yunluo.android.arcadehub.Emulator;
import com.yunluo.android.arcadehub.GamePlayActivity;
import com.yunluo.android.arcadehub.R;
import com.yunluo.android.arcadehub.helpers.PrefsHelper;
import com.yunluo.android.arcadehub.input.ControlCustomizer;
import com.yunluo.android.arcadehub.input.InputHandler;
import com.yunluo.android.arcadehub.input.InputValue;
import com.yunluo.android.arcadehub.input.TiltSensor;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class InputView extends ImageView {

	protected GamePlayActivity mm = null;
	protected Bitmap bmp = null;
	protected Paint pnt = new Paint();
	protected Rect rsrc = new Rect();
	protected Rect rdst = new Rect();
	protected Rect rclip = new Rect();
	protected int ax = 0;
	protected int ay = 0;
	protected float dx = 1;
	protected float dy = 1;
	static BitmapDrawable btns_images[][] = null;

	private boolean isLV = false;
	private boolean isRV = false;
	private boolean isAV = false;
	private boolean isYV = false;

	public final static int KEY_A_ID = 4096;
	public final static int KEY_Y_ID = 32768;
	public final static int KEY_L_ID = 1024;
	public final static int KEY_R_ID = 2048;
	public static int NEXT_MULTIKEY = -1;

	public void setMAME4droid(GamePlayActivity mm) {
		this.mm = mm;
		if (mm == null)
			return;

		if (btns_images == null) {
			btns_images = new BitmapDrawable[InputHandler.NUM_BUTTONS][2];
			initImg();
		} else {
			initImg();
		}
		
	}

	private void initImg() {
		if(null == btns_images) {
			return;
		}
		
		btns_images[InputHandler.BTN_A][InputHandler.BTN_NO_PRESS_STATE] = (BitmapDrawable) mm
				.getResources().getDrawable(R.drawable.ic_btn_red_a);
		btns_images[InputHandler.BTN_A][InputHandler.BTN_PRESS_STATE] = (BitmapDrawable) mm
				.getResources().getDrawable(R.drawable.ic_btn_red_a_press);
		
		btns_images[InputHandler.BTN_B][InputHandler.BTN_NO_PRESS_STATE] = (BitmapDrawable) mm
				.getResources().getDrawable(R.drawable.ic_btn_red_b);
		btns_images[InputHandler.BTN_B][InputHandler.BTN_PRESS_STATE] = (BitmapDrawable) mm
				.getResources().getDrawable(R.drawable.ic_btn_red_b_press);
		
		btns_images[InputHandler.BTN_X][InputHandler.BTN_NO_PRESS_STATE] = (BitmapDrawable) mm
				.getResources().getDrawable(R.drawable.ic_btn_red_x);
		btns_images[InputHandler.BTN_X][InputHandler.BTN_PRESS_STATE] = (BitmapDrawable) mm
				.getResources().getDrawable(R.drawable.ic_btn_red_x_press);

		btns_images[InputHandler.BTN_Y][InputHandler.BTN_NO_PRESS_STATE] = (BitmapDrawable) mm
				.getResources().getDrawable(R.drawable.ic_btn_red_y);
		btns_images[InputHandler.BTN_Y][InputHandler.BTN_PRESS_STATE] = (BitmapDrawable) mm
				.getResources().getDrawable(R.drawable.ic_btn_red_y_press);

		btns_images[InputHandler.BTN_L1][InputHandler.BTN_NO_PRESS_STATE] = (BitmapDrawable) mm
				.getResources().getDrawable(R.drawable.ic_btn_red_l);
		btns_images[InputHandler.BTN_L1][InputHandler.BTN_PRESS_STATE] = (BitmapDrawable) mm
				.getResources().getDrawable(R.drawable.ic_btn_red_l_press);

		btns_images[InputHandler.BTN_R1][InputHandler.BTN_NO_PRESS_STATE] = (BitmapDrawable) mm
				.getResources().getDrawable(R.drawable.ic_btn_red_r);
		btns_images[InputHandler.BTN_R1][InputHandler.BTN_PRESS_STATE] = (BitmapDrawable) mm
				.getResources().getDrawable(R.drawable.ic_btn_red_r_press);
	}

	private void initImgEn() {
		if(null == btns_images) {
			return;
		}
		btns_images[InputHandler.BTN_L2][InputHandler.BTN_NO_PRESS_STATE] = (BitmapDrawable) mm
				.getResources().getDrawable(R.drawable.button_l2);
		btns_images[InputHandler.BTN_L2][InputHandler.BTN_PRESS_STATE] = (BitmapDrawable) mm
				.getResources().getDrawable(R.drawable.button_l2_press);

		btns_images[InputHandler.BTN_R2][InputHandler.BTN_NO_PRESS_STATE] = (BitmapDrawable) mm
				.getResources().getDrawable(R.drawable.button_r2);
		btns_images[InputHandler.BTN_R2][InputHandler.BTN_PRESS_STATE] = (BitmapDrawable) mm
				.getResources().getDrawable(R.drawable.button_r2_press);

		btns_images[InputHandler.BTN_START][InputHandler.BTN_NO_PRESS_STATE] = (BitmapDrawable) mm
				.getResources().getDrawable(R.drawable.button_start);
		btns_images[InputHandler.BTN_START][InputHandler.BTN_PRESS_STATE] = (BitmapDrawable) mm
				.getResources().getDrawable(R.drawable.button_start_press);

		btns_images[InputHandler.BTN_SELECT][InputHandler.BTN_NO_PRESS_STATE] = (BitmapDrawable) mm
				.getResources().getDrawable(R.drawable.button_select);
		btns_images[InputHandler.BTN_SELECT][InputHandler.BTN_PRESS_STATE] = (BitmapDrawable) mm
				.getResources().getDrawable(R.drawable.button_select_press);

	}

	final boolean isChinese() {
		String language = Locale.getDefault().getLanguage().substring(0, 2);
		return "zh".equalsIgnoreCase(language);
	}
	
	final int getLngType() {
	    int type = 2;
	    String language = Locale.getDefault().getLanguage().substring(0, 2);
	    if("zh".equalsIgnoreCase(language)) {
	        type = 1;
	    } else if("es".equalsIgnoreCase(language)){
	        type = 2; 
	    } else if("fr".equalsIgnoreCase(language)){
            type = 3; 
        } else if("jp".equalsIgnoreCase(language)){
            type = 4; 
        } else if("kr".equalsIgnoreCase(language)){
            type = 5; 
        } else if("pt".equalsIgnoreCase(language)){
            type = 6; 
        } else {
            type = 7; 
        }
	    return type;
	}

	public InputView(Context context) {
		super(context);
		init();
	}

	public InputView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public InputView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	protected void init() {
		pnt.setARGB(255, 255, 255, 255);
		pnt.setStyle(Style.STROKE);

		pnt.setARGB(255, 255, 255, 255);
		pnt.setTextSize(16);

		this.setFocusable(true);
		this.setFocusableInTouchMode(true);
	}

	@Override
	public void setImageDrawable(Drawable drawable) {
		if (drawable != null) {
			BitmapDrawable bmpdrw = (BitmapDrawable) drawable;
			bmp = bmpdrw.getBitmap();
		} else {
			bmp = null;
		}

		super.setImageDrawable(drawable);
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		if (mm == null) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			return;
		}

		int widthSize = 1;
		int heightSize = 1;

		if (mm.getMainHelper().getscrOrientation() == Configuration.ORIENTATION_LANDSCAPE) {
			widthSize = mm.getWindowManager().getDefaultDisplay().getWidth();
			heightSize = mm.getWindowManager().getDefaultDisplay().getHeight();
		} else {
			int w = 1;
			int h = 1;

			if (mm != null && mm.getInputHandler().getMainRect() != null) {
				w = mm.getInputHandler().getMainRect().width();
				h = mm.getInputHandler().getMainRect().height();
			}

			if (w == 0)
				w = 1;
			if (h == 0)
				h = 1;

			float desiredAspect = (float) w / (float) h;

			widthSize = mm.getWindowManager().getDefaultDisplay().getWidth();
			heightSize = (int) (widthSize / desiredAspect);
		}

		setMeasuredDimension(widthSize, heightSize);
	}

	public void updateImages() {
		ArrayList<InputValue> data = mm.getInputHandler().getAllInputData();

		if (data == null)
			return;

		for (int i = 0; i < data.size(); i++) {
			InputValue v = data.get(i);
			if (v.getType() == InputHandler.TYPE_BUTTON_IMG) {
				btns_images[v.getValue()][InputHandler.BTN_PRESS_STATE]
						.setBounds(v.getRect());
				btns_images[v.getValue()][InputHandler.BTN_PRESS_STATE]
						.setAlpha(mm.getInputHandler().getOpacity());
				btns_images[v.getValue()][InputHandler.BTN_NO_PRESS_STATE]
						.setBounds(v.getRect());
				btns_images[v.getValue()][InputHandler.BTN_NO_PRESS_STATE]
						.setAlpha(mm.getInputHandler().getOpacity());
			}
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {

		super.onSizeChanged(w, h, oldw, oldh);

		int bw = 1;
		int bh = 1;

		if (mm != null && mm.getInputHandler().getMainRect() != null) {
			bw = mm.getInputHandler().getMainRect().width();
			bh = mm.getInputHandler().getMainRect().height();
		}

		if (bw == 0)
			bw = 1;
		if (bh == 0)
			bh = 1;

		float desiredAspect = (float) bw / (float) bh;

		int tmp = (int) ((float) w / desiredAspect);
		if (tmp <= h) {
			ax = 0;
			ay = (h - tmp) / 2;
			h = tmp;
		} else {
			tmp = (int) ((float) h * desiredAspect);
			ay = 0;
			ax = (w - tmp) / 2;
			w = tmp;
		}

		dx = (float) w / (float) bw;
		dy = (float) h / (float) bh;

		if (mm == null || mm.getInputHandler() == null)
			return;

		mm.getInputHandler().setFixFactor(ax, ay, dx, dy);

		updateImages();
	}

	@Override
	protected void onDraw(Canvas canvas) {

		if (bmp != null)
			super.onDraw(canvas);

		if (mm == null)
			return;

		ArrayList<InputValue> data = mm.getInputHandler().getAllInputData();

		boolean hideStick = (mm.getMainHelper().getscrOrientation() == Configuration.ORIENTATION_LANDSCAPE || mm
				.getMainHelper().getscrOrientation() == Configuration.ORIENTATION_PORTRAIT
				&& Emulator.isPortraitFull())
				&& (mm.getPrefsHelper().isHideStick()
						|| mm.getInputHandler().isControllerDevice() || (mm
						.getPrefsHelper().isLightgun() && Emulator.isInMAME() && !Emulator
							.isInMenu())) && !ControlCustomizer.isEnabled();

		int size = 0;
		if(null != data) {
			size = data.size();
		}
		
		for (int i = 0; i < size; i++) {
			InputValue v = data.get(i);
			BitmapDrawable d = null;
			canvas.getClipBounds(rclip);
			    if (mm.getPrefsHelper().getControllerType() != PrefsHelper.PREF_DIGITAL_DPAD
					&& v.getType() == InputHandler.TYPE_ANALOG_RECT
					&& rclip.intersect(v.getRect())) {
				if (!hideStick)
					mm.getInputHandler().getAnalogStick().draw(canvas);
			} else if (v.getType() == InputHandler.TYPE_BUTTON_IMG
					&& rclip.intersect(v.getRect())) {
				if (mm.getMainHelper().getscrOrientation() == Configuration.ORIENTATION_LANDSCAPE
						|| (mm.getMainHelper().getscrOrientation() == Configuration.ORIENTATION_PORTRAIT && Emulator
								.isPortraitFull())) {
					int n;
					if (mm.getInputHandler().isControllerDevice()
							|| (mm.getPrefsHelper().isLightgun()
									&& Emulator.isInMAME() && !Emulator
										.isInMenu()))
						n = 0;
					else if (!Emulator.isInMAME())
						n = 2;
					else {
						n = mm.getPrefsHelper().getNumButtons();
						if (n == -1) {
							n = Emulator.getValue(Emulator.NUMBTNS);
							if (n <= 2)
								n = 2;
							else if (n <= 4)
								n = 4;
							else
								n = 6;
						}
					}

					int b = v.getValue();
					if (!ControlCustomizer.isEnabled()) {

						if (b == InputHandler.BTN_B && n < 1)
							continue;
						if (b == InputHandler.BTN_X && n < 2)
							continue;
						if (n == 4) {
							if (b == InputHandler.BTN_A && n < 3) {
								continue;
							}
							if (b == InputHandler.BTN_Y && n < 4) {
								continue;
							}
							if ((b == InputHandler.BTN_L1) && (false == isLV())) {
								continue;
							}
							if ((b == InputHandler.BTN_R1) && (false == isRV())) {
								continue;
							}
						} else if (n == 6) {
							if (b == InputHandler.BTN_A && n < 3)
								continue;
							if (b == InputHandler.BTN_Y && n < 4)
								continue;
							if (b == InputHandler.BTN_L1 && n < 5)
								continue;
							if (b == InputHandler.BTN_R1 && n < 5)
								continue;
						} else {
							if ((b == InputHandler.BTN_A) && (false == isAV())) {
								continue;
							}
							if ((b == InputHandler.BTN_Y) && (false == isYV())) {
								continue;
							}
							if ((b == InputHandler.BTN_L1) && (false == isLV())) {
								continue;
							}
							if ((b == InputHandler.BTN_R1) && (false == isRV())) {
								continue;
							}
						}

					}
				}
				d = btns_images[v.getValue()][mm.getInputHandler()
						.getBtnStates()[v.getValue()]];
			}

			if (d != null) {
				d.draw(canvas);
			}
		}

		if (ControlCustomizer.isEnabled())
			mm.getInputHandler().getControlCustomizer().draw(canvas);

		if (Emulator.isDebug()) {
			ArrayList<InputValue> ids = mm.getInputHandler().getAllInputData();
			Paint p2 = new Paint();
			p2.setARGB(255, 255, 255, 255);
			p2.setStyle(Style.STROKE);

			for (int i = 0; i < ids.size(); i++) {
				InputValue v = ids.get(i);
				Rect r = v.getRect();
				if (r != null) {
					if (v.getType() == InputHandler.TYPE_BUTTON_RECT)
						canvas.drawRect(r, p2);
					else if (mm.getPrefsHelper().getControllerType() == PrefsHelper.PREF_DIGITAL_DPAD
							&& v.getType() == InputHandler.TYPE_STICK_RECT)
						canvas.drawRect(r, p2);
					else if (mm.getPrefsHelper().getControllerType() != PrefsHelper.PREF_DIGITAL_DPAD
							&& v.getType() == InputHandler.TYPE_ANALOG_RECT)
						canvas.drawRect(r, p2);
				}
			}

			p2.setTextSize(30);
			if (mm.getInputHandler().getTiltSensor().isEnabled()
					&& TiltSensor.str != null)
				canvas.drawText(TiltSensor.str, 100, 150, p2);
		}
	}
	
	public void update(int key, boolean type) {
		switch(key) {
		case 4096: // A
			updateA(type);
			break;
		case 8192: // B
			updateB(type);
			break;
		case 16384:	// X
			updateX(type);
			break;
		case 32768: // Y
			updateY(type);
			break;
		case 1024: // L
			updateL(type);
			break;
		case 2048: // R
			updateR(type);
			break;
		}
			
		updateImages();
		
		this.invalidate();
	}
	
	public void resetKey(boolean isA, boolean isB, boolean isX, boolean isY, boolean isL, boolean isR) {
		if(null == btns_images) {
			return;
		}
		if(true == isA) {
			initBtnImages(InputHandler.BTN_A, R.drawable.ic_btn_red_a, R.drawable.ic_btn_red_a_press);
		}
		if(true == isB) {
			initBtnImages(InputHandler.BTN_B, R.drawable.ic_btn_red_b, R.drawable.ic_btn_red_b_press);
		}
		if(true == isX) {
			initBtnImages(InputHandler.BTN_X, R.drawable.ic_btn_red_x, R.drawable.ic_btn_red_x_press);
		}
		if(true == isY) {
			initBtnImages(InputHandler.BTN_Y, R.drawable.ic_btn_red_y, R.drawable.ic_btn_red_y_press);
		}
		if(true == isL) {
			initBtnImages(InputHandler.BTN_L1, R.drawable.ic_btn_red_l, R.drawable.ic_btn_red_l_press);
		} 
		if(true == isR) {
			initBtnImages(InputHandler.BTN_R1, R.drawable.ic_btn_red_r, R.drawable.ic_btn_red_r_press);
		}

	}
	
	private void updateA(boolean type) {
		if(true == type) {
			initBtnImages(InputHandler.BTN_A, R.drawable.ic_btn_green_a, R.drawable.ic_btn_green_a_press);
		} else {
			initBtnImages(InputHandler.BTN_A, R.drawable.ic_btn_cyan_a, R.drawable.ic_btn_cyan_a_press);
		}
	}
	
	private void updateB(boolean type) {
		if(true == type) {
			initBtnImages(InputHandler.BTN_B, R.drawable.ic_btn_green_b, R.drawable.ic_btn_green_b_press);
		} else {
			initBtnImages(InputHandler.BTN_B, R.drawable.ic_btn_cyan_b, R.drawable.ic_btn_cyan_b_press);
		}
	}
	
	private void updateY(boolean type) {
		if(true == type) {
			initBtnImages(InputHandler.BTN_Y, R.drawable.ic_btn_green_y, R.drawable.ic_btn_green_y_press);
		} else {
			initBtnImages(InputHandler.BTN_Y, R.drawable.ic_btn_cyan_y, R.drawable.ic_btn_cyan_y_press);
		}
	}
	
	private void updateX(boolean type) {
		if(true == type) {
			initBtnImages(InputHandler.BTN_X, R.drawable.ic_btn_green_x, R.drawable.ic_btn_green_x_press);
		} else {
			initBtnImages(InputHandler.BTN_X, R.drawable.ic_btn_cyan_x, R.drawable.ic_btn_cyan_x_press);
		}
	}
	
	private void updateL(boolean type) {
		if(true == type) {
			initBtnImages(InputHandler.BTN_L1, R.drawable.ic_btn_green_l, R.drawable.ic_btn_green_l_press);
		} else {
			initBtnImages(InputHandler.BTN_L1, R.drawable.ic_btn_cyan_l, R.drawable.ic_btn_cyan_l_press);
		} 
	}

	private void updateR(boolean type) {
		if(true == type) {
			initBtnImages(InputHandler.BTN_R1, R.drawable.ic_btn_green_r, R.drawable.ic_btn_green_r_press);
		} else {
			initBtnImages(InputHandler.BTN_R1, R.drawable.ic_btn_cyan_r, R.drawable.ic_btn_cyan_r_press);
		}
	}
	
	private void initBtnImages(int key, int id, int press_id) {
		btns_images[key][InputHandler.BTN_NO_PRESS_STATE] = (BitmapDrawable) mm
				.getResources().getDrawable(id);
		btns_images[key][InputHandler.BTN_PRESS_STATE] = (BitmapDrawable) mm
				.getResources().getDrawable(press_id);
	}

	private boolean isLV() {
		return isLV;
	}

	public void setLV(boolean isLV) {
		this.isLV = isLV;
	}

	private boolean isRV() {
		return isRV;
	}

	public void setRV(boolean isRV) {
		this.isRV = isRV;
		
	}

	private boolean isAV() {
		return isAV;
	}

	public void setAV(boolean isAV) {
		this.isAV = isAV;
	}

	private boolean isYV() {
		return isYV;
	}

	public void setYV(boolean isYV) {
		this.isYV = isYV;
	}

	public void setVisiable(int type, boolean flag) {
		if (true == flag) {
			resetMultiKey();
			NEXT_MULTIKEY = type;
		} else {
		}
		switch (type) {
		case KEY_A_ID:
			setAV(true);
			break;
		case KEY_Y_ID:
			setYV(true);
			break;
		case KEY_L_ID:
			setLV(true);
			break;
		case KEY_R_ID:
			setRV(true);
			break;
		}
	}

	public void setResetVisiable(int type, boolean flag) {
        if (true == flag) {
            reset(type);
        }
    }

	private void resetMultiKey() {
		reset(NEXT_MULTIKEY);
	}

	private void reset(int key) {
		if (-1 != key) {
			switch (key) {
			case KEY_A_ID:
				setAV(false);
				break;
			case KEY_Y_ID:
				setYV(false);
				break;
			case KEY_L_ID:
				setLV(false);
				break;
			case KEY_R_ID:
				setRV(false);
				break;
			}
		}
	}

}
