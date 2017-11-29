package com.suramire.androidgame25;

import android.graphics.Bitmap;

import com.suramire.androidgame25.util.L;

import java.util.List;

/**
 * 砖块类
 */

public class Brick extends Sprite {

    private Mushroom mushroom;



    //标志道具是否已显示
    private  boolean hasItem;

    //标志砖块是否是上下移动状态
    private boolean isJumping;
    //上下移动的速度
    private int speedY;

    public boolean isHasItem() {
        return hasItem;
    }

    public void setHasItem(boolean hasItem) {
        this.hasItem = hasItem;
    }

    public Mushroom getMushroom() {
        return mushroom;
    }

    public void setMushroom(Mushroom mushroom) {
        this.mushroom = mushroom;
    }

    public boolean isJumping() {
        return isJumping;
    }

    public void setJumping(boolean jumping) {
        isJumping = jumping;
    }

    public int getSpeedY() {
        return speedY;
    }

    public void setSpeedY(int speedY) {
        this.speedY = speedY;
    }

    public Brick(int width, int height, List<Bitmap> bitmaps) {
        super(width, height, bitmaps);
    }

    @Override
    public void logic() {
        if(isJumping()){
            if(hasItem){
                // TODO: 2017/11/29 这里显示出道具

                mushroom.setVisiable(true);
                mushroom.setPosition(getX(),getY()-getHeight());
                hasItem = false;
            }
            move(0,speedY++);
            if(speedY>4){
                setJumping(false);
            }
        }
    }

    /**
     * 为砖块添加道具
     * @param e 是否添加标志位
     * @param bitmap 道具图片（单帧方式）
     */
    public void createItem(boolean e,Bitmap bitmap){
        if(e){
            this.mushroom = new Mushroom(bitmap);
            this.mushroom.setDirection(Site.上右);
            hasItem = true;
        }
    }
}
