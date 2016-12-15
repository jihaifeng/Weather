package com.jihf.weather.welcome;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jihf.weather.R;
import com.jihf.weather.WeatherApplication;
import com.jihf.weather.base.BaseActivity;
import com.jihf.weather.city.CityPickActivity;
import com.jihf.weather.config.Config;
import com.jihf.weather.http.HttpLinstener;
import com.jihf.weather.http.HttpManager;
import com.jihf.weather.weather.WeatherActivity;
import com.jihf.weather.weather.bean.ResultsBean;
import java.util.List;

/**
 * Func：
 * User：jihf
 * Data：2016-12-15
 * Time: 15:46
 * Mail：jihaifeng@raiyi.com
 */
public class WelcomeActivity extends BaseActivity {
  private ImageView welcomePic;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_welcome);
    initView();
    getWelcomePic();
    //updateWelComePic("");
  }

  private void getWelcomePic() {
    HttpManager.getInstance(WelcomeActivity.this).getBingPic(new HttpLinstener() {
      @Override public void onSuccess(String response) {
        updateWelComePic(response);
      }

      @Override public void onFailure(String msg, Throwable e) {

      }

      @Override public void onSuccess(List<ResultsBean> resultsBean) {

      }
    });
  }

  private void updateWelComePic(String response) {
    Glide.with(WelcomeActivity.this).load(response).diskCacheStrategy(DiskCacheStrategy.ALL).into(welcomePic);
    new Handler().postDelayed(new Runnable() {
      @Override public void run() {
        WeatherApplication weatherApplication = WeatherApplication.getInstance();
        if (!TextUtils.isEmpty(getSharedPreferences(Config.SELECT_CITY))) {
          Bundle bundle = new Bundle();
          bundle.putString(Config.CITY_NAME, getSharedPreferences(Config.SELECT_CITY));
          JumpToWithBundle(WelcomeActivity.this, WeatherActivity.class, bundle);
        } else {
          JumpTo(WelcomeActivity.this, CityPickActivity.class);
        }
        WelcomeActivity.this.finish();
      }
    }, 3 * 1000);
  }

  private void initView() {
    welcomePic = (ImageView) findViewById(R.id.iv_welcome);
  }
}
