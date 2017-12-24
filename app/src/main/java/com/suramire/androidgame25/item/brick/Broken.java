package com.suramire.androidgame25.item.brick;

import android.graphics.Bitmap;

import com.suramire.androidgame25.Sprite;

import java.util.List;

/**
 * Created by Suramire on 2017/12/24.
 */

public class Broken extends Sprite {
    public Broken(int width, int height, List<Bitmap> bitmaps) {
        super(width, height, bitmaps);
    }

    @Override
    public void logic() {
        if(isVisiable()){
            nextFrame();
            move(0,-3);
            if(getFrameSequenceIndex()==0){
                setVisiable(false);
            }
        }
    }
}
