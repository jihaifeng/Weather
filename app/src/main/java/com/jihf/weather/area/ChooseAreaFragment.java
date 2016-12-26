package com.jihf.weather.area;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.jihf.weather.R;
import com.jihf.weather.area.bean.City;
import com.jihf.weather.area.bean.County;
import com.jihf.weather.area.bean.Province;
import com.jihf.weather.city.CityManagerActivity;
import com.jihf.weather.config.Config;
import com.jihf.weather.config.PermissionConfig;
import com.jihf.weather.http.HttpLinstener;
import com.jihf.weather.http.HttpManager;
import com.jihf.weather.main.MainActivity;
import com.jihf.weather.utils.CityUtils;
import com.jihf.weather.utils.DrawableUtils;
import com.jihf.weather.utils.ToastUtil;
import com.jihf.weather.utils.Utility;
import com.jihf.weather.weather.WeatherActivity;
import com.raiyi.wsh_lib_bdlocation.mgr.AddressListener;
import com.raiyi.wsh_lib_bdlocation.mgr.LocationMgr;
import com.raiyi.wsh_lib_bdlocation.mgr.bean.AddressItem;
import java.util.ArrayList;
import java.util.List;
import org.litepal.crud.DataSupport;

import static android.app.Activity.RESULT_OK;

public class ChooseAreaFragment extends Fragment {

  private static final String TAG = "ChooseAreaFragment";

  public static final int LEVEL_PROVINCE = 1;

  public static final int LEVEL_CITY = 2;

  public static final int LEVEL_COUNTY = 3;

  private ProgressDialog progressDialog;

  private TextView titleText;
  //location
  private static TextView tv_ocation;
  private static LinearLayout ll_Location;
  private static ProgressBar pb_location;
  private static ImageView iv_location;

  private RelativeLayout rl_back;
  private RelativeLayout rl_more;
  private SwipeRefreshLayout sf_area;

  private ListView listView;

  private ArrayAdapter<String> adapter;

  private List<String> dataList = new ArrayList<>();

  /**
   * 省列表
   */
  private List<Province> provinceList;

  /**
   * 市列表
   */
  private List<City> cityList;

  /**
   * 县列表
   */
  private List<County> countyList;

  /**
   * 选中的省份
   */
  private Province selectedProvince;

  /**
   * 选中的城市
   */
  private City selectedCity;

  /**
   * 当前选中的级别
   */
  private int currentLevel = LEVEL_PROVINCE;

  private String strLocation = "定位中。。。";

