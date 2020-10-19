package com.jiejue.diytomcat.test;

import cn.hutool.core.util.NetUtil;
import cn.hutool.core.util.StrUtil;
import com.jiejue.diytomcat.util.MiniBrowser;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;


public class TestTomcat {

    //预先定义端口和ip地址，方便修改
    private static int port = 18080;
    private static String ip = "127.0.0.1";


    //在测试之前会先检查diytomcat是否已经启动了，如果未启动，就不用做测试了，并且给出提示信息
    @BeforeClass
    public static void beforeClass(){
        //所有cesium开始前看diytomcat是否已经启动
        if(NetUtil.isUsableLocalPort(port)){
            System.err.println("请先启动 位于端口：" + port + " 的diy tomcat，否则无法进行单元测试");
            System.exit(1);
        }
        else{
            System.out.println("检测到diy tomcat已经启动，开始进行单元测试");
        }
    }

    //测试方法，用于访问：http://127.0.0.1:18080/ 并验证返回值是否是“HELLO DIY tomcat”，如果不是就会测试失败
    @Test
    public void testHelloTomcat(){
        String html = getContentString("/");
        Assert.assertEquals(html, "HELLO DIY tomcat");
    }


    //工具方法，用来获取网页返回
    private String getContentString(String uri){
        String url = StrUtil.format("http://{}:{}{}", ip, port, uri);
        String content = MiniBrowser.getContentString(url);
        return content;
    }
}