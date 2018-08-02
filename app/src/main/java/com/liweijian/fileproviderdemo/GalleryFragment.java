package com.liweijian.fileproviderdemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.liweijian.fileproviderdemo.utils.ImageUtils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by Administrator on 2018/8/2.
 */

public class GalleryFragment extends Fragment {

    private static ImageView photo;

    private ArrayList names = null;
    private ArrayList descs= null;
    private ArrayList fileNames = null;
    private ArrayList<Bitmap> photos = null;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            GridLayoutManager layoutManager =new GridLayoutManager(act,2,GridLayoutManager.HORIZONTAL,false);
            recyclerView.setLayoutManager(layoutManager);
            ImageAdapter adapter = new ImageAdapter(photos);
            recyclerView.setAdapter(adapter);

        }
    };

    private RecyclerView recyclerView;


    private MainActivity act = (MainActivity)getActivity();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gallery_fragment,container,false);

        photo = (ImageView)view.findViewById(R.id.photo) ;
        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
        autoObtainStoragePermission();

        return view;
    }

    private void getPhotos(){
        names = new ArrayList();
        descs = new ArrayList();
        fileNames = new ArrayList();
        photos = new ArrayList();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    MainActivity activity = (MainActivity)getActivity();
                    Cursor cursor = activity.getContentResolver().query(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
                    int i=1;
                    while (cursor.moveToNext()) {
                        if(i > 20)
                            break;
                        //获取图片的名称
                        String name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                        //获取图片的生成日期
                        byte[] data = cursor.getBlob(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        //获取图片的详细信息
                        String desc = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DESCRIPTION));
//            names.add(name);
//            descs.add(desc);
                        //fileNames.add(new String(data, 0, data.length - 1));
                        String filename = new String(data, 0, data.length - 1);
                        Bitmap bitmap = BitmapFactory.decodeFile(filename);
                        Bitmap bm = ImageUtils.compressBitmap(bitmap);
                        photos.add(bm);
                        if(i %5 ==0) {
                            Message msg = new Message();
                            mHandler.sendMessage(msg);
                        }
                        i++;
                        Log.d("number",i+"");
//            GridLayoutManager layoutManager =new GridLayoutManager(activity,2,GridLayoutManager.HORIZONTAL,false);
//            recyclerView.setLayoutManager(layoutManager);
//            ImageAdapter adapter = new ImageAdapter(fileNames);
//            recyclerView.setAdapter(adapter);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
//        MainActivity activity = (MainActivity)getActivity();
//        Cursor cursor = activity.getContentResolver().query(
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
//        while (cursor.moveToNext()) {
//            //获取图片的名称
//            String name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
//            //获取图片的生成日期
//            final byte[] data = cursor.getBlob(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
//            //获取图片的详细信息
//            String desc = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DESCRIPTION));
////            names.add(name);
////            descs.add(desc);
//            String filename = new String(data, 0, data.length - 1);
//
//            //fileNames.add(new String(data, 0, data.length - 1));
//            Message msg = new Message();
//            Bundle bundle = new Bundle();
//            bundle.putString("filename",filename);
//            msg.setData(bundle);
//            mHandler.sendMessage(msg);
//            GridLayoutManager layoutManager =new GridLayoutManager(activity,2,GridLayoutManager.HORIZONTAL,false);
//            recyclerView.setLayoutManager(layoutManager);
//            ImageAdapter adapter = new ImageAdapter(fileNames);
//            recyclerView.setAdapter(adapter);
        }

    public static void setImage(Bitmap bm){
        photo.setImageBitmap(bm);
    }


    private void autoObtainStoragePermission() {
        Log.i("test","自动获取sdk权限");
        MainActivity activity = (MainActivity)getActivity();
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            getPhotos();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getPhotos();
                } else {
                    Toast.makeText(getContext(), "请允许打开操作SDCard！！",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    
    
    
}
