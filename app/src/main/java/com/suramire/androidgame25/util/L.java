package com.suramire.androidgame25.util;

/**
 * Created by Suramire on 2017/6/26.
 * Log统一管理类
 */

import android.util.Log;

public class L
{

    private L()
    {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static boolean isDebug = true;// 是否需要打印bug，可以在application的onCreate函数里面初始化
    private static final String TAG = "aaa";

    // 下面四个是默认tag的函数
    public static void i(String msg)
    {
        if (isDebug)
            try{
                Log.i(TAG, msg);
            }catch (RuntimeException e){
                System.out.println(msg);
            }

    }

    public static void d(String msg)
    {
        if (isDebug)
            try{
                Log.d(TAG, msg);
            }catch (RuntimeException e){
                System.out.println(msg);
            }
    }

    public static void e(String msg)
    {
        if (isDebug)
            try{
                Log.e(TAG, msg);
            }catch (RuntimeException e){
                System.out.println(msg);
            }
    }

    public static void v(String msg)
    {
        if (isDebug)
            try{
                Log.v(TAG, msg);
            }catch (RuntimeException e){
                System.out.println(msg);
            }
    }

    // 下面是传入自定义tag的函数
    public static void i(String tag, String msg)
    {
        if (isDebug)
            try{
                Log.i(tag, msg);
            }catch (RuntimeException e){
                System.out.println(msg);
            }
    }

    public static void d(String tag, String msg)
    {
        i(tag, msg);
    }

    public static void e(String tag, String msg)
    {
        i(tag, msg);
    }

    public static void v(String tag, String msg)
    {
        i(tag, msg);
    }
}