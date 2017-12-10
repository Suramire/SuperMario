package com.suramire.androidgame25;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by Suramire on 2017/12/10.
 */

public class EnemyBullet extends Bullet {
    private boolean isMirror;//是否翻转

    public boolean isMirror() {
        return isMirror;
    }

    public void setMirror(boolean mirror) {
        isMirror = mirror;
    }

    public EnemyBullet(Bitmap bitmap) {
        super(bitmap);
    }

    @Override
    public void logic() {
        if(isVisiable()){
            switch (getDirection()){
                //道具往左移动
                case 左:{
                    move(-6,0);
                }break;
                //道具往右移动
                case 右:{
                    move(6,0);
                }break;
            }
        }
    }


    @Override
    public void draw(Canvas canvas) {
        if (isMirror()) {
            canvas.save();
            //翻转画布 相当于翻转人物
            canvas.scale(-1, 1, getX() + getWidth() / 2, getY() + getHeight() / 2);
            super.draw(canvas);

            canvas.restore();
        } else {

            super.draw(canvas);

        }
    }
}
