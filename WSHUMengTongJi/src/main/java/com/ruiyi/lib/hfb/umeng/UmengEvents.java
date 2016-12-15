package com.ruiyi.lib.hfb.umeng;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.TextUtils;
import com.baidu.mobstat.StatService;
import com.umeng.analytics.MobclickAgent;

//   1 话费宝（默认）   2 无线城市+流量掌厅       3 无线城市      4 流量掌厅     5 微生活  6.爱游戏   7. 4G流量包
public class UmengEvents {

  // 事件点击
  public static final String HFB_EVENT = "HFB_EVENT";

  // 模块点击
  public static final String HFB_MODULES = "HFB_MODULES";

  // 广告点击 event_id 300 - 500
  private static final String HFB_ADS = "HFB_ADS";

  public static final String HFB_API = "HFB_API";
  public static String CHANNEL = null;

  public static void setChannelId(Context context, String channel) {
    if (channel != null) {
      CHANNEL = channel;
      MobclickAgent.UMAnalyticsConfig config = new MobclickAgent.UMAnalyticsConfig(context, UmengConfig.UMENG_APPKEY, channel);
      MobclickAgent.startWithConfigure(config);
      //MobclickAgent.setDebugMode(true);
      //AnalyticsConfig.setChannel(channel);
    }
  }

  public static void setChannel(Context context, String androidManifestChannel) {
    if (CHANNEL != null && CHANNEL.trim().length() > 1) {// 存在该chanel
      // id不需要重新

      return;
    }

    Bundle metaData = null;
    String channel = null;
    try {
      ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
      if (null != ai) {
        metaData = ai.metaData;
      }
      if (null != metaData) {
        Object object = metaData.get(androidManifestChannel);
        if (object != null) {
          channel = object.toString();
        }
      }
    } catch (NameNotFoundException e) {

      e.printStackTrace();
    }
    setChannelId(context, channel);
  }

  /**
   * 在HfbApplication 初始化之后调用 话费宝统计接口实现， 116114 | 流量掌厅 | 话费宝 统计时实现该接口
   */
  public static void init(UInterfaceEvent iEvent) {
    // event = iEvent;
  }

  /**
   * activity 统计通用事件专用 onResume
   *
   * @param cxt 设备上下文
   */
  public static void onResume(Context cxt) {

    MobclickAgent.onResume(cxt);

    StatService.onResume(cxt);
  }

  /**
   * activity 统计通用事件专用 onPause
   *
   * @param cxt 设备上下文
   */
  public static void onPause(Context cxt) {

    MobclickAgent.onPause(cxt);

    StatService.onPause(cxt);
  }

  /**
   * fragment统计通用事件专用onResume
   *
   * @param cxt 设备上下文
   * @param page 类的名字
   */
  public static void onPageStart(Context cxt, String page) {

    MobclickAgent.onPageStart(page);

    StatService.onPageStart(cxt, page);
  }

  /**
   * fragment统计通用事件专用 onPause
   *
   * @param cxt 设备上下文
   * @param page 类的名字
   */
  public static void onPageEnd(Context cxt, String page) {

    MobclickAgent.onPageEnd(page);

    StatService.onPageEnd(cxt, page);
  }

  /**
   * 通用事件,标准版本
   *
   * @param cxt 设备上下文
   * @param event_id 业务端注册的事件 id
   * @param label event id 下的各种事件添加的标签(拼接时使用冒号：拼接)
   */
  public static void onEvent(Context cxt, String event_id, String label) {

    com.baidu.mobstat.StatService.onEvent(cxt, event_id, label, 1);

    com.umeng.analytics.MobclickAgent.onEvent(cxt, event_id, label);
  }

  /**
   * 事件统计 扩展版本
   *
   * @param context 设备上下文
   * @param event_id 业务端注册的事件 id
   * @param labels event id 下的各种事件添加的标签(拼接时使用冒号：拼接)
   */
  public static void onEvent(Context context, String event_id, String... labels) {

    if (TextUtils.isEmpty(event_id)) {

      return;
    }

    StringBuilder labelBuilder = new StringBuilder();

    if (labels != null) {

      for (String str : labels) {

        if (labelBuilder.length() > 0) {

          labelBuilder.append(":");
        }

        labelBuilder.append(str);
      }
    }

    if (labelBuilder.length() == 0) {

      labelBuilder.append(event_id);
    }

    com.baidu.mobstat.StatService.onEvent(context, event_id, labelBuilder.toString(), 1);

    com.umeng.analytics.MobclickAgent.onEvent(context, event_id, labelBuilder.toString());
  }

