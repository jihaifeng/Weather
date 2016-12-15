package com.jihf.weather.weather.bean;

import java.util.List;

/**
 * Func：
 * User：jihf
 * Data：2016-12-15
 * Time: 10:11
 * Mail：jihaifeng@raiyi.com
 */
public class WeatherBase {
  /**
   * error : 0
   * status : success
   * date : 2016-12-15
   * results : [{"currentCity":"杭州","pm25":"64","index":[{"title":"穿衣","zs":"冷","tipt":"穿衣指数","des":"天气冷，建议着棉服、羽绒服、皮夹克加羊毛衫等冬季服装。年老体弱者宜着厚棉衣、冬大衣或厚羽绒服。"},{"title":"洗车","zs":"较适宜","tipt":"洗车指数","des":"较适宜洗车，未来一天无雨，风力较小，擦洗一新的汽车至少能保持一天。"},{"title":"旅游","zs":"一般","tipt":"旅游指数","des":"天空状况还是比较好的，但温度稍微有点低，且风稍大，会让您感觉些许凉意。外出请注意防风。"},{"title":"感冒","zs":"较易发","tipt":"感冒指数","des":"天凉，昼夜温差较大，较易发生感冒，请适当增减衣服，体质较弱的朋友请注意适当防护。"},{"title":"运动","zs":"较不宜","tipt":"运动指数","des":"天气较好，但考虑风力较大，天气寒冷，推荐您进行室内运动，若在户外运动须注意保暖。"},{"title":"紫外线强度","zs":"最弱","tipt":"紫外线强度指数","des":"属弱紫外线辐射天气，无需特别防护。若长期在户外，建议涂擦SPF在8-12之间的防晒护肤品。"}],"weather_data":[{"date":"周四 12月15日 (实时：4℃)","dayPictureUrl":"http://api.map.baidu.com/images/weather/day/duoyun.png","nightPictureUrl":"http://api.map.baidu.com/images/weather/night/qing.png","weather":"多云转晴","wind":"北风3-4级","temperature":"7 ~ 0℃"},{"date":"周五","dayPictureUrl":"http://api.map.baidu.com/images/weather/day/qing.png","nightPictureUrl":"http://api.map.baidu.com/images/weather/night/duoyun.png","weather":"晴转多云","wind":"东北风微风","temperature":"8 ~ 0℃"},{"date":"周六","dayPictureUrl":"http://api.map.baidu.com/images/weather/day/duoyun.png","nightPictureUrl":"http://api.map.baidu.com/images/weather/night/duoyun.png","weather":"多云","wind":"南风微风","temperature":"12 ~ 5℃"},{"date":"周日","dayPictureUrl":"http://api.map.baidu.com/images/weather/day/xiaoyu.png","nightPictureUrl":"http://api.map.baidu.com/images/weather/night/duoyun.png","weather":"小雨转多云","wind":"南风微风","temperature":"14 ~ 7℃"}]}]
   */

  public int error;
  public String status;
  public String date;
  public List<ResultsBean> results;
  
 
}
