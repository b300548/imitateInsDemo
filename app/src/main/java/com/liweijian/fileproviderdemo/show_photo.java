package com.liweijian.fileproviderdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

public class show_photo extends AppCompatActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_photo);

        imageView = (ImageView)findViewById(R.id.photo);
        imageView.setImageBitmap(CameraActivity.bitmap);
        MainActivity.setBitmap(CameraActivity.bitmap);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("图片预览");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.item,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                break;
            case R.id.next:
//                if (MainActivity.cropImageUri == null)
//                    ToastUtils.showShort(MainActivity.this,"请选择照片");
//                else {
//                    Intent intent = new Intent();
//                    intent.setClass(MainActivity.this, TextEdit.class);
//                    startActivity(intent);
//                }
                Intent intent = new Intent();
                intent.setClass(show_photo.this, TextEdit.class);
                startActivity(intent);
                break;
        }
        return true;
    }
}
