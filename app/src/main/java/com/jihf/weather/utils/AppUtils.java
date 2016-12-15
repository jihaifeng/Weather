package com.jihf.weather.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Process;
import java.util.Stack;

/**
 * Func：
 * User：jihf
 * Data：2016-12-15
 * Time: 15:39
 * Mail：jihaifeng@raiyi.com
 */
public class AppUtils {
  private static AppUtils instance;
  //Activity栈
  private Stack<Activity> activityStack = new Stack<>();

  public synchronized static AppUtils getInstance() {
    if (null == instance) {
      instance = new AppUtils();
    }
    return instance;
  }

  /**
   * 添加activity,入栈
   */
  public void addActivity(Activity activity) {
    if (null != activity) {
      activityStack.add(activity);
    }
  }

  /**
   * 移除activity，出栈
   */
  public void removeActivity(Activity activity) {
    if (null != activity && activityStack.contains(activity)) {
      activityStack.remove(activity);
    }
  }

  /**
   * 获取当前Activity（栈顶Activity）
   */
  public Activity getCurrentActivity() {
    return activityStack.lastElement();
  }

  /**
   * 结束当前activity,栈顶activity
   */
  public void finishCurrentActivity() {
    Activity activity = activityStack.lastElement();
    finishActivity(activity);
  }

  /**
   * 结束指定的Activity
   */
  public void finishActivity(Activity activity) {
    if (null != activity) {
      activityStack.remove(activity);
      activity.finish();
      activity = null;
    }
  }

  /**
   * 结束指定类名的Activity
   */
  public void finishActivityByName(Class<?> cls) {
    for (Activity activity : activityStack) {
      if (activity.getClass().equals(cls)) {
        finishActivity(activity);
      }
    }
  }

  /**
   * 结束所有Activity
   */
  public void finishAllActivity() {
    for (Activity activity : activityStack) {
      finishActivity(activity);
    }
    activityStack.clear();
  }

  public void AppExit(Context context) {
    try {
      finishAllActivity();
      ActivityManager activityMgr = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
      activityMgr.killBackgroundProcesses(context.getPackageName());
      System.exit(0);
      Process.killProcess(Process.myPid());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
