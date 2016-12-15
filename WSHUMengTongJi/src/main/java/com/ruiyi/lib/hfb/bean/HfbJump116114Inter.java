package com.ruiyi.lib.hfb.bean;

import android.content.Context;
import android.content.Intent;

public interface HfbJump116114Inter {

	// 启动116114相关的的模块
	public void startAdvertise(Context context, AdvertisementBase advertisement);

	/**
	 * 话费宝兑换绑定
	 * 
	 * @param cxt
	 */
	public void doBind(Context cxt);

	/**
	 * accessToken 验证失败后 跳转到116114登录页方法
	 * 
	 * @param cxt
	 */
	public void doLogin(Context cxt);

	/**
	 * 豆豆兑换 实名认证
	 * 
	 * @param cxt
	 */
	public void doAuth(Context cxt);

	public Intent getPushWebIntent(Context cxt, String title, String url);

	public String getAccessToken();

	/**
	 * 跳转至下载首页
	 * 
	 * @param cxt
	 */
	// public void jumpHfbMain(Context cxt);

}
