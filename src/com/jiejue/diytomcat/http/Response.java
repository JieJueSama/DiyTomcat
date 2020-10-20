package com.jiejue.diytomcat.http;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

public class Response {

    //用于存放返回的html文本
    private StringWriter stringWriter;
    //用于提供一个getWriter()方法，这样就可以像HttpServletResponse那样写成response.getWriter.println();这种风格了
    private PrintWriter writer;
    private String contentType;

    public Response(){
        this.stringWriter = new StringWriter();
        this.writer = new PrintWriter(stringWriter);
        this.contentType = "text/html";
    }

    public String getContentType(){
        return contentType;
    }

    public PrintWriter getWriter(){
        return writer;
    }

    public byte[] getBody() throws UnsupportedEncodingException {
        String content = stringWriter.toString();
        byte[] body = content.getBytes("utf-8");
        return body;
    }
}
