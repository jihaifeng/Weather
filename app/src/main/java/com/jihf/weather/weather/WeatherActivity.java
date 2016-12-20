package com.jihf.weather.weather;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.jihf.weather.R;
import com.jihf.weather.area.AreaPickActivity;
import com.jihf.weather.base.BaseActivity;
import com.jihf.weather.config.Config;
import com.jihf.weather.config.PermissionConfig;
import com.jihf.weather.customview.RecyclerView.DividerGridItemDecoration;
import com.jihf.weather.customview.RecyclerView.GridLayoutManagerPlus;
import com.jihf.weather.customview.ScrollView.MyScrollView;
import com.jihf.weather.http.HttpLinstener;
import com.jihf.weather.http.HttpManager;
import com.jihf.weather.utils.AppUtils;
import com.jihf.weather.utils.CityUtils;
import com.jihf.weather.utils.ScreenUtil;
import com.jihf.weather.utils.TimeUtils;
import com.jihf.weather.utils.ToastUtil;
import com.jihf.weather.weather.bean.IndexBean;
import com.jihf.weather.weather.bean.ResultsBean;
import com.jihf.weather.weather.bean.WeatherBase;
import com.jihf.weather.weather.bean.WeatherDataBean;
import java.util.ArrayList;
import java.util.List;

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
    toolbar = (Toolbar) findViewById(R.id.tb_title);
    setSupportActionBar(toolbar);
    getSupportActionBar().setTitle("");
    Log.i(TAG, "onCreate: " + AppUtils.getInstance().getActivityNum());
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

  private void showError(String s) {
    pullDown = false;
    hideProgressDialog();
    if (TextUtils.isEmpty(s)) {
      return;
    }
    rlLoading.setVisibility(View.VISIBLE);
    tv_error.setVisibility(View.VISIBLE);
    ll_weather_root.setVisibility(View.GONE);
    tv_error.setText(s);
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
    Bundle bundle = WeatherActivity.this.getIntent().getExtras();
    String city = bundle.getString(Config.CITY_NAME_INTENT);
    updateSelectCity(city);
  }

  private void updateSelectCity(String city) {
    if (!TextUtils.isEmpty(city) && city.contains("市")) {
      city = city.substring(0, city.length() - 1);
    }
    cityName = city;
    setSharedPreferences(Config.CURRENT_CITY_NAME, cityName);
    cityList = CityUtils.getCityList();
    if (null == cityList) {
      cityList = new ArrayList<>();
    }
    if (!cityList.contains(cityName)) {
      cityList.add(cityName);
    }
    if (ContextCompat.checkSelfPermission(WeatherActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
      ActivityCompat.requestPermissions(WeatherActivity.this, new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, PermissionConfig.WRITE_SETTINGS);
    } else {
      CityUtils.insertCityList(cityList);
    }
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
        JumpTo(WeatherActivity.this, AddWeatherCityActivity.class);
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
        toolbar.setBackgroundColor(Color.argb((int) (offset * 140), 0, 0, 0));
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
    Log.i(TAG, "getWeather: " + AppUtils.getInstance().getActivityNum());
    if (!pullDown) {
      showProgressDialog();
      pullDown = false;
    }
    HttpManager.getInstance(WeatherActivity.this).getWeatherData(CityUtils.getCityStr(), new HttpLinstener() {
      @Override public void onSuccess(String response) {
        sf_weather_root.setRefreshing(false);
        if (TextUtils.isEmpty(response)) {
          showError("数据返回出错，请稍后重试");
          ToastUtil.showShort(WeatherActivity.this, "数据返回出错，请稍后重试");
          return;
        }
        WeatherBase weatherBase = new Gson().fromJson(response, WeatherBase.class);
        if (null == weatherBase) {
          showError("数据返回出错，请稍后重试");
          ToastUtil.showShort(WeatherActivity.this, "数据解析出错，请稍后重试");
          return;
        }
        updateData(weatherBase.results);
      }

      @Override public void onFailure(String msg, Throwable e) {
        showError(msg);
        sf_weather_root.setRefreshing(false);
        ToastUtil.showShort(WeatherActivity.this, "msg = " + msg);
      }
    });
  }

  private void updateData(List<ResultsBean> results) {
    pullDown = false;
    if (null == results || results.size() == 0) {
      showError("数据出错，请稍后重试");
      ToastUtil.showShort(WeatherActivity.this, "数据出错...");
      return;
    }
    hideProgressDialog();
    updateViewVisibility(true);
    for (ResultsBean resultsBean : results) {
      if (resultsBean.currentCity.equalsIgnoreCase(cityName)) {
        List<WeatherDataBean> weatherDataBean = resultsBean.weather_data;
        weatherRyAdapter.updateData(weatherDataBean);
      }
    }
    updateHeaderData(results);
    updateFootData(results);
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
      viewLine.setVisibility(View.GONE);
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
            curTemperature = date.substring(index + 1, date.length() - 1);
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
        tv_current_city.setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View view) {
            Intent intent = new Intent();
            intent.setClass(WeatherActivity.this, AreaPickActivity.class);
            startActivityForResult(intent, SELECT_CITY_CODE);
          }
        });
        updateBg(desc, iv_weather_bg);
      }
    }
  }

  private void updateBg(String desc, ImageView view) {
    int drawableId = 0;
    int hour = TimeUtils.getCurHour();
    Log.e("updateBg", "updateBg: " + hour);
    if (hour >= 18 || hour < 6) {
      //夜
      if (desc.contains("晴")) {
        drawableId = R.drawable.weather_night_sun;
      } else if (desc.contains("雨")) {
        drawableId = R.drawable.weather_night_rain;
      } else if (desc.contains("雪")) {
        drawableId = R.drawable.weather_night_snow;
      }
    } else {
      //昼
      if (desc.contains("晴")) {
        drawableId = R.drawable.weather_day_sun;
      } else if (desc.contains("雨")) {
        drawableId = R.drawable.weather_day_rain;
      } else if (desc.contains("雪")) {
        drawableId = R.drawable.weather_day_snow;
      }
    }
    if (drawableId == 0) {
      Glide.with(WeatherActivity.this).load(TextUtils.isEmpty(getSharedPreferences(Config.WELCOM_PIC)) ? "" : getSharedPreferences(Config.WELCOM_PIC)).diskCacheStrategy(DiskCacheStrategy.ALL).into(view);
    } else {
      Glide.with(WeatherActivity.this).load(drawableId).diskCacheStrategy(DiskCacheStrategy.ALL).into(view);
    }
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode != Activity.RESULT_OK || data == null) {
      return;
    }
    switch (requestCode) {
      case Config.SELECT_CITY_CODE:
        String city = data.getStringExtra(Config.CITY_NAME_INTENT);
        updateSelectCity(city);
        getWeather();
        break;
    }
  }

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if (requestCode == PermissionConfig.WRITE_SETTINGS) {
      if (grantResults.length > 0) {
        CityUtils.insertCityList(cityList);
      } else {
        ActivityCompat.shouldShowRequestPermissionRationale(WeatherActivity.this,Manifest.permission.WRITE_SETTINGS);
        ToastUtil.showShort(WeatherActivity.this, "缺少必要权限权限。。。");
      }
    }
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


