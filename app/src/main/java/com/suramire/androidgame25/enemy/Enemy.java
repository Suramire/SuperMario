package com.suramire.androidgame25.enemy;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.suramire.androidgame25.Sprite;
import com.suramire.androidgame25.TiledLayer;
import com.suramire.androidgame25.enums.Site;

import java.util.List;

/**
 * Created by Suramire on 2017/11/9.
 */

public class Enemy extends Sprite {

    //region Fields
    protected boolean isMirror;//是否翻转
    private boolean isRunning;//是否跑动
    private boolean isJumping;//是否跳跃
    private boolean isDead;//是否死亡
	protected int delay;
    public int speedY;
    private long delay2;
    //endregion

    //region Getter & Setter
    public boolean isMirror() {
        return isMirror;
    }

    public void setMirror(boolean mirror) {
        isMirror = mirror;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public boolean isJumping() {
        return isJumping;
    }

    public void setJumping(boolean jumping) {
        isJumping = jumping;
    }

    public boolean isDead() {
        return isDead;
    }

    public void setDead(boolean dead) {
        isDead = dead;
    }
    //endregion

    public Enemy(int width, int height, List<Bitmap> bitmaps) {
        super(width, height, bitmaps);
    }

    @Override
    public void logic() {
        if(isDead()){
        	if(delay++==25){
        		setmVisiable(false);
        	}else{
        		 setFrameSequenceIndex(2);
        	}
        }else if(isJumping()){
            //使敌人落地
            move(0,speedY++);
            setFrameSequenceIndex(0);
        }else {
            if(delay2++>=7){
                nextFrame();
                delay2 =0;
                //循环跑动贴图
                if(getFrameSequenceIndex()>=2){
                    setFrameSequenceIndex(0);

                }
            }



            if(isMirror){
            	move(-2, 0);
            }else{
            	move(2, 0);
            }
            
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if(isMirror()){
            canvas.save();
            //翻转画布 相当于翻转人物
            canvas.scale(-1,1,getX()+getWidth()/2,getY()+getHeight()/2);
            super.draw(canvas);
            canvas.restore();
        }else{
            super.draw(canvas);
        }
    }

    @Override
    protected void outOfBounds() {
    	//在超出左边界 以及掉入坑里的是否表示为不可见
        if(getX()<-getWidth() || getY()>400){
        	setmVisiable(false);
        }
    }

    /**
     * 碰撞检测
     * @param tiledLayer
     * @param site
     * @return
     */
    public boolean siteCollisionWith(TiledLayer tiledLayer, Site site){
        int siteX = 0;
        int siteY = 0;
        switch (site){
            case 上左:{
                siteX = getX() + getWidth() / 4;
                siteY = getY();
            }break;
            case 上中:{
                siteX = getX() + getWidth() / 2;
                siteY = getY();
            }break;
            case 上右:{
                siteX = getX() + 3 * getWidth() / 4;
                siteY = getY();
            }break;

            case 下左:{
                siteX = getX() + getWidth() / 4;
                siteY = getY() + getHeight();
            }break;
            case 下中:{
                siteX = getX() + getWidth() / 2;
                siteY = getY() + getHeight();
            }break;
            case 下右:{
                siteX = getX() + 3 * getWidth() / 4;
                siteY = getY() + getHeight();
            }break;

            case 左上:{
                siteX = getX();
                siteY = getY() + getHeight() / 4;
            }break;
            case 左中:{
                siteX = getX();
                siteY = getY() + getHeight() / 2;
            }break;
            case 左下:{
                siteX = getX();
                siteY = getY() + 3 * getHeight() / 4;
            }break;

            case 右上:{
                siteX = getX() + getWidth();
                siteY = getY() + getHeight() / 4;
            }break;
            case 右中:{
                siteX = getX() + getWidth();
                siteY = getY() + getHeight() / 2;
            }break;
            case 右下:{
                siteX = getX() + getWidth();
                siteY = getY() + 3 * getHeight() / 4;
            }break;
        }
        //在地图上的坐标
        int mapX = siteX - tiledLayer.getX();
        int mapY = siteY - tiledLayer.getY();
        //在地图上的对应行列
        int col = mapX / tiledLayer.getWidth();
        int row = mapY / tiledLayer.getHeight();
        //超出边界
        if(col>tiledLayer.getCols()-1|| row>tiledLayer.getRows()-1){
            return true;
        }
        //存在障碍物
        if(tiledLayer.getTiledCell(col,row)!=0){
            return true;
        }
        return false;
    }
}
