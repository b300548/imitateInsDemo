package com.liweijian.fileproviderdemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    @ViewInject(R.id.photo)
    private ImageView photo;
    @ViewInject(R.id.takePic)
    private Button takePic;
    @ViewInject(R.id.takeGallery)
    private Button takeGallery;
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int CODE_RESULT_REQUEST = 0xa2;
    private static final int CAMERA_PERMISSIONS_REQUEST_CODE = 0x03;
    private static final int STORAGE_PERMISSIONS_REQUEST_CODE = 0x04;
    private File fileUri = new File(Environment.getExternalStorageDirectory().getPath()+File.separator + new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.CHINA)
            .format(new Date())+"photo.jpg");
    private File fileCropUri = new File(Environment.getExternalStorageDirectory().getPath() +File.separator+ new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.CHINA)
            .format(new Date())+"crop_photo.jpg");
    public static Uri imageUri;
    public static Uri cropImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewUtils.inject(this);

        setTitle("选择照片");
    }


    @OnClick({R.id.takePic, R.id.takeGallery})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.takePic:
                Log.i("test","点击打开相机按钮");
                autoObtainCameraPermission();
                break;
            case R.id.takeGallery:
                Log.i("test","点击打开相册按钮");
                autoObtainStoragePermission();
                break;
            default:
        }
    }

    /**
     * 自动获取相机权限
     */
    private void autoObtainCameraPermission() {
        Log.i("test","自动获取相机权限");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                ToastUtils.showShort(this, "您已经拒绝过一次");
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, CAMERA_PERMISSIONS_REQUEST_CODE);
        } else {//有权限直接调用系统相机拍照
            if (hasSdcard()) {
                Log.i("test","已经获取了相机权限");
                imageUri = Uri.fromFile(fileUri);
                //通过FileProvider创建一个content类型的Uri
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Log.i("fileuri",fileUri.toString());
                    imageUri = FileProvider.getUriForFile(MainActivity.this, "com.liweijian.fileproviderdemo", fileUri);
                }
                PhotoUtils.takePicture(this, imageUri, CODE_CAMERA_REQUEST);
            } else {
                ToastUtils.showShort(this, "设备没有SD卡！");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            //调用系统相机申请拍照权限回调
            case CAMERA_PERMISSIONS_REQUEST_CODE: {
                Log.i("test","调用系统相机申请拍照权限回调");
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (hasSdcard()) {
                        imageUri = Uri.fromFile(fileUri);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                            imageUri = FileProvider.getUriForFile(MainActivity.this, "com.liweijian.fileproviderdemo", fileUri);//通过FileProvider创建一个content类型的Uri
                        PhotoUtils.takePicture(this, imageUri, CODE_CAMERA_REQUEST);
                    } else {
                        ToastUtils.showShort(this, "设备没有SD卡！");
                    }
                } else {

                    ToastUtils.showShort(this, "请允许打开相机！！");
                }
                break;


            }
            //调用系统相册申请Sdcard权限回调
            case STORAGE_PERMISSIONS_REQUEST_CODE:
                Log.i("test","调用系统相册申请Sdcard权限回调");
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    PhotoUtils.openPic(this, CODE_GALLERY_REQUEST);
                } else {

                    ToastUtils.showShort(this, "请允许打开操作SDCard！！");
                }
                break;
            default:
        }
    }

    private static final int OUTPUT_X = 480;
    private static final int OUTPUT_Y = 480;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i("onActivityResule",Integer.toString(resultCode));

//        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                //拍照完成回调
                case CODE_CAMERA_REQUEST:
                    Log.i("test","相机回调");
                    cropImageUri = Uri.fromFile(fileCropUri);
                    PhotoUtils.cropImageUri(this, imageUri, cropImageUri, 1, 1, OUTPUT_X, OUTPUT_Y, CODE_RESULT_REQUEST);
                    break;
                //访问相册完成回调
                case CODE_GALLERY_REQUEST:
                    if (resultCode == RESULT_OK) {
                        Log.i("test", "相册回调");
                        if (hasSdcard()) {
                            cropImageUri = Uri.fromFile(fileCropUri);
                            Uri newUri = Uri.parse(PhotoUtils.getPath(this, data.getData()));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                newUri = FileProvider.getUriForFile(this, "com.liweijian.fileproviderdemo", new File(newUri.getPath()));
                            }
                            PhotoUtils.cropImageUri(this, newUri, cropImageUri, 1, 1, OUTPUT_X, OUTPUT_Y, CODE_RESULT_REQUEST);
                        } else {
                            ToastUtils.showShort(this, "设备没有SD卡！");
                        }
                        break;
                    }
                case CODE_RESULT_REQUEST:
                    if (resultCode == RESULT_OK) {
                        Log.i("test", "裁剪回调");
                        Bitmap bitmap = PhotoUtils.getBitmapFromUri(cropImageUri, this);
                        if (bitmap != null) {
                            showImages(bitmap);
                        }
                        break;
                    }
                default:
            }
//        }
    }


    /**
     * 自动获取sdk权限
     */

    private void autoObtainStoragePermission() {
        Log.i("test","自动获取sdk权限");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSIONS_REQUEST_CODE);
        } else {
            PhotoUtils.openPic(this, CODE_GALLERY_REQUEST);
        }

    }

    private void showImages(Bitmap bitmap) {
        Log.i("test","显示图片");
        photo.setImageBitmap(bitmap);
    }

    /**
     * 检查设备是否存在SDCard的工具方法
     */
    public static boolean hasSdcard() {
        Log.i("test","判断是否有sdcard");
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
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
                Intent intent1 = new Intent();
                setResult(2,intent1);
                finish();
            case R.id.next:
                if (MainActivity.cropImageUri == null)
                    ToastUtils.showShort(MainActivity.this,"请选择照片");
                else {
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, TextEdit.class);
                    startActivity(intent);
                }
        }
        return true;
    }
}
