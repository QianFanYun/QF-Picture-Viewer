package com.qianfanyun.photoview.gallaryImageSelect;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.qianfanyun.photoview.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author by morton_ws on 16/5/13.
 */
public class GallaryAdapter extends RecyclerView.Adapter<GallaryAdapter.GallaryViewHolder> {

    private Context mContext;
    private LayoutInflater inflater;
    private List<String> mGallaryImagePathList = new ArrayList<>();

    private List<String> mSelectedImagePathList = new ArrayList<>();

    private boolean selectedAllImagesItem = false;

    public GallaryAdapter(Context context) {
        this.mContext = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public GallaryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_gallary_image, parent, false);
        return new GallaryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final GallaryViewHolder holder, final int position) {
        String path = "";
        if (position >= 0 && position < mGallaryImagePathList.size()) {

            holder.rl_select_image.setVisibility(View.VISIBLE);
            if (position == 0 && selectedAllImagesItem) {
                holder.rl_select_image.setVisibility(View.GONE);
                holder.gallary_image.setImageUri(R.mipmap.icon_take_photo, R.mipmap.icon_take_photo);
                holder.gallary_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onSelectedImageListener != null) {
                            onSelectedImageListener.OnTakePhoto();
                        }
                    }
                });
                return;
            }

            int position_index = position;
            if (selectedAllImagesItem) {
                position_index -= 1;
            }

            path = mGallaryImagePathList.get(position_index);
            if (path.isEmpty()) {
                path = "";
            }

            File imageFile = new File(path);
            holder.gallary_image.setImageUri(imageFile);
        }

        boolean hasSelectedImage = mSelectedImagePathList.contains(path);
        if (hasSelectedImage) {
            holder.gallary_image.setColorFilter(R.color.black);
            holder.image_has_selected.setBackgroundResource(R.mipmap.pictures_selected);
        } else {
            holder.gallary_image.clearColorFilter();
            holder.image_has_selected.setBackgroundResource(R.mipmap.picture_unselected);
        }

        final String gallaryImageLocalPath = path;
        holder.gallary_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent largeImageIntent = new Intent(mContext, LargeImageViewActivity.class);
                largeImageIntent.putExtra(LargeImageViewActivity.IMAGE_PATH, gallaryImageLocalPath);
                mContext.startActivity(largeImageIntent);
            }
        });
        holder.rl_select_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean hasSelectedImage = mSelectedImagePathList.contains(gallaryImageLocalPath);
                if (hasSelectedImage) {
                    holder.gallary_image.clearColorFilter();
                    holder.image_has_selected.setBackgroundResource(R.mipmap.picture_unselected);

                    mSelectedImagePathList.remove(gallaryImageLocalPath);

                    if (onSelectedImageListener != null) {
                        onSelectedImageListener.OnSelectedOperation(gallaryImageLocalPath, 0);
                    }
                } else {
                    int hasSelectedImageSize = mSelectedImagePathList.size();
                    if (hasSelectedImageSize == GallaryImageSelectActivity.mMaxImageSelected) {
                        Toast.makeText(mContext, "你最多只能选择" + GallaryImageSelectActivity.mMaxImageSelected + "张照片", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    holder.gallary_image.setColorFilter(R.color.black);
                    holder.image_has_selected.setBackgroundResource(R.mipmap.pictures_selected);

                    mSelectedImagePathList.add(gallaryImageLocalPath);

                    if (onSelectedImageListener != null) {
                        onSelectedImageListener.OnSelectedOperation(gallaryImageLocalPath, 1);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        int count = mGallaryImagePathList.size();
        if (selectedAllImagesItem) {
            count += 1;
        }
        return count;
    }

    public void addScanImages(List<String> scanImagePathList, List<String> selectedImagePathList) {
        addScanImages(scanImagePathList, selectedImagePathList, false);
    }

    public void addScanImages(List<String> scanImagePathList, List<String> selectedImagePathList, boolean selectedAllImagesItem) {
        mGallaryImagePathList.clear();
        mSelectedImagePathList.clear();
        this.selectedAllImagesItem = selectedAllImagesItem;
        notifyDataSetChanged();
        if (scanImagePathList == null) {
            scanImagePathList = new ArrayList<>();
        }
        mGallaryImagePathList.addAll(scanImagePathList);

        if (selectedImagePathList == null) {
            selectedImagePathList = new ArrayList<>();
        }
        mSelectedImagePathList.addAll(selectedImagePathList);

        notifyDataSetChanged();
    }

    class GallaryViewHolder extends RecyclerView.ViewHolder {

        GallarySquareImageView gallary_image;

        RelativeLayout rl_select_image;
        ImageView image_has_selected;

        public GallaryViewHolder(View view) {
            super(view);
            gallary_image = (GallarySquareImageView) view.findViewById(R.id.gallary_image);

            rl_select_image = (RelativeLayout) view.findViewById(R.id.rl_select_image);
            image_has_selected = (ImageView) view.findViewById(R.id.image_has_selected);
        }
    }

    private OnSelectedImageListener onSelectedImageListener;

    public void addOnSelectedImageListener(OnSelectedImageListener onSelectedImageListener) {
        this.onSelectedImageListener = onSelectedImageListener;
    }

    public interface OnSelectedImageListener {
        /**
         * 图片选择操作
         *
         * @param imageLocalPath    选择图片的本地路径
         * @param selectedOperation 1-选择;0-未选择 或 取消选择
         */
        public void OnSelectedOperation(String imageLocalPath, int selectedOperation);

        public void OnTakePhoto();
    }
}