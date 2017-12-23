package com.suramire.androidgame25.enemy;

import android.graphics.Bitmap;

import java.util.List;

/**
 * 敌人类-板栗
 */

public class Chestunt extends Enemy {

    public Chestunt(int width, int height, List<Bitmap> bitmaps) {
        super(width, height, bitmaps);
    }

    @Override
    public void logic() {
        super.logic();
        if(isDead()){
            if(!isOverturn()){
                setFrameSequenceIndex(2);
            }
        }else if(isJumping()){
            //使敌人落地
            move(0,mSpeedY++);
            setFrameSequenceIndex(0);
        }else {
            if(delay1++>=7){
                nextFrame();
                delay1 =0;
                //循环跑动贴图
                if(getFrameSequenceIndex()>=2){
                    setFrameSequenceIndex(0);

                }
            }
            if(isMirror()){
                move(2, 0);
            }else{
                move(-2, 0);
            }

        }
    }
}
