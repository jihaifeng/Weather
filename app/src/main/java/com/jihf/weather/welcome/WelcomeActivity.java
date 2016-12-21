package com.jihf.weather.welcome;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jihf.weather.R;
import com.jihf.weather.base.BaseActivity;
import com.jihf.weather.config.Config;
import com.jihf.weather.http.HttpLinstener;
import com.jihf.weather.http.HttpManager;
import com.jihf.weather.main.MainActivity;
import com.jihf.weather.utils.CustomStatusBar;

//import com.jihf.weather.WeatherApplication;

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
    CustomStatusBar.setTranslucentForPic(this, Color.TRANSPARENT,false,false);
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
        updateWelComePic("");
      }
    });
  }

  private void updateWelComePic(String response) {
    if (TextUtils.isEmpty(response)) {
      Glide.with(WelcomeActivity.this).load(R.drawable.timg).into(welcomePic);
    } else {
      Glide.with(WelcomeActivity.this).load(response).placeholder(R.drawable.timg).diskCacheStrategy(DiskCacheStrategy.ALL).into(welcomePic);
      setSharedPreferences(Config.WELCOM_PIC, response);
    }
    new Handler().postDelayed(new Runnable() {
      @Override public void run() {
        JumpTo(WelcomeActivity.this, MainActivity.class);
        WelcomeActivity.this.finish();
      }
    }, 1 * 1000);
  }

  private void initView() {
    welcomePic = (ImageView) findViewById(R.id.iv_welcome);
  }
}
