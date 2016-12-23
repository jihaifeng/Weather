package com.jihf.weather.weather;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.jihf.weather.R;
import com.jihf.weather.city.CityManagerActivity;
import com.jihf.weather.config.Config;
import com.jihf.weather.utils.CityUtils;
import com.jihf.weather.utils.SpannableStringUtils;
import com.jihf.weather.utils.WeatherUtils;
import com.jihf.weather.weather.bean.ResultsBean;
import com.jihf.weather.weather.bean.WeatherDataBean;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Func：
 * User：jihf
 * Data：2016-12-15
 * Time: 11:03
 * Mail：jihaifeng@raiyi.com
 */
public class WeatherRyAdapter extends SwipeMenuAdapter<WeatherRyAdapter.WeatherHolder> {

  public static final int TYPE_HEADER = 0;  //说明是带有Header的
  public static final int TYPE_FOOTER = 1;  //说明是带有Footer的
  public static final int TYPE_NORMAL = 2;  //说明是不带有header和footer的

  //HeaderView, FooterView
  private View mHeaderView;
  private View mFooterView;
  private List<WeatherDataBean> mList;
  private List<ResultsBean> beanList;

  private Context mContext;

  public WeatherRyAdapter(Context mContext) {
    this.mContext = mContext;
  }

  public void updateResultsData(List<ResultsBean> list) {
    if (null == list || list.size() == 0) {
      return;
    }
    this.beanList = list;
    notifyDataSetChanged();
  }

  public void updateData(List<WeatherDataBean> list) {
    if (null == list || list.size() == 0) {
      return;
    }
    this.mList = list;
    notifyDataSetChanged();
  }

  @Override public View onCreateContentView(ViewGroup parent, int viewType) {
    if (mHeaderView != null && viewType == TYPE_HEADER) {
      return mHeaderView;
    }
    if (mFooterView != null && viewType == TYPE_FOOTER) {
      return mFooterView;
    }
    return LayoutInflater.from(mContext).inflate(R.layout.weather_data_item, parent, false);
  }

  @Override public WeatherHolder onCompatCreateViewHolder(View realContentView, int viewType) {
    if (mHeaderView != null && viewType == TYPE_HEADER) {
      return new WeatherHolder(mHeaderView);
    }
    if (mFooterView != null && viewType == TYPE_FOOTER) {
      return new WeatherHolder(mFooterView);
    }
    return new WeatherHolder(realContentView);
  }

  //@Override public WeatherHolder onCreateViewHolder(ViewGroup parent, int viewType) {
  //  if (mHeaderView != null && viewType == TYPE_HEADER) {
  //    return new WeatherHolder(mHeaderView);
  //  }
  //  if (mFooterView != null && viewType == TYPE_FOOTER) {
  //    return new WeatherHolder(mFooterView);
  //  }
  //  return new WeatherHolder(LayoutInflater.from(mContext).inflate(R.layout.weather_data_item, parent, false));
  //}

