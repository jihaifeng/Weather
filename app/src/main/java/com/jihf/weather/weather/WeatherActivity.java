package com.jihf.weather.weather;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.ScrollView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jihf.weather.R;
import com.jihf.weather.WeatherApplication;
import com.jihf.weather.base.BaseActivity;
import com.jihf.weather.base.BaseDataDb;
import com.jihf.weather.city.CityManagerActivity;
import com.jihf.weather.config.Config;
import com.jihf.weather.customview.RecyclerView.DividerGridItemDecoration;
import com.jihf.weather.customview.RecyclerView.GridLayoutManagerPlus;
import com.jihf.weather.customview.ScrollView.MyScrollView;
import com.jihf.weather.http.WeatherLinstener;
import com.jihf.weather.swipbackhelper.SwipeBackHelper;
import com.jihf.weather.utils.CityUtils;
import com.jihf.weather.utils.CustomStatusBar;
import com.jihf.weather.utils.DialogUtils;
import com.jihf.weather.utils.NotificationUtils;
import com.jihf.weather.utils.ScreenUtil;
import com.jihf.weather.utils.ToastUtil;
import com.jihf.weather.utils.WeatherUtils;
import com.jihf.weather.weather.bean.IndexBean;
import com.jihf.weather.weather.bean.ResultsBean;
import com.jihf.weather.weather.bean.WeatherDataBean;
import java.util.ArrayList;
import java.util.List;

import static android.app.PendingIntent.FLAG_CANCEL_CURRENT;
import static com.jihf.weather.config.Config.SELECT_CITY_CODE;

/**
 * Func：
 * User：jihf
 * Data：2016-12-14
 * Time: 14:45
 * Mail：jihaifeng@raiyi.com
 */
