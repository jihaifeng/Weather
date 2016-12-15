package com.raiyi.wsh_lib_bdlocation.mgr.bean;

/**
 * @author xjl
 */
public class AddressItem {
  private double pointx;// 经度
  private double pointy;// 维度
  private double radius;// 半径
  private String address;// 详细地址
  private String CityCode;// 城市编码
  private String Province;// 省份
  private String City;// 城市

  private int loc_type;// 手机当前方向
  private String district;// 区/县信息
  private String time;// 当前定位时间
  private String street;// 街道信息

  public String getProvince() {
    return Province;
  }

  public void setProvince(String province) {
    Province = province;
  }

  /**
   * @return 纬度信息
   */
  public double getPointx() {
    return pointx;
  }

  public void setPointx(double pointx) {
    this.pointx = pointx;
  }

  /**
   * @return 经度信息
   */
  public double getPointy() {
    return pointy;
  }

  public void setCityCode(String CityCode) {
    this.CityCode = CityCode;
  }

  public String getCityCode() {
    return this.CityCode;
  }

  public void setPointy(double pointy) {
    this.pointy = pointy;
  }

  /**
   * @return定位半径
   */
  public double getRadius() {
    return radius;
  }

  public void setRadius(double radius) {
    this.radius = radius;
  }

  /**
   * @return得到详细地址信息
   */
  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getCity() {
    // address = "中国北京市西城区西单北大街133号";
    // address = "中国内蒙古自治区呼和浩特市新城区锡林郭勒南路2号";
    String city = "";

    // 通过详细地址字符串匹配出省份名和城市名
    //ErrorLocationLogger.getLogger().doLog("address = " + address);
    if (address != null) {
      // 对类似于“吉林省吉林市”这种地址做处理
      int endIndex = address.indexOf("市");
      int startIndex = address.indexOf("省");
      if (startIndex < 0) {
        startIndex = 0;
      } else {
        startIndex += 1;
      }
      if (endIndex < 0) {
        city = address.substring(startIndex);
      } else if (endIndex > startIndex) {
        // 地址类似于“中国北京市”这类地址作处理
        city = address.substring(startIndex, endIndex + 1);
        if (city.contains("中国")) {
          int tempIndex = city.indexOf("国");
          if (tempIndex < 0) {
            tempIndex = 0;
          } else {
            tempIndex += 1;
          }
          city = city.substring(tempIndex);
        }
        if (city.contains("自治区")) {
          // 处理“中国内蒙古自治区呼和浩特市”这类地址
          int tempIndex2 = city.indexOf("区");
          if (tempIndex2 < 0) {
            tempIndex2 = 0;
          } else {
            tempIndex2 += 1;
          }
          city = city.substring(tempIndex2);
        }
      }
    }
    return city;
  }

  public void setCity(String city) {
    City = city;
  }

  /**
   * @return获取定位
   */
  public int getLoc_type() {
    return loc_type;
  }

  public void setLoc_type(int loc_type) {
    this.loc_type = loc_type;
  }

  /**
   * @return 获取区、县信息
   */
  public String getDistrict() {
    return district;
  }

  public void setDistrict(String district) {
    this.district = district;
  }

  /**
   * @return得到定位时间
   */
  public String getTime() {
    return time;
  }

  public void setTime(String time) {
    this.time = time;
  }

  /**
   * @return得到街道地址信息
   */
  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }
}
