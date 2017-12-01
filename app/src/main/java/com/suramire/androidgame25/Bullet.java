package com.suramire.androidgame25;

import android.graphics.Bitmap;




/**
 * Created by Suramire on 2017/11/30.
 */

public class Bullet extends MySprite {

    public Bullet(Bitmap bitmap) {
        super(bitmap);
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
            switch (getDirection()){
                //道具往左移动
                case 左:{
                    if(isJumping()){
                        move(-8,speedY++);
                    }else{
                        move(-8,0);
                    }

                }break;
                //道具往右移动
                case 右:{
                    if(isJumping()){
                        move(8,speedY++);
                    }else{
                        move(8,0);

                    }
                }break;
            }
        }
    }
}
