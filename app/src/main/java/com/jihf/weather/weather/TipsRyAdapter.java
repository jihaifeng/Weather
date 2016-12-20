package com.jihf.weather.weather;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jihf.weather.R;
import com.jihf.weather.config.Config;
import com.jihf.weather.utils.SnackBarUtils;
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
  private CoordinatorLayout mRoot;
  private List<IndexBean> mList;

  public void updateData(List<IndexBean> list) {
    this.mList = list;
    notifyDataSetChanged();
  }

  public TipsRyAdapter(Context mContext, CoordinatorLayout cl) {
    this.mContext = mContext;
    this.mRoot = cl;
  }

  @Override public RytipsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new RytipsViewHolder(LayoutInflater.from(mContext).inflate(R.layout.weather_foot_item, parent, false));
  }

  @Override public void onBindViewHolder(final RytipsViewHolder holder, final int position) {
    holder.title.setText((TextUtils.isEmpty(mList.get(position).tipt)) ? "指数" : mList.get(position).tipt);
    holder.desc.setText(TextUtils.isEmpty(mList.get(position).zs) ? "合适" : mList.get(position).zs);
    int drawable = getItemPic(TextUtils.isEmpty(mList.get(position).tipt) ? "" : mList.get(position).tipt);
    Glide.with(mContext).load(drawable).placeholder(R.drawable.weather_icon_other).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.ivTips);
    holder.root.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        final Snackbar snackbar;
        if (null == mRoot) {
          snackbar = SnackBarUtils.LongSnackbar(holder.root, mList.get(position).des, SnackBarUtils.Alert);
        } else {
          snackbar = SnackBarUtils.LongSnackbar(mRoot, mList.get(position).des, SnackBarUtils.Confirm);
        }
        snackbar.setAction("知道了", new View.OnClickListener() {
          @Override public void onClick(View view) {
            snackbar.dismiss();
          }
        });
        snackbar.setActionTextColor(SnackBarUtils.getMsgColor());
        snackbar.show();
      }
    });
  }

  private int getItemPic(String tipt) {
    int pic = R.drawable.weather_icon_other;
    if (TextUtils.isEmpty(tipt)) {
      return pic;
    }
    if (tipt.contains(Config.WEATHER_DRESS)) {
      pic = R.drawable.weather_icon_dress;
    } else if (tipt.contains(Config.WEATHER_WASHCAR)) {
      pic = R.drawable.weather_icon_wash_car;
    } else if (tipt.contains(Config.WEATHER_CATCHACOLD)) {
      pic = R.drawable.weather_icon_catchacold;
    } else if (tipt.contains(Config.WEATHER_UV)) {
      pic = R.drawable.weather_icon_uv;
    } else if (tipt.contains(Config.WEATHER_TOURISM)) {
      pic = R.drawable.weather_icon_tourism;
    } else if (tipt.contains(Config.WEATHER_MOVEMENT)) {
      pic = R.drawable.weather_icon_movement;
    }
    return pic;
  }

  @Override public int getItemCount() {
    return null != mList ? mList.size() : 0;
  }

  public class RytipsViewHolder extends RecyclerView.ViewHolder {
    TextView title;
    TextView desc;
    ImageView ivTips;
    LinearLayout root;

    public RytipsViewHolder(View itemView) {
      super(itemView);
      ivTips = (ImageView) itemView.findViewById(R.id.iv_tips);
      title = (TextView) itemView.findViewById(R.id.tips_title);
      desc = (TextView) itemView.findViewById(R.id.tips_desc);
      root = (LinearLayout) itemView.findViewById(R.id.tips_root);
    }
  }
}
