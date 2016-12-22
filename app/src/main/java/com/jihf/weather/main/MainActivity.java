package com.jihf.weather.main;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import com.jihf.weather.R;
import com.jihf.weather.base.BaseActivity;
import com.jihf.weather.config.Config;
import com.jihf.weather.utils.CityUtils;
import com.jihf.weather.utils.CustomStatusBar;
import com.jihf.weather.weather.WeatherActivity;

/**
 * Func：
 * User：jihf
 * Data：2016-12-16
 * Time: 09:49
 * Mail：jihaifeng@raiyi.com
 */
public class MainActivity extends BaseActivity {

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.tb_title);
    CustomStatusBar.setTranslucent(this, Color.TRANSPARENT, false, toolbar);
    if (!TextUtils.isEmpty(getSharedPreferences(Config.CURRENT_CITY_NAME))) {
      Bundle bundle = new Bundle();
      bundle.putString(Config.CITY_NAME_INTENT, getSharedPreferences(Config.CURRENT_CITY_NAME));
      JumpToWithBundle(this, WeatherActivity.class, bundle);
      finish();
    }
    if (null != CityUtils.getCityList() && CityUtils.getCityList().size() != 0) {
      Bundle bundle = new Bundle();
      bundle.putString(Config.CITY_NAME_INTENT, CityUtils.getCityList().get(0));
      JumpToWithBundle(this, WeatherActivity.class, bundle);
      finish();
    }
  }
}
