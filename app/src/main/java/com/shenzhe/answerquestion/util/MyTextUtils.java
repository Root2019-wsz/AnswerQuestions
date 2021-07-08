package com.shenzhe.answerquestion.util;

import android.text.TextUtils;

/**
 * 判断文本是否合法
 * 不能为空(null , "" ,或者全空格 ),防止发出全是空格的内容
 * 也可限定长度
 */

public class MyTextUtils {
    public static boolean isEqual(String s1,String s2){
        return s1.equals(s2);
    }
    public static boolean isLegal(String text, int minLenth, int maxLEnth) {
        //为空
        if (TextUtils.isEmpty(text) || isAllSpace(text)) {
            return false;
        } else if (text.length() >= minLenth && text.length() <= maxLEnth) {
            return true;
        }
        return false;
    }

    public static boolean isEmpty(String text) {
        return TextUtils.isEmpty(text) || isAllSpace(text);
    }
    public static boolean isNull(String text) {
        return text.equals("null")||TextUtils.isEmpty(text) || isAllSpace(text);
    }
    private static boolean isAllSpace(String text) {
        char temp[] = text.toCharArray();
        for (char aTemp : temp) {
            if (aTemp != ' ') return false;
        }
        return true;
    }
}
