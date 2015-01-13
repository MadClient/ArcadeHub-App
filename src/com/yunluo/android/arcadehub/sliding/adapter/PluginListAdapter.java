/**
 * ArcadeHub
 * @filename: PluginAdapter.java
 */

package com.yunluo.android.arcadehub.sliding.adapter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fortysevendeg.swipelistview.SwipeListView;
import com.yunluo.android.arcadehub.GameListActivity;
import com.yunluo.android.arcadehub.Emulator;
import com.yunluo.android.arcadehub.R;
import com.yunluo.android.arcadehub.async.RomInfo;
import com.yunluo.android.arcadehub.utils.ContentValue;
import com.yunluo.android.arcadehub.utils.Debug;
import com.yunluo.android.arcadehub.utils.FileUtil;
import com.yunluo.android.arcadehub.utils.SharePreferenceUtil;
import com.yunluo.android.arcadehub.utils.Utils;

public class PluginListAdapter extends BaseAdapter {

    private AssetManager assetMgr;

    private GameListActivity mContext = null;

    private List<RomInfo> items = null;

    private LayoutInflater inflater;

    private static final int SINGLE_GAMES = 1; 

    private static final int NETPLAY_GAMES = 2; 

    private static final int BTPLAY_GAMES = 3; 	

    public static int TYPE_GAMES = SINGLE_GAMES; 

    private SwipeListView listView;	

    private int removeWidth = GameListActivity.SCREEN_WIDTH/5;
    
    private String names[] = null;
    
    private int nameSize = 0;
    public PluginListAdapter(GameListActivity context, List<RomInfo> list) {
        this.mContext = context;
        this.items = list;
        inflater = LayoutInflater.from(context);

        assetMgr = mContext.getAssets();

        try {
        	names = assetMgr.list("icon");
        	nameSize = names.length;
        	for (int i = 0; i < nameSize; i++) {
        		int index = names[i].indexOf(".png");
        		names[i] = names[i].substring(0, index);
			}
        	
		} catch (IOException e) {
		}
    }

    /*
     * (non-Javadoc)
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        return items.size();
    }

    /*
     * (non-Javadoc)
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    /*
     * (non-Javadoc)
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    @Override 
    public boolean isEnabled(int position) { 
        
        RomInfo mRom = items.get(position);
        if(null == mRom.getId()){
            return true; 
        }else{
            return false;
        }
    }
    
	/*
     * (non-Javadoc)
     * @see android.widget.Adapter#getView(int, android.view.View,
     * android.view.ViewGroup)
     */
    InputStream isMask = null;
    Bitmap bmpMask = null;
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
    	if(position >= items.size()) {
        	return null;
        }
    	
        PluginHolder holder = null;
        if (convertView == null) {
        	holder = new PluginHolder();
        	convertView = inflater.inflate(R.layout.list_plugin_item, null);
        	holder.icon = (ImageView)convertView.findViewById(R.id.plugin_list_img);
        	holder.icon_1 = (ImageView)convertView.findViewById(R.id.plugin_list_img_mask);
        	holder.title = (TextView)convertView.findViewById(R.id.plugin_list_titile);
        	holder.content = (TextView)convertView.findViewById(R.id.plugin_list_content);
        	holder.romSize = (TextView)convertView.findViewById(R.id.plugin_list_size);
        	holder.remove = (TextView)convertView.findViewById(R.id.plugin_list_remove_tv);
        	holder.layoutSingle = (LinearLayout)convertView.findViewById(R.id.plugin_single_game);
        	holder.layoutWifi = (LinearLayout)convertView.findViewById(R.id.plugin_wifi_game);
        	holder.layoutBluetooth = (LinearLayout)convertView.findViewById(R.id.plugin_bluetooth_game);
        	holder.dlLayout = (RelativeLayout)convertView.findViewById(R.id.plugin_download_layout);
        	holder.dlSize = (TextView)convertView.findViewById(R.id.plugin_download_size);
        	holder.dlPrecent = (TextView)convertView.findViewById(R.id.plugin_download_precent);
        	holder.dlProgress = (ProgressBar)convertView.findViewById(R.id.plugin_download_progress);
        	holder.pluginHehind = (RelativeLayout)convertView.findViewById(R.id.plugin_list_game_bottom);

        	RelativeLayout.LayoutParams removeLp = (RelativeLayout.LayoutParams) holder.remove.getLayoutParams();
        	removeLp.width = removeWidth;
        	holder.remove.setLayoutParams(removeLp);

        	convertView.setTag(holder);
        } else {
            holder = (PluginHolder)convertView.getTag();
        }

