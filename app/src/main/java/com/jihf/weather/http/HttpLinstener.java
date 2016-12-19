package com.jihf.weather.http;

/**
 * Func：
 * User：jihf
 * Data：2016-12-14
 * Time: 15:40
 * Mail：jihaifeng@raiyi.com
 */
public interface HttpLinstener {
  void onSuccess(String response);

  void onFailure(String msg, Throwable e);
}
