package com.suramire.androidgame25.item;

import android.graphics.Bitmap;

import com.suramire.androidgame25.ItemSprite;

import java.util.List;


/**
 * Created by Suramire on 2017/11/29.
 */

public class Flower extends ItemSprite {

    public Flower(Bitmap bitmap) {
        super(bitmap);
    }

    public Flower(int width, int height, List<Bitmap> bitmaps) {
        super(width, height, bitmaps);
    }
}
