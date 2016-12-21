package com.jihf.weather.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 自定义状态栏，导航栏沉浸
 *
 * @author jihf
 * @Impor 切记，此类须在setContentView()后调用
 */
public class CustomStatusBar {
  // 半透明效果
  public static final int DEFAULT_STATUS_BAR_ALPHA = 56;
  // 状态栏
  private static int TRANSLUCENT_STATUS = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
  // 导航栏
  private static int TRANSLUCENT_NAVIGATION = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
  // system_bar
  private static int SYSTEM_BAR = WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;
  // android 6.0设置深色状态栏
  public static final int SYSTEM_UI_FLAG_LIGHT_STATUS_BAR = 0x00002000;
  public static final int SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN = 0x00000400;
  public static   boolean isWhiteTitle = false;

  /**
   * 设置状态栏颜色
   *
   * @param activity 需要设置的activity
   * @param color 状态栏颜色值
   * @param statusBarAlpha 状态栏透明度
   */
  public static void setColor(Activity activity, int color, boolean isWhiteBg, int statusBarAlpha) {
    Log.e("Color>>>>>Build.BRAND", "" + Build.BRAND);
    if ("Xiaomi".equalsIgnoreCase(Build.BRAND) && isWhiteBg) {
      MIUISetStatusBar(activity, true);
    } else if ("MeiZu".equalsIgnoreCase(Build.BRAND) && isWhiteBg) {
      FlymeSetStatusBar(activity, true, null);
    } else {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        activity.getWindow().addFlags(SYSTEM_BAR);
        activity.getWindow().clearFlags(TRANSLUCENT_STATUS);
        if (isWhiteBg && Build.VERSION.SDK_INT < 23) {
          // 6.0以下白色背景
          statusBarAlpha = DEFAULT_STATUS_BAR_ALPHA;
        }
        activity.getWindow().setStatusBarColor(calculateStatusColor(color, statusBarAlpha));
      } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        if (isWhiteBg && Build.VERSION.SDK_INT < 23) {
          // 6.0以下白色背景
          statusBarAlpha = DEFAULT_STATUS_BAR_ALPHA;
        }
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // 生成一个状态栏大小的矩形
        View statusView = createStatusBarView(activity, color, statusBarAlpha);
        // 添加 statusView 到布局中
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        decorView.addView(statusView);
        setRootView(activity, true, null, null);
      }
    }
  }

  /**
   * 普通Activity的沉浸
   *
   * @param activity 需要设置沉浸的Activty(不能为空)
   * @param statusBarAlpha 沉浸栏的透明度（0-255，0-全透明，半透明-112，255-不透明）
   * @param isWhiteBg 是否白色背景，若是白色背景（true）并且该机型在android
   * 6.0以下则添加半透明状态栏，此时statusBarAlpha参数无效
   * @param statusBar 状态栏的背景色需要设置和status一致(可以为空，为空时默认整个布局的背景色)
   */
  public static void setTranslucent(Activity activity, int statusBarAlpha, boolean isWhiteBg, ViewGroup statusBar) {
    Log.e("Translucent>>>>>>Build.BRAND", "" + Build.BRAND);
    if ("Xiaomi".equalsIgnoreCase(Build.BRAND) && isWhiteBg) {
      MIUISetStatusBar(activity, true);
    } else if ("MeiZu".equalsIgnoreCase(Build.BRAND) && isWhiteBg) {
      FlymeSetStatusBar(activity, true, null);
    } else {
      if (isWhiteBg && Build.VERSION.SDK_INT < 23) {
        // 白色背景状态栏 并且是android 6.0 以下
        setTranslucent(activity, null, DEFAULT_STATUS_BAR_ALPHA, true, statusBar, true);
      } else {
        // 非白色状态栏或是android 6.0 以上
        if (statusBar != null) {
          setTranslucent(activity, null, statusBarAlpha, false, statusBar, true);
        } else {

          setTranslucent(activity, null, statusBarAlpha, true, statusBar, true);
        }
      }
    }
  }

  /**
   * DrawLayout的沉浸
   *
   * @param activity 需要设置沉浸的Activty(不能为空)
   * @param drawerLayout 是否使用了drawerLayout布局，可以为空，即非drawerLayout布局
   * @param isWhiteBg 是否白色背景，若是白色背景并且该机型在android 6.0以下则添加半透明状态栏，此时statusBarAlpha参数无效
   * @param statusBarAlpha 沉浸栏的透明度（0-255，0-全透明，半透明-112，255-不透明）
   * @param statusBar 状态栏的背景色需要设置和status一致(可以为空，为空时默认整个布局的背景色)
   */
  public static void setTranslucentForDrawLayout(Activity activity, DrawerLayout drawerLayout, boolean isWhiteBg, int statusBarAlpha, ViewGroup statusBar) {
    Log.e("DrawLayout>>>>Build.BRAND", "" + Build.BRAND);
    if ("Xiaomi".equalsIgnoreCase(Build.BRAND) && drawerLayout == null && isWhiteBg) {
      MIUISetStatusBar(activity, true);
    } else if ("MeiZu".equalsIgnoreCase(Build.BRAND) && isWhiteBg) {
      FlymeSetStatusBar(activity, true, drawerLayout);
    } else {
      if (isWhiteBg && Build.VERSION.SDK_INT < 23) {
        // 白色背景状态栏 并且是android 6.0 以下
        setTranslucent(activity, drawerLayout, DEFAULT_STATUS_BAR_ALPHA, true, statusBar, true);
      } else {
        // 非白色状态栏或是android 6.0 以上

        if (statusBar != null) {
          setTranslucent(activity, drawerLayout, statusBarAlpha, false, statusBar, true);
        } else {

          setTranslucent(activity, drawerLayout, statusBarAlpha, true, statusBar, true);
        }
      }
    }
  }

  /**
   * 图片背景的沉浸
   *
   * @param activity 需要设置沉浸的Activty(不能为空)
   * @param statusBarAlpha 沉浸栏的透明度（0-255，0-全透明，半透明-112，255-不透明）
   * @param isWhiteBg 是否白色背景，若是白色背景并且该机型在android 6.0以下则添加半透明状态栏，此时statusBarAlpha参数无效
   * @param isAll true===导航栏和状态栏都变色，false === 仅状态栏变色
   */
  public static void setTranslucentForPic(Activity activity, int statusBarAlpha, boolean isWhiteBg, boolean isAll) {
    setTranslucent(activity, null, statusBarAlpha, false, null, isAll);
    isWhiteTitle = isWhiteBg;
    Log.e("Pic>>>>>Build.BRAND", "" + Build.BRAND);
    if ("Xiaomi".equalsIgnoreCase(Build.BRAND) && isWhiteBg) {
      MIUISetStatusBar(activity, true);
    } else if ("MeiZu".equalsIgnoreCase(Build.BRAND) && isWhiteBg) {
      FlymeSetStatusBar(activity, true, null);
    } else {
      if (isWhiteBg && Build.VERSION.SDK_INT < 23) {
        // 白色背景状态栏 并且是android 6.0 以下
        setTranslucent(activity, null, DEFAULT_STATUS_BAR_ALPHA, false, null, isAll);
      } else {
        // 非白色状态栏或是android 6.0 以上
        setTranslucent(activity, null, statusBarAlpha, false, null, isAll);
      }
    }
  }

  /**
   * 设置透明属性
   *
   * @param activity 需要设置沉浸的Activty(不能为空)
   * @param drawerLayout 是否使用了drawerLayout布局，可以为空，即非drawerLayout布局
   * @param statusBarAlpha 沉浸栏的透明度（0-255，0-全透明，半透明-112，255-不透明）
   * @param diff 是否需要显示状态栏（使用图片做背景时一般为false）
   * @param statusBar 状态栏的背景色需要设置和status一致(可以为空，为空时默认整个布局的背景色)
   * @param isAll true===导航栏和状态栏都变色，false === 仅状态栏变色
   */
  private static void setTranslucent(Activity activity, DrawerLayout drawerLayout, int statusBarAlpha, boolean diff, ViewGroup statusBar, boolean isAll) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
      // android 4.4以下，不做修改
      return;
    }
    setTransparent(activity, drawerLayout, diff, isAll, statusBar);
    addTranslucentView(activity, statusBarAlpha);
  }

  /**
   * 设置透明属性
   *
   * @param activity 需要设置沉浸的Activty(不能为空)
   * @param drawerLayout 是否使用了drawerLayout布局，可以为空，即非drawerLayout布局
   * @param diff 是否需要显示状态栏（使用图片做背景时一般为false）
   * @param isAll true===导航栏和状态栏都变色，false === 仅状态栏变色
   * @param statusBar 状态栏的背景色需要设置和status一致(可以为空，为空时默认整个布局的背景色)
   */
  private static void setTransparent(Activity activity, DrawerLayout drawerLayout, boolean diff, boolean isAll, ViewGroup statusBar) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
      return;
    }
    transparentStatusBar(activity, isAll);
    setRootView(activity, diff, statusBar, drawerLayout);
  }

  private static void transparentStatusBar(Activity activity, boolean isAll) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
      // android 4.4以下
      return;
      // activity.requestWindowFeature(Window.FEATURE_NO_TITLE);//
      // 去掉标题栏

    } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
      // android 4.4 到android 5.0
      activity.getWindow().addFlags(TRANSLUCENT_STATUS);
    } else if (Build.VERSION.SDK_INT < 23) {
      // android 5.0到android 6.0
      activity.getWindow().addFlags(SYSTEM_BAR);
      activity.getWindow().clearFlags(TRANSLUCENT_STATUS);
      activity.getWindow().addFlags(TRANSLUCENT_STATUS);
      activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
      setSystemUi(activity);
    } else if (Build.VERSION.SDK_INT >= 23) {
      // android 6.0以上
      activity.getWindow().addFlags(SYSTEM_BAR);
      activity.getWindow().clearFlags(TRANSLUCENT_STATUS);
      activity.getWindow().addFlags(TRANSLUCENT_STATUS);
      activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
      // android 6.0 设置深色状态栏
      if (isWhiteTitle) {
        setSystemUi(activity);
      }
    }
  }

  /**
   * 设置状态栏字体颜色
   */
  private static void setSystemUi(Activity activity) {
    activity.getWindow().getDecorView().setSystemUiVisibility(SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
  }

  /**
   * 设置根布局是否用于图片
   *
   * @param activity 需要设置沉浸的Activty(不能为空)
   * @param diff 是否需要显示状态栏（使用图片做背景时一般为false）
   * @param statusBar 状态栏的背景色需要设置和status一致(可以为空，为空时默认整个布局的背景色)
   * @param drawerLayout 是否使用了drawerLayout布局，可以为空，即非drawerLayout布局
   */
  @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH) private static void setRootView(Activity activity, boolean diff, ViewGroup statusBar, DrawerLayout drawerLayout) {
    ViewGroup rootView = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
    if (null != statusBar) {
      statusBar.setFitsSystemWindows(true);
      statusBar.setClipToPadding(true);
    } else {
      if (diff) {
        // 非图片背景
        rootView.setFitsSystemWindows(true);
        rootView.setClipToPadding(true);
      } else {
        // 图片背景
        rootView.setFitsSystemWindows(false);
        rootView.setClipToPadding(false);
      }
    }
    if (drawerLayout != null) {
      int h = getStatusBarHeight(activity);
      if (null != drawerLayout.getChildAt(1)){
        drawerLayout.getChildAt(1).setPadding(0, h, 0, 0);
      }
      // 此处不通用，针对116114首页添加以下代码huo
      if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT || "Xiaomi".equalsIgnoreCase(Build.BRAND) || "MeiZu".equalsIgnoreCase(Build.BRAND)) {
        drawerLayout.getChildAt(0).setPadding(0, h, 0, 0);
      }
    }
  }

  /**
   * 添加半透明矩形条
   *
   * @param activity 需要设置沉浸的Activty(不能为空)
   * @param statusBarAlpha 透明值
   */
  private static void addTranslucentView(Activity activity, int statusBarAlpha) {
    ViewGroup contentView = (ViewGroup) activity.findViewById(android.R.id.content);
    // 移除半透明矩形,以免叠加
    if (contentView.getChildCount() > 1) {
      contentView.removeViewAt(1);
    }
    contentView.addView(createTranslucentStatusBarView(activity, statusBarAlpha));
  }

  /**
   * 创建半透明矩形 View
   *
   * @param activity 需要设置沉浸的Activty(不能为空)
   * @param alpha 透明值
   * @return View
   */
  private static View createTranslucentStatusBarView(Activity activity, int alpha) {
    // 绘制一个和状态栏一样高的矩形
    View statusBarView = new View(activity);
    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(activity));
    statusBarView.setLayoutParams(params);
    statusBarView.setBackgroundColor(Color.argb(alpha, 0, 0, 0));
    return statusBarView;
  }

  /**
   * 获取状态栏高度
   *
   * @param context context
   * @return 状态栏高度
   */
  private static int getStatusBarHeight(Context context) {
    // 获得状态栏高度
    int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
    return context.getResources().getDimensionPixelSize(resourceId);
  }

  /**
   * 计算状态栏颜色
   *
   * @param color color值
   * @param alpha alpha值
   * @return 最终的状态栏颜色
   */
  private static int calculateStatusColor(int color, int alpha) {
    float a = 1 - alpha / 255f;
    int red = color >> 16 & 0xff;
    int green = color >> 8 & 0xff;
    int blue = color & 0xff;
    red = (int) (red * a + 0.5);
    green = (int) (green * a + 0.5);
    blue = (int) (blue * a + 0.5);
    return 0xff << 24 | red << 16 | green << 8 | blue;
  }

  /**
   * 生成一个和状态栏大小相同的半透明矩形条
   *
   * @param activity 需要设置的activity
   * @param color 状态栏颜色值
   * @param alpha 透明值
   * @return 状态栏矩形条
   */
  private static View createStatusBarView(Activity activity, int color, int alpha) {
    // 绘制一个和状态栏一样高的矩形
    View statusBarView = new View(activity);
    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(activity));
    statusBarView.setLayoutParams(params);
    statusBarView.setBackgroundColor(calculateStatusColor(color, alpha));
    return statusBarView;
  }

  /**
   * 设置状态栏图标为深色和魅族特定的文字风格 可以用来判断是否为Flyme用户
   *
   * @param activity 需要设置的窗口
   * @param dark 是否把状态栏字体及图标颜色设置为深色
   * @return boolean 成功执行返回true
   */
  private static boolean FlymeSetStatusBar(Activity activity, boolean dark, DrawerLayout drawerLayout) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      setTranslucentStatus(activity, true);
    }
    setRootView(activity, true, null, drawerLayout);
    setSystemUi(activity);
    boolean result = false;
    Window window = activity.getWindow();
    if (window != null) {
      try {
        WindowManager.LayoutParams lp = window.getAttributes();
        Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
        Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
        darkFlag.setAccessible(true);
        meizuFlags.setAccessible(true);
        int bit = darkFlag.getInt(null);
        int value = meizuFlags.getInt(lp);
        if (dark) {
          value |= bit;
        } else {
          value &= ~bit;
        }
        meizuFlags.setInt(lp, value);
        window.setAttributes(lp);
        result = true;
      } catch (Exception e) {
        Log.e("MeiZu", "setStatusBarDarkIcon: failed");
      }
    }
    return result;
  }

  /**
   * 设置状态栏字体图标为深色，需要MIUIV6以上
   *
   * @param activity 需要设置的窗口
   * @param darkmode 是否把状态栏字体及图标颜色设置为深色
   * @return boolean 成功执行返回true
   */
  private static boolean MIUISetStatusBar(Activity activity, boolean darkmode) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      setTranslucentStatus(activity, true);
    }
    setRootView(activity, true, null, null);
    boolean result = false;
    Class<? extends Window> clazz = activity.getWindow().getClass();
    try {
      int darkModeFlag = 0;
      Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
      Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
      darkModeFlag = field.getInt(layoutParams);
      Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
      extraFlagField.invoke(activity.getWindow(), darkmode ? darkModeFlag : 0, darkModeFlag);
      result = true;
    } catch (Exception e) {
      e.printStackTrace();
      Log.e("MIUI", "setStatusBarDarkIcon: failed");
    }
    return result;
  }

  @TargetApi(19) private static void setTranslucentStatus(Activity activity, boolean on) {
    Window win = activity.getWindow();
    WindowManager.LayoutParams winParams = win.getAttributes();
    final int bits = TRANSLUCENT_STATUS;
    if (on) {
      winParams.flags |= bits;
    } else {
      winParams.flags &= ~bits;
    }
    win.setAttributes(winParams);
  }
}
