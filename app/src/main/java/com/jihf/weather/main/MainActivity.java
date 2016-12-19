package com.jihf.weather.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.jihf.weather.R;
import com.jihf.weather.base.BaseActivity;
import com.jihf.weather.config.Config;
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
    if (!TextUtils.isEmpty(getSharedPreferences(Config.SELECT_CITY))) {
      Bundle bundle = new Bundle();
      bundle.putString(Config.CITY_NAME, getSharedPreferences(Config.SELECT_CITY));
      Intent intent = new Intent(this, WeatherActivity.class);
      intent.putExtras(bundle);
      startActivity(intent);
      finish();
    }
  }
}
