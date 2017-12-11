package com.suramire.androidgame25;

import android.graphics.Bitmap;

import com.suramire.androidgame25.enums.ItemType;
import com.suramire.androidgame25.enums.Site;
import com.suramire.androidgame25.item.Coin;
import com.suramire.androidgame25.item.Flower;
import com.suramire.androidgame25.item.Mushroom;
import com.suramire.androidgame25.item.Star;

import java.util.List;

/**
 * 砖块类
 */

public class Brick extends Sprite {
    //表示道具类型 枚举
    protected ItemType itemType;
    protected ItemSprite itemSprite;
    //标志道具是否已显示
    protected boolean hasItem;
    //标志砖块是否是上下移动状态
    protected boolean isJumping;
    //上下移动的速度
    protected int speedY;

    //region Getter&Setter
    public ItemType getItemType() {
        return itemType;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }
    public boolean isHasItem() {
        return hasItem;
    }

    public void setHasItem(boolean hasItem) {
        this.hasItem = hasItem;
    }

    public ItemSprite getItemSprite() {
        return itemSprite;
    }

    public void setItemSprite(Mushroom itemSprite) {
        this.itemSprite = itemSprite;
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
    //endregion

    public Brick(int width, int height, List<Bitmap> bitmaps) {
        super(width, height, bitmaps);
    }

    @Override
    public void logic() {
        if(isJumping()){
            if(hasItem){
                itemSprite.setmVisiable(true);
                itemSprite.setPosition(getX(),getY()-getHeight());
                hasItem = false;
            }
            move(0,speedY++);
            if(speedY>4){
                setJumping(false);
            }
        }
        if(!hasItem){
            setFrameSequenceIndex(4);
        }
    }

    /**
     * 为砖块添加道具
     * @param e 是否添加标志位
     * @param bitmap 道具图片（单帧方式）
     */
    public void createItem(boolean e,Bitmap bitmap,ItemType type){
        setItemType(type);
        if(e){
            switch (type){
                case Mushroom:{
                    //蘑菇默认往右移动
                    itemSprite = new Mushroom(bitmap);
                    this.itemSprite.setDirection(Site.上);
                }break;
                case Coin:{

                }break;
                case Flower:{
                    //花默认不移动
                    itemSprite = new Flower(bitmap);
                    this.itemSprite.setDirection(Site.上);
                }break;
                case Star:{
                    itemSprite = new Star(bitmap);
                    this.itemSprite.setDirection(Site.上);
                }break;
            }

            hasItem = true;
        }
    }

    /**
     * 为砖块添加道具
     * @param e 是否添加标志位
     * @param bitmaps 道具图片（多帧方式）
     */
    public void createItem(boolean e,List<Bitmap>  bitmaps,ItemType type){
        setItemType(type);
        if(e){
            switch (type){

                case Coin:{
                    itemSprite = new Coin(40,40,bitmaps);
                    this.itemSprite.setDirection(Site.上);
                }break;

            }

            hasItem = true;
        }
    }
}
