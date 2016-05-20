package com.qianfan.photoview.gallaryImageSelect;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.qianfan.photoview.R;

import java.io.File;

/**
 * @author by mortonws on 2016/5/19.
 */
public class GallaryImageView extends ImageView {
    private Context mContext;

    public GallaryImageView(Context context) {
        this(context, null);
    }

    public GallaryImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public void setImageUri(String imageLocalPath) {
        File imageFile = new File(imageLocalPath);
        setImageUri(imageFile);
    }

    public void setImageUri(File imageFile) {
        if (imageFile.getAbsolutePath().toLowerCase().endsWith(".gif")) {
            Glide.with(mContext)
                    .load(imageFile)
                    .asBitmap()
                    .error(R.color.gray)
                    .placeholder(R.color.gray)
                    .into(this);
        } else {
            Glide.with(mContext)
                    .load(imageFile)
                    .error(R.color.gray)
                    .placeholder(R.color.gray)
                    .crossFade(500)
                    .into(this);
        }
    }

    public void setImageUri(int resId) {
        Glide.with(mContext)
                .load(R.mipmap.icon_take_photo)
                .error(R.color.gray)
                .placeholder(R.color.gray)
                .into(this);
    }

    public void setImageUri(int resId, int errorResId) {
        Glide.with(mContext)
                .load(resId)
                .error(errorResId)
                .placeholder(R.color.gray)
                .into(this);
    }
}
