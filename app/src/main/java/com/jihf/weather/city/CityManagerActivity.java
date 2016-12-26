package com.jihf.weather.city;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.jihf.weather.R;
import com.jihf.weather.area.AreaPickActivity;
import com.jihf.weather.base.BaseActivity;
import com.jihf.weather.config.Config;
import com.jihf.weather.http.WeatherLinstener;
import com.jihf.weather.utils.CityUtils;
import com.jihf.weather.utils.CustomStatusBar;
import com.jihf.weather.utils.ToastUtil;
import com.jihf.weather.utils.WeatherUtils;
import com.jihf.weather.weather.WeatherRyAdapter;
import com.jihf.weather.weather.bean.ResultsBean;
import com.yanzhenjie.recyclerview.swipe.Closeable;
import com.yanzhenjie.recyclerview.swipe.OnSwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import java.util.ArrayList;
import java.util.List;

/**
 * Func：
 * User：jihf
 * Data：2016-12-20
 * Time: 16:12
 * Mail：jihaifeng@raiyi.com
 */
public class CityManagerActivity extends BaseActivity {
  private SwipeMenuRecyclerView ryAddCity;
  private WeatherRyAdapter ryAdapter;
  private SwipeRefreshLayout cityManager;
  private boolean pullDown = false;
  private Toolbar toolbar;
  private TextView toolbar_title;
  private RelativeLayout rl_back;
  private RelativeLayout rl_more;

  private String cityName = "北京";
  private List<String> cityList;
  public static final int MAX_CITY_NUM = 8;
  public static final int MIN_CITY_NUM = 1;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_city);
    setToolBar();
    CustomStatusBar.setTranslucent(this, Color.TRANSPARENT, false, toolbar);
    initView();
    getWeather();
  }

  private void setToolBar() {
    toolbar = (Toolbar) findViewById(R.id.tb_title);
    toolbar_title = (TextView) findViewById(R.id.toolbar_title);
    rl_back = (RelativeLayout) findViewById(R.id.rl_back);
    rl_back.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        CityManagerActivity.this.finish();
      }
    });
    rl_more = (RelativeLayout) findViewById(R.id.rl_more);
    rl_more.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        if (CityUtils.getCityList().size() <= MAX_CITY_NUM) {
          startActivityForResult(new Intent(CityManagerActivity.this, AreaPickActivity.class), Config.ADD_CITY_CODE);
        } else {
          ToastUtil.showShort(CityManagerActivity.this, "亲，最多关注" + MAX_CITY_NUM + "个地区哦。。。");
        }
      }
    });
    toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.half_transparent));
    setSupportActionBar(toolbar);
    getSupportActionBar().setTitle("");
    toolbar_title.setText("城市管理");
  }

  private void initView() {
    ryAddCity = (SwipeMenuRecyclerView) findViewById(R.id.ry_addcity);
    ryAddCity.setLayoutManager(new LinearLayoutManager(this));
    ryAddCity.setHasFixedSize(true);
    ryAddCity.setSwipeMenuCreator(swipeMenuCreator);
    // 设置菜单Item点击监听。
    ryAddCity.setSwipeMenuItemClickListener(new OnSwipeMenuItemClickListener() {
      @Override public void onItemClick(Closeable closeable, int adapterPosition, int menuPosition, int direction) {
        closeable.smoothCloseMenu();// 关闭被点击的菜单。
        if (CityUtils.getCityList().size() > MIN_CITY_NUM) {
          //ryAdapter.notifyItemRemoved(adapterPosition);
          ryAdapter.removeData(adapterPosition);
        } else {
          ToastUtil.showShort(CityManagerActivity.this, "亲，至少要保留一个地区哦。。。");
        }
      }
    });
    if (null == ryAdapter) {
      ryAdapter = new WeatherRyAdapter(this);
    }
    ryAddCity.setAdapter(ryAdapter);
    cityManager = (SwipeRefreshLayout) findViewById(R.id.sf_city_manager);
    cityManager.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.default_indexBar_textColor, R.color.default_indexBar_selectedTextColor);
    cityManager.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override public void onRefresh() {
        pullDown = true;
        getWeather();
      }
    });
  }

  private void getWeather() {
    WeatherUtils.getWeather(CityManagerActivity.this, pullDown, new WeatherLinstener() {
      @Override public void showError(String msg) {
        pullDown = false;
        cityManager.setRefreshing(false);
        ToastUtil.showShort(CityManagerActivity.this, TextUtils.isEmpty(msg) ? "数据异常" : msg);
      }

      @Override public void showData(List<ResultsBean> results) {
        pullDown = false;
        cityManager.setRefreshing(false);
        ryAdapter.updateResultsData(results);
      }
    });
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode != Activity.RESULT_OK || data == null) {
      return;
    }
    switch (requestCode) {
      case Config.ADD_CITY_CODE:
        String city = data.getStringExtra(Config.CITY_NAME_INTENT);
        updateSelectCity(city);
        getWeather();
        break;
    }
  }

  private void updateSelectCity(String city) {
    cityName = city;
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

  /**
   * 菜单创建器。在Item要创建菜单的时候调用。
   */
  private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
    @Override public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {

      SwipeMenuItem addItem = new SwipeMenuItem(CityManagerActivity.this).setBackgroundDrawable(R.drawable.selector_green)// 点击的背景。
          .setImage(R.mipmap.ic_action_delete) // 图标。
          .setWidth(RecyclerView.LayoutParams.WRAP_CONTENT)
          // 宽度。
          .setHeight(RecyclerView.LayoutParams.WRAP_CONTENT); // 高度。
      swipeRightMenu.addMenuItem(addItem); // 添加一个按钮到左侧菜单。
      // 上面的菜单哪边不要菜单就不要添加。
    }
  };

  @Override protected void onPause() {
    super.onPause();
    pullDown = false;
  }

  @Override protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    pullDown = false;
  }
}