        RomInfo mRom = items.get(position);
        InputStream is = null;
        String romName = mRom.getName();
        try {
        	is = assetMgr.open("icon/" + romName + ".png");
        	mRom.setIcon(romName);
        } catch (IOException e) {
        	is = null;
        }
        
        if(null == is) {
        	String icon = mRom.getIcon();
        	if(null != icon) {
        		try {
        			is = assetMgr.open("icon/" + icon + ".png");
        			mRom.setIcon(icon);
        		} catch (IOException e) {
        			is = null;
        		}
        	}

        	if(null == is) {
        		
        		String def = null;
        		for (int i = 0; i < nameSize; i++) {
        			float f = Utils.getSimilarityRatio(names[i], romName);
        			int half = romName.length()/2;
        			String target = romName.substring(0, half);
        			if(names[i].startsWith(target) && f > 0.7) {
        				def = names[i];
        				break;
        			}
        		}
        		
        		if(null != def) {
        			try {
        				is = assetMgr.open("icon/" + def + ".png");
        				mRom.setIcon(def);
        			} catch (IOException e) {
        				is = null;
        			}
        		} 
        	}

        	if(null == is) {
        		String filepath = mRom.getFilepath();
        		if("src/mame/drivers/neodrvr.c".equals(filepath)) {
        			try {
        				is = assetMgr.open("icon/default_neogeo.png");
        				mRom.setIcon("default_neogeo");
        			} catch (IOException e) {
        				is = null;
        			}
        		}
        	}

        	if(null == is) {
        		try {
        			is = assetMgr.open("icon/default0.png");
        			mRom.setIcon("default0");
        		} catch (IOException e) {
//        			e.printStackTrace();
        		}
        	}
        }

        Bitmap bmp = BitmapFactory.decodeStream(is);
        if (bmp != null) {
            holder.icon.setImageBitmap(bmp);
            holder.icon.setScaleType(ScaleType.FIT_CENTER);
        }
        if(null == isMask || bmpMask == null){
            try {
                isMask = assetMgr.open("icon/icon_mask.png");
            } catch (IOException e) {
                is = null;
            } 
            bmpMask = BitmapFactory.decodeStream(isMask);
        }
        if (null != bmpMask) {    
            holder.icon_1.setImageBitmap(bmpMask);
            holder.icon_1.setScaleType(ScaleType.FIT_CENTER);
        }
        
        holder.title.setText(mRom.getDesc());
        if (null == mRom.getId()) {
        	holder.pluginHehind.setVisibility(View.VISIBLE);
            holder.dlLayout.setVisibility(View.GONE);
            holder.content.setVisibility(View.VISIBLE);
            holder.romSize.setVisibility(View.VISIBLE);
            
            holder.content.setText(mRom.getPath().replace(ContentValue.SDCADE_PATH, "")+File.separator+ mRom.getName()+".zip");
            holder.romSize.setText(mRom.getSize());
            setOnClickListener(holder, position);
        } else {
        	holder.pluginHehind.setVisibility(View.GONE);
            holder.dlLayout.setVisibility(View.VISIBLE);
            holder.content.setVisibility(View.GONE);
            holder.romSize.setVisibility(View.GONE);
            
            String size = mRom.getSize();
            if (null == size) {
                size = "0";
            }
            holder.dlSize.setText(size + " MB");
            holder.dlPrecent.setText(mRom.getPrecent() + "%");
            holder.dlProgress.setProgress(mRom.getPrecent());
        }

