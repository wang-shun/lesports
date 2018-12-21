package com.lesports.search.qmt.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;

public class PinyinUtils {

	private static HanyuPinyinOutputFormat defaultFormat;

	static {
		defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		defaultFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
	}

	public static String pinyin(String chineseText) {
		if (chineseText == null) {
			return "";
		}
		char[] cl_chars = chineseText.trim().toCharArray();
		if (cl_chars == null || cl_chars.length == 0) {
			return "";
		}
		String pinyin = "";
		for (int i = 0; i < cl_chars.length; i++) {
			if (String.valueOf(cl_chars[i]).matches("[\u4e00-\u9fa5]+")) {
				try {
					pinyin += PinyinHelper.toHanyuPinyinStringArray(cl_chars[i], defaultFormat)[0];
				} catch (Exception e) {
					pinyin += cl_chars[i];
				}
			} else {
				pinyin += cl_chars[i];
			}
		}
		return pinyin;
	}

	public static void main(String[] args) {
		System.out.println(pinyin("恒大"));
	}
}
