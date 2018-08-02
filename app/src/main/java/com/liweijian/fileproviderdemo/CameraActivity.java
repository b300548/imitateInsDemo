package com.liweijian.fileproviderdemo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

public class CameraActivity extends AppCompatActivity {
    private static final String TAG = "CameraActivity";
    private android.hardware.Camera mCamera;
    private CameraPreview mPreview;
    private FrameLayout mCameralayout;
    private Button mTakePictureBtn;
    private int mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;

    public static Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        // 设置全屏
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera);
        getSupportActionBar().hide();
        autoObtainCameraPermission();
    }

    // 判断相机是否支持
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    // 设置相机横竖屏
    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, Camera camera) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }


    // 获取相机实例
    public Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(mCameraId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    // 开始预览相机
    public void openCamera(){
        if (mCamera == null) {
            mCamera = getCameraInstance();
            mPreview = new CameraPreview(CameraActivity.this, mCamera);
            mPreview.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    mCamera.autoFocus(null);
                    return false;
                }
            });
            mCameralayout = (FrameLayout) findViewById(R.id.camera_preview);
            mCameralayout.addView(mPreview);
            mCamera.startPreview();
        }
    }

    // 释放相机
    public void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    // 旋转图片
    public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap returnBm = null;
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                    bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }
        return returnBm;
    }

    // 拍照回调
    private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(final byte[] data, Camera camera) {
//            File pictureDir = Environment
//                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//
//            final String picturePath = pictureDir
//                    + File.separator
//                    + new SimpleDateFormat("yyyyMMdd-HHmm", Locale.CHINA).format(new Date()).toString()+ ".jpg";
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    File file = new File(picturePath);
//                    try {
//                        // 获取当前旋转角度, 并旋转图片
//                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//                        bitmap = rotateBitmapByDegree(bitmap, 90);
//                        BufferedOutputStream bos = new BufferedOutputStream(
//                                new FileOutputStream(file));
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
//                        bos.flush();
//                        bos.close();
//                        bitmap.recycle();
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }).start();

            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            bitmap = rotateBitmapByDegree(bitmap, 90);
            Intent intent = new Intent(CameraActivity.this,show_photo.class);
            startActivity(intent);
            // mCamera.startPreview();
        }
    };

    // 切换前置和后置摄像头
    public void switchCamera() {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(mCameraId, cameraInfo);
        if(cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK){
            mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        }
        else{
            mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
        mCameralayout.removeView(mPreview);
        releaseCamera();
        openCamera();
        setCameraDisplayOrientation(CameraActivity.this, mCameraId, mCamera);
    }

    // 聚焦回调
    private Camera.AutoFocusCallback mAutoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            if (success) {
                mCamera.takePicture(null, null, mPictureCallback);
            }
        }
    };

    private void autoObtainCameraPermission() {
        Log.i("test","自动获取相机权限");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                //ToastUtils.showShort(this, "您已经拒绝过一次");
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            if (!checkCameraHardware(this)) {
                Toast.makeText(CameraActivity.this, "相机不支持", Toast.LENGTH_SHORT)
                        .show();
            } else {
                mCamera = getCameraInstance();
                mPreview = new CameraPreview(CameraActivity.this, mCamera);
                mCameralayout = (FrameLayout) findViewById(R.id.camera_preview);
                mCameralayout.addView(mPreview);
                openCamera();
                mTakePictureBtn = (Button)findViewById(R.id.button_capture);
                mTakePictureBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCamera.autoFocus(mAutoFocusCallback);
                    }
                });
                setCameraDisplayOrientation(this, mCameraId, mCamera);
            }
            //有权限直接调用系统相机拍照
//            if (hasSdcard()) {
//                Log.i("test","已经获取了相机权限");
//                imageUri = Uri.fromFile(fileUri);
//                //通过FileProvider创建一个content类型的Uri
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                    Log.i("fileuri",fileUri.toString());
//                    imageUri = FileProvider.getUriForFile(MainActivity.this, "com.liweijian.fileproviderdemo", fileUri);
//                }
//                PhotoUtils.takePicture(this, imageUri, CODE_CAMERA_REQUEST);
//            } else {
//                ToastUtils.showShort(this, "设备没有SD卡！");
//            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case 1:
                if (!checkCameraHardware(this)) {
                    Toast.makeText(CameraActivity.this, "相机不支持", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    mCamera = getCameraInstance();
                    mPreview = new CameraPreview(CameraActivity.this, mCamera);
                    mCameralayout = (FrameLayout) findViewById(R.id.camera_preview);
                    mCameralayout.addView(mPreview);
                    openCamera();
                    mTakePictureBtn = (Button)findViewById(R.id.button_capture);
                    mTakePictureBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mCamera.autoFocus(mAutoFocusCallback);
                        }
                    });
                    setCameraDisplayOrientation(this, mCameraId, mCamera);
                }
                break;
        }
    }
}
