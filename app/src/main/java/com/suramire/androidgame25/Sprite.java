package com.suramire.androidgame25;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.List;

/**
 * Created by Suramire on 2017/10/9.
 */

public class Sprite {

    //region 字段
    private int mX;
    private int mY;
    private boolean visiable;
    private int mWidth;
    private int mHeight;
    private Bitmap mBitmap;
    private int mFrameNumber;
    private int[] mFrameX;
    private int[] mFrameY;
    private int[] mFrameSequence;//帧序列
	private int mFrameSequenceIndex;//帧序列的索引
    private Rect mDest;
    private Rect mSrc;
    private List<Bitmap> mBitmaps;

    //endregion

    //region Getter and Setter
    public int[] getmFrameSequence() {
		return mFrameSequence;
	}

	public void setmFrameSequence(int[] mFrameSequence) {
		this.mFrameSequence = mFrameSequence;
	}

	public int getmFrameSequenceIndex() {
		return mFrameSequenceIndex;
	}

	public void setmFrameSequenceIndex(int mFrameSequenceIndex) {
		this.mFrameSequenceIndex = mFrameSequenceIndex;
	}

    
    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    public int getX() {
        return mX;
    }

    public void setX(int x) {
        mX = x;
    }

    public int getY() {
        return mY;
    }

    public void setY(int y) {
        mY = y;
    }


    public int getWidth() {return mWidth;}

    public void setWidth(int width) {
        mWidth = width;
    }

    public int getHeight() {
        return mHeight;
    }

    public void setHeight(int height) {
        mHeight = height;
    }

    public boolean isVisiable() {
        return visiable;
    }

    public void setVisiable(boolean visiable) {
        this.visiable = visiable;
    }


    //endregion


    public Sprite(int width, int height, List<Bitmap> bitmaps) {
        super();
        mWidth = width;
        mHeight = height;
        mBitmaps = bitmaps;
        mFrameSequence = new int[mBitmaps.size()];
        for (int i = 0; i < mFrameSequence.length; i++) {
            mFrameSequence[i]=i;
        }
    }

    public Sprite(Bitmap bitmap) {
        this(bitmap, bitmap.getWidth(), bitmap.getHeight());
    }

    public Sprite(Bitmap bitmap, int width, int height) {
        super();
        this.mBitmap = bitmap;
        setHeight(height);
        setWidth(width);
        int w = bitmap.getWidth() / width;
        int h = bitmap.getHeight() / height;
        mFrameNumber = w * h;
        mFrameX = new int[mFrameNumber];
        mFrameY = new int[mFrameNumber];
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                mFrameX[i * w + j] = j * width;
                mFrameY[i * w + j] = i * height;
            }
        }
        mSrc = new Rect();
        mDest = new Rect();
        mFrameSequence = new int[mFrameNumber];
        for (int i = 0; i < mFrameSequence.length; i++) {
			mFrameSequence[i] = i;//为序列初始化值
			
		}
    }

    public void setPosition(int x, int y){
        this.mX = x;
        this.mY = y;
    }

    public void move(float x,float y){
        mY += y;
        mX += x;
        outOfBounds();
    }

    public void draw(Canvas canvas){
        if(isVisiable()){
            //采用切图方式
            if(mBitmap!=null){
                int x = mFrameX[mFrameSequence[mFrameSequenceIndex]];
                int y = mFrameY[mFrameSequence[mFrameSequenceIndex]];
                mSrc.set(x, y, x + getWidth(), y + getHeight());
                mDest.set(getX(),getY(),getX()+getWidth(),getY()+getHeight());
                canvas.drawBitmap(mBitmap, mSrc, mDest, null);
            }else{
                canvas.drawBitmap(mBitmaps.get(mFrameSequence[mFrameSequenceIndex]),
                        getX(),getY(),null);
            }


        }
    }

    public void logic(){
    }

    protected void outOfBounds() {

    }
    
    /*碰撞检测*/
    public boolean collisionWith(Sprite sprite){
        if (!isVisiable() || !sprite.isVisiable()) {
            return false;
        }
        if(getX()<sprite.getX()&& getX()+getWidth()<sprite.getX()){
    		return false;
    	}
    	if(sprite.getX()<getX()&& sprite.getX()+sprite.getWidth()<getX()){
    		return false;
    	}
    	if(getY()<sprite.getY()&& getY()+getHeight()<sprite.getY()){
    		return false;
    	}
    	if(sprite.getY()<getY()&& sprite.getY()+sprite.getHeight()<getY()){
    		return false;
    	}
    	return true;
    }

    public void nextFrame() {
    	mFrameSequenceIndex = (mFrameSequenceIndex + 1) % mFrameSequence.length;
    }

}

