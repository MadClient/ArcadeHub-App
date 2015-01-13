package com.yunluo.android.arcadehub.data;

import java.util.ArrayList;
import java.util.Locale;

import com.google.api.translate.Language;
import com.yunluo.android.arcadehub.Emulator;
import com.yunluo.android.arcadehub.GamePlayActivity;
import com.yunluo.android.arcadehub.cheat.CheatObject;
import com.yunluo.android.arcadehub.interfac.OnAddCheatListener;
import com.yunluo.android.arcadehub.utils.TranslateUtils;
import com.yunluo.android.arcadehub.utils.Utils;

/**
 * 
 * Save cheat list
 * @author MadClient
 *
 */
public class CheatListData implements OnAddCheatListener {

	private ArrayList<CheatObject> mCheatList = new ArrayList<CheatObject>();

	private String lng = Locale.getDefault().getLanguage().substring(0, 2);
	
	private GamePlayActivity mGamePlayActivity = null;
	
	static class CheatListDataHolder{
		public static CheatListData INSTANCE = new CheatListData();
	}
	
	private CheatListData() {
		Emulator.setOnAddCheatListener(this);
	}
	
	public static CheatListData getInstance() {
		return CheatListDataHolder.INSTANCE;
	}
	
	@Override
	public void onAddCheat(int index, String text) {
		CheatObject cheatObj = new CheatObject();
		cheatObj.setId(index);
		cheatObj.setTitle(text);
		if(null != mCheatList) {
			mCheatList.add(cheatObj);
		}
	}
	
	/**
     * change language
     */
    private String changeLanguage(String str) {
        String target = null;
        if("zh".equalsIgnoreCase(lng)) {
            if(true == Utils.isCN()) {
                target = TranslateUtils.translate(str, Language.ENGLISH, Language.CHINESE_SIMPLIFIED);
            } else {
                target = TranslateUtils.translate(str, Language.ENGLISH, Language.CHINESE_TRADITIONAL);
            }
        } else if("es".equalsIgnoreCase(lng)) { //西班牙文
            target = TranslateUtils.translate(str, Language.ENGLISH, Language.ESPERANTO);
        } else if("fr".equalsIgnoreCase(lng)) { //法文
            target = TranslateUtils.translate(str, Language.ENGLISH, Language.FRENCH);
        } else if("ja".equalsIgnoreCase(lng)) { //日文
            target = TranslateUtils.translate(str, Language.ENGLISH, Language.JAPANESE);
        } else if("pt".equalsIgnoreCase(lng)) { //葡萄牙
            target = TranslateUtils.translate(str, Language.ENGLISH, Language.PORTUGUESE);
        } else if("ko".equalsIgnoreCase(lng)) { //韩文
            target = TranslateUtils.translate(str, Language.ENGLISH, Language.KOREAN);
        } else {
            target = str;
        }
        return target;
    }

	public ArrayList<CheatObject> getCheatList() {
		return mCheatList;
	}

	public void setCheatList(ArrayList<CheatObject> mCheatList) {
		this.mCheatList = mCheatList;
	}
	
	/**
	 * clean cheat list
	 */
	public void clear() {
		if(null != mCheatList) {
			mCheatList.clear();
		}
	}

    public void setContext(GamePlayActivity mActivity) {
        this.mGamePlayActivity = mActivity;
    }


}
