package com.liweijian.fileproviderdemo;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.liweijian.fileproviderdemo.app.AppConstants;
import com.liweijian.fileproviderdemo.utils.ImageUtils;
import com.liweijian.fileproviderdemo.view.MyRelativeLayout;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TextEdit extends AppCompatActivity implements MyRelativeLayout.MyRelativeTouchCallBack{
    private MyRelativeLayout rela;
    public static final String TAG = "TextEdit";

    public static Bitmap editedbitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_edit);
        //getSupportActionBar().hide();
        initUI();
        setTitle("编辑文字");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void initUI() {
        rela = (MyRelativeLayout) findViewById(R.id.id_rela);
        Bitmap bitmap = PhotoUtils.getBitmapFromUri(MainActivity.cropImageUri,this);
        rela.setBackGroundBitmap(bitmap);
        rela.setMyRelativeTouchCallBack(this);
    }

    /**
     * 当时重写这个方法是因为项目中有左右滑动切换不同滤镜的效果
     *
     * @param direction
     */
    @Override
    public void touchMoveCallBack(int direction) {
        if (direction == AppConstants.MOVE_LEFT) {
            Toast.makeText(TextEdit.this, "你在向左滑动！", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(TextEdit.this, "你在向右滑动！", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 这个方法可以用来实现滑到某一个地方删除该TextView的实现
     *
     * @param textView
     */
    @Override
    public void onTextViewMoving(TextView textView) {
        Log.d(TAG, "TextView正在滑动");
    }

    @Override
    public void onTextViewMovingDone() {
        Toast.makeText(TextEdit.this, "标签TextView滑动完毕！", Toast.LENGTH_SHORT).show();
    }

    public void btnClickExplain(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("温馨提示:");
        builder.setMessage("一、点击图片可以添加文字，点击文字滑动可以改变文字位置；\n" +
                "二、在图片上进行两指缩放旋转可以改变标签的方向和大小，当有多个标签TextView存在时，两指缩放和旋转会自动寻找离两指中心点最近的文字进行操作！\n" +
                "三、对话框中可滑动滑条改变字体颜色!\n" +
                "四、点击保存能将画布生成图片保存下来!\n");

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
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
                Intent intent1 = new Intent();
                finish();
                break;
            case R.id.save: {
                editedbitmap = ImageUtils.createViewBitmap(rela, rela.getWidth(), rela.getHeight());
                String fileName =  new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".png";
                String result = ImageUtils.saveBitmapToFile(editedbitmap, fileName);
                Toast.makeText(TextEdit.this, "保存位置:" + result, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(TextEdit.this,publish.class);
                startActivity(intent);
            }
        }
        return true;
    }
}
