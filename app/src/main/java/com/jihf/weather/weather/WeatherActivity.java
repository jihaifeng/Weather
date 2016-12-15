package com.jihf.weather.weather;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.google.gson.Gson;
import com.jihf.weather.R;
import com.jihf.weather.base.BaseActivity;
import com.jihf.weather.city.CityPickActivity;
import com.jihf.weather.config.Config;
import com.jihf.weather.http.HttpLinstener;
import com.jihf.weather.http.HttpManager;
import com.jihf.weather.utils.ToastUtil;
import com.jihf.weather.weather.bean.IndexBean;
import com.jihf.weather.weather.bean.ResultsBean;
import com.jihf.weather.weather.bean.WeatherBase;
import com.jihf.weather.weather.bean.WeatherDataBean;
import com.ruiyi.okhttp.OkParams;
import java.util.List;

/**
 * Func：
 * User：jihf
 * Data：2016-12-14
 * Time: 14:45
 * Mail：jihaifeng@raiyi.com
 */
public class WeatherActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {
  private OkParams okParams = new OkParams();
  private RecyclerView ry_weather_desc;
  private SwipeRefreshLayout sf_weather_root;
  private WeatherRyAdapter weatherRyAdapter;
  private String cityName = "北京";

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_weather);
    //getSupportActionBar().setTitle("天气");
    initView();
    getSelectCity();
    getWeather();
  }

  private void getSelectCity() {
    Bundle bundle = getIntent().getExtras();
    cityName = bundle.getString(Config.CITY_NAME);
    if (!TextUtils.isEmpty(cityName) && cityName.contains("市")) {
      cityName = cityName.substring(0, cityName.length() - 1);
    }
   setSharedPreferences(Config.SELECT_CITY, cityName);
    Log.e("ss", "getSelectCity: " + getSharedPreferences(Config.SELECT_CITY));
  }

  @Override public void onRefresh() {
    sf_weather_root.setRefreshing(true);
    //getWeather();
  }

  private void initView() {
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

      @Override public void onSuccess(List<ResultsBean> resultsBean) {
        //sf_weather_root.setRefreshing(false);
        //updateData(resultsBean);
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
    addHeadView(results, ry_weather_desc);
    addFootView(results, ry_weather_desc);
  }

  private void addFootView(List<ResultsBean> results, RecyclerView recyclerView) {
    if (null == results || results.size() == 0) {
      return;
    }
    TipsRyAdapter ryTipsAdapter;
    RecyclerView ryTips;
    View foot = LayoutInflater.from(WeatherActivity.this).inflate(R.layout.weather_foot, recyclerView, false);
    ryTipsAdapter = new TipsRyAdapter(WeatherActivity.this);
    ryTips = (RecyclerView) foot.findViewById(R.id.ry_weather_tips);
    ryTips.setLayoutManager(new LinearLayoutManager(WeatherActivity.this));
    ryTips.setHasFixedSize(true);
    ryTips.setAdapter(ryTipsAdapter);
    for (ResultsBean resultsBean : results) {
      if (resultsBean.currentCity.equalsIgnoreCase(cityName)) {
        List<IndexBean> indexBeen = resultsBean.index;
        if (null != indexBeen && indexBeen.size() != 0) {
          weatherRyAdapter.setFooterView(foot);
          ryTipsAdapter.updateData(indexBeen);
        }
      }
    }
  }

  private void addHeadView(List<ResultsBean> results, RecyclerView recyclerView) {
    if (null == results || results.size() == 0) {
      return;
    }
    TextView tv_weather_desc;
    TextView tv_current_city;
    TextView tv_current_temperature;
    View header = LayoutInflater.from(WeatherActivity.this).inflate(R.layout.weather_header, recyclerView, false);
    tv_weather_desc = (TextView) header.findViewById(R.id.tv_weather_desc);
    tv_current_city = (TextView) header.findViewById(R.id.tv_current_city);
    tv_current_temperature = (TextView) header.findViewById(R.id.tv_current_temperature);
    for (ResultsBean resultsBean : results) {
      if (resultsBean.currentCity.equalsIgnoreCase(cityName)) {
        tv_current_city.setText(!TextUtils.isEmpty(resultsBean.currentCity) ? resultsBean.currentCity : "北京");
        if (null != results && results.size() != 0 && null != resultsBean.weather_data && resultsBean.weather_data.size() != 0) {
          String date = resultsBean.weather_data.get(0).date;
          if (!TextUtils.isEmpty(date) && date.length() > 2) {
            date = date.substring(date.length() - 3, date.length() - 1);
            tv_current_temperature.setText(date);
          } else {
            tv_current_temperature.setText(resultsBean.pm25);
          }
          String desc = resultsBean.weather_data.get(0).weather;
          tv_weather_desc.setText(TextUtils.isEmpty(desc) ? "晴朗" : desc);
        } else {
          String desc = resultsBean.weather_data.get(0).weather;
          tv_weather_desc.setText("晴朗");
          tv_current_temperature.setText(resultsBean.pm25);
        }

        tv_current_city.setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View view) {
            JumpTo(WeatherActivity.this, CityPickActivity.class);
          }
        });
      }
    }
    weatherRyAdapter.setHeaderView(header);
  }
}


