package com.ruiyi.okhttp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;
import com.google.gson.Gson;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author Lucky
 */
@SuppressWarnings("rawtypes") public class OkHttpManager {
  private static OkHttpManager mInstance;
  private OkHttpClient mOkHttpClient;
  private Handler mDelivery;
  private Gson mGson;

  public static final String TAG = OkHttpManager.class.getSimpleName();

  private OkHttpManager() {
    mOkHttpClient = new OkHttpClient();
    // cookie enabled
    mOkHttpClient.getDispatcher().setMaxRequests(3);
    mOkHttpClient.setCookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER));
    mDelivery = new Handler(Looper.getMainLooper());
    mGson = new Gson();
  }

  public void execute(Runnable runnable) {
    if (runnable != null) {
      mOkHttpClient.getDispatcher().getExecutorService().execute(runnable);
    }
  }

  public synchronized static OkHttpManager getInstance() {
    if (mInstance == null) {
      synchronized (OkHttpManager.class) {
        if (mInstance == null) {
          mInstance = new OkHttpManager();
        }
      }
    }
    return mInstance;
  }

  /**
   * 同步的Get请求
   *
   * @return Response
   */
  public Response getAsyn(String url) throws IOException {
    final Request request = new Request.Builder().url(url).build();
    Call call = mOkHttpClient.newCall(request);
    Response execute = call.execute();
    return execute;
  }

  /**
   * 同步的Get请求
   *
   * @return 字符串
   */
  public String getAsString(String url) throws IOException {
    Response execute = getAsyn(url);
    return execute.body().string();
  }

  /**
   * 异步的get请求
   */
  public void getAsyn(String url, OkParams params, final RequestCallback callback) {
    final Request request;
    if (null != params) {
      request = new Request.Builder().url(url + "?" + params.getParamsFormMap()).build();
    } else {
      request = new Request.Builder().url(url).build();
    }
    deliveryResult(callback, request);
  }

  /**
   * 异步的get请求
   */
  public void getAsyn(String url, final RequestCallback callback) {
    final Request request = new Request.Builder().url(url).build();
    deliveryResult(callback, request);
  }

  /**
   * 同步的Post请求
   *
   * @param params post的参数
   */
  public Response post(String url, OkParams... params) throws IOException {
    Request request = buildPostRequest(url, params);
    Response response = mOkHttpClient.newCall(request).execute();
    return response;
  }

  /**
   * 同步的Post请求
   *
   * @param params post的参数
   * @return 字符串
   */
  public String postAsString(String url, OkParams... params) throws IOException {
    Response response = post(url, params);
    return response.body().string();
  }

  /**
   * 异步的post请求
   */
  public void postAsyn(String url, final RequestCallback callback, OkParams... params) {
    Log.i(TAG, "postAsyn = " + url + "    " + params + "    " + callback);
    Request request = buildPostRequest(url, params);
    deliveryResult(callback, request);
  }

  /**
   * 异步的post请求
   */
  public void postAsyn(String url, final RequestCallback callback, Map<String, String> params) {
    Log.i(TAG, "postAsyn = " + url + "    " + params + "    " + callback);
    OkParams[] paramsArr = map2Params(params);
    Request request = buildPostRequest(url, paramsArr);
    deliveryResult(callback, request);
  }

  /**
   * 同步基于post的文件上传
   */
  public Response upload(String url, File[] files, String[] fileKeys, OkParams... params) throws IOException {
    Request request = buildMultipartFormRequest(url, files, fileKeys, params);
    return mOkHttpClient.newCall(request).execute();
  }

  public Response upload(String url, File file, String fileKey) throws IOException {
    Request request = buildMultipartFormRequest(url, new File[] { file }, new String[] { fileKey }, null);
    return mOkHttpClient.newCall(request).execute();
  }

  public Response upload(String url, File file, String fileKey, OkParams... params) throws IOException {
    Request request = buildMultipartFormRequest(url, new File[] { file }, new String[] { fileKey }, params);
    return mOkHttpClient.newCall(request).execute();
  }

  /**
   * 异步基于post的文件上传
   *
   * @throws IOException
   */
  public void uploadAsyn(String url, RequestCallback callback, File[] files, String[] fileKeys, OkParams... params) throws IOException {
    Request request = buildMultipartFormRequest(url, files, fileKeys, params);
    deliveryResult(callback, request);
  }

  /**
   * 异步基于post的文件上传，单文件不带参数上传
   *
   * @throws IOException
   */
  public void uploadAsyn(String url, RequestCallback callback, File file, String fileKey) throws IOException {
    Request request = buildMultipartFormRequest(url, new File[] { file }, new String[] { fileKey }, null);
    deliveryResult(callback, request);
  }

  /**
   * 异步基于post的文件上传，单文件不带参数上传
   *
   * @throws IOException
   */
  public void uploadAsyn(String url, MultipartBuilder builder, RequestCallback callback) throws IOException {
    RequestBody requestBody = builder.build();
    Request request = new Request.Builder().url(url).post(requestBody).build();
    deliveryResult(callback, request);
  }

  /**
   * 异步基于post的文件上传，单文件且携带其他form参数上传
   *
   * @throws IOException
   */
  public void uploadAsyn(String url, RequestCallback callback, File file, String fileKey, OkParams... params) throws IOException {
    Request request = buildMultipartFormRequest(url, new File[] { file }, new String[] { fileKey }, params);
    deliveryResult(callback, request);
  }

  /**
   * 异步下载文件
   *
   * @param destFileDir 本地文件存储的文件夹
   */
  public void downloadAsyn(final String url, final String destFileDir, final RequestCallback callback) {
    final Request request = new Request.Builder().url(url).build();
    final Call call = mOkHttpClient.newCall(request);
    call.enqueue(new Callback() {
      @Override public void onFailure(final Request request, final IOException e) {
        sendFailedStringCallback(request, e, callback);
      }

      @SuppressWarnings("unchecked") @Override public void onResponse(Response response) {
        InputStream is = null;
        byte[] buf = new byte[2048];
        int len = 0;
        FileOutputStream fos = null;
        try {
          is = response.body().byteStream();
          File file = new File(destFileDir, getFileName(url));
          fos = new FileOutputStream(file);
          while ((len = is.read(buf)) != -1) {
            fos.write(buf, 0, len);
          }
          fos.flush();
          // 如果下载文件成功，第一个参数为文件的绝对路径
          sendSuccessResultCallback(file.getAbsolutePath(), callback);
        } catch (IOException e) {
          sendFailedStringCallback(response.request(), e, callback);
        } finally {
          try {
            if (is != null) {
              is.close();
            }
          } catch (IOException e) {
          }
          try {
            if (fos != null) {
              fos.close();
            }
          } catch (IOException e) {
          }
        }
      }
    });
  }

  public String getFileName(String path) {
    int separatorIndex = path.lastIndexOf("/");
    return (separatorIndex < 0) ? path : path.substring(separatorIndex + 1, path.length());
  }

  /**
   * 加载图片
   *
   * @throws IOException
   */
  public void displayImage(final ImageView view, final String url, final int errorResId) {
    final Request request = new Request.Builder().url(url).build();
    Call call = mOkHttpClient.newCall(request);
    call.enqueue(new Callback() {
      @Override public void onFailure(Request request, IOException e) {
        setErrorResId(view, errorResId);
      }

      @Override public void onResponse(Response response) {
        InputStream is = null;
        try {
          is = response.body().byteStream();
          OkHttpImageUtils.ImageSize actualImageSize = OkHttpImageUtils.getImageSize(is);
          OkHttpImageUtils.ImageSize imageViewSize = OkHttpImageUtils.getImageViewSize(view);
          int inSampleSize = OkHttpImageUtils.calculateInSampleSize(actualImageSize, imageViewSize);
          try {
            is.reset();
          } catch (IOException e) {
            response = getAsyn(url);
            is = response.body().byteStream();
          }

          BitmapFactory.Options ops = new BitmapFactory.Options();
          ops.inJustDecodeBounds = false;
          ops.inSampleSize = inSampleSize;
          final Bitmap bm = BitmapFactory.decodeStream(is, null, ops);
          mDelivery.post(new Runnable() {
            @Override public void run() {
              view.setImageBitmap(bm);
            }
          });
        } catch (Exception e) {
          setErrorResId(view, errorResId);
        } finally {
          if (is != null) {
            try {
              is.close();
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        }
      }
    });
  }

  public void setErrorResId(final ImageView view, final int errorResId) {
    mDelivery.post(new Runnable() {
      @Override public void run() {
        view.setImageResource(errorResId);
      }
    });
  }

  public Request buildMultipartFormRequest(String url, File[] files, String[] fileKeys, OkParams[] params) {
    params = validateParam(params);

    MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM);

    for (OkParams param : params) {
      builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + param.key + "\""), RequestBody.create(null, param.value));
    }
    if (files != null) {
      RequestBody fileBody = null;
      for (int i = 0; i < files.length; i++) {
        File file = files[i];
        String fileName = file.getName();
        fileBody = RequestBody.create(MediaType.parse(guessMimeType(fileName)), file);
        // TODO 根据文件名设置contentType
        builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + fileKeys[i] + "\"; filename=\"" + fileName + "\""), fileBody);
      }
    }

    RequestBody requestBody = builder.build();
    return new Request.Builder().url(url).post(requestBody).build();
  }

  public String guessMimeType(String path) {
    FileNameMap fileNameMap = URLConnection.getFileNameMap();
    String contentTypeFor = fileNameMap.getContentTypeFor(path);
    if (contentTypeFor == null) {
      contentTypeFor = "application/octet-stream";
    }
    return contentTypeFor;
  }

  public OkParams[] validateParam(OkParams[] params) {
    if (params == null) {
      return new OkParams[0];
    } else {
      return params;
    }
  }

  public OkParams[] map2Params(Map<String, String> params) {
    if (params == null) {
      return new OkParams[0];
    }
    int size = params.size();
    OkParams[] res = new OkParams[size];
    Set<Map.Entry<String, String>> entries = params.entrySet();
    int i = 0;
    for (Map.Entry<String, String> entry : entries) {
      res[i++] = new OkParams(entry.getKey(), entry.getValue());
    }
    return res;
  }

  public static final String SESSIONKEY = "Set-Cookie";
  public static final String mSessionKey = "JSESSIONID";

  public Map<String, String> mSessions = new HashMap<String, String>();

  public void deliveryResult(final RequestCallback callback, Request request) {
    mOkHttpClient.newCall(request).enqueue(new Callback() {
      @Override public void onFailure(final Request request, final IOException e) {
        sendFailedStringCallback(request, e, callback);
      }

      @SuppressWarnings("unchecked") @Override public void onResponse(final Response response) {
        try {
          final String string = response.body().string();
          if (callback.mType == String.class) {
            sendSuccessResultCallback(string, callback);
          } else {
            Object o = mGson.fromJson(string, callback.mType);
            sendSuccessResultCallback(o, callback);
          }
        } catch (IOException e) {
          sendFailedStringCallback(response.request(), e, callback);
        } catch (com.google.gson.JsonParseException e)// Json解析的错误
        {
          sendFailedStringCallback(response.request(), e, callback);
        }
      }
    });
  }

  public void sendFailedStringCallback(final Request request, final Exception e, final RequestCallback callback) {
    mDelivery.post(new Runnable() {
      @Override public void run() {
        if (callback != null) {
          callback.onFailure(request, e);
        }
      }
    });
  }

  public void sendSuccessResultCallback(final Object object, final RequestCallback<Object> callback) {
    mDelivery.post(new Runnable() {
      @Override public void run() {
        if (callback != null) {
          callback.onSuccess(object);
        }
      }
    });
  }

  public Request buildPostRequest(String url, OkParams[] params) {
    if (params == null) {
      params = new OkParams[0];
    }
    FormEncodingBuilder builder = new FormEncodingBuilder();
    Log.i(TAG, "params = " + params);
    for (OkParams param : params) {
      Log.i(TAG, param.key + "=" + param.value);
      builder.add(param.key, param.value);
    }
    RequestBody requestBody = builder.build();
    return new Request.Builder().url(url).post(requestBody).build();
  }

  public String getParamsFormMap(Map<String, String> paramsMap) {
    String params = null;
    if (paramsMap != null && paramsMap.size() != 0) {

      // 获得这个集合的迭代器，保存在iter里
      Iterator iter = paramsMap.entrySet().iterator();
      int count = 0;
      //			HashMap<String, Object> gtimeLength = new HashMap<String, Object>();
      while (iter.hasNext()) {
        count++;
        Entry entry = (Entry) iter.next();
        Object key = entry.getKey();
        Object value = entry.getValue();
        // 能获得map中的每一个键值对了
        if (count == 1) {
          params = key.toString() + "=" + value.toString() + "&";
        } else if (!iter.hasNext()) {
          params += key.toString() + "=" + value.toString();
        } else {
          params += key.toString() + "=" + value.toString() + "&";
        }
        Log.i(TAG, key + "=" + value);
      }
    }
    return params;
  }
}