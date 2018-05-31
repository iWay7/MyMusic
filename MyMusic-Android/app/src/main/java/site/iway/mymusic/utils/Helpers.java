package site.iway.mymusic.utils;

import site.iway.javahelpers.HanziPinyinHelper;

public class Helpers {

    public static String stringToPinyin(String s) {
        StringBuilder builder = new StringBuilder();
        int length = s.length();
        for (int i = 0; i < length; i++) {
            char c = s.charAt(i);
            String pinyin = HanziPinyinHelper.getPinyin(c);
            builder.append(pinyin == null ? c : pinyin);
        }
        return builder.toString();
    }

}
