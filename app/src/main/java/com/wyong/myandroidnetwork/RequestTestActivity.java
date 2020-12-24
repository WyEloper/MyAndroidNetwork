package com.wyong.myandroidnetwork;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.wyong.myandroidnetwork.domain.CommentItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 服务器 接口地址：https://github.com/TrillGates/SOBAndroidMiniWeb
 * <p>
 * 真机调试地址：http://192.168.137.1:9102/get/param
 * 模拟器调试地址：http://10.0.2.2:9102/get/text
 */
public class RequestTestActivity extends AppCompatActivity {

    private static final String TAG = "PostTestActivity";
    private OutputStream mOutputStream = null;
    private InputStream mInputStream = null;

//    private static final String BASE_URL = "http://192.168.137.1:9102";
    private static final String BASE_URL = "http://10.0.2.2:9102";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_test);
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

}