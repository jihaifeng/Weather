package com.jihf.weather.utils;

import android.content.Context;
import android.content.res.TypedArray;
import com.jihf.weather.R;

/**
 * Func：
 * User：jihf
 * Data：2016-12-20
 * Time: 14:28
 * Mail：jihaifeng@raiyi.com
 */
public class ToolBarUtils {

  public static int getToolbarHeight(Context context) {
    final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(new int[] { R.attr.actionBarSize });
    int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
    styledAttributes.recycle();

    return toolbarHeight;
  }
}
