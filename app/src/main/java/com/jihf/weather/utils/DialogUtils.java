package com.jihf.weather.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

/**
 * Func：
 * User：jihf
 * Data：2016-12-26
 * Time: 11:08
 * Mail：jihaifeng@raiyi.com
 */
public class DialogUtils {
  private static AlertDialog alertDialog;
  private static Context mContext;
  private static AlertDialog.Builder builder;
  private static DialogUtils instance;

  public static DialogUtils getInstance(Context context) {
    mContext = context;
    if (null == instance) {
      synchronized (DialogUtils.class) {
        if (null == instance) {
          instance = new DialogUtils();
        }
      }
    }
    return instance;
  }

  public void showDefault(String title, String msg, String positiveText, String negativeText, final buttonLinstener linstener) {
    final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
    builder.setTitle(title);
    builder.setMessage(msg);
    if (!TextUtils.isEmpty(positiveText)) {
      builder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
        @Override public void onClick(DialogInterface dialogInterface, int i) {
          dismiss();
          linstener.onPositiveClick();
        }
      });
    }
    if (!TextUtils.isEmpty(negativeText)) {
      builder.setNegativeButton(negativeText, new DialogInterface.OnClickListener() {
        @Override public void onClick(DialogInterface dialogInterface, int i) {
          dismiss();
          linstener.onNegativeClick();
        }
      });
    }
    alertDialog = builder.create();
    alertDialog.show();
  }

  public interface buttonLinstener {
    void onNegativeClick();

    void onPositiveClick();

    void onNeutralClick();
  }

  public static void dismiss() {
    if (null != alertDialog) {
      alertDialog.dismiss();
    }
    clearDialogObject();
  }

  private static void clearDialogObject() {
    if (null != alertDialog) {
      alertDialog = null;
    }
    if (null != mContext) {
      mContext = null;
    }

    if (null != builder) {
      builder = null;
    }
  }
}

