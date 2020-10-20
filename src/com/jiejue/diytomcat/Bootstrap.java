package com.jiejue.diytomcat;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.NetUtil;
import cn.hutool.core.util.StrUtil;
import com.jiejue.diytomcat.http.Request;
import com.jiejue.diytomcat.http.Response;
import com.jiejue.diytomcat.util.Constant;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;

public class Bootstrap {

    public static void main(String[] args) {
        try {
            //本服务器使用的端口号是18080
            int port = 18080;

            //用来判断18080端口是否被占用，如果被占用，返回false，不是则是true
            if(!NetUtil.isUsableLocalPort(port)){
                System.out.println(port + "端口已经被占用，请检查！");
                return;
            }

            //在端口18080上启动ServerScoket
            //服务器和浏览器是通过Socket进行通信的，所以这里需要启动一个ServerScoket
            ServerSocket ss = new ServerSocket(port);

            //while循环用于，处理掉一个socket请求后，再处理下一个socket请求
            while(true){
                //表示收到一个浏览器客户端请求
                Socket s = ss.accept();

                Request request = new Request(s);
//                //打开输入流，准备接受客户端的请求
//                InputStream is = s.getInputStream();
                //准备一个长度是1024的字节数组，把浏览器的信息读出来放进去，浏览器提交的可能短语或者长于1024，这种做法后期有待改进
//                int bufferSize = 1024;
//                byte[] buffer = new byte[bufferSize];
//                is.read(buffer);
                //把字节数组转换成字符串，并且打印出来
//                String requestString = new String(buffer, "utf-8");
                System.out.println("浏览器的输入信息：\r\n" + request.getRequestString());
                System.out.println("uri:" + request.getUri());
                //打开输出流，准备给客户端输出信息
//                OutputStream os = s.getOutputStream();
                /*
                HTTP请求头固定格式：
                    ①请求方法 + 空格 + URL +空格+ 协议版本 +return && next
                    ②头部字段名称：值+return && next
                    ③return && next
                HTTP响应头固定格式：
                    ①协议版本 + 空格 +状态码+空格+状态码描述+return && next
                    ②头部字段名称：值+return && next
                    ③return && next
                */
//                String response_head = "HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n\r\n";
//                String responseString = "HELLO DIY tomcat";
//                responseString = response_head + responseString;

                //将字符串转成字节数组发出去
//                os.write(responseString.getBytes());
//                os.flush();

                Response response = new Response();
                String html = "HELLO DIY tomcat";
                response.getWriter().println(html);

                handle200(s, response);


                //关闭客户端对应的socket
                s.close();
            }

        }catch(IOException e){

            e.printStackTrace();
        }
    }

    private static void handle200(Socket s, Response response) throws IOException {
        String contentType = response.getContentType();
        String headText = Constant.response_head_202;
        headText = StrUtil.format(headText, contentType);
        byte[] head = headText.getBytes();

        byte[] body = response.getBody();

        byte[] responseBytes = new byte[head.length + body.length];
        ArrayUtil.copy(head, 0, responseBytes, 0, head.length);
        ArrayUtil.copy(body, 0, responseBytes, head.length, body.length);

        OutputStream os = s.getOutputStream();
        os.write(responseBytes);
        s.close();

    }
}
