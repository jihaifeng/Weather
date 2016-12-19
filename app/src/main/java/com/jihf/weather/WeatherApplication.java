//package com.jihf.weather;
//
//import android.content.SharedPreferences;
//import android.preference.PreferenceManager;
//import com.jihf.weather.crash.CrashHandler;
//import org.litepal.LitePalApplication;
//
///**
// * Func：
// * User：jihf
// * Data：2016-12-15
// * Time: 15:38
// * Mail：jihaifeng@raiyi.com
// */
//public class WeatherApplication extends LitePalApplication {
//  private static WeatherApplication instance;
//  private SharedPreferences sharedPreferences;
//  public static final String APP_SP = "APP_SP";
//
//  @Override public void onCreate() {
//    super.onCreate();
//
//    //开启全局异常捕获
//    CrashHandler.getInstance().init(WeatherApplication.getInstance(), CrashHandler.DEFAULT);
//  }
//
//  public static WeatherApplication getInstance() {
//    if (null == instance) {
//      synchronized (WeatherApplication.class) {
//        if (null == instance) {
//          instance = new WeatherApplication();
//        }
//      }
//    }
//    return instance;
//  }
//
//  public void setSharedPreferences(String key, String val) {
//    if (null == sharedPreferences) {
//      sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//    }
//    sharedPreferences.edit().putString(key, val);
//  }
//
//  public String getSharedPreferences(String key) {
//    if (null == sharedPreferences) {
//      sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//    }
//    return sharedPreferences.getString(key, null);
//  }
//}
