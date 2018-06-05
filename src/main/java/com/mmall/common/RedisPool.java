package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by Administrator
 */
public class RedisPool {
    private static JedisPool pool;//jedis连接池，static保证在tomcat启动时就加载进来，在静态代码块里初始化它
    private static Integer maxTotal = Integer.parseInt(PropertiesUtil.getProperty("redis.max.total","20")); //最大连接数
    private static Integer maxIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.max.idle","20"));//在jedispool中最大的idle状态(空闲的)的jedis实例的个数
    private static Integer minIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.min.idle","20"));//在jedispool中最小的idle状态(空闲的)的jedis实例的个数

    private static Boolean testOnBorrow = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.borrow","true"));//在borrow一个jedis实例的时候，是否要进行验证操作，如果赋值true。则得到的jedis实例肯定是可以用的。
    private static Boolean testOnReturn = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.return","true"));//在return一个jedis实例的时候，是否要进行验证操作，如果赋值true。则放回jedispool的jedis实例肯定是可以用的。

    private static String redisIp = PropertiesUtil.getProperty("redis1.ip");
    private static Integer redisPort = Integer.parseInt(PropertiesUtil.getProperty("redis1.port"));


    private static void initPool(){
        JedisPoolConfig config = new JedisPoolConfig();

        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);

        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);

        config.setBlockWhenExhausted(true);//连接耗尽的时候，是否阻塞，false会抛出异常，true阻塞直到超时。默认为true。

        pool = new JedisPool(config,redisIp,redisPort,1000*2);//单位毫秒
    }

    static{
        initPool();
    }

    public static Jedis getJedis(){//从连接池里拿出一个实例
        return pool.getResource();
    }



    //有程序认为判断是放回BrokenResource还是returnResource，testOnReturn就赋值为false，内部不用再次判断
    //因为pool是private的，要封装两个方法调用pool
    public static void returnBrokenResource(Jedis jedis){//坏连接放入BrokenResource
        pool.returnBrokenResource(jedis);//源码里已经判断了jedis为空
    }



    public static void returnResource(Jedis jedis){//把jedis放回连接池，
        pool.returnResource(jedis);
    }


    public static void main(String[] args) {
        Jedis jedis = pool.getResource();
        jedis.set("a","aaa");
        returnResource(jedis);

        pool.destroy();//临时调用，销毁连接池中的所有连接，因为业务代码会不断地重复使用连接，所以在业务代码里不销毁
        System.out.println("program is end");


    }







}