        return convertView;
    }
    
    private void setOnClickListener(PluginHolder holder, final int position) {
    	 holder.remove.setOnClickListener(new OnClickListener() {

             @Override
             public void onClick(View view) {
                 deleteDialog(view.getParent().getParent(), position);
             }

         });
    	 
         holder.layoutSingle.setOnClickListener(new OnClickListener() {

             @Override
             public void onClick(View view) {
                 option(position, SINGLE_GAMES);
             }

         });
         holder.layoutWifi.setOnClickListener(new OnClickListener() {

             @Override
             public void onClick(View view) {
                 option(position, NETPLAY_GAMES);
             }

         });
         holder.layoutBluetooth.setOnClickListener(new OnClickListener() {

             @Override
             public void onClick(View view) {
                 option(position, BTPLAY_GAMES);
             }

         });
    }
    
	private void removeItem(ViewParent viewParent, final int position) {
	    if(null == viewParent) {
	        return;
	    }
	    
		final Animation animation = (Animation) AnimationUtils.loadAnimation(mContext, R.anim.plugin_item_delete);
		animation.setAnimationListener(new AnimationListener() {
			public void onAnimationStart(Animation animation) {
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {
				RomInfo rom = items.remove(position);
				notifyDataSetChanged();
				if(null != rom) {
				    final String path = rom.getPath();
				    final String name = rom.getName();
				    new Thread() {
			            public void run(){
			                String tmp = "";
			                if(path.startsWith("/files")) {
			                    tmp = "/data/data/com.yunluo.android.arcadehub"+ path;
			                    Utils.chmodeFile(tmp);
			                }
			                FileUtil.deleteFile(tmp+File.separator + name + ".zip");
			            }
			        }.start();
				}
			}
		});

		((ViewGroup) viewParent).startAnimation(animation);
	}
	
	private void deleteDialog(final ViewParent viewParent, final int position) {
	    closeAnimate(position);
	    
	    new AlertDialog.Builder(mContext)
    	    .setTitle(mContext.getResources().getString(R.string.BTN_COMMON_DELETE))
    	    .setMessage(mContext.getResources().getString(R.string.gamelist_alert_delete_msg))
    	    .setPositiveButton(mContext.getResources().getString(R.string.BTN_COMMON_OK), new DialogInterface.OnClickListener() {
    
    	        @Override
    	        public void onClick(DialogInterface dialog, int which) {
    	            removeItem(viewParent, position);
    	        }
    	    })
    	    .setNegativeButton(mContext.getResources().getString(R.string.BTN_COMMON_CANCEL), new DialogInterface.OnClickListener() {
    
    	        @Override
    	        public void onClick(DialogInterface dialog, int which) {
    
    	        }
    	    }).show();
	}

    private void option(int position, int type) {
    	TYPE_GAMES = type;
    	closeAnimate(position);
    	boolean boo = save(position);
    	if(true == boo) {
    		switch (type) {
    		case SINGLE_GAMES:
    			if (false == FileUtil.exists(SharePreferenceUtil.loadPath(mContext), SharePreferenceUtil.loadName(mContext))) {
    				Emulator.dimissLoading();
    				Toast.makeText(mContext, "The game does not currently exist.", Toast.LENGTH_SHORT).show();
    				return;
    			}
    			mContext.showGamesPlay(true);
    			break;
    		case NETPLAY_GAMES:
    			mContext.startNetplay();
    			break;
    		case BTPLAY_GAMES:
    			mContext.startBtPlay();
    			break;
    		default:
    			Debug.d("This action does not exist.", "");
    			break;
    		}
    	}
    }
    
    public void setListView(SwipeListView listView) {
        this.listView = listView;
    }
    
    private boolean save(int position) {
    	if (null != items && items.size() >= 1) {
    		Emulator.showLoading();
    		RomInfo mRomInfo = items.get(position);
    		if (null != mRomInfo) {
    			SharePreferenceUtil.saveName(mContext, mRomInfo.getName());
    			SharePreferenceUtil.savePath(mContext, mRomInfo.getPath());
    			SharePreferenceUtil.saveDesc(mContext, mRomInfo.getDesc());
    			return true;
    		}
    	}
    	return false;
    }
    
    private void closeAnimate(int position) {
    	if (null != listView) {
			listView.closeAnimate(position);
		}
    }

    public void updateView(final int index) {
    	PluginHolder holder = getPluginHolder(index);
    	if(null != holder) {
    	    int z = items.size();
            if(0 == z){
                return;
            }
    		RomInfo mRom = items.get(index);
    		if(null != mRom.getId()) {
    			holder.pluginHehind.setVisibility(View.GONE);
    			holder.dlLayout.setVisibility(View.VISIBLE);
    			holder.content.setVisibility(View.GONE);
    			holder.romSize.setVisibility(View.GONE);
    			
    			setClicked(holder, false);
    			
    			String size = mRom.getSize();
    			if (null == size) {
    				size = "0";
    			}
    			holder.dlSize.setText(size + " M");
    			holder.dlPrecent.setText(mRom.getPrecent() + "%");
    			holder.dlProgress.setProgress(mRom.getPrecent());
    		} else {
    			holder.pluginHehind.setVisibility(View.VISIBLE);
    			holder.dlLayout.setVisibility(View.GONE);
    			holder.content.setVisibility(View.VISIBLE);
    			holder.romSize.setVisibility(View.VISIBLE);

    			holder.content.setText(mRom.getPath().replace(ContentValue.SDCADE_PATH, "")+File.separator+ mRom.getName()+".zip");
    			holder.romSize.setText(mRom.getSize()+" M");
    			setClicked(holder, true);
    			setOnClickListener(holder, index);
    		}
    	}
    }
    
    private void setClicked(PluginHolder holder, boolean boo) {
        holder.layoutSingle.setClickable(boo); 
        holder.layoutWifi.setClickable(boo);
        holder.layoutBluetooth.setClickable(boo);
		holder.remove.setClickable(boo); 
    }
    
    private PluginHolder getPluginHolder(int position) {
    	PluginHolder holder = null;
    	int visiblePosition = listView.getFirstVisiblePosition();
    	int target = position - visiblePosition;
    	if(target >= 0) {
    		View view = listView.getChildAt(target);
    		if (null == view) {
    			return null;
    		}
    		holder = (PluginHolder) view.getTag();

    		if(null == holder) {
    			return null;
    		}
    	}
    	return holder;
    }
    
    public void setClicked(int position, boolean boo) {
    	PluginHolder holder = getPluginHolder(position);
    	if(null != holder) {
    		if(true == boo) {
    			holder.layoutSingle.setClickable(true); 
    		    holder.layoutWifi.setClickable(true);
    		    holder.layoutBluetooth.setClickable(true);
    			holder.remove.setClickable(false); 
    		} else { 
    		    holder.layoutSingle.setClickable(false); 
                holder.layoutWifi.setClickable(false);
                holder.layoutBluetooth.setClickable(false);
    			holder.remove.setClickable(true); 
    		}
    	}
    }
    
    static class PluginHolder {
    	
    	RelativeLayout pluginHehind = null;
    	
        ImageView icon = null;

        TextView remove = null;

        TextView content = null;

        TextView title = null;
        
        LinearLayout layoutSingle = null;
        
        LinearLayout layoutWifi = null;
        
        LinearLayout layoutBluetooth = null;
        
        TextView romSize = null;
        
        ImageView icon_1 = null;

        RelativeLayout dlLayout = null;

        TextView dlSize = null;

        TextView dlPrecent = null;

        ProgressBar dlProgress = null;
        
    }


}
