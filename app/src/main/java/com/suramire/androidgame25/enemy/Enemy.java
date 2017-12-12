package com.suramire.androidgame25.enemy;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.suramire.androidgame25.Sprite;

import java.util.List;

/**
 * Created by Suramire on 2017/11/9.
 */

public class Enemy extends Sprite {

    //region Fields

	protected int delay;

    private long delay2;
    //endregion



    public Enemy(int width, int height, List<Bitmap> bitmaps) {
        super(width, height, bitmaps);
    }

    @Override
    public void logic() {
        if(isDead()){
        	if(delay++>25){
        		setVisiable(false);
                delay=0;
        	}else{
        		 setFrameSequenceIndex(2);
        	}
        }else if(isJumping()){
            //使敌人落地
            move(0,mSpeedY++);
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
            if(isMirror()){
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
        	setVisiable(false);
        }
    }


}
