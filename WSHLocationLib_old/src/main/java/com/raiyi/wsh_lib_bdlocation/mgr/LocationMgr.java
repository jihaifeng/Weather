/*
修正履历

 Bug编号                   修正日期       修正人     修正内容
==============================================
 Bug #8353     20150423  朱香锋      团购页面会引起定位空指针异常，方法onReceiveLocation(BDLocation location)中做了空指针判断
==============================================  
 */
package com.raiyi.wsh_lib_bdlocation.mgr;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.location.Poi;
import com.raiyi.wsh_lib_bdlocation.log.ErrorConfig;
import com.raiyi.wsh_lib_bdlocation.mgr.bean.AddressItem;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

/**
 * @author xjl 定位控制器
 */

public class LocationMgr {
	private static final String TAG = LocationMgr.class.getSimpleName();

    private static Context context;
    private static LocationMgr locMgr;
    private static String type = "all";// 定位位置類型，"all"代表位置所有详情信息
    private LocationClient mLClient;
    private LocationClientOption option;
    private List<AddressListener> addressListeners = new ArrayList<AddressListener>();
    private BDLocationListener listener;

    private LocationMgr(Context mcontext, String addrtype) {
        super();
        context = mcontext.getApplicationContext();// 防止退出再进定位不成功；
        if (addrtype != null) {
            type = addrtype;
        }
    }

    /**
     * @param context
     * @param type
     *            "all"代表返回的定位结果包含地址信息 "detail" 代表的定位是详细信息
     * @return
     */
    public static LocationMgr getInstance(Context context, String type) {
        if (locMgr == null && context != null) {
            locMgr = new LocationMgr(context, type);
        }
//        initLocationClient();
        return locMgr;
    }

    public void locationAddress(AddressListener addListener) {
        this.addressListeners.add(addListener);
        if(mLClient != null)
            return;
        mLClient = new LocationClient(context);
//        mLClient.setForBaiduMap(false);
        registerLocation(new BDLocationListenerImpl());
        if (mLClient != null) {
            initLocation();
            mLClient.setLocOption(option);
            mLClient.start();//定位SDK start之后会默认发起一次定位请求，开发者无须判断isstart并主动调用request
            mLClient.requestLocation();
        }
    }
    
    private void initLocation() {
    	option = new LocationClientOption();
//    	tempMode = LocationMode.Hight_Accuracy;
//        tempMode = LocationMode.Battery_Saving;
//        tempMode = LocationMode.Device_Sensors;
        option.setLocationMode(LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
//      tempcoor="gcj02";//国家测绘局标准
//      tempcoor="bd09ll";//百度经纬度标准
//      tempcoor="bd09";//百度墨卡托标准
        option.setCoorType("gcj02");//可选，默认gcj02，设置返回的定位结果坐标系，
        int span=5000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(false);//可选，默认false,设置是否使用gps
        option.setLocationNotify(false);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIgnoreKillProcess(true);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
       
    }

    /**
     * l注册定位监听器接口
     */
    private void registerLocation(BDLocationListener l) {
        try {
            this.listener = l;
            if (mLClient != null) {
                if (mLClient.isStarted()) {
                    mLClient.requestLocation();
                } else {
                    if (listener != null)
                        // 接触上次绑定的监听器并停止定位
                        mLClient.unRegisterLocationListener(listener);
                    mLClient.stop();
                }
                mLClient.registerLocationListener(listener);
            }
        } catch (Exception e) {
            stopLocation();
        }
    }

    /**
     * 停止定位
     */
    public void stopLocation() {
        if (mLClient != null) {
            if (listener != null) {
                try {
                    mLClient.unRegisterLocationListener(listener);
                    listener = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            mLClient.stop();
            mLClient = null;
        }
    }

    public void clear()
    {
        stopLocation();
        addressListeners.clear();
    }

    /**
     * 将jsonSrc 解析成bean AddressItem
     * 
     * @param jsonSrc
     * @return
     */
    public AddressItem parseAddr(String jsonSrc) {
        AddressItem aItem = null;
        if (!TextUtils.isEmpty(jsonSrc) && !jsonSrc.equals("null")) {
            try {
                JSONObject object = new JSONObject(jsonSrc);
                if (object.getJSONObject("result").getInt("error") == 161) {
                    aItem = new AddressItem();
                    if (!object.isNull("content")) {
                        JSONObject content = object.getJSONObject("content");
                        if (!content.isNull("point")) {
                            JSONObject point = content.getJSONObject("point");
                            aItem.setPointx(point.getDouble("x"));
                            aItem.setPointy(point.getDouble("y"));
                        }
                        if (!content.isNull("radius")) {
                            aItem.setRadius(content.getDouble("radius"));
                        }
                        if (!content.isNull("addr")) {
                            JSONObject addr = content.getJSONObject("addr");
                            aItem.setAddress(addr.getString(type));
                        }
                    }
                }
            } catch (Exception e) {
            }
        }
        // Log.i("LOC", "parseAddr"+aItem.getAddress());
        return aItem;
    }

    private class BDLocationListenerImpl implements BDLocationListener {

        /*
         * (non-Javadoc) 接收异步返回的定位结果，参数是BDLocation类型参数
         * 
         * @see
         * com.baidu.location.BDLocationListener#onReceiveLocation(com.baidu
         * .location.BDLocation)
         */
        @Override
        public void onReceiveLocation(BDLocation location) {

//            if (mLClient != null) { // 修复团购页面打开这里会报空指针错误
//                mLClient.stop();
//            }

            stopLocation();

            AddressItem address = new AddressItem();
            address.setPointy(location.getLatitude());
            address.setPointx(location.getLongitude());
            address.setCityCode(location.getCityCode());
            address.setProvince(location.getProvince());
            address.setCity(location.getCity());
            address.setStreet(location.getStreet());
            address.setLoc_type(location.getLocType());
            address.setDistrict(location.getDistrict());
            if (!TextUtils.isEmpty(location.getAddrStr())) {
                address.setAddress(location.getAddrStr());
            }
            if (addressListeners != null && !addressListeners.isEmpty()) {
                for (AddressListener listener:addressListeners)
                {
                    listener.getAddress(address);
                }
                addressListeners.clear();
            }
            int type = location.getLocType();
            Log.i(TAG, "type = " + type);
            String errorMsg = ErrorConfig.getInstance().getErrorMsg(type);
            //ErrorLocationLogger.getLogger().doLog(errorMsg);
        }

        public void onReceivePoi(BDLocation poiLocation) {

//            if (mLClient != null) {
//                mLClient.stop();
//            }

            StringBuffer sb = new StringBuffer(256);
            sb.append("Poi time : ");
            sb.append(poiLocation.getTime());
            sb.append("\nerror code : ");
            sb.append(poiLocation.getLocType());
            sb.append("\nlatitude : ");
            sb.append(poiLocation.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(poiLocation.getLongitude());
            sb.append("\nradius : ");
            sb.append(poiLocation.getRadius());
            if (poiLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
                sb.append("\naddr : ");
                sb.append(poiLocation.getAddrStr());
            }
//            if (poiLocation.hasPoi()) {
//                sb.append("\nPoi:");
//                sb.append(poiLocation.getPoi());
//            } else {
//                sb.append("noPoi information");
//            }
            List<Poi> list = poiLocation.getPoiList();// POI数据
            if (list != null) {
            	sb.append("\npoilist size = : ");
            	sb.append(list.size());
            	for (Poi p : list) {
            		sb.append("\npoi= : ");
            		sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
            	}
            }
            stopLocation();
        }

    }
}
