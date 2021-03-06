package com.jihf.weather.customview.RecyclerView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import com.jihf.weather.utils.ToolBarUtils;

public abstract class HidingScrollListener extends RecyclerView.OnScrollListener {
 
    private int mToolbarOffset = 0;
    private int mToolbarHeight;
     
    public HidingScrollListener(Context context) {
        mToolbarHeight = ToolBarUtils.getToolbarHeight(context);
    }
     
    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
         
        clipToolbarOffset();
        onMoved(mToolbarOffset);
         
        if((mToolbarOffset <mToolbarHeight && dy>0) || (mToolbarOffset >0 && dy<0)) {
            mToolbarOffset += dy;
        }
    }
     
    private void clipToolbarOffset() {
        if(mToolbarOffset > mToolbarHeight) {
            mToolbarOffset = mToolbarHeight;
        } else if(mToolbarOffset < 0) {
            mToolbarOffset = 0;
        }
    }
     
    public abstract void onMoved(int distance);
}