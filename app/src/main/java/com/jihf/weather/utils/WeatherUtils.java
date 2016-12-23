package com.jihf.weather.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.google.gson.Gson;
import com.jihf.weather.R;
import com.jihf.weather.http.HttpLinstener;
import com.jihf.weather.http.HttpManager;
import com.jihf.weather.http.WeatherLinstener;
import com.jihf.weather.weather.bean.WeatherBase;

/**
 * Func：
 * User：jihf
 * Data：2016-12-21
 * Time: 13:57
 * Mail：jihaifeng@raiyi.com
 */
public abstract class WeatherUtils {
  public static void getWeather(final Context context, boolean pullDown, final WeatherLinstener weatherLinstener) {
    if (!pullDown) {
      ProgressUtils.showProgressDialog(context);
    }
    HttpManager.getInstance(context).getWeatherData(CityUtils.getCityStr(), new HttpLinstener() {
      @Override public void onSuccess(String response) {
        ProgressUtils.hideProgressDialog();
        if (TextUtils.isEmpty(response)) {
          weatherLinstener.showError("数据返回出错，请稍后重试");
          return;
        }
        WeatherBase weatherBase = new Gson().fromJson(response, WeatherBase.class);
        if (null == weatherBase) {
          weatherLinstener.showError("数据返回出错，请稍后重试");
          return;
        }
        weatherLinstener.showData(weatherBase.results);
      }

      @Override public void onFailure(String msg, Throwable e) {
        ProgressUtils.hideProgressDialog();
        weatherLinstener.showError(msg);
      }
    });
  }

  public static int getWeatherIcon(String weather) {
    int drawableId = R.drawable.weather_icon_nonetwork;
    if (TextUtils.isEmpty(weather)) {
      return drawableId;
    }
    int hour = TimeUtils.getCurHour();
    Log.e("updateBg", "updateBg: " + hour);
    if (6 < hour && hour < 18) {
      //昼
      if (weather.contains("晴") && !weather.contains("阴") && !weather.contains("多云")) {
        //晴天
        drawableId = R.drawable.weather_icon_sun;
      } else if (weather.contains("晴") && weather.contains("阴") && !weather.contains("多云")) {
        //阴转晴，晴转阴
        drawableId = R.drawable.weather_icon_cloudturnsun;
      } else if (weather.contains("晴") && !weather.contains("阴") && weather.contains("多云")) {
        //多云转晴，晴转多云
        drawableId = R.drawable.weather_icon_sunturncloud;
      } else if (!weather.contains("晴") && weather.contains("阴") && weather.contains("多云")) {
        //多云转阴，阴转多云
        drawableId = R.drawable.weather_icon_sunturncloud;
      }
    } else {
      //夜
      if (weather.contains("晴") && !weather.contains("阴") && !weather.contains("多云")) {
        //晴天
        drawableId = R.drawable.weather_icon_moon;
      } else if (weather.contains("晴") && weather.contains("阴") && !weather.contains("多云")) {
        //阴转晴，晴转阴
        drawableId = R.drawable.weather_icon_cloudturnmoon;
      } else if (weather.contains("晴") && !weather.contains("阴") && weather.contains("多云")) {
        //多云转晴，晴转多云
        drawableId = R.drawable.weather_icon_moonturncloud;
      } else if (!weather.contains("晴") && weather.contains("阴") && weather.contains("多云")) {
        //多云转阴，阴转多云
        drawableId = R.drawable.weather_icon_moonturncloud;
      }
    }
    if (!weather.contains("晴") && weather.contains("阴") && !weather.contains("多云")) {
      //阴天
      drawableId = R.drawable.weather_icon_cloudy;
    }
    if (!weather.contains("晴") && !weather.contains("阴") && weather.contains("多云")) {
      //多云
      drawableId = R.drawable.weather_icon_cloud;
    }
    if (weather.contains("小雨")) {
      //小雨
      drawableId = R.drawable.weather_icon_lightrain;
    }
    if (weather.contains("中雨")) {
      //中雨
      drawableId = R.drawable.weather_icon_moderaterain;
    }
    if (weather.contains("大雨")) {
      //大雨
      drawableId = R.drawable.weather_icon_heavyrain;
    }
    if (weather.contains("暴雨")) {
      //暴雨
      drawableId = R.drawable.weather_icon_heavyrains;
    }
    if (weather.contains("雷阵雨")) {
      //雷阵雨
      drawableId = R.drawable.weather_icon_thunderstorm;
    }
    if (weather.contains("小雪")) {
      //小雪
      drawableId = R.drawable.weather_icon_lightsnow;
    }
    if (weather.contains("中雪")) {
      //中雪
      drawableId = R.drawable.weather_icon_moderatesnow;
    }
    if (weather.contains("大雪")) {
      //大雪
      drawableId = R.drawable.weather_icon_heavysnow;
    }
    if (weather.contains("暴雪")) {
      //暴雪
      drawableId = R.drawable.weather_icon_blizzard;
    }
    if (weather.contains("雨夹雪")) {
      //雨夹雪
      drawableId = R.drawable.weather_icon_sleet;
    }
    if (weather.contains("冰雹")) {
      //冰雹
      drawableId = R.drawable.weather_icon_hail;
    }
    if (weather.contains("彩虹")) {
      //彩虹
      drawableId = R.drawable.weather_icon_rainbow;
    }
    if (weather.contains("沙尘暴")) {
      //沙尘暴
      drawableId = R.drawable.weather_icon_duststorms;
    }
    if (weather.contains("雾")) {
      //雾
      drawableId = R.drawable.weather_icon_fog;
    }
    if (weather.contains("霾")) {
      //霾
      drawableId = R.drawable.weather_icon_haze;
    }
    if (weather.contains("火山")) {
      //火山
      drawableId = R.drawable.weather_icon_volcano;
    }
    if (weather.contains("龙卷风")) {
      //龙卷风
      drawableId = R.drawable.weather_icon_tornado;
    }
    return drawableId;
  }

  public static int getWeatherBg(String desc) {
    int drawableId = R.drawable.timg;
    if (TextUtils.isEmpty(desc)) {
      return drawableId;
    }
    int hour = TimeUtils.getCurHour();
    Log.e("updateBg", "updateBg: " + hour);
    if (hour >= 18 || hour < 6) {
      //夜
      if (desc.contains("晴")) {
        drawableId = R.drawable.weather_night_sun;
      } else if (desc.contains("雨")) {
        drawableId = R.drawable.weather_night_rain;
      } else if (desc.contains("雪")) {
        drawableId = R.drawable.weather_night_snow;
      }
    } else {
      //昼
      if (desc.contains("晴")) {
        drawableId = R.drawable.weather_day_sun;
      } else if (desc.contains("雨")) {
        drawableId = R.drawable.weather_day_rain;
      } else if (desc.contains("雪")) {
        drawableId = R.drawable.weather_day_snow;
      }
    }

    return drawableId;
  }
}
