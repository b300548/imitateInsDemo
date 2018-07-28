package com.liweijian.fileproviderdemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.liweijian.fileproviderdemo.utils.ImageUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

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

        textView = findViewById(R.id.pub_text);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.item2,menu);
            return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                break;
            case R.id.save: {
                ToastUtils.showShort(this,"输入了："+textView.getText().toString());
            }
        }
        return true;
    }

}
