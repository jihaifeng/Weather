package com.jihf.weather;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDexApplication;
import com.baidu.mobstat.StatService;
import com.jihf.weather.config.Config;
import com.jihf.weather.crash.CrashHandler;
import com.jihf.weather.utils.ScreenUtil;
import com.ruiyi.lib.hfb.umeng.UmengEvents;
import org.litepal.LitePalApplication;

/**
 * Func：自定义Application继承MultiDexApplication实现分包
 * User：jihf
 * Data：2016-12-15
 * Time: 15:38
 * Mail：jihaifeng@raiyi.com
 */
public class WeatherApplication extends MultiDexApplication {

  private static WeatherApplication instance;
  private SharedPreferences sharedPreferences;
  public static final String APP_SP = "APP_SP";

  @Override public void onCreate() {
    super.onCreate();
    //统计
    UmengEvents.setChannel(getApplicationContext(), Config.TONGJI_CHANNEL);
    //开启全局异常捕获
    CrashHandler.getInstance().init(WeatherApplication.getInstance(), CrashHandler.DEFAULT);
    //litePal数据库初始化
    LitePalApplication.initialize(getApplicationContext());
    //屏幕工具类初始化
    ScreenUtil.getInstance(getApplicationContext());
    //百度统计调试模式
    StatService.setDebugOn(false);
  }

  public static WeatherApplication getInstance() {
    if (null == instance) {
      synchronized (WeatherApplication.class) {
        if (null == instance) {
          instance = new WeatherApplication();
        }
      }
    }
    return instance;
  }

  public void setSharedPreferences(String key, String val) {
    if (null == sharedPreferences) {
      sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }
    sharedPreferences.edit().putString(key, val);
  }

  public String getSharedPreferences(String key) {
    if (null == sharedPreferences) {
      sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }
    return sharedPreferences.getString(key, null);
  }
}
