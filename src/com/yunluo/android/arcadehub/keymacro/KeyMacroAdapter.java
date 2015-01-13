package com.yunluo.android.arcadehub.keymacro;

import java.util.List;

import com.yunluo.android.arcadehub.R;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class KeyMacroAdapter extends BaseAdapter {

	private Context mContext = null;
	private List<KeyMacroObj> mList = null;
	private String[] ids = {"←", "↑", "→", "↓", "↖", "↘", "↗", "↘", "A", "B", "X", "Y","AB", "AX", "AY", "BX", "BY", "XY", "ABX", "ABY", "AXY", "BXY", "ABXY"};
	
	private LayoutInflater mInflater = null;
	private Typeface mTypeface = null;
	
	public KeyMacroAdapter(Context context, List<KeyMacroObj> list) {
		this.mContext = context;
		this.mList = list;
		this.mInflater = LayoutInflater.from(mContext);
		mTypeface  = Typeface.createFromAsset(mContext.getAssets(), "fonts/DroidSansFallbackLegacy.ttf"); 
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		Holder holder = null;
		if(null == convertView) {
			holder = new Holder();
			convertView = mInflater.inflate(R.layout.key_macro_item, null);
			holder.tv = (TextView) convertView.findViewById(R.id.keymacro_item_iv);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		
		if(null != mList) {
			KeyMacroObj obj = mList.get(position);
			holder.tv.setTypeface(mTypeface);
			holder.tv.setText(ids[obj.getType()]);
		}
		
		return convertView;
	}
	
	static class Holder {
		TextView tv = null;
	}

}
