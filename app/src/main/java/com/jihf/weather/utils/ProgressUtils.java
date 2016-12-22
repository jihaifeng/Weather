package com.jihf.weather.utils;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Func：
 * User：jihf
 * Data：2016-12-21
 * Time: 14:24
 * Mail：jihaifeng@raiyi.com
 */
public class ProgressUtils {
  private static ProgressDialog progressDialog;

  /**
   * 显示进度对话框
   */
  public static void showProgressDialog(Context context) {
    if (progressDialog == null) {
      progressDialog = new ProgressDialog(context);
    }
    progressDialog.setMessage("正在加载...");
    progressDialog.setCanceledOnTouchOutside(false);
    progressDialog.show();
  }

  /**
   * 关闭进度对话框
   */
  public static void hideProgressDialog() {
    if (progressDialog != null && progressDialog.isShowing()) {
      progressDialog.dismiss();
    }
    progressDialog = null;
  }
}
