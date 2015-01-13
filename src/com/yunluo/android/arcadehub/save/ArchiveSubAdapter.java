package com.yunluo.android.arcadehub.save;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunluo.android.arcadehub.R;
import com.yunluo.android.arcadehub.utils.Utils;

public class ArchiveSubAdapter extends BaseAdapter {

	private Context mContext;
	private List<ArchiveObj> mList;
	
	public ArchiveSubAdapter(Context context, List<ArchiveObj> list) {
		this.mContext = context;
		this.mList = list;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int positon) {
		return mList.get(positon);
	}

	@Override
	public long getItemId(int positon) {
		// TODO Auto-generated method stub
		return positon;
	}

	@Override
	public View getView(int position, View view, ViewGroup viewgroup) {
		Holder mHolder = null;
		if(null == view) {
			mHolder = new Holder();
			view = LayoutInflater.from(mContext).inflate(R.layout.load_game_file_item, null);
			mHolder.icon = (ImageView) view.findViewById(R.id.load_game_icon);
			mHolder.desc = (TextView) view.findViewById(R.id.load_game_file_desc);
			mHolder.name = (TextView) view.findViewById(R.id.load_game_file_name);
			mHolder.count = (TextView) view.findViewById(R.id.load_game_file_count);
			view.setTag(mHolder);
		} else {
			mHolder = (Holder) view.getTag();
		}
		
		ArchiveObj obj = mList.get(position);
		if(null != obj) {
			Bitmap bmp = Utils.getImageFromAssetsFile(mContext, obj.getName());
			
			bmp = Utils.getRoundedCornerBitmap(bmp);
			mHolder.icon.setImageBitmap(bmp);
			mHolder.desc.setText(obj.getDesc());
			mHolder.name.setText(mContext.getResources().getString(R.string.LISTITEM_MENU_ARCHIVE)+": "+obj.getCount());
		}
		
		return view;
	}

	static class Holder {
		TextView desc;
		TextView name;
		TextView count;
		ImageView icon;
	}
}
