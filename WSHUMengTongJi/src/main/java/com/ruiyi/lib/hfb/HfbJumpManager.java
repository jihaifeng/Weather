package com.ruiyi.lib.hfb;

import android.content.Context;
import android.content.Intent;

import com.ruiyi.lib.hfb.bean.AdvertisementBase;
import com.ruiyi.lib.hfb.bean.HfbJump116114Inter;

public class HfbJumpManager {

	private HfbJump116114Inter mHfbJump116114Inter;

	private static HfbJumpManager mInstance;

	public static HfbJumpManager getInstance() {

		if (mInstance == null) {

			mInstance = new HfbJumpManager();

		}

		return mInstance;

	}

	public void startAdvertise(Context context, AdvertisementBase advertisement) {

		if (mHfbJump116114Inter != null) {

			mHfbJump116114Inter.startAdvertise(context, advertisement);

		}
	}

	public void startWebPage(Context cxt, String title, String url) {
		AdvertisementBase adVertisement = new AdvertisementBase();
		adVertisement.setmKey("wap#common");
		adVertisement.setName(title);
		adVertisement.setUrl(url);
		startAdvertise(cxt, adVertisement);
	}

	public Intent getPushWebIntent(Context cxt, String title, String url) {
		if (mHfbJump116114Inter != null) {

			return mHfbJump116114Inter.getPushWebIntent(cxt, title, url);

		}
		return null;
	}

	public void startAuth(Context cxt) {
		if (mHfbJump116114Inter != null) {
			mHfbJump116114Inter.doAuth(cxt);
		}
	}

	public void doLogin(Context cxt) {
		if (mHfbJump116114Inter != null) {
			mHfbJump116114Inter.doLogin(cxt);
		}
	}

	public void doBind(Context cxt) {
		if (mHfbJump116114Inter != null) {
			mHfbJump116114Inter.doBind(cxt);
		}
	}

	public String getAccessToken() {
		if (mHfbJump116114Inter != null) {
			String at = mHfbJump116114Inter.getAccessToken();
			return at;
		}
		return "";
	}

	public void setHfbJump116114Inter(HfbJump116114Inter mHfbJump116114Inter) {
		this.mHfbJump116114Inter = mHfbJump116114Inter;
	}

	public HfbJump116114Inter getHfbJump116114Inter() {
		return mHfbJump116114Inter;
	}
}
