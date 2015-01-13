package com.yunluo.android.arcadehub.netplay.adapter;

import java.util.ArrayList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.yunluo.android.arcadehub.R;
import com.yunluo.android.arcadehub.netplay.Category;
import com.yunluo.android.arcadehub.netplay.NetPlayActivity;
import com.yunluo.android.arcadehub.netplay.obj.DeviceInfo;
import com.yunluo.android.arcadehub.utils.Debug;

public class BtDeviceAdapter extends BaseAdapter {
	
	private static final int TYPE_CATEGORY_ITEM = 0;
	private static final int TYPE_ITEM = 1;
	
	private NetPlayActivity mContext = null;
	private ArrayList<Category> mCategoryList = null;
	private LayoutInflater mInflater = null;
	
	public BtDeviceAdapter(NetPlayActivity mm, ArrayList<Category> categoryList) {
		mContext = mm;
		this.mCategoryList = categoryList;
		mInflater  = LayoutInflater.from(mContext);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public int getCount() {
		int count = 0;
		
		if (null != mCategoryList) {
			for (Category category : mCategoryList) {
				count += category.getItemCount();
			}
		}
		
		return count;
	}

	@Override
	public Object getItem(int position) {
		if (null == mCategoryList || position <  0|| position > getCount()) {
			return null; 
		}
		int categroyFirstIndex = 0;
		
		for (Category category : mCategoryList) {
			int size = category.getItemCount();
			int categoryIndex = position - categroyFirstIndex;
			if (categoryIndex < size && false == category.isHideTitle()) {
				return  category.getItem( categoryIndex );
			}
			categroyFirstIndex += size;
		}
		
		return null;
	}

	@Override
	public int getItemViewType(int position) {
		if (null == mCategoryList || position <  0|| position > getCount()) {
			return TYPE_ITEM; 
		}
		
		
		int categroyFirstIndex = 0;
		
		for (Category category : mCategoryList) {
			int size = category.getItemCount();
			int categoryIndex = position - categroyFirstIndex;
			if (categoryIndex == 0) {
				return TYPE_CATEGORY_ITEM;
			}
			
			categroyFirstIndex += size;
		}
		
		return TYPE_ITEM;
	}
	
	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override 
	public boolean isEnabled(int position) { 
		int itemViewType = getItemViewType(position);
		switch (itemViewType) {
		case TYPE_CATEGORY_ITEM:
			return false;
		case TYPE_ITEM:
			return true;
		}
		return true;
	} 

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int itemViewType = getItemViewType(position);
		switch (itemViewType) {
		case TYPE_CATEGORY_ITEM:
			TitleHolder holder = null;
			if(null == convertView) {
			    holder = new TitleHolder();
			    convertView = mInflater.inflate(R.layout.netplay_info_header, null);
			    convertView.setTag(holder);
			} else {
			    holder = (TitleHolder) convertView.getTag();
			}
			mContext.updateHeader(convertView);
			break;
		case TYPE_ITEM:
			Holder mHolder = null;
			if(convertView == null) {
				mHolder = new Holder();
				convertView = LayoutInflater.from(mContext).inflate(R.layout.device_info_item, null);
				mHolder.roomName = (TextView) convertView.findViewById(R.id.bt_device_room);
				mHolder.deviceName = (TextView) convertView.findViewById(R.id.bt_device_name);
				mHolder.gameName = (TextView) convertView.findViewById(R.id.bt_device_gamename);
				mHolder.cpu = (TextView) convertView.findViewById(R.id.bt_device_cpu);
				mHolder.ram = (TextView) convertView.findViewById(R.id.bt_device_ram);
				mHolder.os = (ImageView) convertView.findViewById(R.id.bt_device_os_img);
				convertView.setTag(mHolder);
			} else {
				mHolder = (Holder) convertView.getTag();
			}
			
			DeviceInfo info = (DeviceInfo) getItem(position);
			if(null != info) {
			    mHolder.roomName.setText(mContext.getResources().getString(R.string.MSG_ROOM)+info.getId()+" -- ");
				mHolder.deviceName.setText(info.getDeviceName());
				mHolder.gameName.setText(info.getDesc());
				mHolder.cpu.setText("\tCPU: "+info.getCpu());
				mHolder.ram.setText("\tRAM："+info.getRam());
				mHolder.os.setImageResource(R.drawable.ic_android);
			}
			break;
		}
		return convertView;
	}

	static class Holder {
	    TextView roomName = null;
		TextView deviceName = null;
		TextView gameName = null;
		TextView cpu = null;
		TextView ram = null;
		ImageView os = null;
	}
	
	static class TitleHolder {
		TextView title;
	}
}
