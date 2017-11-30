package com.suramire.androidgame25;

import android.graphics.Bitmap;

import java.util.List;

/**
 * Created by Suramire on 2017/11/25.
 */

public class Coin extends Sprite {
    public Coin(int width, int height, List<Bitmap> bitmaps) {
        super(width, height, bitmaps);
        setmFrameSequenceIndex(3);
    }

    @Override
    public void logic() {
        if(isVisiable()){
            nextFrame();
        }
    }
}
