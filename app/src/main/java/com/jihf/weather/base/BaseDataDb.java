package com.jihf.weather.base;

import android.content.ContentValues;
import android.text.TextUtils;
import java.util.List;
import org.litepal.crud.DataSupport;

/**
 * Func：
 * User：jihf
 * Data：2016-12-20
 * Time: 16:41
 * Mail：jihaifeng@raiyi.com
 */
public class BaseDataDb extends DataSupport {
  public String key;
  public String value;

  public static List<BaseDataDb> get(String key) {
    if (TextUtils.isEmpty(key)) {
      return null;
    }
    List<BaseDataDb> datas = DataSupport.where("key = ?", key).find(BaseDataDb.class);
    if (null != datas && datas.size() != 0) {
      return datas;
    }
    return null;
  }

  public static boolean insert(String key, String val) {
    if (TextUtils.isEmpty(key)) {
      return false;
    }
    boolean isKeyExist = DataSupport.isExist(BaseDataDb.class, "key = ? ", key);
    if (isKeyExist) {
      return update(key, val) > 0;
    }
    BaseDataDb baseDbData = new BaseDataDb();
    baseDbData.key = key;
    baseDbData.value = val;
    return baseDbData.save();
  }

  public static int update(String key, String val) {
    if (TextUtils.isEmpty(key)) {
      return 0;
    }
    ContentValues values = new ContentValues();
    values.put("value", val);
    return DataSupport.updateAll(BaseDataDb.class, values, "key = ? ", key);
  }

  public static int delete(String key) {
    if (TextUtils.isEmpty(key)) {
      return 0;
    }
    return DataSupport.deleteAll(BaseDataDb.class, "key = ? ", key);
  }
}
