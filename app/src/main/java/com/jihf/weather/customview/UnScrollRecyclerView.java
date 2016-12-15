package com.jihf.weather.customview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Func：
 * User：jihf
 * Data：2016-12-15
 * Time: 14:27
 * Mail：jihaifeng@raiyi.com
 */
public class UnScrollRecyclerView extends RecyclerView {
  public UnScrollRecyclerView(Context context) {
    super(context);
  }

  public UnScrollRecyclerView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public UnScrollRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override protected void onMeasure(int widthSpec, int heightSpec) {
    int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
    super.onMeasure(widthSpec, expandSpec);
  }
}
