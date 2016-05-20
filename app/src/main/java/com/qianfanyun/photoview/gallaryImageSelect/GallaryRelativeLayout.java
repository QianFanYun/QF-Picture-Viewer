package com.qianfanyun.photoview.gallaryImageSelect;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * @author by morton_ws on 16/5/17.
 */
public class GallaryRelativeLayout extends RelativeLayout {
    public GallaryRelativeLayout(Context context) {
        this(context, null);
    }

    public GallaryRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }
}