  private static Context mContext;

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.choose_area, container, false);
    mContext = getActivity();
    initView(view);
    getLocationAddr();
    adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
    listView.setAdapter(adapter);
    return view;
  }

  private void initView(View view) {
    titleText = (TextView) view.findViewById(R.id.toolbar_title);
    rl_back = (RelativeLayout) view.findViewById(R.id.rl_back);
    rl_more = (RelativeLayout) view.findViewById(R.id.rl_more);
    rl_more.setVisibility(View.GONE);
    listView = (ListView) view.findViewById(R.id.list_view);
    tv_ocation = (TextView) view.findViewById(R.id.tv_location);
    ll_Location = (LinearLayout) view.findViewById(R.id.ll_location);
    pb_location = (ProgressBar) view.findViewById(R.id.pb_location_refresh);
    iv_location = (ImageView) view.findViewById(R.id.iv_location_refresh);
    tv_ocation.setText("定位中。。。");
    pb_location.setVisibility(View.VISIBLE);
    iv_location.setVisibility(View.GONE);
    iv_location.setImageDrawable(DrawableUtils.tintDrawable(getActivity(), R.drawable.location_refresh, R.color.gray));
    ll_Location.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        if (tv_ocation.getText().toString().contains("定位失败")) {
          getLocationAddr();
        } else {
          doNext(tv_ocation.getText().toString());
        }
      }
    });
    sf_area = (SwipeRefreshLayout) view.findViewById(R.id.sf_area);
    sf_area.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override public void onRefresh() {
        getLocationAddr();
        if (currentLevel == LEVEL_PROVINCE) {
          queryProvinces();
        } else if (currentLevel == LEVEL_CITY) {
          queryCities();
        } else if (currentLevel == LEVEL_COUNTY) {
          queryCounties();
        }
      }
    });
  }

  private void getLocationAddr() {
    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
      ActivityCompat.requestPermissions(getActivity(), new String[] { Manifest.permission.ACCESS_COARSE_LOCATION }, PermissionConfig.PERMISSION_LOCATION);
    } else {
      startLocation();
    }
  }

  public static void startLocation() {
    // 定位
    tv_ocation.setText("定位中。。。");
    pb_location.setVisibility(View.VISIBLE);
    iv_location.setVisibility(View.GONE);
    LocationMgr.getInstance(mContext, "all").locationAddress(new AddressListener() {
      @Override public void getAddress(AddressItem address) {
        if (address == null || TextUtils.isEmpty(address.getAddress())) {
          tv_ocation.setText("定位失败 (点击刷新) ");
          pb_location.setVisibility(View.GONE);
          iv_location.setVisibility(View.VISIBLE);
        } else {
          pb_location.setVisibility(View.GONE);
          iv_location.setVisibility(View.GONE);
          if (!TextUtils.isEmpty(address.getDistrict())) {
            //区,县
            tv_ocation.setText(address.getDistrict());
          } else if (!TextUtils.isEmpty(address.getCity())) {
            //市
            tv_ocation.setText(address.getCity());
          } else {
            tv_ocation.setText("定位失败,位置信息不全 (点击刷新) ");
          }
        }
      }
    });
  }

  @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (currentLevel == LEVEL_PROVINCE) {
          selectedProvince = provinceList.get(position);
          queryCities();
        } else if (currentLevel == LEVEL_CITY) {
          selectedCity = cityList.get(position);
          queryCounties();
        } else if (currentLevel == LEVEL_COUNTY) {
          String weatherId = countyList.get(position).getCountyName();
          doNext(weatherId);
        }
      }
    });
    rl_back.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (currentLevel == LEVEL_COUNTY) {
          queryCities();
        } else if (currentLevel == LEVEL_CITY) {
          queryProvinces();
        } else {
          getActivity().finish();
        }
      }
    });
    queryProvinces();
  }

  private void doNext(String weatherId) {
    if (getActivity() instanceof AreaPickActivity) {
      if (CityUtils.getCityList().contains(weatherId)) {
        ToastUtil.showShort(getActivity(), "该地区正在关注中不可重复添加哦。。。");
      } else {
        Intent intent = new Intent(getActivity(), CityManagerActivity.class);
        intent.putExtra(Config.CITY_NAME_INTENT, weatherId);
        getActivity().setResult(RESULT_OK, intent);
        getActivity().finish();
      }
    } else if (getActivity() instanceof MainActivity) {
      Bundle bundle = new Bundle();
      bundle.putString(Config.CITY_NAME_INTENT, weatherId);
      Intent intent = new Intent(getActivity(), WeatherActivity.class);
      intent.putExtras(bundle);
      startActivity(intent);
      getActivity().finish();
    }
  }

  /**
   * 查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询。
   */
  private void queryProvinces() {
    titleText.setText("中国");
    if (getActivity() instanceof MainActivity) {
      rl_back.setVisibility(View.GONE);
    }
    provinceList = DataSupport.findAll(Province.class);
    if (provinceList.size() > 0) {
      dataList.clear();
      for (Province province : provinceList) {
        dataList.add(province.getProvinceName());
      }
      if (null != sf_area) {
        sf_area.setRefreshing(false);
      }
      adapter.notifyDataSetChanged();
      listView.setSelection(0);
      currentLevel = LEVEL_PROVINCE;
    } else {
      String address = "http://guolin.tech/api/china";
      queryFromServer(address, "province");
    }
  }

  /**
   * 查询选中省内所有的市，优先从数据库查询，如果没有查询到再去服务器上查询。
   */
  private void queryCities() {
    if (null == selectedProvince) {
      ToastUtil.showShort(getActivity(), "数据异常").show();
      return;
    }
    titleText.setText(selectedProvince.getProvinceName());
    rl_back.setVisibility(View.VISIBLE);
    cityList = DataSupport.where("provinceid = ?", String.valueOf(selectedProvince.getId())).find(City.class);
    if (cityList.size() > 0) {
      dataList.clear();
      for (City city : cityList) {
        dataList.add(city.getCityName());
      }
      if (null != sf_area) {
        sf_area.setRefreshing(false);
      }
      adapter.notifyDataSetChanged();
      listView.setSelection(0);
      currentLevel = LEVEL_CITY;
    } else {
      int provinceCode = selectedProvince.getProvinceCode();
      String address = "http://guolin.tech/api/china/" + provinceCode;
      queryFromServer(address, "city");
    }
  }

  /**
   * 查询选中市内所有的县，优先从数据库查询，如果没有查询到再去服务器上查询。
   */
  private void queryCounties() {
    if (null == selectedCity) {
      ToastUtil.showShort(getActivity(), "数据异常").show();
      return;
    }
    titleText.setText(selectedCity.getCityName());
    rl_back.setVisibility(View.VISIBLE);
    countyList = DataSupport.where("cityid = ?", String.valueOf(selectedCity.getId())).find(County.class);
    if (countyList.size() > 0) {
      dataList.clear();
      for (County county : countyList) {
        dataList.add(county.getCountyName());
      }
      if (null != sf_area) {
        sf_area.setRefreshing(false);
      }
      adapter.notifyDataSetChanged();
      listView.setSelection(0);
      currentLevel = LEVEL_COUNTY;
    } else {
      int provinceCode = selectedProvince.getProvinceCode();
      int cityCode = selectedCity.getCityCode();
      String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
      queryFromServer(address, "county");
    }
  }

  /**
   * 根据传入的地址和类型从服务器上查询省市县数据。
   */
  private void queryFromServer(String address, final String type) {
    showProgressDialog();
    HttpManager.getInstance(getActivity()).getCity(address, new HttpLinstener() {
      @Override public void onSuccess(String response) {
        if (null != sf_area) {
          sf_area.setRefreshing(false);
        }
        boolean result = false;
        if ("province".equals(type)) {
          result = Utility.handleProvinceResponse(response);
        } else if ("city".equals(type)) {
          result = Utility.handleCityResponse(response, selectedProvince.getId());
        } else if ("county".equals(type)) {
          result = Utility.handleCountyResponse(response, selectedCity.getId());
        }
        if (result) {
          getActivity().runOnUiThread(new Runnable() {
            @Override public void run() {
              closeProgressDialog();
              if ("province".equals(type)) {
                queryProvinces();
              } else if ("city".equals(type)) {
                queryCities();
              } else if ("county".equals(type)) {
                queryCounties();
              }
            }
          });
        }
      }

      @Override public void onFailure(String msg, Throwable e) {
        // 通过runOnUiThread()方法回到主线程处理逻辑
        getActivity().runOnUiThread(new Runnable() {
          @Override public void run() {
            if (null != sf_area) {
              sf_area.setRefreshing(false);
            }
            closeProgressDialog();
            Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
          }
        });
      }
    });
  }

  /**
   * 显示进度对话框
   */
  private void showProgressDialog() {
    if (progressDialog == null) {
      progressDialog = new ProgressDialog(getActivity());
      progressDialog.setMessage("正在加载...");
      progressDialog.setCanceledOnTouchOutside(false);
    }
    progressDialog.show();
  }

  /**
   * 关闭进度对话框
   */
  private void closeProgressDialog() {
    if (progressDialog != null) {
      progressDialog.dismiss();
    }
  }
}
