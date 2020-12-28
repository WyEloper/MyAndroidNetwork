package com.wyong.myandroidnetwork;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.wyong.myandroidnetwork.domain.CommentItem;
import com.wyong.myandroidnetwork.utils.IOUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * Java 原生API 发起网络请求。
 *
 * 服务器 接口地址：https://github.com/TrillGates/SOBAndroidMiniWeb
 * <p>
 * 真机调试地址：http://192.168.137.1:9102/get/param
 * 模拟器调试地址：http://10.0.2.2:9102/get/text
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class RequestTestActivity extends AppCompatActivity {

    private static final String TAG = "PostTestActivity";
    private OutputStream mOutputStream = null;
    private InputStream mInputStream = null;

//    private static final String BASE_URL = "http://192.168.137.1:9102";
    private static final String BASE_URL = "http://10.0.2.2:9102";

    public static final int PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_test);

        //请求文件读写权限(建议使用第三方库进行动态获取)
        int result = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_CODE);
        }
    }

    /**
     * post带参数请求
     */
    public void postWithParams(View view) {
        Map<String, String> params = new HashMap<>();
        params.put("string", "这是我提交的字符串");
        startRequest(params, "POST", "/post/string");
    }

    /**
     * get带参数请求
     */
    public void getWithParams(View view) {
        Map<String, String> params = new HashMap<>();
        params.put("keyword", "这是我的关键字keyword");
        params.put("page", "12");
        params.put("order", "0");
        startRequest(params, "GET", "/get/param");
    }

    private void startRequest(Map<String, String> params, String method, String api) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedReader bufferedReader = null;
                try {
                    //组装参数
                    StringBuilder sb = new StringBuilder();
                    if (params != null && params.size() > 0) {
                        sb.append("?");
                        Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
                        while (iterator.hasNext()) {
                            Map.Entry<String, String> next = iterator.next();
                            sb.append(next.getKey());
                            sb.append("=");
                            sb.append(next.getValue());

                            if (iterator.hasNext()) {
                                sb.append("&");
                            }
                        }
                        Log.d(TAG, "sb result ===> " + sb.toString());
                    }
                    String paramStr = sb.toString();
                    URL url;
                    if (paramStr != null && paramStr.length() > 0) {
                        url = new URL(BASE_URL + api + paramStr);
                    } else {
                        url = new URL(BASE_URL + api);
                    }
                    Log.d(TAG, "url ===> " + url.toString());

                    //请求网络
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod(method);
                    connection.setRequestProperty("Accept-Language", "zh-CN,zh");
                    connection.setRequestProperty("Accept", "*/*");
                    connection.connect();
                    //结果码
                    int responseCode = connection.getResponseCode();
                    Log.d(TAG, "responseCode ===> " + responseCode);
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = connection.getInputStream();
                        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        String jsonStr = bufferedReader.readLine();
                        Log.d(TAG, "result ===> " + jsonStr);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    /**
     * post 提交评论
     */
    public void postRequest(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(BASE_URL + "/post/comment");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(10000);
                    connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    connection.setRequestProperty("Accept", "application/json,text/plain,*/*");
                    connection.setRequestProperty("Accept-Language", "zh-CN,zh");

                    CommentItem commentItem = new CommentItem("234123", "我是评论内容。。。哈哈哈");
                    Gson gson = new Gson();
                    String jsonStr = gson.toJson(commentItem);
                    byte[] bytes = jsonStr.getBytes("UTF-8");
                    Log.d(TAG, "jsonStr length ===> " + jsonStr.length());
                    Log.d(TAG, "bytes length ===> " + bytes.length);
                    connection.setRequestProperty("Content-Length", String.valueOf(bytes.length));
                    // 连接
                    connection.connect();
                    // 把数据给到服务器
                    mOutputStream = connection.getOutputStream();
                    mOutputStream.write(bytes);
                    mOutputStream.flush();

                    // 拿到结果
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        mInputStream = connection.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(mInputStream));
                        Log.d(TAG, "result === > " + bufferedReader.readLine());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (mOutputStream != null) {
                        try {
                            mOutputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (mInputStream != null) {
                            try {
                                mInputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }).start();
    }

    /**
     * post 方式上传单文件
     * @param view
     */
    public void postFile(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OutputStream outputStream = null;
                InputStream inputStream = null;
                BufferedInputStream bfi = null;
                try {
                    File file = new File("/sdcard/1601017789861.jpg");
                    String fileKey = "file";
                    String fileName = file.getName();
                    String fileType = "image/jpeg";
                    //上传文件时随机生成的字符串
                    String BOUNDARY = "--------------------------616331138829946456992433";
//                    String BOUNDARY = "----------------------------616331138829946456992433";
//                    String BOUNDARY = "----------------------------616331138829946456992433--";
                    URL url = new URL(BASE_URL + "/file/upload");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(10000);
                    connection.setRequestProperty("User-Agent","Android/" + Build.VERSION.SDK_INT);
                    connection.setRequestProperty("Accept","*/*");
                    connection.setRequestProperty("Cache-Control","no-cache");
                    connection.setRequestProperty("Content-Type","multipart/form-data; boundary=" + BOUNDARY);
                    connection.setRequestProperty("Connection","keep-alive");
                    connection.setDoInput(true);
                    connection.setDoOutput(true);

                    //链接
                    connection.connect();
                    outputStream = connection.getOutputStream();
                    //准备数据(头部信息)
                    //----------------------------616331138829946456992433
                    //Content-Disposition: form-data; name="file"; filename="5c40e506.jpg"
                    //Content-Type: image/jpeg
                    StringBuilder headerInfo = new StringBuilder();
                    headerInfo.append("--");
                    headerInfo.append("BOUNDARY");
                    headerInfo.append("\r\n");
                    headerInfo.append("Content-Disposition: form-data; name=\""+ fileKey + "\"; filename=\"" + fileName + "\"");
                    headerInfo.append("\r\n");
                    headerInfo.append("Content-Type: " + fileType);
                    headerInfo.append("\r\n");
                    headerInfo.append("\r\n");
                    byte[] headerInfoBytes = headerInfo.toString().getBytes("UTF-8");
                    outputStream.write(headerInfoBytes);
                    //文件内容
                    FileInputStream fos = new FileInputStream(file);
                    bfi = new BufferedInputStream(fos);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = bfi.read(buffer,0,buffer.length)) != -1 ){
                        outputStream.write(buffer, 0, length);
                    }

                    //写尾部信息
                    StringBuilder footerInfo = new StringBuilder();
                    headerInfo.append("\r\n");
                    footerInfo.append("--");
                    headerInfo.append("BOUNDARY");
                    footerInfo.append("--");
                    headerInfo.append("\r\n");
                    headerInfo.append("\r\n");
                    outputStream.write(footerInfo.toString().getBytes("UTF-8"));
                    outputStream.flush();

                    //获取返回值
                    int responseCode = connection.getResponseCode();
                    Log.d(TAG,"responseCode ===> " + responseCode);
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        inputStream = connection.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        String result = bufferedReader.readLine();
                        Log.d(TAG,"result -=-=-= > " + result);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (bfi != null) {
                        try {
                            bfi.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }


        }).start();
    }


    /**
     * post 方式上传多个文件
     * @param view
     */
    public void postFiles(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OutputStream outputStream = null;
                try {
                    File fileOne = new File("/sdcard/1601017789861.jpg");
                    File fileTwo = new File("/sdcard/1601018119390.jpg");
                    File fileThree = new File("/sdcard/avatar_small.jpg");

                    String fileKey = "files";
//                    String fileName = file.getName();
                    String fileType = "image/jpeg"; // image/png
                    //上传文件时随机生成的字符串
                    String BOUNDARY = "--------------------------374872480769042820543935";
//                    String BOUNDARY = "----------------------------374872480769042820543935";
//                    String BOUNDARY = "----------------------------374872480769042820543935--";
                    URL url = new URL(BASE_URL + "/files/upload");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(10000);
                    connection.setRequestProperty("User-Agent","Android/" + Build.VERSION.SDK_INT);
                    connection.setRequestProperty("Accept","*/*");
                    connection.setRequestProperty("Cache-Control","no-cache");
                    connection.setRequestProperty("Content-Type","multipart/form-data; boundary=" + BOUNDARY);
                    connection.setRequestProperty("Connection","keep-alive");
                    connection.setDoInput(true);
                    connection.setDoOutput(true);

                    //链接
                    connection.connect();
                    outputStream = connection.getOutputStream();

                    //上传文件 公共方法
                    uploadFile(fileOne, fileKey, fileOne.getName(), fileType, outputStream, false);
                    uploadFile(fileTwo, fileKey, fileTwo.getName(), fileType, outputStream, false);
                    uploadFile(fileThree, fileKey, fileThree.getName(), fileType, outputStream, true);
                    outputStream.flush();

                    //获取返回值
                    int responseCode = connection.getResponseCode();
                    Log.d(TAG,"responseCode ===> " + responseCode);
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = connection.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        String result = bufferedReader.readLine();
                        Log.d(TAG,"result -=-=-= > " + result);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
//                    if (bfi != null) {
//                        try {
//                            bfi.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
//
//                    if (inputStream != null) {
//                        try {
//                            inputStream.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
                }
            }

            private void uploadFile(File file, String fileKey, String fileName,
                                    String fileType, OutputStream outputStream, Boolean isLast) throws IOException {
                //准备数据(头部信息)
                //----------------------------616331138829946456992433
                //Content-Disposition: form-data; name="file"; filename="5c40e506.jpg"
                //Content-Type: image/jpeg
                StringBuilder headerInfo = new StringBuilder();
                headerInfo.append("--");
                headerInfo.append("BOUNDARY");
                headerInfo.append("\r\n");
                headerInfo.append("Content-Disposition: form-data; name=\""+ fileKey + "\"; filename=\"" + fileName + "\"");
                headerInfo.append("\r\n");
                headerInfo.append("Content-Type: " + fileType);
                headerInfo.append("\r\n");
                headerInfo.append("\r\n");
                byte[] headerInfoBytes = headerInfo.toString().getBytes("UTF-8");
                outputStream.write(headerInfoBytes);
                //文件内容
                FileInputStream fos = new FileInputStream(file);
                BufferedInputStream bfi = new BufferedInputStream(fos);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = bfi.read(buffer,0,buffer.length)) != -1 ){
                    outputStream.write(buffer, 0, length);
                }

                //写尾部信息
                StringBuilder footerInfo = new StringBuilder();
                headerInfo.append("\r\n");
                footerInfo.append("--");
                headerInfo.append("BOUNDARY");
                if (isLast) {
                    footerInfo.append("--");
                    headerInfo.append("\r\n");
                }
                headerInfo.append("\r\n");
                outputStream.write(footerInfo.toString().getBytes("UTF-8"));
            }

        }).start();
    }


    /**
     * get 方式下载文件
     * @param view
     *
     * 接口：
     * /download/{fileName}
     *
     * fileName的取值为：[0,16]
     */
    public void downLoadFile(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileOutputStream fileOutputStream = null;
                InputStream inputStream = null;
                try {
                    URL url = new URL(BASE_URL + "/download/10");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(10000);
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Accept", "*/*");
                    connection.setRequestProperty("connection", "keep-alive");
                    connection.setRequestProperty("Accept-Language", "zh-CN,zh");
                    connection.connect();

                    int responseCode = connection.getResponseCode();
                    Log.d(TAG,"responseCode ===> " + responseCode);
                    if (responseCode == HttpURLConnection.HTTP_OK) {
//                        //获取头部信息
//                        Map<String, List<String>> headerFields = connection.getHeaderFields();
//                        for (Map.Entry<String, List<String>> stringListEntry : headerFields.entrySet()) {
//                            Log.d(TAG, stringListEntry.getKey() + " ===> " + stringListEntry.getValue());
//                        }
//
//                        //获取body信息
//                        List<String> strings = headerFields.get("Content-disposition");
//                        for (String string : strings) {
//                            Log.d(TAG, "string ===> " + string);
//                        }

                        String headerField = connection.getHeaderField("Content-disposition");
                        Log.d(TAG, "headerField ===> " + headerField);
                        //获取文件名称
//                        int index = headerField.indexOf("filename=");
//                        String fileName = headerField.substring(index + "filename=".length());
//                        Log.d(TAG, "fileName ===> " + fileName);
                        String fileName = headerField.replace("attachment; filename=", "");
                        Log.d(TAG, "fileName ===> " + fileName);

                        //设置图片保存路径
                        File picFile = RequestTestActivity.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                        Log.d(TAG, "picFile ===> " + picFile);
                        if (!picFile.exists()) {
                            picFile.mkdirs();
                        }
                        File file = new File(picFile + File.separator + fileName);
                        if (!file.exists()) {
                            file.createNewFile();
                        }

                        fileOutputStream = new FileOutputStream(file);
                        inputStream = connection.getInputStream();
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = inputStream.read(buffer, 0, buffer.length)) != -1) {
                            //写文件到本地
                            fileOutputStream.write(buffer, 0, length);
                        }
                        fileOutputStream.flush();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    IOUtils.ioClose(fileOutputStream);
                    IOUtils.ioClose(inputStream);
                }
            }
        }).start();
    }
}