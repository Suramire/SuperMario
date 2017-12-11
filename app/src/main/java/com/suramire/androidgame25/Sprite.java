package com.suramire.androidgame25;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.suramire.androidgame25.enums.Site;

import java.util.List;

/**
 * Created by Suramire on 2017/10/9.
 * 精灵类
 * 扩充：
 * 1.与另外一个精灵碰撞时判断碰撞方位
 * 2.与地图发生碰撞的逻辑处理
 */

public class Sprite {

    //region 字段
    private int mX;//x轴坐标
    private int mY;//y轴坐标
    private boolean mVisiable;//可见性
    private int mWidth;//单帧宽度
    private int mHeight;//单帧高度
    private Bitmap mBitmap;//使用单张图片时
    private List<Bitmap> mBitmaps;//使用多帧时
    private int mFrameNumber;//总帧数
    private int[] mFrameX;//每帧的x坐标
    private int[] mFrameY;//每帧的y坐标
    private int[] mFrameSequence;//帧序列
	private int mFrameSequenceIndex;//帧序列的索引
    private Rect mDest;//目标剪切区
    private Rect mSrc;//源图片剪切区
	//endregion

    //region Getter and Setter
    public List<Bitmap> getBitmaps() {
	    return mBitmaps;
    }
	
	public void setBitmaps(List<Bitmap> mBitmaps) {
		this.mBitmaps = mBitmaps;
	}

	public void setFrameSequence(int[] mFrameSequence) {
		this.mFrameSequence = mFrameSequence;
	}

	public int getFrameSequenceIndex() {
		return mFrameSequenceIndex;
	}

	public void setFrameSequenceIndex(int mFrameSequenceIndex) {
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

    public boolean ismVisiable() {
        return mVisiable;
    }

    public void setmVisiable(boolean mVisiable) {
        this.mVisiable = mVisiable;
    }


    //endregion

    public Sprite(int width, int height, List<Bitmap> bitmaps) {
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

    /**
     * 设置位置
     * @param x x轴坐标
     * @param y y轴坐标
     */
    public void setPosition(int x, int y){
        this.mX = x;
        this.mY = y;
    }

    /**
     * 移动
     * @param x x轴方向距离
     * @param y y轴方向距离
     */
    public void move(float x,float y){
        mY += y;
        mX += x;
        outOfBounds();
    }

    public void draw(Canvas canvas){
        draw(canvas,null);
    }

    /**
     * 绘制自身
     * @param canvas 画布对象
     * @param paint 为了实现半透明效果需要的画笔对象
     */
    public void draw(Canvas canvas,Paint paint){
	    if(ismVisiable()) {
		    //采用切图方式
		    if (mBitmap != null) {
			    int x = mFrameX[mFrameSequence[mFrameSequenceIndex]];
			    int y = mFrameY[mFrameSequence[mFrameSequenceIndex]];
			    mSrc.set(x, y, x + getWidth(), y + getHeight());
			    mDest.set(getX(), getY(), getX() + getWidth(), getY() + getHeight());
			    canvas.drawBitmap(mBitmap, mSrc, mDest, paint);
		    } else {
		    	//采用单帧贴图
			    canvas.drawBitmap(mBitmaps.get(mFrameSequence[mFrameSequenceIndex]),
					    getX(), getY(), paint);
		    }
	    }
    }
	
	/**自身逻辑*/
    public void logic(){
    }
	/* 边界处理*/
    protected void outOfBounds() {
    }

    /**
     * 下一帧
     */
    public void nextFrame() {
        mFrameSequenceIndex = (mFrameSequenceIndex + 1) % mFrameSequence.length;
    }


    /**
     * 碰撞检测
     * @param sprite 目标精灵
     * @return 是否碰撞
     */
    public boolean collisionWith(Sprite sprite){
        if (!ismVisiable() || !sprite.ismVisiable()) {
            return false;
        }
        if(getX()<sprite.getX()&& getX()+getWidth()<sprite.getX()){
//            精灵在右
    		return false;
    	}
    	if(sprite.getX()<getX()&& sprite.getX()+sprite.getWidth()<getX()){
//            精灵在左
    		return false;
    	}
    	if(getY()<sprite.getY()&& getY()+getHeight()<sprite.getY()){
//            精灵在下
    		return false;
    	}
    	if(sprite.getY()<getY()&& sprite.getY()+sprite.getHeight()<getY()){
//            精灵在上
    		return false;
    	}
    	return true;
    }

    /**
     * 精灵与精灵碰撞检测
     * @param sprite 被碰撞的精灵
     * @param site 碰撞方位
     * @return 是否碰撞
     */

    public boolean siteCollisionWith(Sprite sprite,Site site){
        int sy = sprite.getY();
        int sx = sprite.getX();
        int sh = sprite.getHeight();
        int sw = sprite.getWidth();
        int w = getWidth();
        int h = getHeight();
        int x = getX();
        int y = getY();

        switch (site){
            case 下:{

                if(collisionWith(sprite)
                        && sy > y
                        && h >=sy-y
                        && x+w/2>=sx
                        && x+w/2 <=sx+sw
                        ){
                    return true;
                }
            }break;
            case 上:{

                if(collisionWith(sprite)
                        && sy < y
                        && sy + sh >= y //砖块高于精灵最多一行高度
                        && x + w /2>= sx//精灵右1/2宽度可以顶砖块
                        && x + w /2<= sx + sw//精灵左1/2宽度可以顶砖块
                        ){
                    return true;
                }
            }break;

            case 右:{
                if(collisionWith(sprite)
                        &&x+w==sx
                        &&sy - y <h//只和同一行砖块左右碰撞
                        ){
                    return true;
                }
            }break;
            case 左:{
                if(collisionWith(sprite)
                        &&sx+sw==x
                        &&sy - y <h//只和同一行砖块左右碰撞
                        ){
                    return true;
                }
            }break;
        }

        return false;
    }


}

