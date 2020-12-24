package com.wyong.myandroidnetwork;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 使用 HttpURLConnection 加载网络图片
 * <p>
 * 加载大图片，解决OOM问题
 */
public class PicLoadActivity extends AppCompatActivity {

    private static final String TAG = "PicLoadActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_load);
    }

    public void loadPic(View view) {

        /**
         * 使用开源框架的算法 动态设置图片采样率
         */
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 拿到图片的大小
        BitmapFactory.decodeResource(getResources(), R.mipmap.test_pic2, options);
        ImageView imageView = findViewById(R.id.result_image);
        // 拿到控件的尺寸
        int measuredHeight = imageView.getMeasuredHeight();
        int measuredWidth = imageView.getMeasuredWidth();
        Log.d(TAG, "measuredHeight===> " + measuredHeight);
        Log.d(TAG, "measuredWidth===> " + measuredWidth);
        // 使用开源框架的计算方式
        options.inSampleSize = calculateInSampleSize(options, measuredWidth, measuredHeight);
        options.inJustDecodeBounds = false;
        Log.d(TAG, "options.inSampleSize===> " + options.inSampleSize);
        // 根据控件的大小  动态计算 sample 值
        Bitmap bigImage = BitmapFactory.decodeResource(getResources(), R.mipmap.test_pic2, options);
        imageView.setImageBitmap(bigImage);


        /**
         * 自己写的动态设置图片采样率
         */
        // 加载超大图片  可能会出现 OOM
        // options 采样率设置
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        // 拿到图片的大小
//        BitmapFactory.decodeResource(getResources(), R.mipmap.test_pic2, options);
//        ImageView imageView = findViewById(R.id.result_image);
//        int outHeight = options.outHeight;
//        int outWidth = options.outWidth;
//        Log.d(TAG, "outHeight===> " + outHeight);
//        Log.d(TAG, "outWidth===> " + outWidth);
//        // 拿到控件的尺寸
//        int measuredHeight = imageView.getMeasuredHeight();
//        int measuredWidth = imageView.getMeasuredWidth();
//        Log.d(TAG, "measuredHeight===> " + measuredHeight);
//        Log.d(TAG, "measuredWidth===> " + measuredWidth);
//        // 图片比控件小的时候就原图显示  （不进行缩放）
//        options.inSampleSize = 1;
//        // 计算  图片的宽度 除以 控件的宽度
//        // 图片的高度 除以 控件的高度
//        // 取两者间的小值
//        if (outHeight > measuredHeight || outWidth > measuredWidth) {
//            int subHeight = outHeight / measuredHeight;
//            int subWidth = outWidth / measuredWidth;
////            options.inSampleSize = subHeight > subWidth ? subWidth : subHeight;
//            options.inSampleSize = Math.min(subHeight, subWidth);
//        }
//        options.inJustDecodeBounds = false;
//        Log.d(TAG, "options.inSampleSize===> " + options.inSampleSize);
//        // 根据控件的大小  动态计算 sample 值
//        Bitmap bigImage = BitmapFactory.decodeResource(getResources(), R.mipmap.test_pic2, options);
//        imageView.setImageBitmap(bigImage);


        /**
         * 直接获取网络图片的操作
         */
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    URL url = new URL("https://imgs.sunofbeaches.com/group1/M00/00/39/rBsADV_DCQCAAE8WAATrkdRYCOE863.png");
//                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                    connection.setConnectTimeout(10000);
//                    connection.setRequestMethod("GET");
//                    connection.setRequestProperty("accept", "*/*");
//                    connection.setRequestProperty("connection", "keep-alive");
//                    connection.setRequestProperty("Accept-Language", "zh-CN,zh");
//                    connection.connect();
//
//                    //结果码
//                    int responseCode = connection.getResponseCode();
//                    if (responseCode == HttpURLConnection.HTTP_OK) {
//                        InputStream inputStream = connection.getInputStream();
//                        // 转成Bitmap
//                        final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//                        //更新UI 要在主线程中
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                ImageView imageView = findViewById(R.id.result_image);
//                                imageView.setImageBitmap(bitmap);
//                            }
//                        });
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
    }

    /**
     * 开源框架的动态设置图片采样率
     *
     * https://www.sunofbeach.net/a/1201092087920054272
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int maxWidth, int maxHeight) {

        //这里其实是获取到默认的高度和宽度，也就是图片的实际高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;

        //默认采样率为1，也就是不变嘛。
        int inSampleSize = 1;


        //===============核心算法啦====================
        if (width > maxWidth || height > maxHeight) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) maxHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) maxWidth);
            }

            final float totalPixels = width * height;

            final float maxTotalPixels = maxWidth * maxHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > maxTotalPixels) {
                inSampleSize++;
            }
        }
        //=============核心算法end================
        return inSampleSize;
    }
}