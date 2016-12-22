package com.jihf.weather.http;

import com.jihf.weather.weather.bean.ResultsBean;
import java.util.List;

/**
 * Func：
 * User：jihf
 * Data：2016-12-21
 * Time: 14:35
 * Mail：jihaifeng@raiyi.com
 */
public abstract class WeatherLinstener {
  public abstract void showError(String msg);

  public abstract void showData(List<ResultsBean> results);
}
