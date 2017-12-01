package com.suramire.androidgame25.item;

import android.graphics.Bitmap;

import com.suramire.androidgame25.MySprite;

import java.util.List;


/**
 * Created by Suramire on 2017/11/29.
 */

public class Mushroom extends MySprite {

    public Mushroom(Bitmap bitmap) {
        super(bitmap);
    }

    public Mushroom(int width, int height, List<Bitmap> bitmaps) {
        super(width, height, bitmaps);
    }
}
