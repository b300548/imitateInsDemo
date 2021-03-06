package com.liweijian.fileproviderdemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.liweijian.fileproviderdemo.utils.ImageUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Created by Administrator on 2018/8/1.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private List<String> mList;

    private Bitmap bm;

    public ImageAdapter(List<String> list){
        mList = list;
    }



    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.photoView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                String filename = mList.get(position);
                Bitmap bitmap = BitmapFactory.decodeFile((String)filename);
                bm = ImageUtils.compressBitmap(bitmap);
               // bm = mList.get(position);
                MainActivity.setBitmap(bm);
                GalleryFragment.setImage(bm);

    }
});
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String src = mList.get(position);

        Bitmap bitmap = BitmapFactory.decodeFile((String) src);
        Bitmap bm = ImageUtils.compressBitmap(bitmap);
        holder.imageView.setImageBitmap(bm);

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        View photoView;
        ImageView imageView;

        public ViewHolder(View view){
            super(view);
            photoView = view;
            imageView = (ImageView)view.findViewById(R.id.imageView);
        }
    }

}
