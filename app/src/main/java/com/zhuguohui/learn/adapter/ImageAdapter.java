package com.zhuguohui.learn.adapter;

import android.content.Context;
import android.view.View;
import android.widget.Gallery;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.zhuguohui.learn.StackLayout;

/**
 * Created by yuelin on 2016/4/26.
 */
public class ImageAdapter extends StackLayout.BaseAdapter{
    private int[] images; // 数据源
    private Context context;

    public ImageAdapter(Context context,int[] images) {
        super();
        this.context = context;
        this.images = images;
    }


    @Override
    public int getVisibleCount() {
        return 3;
    }

    @Override
    public int getCount() {
         return images.length;
    }

    @Override
    public View getView(View view, int position, StackLayout parent) {
        ImageView imageView;
        if(view==null) {
            imageView = new ImageView(context); // 创建一个ImageView视图
            imageView.setScaleType(ImageView.ScaleType.FIT_XY); // 图片的布局方式
            imageView.setLayoutParams(new Gallery.LayoutParams(500, 400));
        }else {
            imageView= (ImageView) view;

        }
        Glide.with(context).load(images[position]).into(imageView);
        return imageView;
    }
}
