package com.jihf.weather.http;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.jihf.weather.config.Config;
import com.ruiyi.okhttp.OkHttpManager;
import com.ruiyi.okhttp.OkParams;
import com.ruiyi.okhttp.RequestCallback;
import com.squareup.okhttp.Request;

/**
 * Func：
 * User：jihf
 * Data：2016-12-14
 * Time: 15:37
 * Mail：jihaifeng@raiyi.com
 */
public class HttpManager {
  public static final String TAG = HttpManager.class.getSimpleName().trim();
  public static HttpManager instance;
  private static Context mContext;
  private OkHttpManager okHttpManager;

  public static HttpManager getInstance(Context context) {
    mContext = context;
    if (null == instance) {
      synchronized (HttpManager.class) {
        if (null == instance) {
          instance = new HttpManager();
        }
      }
    }
    return instance;
  }

  public void getBingPic(HttpLinstener httpLinstener) {
    getAsyn(Constant.BING_PIC_URL, null, httpLinstener);
  }

  public void getWeatherData(String cityName, HttpLinstener httpLinstener) {
    OkParams params = new OkParams();
    if (TextUtils.isEmpty(cityName)) {
      params.add("location", "北京");
    } else {
      params.add("location", cityName);
    }
    params.add("output", "json");
    params.add("ak", Config.BAIDU_WEATHER_AK);
    params.add("mcode", Config.BAIDU_WEATHER_MCODE);
    getAsyn(Constant.BAIDU_WEATHER_URL, params, httpLinstener);
  }

  private void getAsyn(String url, OkParams params, final HttpLinstener httpLinstener) {
    if (null == okHttpManager) {
      okHttpManager = OkHttpManager.getInstance();
    }
    Log.i(TAG, "getAsyn: 异步请求链接 = " + url + ((null != params) ? "?" + params.getParamsFormMap() : ""));
    okHttpManager.getAsyn(url, params, new RequestCallback<String>() {

      @Override public void onFailure(Request request, Exception e) {
        Log.i(TAG, "onFailure: 请求失败 = " + request.toString());
        if (null != httpLinstener) {
          httpLinstener.onFailure(request.toString(), e);
        }
      }

      @Override public void onSuccess(String response) {
        Log.i(TAG, "onSuccess: 请求成功 = " + response);
        if (TextUtils.isEmpty(response)) {
          httpLinstener.onFailure("数据返回出错，请稍后重试", null);
          return;
        }
        //WeatherBase weatherBase = new Gson().fromJson(response, WeatherBase.class);
        //if (null == weatherBase) {
        //  httpLinstener.onFailure("数据解析出错，请稍后重试", null);
        //  return;
        //}
        //if (weatherBase.error != 0) {
        //  httpLinstener.onFailure(weatherBase.status, null);
        //}
        //if (!TextUtils.isEmpty(weatherBase.status) && weatherBase.status.equalsIgnoreCase("success") && null != weatherBase.results) {
        //  httpLinstener.onSuccess(weatherBase.results);
        //}
        if (null != httpLinstener) {
          httpLinstener.onSuccess(response);
        }
      }
    });
  }
}
