package com.wyong.myandroidnetwork;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.wyong.myandroidnetwork.domain.CommentItem;
import com.wyong.myandroidnetwork.utils.IOUtils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * OkHttp 发起网络请求。
 * <p>
 * 服务器 接口地址：https://github.com/TrillGates/SOBAndroidMiniWeb
 * <p>
 * 真机调试地址：http://192.168.137.1:9102/get/param
 * 模拟器调试地址：http://10.0.2.2:9102/get/text
 */
public class OkHttpActivity extends AppCompatActivity {

    //    private static final String BASE_URL = "http://192.168.137.1:9102";
    public static final String BASE_URL = "http://10.0.2.2:9102";
    private static final String TAG = "OkHttpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_okhttp);
    }

    public void getRequest(View view) {
        //要有客户端，就类似我们要有一个浏览器
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10000, TimeUnit.MILLISECONDS)
                .build();
        //创建连接  请求内容
        Request request = new Request.Builder()
                .get()
                .url(BASE_URL + "/get/text")
                .build();
        //用 client 去创建请求任务
        Call task = okHttpClient.newCall(request);
        //异步请求
        task.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d(TAG, "onFailure === > " + e.toString());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                int code = response.code();
                Log.d(TAG, "code === > " + code);

                if (code == HttpURLConnection.HTTP_OK) {
                    ResponseBody body = response.body();
                    Log.d(TAG, "body === > " + body.string());
                }
            }
        });

    }

    public void postComment(View view) {
        //要有客户端，就类似我们要有一个浏览器
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10000, TimeUnit.MILLISECONDS)
                .build();

        // 要提交的内容
        CommentItem commentItem = new CommentItem("234123", "我在学习post提交评论内容，欧耶！");
        Gson gson = new Gson();
        String jsonStr = gson.toJson(commentItem);
        MediaType mediaType = MediaType.Companion.parse("application/json");

        RequestBody requestBody = RequestBody.Companion.create(jsonStr, mediaType);

        final Request request = new Request.Builder()
                .post(requestBody)
                .url(BASE_URL + "/post/comment")
                .build();

        Call task = client.newCall(request);
        task.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d(TAG, "onFailure === > " + e.toString());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                int code = response.code();
                Log.d(TAG, "code === > " + code);

                if (code == HttpURLConnection.HTTP_OK) {
                    ResponseBody body = response.body();
                    if (body != null) {
                        Log.d(TAG, "body === > " + body.string());
                    }
                }
            }
        });
    }

    /**
     * OkHttp 上传单个文件
     *
     * @param view
     */
    public void postFile(View view) {
        //要有客户端，就类似我们要有一个浏览器
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10000, TimeUnit.MILLISECONDS)
                .build();

        File file = new File("/storage/emulated/0/Android/data/com.wyong.myandroidnetwork/files/Pictures/11.png");
