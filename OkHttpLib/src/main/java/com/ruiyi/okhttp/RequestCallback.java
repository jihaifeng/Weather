package com.ruiyi.okhttp;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.google.gson.internal.$Gson$Types;
import com.squareup.okhttp.Request;

public abstract class RequestCallback<T> {
	Type mType;

	public RequestCallback() {
		mType = getSuperclassTypeParameter(getClass());
	}

	static Type getSuperclassTypeParameter(Class<?> subclass) {
		Type superclass = subclass.getGenericSuperclass();
		if (superclass instanceof Class) {
			throw new RuntimeException("Missing type parameter.");
		}
		ParameterizedType parameterized = (ParameterizedType) superclass;
		Type type = parameterized.getActualTypeArguments()[0];
		return $Gson$Types.canonicalize(type);
	}

	public abstract void onFailure(Request request, Exception e);

	public abstract void onSuccess(T response);
}