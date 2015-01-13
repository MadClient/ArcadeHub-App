package com.yunluo.android.arcadehub.cheat;

import java.util.ArrayList;
import java.util.HashMap;
import com.yunluo.android.arcadehub.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class CheatAdapter extends BaseAdapter {

	// private ArrayList<String> mList = null;
	ArrayList<CheatObject> mList = null;
	private Context mContext = null;
	private LayoutInflater inflate;
	public static HashMap<Integer, Boolean> selectedItems = new HashMap<Integer, Boolean>();

	public CheatAdapter(Context context, ArrayList<CheatObject> list) {
		this.mContext = context;
		this.mList = list;
		inflate = LayoutInflater.from(mContext);
	}

	public void updateData() {
		if (selectedItems.size() <= 0 && null != mList ) {
		    int j = mList.size();
			for (int i = 0; i < j; i++) {
				selectedItems.put(i, false);
			}
		}
	}
	
	/**
	 * clean cheat
	 */
	public void clear() {
		if(null != selectedItems) {
			selectedItems.clear();
		}
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int i) {
		return mList.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int i, View view, ViewGroup viewgroup) {
		Holder mHolder = null;
		if (null == view) {
			mHolder = new Holder();
			view = inflate.inflate(R.layout.cheat_item, null);
			mHolder.tv = (TextView) view.findViewById(R.id.cheat_item_tv);
			mHolder.cb = (CheckBox) view.findViewById(R.id.cheat_item_check);
			view.setTag(mHolder);
		} else {
			mHolder = (Holder) view.getTag();
		}

		mHolder.tv.setText(mList.get(i).getTitle());
		int color = mContext.getResources().getColor(R.color.black);
		mHolder.tv.setTextColor(color);
		if (null != selectedItems) {
		    Boolean isSelectedItems = selectedItems.get(i);
		    if(null !=  isSelectedItems){
		        mHolder.cb.setChecked(selectedItems.get(i));
		    }
		}
		return view;
	}

	static class Holder {
		TextView tv = null;
		CheckBox cb = null;
	}

}
