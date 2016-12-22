package com.jihf.weather.area;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import com.jihf.weather.R;
import com.jihf.weather.base.BaseActivity;
import com.jihf.weather.utils.CustomStatusBar;

/**
 * Func：
 * User：jihf
 * Data：2016-12-16
 * Time: 13:19
 * Mail：jihaifeng@raiyi.com
 */
public class AreaPickActivity extends BaseActivity {
  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.tb_title);
    CustomStatusBar.setTranslucent(this, Color.TRANSPARENT, false, toolbar);
  }
}
