package com.suramire.androidgame25.item;

import android.graphics.Bitmap;

import com.suramire.androidgame25.ItemSprite;

import java.util.List;

/**
 * Created by Suramire on 2017/12/1.
 */

public class Star extends ItemSprite {
    public Star(Bitmap bitmap) {
        super(bitmap);
    }

    public Star(int width, int height, List<Bitmap> bitmaps) {
        super(width, height, bitmaps);
    }
}
