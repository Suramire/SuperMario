package com.suramire.androidgame25.enemy;

import android.graphics.Bitmap;

import java.util.List;

/**
 * Created by Suramire on 2017/12/11.
 */

public class Turtle extends Enemy {

    private long delay2;
    private boolean canFly;
    private long delay00;

    public boolean isCanFly() {
        return canFly;
    }

    public void setCanFly(boolean canFly) {
        this.canFly = canFly;
    }

    public Turtle(int width, int height, List<Bitmap> bitmaps) {
        super(width, height, bitmaps);
    }

    @Override
    public void logic() {
        if (!isDead()) {
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
                if(delay2++>=7){
                    nextFrame();
                    delay2 =0;
                    //循环跑动贴图
                    if(getFrameSequenceIndex()>=2){
                        setFrameSequenceIndex(0);

                    }
                }

            }else{
                if(delay2++>=7){
                    nextFrame();
                    delay2 =0;
                    //循环跑动贴图
                    if(getFrameSequenceIndex()<5){
                        setFrameSequenceIndex(5);
                    }

                }

            }
        }else{
            if(delay00++>20){
                setVisiable(false);
                delay00=0;
            }
        }
    }

}
