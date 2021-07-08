package com.shenzhe.answerquestion.util;

import android.widget.Toast;

/**
 * 弹吐司
 */

public class ToastUtil {
    public static void makeToast(String content){
        Toast.makeText(MyApplication.getContext(),content,Toast.LENGTH_SHORT).show();
    }
}
