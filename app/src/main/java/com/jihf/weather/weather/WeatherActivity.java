package com.jihf.weather.weather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.jihf.weather.R;
import com.jihf.weather.area.AreaPickActivity;
import com.jihf.weather.base.BaseActivity;
import com.jihf.weather.config.Config;
import com.jihf.weather.customview.RecyclerView.DividerGridItemDecoration;
import com.jihf.weather.customview.RecyclerView.GridLayoutManagerPlus;
import com.jihf.weather.http.HttpLinstener;
import com.jihf.weather.http.HttpManager;
import com.jihf.weather.utils.ScreenUtil;
import com.jihf.weather.utils.TimeUtils;
import com.jihf.weather.utils.ToastUtil;
import com.jihf.weather.weather.bean.IndexBean;
import com.jihf.weather.weather.bean.ResultsBean;
import com.jihf.weather.weather.bean.WeatherBase;
import com.jihf.weather.weather.bean.WeatherDataBean;
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
  private SwipeRefreshLayout sf_weather_root;
  private WeatherRyAdapter weatherRyAdapter;
  private String cityName = "北京";
  //header
  private View header;
  private TextView tv_weather_desc;
  private TextView tv_current_city;
  private TextView tv_current_temperature;
  private TextView tv_current_pm25;
  private ImageView iv_weather_bg;

  //foot
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
  private View foot;
  private DividerGridItemDecoration dividerGridItemDecoration;

  private String curTemperature = "0";

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_weather);
    //getSupportActionBar().setTitle("天气");
    setItemDecoration();
    initView();
    getSelectCity();
    getWeather();
  }

  private void setItemDecoration() {
    dividerGridItemDecoration = new DividerGridItemDecoration(WeatherActivity.this, ContextCompat.getColor(WeatherActivity.this, R.color.gray));
    dividerGridItemDecoration.setDividerHeight(ScreenUtil.dip2px((float) 0.8));
    dividerGridItemDecoration.setDividerWidth(ScreenUtil.dip2px((float) 0.8));
  }

  private void getSelectCity() {
    Bundle bundle = WeatherActivity.this.getIntent().getExtras();
    String city = bundle.getString(Config.CITY_NAME);
    updateSelectCity(city);
    Log.e("ss", "getSelectCity: " + getSharedPreferences(Config.SELECT_CITY));
  }

  private void updateSelectCity(String city) {
    if (!TextUtils.isEmpty(city) && city.contains("市")) {
      city = city.substring(0, city.length() - 1);
    }
    cityName = city;
    setSharedPreferences(Config.SELECT_CITY, cityName);
  }

  @Override public void onRefresh() {
    sf_weather_root.setRefreshing(true);
    getWeather();
  }

  private void initView() {
    //body
    weatherRyAdapter = new WeatherRyAdapter(WeatherActivity.this);
    ry_weather_desc = (RecyclerView) findViewById(R.id.ry_weather_desc);
    ry_weather_desc.setLayoutManager(new LinearLayoutManager(WeatherActivity.this));
    ry_weather_desc.setHasFixedSize(true);
    ry_weather_desc.setAdapter(weatherRyAdapter);
    sf_weather_root = (SwipeRefreshLayout) findViewById(R.id.sf_weather_root);
    sf_weather_root.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.default_indexBar_textColor, R.color.default_indexBar_selectedTextColor);
    sf_weather_root.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override public void onRefresh() {
        getWeather();
      }
    });
    //header
    tv_weather_desc = (TextView) findViewById(R.id.tv_weather_desc);
    tv_current_city = (TextView) findViewById(R.id.tv_current_city);
    tv_current_pm25 = (TextView) findViewById(R.id.tv_current_pm25);
    iv_weather_bg = (ImageView) findViewById(R.id.iv_weather_bg);
    tv_current_temperature = (TextView) findViewById(R.id.tv_current_temperature);

    //foot1
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
    ryTipsAdapter = new TipsRyAdapter(WeatherActivity.this);
    ryTips = (RecyclerView) findViewById(R.id.ry_weather_tips);
    ryTips.setLayoutManager(new GridLayoutManagerPlus(WeatherActivity.this, 3));
    ryTips.addItemDecoration(dividerGridItemDecoration);
    ryTips.setHasFixedSize(true);
    ryTips.setAdapter(ryTipsAdapter);
  }

  private void getWeather() {
    HttpManager.getInstance(WeatherActivity.this).getWeatherData(cityName, new HttpLinstener() {
      @Override public void onSuccess(String response) {
        sf_weather_root.setRefreshing(false);
        if (TextUtils.isEmpty(response)) {
          ToastUtil.showShort(WeatherActivity.this, "数据返回出错，请稍后重试");
          return;
        }
        WeatherBase weatherBase = new Gson().fromJson(response, WeatherBase.class);
        if (null == weatherBase) {
          ToastUtil.showShort(WeatherActivity.this, "数据解析出错，请稍后重试");
          return;
        }
        updateData(weatherBase.results);
      }

      @Override public void onFailure(String msg, Throwable e) {
        ToastUtil.showShort(WeatherActivity.this, "msg = " + msg);
      }
    });
  }

  private void updateData(List<ResultsBean> results) {
    if (null == results || results.size() == 0) {
      ToastUtil.showShort(WeatherActivity.this, "数据出错...");
      return;
    }
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
      //String windData = TextUtils.isEmpty(weatherDataBeen.get(0).wind) ? "" : weatherDataBeen.get(0).wind;
      String windData = "东风偏北风3-5级";
      Log.e("updateFoot1Data", "updateFoot1Data: " + windData);
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
          tv_wind_desc.setText(wind1);
          tv_wind.setText(wind2);
        } else {
          tv_wind.setVisibility(View.GONE);
          tv_wind_desc.setText(windData);
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
          tv_current_pm25.setText("PM25  " + resultsBean.pm25);
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
        String city = data.getStringExtra(Config.CITY_NAME);
        updateSelectCity(city);
        getWeather();
        break;
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


