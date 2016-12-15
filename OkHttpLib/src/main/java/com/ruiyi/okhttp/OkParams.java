package com.ruiyi.okhttp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.util.Log;

public class OkParams implements Serializable {
	public String key;
	public String value;
	private static final String TAG = OkParams.class.getSimpleName().trim();
	private Map<String, String> okParamsMap;
	private List<OkParams> okParamsList = new ArrayList<OkParams>();

	public OkParams() {
		if (okParamsMap == null) {
			okParamsMap = new HashMap<String, String>();
		}
	}

	public void add(String key, String value) {
		okParamsMap.put(key, value);
		Log.i(TAG, "ok = " + okParamsMap);
		okParamsList.add(new OkParams(key, value));
		Log.i(TAG, "params = " + okParamsList + "  " + okParamsMap);

	}

	@Override
	public String toString() {
		return getParamsFormMap();
	}

	public String getParamsFormMap() {
		String params = null;
		if (okParamsMap != null && okParamsMap.size() != 0) {

			// 获得这个集合的迭代器，保存在iter里
			Iterator iter = okParamsMap.entrySet().iterator();
			int count = 0;
			// HashMap<String, Object> gtimeLength = new HashMap<String,
			// Object>();
			while (iter.hasNext()) {
				count++;
				Entry entry = (Entry) iter.next();
				Object key = entry.getKey();
				Object value = entry.getValue();
				// 能获得map中的每一个键值对了
				if (count == 1) {
					params = (key != null ? key.toString() : "") + "="
							+ (value != null ? value.toString() : "") + "&";
				} else if (!iter.hasNext()) {
					params += (key != null ? key.toString() : "") + "="
							+ (value != null ? value.toString() : "");
				} else {
					params += (key != null ? key.toString() : "") + "="
							+ (value != null ? value.toString() : "") + "&";
				}
				Log.i(TAG, key + "=" + value);
			}
		}
		return params;
	}

	public boolean has(String key) {
		if (okParamsMap.get(key) != null) {
			return true;
		} else {
			return false;
		}

	}

	public OkParams(String key, String value) {
		this.key = key;
		this.value = value;
		Log.i(TAG, "key = " + key + "  " + value);
	}

	public Map<String, String> getOkParamsMap() {
		if (okParamsMap == null) {
			return null;
		}
		return okParamsMap;
	}

	public List<OkParams> getOkParamsList() {
		if (okParamsList != null) {

			return okParamsList;
		} else {
			return null;
		}

	}
}
