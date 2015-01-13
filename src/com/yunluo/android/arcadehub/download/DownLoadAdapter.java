package com.yunluo.android.arcadehub.download;

import java.util.List;

import com.yunluo.android.arcadehub.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DownLoadAdapter extends BaseAdapter {

	private List<DownLoadObj> mList = null;
	private LayoutInflater mInflater = null;
	
	public DownLoadAdapter(Context context,
			List<DownLoadObj> list) {
		this.mList = list;
		this.mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return (null == mList) ? 0 : mList.size();
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
			convertView = mInflater.inflate(R.layout.download_item, null);
			mHolder.title = (TextView) convertView.findViewById(R.id.dl_title);
			mHolder.url = (TextView) convertView.findViewById(R.id.dl_url);
			convertView.setTag(mHolder);
		} else {
			mHolder = (Holder) convertView.getTag();
		}
		DownLoadObj obj = mList.get(position);
		if(null == obj) {
			return null;
		}
		mHolder.title.setText(obj.getTitle());
		mHolder.url.setText(obj.getUrl());
		return convertView;
	}
	
	static class Holder {
		TextView title;
		TextView url;
	}

}
