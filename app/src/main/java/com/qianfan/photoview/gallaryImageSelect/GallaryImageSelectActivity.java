package com.qianfan.photoview.gallaryImageSelect;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qianfan.photoview.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author by morton_ws on 16/5/11.
 *         <p>
 *         手机相册选择
 */
public class GallaryImageSelectActivity extends AppCompatActivity {

    public static int mDurationTime = 200;
    public static String mAllImageTitle = "所有图片";
    public static int mMaxImageSelected = 9;

    /*相册图片展示的Recyclerview */
    private RecyclerView gallary_recyclerView;
    /*选择所有图片的相册名称列表*/
    private TextView tv_select_all_gallary;
    /*所有相册目录像是的布局*/
    private RelativeLayout rl_all_gallary_dir_name;
    /*所有相册名称展示的view*/
    private RecyclerView gallary_name_recyclerview;
    /*点击完成选择照片，在页面右上角*/
    private TextView tv_complete_select_image;

    private RelativeLayout rl_bottom;
    /*点击预览选择的图片*/
    private TextView tv_preview_image;
    private RelativeLayout rl_finish;

    /*主页面所有图片显示的layoutmananger*/
    private GridLayoutManager gridLayoutManager;
    /*显示所有照片的adapter*/
    private GallaryAdapter mGallaryImageAdapter;

    /*显示相册目录的LayoutManager*/
    private LinearLayoutManager linearLayoutManager;
    /*相册目录显示使用的adapter*/
    private GallaryAllNameAdapter mAllGallaryDirFileAdapter;

    private Context mContext;

//    private ScaleAnimation scaleOutAnimation;
//    private ScaleAnimation scaleInAnimation;

    /*选择相册时的出现和消失的动画*/
    TranslateAnimation translateInAnimation;
    TranslateAnimation translateOutAnimation;

    /*扫描到的所有图片的绝对路径 集合*/
    private List<String> mAllImagesLocalStoragePath = new ArrayList<>();
    /*扫描到含有图片的所有目录的绝对路径 集合*/
    private List<String> mGallaryDirLocalStoragePathList = new ArrayList<>();

    /**
     * 相册-对应相册下所有图片 集合
     * key-含有图片文件夹的绝对路径
     * value--该文件夹下所有图片的绝对路径 集合
     */
    private Map<String, List<String>> mGallaryDirImageMap = new HashMap<>();

    /*已经选择的所有图片的绝对路径 集合*/
    private List<String> mHasSelectedImagePathList = new ArrayList<>();

    /**
     * 相册目录文件夹的对象  集合
     * 1、包含该文件夹下第一张图片的绝对地址
     * 2、该文件夹下图片的数量
     * 3、该文件夹的绝对路径
     */
    private List<GallaryDirEntity> mGallaryDirList = new ArrayList<>();

    private ProgressDialog dialog;
    private File takePhotoImage = null;
    private ScanImagesRunnable mScanImagesRunnable;
    private AlertDialog mFinishActivityWarnDialog;
    private AlertDialog mConfigPermissionDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallary_image_select);
        mContext = this;
        initView();

        dialog = ProgressDialog.show(this, null, "正在加载...");
        scanLocalGallaryImages();

        initListener();
    }

    private void initView() {
        gallary_recyclerView = (RecyclerView) findViewById(R.id.gallary_recyclerview);
        tv_select_all_gallary = (TextView) findViewById(R.id.tv_select_all_gallary);
        rl_all_gallary_dir_name = (RelativeLayout) findViewById(R.id.rl_all_gallary_name);
        gallary_name_recyclerview = (RecyclerView) findViewById(R.id.gallary_name_recyclerview);
        tv_complete_select_image = (TextView) findViewById(R.id.tv_complete_selected_image);
        rl_bottom = (RelativeLayout) findViewById(R.id.rl_bottom);
        tv_preview_image = (TextView) findViewById(R.id.tv_preview_big_image);
        rl_finish = (RelativeLayout) findViewById(R.id.rl_finish);

        translateInAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, .0f, Animation.RELATIVE_TO_SELF, .0f,
                Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, .0f);
        translateInAnimation.setDuration(mDurationTime);

        translateOutAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, .0f, Animation.RELATIVE_TO_SELF, .0f,
                Animation.RELATIVE_TO_SELF, .0f, Animation.RELATIVE_TO_SELF, 1.0f);
        translateOutAnimation.setDuration(mDurationTime);


