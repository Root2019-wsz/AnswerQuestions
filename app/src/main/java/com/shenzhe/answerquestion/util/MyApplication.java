package com.shenzhe.answerquestion.util;


import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.shenzhe.answerquestion.bean.Person;

import java.net.CookieStore;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * 全局context
 */

public class MyApplication extends Application {
    private static Person sMPerson;
    private static Context mContext;
    private static final String TAG = "MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        Log.d(TAG,"onCreate:  "+mContext.toString());
    }


    public static void setUser(Person person){
        sMPerson = person;
    }


    public static Person getUser(){
        return sMPerson;
    }


    public static Context getContext(){
        return mContext;
    }

    public static Integer getId(){
        if (sMPerson != null && sMPerson.getId() != null){
            return sMPerson.getId();
        }
        return null;
    }
}
