package com.example.mayfu11;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {

    private static final String PREFS_NAME = "UserDataPrefs";

    // Kunci untuk menyimpan dan mengambil data
    private static final String KEY_WEIGHT = "weight";
    private static final String KEY_HEIGHT = "height";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_AGE = "age";
    private static final String KEY_NOTIFICATION_ENABLED = "notificationEnabled";  // Kunci untuk status Switch

    // Menyimpan data ke SharedPreferences
    public static void saveUserData(Context context, String weight, String height, int gender, String age) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_WEIGHT, weight);
        editor.putString(KEY_HEIGHT, height);
        editor.putInt(KEY_GENDER, gender);
        editor.putString(KEY_AGE, age);
        editor.apply();  // apply() untuk menyimpan secara asynchronous
    }

    // Menyimpan status Switch (notifikasi) ke SharedPreferences
    public static void saveNotificationStatus(Context context, boolean isEnabled) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_NOTIFICATION_ENABLED, isEnabled);
        editor.apply();  // apply() untuk menyimpan secara asynchronous
    }

    // Mengambil data dari SharedPreferences
    public static String getWeight(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_WEIGHT, "");  // Default empty string
    }

    public static String getHeight(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_HEIGHT, "");  // Default empty string
    }

    public static int getGender(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_GENDER, -1);  // Default -1 if not set
    }

    public static String getKeyAge(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_AGE, "");  // Default empty string
    }

    // Mengambil status Switch (notifikasi) dari SharedPreferences
    public static boolean getNotificationStatus(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_NOTIFICATION_ENABLED, false);  // Default false jika belum ada
    }
}