public class WeatherActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

  private RecyclerView ry_weather_desc;
  private MyScrollView sl_root;
  private SwipeRefreshLayout sf_weather_root;
  private WeatherRyAdapter weatherRyAdapter;
  private String cityName = "北京";
  private List<String> cityList;
  //toolbar
  private RelativeLayout weatherBack;
  private RelativeLayout weatherMore;
  //header
  private View header;
  private TextView tv_weather_desc;
  private TextView tv_current_city;
  private TextView tv_current_temperature;
  private TextView tv_current_pm25;
  private ImageView iv_weather_bg;
  //foot
  private View foot1;
  private TextView tv_wind;
  private TextView tv_wind_desc;
  private LinearLayout ll_wind;
  private TextView tv_pm25;
  private LinearLayout ll_PM25;
  private TextView tv_temperature;
  private LinearLayout ll_temperature;
  private View viewLine;
  private View viewLine1;
  private TipsRyAdapter ryTipsAdapter;

  private RecyclerView ryTips;
  private CoordinatorLayout cl_root;
  private View foot2;
  private DividerGridItemDecoration dividerGridItemDecoration;

  private TextView tv_error;
  private LinearLayout rlLoading;
  private ImageView ivRefresh;
  private LinearLayout ll_weather_root;
  private String curTemperature = "0";
  private boolean pullDown = false;
  private Toolbar toolbar;
  int mDistanceY = 0;
  private float headerHeight;//顶部高度
  private float minHeaderHeight;//顶部最低高度，即Bar的高度

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_weather);
    SwipeBackHelper.getCurrentPage(this).setSwipeBackEnable(false);
    toolbar = (Toolbar) findViewById(R.id.tb_title);
    setSupportActionBar(toolbar);
    getSupportActionBar().setTitle("");
    CustomStatusBar.setTranslucent(this, Color.TRANSPARENT, false, toolbar);
    setItemDecoration();
    initView();
    updateViewVisibility(false);
    getSelectCity();
    getWeather();
  }

  private void updateViewVisibility(boolean isShow) {
    ll_weather_root.setVisibility(isShow ? View.VISIBLE : View.GONE);
    rlLoading.setVisibility(View.GONE);
  }

  private void showErrorView(String s) {
    pullDown = false;
    hideProgressDialog();
    if (TextUtils.isEmpty(s)) {
      return;
    }
    rlLoading.setVisibility(View.VISIBLE);
    tv_error.setVisibility(View.VISIBLE);
    ll_weather_root.setVisibility(View.GONE);
    tv_error.setText(s);
    toolbar.setBackgroundColor(ContextCompat.getColor(WeatherActivity.this, R.color.half_transparent));
    ivRefresh.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        getWeather();
      }
    });
  }

  private void setItemDecoration() {
    dividerGridItemDecoration = new DividerGridItemDecoration(WeatherActivity.this, ContextCompat.getColor(WeatherActivity.this, R.color.gray));
    dividerGridItemDecoration.setDividerHeight(ScreenUtil.dip2px((float) 0.8));
    dividerGridItemDecoration.setDividerWidth(ScreenUtil.dip2px((float) 0.8));
  }

  private void getSelectCity() {
    Intent intent = WeatherActivity.this.getIntent();
    String city = cityName;
    if (null != intent) {
      Bundle bundle = intent.getExtras();
      if (null != bundle) {
        city = bundle.getString(Config.CITY_NAME_INTENT);
      }
    }
    updateSelectCity(city);
  }

  private void updateSelectCity(String city) {
    if (!TextUtils.isEmpty(city) && city.contains("市")) {
      city = city.substring(0, city.length() - 1);
    }
    cityName = city;
    BaseDataDb.insert(Config.CURRENT_CITY_NAME, cityName);
    setSharedPreferences(Config.CURRENT_CITY_NAME, cityName);
    cityList = CityUtils.getCityList();
    if (null == cityList) {
      cityList = new ArrayList<>();
    }
    if (!cityList.contains(cityName)) {
      cityList.add(cityName);
    }
    CityUtils.insertCityList(cityList);
  }

  @Override public void onRefresh() {
    sf_weather_root.setRefreshing(true);
    getWeather();
  }

  private void initView() {
    //toolbar
    weatherBack = (RelativeLayout) findViewById(R.id.rl_back);
    weatherBack.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        WeatherActivity.this.finish();
      }
    });
    weatherBack.setVisibility(canGoBack() ? View.VISIBLE : View.GONE);
    weatherMore = (RelativeLayout) findViewById(R.id.rl_more);
    weatherMore.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        startActivityForResult(new Intent(WeatherActivity.this, CityManagerActivity.class), SELECT_CITY_CODE);
      }
    });
    //body
    weatherRyAdapter = new WeatherRyAdapter(WeatherActivity.this);
    ry_weather_desc = (RecyclerView) findViewById(R.id.ry_weather_desc);
    sl_root = (MyScrollView) findViewById(R.id.sl_root);
    ry_weather_desc.setLayoutManager(new LinearLayoutManager(WeatherActivity.this));
    ry_weather_desc.setHasFixedSize(true);
    ry_weather_desc.setAdapter(weatherRyAdapter);
    sf_weather_root = (SwipeRefreshLayout) findViewById(R.id.sf_weather_root);
    sf_weather_root.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.default_indexBar_textColor, R.color.default_indexBar_selectedTextColor);
    sf_weather_root.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override public void onRefresh() {
        pullDown = true;
        getWeather();
      }
    });
    sl_root.setOnScrollListener(new MyScrollView.OnScrollChangedListener() {
      @Override public void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt) {
        //Y轴偏移量
        float scrollY = who.getScrollY();
        minHeaderHeight = toolbar.getHeight();
        headerHeight = header.getHeight();
        //变化率
        float headerBarOffsetY = headerHeight - minHeaderHeight;//Toolbar与header高度的差值
        float offset = 1 - Math.max((headerBarOffsetY - scrollY) / headerBarOffsetY, 0f);
        toolbar.setBackgroundColor(Color.argb((int) (offset * 255), 0, 0, 0));
      }
    });
    //header
    header = findViewById(R.id.weather_header);
    tv_weather_desc = (TextView) findViewById(R.id.tv_weather_desc);
    tv_current_city = (TextView) findViewById(R.id.tv_current_city);
    tv_current_pm25 = (TextView) findViewById(R.id.tv_current_pm25);
    iv_weather_bg = (ImageView) findViewById(R.id.iv_weather_bg);
    tv_current_temperature = (TextView) findViewById(R.id.tv_current_temperature);

    //foot1
    foot1 = findViewById(R.id.weather_foot1);
    tv_pm25 = (TextView) findViewById(R.id.tv_pm25);
    ll_PM25 = (LinearLayout) findViewById(R.id.ll_pm25);
    viewLine = findViewById(R.id.view_Line);
    tv_wind = (TextView) findViewById(R.id.tv_wind);
    tv_wind_desc = (TextView) findViewById(R.id.tv_wind_desc);
    ll_wind = (LinearLayout) findViewById(R.id.ll_wind);
    viewLine1 = findViewById(R.id.view_Line1);
    tv_temperature = (TextView) findViewById(R.id.tv_temperature);
    ll_temperature = (LinearLayout) findViewById(R.id.ll_temperature);

    //foot2
    foot2 = findViewById(R.id.weather_foot2);
    cl_root = (CoordinatorLayout) findViewById(R.id.cl_root);
    ryTipsAdapter = new TipsRyAdapter(WeatherActivity.this, cl_root);
    ryTips = (RecyclerView) findViewById(R.id.ry_weather_tips);
    ryTips.setLayoutManager(new GridLayoutManagerPlus(WeatherActivity.this, 3));
    ryTips.addItemDecoration(dividerGridItemDecoration);
    ryTips.setHasFixedSize(true);
    ryTips.setAdapter(ryTipsAdapter);
    //pb
    rlLoading = (LinearLayout) findViewById(R.id.rl_loading);
    ll_weather_root = (LinearLayout) findViewById(R.id.ll_weather_root);
    ivRefresh = (ImageView) findViewById(R.id.iv_refresh);
    tv_error = (TextView) findViewById(R.id.tv_error);
  }

  private void getWeather() {
    WeatherUtils.getWeather(WeatherActivity.this, pullDown, new WeatherLinstener() {
      @Override public void showError(String msg) {
        sf_weather_root.setRefreshing(false);
        showErrorView(msg);
        ToastUtil.showShort(WeatherActivity.this, TextUtils.isEmpty(msg) ? "数据异常" : msg);
      }

      @Override public void showData(List<ResultsBean> results) {
        sf_weather_root.setRefreshing(false);
        updateData(results);
      }
    });
  }

  private void updateData(List<ResultsBean> results) {
    pullDown = false;
    if (null == results || results.size() == 0) {
      showErrorView("数据出错，请稍后重试");
      ToastUtil.showShort(WeatherActivity.this, "数据出错...");
      return;
    }
    updateViewVisibility(true);
    for (ResultsBean resultsBean : results) {
      if (resultsBean.currentCity.equalsIgnoreCase(cityName)) {
        List<WeatherDataBean> weatherDataBean = resultsBean.weather_data;
        weatherRyAdapter.updateData(weatherDataBean);
      }
    }
    updateHeaderData(results);
    updateFootData(results);
    showWeatherNotification(results);
  }

  private void showWeatherNotification(List<ResultsBean> results) {
    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
    //设置通知小ICON,一定要设置，不能少
    builder.setSmallIcon(R.mipmap.weather_launcher);
    Intent intent = new Intent(this, WeatherActivity.class);
    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, FLAG_CANCEL_CURRENT);
    builder.setContentIntent(pendingIntent);

    RemoteViews remoteViews;

    if (NotificationUtils.isDarkNotification(WeatherActivity.this)) {
      //深色通知栏背景，设置浅色自定义通知栏布局
      remoteViews = new RemoteViews(getPackageName(), R.layout.white_notification_view);
    } else {
      remoteViews = new RemoteViews(getPackageName(), R.layout.dark_notification_view);
    }
    for (ResultsBean resultsBean : results) {
      if (resultsBean.currentCity.equalsIgnoreCase(cityName)) {

        if (results.size() != 0 && null != resultsBean.weather_data && resultsBean.weather_data.size() != 0) {
          String date = TextUtils.isEmpty(resultsBean.weather_data.get(0).date) ? "" : resultsBean.weather_data.get(0).date;
          if (!TextUtils.isEmpty(date) && date.contains("实时")) {
            int index = date.lastIndexOf("(");
            curTemperature = date.substring(index + 1, date.length() - 1).replace("℃", "°");
            remoteViews.setTextViewText(R.id.tv_city, resultsBean.currentCity + " ( " + curTemperature.trim() + ")");
          } else {
            remoteViews.setTextViewText(R.id.tv_city, resultsBean.currentCity);
          }
          String desc = resultsBean.weather_data.get(0).weather;
          remoteViews.setTextViewText(R.id.weather_desc, "今日天气：" + desc);
          remoteViews.setTextViewText(R.id.tv_temperature, resultsBean.weather_data.get(0).temperature.replace("℃", "°"));
          remoteViews.setTextViewText(R.id.tv_pm25, TextUtils.isEmpty(resultsBean.pm25) ? "" : "PM2.5: " + resultsBean.pm25);
          remoteViews.setImageViewResource(R.id.iv_image, WeatherUtils.getWeatherIcon(desc));
        }
      }
    }
    builder.setContent(remoteViews);
    //点击通知栏的notification后，是否自动被取消消失
    builder.setAutoCancel(false);
    //是否禁止滑动删除
    builder.setOngoing(true);
    //通知首次出现在通知栏，带上升动画效果的
    builder.setTicker("通知栏也能看天气啦");
    builder.setPriority(NotificationCompat.PRIORITY_MAX);
    notificationManager.notify(Config.WEATHER_NOTI_ID, builder.build());

    ////设置通知栏点击意图
    // builder.setContentIntent(new PendingIntent());
    // //  .setNumber(number) //设置通知集合的数量
    // builder.setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
    // builder.setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
    // builder.setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
    // builder.setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
    // builder.setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
    // //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
    // builder.setSmallIcon(R.drawable.ic_launcher);//设置通知小ICON
  }

  private void updateFootData(List<ResultsBean> results) {
    for (ResultsBean resultsBean : results) {
      if (resultsBean.currentCity.equalsIgnoreCase(cityName)) {
        updateFoot1Data(resultsBean);
        List<IndexBean> indexBeen = resultsBean.index;
        if (null != indexBeen && indexBeen.size() != 0) {
          ryTipsAdapter.updateData(indexBeen);
        }
      }
    }
  }

  private void updateFoot1Data(ResultsBean resultsBean) {
    //风力
    List<WeatherDataBean> weatherDataBeen = resultsBean.weather_data;
    if (null != weatherDataBeen && weatherDataBeen.size() != 0) {
      String windData = TextUtils.isEmpty(weatherDataBeen.get(0).wind) ? "" : weatherDataBeen.get(0).wind;
      if (TextUtils.isEmpty(windData)) {
        ll_wind.setVisibility(View.GONE);
        viewLine.setVisibility(View.GONE);
      } else {
        ll_wind.setVisibility(View.VISIBLE);
        viewLine.setVisibility(View.VISIBLE);
        if (windData.contains("级")) {
          int index = windData.lastIndexOf("风");
          String wind1 = windData.substring(0, index + 1);
          String wind2 = windData.substring(index + 1, windData.length());
          tv_wind.setVisibility(View.VISIBLE);
          tv_wind_desc.setVisibility(View.VISIBLE);
          tv_wind_desc.setText(wind1);
          tv_wind.setText(wind2);
        } else {
          tv_wind_desc.setVisibility(View.GONE);
          tv_wind.setVisibility(View.VISIBLE);
          tv_wind.setText(windData);
        }
      }
    }
    //温度
    if (!TextUtils.isEmpty(curTemperature)) {
      viewLine1.setVisibility(View.VISIBLE);
      ll_temperature.setVisibility(View.VISIBLE);
      tv_temperature.setText(curTemperature);
    } else {
      viewLine1.setVisibility(View.GONE);
      ll_temperature.setVisibility(View.GONE);
    }
    //PM25
    if (TextUtils.isEmpty(resultsBean.pm25)) {
      if (viewLine1.getVisibility() == View.VISIBLE) {
        viewLine1.setVisibility(View.GONE);
      }
      ll_PM25.setVisibility(View.GONE);
    } else {
      ll_PM25.setVisibility(View.VISIBLE);
      viewLine.setVisibility(View.VISIBLE);
      tv_pm25.setText(resultsBean.pm25);
    }
  }

  private void updateHeaderData(List<ResultsBean> results) {
    for (ResultsBean resultsBean : results) {
      if (resultsBean.currentCity.equalsIgnoreCase(cityName)) {
        tv_current_city.setText(!TextUtils.isEmpty(resultsBean.currentCity) ? resultsBean.currentCity : "北京");
        String desc = "";
        if (results.size() != 0 && null != resultsBean.weather_data && resultsBean.weather_data.size() != 0) {
          String date = TextUtils.isEmpty(resultsBean.weather_data.get(0).date) ? "" : resultsBean.weather_data.get(0).date;
          if (!TextUtils.isEmpty(date) && date.contains("实时")) {
            int index = 0;
            if (date.contains("实时：")) {
              index = date.lastIndexOf("：");
            } else {
              index = date.lastIndexOf("时");
            }
            curTemperature = date.substring(index + 1, date.length() - 1).replace("℃", "");
            tv_current_temperature.setText(curTemperature);
          } else {
            tv_current_temperature.setText("未获取到数据");
          }
          desc = resultsBean.weather_data.get(0).weather;
          tv_weather_desc.setText((TextUtils.isEmpty(desc) ? "晴朗" : desc));
        } else {
          tv_weather_desc.setText("未获取到数据");
        }
        if (TextUtils.isEmpty(resultsBean.pm25)) {
          tv_current_pm25.setVisibility(View.GONE);
        } else {
          tv_current_pm25.setVisibility(View.VISIBLE);
          tv_current_pm25.setText("PM2.5  " + resultsBean.pm25);
        }
        Glide.with(WeatherActivity.this).load(WeatherUtils.getWeatherBg(desc)).diskCacheStrategy(DiskCacheStrategy.ALL).into(iv_weather_bg);
        //Glide.with(WeatherActivity.this).load(R.drawable.thunderrain).diskCacheStrategy(DiskCacheStrategy.ALL).into(iv_weather_bg);
      }
    }
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode != Activity.RESULT_OK || data == null) {
      return;
    }
    switch (requestCode) {
      case SELECT_CITY_CODE:
        String city = data.getStringExtra(Config.CITY_NAME_INTENT);
        updateSelectCity(city);
        getWeather();
        break;
    }
  }

  @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
      DialogUtils.getInstance(this).showDefault("温馨提示", "真的要离开114天气么，小4会想你的哦。。。", "残忍离开", "我再看看", new DialogUtils.buttonLinstener() {
        @Override public void onNegativeClick() {

        }

        @Override public void onPositiveClick() {
          WeatherApplication.getInstance().exitApp();
        }

        @Override public void onNeutralClick() {

        }
      });
    }
    return super.onKeyDown(keyCode, event);
  }

  @Override protected void onResume() {
    super.onResume();
  }

  @Override protected void onPause() {
    super.onPause();
  }

  @Override protected void onDestroy() {
    super.onDestroy();
  }
}


