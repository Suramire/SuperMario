package com.suramire.androidgame25.item;

import android.graphics.Bitmap;

import java.util.List;


/**
 * Created by Suramire on 2017/11/30.
 */

public class Bullet extends ItemSprite {

    private int delay;

    public Bullet(Bitmap bitmap) {
        super(bitmap);
    }

    public Bullet(int width, int height, List<Bitmap> bitmaps) {
        super(width, height, bitmaps);
    }

    @Override
    protected void outOfBounds() {
        //在超出左边界 以及掉入坑里的是否表示为不可见
        if(getX()<-getWidth() || getY()>400 ||getX()>800){
            setVisiable(false);
        }
    }

    @Override
    public void logic() {
        if(isVisiable()){
            nextFrame();
            if(isDead()){
                if(delay++>10){
                    setVisiable(false);
                }
            }

            if(isMirror()){
                if(isJumping()){
                    move(10,mSpeedY++);
                }else{
                    move(10,0);

                }
            }else{
                if(isJumping()){
                    move(-10,mSpeedY++);
                }else{
                    move(-10,0);
                }
            }
        }
    }
}
