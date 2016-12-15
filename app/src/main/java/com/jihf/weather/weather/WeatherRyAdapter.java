package com.jihf.weather.weather;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.jihf.weather.R;
import com.jihf.weather.weather.bean.WeatherDataBean;
import java.util.List;

/**
 * Func：
 * User：jihf
 * Data：2016-12-15
 * Time: 11:03
 * Mail：jihaifeng@raiyi.com
 */
public class WeatherRyAdapter extends RecyclerView.Adapter<WeatherRyAdapter.WeatherHolder> {

  public static final int TYPE_HEADER = 0;  //说明是带有Header的
  public static final int TYPE_FOOTER = 1;  //说明是带有Footer的
  public static final int TYPE_NORMAL = 2;  //说明是不带有header和footer的

  //HeaderView, FooterView
  private View mHeaderView;
  private View mFooterView;
  private List<WeatherDataBean> mList;

  private Context mContext;

  public WeatherRyAdapter(Context mContext) {
    this.mContext = mContext;
  }

  public void updateData(List<WeatherDataBean> list) {
    this.mList = list;
    notifyDataSetChanged();
  }

  @Override public WeatherHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if (mHeaderView != null && viewType == TYPE_HEADER) {
      return new WeatherHolder(mHeaderView);
    }
    if (mFooterView != null && viewType == TYPE_FOOTER) {
      return new WeatherHolder(mFooterView);
    }
    return new WeatherHolder(LayoutInflater.from(mContext).inflate(R.layout.weather_data_item, parent, false));
  }

  @Override public void onBindViewHolder(WeatherHolder holder, int position) {

    if (null != mList && !isHeaderView(position) && !isFooterView(position)) {
      if (mHeaderView != null) {
        position--;
      }
      //这里加载数据的时候要注意，是从position-1开始，因为position==0已经被header占用了
      holder.weather_date.setText(mList.get(position).date.length() > 2 ? mList.get(position).date.substring(0, 2) : mList.get(position).date);
      holder.weather_desc.setText(mList.get(position).weather);
      holder.weather_temperature.setText(mList.get(position).temperature);
      Glide.with(mContext).load(mList.get(position).dayPictureUrl).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(holder.weather_pic);
    }
  }

  @Override public int getItemCount() {
    int count = (mList == null ? 0 : mList.size());
    if (mHeaderView != null) {
      count++;
    }

    if (mFooterView != null) {
      count++;
    }
    return count;
  }

  public class WeatherHolder extends RecyclerView.ViewHolder {
    TextView weather_date;
    TextView weather_temperature;
    TextView weather_desc;
    ImageView weather_pic;

    public WeatherHolder(View itemView) {
      super(itemView);
      //如果是headerview或者是footerview,直接返回
      if (itemView == mHeaderView) {
        return;
      }
      if (itemView == mFooterView) {
        return;
      }
      weather_date = (TextView) itemView.findViewById(R.id.tv_weather_date);
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
