package com.jihf.weather.area.bean;

import android.content.ContentValues;
import android.text.TextUtils;
import android.util.Log;
import java.util.List;
import org.litepal.crud.DataSupport;

/**
 * Func：
 * User：jihf
 * Data：2016-12-20
 * Time: 16:41
 * Mail：jihaifeng@raiyi.com
 */
public class BaseDbData extends DataSupport {
  public String title;
  public String content;

  public static List<BaseDbData> get(String key) {
    if (TextUtils.isEmpty(key)) {
      return null;
    }
    List<BaseDbData> datas = DataSupport.where("title = ?", key).find(BaseDbData.class);
    if (null != datas && datas.size() != 0) {
      return datas;
    }
    return null;
  }

  public static boolean insert(String key, String val) {
    if (TextUtils.isEmpty(key)) {
      return false;
    }
    boolean isKeyExist = DataSupport.isExist(BaseDbData.class, "title = ? ", key);
    Log.i("CityUtils", "insert: " + isKeyExist);
    if (isKeyExist) {
      return update(key, val) > 0;
    }
    BaseDbData baseDbData = new BaseDbData();
    baseDbData.title = key;
    baseDbData.content = val;
    return baseDbData.save();
  }

  public static int update(String key, String val) {
    if (TextUtils.isEmpty(key)) {
      return 0;
    }
    ContentValues values = new ContentValues();
    values.put("content", val);
    return DataSupport.updateAll(BaseDbData.class, values, "title = ? ", key);
  }

  public static int delete(String key) {
    if (TextUtils.isEmpty(key)) {
      return 0;
    }
    return DataSupport.deleteAll(BaseDbData.class, "title = ? ", key);
  }
}
