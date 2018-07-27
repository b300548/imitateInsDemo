package com.liweijian.fileproviderdemo;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class publish extends AppCompatActivity {
    private ImageView imageView;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        setTitle("新帖子");

        imageView = (ImageView)findViewById(R.id.image_publish);

        Drawable bmd = new BitmapDrawable(TextEdit.editedbitmap);
        imageView.setBackground(bmd);


    }
}
