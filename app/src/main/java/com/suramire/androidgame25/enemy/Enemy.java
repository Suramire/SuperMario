package com.suramire.androidgame25.enemy;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.suramire.androidgame25.Sprite;

import java.util.List;

/**
 * Created by Suramire on 2017/11/9.
 */

public class Enemy extends Sprite {
	protected int delay;
    protected int delay1;
    protected int delay2;

    public Enemy(int width, int height, List<Bitmap> bitmaps) {
        super(width, height, bitmaps);
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
