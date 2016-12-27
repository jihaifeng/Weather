package com.jihf.weather.welcome;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jihf.weather.R;
import com.jihf.weather.base.BaseActivity;
import com.jihf.weather.config.Config;
import com.jihf.weather.http.HttpLinstener;
import com.jihf.weather.http.HttpManager;
import com.jihf.weather.main.MainActivity;
import com.jihf.weather.utils.CustomStatusBar;
import java.util.Timer;
import java.util.TimerTask;

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
  private Timer timer = new Timer();
  private TextView tv_time;
  public static int Guide_Time = 3;
  public static int time;
  private MyTimerTask mTimerTask;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_welcome);
    CustomStatusBar.setTranslucentForPic(this, Color.TRANSPARENT, false, false);
    initView();
    //getWelcomePic();
    updateWelComePic("");
  }

  class MyTimerTask extends TimerTask {
    @Override public void run() {
      time--;
      if (timer != null) {
        timer.cancel();
      }
      if (time != 0) {
        updateText(time);
        timer = new Timer();
        timer.schedule(new MyTimerTask(), 1000);
      } else {
        JumpTo(WelcomeActivity.this, MainActivity.class);
        WelcomeActivity.this.finish();
      }
    }
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    if (null != timer) {
      timer.cancel();
    }
  }

  private void updateText(int t) {
    runOnUiThread(new Runnable() {
      @Override public void run() {
        tv_time.setText(time + "s 跳过");
      }
    });
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
      Glide.with(WelcomeActivity.this).load(R.drawable.pic_welcome).into(welcomePic);
    } else {
      Glide.with(WelcomeActivity.this).load(response).placeholder(R.drawable.pic_welcome).diskCacheStrategy(DiskCacheStrategy.ALL).into(welcomePic);
      setSharedPreferences(Config.WELCOM_PIC, response);
    }
    time = Guide_Time;
    updateText(time);
    timer.schedule(new MyTimerTask(), 1000);
  }

  private void initView() {
    welcomePic = (ImageView) findViewById(R.id.iv_welcome);
    tv_time = (TextView) findViewById(R.id.tv_time);
    tv_time.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        JumpTo(WelcomeActivity.this, MainActivity.class);
        WelcomeActivity.this.finish();
      }
    });
  }
}
