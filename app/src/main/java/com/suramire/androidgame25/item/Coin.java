package com.suramire.androidgame25.item;

import android.graphics.Bitmap;

import java.util.List;

/**
 * Created by Suramire on 2017/11/25.
 */

public class Coin extends ItemSprite {
    private int delay;
    public Coin(int width, int height, List<Bitmap> bitmaps) {
        super(width, height, bitmaps);
        setFrameSequenceIndex(3);
    }

    @Override
    public void logic() {
        if(isVisiable()){
            if(delay++>1){
                nextFrame();
                delay=0;
            }

        }
    }
}
