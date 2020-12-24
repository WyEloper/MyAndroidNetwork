package com.wyong.myandroidnetwork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.wyong.myandroidnetwork.adapters.GetResultListAdapter;
import com.wyong.myandroidnetwork.domain.GetTextItem;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 使用Java的API发起网络请求  HttpURLConnection
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private RecyclerView mRecyclerView;
    private GetResultListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        mRecyclerView = findViewById(R.id.result_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //添加分割线
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = 5;
                outRect.bottom = 5;
            }
        });
        mAdapter = new GetResultListAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }

    public void loadJson(View view) {
        //现实开发中不能这么写
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // api 27 以后不能直接使用 http 协议
                    //URL url = new URL("http://10.0.2.2:9102/get/text");//用模拟器的时候用这个地址
                    URL url = new URL("http://192.168.137.1:9102/get/text");//用真机的时候用这个地址
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(10000);
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Accept", "*/*");
                    connection.setRequestProperty("connection", "keep-alive");
                    connection.setRequestProperty("Accept-Language", "zh-CN,zh");
                    connection.connect();
                    //结果码
                    int responseCode = connection.getResponseCode();
                    if (responseCode == 200) {
                        //获取头部信息
                        Map<String, List<String>> headerFields = connection.getHeaderFields();
                        Set<Map.Entry<String, List<String>>> entries = headerFields.entrySet();
                        for (Map.Entry<String, List<String>> entry : entries) {
                            Log.d(TAG, entry.getKey() + " == " + entry.getValue());
                        }

                        //获取body信息
//                        Object content = connection.getContent();
//                        Log.d(TAG, "content = " + content);
                        InputStream inputStream = connection.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        String json = bufferedReader.readLine();
                        Log.d(TAG, "json ===> " + json);

                        Gson gson = new Gson();
                        GetTextItem getTextItem = gson.fromJson(json, GetTextItem.class);
                        updateUI(getTextItem);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void updateUI(GetTextItem getTextItem) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //getTextItem
                mAdapter.setData(getTextItem);
            }
        });
    }
}