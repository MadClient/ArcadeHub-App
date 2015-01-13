package com.yunluo.android.arcadehub.netplay.adapter;

import java.util.ArrayList;
import android.text.Html;
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

public class NetDeviceAdapter extends BaseAdapter {

	private static final int TYPE_CATEGORY_ITEM = 0;
	private static final int TYPE_ITEM = 1;
	
	private ArrayList<Category> mCategoryList = null;
	private LayoutInflater mInflater = null;
	
	private NetPlayActivity mContext = null;
	
	public NetDeviceAdapter(NetPlayActivity context, ArrayList<Category> categoryList) {
		this.mContext = context;
		mCategoryList = categoryList;
		mInflater  = LayoutInflater.from(context);
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
			if (categoryIndex < size) {
				return  category.getItem(categoryIndex);
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
			if (categoryIndex == 0 && false == category.isHideTitle()) {
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
			DeviceHolder mHolder = null;
			if(null == convertView) {
				mHolder = new DeviceHolder();
				convertView = mInflater.inflate(R.layout.list_devide_info_item, null);
				mHolder.roomName = (TextView) convertView.findViewById(R.id.device_room);
				mHolder.img = (ImageView) convertView.findViewById(R.id.device_os_img);
				mHolder.desc = (TextView) convertView.findViewById(R.id.device_gamename);
				mHolder.ip = (TextView) convertView.findViewById(R.id.device_ip);
				mHolder.cpu = (TextView) convertView.findViewById(R.id.device_cpu);
				mHolder.ram = (TextView) convertView.findViewById(R.id.device_ram);
				convertView.setTag(mHolder);
			} else {
				mHolder = (DeviceHolder) convertView.getTag();
			}
			
			DeviceInfo info = (DeviceInfo) getItem(position);
			if(null == info) {
				return null;
			}
			int os = info.getOs();
			if(os == 1) {
				mHolder.img.setImageResource(R.drawable.ic_android);
			} else {
				mHolder.img.setImageResource(R.drawable.ic_iphone);
			}
			
			String ipC = "<font color='#000000'>"+info.getIp()+"</font>";
			String cpuC = "<font color='#000000'>"+info.getCpu()+"</font>";
			String ramC = "<font color='#000000'>"+info.getRam()+"</font>";
			
			mHolder.roomName.setText(mContext.getResources().getString(R.string.MSG_ROOM) + info.getId()+" -- ");
			mHolder.desc.setText(info.getDesc());
			mHolder.ip.setText("IP: ");
			mHolder.cpu.setText("\tCPU: ");
			mHolder.ram.setText("\tRAM: ");
			
			mHolder.ip.append(Html.fromHtml(ipC));
			mHolder.cpu.append(Html.fromHtml(cpuC));
			mHolder.ram.append(Html.fromHtml(ramC));
			break;
		}
		
		return convertView;
	}
	
	static class DeviceHolder {
	    TextView roomName;
		ImageView img;
		TextView desc;
		TextView ip;
		TextView cpu;
		TextView ram;
	}
	
	static class TitleHolder {
		TextView title;
	}
}
