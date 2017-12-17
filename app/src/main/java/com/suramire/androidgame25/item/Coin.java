package com.suramire.androidgame25.item;

import android.graphics.Bitmap;

import java.util.List;


public class Coin extends ItemSprite {
    private int delay;
    public Coin(int width, int height, List<Bitmap> bitmaps) {
        super(width, height, bitmaps);
    }

    @Override
    public void logic() {
        if(isVisiable()){
            if(delay++>5){
                nextFrame();
                delay=0;
            }

        }
    }
}
