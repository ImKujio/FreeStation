package com.dxys.demo.bingo.TabPage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dxys.demo.bingo.R;
import com.dxys.demo.bingo.altaotu.ImageGroup;
import com.dxys.demo.bingo.altaotu.ImageGroupMannager;
import com.dxys.demo.bingo.imagepager.ImageViewPager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import static com.dxys.demo.bingo.Utlis.log;

/**
 * Created by dxys on 17/10/4.
 */

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyViewHolder> {
    private ArrayList<ImageGroup> imageGroups;
    private Context context;
    private DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.preview)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();
    private int updataindex = 0;

    public MyRecyclerAdapter(Context context,ArrayList<ImageGroup> imageGroups)
    {
        this.context = context;
        this.imageGroups = imageGroups;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder myViewHolder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.image_group_item,null));
        log("laoding : ");
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.title.setText(imageGroups.get(position).groupName);
        ImageLoader.getInstance().displayImage(imageGroups.get(position).groupImageUrl,holder.imageView,displayImageOptions);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageGroupMannager.setCurrentImageGroup(imageGroups.get(position));
                Intent intent = new Intent(context, ImageViewPager.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return position == getItemCount()-1 ? 0 : 1;
    }

    @Override
    public int getItemCount() {
        return imageGroups.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        public TextView title;
        public ImageView imageView;
        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView)itemView.findViewById(R.id.card_text);
            imageView = (ImageView)itemView.findViewById(R.id.card_image);
        }
    }

    public void updata()
    {
        notifyItemRangeChanged(updataindex,getItemCount()-updataindex);
        updataindex = getItemCount()-1;
    }
    public void updataAll()
    {
        notifyItemRangeChanged(0,getItemCount());
    }
}
