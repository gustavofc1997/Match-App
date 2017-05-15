package com.devsideas.leapchat.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.devsideas.leapchat.Application.Application;

import java.util.ArrayList;

/**
 * Created by GustavoFC97 on 18/11/16.
 */
public class SharedPreferenceHelper {

    public final static String ACTIVE = "com.active_user";
    public final static String MY_PIC = "com.active_my_pic";
    public final static String Name = "com.leapchat_name";
    public final static String Genre = "com.leapchat_genre";
    public final static String Interested = "com.leapchat_interested";
    public final static String Phone = "com.leapchat_phone";
    public final static String FILE_NAME = "leapchat_settings";
    private static SharedPreferences sSharedPreferences;
    private static SharedPreferences.Editor sEditor;

    private static void init() {
        if (sSharedPreferences == null) {
            sSharedPreferences = Application.getInstance().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        }
    }

    public static void saveString(String key, String value) {
        init();
        sEditor = sSharedPreferences.edit();
        sEditor.putString(key, value);
        sEditor.commit();
    }

    public static void saveInt(String key, int value) {
        init();
        sEditor = sSharedPreferences.edit();
        sEditor.putInt(key, value);
        sEditor.commit();
    }

    public static int loadInt(String key) {
        init();
        return sSharedPreferences.getInt(key, -1);
    }


    public static String loadString(String key) {
        init();
        return sSharedPreferences.getString(key, null);
    }

    public static void saveBoolean(String key, Boolean value) {
        init();
        sEditor = sSharedPreferences.edit();
        sEditor.putBoolean(key, value);
        sEditor.commit();

    }

    public static void ClearPreferences(ArrayList<String> key) {
        init();
        sEditor = sSharedPreferences.edit();
        for (int i = 0; i < key.size(); i++) {
            sEditor.remove(key.get(i));
            sEditor.commit();
        }

    }


    public static boolean loadBoolean(String key) {
        init();
        return sSharedPreferences.getBoolean(key, false);
    }

}
