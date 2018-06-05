package com.mmall.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Administrator
 */
@Slf4j
public class CookieUtil {

    private final static String COOKIE_DOMAIN = ".happymmall.com";//一级域名
    private final static String COOKIE_NAME = "mmall_login_token";//这个名字要种到客户端浏览器上


    public static String readLoginToken(HttpServletRequest request){
        Cookie[] cks = request.getCookies();
        if(cks != null){
            for(Cookie ck : cks){
                log.info("read cookieName:{},cookieValue:{}",ck.getName(),ck.getValue());
                if(StringUtils.equals(ck.getName(),COOKIE_NAME)){//ck.getName()为空时会抛异常，这样不会抛异常
                    log.info("return cookieName:{},cookieValue:{}",ck.getName(),ck.getValue());
                    return ck.getValue();
                }
            }
        }
        return null;
    }

    //X:domain=".happymmall.com"  下面请求都能拿到
    //a:A.happymmall.com            cookie:domain=A.happymmall.com;path="/"  a拿不到b c d e
    //b:B.happymmall.com            cookie:domain=B.happymmall.com;path="/"  b拿不到a c d e
    //c:A.happymmall.com/test/cc    cookie:domain=A.happymmall.com;path="/test/cc" 能拿到a e  拿不到b d
    //d:A.happymmall.com/test/dd    cookie:domain=A.happymmall.com;path="/test/dd"  能拿到a e  拿不到b c
    //e:A.happymmall.com/test       cookie:domain=A.happymmall.com;path="/test"  拿不到c d

    public static void writeLoginToken(HttpServletResponse response,String token){
        Cookie ck = new Cookie(COOKIE_NAME,token);
        ck.setDomain(COOKIE_DOMAIN);//主要用来在两个不同名称但是后缀相同的网站地址上.这样两个网站就能使用同一个cookie了，可以设置多个域的方法
        ck.setPath("/");//代表设置在根目录
        ck.setHttpOnly(true);//防止脚本共计带来的信息泄露风险，不许通过脚本访问cookie，tomcat6没有这个方法
        //单位是秒。
        //如果这个maxage不设置的话，cookie就不会写入硬盘，而是写在内存。只在当前页面有效。
        ck.setMaxAge(60 * 60 * 24 * 365);//，单位秒  如果是-1，代表永久
        log.info("write cookieName:{},cookieValue:{}",ck.getName(),ck.getValue());
        response.addCookie(ck);
    }


    //登出时调用
    public static void delLoginToken(HttpServletRequest request,HttpServletResponse response){
        Cookie[] cks = request.getCookies();
        if(cks != null){
            for(Cookie ck : cks){
                if(StringUtils.equals(ck.getName(),COOKIE_NAME)){//ck.getName()为空时会抛异常，这样不会抛异常
                    ck.setDomain(COOKIE_DOMAIN);
                    ck.setPath("/");
                    ck.setMaxAge(0);//设置成0，代表删除此cookie。
                    log.info("del cookieName:{},cookieValue:{}",ck.getName(),ck.getValue());
                    response.addCookie(ck);
                    return;
                }
            }
        }
    }







}
