package com.jihf.weather.weather;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.jihf.weather.R;
import com.jihf.weather.weather.bean.IndexBean;
import java.util.List;

/**
 * Func：
 * User：jihf
 * Data：2016-12-15
 * Time: 13:50
 * Mail：jihaifeng@raiyi.com
 */
public class TipsRyAdapter extends RecyclerView.Adapter<TipsRyAdapter.RytipsViewHolder> {
  private Context mContext;
  private List<IndexBean> mList;

  public void updateData(List<IndexBean> list) {
    this.mList = list;
    notifyDataSetChanged();
  }

  public TipsRyAdapter(Context mContext) {
    this.mContext = mContext;
  }

  @Override public RytipsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new RytipsViewHolder(LayoutInflater.from(mContext).inflate(R.layout.weather_foot_item, parent, false));
  }

  @Override public void onBindViewHolder(RytipsViewHolder holder, int position) {
    holder.title.setText((mList.get(position).title.contains("指数") || mList.get(position).title.length() >= 5) ? mList.get(position).title : mList.get(position).title + "指数");
    holder.desc.setText(mList.get(position).zs);
  }

  @Override public int getItemCount() {
    return null != mList ? mList.size() : 0;
  }

  public class RytipsViewHolder extends RecyclerView.ViewHolder {
    TextView title;
    TextView desc;

    public RytipsViewHolder(View itemView) {
      super(itemView);
      title = (TextView) itemView.findViewById(R.id.tips_title);
      desc = (TextView) itemView.findViewById(R.id.tips_desc);
    }
  }
}
