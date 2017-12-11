package com.suramire.androidgame25;

import android.graphics.Bitmap;

import java.util.List;

/**
 * Created by Suramire on 2017/12/9.
 */

public class CommonBrick extends Brick {
    private boolean canBroken;

    public boolean isCanBroken() {
        return canBroken;
    }

    public void setCanBroken(boolean canBroken) {
        this.canBroken = canBroken;
    }

    public CommonBrick(int width, int height, List<Bitmap> bitmaps) {
        super(width, height, bitmaps);
    }

    @Override
    public void logic() {
        if(isCanBroken()){
            if(isJumping()){
                move(0,speedY++);
                if(speedY>4){
                    setJumping(false);
                    setmVisiable(false);
                }
            }

        }else{
            if(isJumping()){
                move(0,speedY++);
                if(speedY>4){
                    setJumping(false);
                }
            }
        }
    }
}