//        scaleOutAnimation = new ScaleAnimation(1.0f, 1.0f, 1.0f, .0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 1.0F);
//
//        scaleInAnimation = new ScaleAnimation(1.0f, 1.0f, .0f, 1.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 1.0f);

        mGallaryImageAdapter = new GallaryAdapter(this);
        gridLayoutManager = new GridLayoutManager(this, 3);
        gallary_recyclerView.setItemAnimator(new DefaultItemAnimator());
        gallary_recyclerView.setAdapter(mGallaryImageAdapter);
        gallary_recyclerView.setLayoutManager(gridLayoutManager);
        gallary_recyclerView.addItemDecoration(new SpacesItemDecoration(6));

        mAllGallaryDirFileAdapter = new GallaryAllNameAdapter(mContext);
        linearLayoutManager = new LinearLayoutManager(mContext, LinearLayout.VERTICAL, false);
        gallary_name_recyclerview.setItemAnimator(new DefaultItemAnimator());
        gallary_name_recyclerview.setAdapter(mAllGallaryDirFileAdapter);
        gallary_name_recyclerview.setLayoutManager(linearLayoutManager);

        AlertDialog.Builder mFinishActivityBuilder = new AlertDialog.Builder(mContext);
        mFinishActivityBuilder.setTitle("关闭相册");
        mFinishActivityBuilder.setMessage("确定要退出吗？");
        mFinishActivityBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mFinishActivityWarnDialog != null && mFinishActivityWarnDialog.isShowing()) {
                    mFinishActivityWarnDialog.dismiss();
                }
                finishActivity();
            }
        });
        mFinishActivityBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mFinishActivityWarnDialog != null && mFinishActivityWarnDialog.isShowing()) {
                    mFinishActivityWarnDialog.dismiss();
                }
            }
        });
        mFinishActivityWarnDialog = mFinishActivityBuilder.create();
        mFinishActivityWarnDialog.setCanceledOnTouchOutside(false);

        Log.e("initView", "packageName===>" + getPackageName());
        AlertDialog.Builder mConfigPermissionBuilder = new AlertDialog.Builder(mContext);
        mConfigPermissionBuilder.setTitle("权限申请");
        mConfigPermissionBuilder.setMessage("拍摄照片需要开启摄像机权限");
        mConfigPermissionBuilder.setPositiveButton("去设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mConfigPermissionDialog != null && mConfigPermissionDialog.isShowing()) {
                    mConfigPermissionDialog.dismiss();
                }
                Intent settingIntent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                settingIntent.addCategory(Intent.CATEGORY_DEFAULT);
                settingIntent.setData(Uri.parse("package:" + getPackageName()));
                settingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(settingIntent);
            }
        });
        mConfigPermissionBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mConfigPermissionDialog != null && mConfigPermissionDialog.isShowing()) {
                    mConfigPermissionDialog.dismiss();
                }
            }
        });
        mConfigPermissionDialog = mConfigPermissionBuilder.create();
        mConfigPermissionDialog.setCanceledOnTouchOutside(false);
    }

    private void initListener() {
        tv_select_all_gallary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rl_all_gallary_dir_name.getVisibility() == View.VISIBLE) {
                    dismissSelectAllGallaryNameView();
                } else {
                    rl_all_gallary_dir_name.startAnimation(translateInAnimation);
                    rl_all_gallary_dir_name.setVisibility(View.VISIBLE);
                }
            }
        });

        mAllGallaryDirFileAdapter.addOnGallarySelectedListener(new GallaryAllNameAdapter.OnGallaryNameSelectedListener() {
            boolean selectedAllImageDir = false;

            @Override
            public void OnSelected(int selectedIndex, String dirFileName) {
                mAllGallaryDirFileAdapter.setSelectedIndex(selectedIndex);
                tv_select_all_gallary.setText(dirFileName);

                String dirPath = mGallaryDirLocalStoragePathList.get(selectedIndex);
                List<String> oneDirImageList = mGallaryDirImageMap.get(dirPath);
                if (oneDirImageList == null || oneDirImageList.size() == 0) {
                    Toast.makeText(mContext, "该目录下未扫描到图片...", Toast.LENGTH_SHORT).show();
                }

                gallary_recyclerView.scrollToPosition(0);

                selectedAllImageDir = selectedIndex == 0;

                mGallaryImageAdapter.addScanImages(oneDirImageList, mHasSelectedImagePathList, selectedAllImageDir);
                handler.sendEmptyMessageDelayed(2001, 150);
            }
        });

        rl_all_gallary_dir_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissSelectAllGallaryNameView();
            }
        });
        mGallaryImageAdapter.addOnSelectedImageListener(new GallaryAdapter.OnSelectedImageListener() {
            @Override
            public void OnSelectedOperation(String imageLocalPath, int selectedOperation) {
                int selectedImageSize = mHasSelectedImagePathList.size();
                if (selectedOperation == 1) {
                    if (selectedImageSize == mMaxImageSelected) {
                        Toast.makeText(mContext, "你最多只能选择" + mMaxImageSelected + "张照片", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    selectedImageSize += 1;
                    mHasSelectedImagePathList.add(imageLocalPath);
                } else if (selectedOperation == 0) {
                    selectedImageSize -= 1;
                    mHasSelectedImagePathList.remove(imageLocalPath);
                }
                if (selectedImageSize > 0) {
                    tv_complete_select_image.setEnabled(true);
                    tv_preview_image.setEnabled(true);
                    String selectImageNumTitle = "(" + selectedImageSize + "/" + mMaxImageSelected + ")";
                    tv_complete_select_image.setText("完成" + selectImageNumTitle);
                    tv_preview_image.setText("预览" + selectImageNumTitle);
                } else {
                    tv_complete_select_image.setEnabled(false);
                    tv_preview_image.setEnabled(false);
                    tv_complete_select_image.setText("完成");
                    tv_preview_image.setText("预览");
                }
            }

            @Override
            public void OnTakePhoto() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                        ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(GallaryImageSelectActivity.this, new String[]{Manifest.permission.CAMERA}, 5001);
                } else {
                    takePhotoImageByOpenCamera();
                }
            }
        });
        tv_complete_select_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int size = mHasSelectedImagePathList.size();
                Toast.makeText(mContext, "你一共选择了" + size + "张照片", Toast.LENGTH_SHORT).show();
            }
        });

        rl_bottom.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        tv_preview_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "点击了预览图片", Toast.LENGTH_SHORT).show();
            }
        });
        rl_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 隐藏选择相册列表的View
     */
    private void dismissSelectAllGallaryNameView() {
        rl_all_gallary_dir_name.startAnimation(translateOutAnimation);
        rl_all_gallary_dir_name.setVisibility(View.GONE);
    }

    private void takePhotoImageByOpenCamera() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            Toast.makeText(this, "暂无外部存储", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        String imageDirPath = path + File.separator + "helloworld";
        File imageDirFile = new File(imageDirPath);
        if (!imageDirFile.exists()) {
            imageDirFile.mkdir();
        }
        String captureImageDirPath = imageDirPath + File.separator + "image";
        File captureImageDirFile = new File(captureImageDirPath);
        if (!captureImageDirFile.exists()) {
            captureImageDirFile.mkdir();
        }
        String imageFileName = "helloworld_" + System.currentTimeMillis() + ".jpg";

        String imageFilePath = captureImageDirFile + File.separator + imageFileName;
        Log.e("OnTakePhoto", "imageFilePath===>" + imageFilePath);

        takePhotoImage = new File(imageFilePath);
        Uri mUri = Uri.fromFile(takePhotoImage);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
        startActivityForResult(intent, 3001);
    }

    private void scanLocalGallaryImages() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            Toast.makeText(this, "暂无外部存储", Toast.LENGTH_SHORT).show();
            return;
        }

        mAllImagesLocalStoragePath.clear();
        mGallaryDirLocalStoragePathList.clear();
        mGallaryDirImageMap.clear();

        if (mScanImagesRunnable == null) {
            mScanImagesRunnable = new ScanImagesRunnable();
        }
        new Thread(mScanImagesRunnable).start();
    }

    private class ScanImagesRunnable implements Runnable {

        @Override
        public void run() {
            Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            ContentResolver mContentResolver = mContext.getContentResolver();

            // 查询图片格式包括:JPEG PNG GIF
            //= ? or " + MediaStore.Images.Media.MIME_TYPE + " = ? or " + MediaStore.Images.Media.MIME_TYPE + " = ?
            Cursor mCursor = mContentResolver.query(mImageUri, null,
                    MediaStore.Images.Media.MIME_TYPE + " in ( ? , ? , ? )  ",
                    new String[]{"image/jpeg", "image/png", "image/gif"},
                    MediaStore.Images.Media.DATE_TAKEN + " desc");

            mAllImagesLocalStoragePath.clear();
            mGallaryDirLocalStoragePathList.clear();
            mGallaryDirImageMap.clear();

            if (mCursor != null) {
                while (mCursor.moveToNext()) {
                    // 获取图片的路径
                    String mPerImageLocalStoragePath = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    // 获取该图片的父路径名
                    File mImageParentDirFile = new File(mPerImageLocalStoragePath).getParentFile();
                    String mImageParentDirFilePath = mImageParentDirFile.getAbsolutePath();

                    //利用一个HashSet防止多次扫描同一个文件夹（不加这个判断，图片多起来还是相当恐怖的~~）
                    mAllImagesLocalStoragePath.add(mPerImageLocalStoragePath);
                    if (!mGallaryDirLocalStoragePathList.contains(mImageParentDirFilePath)) {
                        mGallaryDirLocalStoragePathList.add(mImageParentDirFilePath);
                    }

                    /*一个相册目录文件夹下所有图片的绝对路径 集合*/
                    List<String> mOneGallaryDir_ScanedImageList;
                    if (mGallaryDirImageMap.containsKey(mImageParentDirFilePath)) {
                        mOneGallaryDir_ScanedImageList = mGallaryDirImageMap.get(mImageParentDirFilePath);
                    } else {
                        mOneGallaryDir_ScanedImageList = new ArrayList<>();
                    }

                    mOneGallaryDir_ScanedImageList.add(mPerImageLocalStoragePath);
                    mGallaryDirImageMap.put(mImageParentDirFilePath, mOneGallaryDir_ScanedImageList);
                }
                mCursor.close();

                if (mAllImagesLocalStoragePath.size() > 0) {
                    // 通知Handler扫描图片完成
                    handler.sendEmptyMessage(1001);
                } else {
                    handler.sendEmptyMessage(1002);
                }
            } else {
                handler.sendEmptyMessage(1002);
            }
        }
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                /*扫描相册完成，而且最少扫描到一张图片*/
                case 1001:
                    mGallaryDirList.clear();
                    mGallaryImageAdapter.addScanImages(mAllImagesLocalStoragePath, mHasSelectedImagePathList, true);

                    mGallaryDirImageMap.put(mAllImageTitle, mAllImagesLocalStoragePath);
                    //在list头部添加"全部照片" title
                    mGallaryDirLocalStoragePathList.add(0, mAllImageTitle);

                    for (String dirPath : mGallaryDirLocalStoragePathList) {
                        String firstImagePath = mGallaryDirImageMap.get(dirPath).get(0);

                        GallaryDirEntity gallaryDirEntity = new GallaryDirEntity();
                        gallaryDirEntity.setGallaryCoverLocalPath(firstImagePath);
                        gallaryDirEntity.setGallaryDirFilePath(dirPath);
                        gallaryDirEntity.setGallaryNum(mGallaryDirImageMap.get(dirPath).size());
                        mGallaryDirList.add(gallaryDirEntity);
                    }

                    mAllGallaryDirFileAdapter.addGallaryName(mGallaryDirList);

                    tv_select_all_gallary.setClickable(true);
                    break;
                /*未扫描到图片*/
                case 1002:
                    tv_select_all_gallary.setClickable(false);
                    gallary_recyclerView.setVisibility(View.GONE);
                    Toast.makeText(mContext, "未扫描到图片", Toast.LENGTH_SHORT).show();
                    break;
                /*选择一个相册之后延时关闭 相册名称 选择的layout*/
                case 2001:
                    dismissSelectAllGallaryNameView();
                    break;
                default:
                    break;
            }
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            return false;
        }
    });

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 3001:
                    dialog = ProgressDialog.show(this, null, "正在加载...");

                    if (takePhotoImage != null) {
                        MediaScannerConnection.scanFile(mContext, new String[]{takePhotoImage.getAbsolutePath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                            @Override
                            public void onScanCompleted(String path, Uri uri) {
                                scanLocalGallaryImages();
                            }
                        });
                    } else {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        Toast.makeText(mContext, "拍摄照片失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 5001:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (mConfigPermissionDialog != null && mConfigPermissionDialog.isShowing()) {
                        mConfigPermissionDialog.dismiss();
                    }
                    takePhotoImageByOpenCamera();
                } else {
                    if (mConfigPermissionDialog != null) {
                        if (mConfigPermissionDialog.isShowing()) {
                            mConfigPermissionDialog.dismiss();
                        }
                        mConfigPermissionDialog.show();
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (rl_all_gallary_dir_name.getVisibility() == View.VISIBLE) {
            dismissSelectAllGallaryNameView();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void finish() {
        int size = mHasSelectedImagePathList.size();
        if (size != 0) {
            mFinishActivityWarnDialog.show();
        } else {
            finishActivity();
        }
    }

    private void finishActivity() {
        super.finish();
    }
}