//        MediaType fileMediaType = MediaType.Companion.parse("image/png");
//        RequestBody fileBody = RequestBody.Companion.create(file, fileMediaType);
        MediaType fileType = MediaType.parse("image/png");
        RequestBody fileBody = RequestBody.create(file, fileType);

        RequestBody requestBody = new MultipartBody.Builder()
                .addFormDataPart("file", file.getName(), fileBody)
                .build();

        Request request = new Request.Builder()
                .url(BASE_URL + "/file/upload")
                .post(requestBody)
                .build();

        //发起请求
        Call task = client.newCall(request);
        task.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d(TAG, "onFailure === > " + e.toString());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                int code = response.code();
                Log.d(TAG, "code === > " + code);
                if (code == HttpURLConnection.HTTP_OK) {
                    ResponseBody body = response.body();
                    if (body != null) {
                        String result = body.string();
                        Log.d(TAG, "result ===> " + result);
                    }
                }
            }
        });

    }

    /**
     * OkHttp 上传多个文件
     *
     * @param view
     */
    public void postFiles(View view) {
        //要有客户端，就类似我们要有一个浏览器
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10000, TimeUnit.MILLISECONDS)
                .build();

        File fileOne = new File("/storage/emulated/0/Android/data/com.wyong.myandroidnetwork/files/Pictures/11.png");
        File fileTwo = new File("/storage/emulated/0/Android/data/com.wyong.myandroidnetwork/files/Pictures/12.png");
        File fileThree = new File("/storage/emulated/0/Android/data/com.wyong.myandroidnetwork/files/Pictures/13.png");
        File fileFour = new File("/storage/emulated/0/Download/14.png");
        MediaType fileType = MediaType.parse("image/png");
        RequestBody fileOneBody = RequestBody.create(fileOne, fileType);
        RequestBody fileTwoBody = RequestBody.create(fileTwo, fileType);
        RequestBody fileThreeBody = RequestBody.create(fileThree, fileType);
        RequestBody fileFourBody = RequestBody.create(fileFour, fileType);

        RequestBody requestBody = new MultipartBody.Builder()
                .addFormDataPart("files", fileOne.getName(), fileOneBody)
                .addFormDataPart("files", fileTwo.getName(), fileTwoBody)
                .addFormDataPart("files", fileThree.getName(), fileThreeBody)
                .addFormDataPart("files", fileFour.getName(), fileFourBody)
                .build();

        Request request = new Request.Builder()
                .url(BASE_URL + "/files/upload")
                .post(requestBody)
                .build();

        //发起请求
        Call task = client.newCall(request);
        task.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d(TAG, "onFailure === > " + e.toString());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                int code = response.code();
                Log.d(TAG, "code === > " + code);
                if (code == HttpURLConnection.HTTP_OK) {
                    ResponseBody body = response.body();
                    if (body != null) {
                        String result = body.string();
                        Log.d(TAG, "result ===> " + result);
                    }
                }
            }
        });
    }

    /**
     * OkHttp 下载文件
     *
     * @param view
     */
    public void downloadFiles(View view) {
        //要有客户端，就类似我们要有一个浏览器
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10000, TimeUnit.MILLISECONDS)
                .build();

        Request request = new Request.Builder()
                .get()
                .url(BASE_URL + "/download/15")
                .build();

        final Call call = client.newCall(request);
        //异步请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d(TAG, "onFailure === > " + e.toString());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                int code = response.code();
                Log.d(TAG, "code ===> " + code);
                if (code == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = response.body().byteStream();
                    downloadFile(inputStream, response.headers());
                }
            }
        });

        // 同步请求
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Response execute = call.execute();
//                    int code = execute.code();
//                    Log.d(TAG, "code ===> " + code);
//                    if (code == HttpURLConnection.HTTP_OK) {
//                        Headers headers = execute.headers();
//                        for (int i = 0; i < headers.size(); i++) {
//                            String key = headers.name(i);
//                            String value = headers.value(i);
//
//                            Log.d(TAG, key + " === " + value);
//                        }
//
//                        InputStream inputStream = execute.body().byteStream();
//                        downloadFile(inputStream, headers);
//                    }
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();

    }

    /**
     * 读写文件流
     *
     * @param inputStream
     * @param headers
     * @throws IOException
     */
    private void downloadFile(InputStream inputStream, Headers headers) throws IOException {
        String contentType = headers.get("Content-disposition");
        String fileName = contentType.replace("attachment; filename=", "");
        File outFile = new File(OkHttpActivity.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + File.separator + fileName);
        Log.d(TAG, "outFile === > " + outFile);
        if (!outFile.getParentFile().exists()) {
            outFile.mkdirs();
        }
        if (!outFile.exists()) {
            outFile.createNewFile();
        }

        //读流 写流
        FileOutputStream fos = new FileOutputStream(outFile);
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inputStream.read(buffer, 0, buffer.length)) != -1) {
            fos.write(buffer, 0, len);
        }
        fos.flush();

        IOUtils.closeIO(fos);
        IOUtils.closeIO(inputStream);
    }
}