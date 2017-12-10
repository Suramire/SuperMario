package com.suramire.androidgame25;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.SystemClock;

import com.suramire.androidgame25.enums.Site;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Suramire on 2017/11/9.
 * 玛丽类 实现无敌与免伤状态
 */

public class Mario extends Sprite {
	
	//region Fields
	private final Thread invincibleThread;//无敌倒计时线程
	private final Thread zeroDamagThread;//免伤倒计时线程
	private final Paint paint;
	private boolean isMirror;//是否翻转
    private boolean isRunning;//是否跑动
    private boolean isJumping;//是否跳跃
    private boolean isDead;//是否死亡
    private int status ;//当前状态 0=初始化 1=吃蘑菇后 2=吃花后
    private boolean isInvincible;//标志是否无敌状态
    private boolean isZeroDamage;//标志是否处于免伤状态
    private int invincibleTime;//无敌时间
    private int zeroDamageTime;//免伤时间
	private boolean isInvincibleThreadStarted;//标志进程是否开始
	private boolean isZeroDamageThreadStarted;//标志进程是否开始
    List<Bullet> bullets;
	List<List<Bitmap>> bitmapsList;
	

	
	private int delay;
	//endregion

    //region Getter & Setter
    public List<List<Bitmap>> getBitmapsList() {
	    return bitmapsList;
    }
	
	public void setBitmapsList(List<List<Bitmap>> bitmapsList) {
		this.bitmapsList = bitmapsList;
	}
	
	public int getInvincibleTime() {
		return invincibleTime;
	}
	
	public void setInvincibleTime(int invincibleTime) {
		this.invincibleTime = invincibleTime;
	}
	
	public int getZeroDamageTime() {
		return zeroDamageTime;
	}
	
	public void setZeroDamageTime(int zeroDamageTime) {
		this.zeroDamageTime = zeroDamageTime;
	}
	
	public boolean isZeroDamage() {
		return isZeroDamage;
	}
	
	public void setZeroDamage(boolean zeroDamage) {
		if(zeroDamage){
			zeroDamageTime = 3;
		}
		isZeroDamage = zeroDamage;
	}
	
	public boolean isInvincible() {
		return isInvincible;
	}
	
	public void setInvincible(boolean invincible) {
		isInvincible = invincible;
		if(invincible){
			invincibleTime = 10;
		}
		shapeShift();
		
	}

    public List<Bullet> getBullets() {
        return bullets;
    }

    public void setBullets(List<Bullet> bullets) {
        this.bullets = bullets;
    }
    public boolean isMirror() {
        return isMirror;
    }

