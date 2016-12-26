package com.jihf.weather.area;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import com.jihf.weather.R;
import com.jihf.weather.base.BaseActivity;
import com.jihf.weather.config.PermissionConfig;
import com.jihf.weather.utils.CustomStatusBar;
import com.jihf.weather.utils.ToastUtil;
import com.raiyi.wsh_lib_bdlocation.mgr.LocationMgr;

/**
 * Func：
 * User：jihf
 * Data：2016-12-16
 * Time: 13:19
 * Mail：jihaifeng@raiyi.com
 */
public class AreaPickActivity extends BaseActivity {
  private String locationAddr = "定位失败";

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.tb_title);
    CustomStatusBar.setTranslucent(this, Color.TRANSPARENT, false, toolbar);
  }

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if (requestCode == PermissionConfig.PERMISSION_LOCATION) {
      if (grantResults.length > 0) {
        ChooseAreaFragment.startLocation();
      } else {
        ToastUtil.showShort(this, "缺少定位权限。。。");
      }
    }
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    LocationMgr.getInstance(this, "all").clear();
  }
}
