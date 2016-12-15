package com.jihf.weather.http;

import com.jihf.weather.weather.bean.ResultsBean;
import java.util.List;

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

  void onSuccess(List<ResultsBean> resultsBean);
}
