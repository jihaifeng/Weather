package com.ruiyi.okhttp;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.okhttp.Response;

public class OkHttputils {
	private static final String TAG = OkHttputils.class.getSimpleName().trim();
	private Context mContext;
	private static OkHttputils instance;
	private static OkHttpManager manager;

	public OkHttputils() {
		manager = OkHttpManager.getInstance();
	}

	public static OkHttputils getInstance() {
		if (instance == null) {
			instance = new OkHttputils();
		}
		return instance;
	}

	// *************对外公布的方法************
	
	
	/**
	 * 同步的Get请求
	 * 
	 * @param url
	 * @return Response
	 */
	public Response getAsyn(String url) throws IOException {
		return manager.getAsyn(url);
	}

	 /**
     * 同步的Get请求
     *
     * @param url
     * @return 字符串
     */
	public String getAsString(String url) throws IOException {
		return manager.getAsString(url);
	}
	 /**
     * 异步的get请求
     *
     * @param url
     * @param callback
     */
	public void getAsyn(String url, RequestCallback callback) {
		manager.getAsyn(url, callback);
	}
	
	/**
     * 同步的Post请求
     *
     * @param url
     * @param params post的参数
     * @return
     */
	public Response post(String url, OkParams... params) throws IOException {
		return manager.post(url, params);
	}
	 /**
     * 同步的Post请求
     *
     * @param url
     * @param params post的参数
     * @return 字符串
     */
	public String postAsString(String url, OkParams... params) throws IOException {
		return manager.postAsString(url, params);
	}
	 /**
     * 异步的post请求
     *
     * @param url
     * @param callback
     * @param params
     */
	public void postAsyn(String url, final RequestCallback callback,
			OkParams... params) {
		manager.postAsyn(url, callback, params);
	}
	  /**
     * 异步的post请求
     *
     * @param url
     * @param callback
     * @param params
     */
	public void postAsyn(String url, final RequestCallback callback,
			Map<String, String> params) {
		manager.postAsyn(url, callback, params);
	}
	   /**
     * 同步基于post的文件上传
     *
     * @param params
     * @return
     */
	public Response upload(String url, File[] files, String[] fileKeys,
			OkParams... params) throws IOException {
		return manager.upload(url, files, fileKeys, params);
	}

	public Response upload(String url, File file, String fileKey)
			throws IOException {
		return manager.upload(url, file, fileKey);
	}

	public Response upload(String url, File file, String fileKey, OkParams... params)
			throws IOException {
		return manager.upload(url, file, fileKey, params);
	}
	  /**
     * 异步基于post的文件上传
     *
     * @param url
     * @param callback
     * @param files
     * @param fileKeys
     * @throws IOException
     */
	public void uploadAsyn(String url, RequestCallback callback, File[] files,
			String[] fileKeys, OkParams... params) throws IOException {
		manager.uploadAsyn(url, callback, files, fileKeys, params);
	}
	 /**
     * 异步基于post的文件上传，单文件不带参数上传
     *
     * @param url
     * @param callback
     * @param file
     * @param fileKey
     * @throws IOException
     */
	public void uploadAsyn(String url, RequestCallback callback, File file,
			String fileKey) throws IOException {
		manager.uploadAsyn(url, callback, file, fileKey);
	}
	 /**
     * 异步基于post的文件上传，单文件且携带其他form参数上传
     *
     * @param url
     * @param callback
     * @param file
     * @param fileKey
     * @param params
     * @throws IOException
     */
	public void uploadAsyn(String url, RequestCallback callback, File file,
			String fileKey, OkParams... params) throws IOException {
		manager.uploadAsyn(url, callback, file, fileKey, params);
	}
	  /**
     * 加载图片
     *
     * @param view
     * @param url
     * @throws IOException
     */
	public void displayImage(final ImageView view, String url, int errorResId)
			throws IOException {
		manager.displayImage(view, url, errorResId);
	}

	public void displayImage(final ImageView view, String url) {
		manager.displayImage(view, url, -1);
	}
	 /**
     * 异步下载文件
     *
     * @param url
     * @param destFileDir 本地文件存储的文件夹
     * @param callback
     */
	public void downloadAsyn(String url, String destDir, RequestCallback callback) {
		manager.downloadAsyn(url, destDir, callback);
	}

}
