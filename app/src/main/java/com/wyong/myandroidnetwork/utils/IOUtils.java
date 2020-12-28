package com.wyong.myandroidnetwork.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * 关闭IO流的工具类
 */
public class IOUtils {

    public static void closeIO(Closeable closeable){
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
