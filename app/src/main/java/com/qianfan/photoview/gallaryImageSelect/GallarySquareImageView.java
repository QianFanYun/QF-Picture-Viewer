package com.qianfan.photoview.gallaryImageSelect;

import android.content.Context;
import android.util.AttributeSet;

/**
 * @author by mortonws on 2016/5/18.
 *         <p/>
 *         用来设置图片宽高等比显示，显示成正方形
 */
public class GallarySquareImageView extends GallaryImageView {
    public GallarySquareImageView(Context context) {
        this(context, null);
    }

    public GallarySquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }
}
