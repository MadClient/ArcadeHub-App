package com.yunluo.android.arcadehub.keymacro;

import com.yunluo.android.arcadehub.R;
import com.yunluo.android.arcadehub.combination.wheel.adapter.AbstractWheelTextAdapter;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class WheelAdapter extends AbstractWheelTextAdapter {

	private int ids[] = {
			R.drawable.ic_btn_green_a, 
			R.drawable.ic_btn_green_b, 
			R.drawable.ic_btn_green_x, 
			R.drawable.ic_btn_green_y, 
			R.drawable.ic_btn_green_l, 
			R.drawable.ic_btn_green_r
	};
	
	protected WheelAdapter(Context context) {
		super(context, R.layout.list_wheel_item, NO_RESOURCE);
	}

	@Override
	public int getItemsCount() {
		return ids.length;
	}

	@Override
	protected CharSequence getItemText(int index) {
		return null;
	}
	
	@Override
	public View getItem(int index, View cachedView, ViewGroup parent) {
		View view = super.getItem(index, cachedView, parent);
		ImageView img = (ImageView) view.findViewById(R.id.flag);
		img.setImageResource(ids[index]);
		return view;
	}


	

}
