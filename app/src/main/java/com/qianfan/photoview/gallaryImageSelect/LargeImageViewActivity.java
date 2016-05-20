package com.qianfan.photoview.gallaryImageSelect;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.qianfan.photoview.R;

import java.io.File;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * @author by mortonws on 2016/5/19.
 */
public class LargeImageViewActivity extends AppCompatActivity {
    public static String IMAGE_PATH = "image_path";

    private PhotoView largeImageView;
    private PhotoViewAttacher attacher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_large_image);
        initView();
    }

    private void initView() {
        largeImageView = (PhotoView) findViewById(R.id.large_image);

        attacher = new PhotoViewAttacher(largeImageView);

        String imagePath = getIntent().getStringExtra(IMAGE_PATH);
        Log.e("LargeImageViewActivity", "imagePath===>" + imagePath);

        File imageFile = new File(imagePath);
        Glide.with(this)
                .load(imageFile)
                .error(R.color.gray)
                .placeholder(R.color.gray)
                .crossFade(500)
                .listener(requestListener)
                .into(largeImageView);
    }

    private RequestListener<File, GlideDrawable> requestListener = new RequestListener<File, GlideDrawable>() {
        @Override
        public boolean onException(Exception e, File model, Target<GlideDrawable> target, boolean isFirstResource) {
            Log.e("RequestListener", "onException");
            return false;
        }

        @Override
        public boolean onResourceReady(GlideDrawable resource, File model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
            Log.e("RequestListener", "onResourceReady");
            attacher.update();
            return false;
        }
    };
}
