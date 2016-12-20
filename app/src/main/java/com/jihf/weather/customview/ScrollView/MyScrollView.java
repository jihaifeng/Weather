package com.jihf.weather.customview.ScrollView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.ScrollView;

/**
 * Func：屏蔽 滑动事件
 * User：jihf
 * Data：2016-09-26
 * Time: 10:23
 * Mail：jihaifeng@raiyi.com
 */
public class MyScrollView extends ScrollView {
  private int downX;
  private int downY;
  private int mTouchSlop;
  private OnScrollChangedListener mOnScrollChangedListener;

  public MyScrollView(Context context) {
    super(context);
    mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
  }

  public MyScrollView(Context context, AttributeSet attrs) {
    super(context, attrs);
    mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
  }

  public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
  }

  @Override public boolean onInterceptTouchEvent(MotionEvent e) {
    int action = e.getAction();
    switch (action) {
      case MotionEvent.ACTION_DOWN:
        downX = (int) e.getRawX();
        downY = (int) e.getRawY();
        break;
      case MotionEvent.ACTION_MOVE:
        int moveY = (int) e.getRawY();
        if (Math.abs(moveY - downY) > mTouchSlop) {
          return true;
        }
    }
    return super.onInterceptTouchEvent(e);
  }

  @Override protected void onScrollChanged(int l, int t, int oldl, int oldt) {
    super.onScrollChanged(l, t, oldl, oldt);
    if (mOnScrollChangedListener != null) {
      mOnScrollChangedListener.onScrollChanged(this, l, t, oldl, oldt);
    }
  }

  public void setOnScrollListener(OnScrollChangedListener listener) {
    mOnScrollChangedListener = listener;
  }

  public interface OnScrollChangedListener {
    void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt);
  }
}