  /**
   * @param event_id 0. 每日签到 1. 上传用户图像 2. 我的收藏 3. 我的收益 4. 积分排行 5. 积分兑换 6. 应用更新 7.
   * 消息 8. 设置 9. 下载管理 10. 搜索输入框点击 11. 首页8模块点击 12. 添加&取消收藏 13. 邀请好友
   * 14. 兑换（功能） 15. 兑换成功
   *
   * ads 301. 首页应用列表流广告点击 302. 首页banner广告点击
   */
  public static void onHFBEvent(Context cxt, int event_id) {

    String[] events = getEvent(event_id);

    onHFBEvent(cxt, events);
  }

  public static void onEvent(Context cxt, int event_id, int position) {

    String[] events = getEvent(event_id);

    events[1] = events[1] + "_" + position;

    onHFBEvent(cxt, events);
  }

  public static void onHFBEvent(Context cxt, String[] events) {

    if (events[1] != null) {

      com.baidu.mobstat.StatService.onEvent(cxt, events[0], events[1], 1);

      com.umeng.analytics.MobclickAgent.onEvent(cxt, events[0], events[1]);

      return;
    }

    com.baidu.mobstat.StatService.onEvent(cxt, "HFB_COMMON", events[0], 1);

    com.umeng.analytics.MobclickAgent.onEvent(cxt, "HFB_COMMON", events[0]);
  }

  /**
   * @return String[] 统计id(不为空) & action(有可能为空) 0. 每日签到 1. 上传用户图像 2. 我的收藏 3.
   * 我的收益 4. 积分排行 5. 积分兑换 6. 应用更新 7. 消息 8. 设置 9. 下载管理 10.搜索输入框点击 11.
   * 首页8模块点击 12. 添加&取消收藏 13. 邀请好友 14. 兑换（功能） 15. 兑换成功
   *
   * ads 301. 首页应用列表流广告点击 302. 首页banner广告点击
   */
  private static String[] getEvent(int key) {
    String[] event = new String[2];
    if (key <= 300) {
      if (key == 0) {
        event[0] = HFB_EVENT;
        event[1] = "HFB_CHECKIN";
      } else if (key == 1) {
        event[0] = HFB_EVENT;
        event[1] = "HFB_UPLOAD_HEADICON";
      } else if (key == 2) {
        event[0] = HFB_MODULES;
        event[1] = "HFB_MYFAVORITE";
      } else if (key == 3) {
        event[0] = HFB_MODULES;
        event[1] = "HFB_MYINCOME";
      } else if (key == 4) {
        event[0] = HFB_MODULES;
        event[1] = "HFB_RANK";
      } else if (key == 5) {
        event[0] = HFB_MODULES;
        event[1] = "HFB_EXCHANGE";
      } else if (key == 6) {
        event[0] = HFB_MODULES;
        event[1] = "HFB_UPDATE";
      } else if (key == 7) {
        event[0] = HFB_MODULES;
        event[1] = "HFB_MESSAGE";
      } else if (key == 8) {
        event[0] = HFB_MODULES;
        event[1] = "HFB_SETTINGS";
      } else if (key == 9) {
        event[0] = HFB_MODULES;
        event[1] = "HFB_DOWNMGR";
      } else if (key == 10) {
        event[0] = HFB_EVENT;
        event[1] = "HFB_SEARCH_EDT";
      } else if (key == 11) {
        event[0] = HFB_MODULES;
        event[1] = "HFB_INDEX_MODULES";
      } else if (key == 12) {
        event[0] = HFB_EVENT;
        event[1] = "HFB_FAVORITY_EVENT";
      } else if (key == 13) {
        event[0] = HFB_EVENT;
        event[1] = "HFB_INVITE_FRIEND";
      } else if (key == 14) {
        event[0] = HFB_EVENT;
        event[1] = "HFB_ACTION_EXCHANGE";
      } else if (key == 15) {
        event[0] = HFB_EVENT;
        event[1] = "HFB_ACTION_EXCHANGE_SUCCESS";
      } else {
        event[0] = HFB_MODULES;
        event[1] = "HFB_NEW_EVENT";
      }
    } else {
      event[0] = HFB_ADS;
      if (key == 301) {
        event[1] = "HFB_ADS_INDEX_ADAPTER";
      } else if (key == 302) {
        event[1] = "HFB_ADS_INDEX";
      } else {
        event[1] = "HFB_ADS_NEW";
      }
    }
    return event;
  }
}
