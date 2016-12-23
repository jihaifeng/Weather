package com.jihf.weather.utils;

import android.app.Notification;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import static com.caverock.androidsvg.CSSParser.MediaType.tv;

/**
 * Func：
 * User：jihf
 * Data：2016-12-22
 * Time: 14:56
 * Mail：jihaifeng@raiyi.com
 */
public class NotificationUtils {
  public static final double COLOR_TH = 180.0;
  public static final String DUMMY_TITLE = "DUMMY_TITLE";
  private static TextView titleView;

  interface Iterator {
    void iterator(View view);
  }

  public static void showNotification() {

  }

  //通知栏字体颜色，true===>>>接近黑色的字体，false===>>>接近白色字体
  public static boolean isDarkNotification(Context context) {
    //如果是深色Noti背景，则title字体是浅色，反之亦然
    return !isSimilarColor(Color.BLACK, getNotificationTitleColor(context));
  }

  //获取通知栏title颜色
  private static int getNotificationTitleColor(Context context) {
    if (context instanceof AppCompatActivity) {
      return getNotificationTitleColorCompat(context);
    } else {
      return getNotificationTitleColorInternal(context);
    }
  }

  //获取通知栏title颜色
  private static int getNotificationTitleColorInternal(Context context) {
    NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
    builder.setContentTitle(DUMMY_TITLE);
    Notification notification = builder.build();
    ViewGroup viewGroup = (ViewGroup) notification.contentView.apply(context, new LinearLayout(context));
    titleView = (TextView) viewGroup.findViewById(android.R.id.title);
    if (null == tv) {
      //第三方厂商修改了通知栏布局的默认ID,遍历viewGroup获取text为DUMMY_TITLE的textView并获取其颜色
      iteratorView(viewGroup, new Iterator() {
        @Override public void iterator(View view) {
          if (view instanceof TextView) {
            TextView textView = (TextView) view;
            if (DUMMY_TITLE.equals(textView.getText().toString())) {
              titleView = textView;
            }
          }
        }
      });
    }
    return null == titleView ? 0 : titleView.getCurrentTextColor();
  }

  private static void iteratorView(View view, Iterator iterator) {
    if (null == view || null == iterator) {
      return;
    }
    iterator.iterator(view);
    if (view instanceof ViewGroup) {
      ViewGroup container = (ViewGroup) view;
      int viewNum = container.getChildCount();
      for (int i = 0; i < viewNum; i++) {
        View child = container.getChildAt(i);
        iteratorView(child, iterator);
      }
    }
  }

  //获取通知栏title颜色
  private static int getNotificationTitleColorCompat(Context context) {
    NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
    Notification notification = builder.build();
    int layoutId = notification.contentView.getLayoutId();
    ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(context).inflate(layoutId,null);
    titleView = (TextView) viewGroup.findViewById(android.R.id.title);
    if (null == titleView) {
      //厂商修改了默认ID，查找字体最大的认为是title
      //先找出所有的TextView
      final List<TextView> textViews = new ArrayList<>();
      iteratorView(viewGroup, new Iterator() {
        @Override public void iterator(View view) {
          if (view instanceof TextView) {
            textViews.add((TextView) view);
          }
        }
      });
      //获取字体最大的座位Title
      float maxTextSize = 0;
      int maxTvIndex = 0;
      int tvNum = textViews.size();
      for (int i = 0; i < tvNum; i++) {
        if (maxTextSize < textViews.get(i).getTextSize()) {
          maxTextSize = textViews.get(i).getTextSize();
          maxTvIndex = i;
        }
      }
      titleView = textViews.get(maxTvIndex);
    }
    return titleView.getCurrentTextColor();
  }

  //判断matchColor是否近似baseColor
  public static boolean isSimilarColor(int baseColor, int matchColor) {
    int simpleBaseColor = baseColor | 0xff000000;
    int simpleMatchColor = matchColor | 0xff000000;
    int baseRed = Color.red(simpleBaseColor) - Color.red(simpleMatchColor);
    int baseGreen = Color.green(simpleBaseColor) - Color.green(simpleMatchColor);
    int baseBlue = Color.blue(simpleBaseColor) - Color.blue(simpleMatchColor);
    double val = Math.sqrt(baseRed * baseRed + baseGreen * baseGreen + baseBlue * baseBlue);
    if (val < COLOR_TH) {
      return true;
    }
    return false;
  }
}