    public void setMirror(boolean mirror) {
        isMirror = mirror;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public boolean isJumping() {
        return isJumping;
    }

    public void setJumping(boolean jumping) {
        isJumping = jumping;
    }

    public boolean isDead() {
        return isDead;
    }

    public void setDead(boolean dead) {
        isDead = dead;
    }
	public int getStatus() {
		return status;
	}
	
	public void setStatus(int status) {
		this.status = status;
		if(status==2){
			bullets = new ArrayList<Bullet>();
		}
	}
    //endregion
	
    public Mario(int width, int height, List<Bitmap> bitmaps) {
        super(width, height, bitmaps);
	    invincibleThread = new Thread(new Runnable() {
		    @Override
		    public void run() {
			    while (invincibleTime >= 1) {
				    SystemClock.sleep(1000);
				    invincibleTime--;
			    }
		    }
	    });
	    zeroDamagThread = new Thread(new Runnable() {
		    @Override
		    public void run() {
			    while (zeroDamageTime >= 1) {
				    SystemClock.sleep(1000);
				    zeroDamageTime--;
			    }
			
		    }
	    });
	    paint = new Paint();
	    paint.setAlpha(128);
    }
	
	
	public void shapeShift(){
		shapeShift(getStatus());
	}
 
	/**
	 * 状态变化(变身)
	 * @param targetStatus 目标状态
	 */
    public void shapeShift(int targetStatus){
    	
	    int value =0;
    	if(isInvincible()){
    		value=3;
	    }
	    List<Bitmap> bitmaps = bitmapsList.get(targetStatus+value);
	    Bitmap bitmap = bitmaps.get(0);
	    int width = bitmap.getWidth();
	    int height = bitmap.getHeight();
	    //状态变化才修正坐标
	    if(targetStatus!=getStatus()){
		    //坐标修正
		    int y;
		    if(getWidth()>width){
			    y =getY()+42;
		    }else if(getWidth()<width){
			    y =getY()-42;
		    }else{
			    y = getY();
		    }
		    setPosition(getX(),y);
		    setStatus(targetStatus);
	    }
	    setWidth(width);
	    setHeight(height);
	    setBitmaps(bitmaps);

	    int[] temp = new int[bitmaps.size()];
	    for (int i = 0; i < temp.length; i++) {
		    temp[i] = i;
	    }
	    setmFrameSequence(temp);
	    
	    
	    
    }

    @Override
    public void logic() {
        if(isInvincible()){
        	if(!isInvincibleThreadStarted){
        		invincibleThread.start();
		        isInvincibleThreadStarted = true;
	        }
	        if(invincibleTime<=0){
        		setInvincible(false);
		        isInvincibleThreadStarted = false;
	        }
        }
        if(isZeroDamage()){
        	if(!isZeroDamageThreadStarted){
        		new Thread(zeroDamagThread).start();
		        isZeroDamageThreadStarted = true;
	        }
	        if(zeroDamageTime<=0){
        		isZeroDamage = false;
		        isZeroDamageThreadStarted = false;
	        }
        }
        
        
        if(isDead()){
        	
            setmFrameSequenceIndex(3);
        }else if(isJumping()){
            setmFrameSequenceIndex(2);
        }else if(isRunning()){
            nextFrame();
            //循环跑动贴图
            if(getmFrameSequenceIndex()>=2){
                setmFrameSequenceIndex(0);
            }
        }else{
            setmFrameSequenceIndex(0);
        }
    }

    public void fire(){
        if(!isDead()&&getStatus()==2&&bullets!=null){
            for (int i = 0; i < bullets.size(); i++) {
                Bullet bullet = bullets.get(i);
                if(!bullet.isVisiable()&&delay++>10){
                    bullet.setPosition(getX()+getWidth()/2,getY()+getHeight()/4);
                    bullet.setDirection(isMirror? Site.左:Site.右);
                    bullet.setVisiable(true);
                    bullet.setSpeedY(-4);
                    bullet.setJumping(true);
                    delay=0;
                    break;
                }
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if(isMirror()){
            canvas.save();
	        //翻转画布 相当于翻转人物
	        canvas.scale(-1,1,getX()+getWidth()/2,getY()+getHeight()/2);
            if(isZeroDamage()){
	            super.draw(canvas,paint);
            }else{
	            super.draw(canvas);
            }
            canvas.restore();
        }else{
	        if(isZeroDamage()){
		        super.draw(canvas,paint);
	        }else{
		        super.draw(canvas);
	        }
        }
    }
	

    @Override
    protected void outOfBounds() {
        if(getX()<0){
            setX(0);
        }else if(getX()>800-getHeight()){
            setX(800-getHeight());
        }
        if(getY()<0){
            setY(0);
        }else if(getY()>480-getHeight()){
            setY(480-getHeight());
        }
    }

    /**
     * 碰撞检测
     * @param tiledLayer
     * @param site
     * @return
     */
    public boolean siteCollisionWith(TiledLayer tiledLayer,Site site){
        int siteX = 0;
        int siteY = 0;
        switch (site){
            case 上左:{
                siteX = getX() + getWidth() / 4;
                siteY = getY();
            }break;
            case 上中:{
                siteX = getX() + getWidth() / 2;
                siteY = getY();
            }break;
            case 上右:{
                siteX = getX() + 3 * getWidth() / 4;
                siteY = getY();
            }break;

            case 下左:{
                siteX = getX() + getWidth() / 4;
                siteY = getY() + getHeight();
            }break;
            case 下中:{
                siteX = getX() + getWidth() / 2;
                siteY = getY() + getHeight();
            }break;
            case 下右:{
                siteX = getX() + 3 * getWidth() / 4;
                siteY = getY() + getHeight();
            }break;

            case 左上:{
                siteX = getX();
                siteY = getY() + getHeight() / 4;
            }break;
            case 左中:{
                siteX = getX();
                siteY = getY() + getHeight() / 2;
            }break;
            case 左下:{
                siteX = getX();
                siteY = getY() + 3 * getHeight() / 4;
            }break;

            case 右上:{
                siteX = getX() + getWidth();
                siteY = getY() + getHeight() / 4;
            }break;
            case 右中:{
                siteX = getX() + getWidth();
                siteY = getY() + getHeight() / 2;
            }break;
            case 右下:{
                siteX = getX() + getWidth();
                siteY = getY() + 3 * getHeight() / 4;
            }break;
        }
        //在地图上的坐标
        int mapX = siteX - tiledLayer.getX();
        int mapY = siteY - tiledLayer.getY();
        //在地图上的对应行列
        int col = mapX / tiledLayer.getWidth();
        int row = mapY / tiledLayer.getHeight();
        //超出边界
        if(col>tiledLayer.getCols()-1|| row>tiledLayer.getRows()-1){
            return true;
        }
        //存在障碍物
        if(tiledLayer.getTiledCell(col,row)!=0){
            return true;
        }
        return false;
    }


    /**
     * 玛丽与精灵碰撞检测
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
                        && sy + sh >= y //砖块高于玛丽最多一行高度
                        && x + w /2>= sx//玛丽右1/2宽度可以顶砖块
                        && x + w /2<= sx + sw//玛丽左1/2宽度可以顶砖块
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
//                        &&y-sy<sh
                        &&sy - y <h//只和同一行砖块左右碰撞
                        ){
                    return true;
                }
            }break;
        }

        return false;
    }






}
