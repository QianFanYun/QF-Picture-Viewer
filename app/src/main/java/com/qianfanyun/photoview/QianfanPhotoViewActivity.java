package com.qianfanyun.photoview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.qianfanyun.photoview.gallaryImageSelect.GallaryImageSelectActivity;

public class QianfanPhotoViewActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;

    private Button btn_gallary_image_select;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qianfan_photo_view);

        initView();
    }

    private void initView() {
        mContext = this;
        btn_gallary_image_select = (Button) findViewById(R.id.btn_gallary_image_select);

        if (btn_gallary_image_select != null) {
            btn_gallary_image_select.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_gallary_image_select:
                Intent gallaryImageSelectIntent = new Intent(mContext, GallaryImageSelectActivity.class);
                startActivity(gallaryImageSelectIntent);
                break;
            default:
                break;
        }
    }
}
