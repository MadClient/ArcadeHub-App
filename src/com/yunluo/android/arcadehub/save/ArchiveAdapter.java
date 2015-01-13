package com.yunluo.android.arcadehub.save;

import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.yunluo.android.arcadehub.R;

public class ArchiveAdapter extends BaseAdapter {
	
	private Context mContext = null;
	private ArrayList<ArchiveObj> mList = null;

	public ArchiveAdapter(Context context, ArrayList<ArchiveObj> list) {
		this.mContext = context;
		this.mList = list;
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
		Holder mHolder = null;
		if(null == convertView) {
			mHolder = new Holder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.onfile_sta_item, null);
			mHolder.desc = (TextView) convertView.findViewById(R.id.onfile_descrition);
			mHolder.time = (TextView) convertView.findViewById(R.id.onfile_time);
			convertView.setTag(mHolder);
		} else {
			mHolder = (Holder) convertView.getTag();
		}
		
		mHolder.desc.setText(mList.get(position).getDesc());
		mHolder.time.setText(mList.get(position).getTime());
		return convertView;
	}

	class Holder{
		TextView desc;
		TextView time;
	}

}
