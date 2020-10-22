package com.jiejue.diytomcat.http;

import cn.hutool.core.util.StrUtil;
import com.jiejue.diytomcat.Bootstrap;
import com.jiejue.diytomcat.catalina.Context;
import com.jiejue.diytomcat.util.MiniBrowser;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.function.BooleanSupplier;

public class Request {

    private String requestString;
    private String uri;
    private Socket socket;
    private Context context;

    public Request(Socket socket) throws IOException {
        this.socket = socket;
        parseHttpRequest();
        if(StrUtil.isEmpty(requestString)){
            return;
        }
        parseUri();
    }

    private void parseContext(){
        String path = StrUtil.subBetween(uri, "/", "/");
        if(null == path){
            path = "/";
        }
        else {
            path = "/" + path;
        }
        context = Bootstrap.contextMap.get(path);
        if(null == context){
            context = Bootstrap.contextMap.get("/");
        }
    }

    //parseHttpRequest 用于解析 http请求字符串， 这里面就调用了 MiniBrowser里重构的 readBytes 方法。
    private void parseHttpRequest() throws IOException {
        InputStream is = this.socket.getInputStream();
        byte[] bytes = MiniBrowser.readBytes(is);
        requestString = new String(bytes, "utf-8");
    }

    //解析uri
    private void parseUri(){
        String temp;

        temp = StrUtil.subBetween(requestString, " ", " ");
        if(!StrUtil.contains(temp, '?')){
            uri = temp;
            return;
        }
        temp = StrUtil.subBefore(temp, '?', false);
        uri = temp;

    }

    public String getUri(){
        return uri;
    }

    public String getRequestString(){
        return requestString;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
