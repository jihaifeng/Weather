package com.jihf.weather.utils;

import android.text.TextUtils;
import android.util.Log;
import com.jihf.weather.base.BaseDataDb;
import com.jihf.weather.config.Config;
import java.util.ArrayList;
import java.util.List;

/**
 * Func：
 * User：jihf
 * Data：2016-12-20
 * Time: 18:39
 * Mail：jihaifeng@raiyi.com
 */
public class CityUtils {
  public static final String TAG = CityUtils.class.getSimpleName().trim();

  public static List<String> getCityList() {
    if (TextUtils.isEmpty(getCityStr()) || null == getCityStr()) {
      return new ArrayList<>();
    }
    String cityData = getCityStr();
    return stringToList(cityData);
  }

  public static String getCityStr() {
    List<BaseDataDb> datas = BaseDataDb.get(Config.SELECT_CITY_LIST);
    if (null == datas) {
      return null;
    }
    String cityData = datas.get(0).value;
    return cityData;
  }

  public static boolean insertCityList(List<String> cityList) {
    if (null == cityList || cityList.size() == 0) {
      return false;
    }
    Log.i(TAG, "insertCityList: " + cityList.size());
    return BaseDataDb.insert(Config.SELECT_CITY_LIST, listToString(cityList));
  }

  public static int delete(String str) {
    if (TextUtils.isEmpty(str)) {
      return 0;
    }
    List<String> cityList = CityUtils.getCityList();
    if (cityList.contains(str)) {
      cityList.remove(str);
    }
    Log.i(TAG, "delete: " +cityList);
    return BaseDataDb.update(Config.SELECT_CITY_LIST, listToString(cityList));
  }

  public static List<String> stringToList(String key) {
    if (TextUtils.isEmpty(key)) {
      return null;
    }
    // |  ===>>>(转义)  \u007C
    List<String> list = new ArrayList<>();
    String[] str = key.split("\\u007C");
    for (String val : str) {
      list.add(val);
    }
    return list;
  }

  public static String listToString(List<String> list) {
    if (null == list || list.size() == 0) {
      return null;
    }
    String str = "";
    String end = list.get(list.size() - 1);
    for (int i = 0; i < list.size(); i++) {
      if (i != list.size() - 1) {
        if (TextUtils.isEmpty(str)) {
          str = list.get(i) + "|";
        } else {
          // |  ===>>>(转义)  \u007C
          str += list.get(i) + "|";
        }
      } else {
        if (TextUtils.isEmpty(str)) {
          str = list.get(i);
        } else {
          str += list.get(i);
        }
      }
    }
    Log.i(TAG, "listToString: " + str);
    return str;
  }
}
