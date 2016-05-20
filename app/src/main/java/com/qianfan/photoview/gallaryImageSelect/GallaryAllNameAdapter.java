package com.qianfan.photoview.gallaryImageSelect;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qianfan.photoview.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author by morton_ws on 16/5/16.
 */
public class GallaryAllNameAdapter extends RecyclerView.Adapter<GallaryAllNameAdapter.GallaryNameViewHolder> {

    private Context mContext;
    private int selectedIndex = 0;
    private LayoutInflater inflater;

    private List<GallaryDirEntity> gallaryDirList = new ArrayList<>();

    public GallaryAllNameAdapter(Context context) {
        this.mContext = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public GallaryNameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_gallary_name, parent, false);

        return new GallaryNameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final GallaryNameViewHolder holder, final int position) {
        GallaryDirEntity gallaryDirEntity = gallaryDirList.get(position);
        String gallaryDirPath = gallaryDirEntity.getGallaryDirFilePath();
        String eachGallaryCoverPath = gallaryDirEntity.getGallaryCoverLocalPath();
        int gallaryImageNum = gallaryDirEntity.getGallaryNum();

        if (eachGallaryCoverPath.isEmpty()) {
            eachGallaryCoverPath = "";
        }
        File coverImageFile = new File(eachGallaryCoverPath);
        holder.image_gallary_cover.setImageUri(coverImageFile);


        File dirFile = new File(gallaryDirPath);
        final String dirFileName = dirFile.getName();
        holder.tv_gallary_name.setText(dirFileName);
        holder.tv_gallary_image_num.setText(gallaryImageNum + "张");
        if (selectedIndex == position) {
            holder.image_selected_status.setVisibility(View.VISIBLE);
        } else {
            holder.image_selected_status.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onGallaryNameSelectedListener != null) {
                    onGallaryNameSelectedListener.OnSelected(position, dirFileName);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return gallaryDirList.size();
    }

    public void addGallaryName(List<GallaryDirEntity> gallaryDirList) {
        this.gallaryDirList.clear();
        if (gallaryDirList != null) {
            this.gallaryDirList.addAll(gallaryDirList);
        }
        notifyDataSetChanged();
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
        notifyDataSetChanged();

    }

    public class GallaryNameViewHolder extends RecyclerView.ViewHolder {
        TextView tv_gallary_name;
        TextView tv_gallary_image_num;
        ImageView image_selected_status;
        GallaryImageView image_gallary_cover;

        public GallaryNameViewHolder(View view) {
            super(view);
            tv_gallary_name = (TextView) view.findViewById(R.id.tv_gallary_name);
            tv_gallary_image_num = (TextView) view.findViewById(R.id.tv_gallary_image_num);
            image_selected_status = (ImageView) view.findViewById(R.id.image_selected_status);
            image_gallary_cover = (GallaryImageView) view.findViewById(R.id.image_gallary_cover);
        }
    }

    private OnGallaryNameSelectedListener onGallaryNameSelectedListener;

    public void addOnGallarySelectedListener(OnGallaryNameSelectedListener onGallaryNameSelectedListener) {
        this.onGallaryNameSelectedListener = onGallaryNameSelectedListener;
    }


    public interface OnGallaryNameSelectedListener {
        public void OnSelected(int selectedIndex, String dirFileName);
    }
}
