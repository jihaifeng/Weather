package com.raiyi.wsh_lib_bdlocation.log;


public class ErrorConfig {
	private static ErrorConfig Instance;

	public static ErrorConfig getInstance() {
		synchronized (ErrorConfig.class) {
			if (Instance == null) {
				Instance = new ErrorConfig();
			}
			return Instance;
		}
	}
	/*
	61 ： GPS定位结果，GPS定位成功。
	62 ： 无法获取有效定位依据，定位失败，请检查运营商网络或者wifi网络是否正常开启，尝试重新请求定位。
	63 ： 网络异常，没有成功向服务器发起请求，请确认当前测试手机网络是否通畅，尝试重新请求定位。
	65 ： 定位缓存的结果。
	66 ： 离线定位结果。通过requestOfflineLocaiton调用时对应的返回结果。
	67 ： 离线定位失败。通过requestOfflineLocaiton调用时对应的返回结果。
	68 ：网络连接失败 时，查找本地离线定位时对应的返回结果。
	161： 网络定位结果，网络定位定位成功。
	162： 请求串密文解析失败，一般是由于客户端SO文件加载失败造成，请严格参照开发指南或demo开发，放入对应SO文件。
	167： 服务端定位失败，请您检查是否禁用获取位置信息权限，尝试重新请求定位。
	502： key参数错误，请按照说明文档重新申请KEY。
	505： key不存在或者非法，请按照说明文档重新申请KEY。
	601： key服务被开发者自己禁用，请按照说明文档重新申请KEY。
	602： key mcode不匹配，您的ak配置过程中安全码设置有问题，请确保：sha1正确，“;”分号是英文状态；且包名是您当前运行应用的包名，请按照说明文档重新申请KEY。
	501～700：key验证失败，请按照说明文档重新申请KEY。
	*/
	public String getErrorMsg(int errorCode) {
		String errorMsg = null;

		if (errorCode == 61) {
			errorMsg = "GPS定位结果，GPS定位成功";
		} else if (errorCode == 62) {
			errorMsg = "无法获取有效定位依据，定位失败，请检查运营商网络或者wifi网络是否正常开启，尝试重新请求定位";
		} else if (errorCode == 63) {
			errorMsg = "无法获取有效定位依据，定位失败，请检查运营商网络或者wifi网络是否正常开启，尝试重新请求定位";
		} else if (errorCode == 65) {
			errorMsg = "定位缓存的结果";
		} else if (errorCode == 66) {
			errorMsg = "离线定位结果。通过requestOfflineLocaiton调用时对应的返回结果";
		} else if (errorCode == 67) {
			errorMsg = "离线定位失败。通过requestOfflineLocaiton调用时对应的返回结果";
		} else if (errorCode == 68) {
			errorMsg = "网络连接失败 时，查找本地离线定位时对应的返回结果";
		} else if (errorCode == 161) {
			errorMsg = "网络定位结果，网络定位定位成功";
		} else if (errorCode == 162) {
			errorMsg = "请求串密文解析失败，一般是由于客户端SO文件加载失败造成，请严格参照开发指南或demo开发，放入对应SO文件";
		} else if (errorCode == 167) {
			errorMsg = "服务端定位失败，请您检查是否禁用获取位置信息权限，尝试重新请求定位";
		} else if (errorCode == 502) {
			errorMsg = "key参数错误，请按照说明文档重新申请KEY";
		} else if (errorCode == 505) {
			errorMsg = "key不存在或者非法，请按照说明文档重新申请KEY";
		} else if (errorCode == 601) {
			errorMsg = " key服务被开发者自己禁用，请按照说明文档重新申请KEY";
		} else if (errorCode == 602) {
			errorMsg = "key mcode不匹配，您的ak配置过程中安全码设置有问题，请确保：sha1正确，“;”分号是英文状态；且包名是您当前运行应用的包名，请按照说明文档重新申请KEY";
		} else if (errorCode <= 700 && errorCode >= 501) {
			errorMsg = "key验证失败，请按照说明文档重新申请KEY";
		} else {
			errorMsg = "请对比百度官方Api";
		}
		return "errorCode = " + errorCode +">>>>>>"+"errorMsg = " + errorMsg;
	}
}