  @Override public void onBindViewHolder(WeatherHolder holder, int position) {

    if ((null != mList || null != beanList) && !isHeaderView(position) && !isFooterView(position)) {
      if (mHeaderView != null) {
        position--;
      }
      String str = "";
      WeatherDataBean weatherDataBean = null;
      if (mContext instanceof WeatherActivity) {
        str = (mList.get(position).date.length() > 2 ? mList.get(position).date.substring(0, 2) : mList.get(position).date);
        if (null != mList.get(position)) {
          weatherDataBean = mList.get(position);
        }
        holder.view_Line.setVisibility(View.GONE);
      } else if (mContext instanceof CityManagerActivity) {
        str = beanList.get(position).currentCity;
        if (null != beanList.get(position) && null != beanList.get(position).weather_data && beanList.get(position).weather_data.size() != 0) {
          weatherDataBean = beanList.get(position).weather_data.get(0);
        }
        holder.view_Line.setVisibility(View.VISIBLE);
        final String finalStr = str;
        holder.ll_weather.setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View view) {
            Intent intent = new Intent(mContext, WeatherActivity.class);
            intent.putExtra(Config.CITY_NAME_INTENT, finalStr);
            ((CityManagerActivity) mContext).setResult(RESULT_OK, intent);
            ((CityManagerActivity) mContext).finish();
          }
        });
      }
      if (null != weatherDataBean) {
        //这里加载数据的时候要注意，是从position-1开始，因为position==0已经被header占用了
        if (str.equals(WeatherUtils.getCurCityName())) {
          holder.weather_default.setVisibility(View.VISIBLE);
        } else {
          holder.weather_default.setVisibility(View.INVISIBLE);
        }
        holder.weather_date.setText(str);
        SpannableStringUtils.getBuilder(str).setSubscript();
        holder.weather_desc.setText(weatherDataBean.weather);
        holder.weather_temperature.setText(weatherDataBean.temperature.replace("~", "/").replace("℃","°"));
        int drawableId = WeatherUtils.getWeatherIcon(weatherDataBean.weather);
        Glide.with(mContext).load(drawableId).error(R.drawable.weather_icon_nonetwork).into(holder.weather_pic);
      }
    }
  }

  @Override public int getItemCount() {
    int count = 0;
    if (mContext instanceof WeatherActivity) {
      count = (mList == null ? 0 : mList.size());
    } else if (mContext instanceof CityManagerActivity) {
      count = (beanList == null ? 0 : beanList.size());
    }
    if (mHeaderView != null) {
      count++;
    }

    if (mFooterView != null) {
      count++;
    }
    return count;
  }

  public void removeData(int adapterPosition) {
    String str = beanList.get(adapterPosition).currentCity;
    Log.i("CityUtils", "removeData: " + str);
    if (mContext instanceof CityManagerActivity) {
      beanList.remove(adapterPosition);
      CityUtils.delete(str);
    }
    notifyDataSetChanged();
  }

  public class WeatherHolder extends RecyclerView.ViewHolder {
    TextView weather_date;
    TextView weather_temperature;
    TextView weather_default;
    TextView weather_desc;
    ImageView weather_pic;
    LinearLayout ll_weather;
    View view_Line;

    public WeatherHolder(View itemView) {
      super(itemView);
      //如果是headerview或者是footerview,直接返回
      if (itemView == mHeaderView) {
        return;
      }
      if (itemView == mFooterView) {
        return;
      }
      view_Line = itemView.findViewById(R.id.view_Line);
      ll_weather = (LinearLayout) itemView.findViewById(R.id.ll_weather);
      weather_date = (TextView) itemView.findViewById(R.id.tv_weather_date);
      weather_default = (TextView) itemView.findViewById(R.id.tv_default);
      weather_desc = (TextView) itemView.findViewById(R.id.tv_weather_desc);
      weather_temperature = (TextView) itemView.findViewById(R.id.tv_weather_temperature);
      weather_pic = (ImageView) itemView.findViewById(R.id.iv_weather_pic);
    }
  }

  /** 重写这个方法，很重要，是加入Header和Footer的关键，我们通过判断item的类型，从而绑定不同的view    * */
  @Override public int getItemViewType(int position) {
    if (mHeaderView == null && mFooterView == null) {
      return TYPE_NORMAL;
    }
    if (position == 0) {
      //第一个item应该加载Header
      return TYPE_HEADER;
    }
    if (position == getItemCount() - 1) {
      //最后一个,应该加载Footer
      return TYPE_FOOTER;
    }
    return TYPE_NORMAL;
  }

  //HeaderView和FooterView的get和set函数
  public View getHeaderView() {
    return mHeaderView;
  }

  public void setHeaderView(View headerView) {
    mHeaderView = headerView;
    notifyItemInserted(0);
  }

  public View getFooterView() {
    return mFooterView;
  }

  public void setFooterView(View footerView) {
    mFooterView = footerView;
    notifyItemInserted(getItemCount() - 1);
  }

  private boolean isHeaderView(int position) {
    return (mHeaderView != null) && (position == 0);
  }

  private boolean isFooterView(int position) {
    return (mFooterView != null) && (position == getItemCount() - 1);
  }
}
