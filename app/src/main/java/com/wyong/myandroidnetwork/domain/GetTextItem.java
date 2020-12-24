package com.wyong.myandroidnetwork.domain;

import java.util.List;

public class GetTextItem {

    /**
     * success : true
     * code : 10000
     * message : 获取成功
     * data : [{"id":"1341668106737807360","title":"Android加载大图片，解决OOM问题","viewCount":220,"commentCount":46,"publishTime":"2020-12-23T08:52:58.291+0000","userName":"程序员拉大锯","cover":"/imgs/6.png"},{"id":"1341668106737807361","title":"Volley/Xutils对大图片处理算法源码分析","viewCount":135,"commentCount":89,"publishTime":"2020-12-23T08:52:58.291+0000","userName":"程序员拉大锯","cover":"/imgs/16.png"},{"id":"1341668106737807362","title":"Android开发网络安全配置","viewCount":283,"commentCount":52,"publishTime":"2020-12-23T08:52:58.291+0000","userName":"程序员拉大锯","cover":"/imgs/12.png"},{"id":"1341668106737807363","title":"Android开发网络编程，请求图片","viewCount":66,"commentCount":56,"publishTime":"2020-12-23T08:52:58.291+0000","userName":"程序员拉大锯","cover":"/imgs/14.png"},{"id":"1341668106737807364","title":"Intent页面跳转工具类分享","viewCount":296,"commentCount":91,"publishTime":"2020-12-23T08:52:58.291+0000","userName":"程序员拉大锯","cover":"/imgs/14.png"},{"id":"1341668106737807365","title":"阳光沙滩商城的API文档","viewCount":222,"commentCount":70,"publishTime":"2020-12-23T08:52:58.291+0000","userName":"程序员拉大锯","cover":"/imgs/11.png"},{"id":"1341668106737807366","title":"Android课程视频打包下载","viewCount":133,"commentCount":67,"publishTime":"2020-12-23T08:52:58.291+0000","userName":"程序员拉大锯","cover":"/imgs/8.png"},{"id":"1341668106737807367","title":"非常轻量级的gif录制软件","viewCount":238,"commentCount":41,"publishTime":"2020-12-23T08:52:58.291+0000","userName":"程序员拉大锯","cover":"/imgs/14.png"},{"id":"1341668106737807368","title":"Fiddler抓包工具，墙裂推荐，功能很强大很全的一个工具","viewCount":170,"commentCount":104,"publishTime":"2020-12-23T08:52:58.291+0000","userName":"程序员拉大锯","cover":"/imgs/9.png"},{"id":"1341668106737807369","title":"AndroidStudio奇淫技巧-代码管理","viewCount":261,"commentCount":54,"publishTime":"2020-12-23T08:52:58.291+0000","userName":"程序员拉大锯","cover":"/imgs/3.png"},{"id":"1341668106737807370","title":"OC和Swift混编","viewCount":309,"commentCount":67,"publishTime":"2020-12-23T08:52:58.291+0000","userName":"程序员拉大锯","cover":"/imgs/5.png"},{"id":"1341668106737807371","title":"最新的Android studio是不是没有Android Device Monitor","viewCount":61,"commentCount":93,"publishTime":"2020-12-23T08:52:58.291+0000","userName":"程序员拉大锯","cover":"/imgs/14.png"}]
     */

    public boolean success;
    public int code;
    public String message;
    public List<DataBean> data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 1341668106737807360
         * title : Android加载大图片，解决OOM问题
         * viewCount : 220
         * commentCount : 46
         * publishTime : 2020-12-23T08:52:58.291+0000
         * userName : 程序员拉大锯
         * cover : /imgs/6.png
         */

        public String id;
        public String title;
        public int viewCount;
        public int commentCount;
        public String publishTime;
        public String userName;
        public String cover;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getViewCount() {
            return viewCount;
        }

        public void setViewCount(int viewCount) {
            this.viewCount = viewCount;
        }

        public int getCommentCount() {
            return commentCount;
        }

        public void setCommentCount(int commentCount) {
            this.commentCount = commentCount;
        }

        public String getPublishTime() {
            return publishTime;
        }

        public void setPublishTime(String publishTime) {
            this.publishTime = publishTime;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getCover() {
            return cover;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }
    }
}
