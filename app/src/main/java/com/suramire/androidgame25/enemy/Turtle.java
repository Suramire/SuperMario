package com.suramire.androidgame25.enemy;

import android.graphics.Bitmap;
import android.os.SystemClock;

import java.util.List;

/**
 * 敌人类-乌龟
 */

public class Turtle extends Enemy {

    private final Thread zeroDamagThread;
    private boolean canFly;
    private boolean isZeroDamage;//标志是否处于免伤状态
    private int zeroDamageTime;//免伤时间
    private boolean isZeroDamageThreadStarted;//标志进程是否开始

    public boolean isCanFly() {
        return canFly;
    }

    public void setCanFly(boolean canFly) {
        this.canFly = canFly;
    }

    public void setZeroDamage(boolean zeroDamage) {
        if(zeroDamage){
            zeroDamageTime = 2;
        }
        isZeroDamage = zeroDamage;
    }

    public boolean isZeroDamage() {
        return isZeroDamage;
    }

    public Turtle(int width, int height, List<Bitmap> bitmaps) {
        super(width, height, bitmaps);
        zeroDamagThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (zeroDamageTime >= 1) {
                    SystemClock.sleep(1000);
                    zeroDamageTime--;
                }

            }
        });
    }

    @Override
    public void logic() {
        super.logic();
        if (!isDead()) {
            if(isZeroDamage){
                if(!isZeroDamageThreadStarted){
                    new Thread(zeroDamagThread).start();
                    isZeroDamageThreadStarted = true;
                }
                if(zeroDamageTime<=0){
                    isZeroDamage = false;
                    isZeroDamageThreadStarted = false;
                }
            }

            if(delay++>1){
                if(isJumping()){
                    move(0,mSpeedY++);
                }
                if (!isMirror()) {
                    move(-2, 0);
                } else {
                    move(2, 0);
                }
                delay = 0;
            }

            if(!isCanFly()){
                if(delay1++>=7){
                    nextFrame();
                    delay1 =0;
                    //循环跑动贴图
                    if(getFrameSequenceIndex()>=2){
                        setFrameSequenceIndex(0);

                    }
                }

            }else{
                if(delay1++>=7){
                    nextFrame();
                    delay1 =0;
                    //循环跑动贴图
                    if(getFrameSequenceIndex()<5){
                        setFrameSequenceIndex(5);
                    }

                }

            }
        }else{
            setFrameSequenceIndex(2);
        }
    }

}
