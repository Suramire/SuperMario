package com.suramire.androidgame25.item;

import android.graphics.Bitmap;

import com.suramire.androidgame25.MySprite;
import com.suramire.androidgame25.Sprite;

import java.util.List;

/**
 * Created by Suramire on 2017/11/25.
 */

public class Coin extends MySprite {
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
