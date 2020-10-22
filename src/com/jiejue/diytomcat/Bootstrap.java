package com.jiejue.diytomcat;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.NetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.LogFactory;
import cn.hutool.system.SystemUtil;
import com.jiejue.diytomcat.catalina.Context;
import com.jiejue.diytomcat.http.Request;
import com.jiejue.diytomcat.http.Response;
import com.jiejue.diytomcat.util.Constant;
import com.jiejue.diytomcat.util.ThreadPoolUtil;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Struct;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Bootstrap {

    //声明一个contextMap用于存放路径和Context映射
    public static Map<String, Context> contextMap = new HashMap<>();


    public static void main(String[] args) {



        try {

            logJVM();

            scanContextsOnWebAppsFolder();

            //本服务器使用的端口号是18080
            int port = 18080;

            //用来判断18080端口是否被占用，如果被占用，返回false，不是则是true
//            if(!NetUtil.isUsableLocalPort(port)){
//                System.out.println(port + "端口已经被占用，请检查！");
//                return;
//            }

            //在端口18080上启动ServerScoket
            //服务器和浏览器是通过Socket进行通信的，所以这里需要启动一个ServerScoket
            ServerSocket ss = new ServerSocket(port);

            //while循环用于，处理掉一个socket请求后，再处理下一个socket请求
            while(true){
                //表示收到一个浏览器客户端请求
                Socket s = ss.accept();

                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Request request = new Request(s);

                            System.out.println("浏览器的输入信息：\r\n" + request.getRequestString());
                            System.out.println("uri:" + request.getUri());


                            Response response = new Response();

                            //首先判断uri是否为空，如果为空就不处理
                            String uri = request.getUri();
                            if (null == uri){
                                return;
                            }
                            System.out.println(uri);
                            Context context = request.getContext();
                            if ("/".equals(uri)){
                                String html = "HELLO DIY tomcat";
                                response.getWriter().println(html);
                            }
                            else {
                                //接着处理文件，首先取出文件名，比如访问的是/a.html 那么文件名是a.html
                                String fileName = StrUtil.removePrefix(uri, "/");
                                //获取对应的文件对象file
                                File file = FileUtil.file(context.getDocBase(), fileName);
                                if (file.exists()){
                                    //如果文件存在，那么获取内容通过response.getWriter进行打印
                                    String fileContent = FileUtil.readUtf8String(file);
                                    response.getWriter().println(fileContent);

                                    if (fileName.equals("timeConsume.html")){
                                        ThreadUtil.sleep(1000);
                                    }
                                }
                                else {
                                    //如果文件不存在，那么打印File Not Found
                                    response.getWriter().println("File Not Found");
                                }
                            }

                                handle200(s, response);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                };

                ThreadPoolUtil.run(r);
            }

        }catch(IOException e){
            LogFactory.get().error(e);
            e.printStackTrace();
        }
    }

    //创建scanContextsOnWebAppsFolder方法用于扫描webapps文件夹下的目录，对这些目录调用loadContext进行加载
    private static void scanContextsOnWebAppsFolder(){
        File[] folders = Constant.webappsFolder.listFiles();
        for (File folder : folders) {

            if(!folder.isDirectory()){
                continue;
            }
            loadContext(folder);

        }
    }

    //加载这个目录位Context对象
    private static void loadContext(File folder){

        String path = folder.getName();
        //如果是ROOT，那么path就是"/"  如果是a 那么path就是"/a" 然后根据path和他们所处于的路径创建Context对象
        if("ROOT".equals(path)){
            path = "/";
        }
        else{
            path = "/" + path;
        }
        String docBase = folder.getAbsolutePath();
        Context context = new Context(path, docBase);
        //把这些对象放进contextMap用于后续使用
        contextMap.put(context.getPath(), context);

    }

    private static void logJVM(){
        Map<String, String> infos = new LinkedHashMap<>();
        infos.put("Server version", "JieJue DiyTomcat/1.01");
        infos.put("Server built", "2020-10-21 23:20:00");
        infos.put("Server Number", "1.0.1");
        infos.put("OS Name\t", SystemUtil.get("os.name"));
        infos.put("OS Version", SystemUtil.get("os.version"));
        infos.put("Architecture", SystemUtil.get("os.arch"));
        infos.put("Java Home", SystemUtil.get("java.home"));
        infos.put("JVM Version", SystemUtil.get("java.runtime.version"));
        infos.put("JVM Vendor", SystemUtil.get("java.vm.specification.vendor"));


        Set<String> keys = infos.keySet();
        for (String key : keys) {
            LogFactory.get().info(key + ":\t\t" + infos.get(key));
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
