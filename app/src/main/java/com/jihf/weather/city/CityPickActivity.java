package com.jihf.weather.city;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import com.jihf.weather.R;
import com.jihf.weather.base.BaseActivity;
import com.jihf.weather.config.Config;
import com.jihf.weather.utils.ToastUtil;
import com.jihf.weather.weather.WeatherActivity;
import com.raiyi.wsh_lib_bdlocation.mgr.AddressListener;
import com.raiyi.wsh_lib_bdlocation.mgr.LocationMgr;
import com.raiyi.wsh_lib_bdlocation.mgr.bean.AddressItem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import me.yokeyword.indexablerv.IndexableAdapter;
import me.yokeyword.indexablerv.IndexableLayout;
import me.yokeyword.indexablerv.SimpleHeaderAdapter;

/**
 * 选择城市
 * Created by YoKey on 16/10/7.
 */
public class CityPickActivity extends BaseActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
  private List<CityEntity> mDatas;
  private SearchFragment mSearchFragment;
  private SearchView mSearchView;
  private FrameLayout mProgressBar;
  private SimpleHeaderAdapter gpsHeaderAdapter;
  private List<CityEntity> gpsCity;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_pick_city);
    //getSupportActionBar().setTitle("选择城市");

    mSearchFragment = (SearchFragment) getSupportFragmentManager().findFragmentById(R.id.search_fragment);
    final IndexableLayout indexableLayout = (IndexableLayout) findViewById(R.id.indexableLayout);
    mSearchView = (SearchView) findViewById(R.id.searchview);
    mProgressBar = (FrameLayout) findViewById(R.id.progress);

    // setAdapter
    CityAdapter adapter = new CityAdapter(this);
    indexableLayout.setAdapter(adapter);
    // set Datas
    mDatas = initDatas();

    // 快速排序。  排序规则设置为：只按首字母  （默认全拼音排序）  效率很高，是默认的10倍左右。  按需开启～
    //        indexableLayout.setFastCompare(true);

    adapter.setDatas(mDatas, new IndexableAdapter.IndexCallback<CityEntity>() {
      @Override public void onFinished(List<CityEntity> datas) {
        // 数据处理完成后回调
        mSearchFragment.bindDatas(mDatas);
        mProgressBar.setVisibility(View.GONE);
      }
    });

    // set Listener
    adapter.setOnItemContentClickListener(new IndexableAdapter.OnItemContentClickListener<CityEntity>() {
      @Override public void onItemClick(View v, int originalPosition, int currentPosition, CityEntity entity) {
        if (entity.getName().equalsIgnoreCase("定位失败")){
          if (ActivityCompat.checkSelfPermission(CityPickActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(CityPickActivity.this, new String[] { Manifest.permission.ACCESS_COARSE_LOCATION }, Config.PERMISSION_LOCATION);
          } else {
            startLocation();
          }
        }else {
          Bundle bundle = new Bundle();
          bundle.putString(Config.CITY_NAME, entity.getName());
          JumpToWithBundle(CityPickActivity.this, WeatherActivity.class, bundle);
          CityPickActivity.this.finish();
        }
      }
    });

    adapter.setOnItemTitleClickListener(new IndexableAdapter.OnItemTitleClickListener() {
      @Override public void onItemClick(View v, int currentPosition, String indexTitle) {
        //ToastUtil.showShort(CityPickActivity.this, "选中:" + indexTitle + "  当前位置:" + currentPosition);
      }
    });

    // 添加 HeaderView DefaultHeaderAdapter接收一个IndexableAdapter, 使其布局以及点击事件和IndexableAdapter一致
    // 如果想自定义布局,点击事件, 可传入 new IndexableHeaderAdapter

    // 热门城市
    indexableLayout.addHeaderAdapter(new SimpleHeaderAdapter<>(adapter, "热", "热门城市", iniyHotCityDatas()));
    gpsCity = iniyGPSCityDatas();
    gpsHeaderAdapter = new SimpleHeaderAdapter<>(adapter, "定", "当前城市", gpsCity);
    indexableLayout.addHeaderAdapter(gpsHeaderAdapter);
    if (ActivityCompat.checkSelfPermission(CityPickActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
      ActivityCompat.requestPermissions(CityPickActivity.this, new String[] { Manifest.permission.ACCESS_COARSE_LOCATION }, Config.PERMISSION_LOCATION);
    } else {
      startLocation();
    }
    // 搜索Demo
    initSearch();
  }

  private void startLocation() {
    // 定位

    LocationMgr.getInstance(CityPickActivity.this, "all").locationAddress(new AddressListener() {
      @Override public void getAddress(AddressItem address) {
        if (address == null || TextUtils.isEmpty(address.getCity())) {
          gpsCity.get(0).setName("定位失败");
        } else {
          gpsCity.get(0).setName(address.getCity());
        }
        gpsHeaderAdapter.notifyDataSetChanged();
      }
    });
  }

  private List<CityEntity> initDatas() {
    List<CityEntity> list = new ArrayList<>();
    List<String> cityStrings = Arrays.asList(getResources().getStringArray(R.array.city_array));
    for (String item : cityStrings) {
      CityEntity cityEntity = new CityEntity();
      cityEntity.setName(item);
      list.add(cityEntity);
    }
    return list;
  }

  private List<CityEntity> iniyHotCityDatas() {
    List<CityEntity> list = new ArrayList<>();
    list.add(new CityEntity("杭州市"));
    list.add(new CityEntity("北京市"));
    list.add(new CityEntity("上海市"));
    list.add(new CityEntity("广州市"));
    return list;
  }

  private List<CityEntity> iniyGPSCityDatas() {
    List<CityEntity> list = new ArrayList<>();
    list.add(new CityEntity("定位中..."));
    return list;
  }

  private void initSearch() {
    getSupportFragmentManager().beginTransaction().hide(mSearchFragment).commit();

    mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      @Override public boolean onQueryTextSubmit(String query) {
        return false;
      }

      @Override public boolean onQueryTextChange(String newText) {
        if (newText.trim().length() > 0) {
          if (mSearchFragment.isHidden()) {
            getSupportFragmentManager().beginTransaction().show(mSearchFragment).commit();
          }
        } else {
          if (!mSearchFragment.isHidden()) {
            getSupportFragmentManager().beginTransaction().hide(mSearchFragment).commit();
          }
        }

        mSearchFragment.bindQueryText(newText);
        return false;
      }
    });
  }

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if (requestCode == Config.PERMISSION_LOCATION) {
      if (grantResults.length > 0) {
        startLocation();
      } else {
        ToastUtil.showShort(CityPickActivity.this, "缺少定位权限。。。");
      }
    }
  }

  @Override public void onBackPressed() {
    if (!mSearchFragment.isHidden()) {
      // 隐藏 搜索
      mSearchView.setQuery(null, false);
      getSupportFragmentManager().beginTransaction().hide(mSearchFragment).commit();
      return;
    }
    super.onBackPressed();
  }
}
