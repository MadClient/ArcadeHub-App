/**
 * ArcadeHub
 * @文件名称 TranslateUtils.java
 */
package com.yunluo.android.arcadehub.utils;

import java.util.List;
import java.util.Locale;

import com.google.api.translate.Language;
import com.google.api.translate.Translate;
import com.yunluo.android.arcadehub.async.RomInfo;

/**
 * @classname TranslateUtils
 * @author siyadong
 */
public class TranslateUtils {

    public static void changeLanguage(List<RomInfo> mRomList, String localLng) {
        if(null == mRomList) {
            return;
        }
        if("zh".equalsIgnoreCase(localLng)) {
            if(true == Utils.isCN()) {
                for(RomInfo info:mRomList) {
                    info.setDesc(info.getCname());
                }
            } else {
                change(mRomList, Language.CHINESE_TRADITIONAL);
            }
        } else if("es".equalsIgnoreCase(localLng)) {
            change(mRomList, Language.ESPERANTO);
        } else if("fr".equalsIgnoreCase(localLng)) {
            change(mRomList, Language.FRENCH);
        } else if("ja".equalsIgnoreCase(localLng)) {
            change(mRomList, Language.JAPANESE);
        } else if("pt".equalsIgnoreCase(localLng)) {
            change(mRomList, Language.PORTUGUESE);
        } else if("ko".equalsIgnoreCase(localLng)) {
            change(mRomList, Language.KOREAN);
        } else {
            for(RomInfo info:mRomList) {
                info.setDesc(info.getEname());
            }
        }
    }
    
    private static void change(List<RomInfo> mRomList, Language to) {
        if(null == mRomList) {
            return;
        }
        for(RomInfo info:mRomList) {
            String desc = translate(info.getCname(), Language.CHINESE_SIMPLIFIED, to);
            info.setDesc(desc);
        }
    }
    
    public static void addTranslateLng(RomInfo mRomInfo, String cname, String ename) {
        if(null == mRomInfo) {
            return;
        }
        String localLng = Locale.getDefault().getLanguage().substring(0, 2);
        
        if("zh".equalsIgnoreCase(localLng)) {
            if(true == Utils.isCN()) {
                if(null != mRomInfo) {
                    mRomInfo.setDesc(cname);
                }
            } else {
                addTranslate(mRomInfo, cname, Language.CHINESE_TRADITIONAL);
            }
        } else if("es".equalsIgnoreCase(localLng)) {
            addTranslate(mRomInfo, cname, Language.ESPERANTO);
        } else if("fr".equalsIgnoreCase(localLng)) { 
            addTranslate(mRomInfo, cname, Language.FRENCH);
        } else if("ja".equalsIgnoreCase(localLng)) { 
            addTranslate(mRomInfo, cname, Language.JAPANESE);
        } else if("pt".equalsIgnoreCase(localLng)) { 
            addTranslate(mRomInfo, cname, Language.PORTUGUESE);
        } else if("ko".equalsIgnoreCase(localLng)) {
            addTranslate(mRomInfo, cname, Language.KOREAN);
        } else {
            if(null != mRomInfo) {
                mRomInfo.setDesc(ename);
            }
        }
    }
    
    private static void addTranslate(RomInfo mRomInfo, String cname, Language to) {
        if(null == mRomInfo && null == cname && null == to) {
            return;
        }
       String desc = translate(cname, Language.CHINESE_SIMPLIFIED, to);
       mRomInfo.setDesc(desc);
    }
    
    public static String translate(String target, Language from, Language to) {
        String translatedText = null;
        try {
            translatedText = Translate.execute(target, from, to);
        } catch (Exception e) {
        }
        return translatedText;
    }
}
