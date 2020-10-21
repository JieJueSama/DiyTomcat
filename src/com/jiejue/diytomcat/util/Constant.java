package com.jiejue.diytomcat.util;

import cn.hutool.system.SystemUtil;

import java.io.File;

//常量工具类，用与存放响应的头信息的模板
public class Constant {

    //202响应的头信息
    public final static String response_head_202 = "HTTP/1.1 200 OK \r\n" + "Content-Type: {}\r\n\r\n";

    public final static File webappsFolder = new File(SystemUtil.get("user.dir"), "webapps");
    public final static File rootFolder = new File(webappsFolder, "ROOT");

}
