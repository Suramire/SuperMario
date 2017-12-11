package com.suramire.androidgame25.item;

import android.graphics.Bitmap;

import com.suramire.androidgame25.ItemSprite;

import java.util.List;

/**
 * Created by Suramire on 2017/11/25.
 */

public class Coin extends ItemSprite {
    public Coin(int width, int height, List<Bitmap> bitmaps) {
        super(width, height, bitmaps);
        setFrameSequenceIndex(3);
    }

    @Override
    public void logic() {
        if(ismVisiable()){
            nextFrame();
        }
    }
}
