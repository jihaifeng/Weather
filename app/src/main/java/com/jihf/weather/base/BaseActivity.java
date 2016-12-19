package com.jihf.weather.base;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.baidu.mobstat.StatService;
import com.jihf.weather.config.Config;
import com.jihf.weather.utils.AppUtils;
import com.jihf.weather.utils.ScreenUtil;
import com.ruiyi.lib.hfb.umeng.UmengEvents;

/**
 * Func：
 * User：jihf
 * Data：2016-12-15
 * Time: 15:39
 * Mail：jihaifeng@raiyi.com
 */
public class BaseActivity extends AppCompatActivity {
  private static String TAG = BaseActivity.class.getSimpleName().trim();
  private Context mBaseContext;
  private Context mCurrentContext;
  private SharedPreferences sharedPreferences;
  public static final String APP_SP = "APP_SP";
  private ProgressDialog progressDialog;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mBaseContext = this;
    UmengEvents.setChannel(this, Config.TONGJI_CHANNEL);
    StatService.setDebugOn(true);
    ScreenUtil.getInstance(this);
    setActivityStatus(this);
  }


  private void setActivityStatus(Activity activity) {
    //设置当前栈顶Activity
    mCurrentContext = activity;
    //设置Activity强制竖屏显示
    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    // 向Acitivty栈（数据类型为List）中添加Activity
    AppUtils.getInstance().addActivity(activity);
    //设置TAG
    TAG = activity.getClass().getSimpleName().trim();
  }

  public Context getCurrentActivity() {
    if (null == mCurrentContext) {
      mCurrentContext = this;
    }
    return mCurrentContext;
  }

  @Override protected void onStart() {
    super.onStart();
    Log.i(TAG, "-------onStart------");
  }

  @Override protected void onPause() {
    super.onPause();
    Log.i(TAG, "-------onPause------");
  }

  @Override protected void onResume() {
    super.onResume();
    Log.i(TAG, "-------onResume------");
  }

  @Override protected void onStop() {
    super.onStop();
    Log.i(TAG, "-------onStop------");
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    // 在Activity栈中移除Activity
    AppUtils.getInstance().removeActivity(this);
    Log.i(TAG, "-------onDestroy------");
  }

  @Override protected void onRestart() {
    super.onRestart();
    Log.i(TAG, "-------onRestart------");
  }

  public void JumpTo(Context from, Class to) {
    Intent intent = new Intent();
    intent.setClass(from, to);
    startActivity(intent);
    UmengEvents.onEvent(from, "activity_jump", "Jump");
  }

  public void JumpToWithBundle(Context from, Class to, Bundle bundle) {
    Intent intent = new Intent();
    intent.putExtras(bundle);
    intent.setClass(from, to);
    startActivity(intent);
    UmengEvents.onEvent(from, "activity_jump", "Jump_with_bundle");
  }

  public void setSharedPreferences(String key, String val) {
    if (null == sharedPreferences) {
      sharedPreferences = getSharedPreferences(APP_SP, Application.MODE_PRIVATE);
    }
    sharedPreferences.edit().putString(key, val).commit();
  }

  public String getSharedPreferences(String key) {
    if (null == sharedPreferences) {
      sharedPreferences = getSharedPreferences(APP_SP, Application.MODE_PRIVATE);
    }
    return sharedPreferences.getString(key, null);
  }

  /**
   * 显示进度对话框
   */
  private void showProgressDialog() {
    if (progressDialog == null) {
      progressDialog = new ProgressDialog(this);
      progressDialog.setMessage("正在加载...");
      progressDialog.setCanceledOnTouchOutside(false);
    }
    progressDialog.show();
  }

  /**
   * 关闭进度对话框
   */
  private void closeProgressDialog() {
    if (progressDialog != null) {
      progressDialog.dismiss();
    }
  }
}
