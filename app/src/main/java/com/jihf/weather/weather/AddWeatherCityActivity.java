package com.jihf.weather.weather;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import com.jihf.weather.R;
import com.jihf.weather.base.BaseActivity;
import com.jihf.weather.utils.CityUtils;
import com.jihf.weather.utils.CustomStatusBar;
import com.jihf.weather.utils.ToastUtil;
import java.util.List;

/**
 * Func：
 * User：jihf
 * Data：2016-12-20
 * Time: 16:12
 * Mail：jihaifeng@raiyi.com
 */
public class AddWeatherCityActivity extends BaseActivity {
  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_city);
    CustomStatusBar.setTranslucent(this, Color.TRANSPARENT,false,null);
    List<String> list = CityUtils.getCityList();
    ToastUtil.showShort(this, "list = " + list.toString());
  }
}
