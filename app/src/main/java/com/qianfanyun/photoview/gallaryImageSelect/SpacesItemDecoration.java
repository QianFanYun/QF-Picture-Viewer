package com.qianfanyun.photoview.gallaryImageSelect;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @author by morton_ws on 16/5/17.
 *         <p/>
 *         设置gridview 每个 item 中间间隔的间距
 */
public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public SpacesItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(space, space, space, space);
    }
}
