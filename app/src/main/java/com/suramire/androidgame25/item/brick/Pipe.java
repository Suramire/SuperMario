package com.suramire.androidgame25.item.brick;


import android.graphics.Bitmap;

import java.util.List;

/**
 * Created by Suramire on 2017/12/25.
 */

public class Pipe extends Brick {
    private boolean isTransfer;

    public boolean isTransfer() {
        return isTransfer;
    }

    public void setTransfer(boolean transfer) {
        isTransfer = transfer;
    }

    public Pipe(int width, int height, List<Bitmap> bitmaps) {
        super(width, height, bitmaps);
    }

    @Override
    public void logic() {
    }
}
