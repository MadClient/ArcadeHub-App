package com.yunluo.android.arcadehub.sort;

import java.util.Comparator;

import com.yunluo.android.arcadehub.async.RomInfo;

public class ComparatorRomInfo implements Comparator<Object> {

	public int compare(Object arg0, Object arg1) {
		RomInfo rom_a = (RomInfo) arg0;
		RomInfo rom_b = (RomInfo) arg1;

		int flag = rom_a.getName().compareTo(rom_b.getName());
		if (flag == 0) {
			return rom_a.getDesc().compareTo(rom_b.getDesc());
		} else {
			return flag;
		}
	}

}
