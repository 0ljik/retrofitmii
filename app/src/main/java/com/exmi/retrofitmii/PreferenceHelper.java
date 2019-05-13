package com.exmi.retrofitmii;

public class PreferenceHelper {
    final public static String KEY_username = "username";
    final public static String KEY_pass = "password";
    final public static String KEY_isloggedin = "isloggedin";
    final public static String KEY_amillis = "amillis";
    final public static String KEY_token = "token";
    final public static String KEY_pin = "pin";

    public static void set_username(String value){
        MainActivity.sharedpreferences.edit().putString(KEY_username, value).commit();
    }
    public static void set_pass(String value){
        MainActivity.sharedpreferences.edit().putString(KEY_pass, value).commit();
    }
    public static void set_isloggedin(Boolean value){
        MainActivity.sharedpreferences.edit().putBoolean(KEY_isloggedin, value).commit();
    }
    public static void set_amillis(Long value){
        MainActivity.sharedpreferences.edit().putLong(KEY_amillis, value).commit();
    }
    public static void set_token(String value){
        MainActivity.sharedpreferences.edit().putString(KEY_token, value).commit();
    }
    public static void set_pin(String value){
        MainActivity.sharedpreferences.edit().putString(KEY_pin, value).commit();
    }

    public static String get_username(){
        return MainActivity.sharedpreferences.getString(KEY_username, "");
    }
    public static String get_pass(){
        return MainActivity.sharedpreferences.getString(KEY_pass, "");
    }
    public static Boolean get_isloggedin(){
        return MainActivity.sharedpreferences.getBoolean(KEY_isloggedin, false);
    }
    public static Long get_amillis(){
        return MainActivity.sharedpreferences.getLong(KEY_amillis, 100);
    }
    public static String get_token(){
        return MainActivity.sharedpreferences.getString(KEY_token, "");
    }
    public static String get_pin(){
        return MainActivity.sharedpreferences.getString(KEY_pin, "");
    }

}
