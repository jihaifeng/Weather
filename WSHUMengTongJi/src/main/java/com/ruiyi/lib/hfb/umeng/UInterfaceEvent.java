package com.ruiyi.lib.hfb.umeng;

import android.content.Context;

// 话费宝|116116|流量掌厅 统计接口
public interface UInterfaceEvent {

	// 进入页面
	public void onResume(Context arg0);

	// 离开页面
	public void onPause(Context arg0);

	// 页面跳转统计(百度)
	public void onPageStart(Context cxt, String page);

	// 页面跳转统计(百度)
	public void onPageEnd(Context cxt, String page);

	// 页面跳转统计(友盟)
	public void onPageStart(String page);

	// 页面跳转统计(友盟)
	public void onPageEnd(String page);

	// 事件统计
	public void onEvent(Context arg0, String arg1);

	// 事件统计 arg2具体事件统计
	public void onEvent(Context arg0, String arg1, String arg2);
}
