package com.suramire.androidgame25;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.SystemClock;

import com.suramire.androidgame25.item.Bullet;

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
    private int status ;//当前状态 0=初始化 1=吃蘑菇后 2=吃花后
    private boolean isInvincible;//标志是否无敌状态
    private boolean isZeroDamage;//标志是否处于免伤状态
    private int invincibleTime;//无敌时间
    private int zeroDamageTime;//免伤时间
	private boolean isInvincibleThreadStarted;//标志进程是否开始
	private boolean isZeroDamageThreadStarted;//标志进程是否开始
    List<Sprite> bullets;
	List<List<Bitmap>> bitmapsList;
	private int speedX;//移动速度

    public int getSpeedX() {
        return speedX;
    }

    public void setSpeedX(int speedX) {
        this.speedX = speedX;
    }

    private int delay;
	//endregion

    //region Getter & Setter
    public List<List<Bitmap>> getBitmapsList() {
	    return bitmapsList;
    }
	
	public void setBitmapsList(List<List<Bitmap>> bitmapsList) {
		this.bitmapsList = bitmapsList;
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

    public int getInvincibleTime() {
        return invincibleTime;
    }

    public int getZeroDamageTime() {
        return zeroDamageTime;
    }

    public List<Sprite> getBullets() {
        return bullets;
    }

    public void setBullets(List<Sprite> bullets) {
        this.bullets = bullets;
    }

	public int getStatus() {
		return status;
	}
	
	public void setStatus(int status) {
		this.status = status;
		if(status==2){
			bullets = new ArrayList<Sprite>();
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
//        speedXThread = new Thread()
	    paint = new Paint();
	    paint.setAlpha(128);
    }
	
	
	public void shapeShift(){
		shapeShift(getStatus());
	}

    /**
     * 获取可用子弹数目
     * @return
     */
	public int getUseableBulletCount(){
        int i = 0;
        if(bullets!=null){
            for (int j = 0; j < bullets.size(); j++) {
                if(!bullets.get(i).isVisiable()){
                    i++;
                }
            }
        }
        return i;
    }


	/**
	 * 状态变化(变身)
	 * @param targetStatus 目标状态
	 */
    public void shapeShift(int targetStatus){
        if(targetStatus!=2){
            bullets = null;
        }
	    int value =0;
    	if(isInvincible()){
    		value=3;
	    }
	    List<Bitmap> bitmaps = getBitmapsList().get(targetStatus+value);
	    Bitmap bitmap = bitmaps.get(0);
	    int width = bitmap.getWidth();
	    int height = bitmap.getHeight();
	    //状态变化才修正坐标
	    if(targetStatus!=getStatus()){
		    //坐标修正
		    int y;
		    if(getWidth()>width){
			    y =getY()+32;
		    }else if(getWidth()<width){
			    y =getY()-32;
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
	    setFrameSequence(temp);
	    
	    
	    
    }

    @Override
    public void logic() {
        if(isInvincible()){
        	if(!isInvincibleThreadStarted){
        		new Thread(invincibleThread).start();
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
            setFrameSequenceIndex(6);
        }else if(isJumping()){
            nextFrame();
            if(getFrameSequenceIndex()>=6){
                setFrameSequenceIndex(3);
            }
        }else if(isRunning()){
            nextFrame();
            //循环跑动贴图
            if(getFrameSequenceIndex()>=3){
                setFrameSequenceIndex(1);
            }
        }else{
            setFrameSequenceIndex(0);
        }
    }

    public void fire(){
        if(!isDead()&&getStatus()==2&&bullets!=null){
            for (int i = 0; i < bullets.size(); i++) {
                Bullet bullet = (Bullet) bullets.get(i);
                if(!bullet.isVisiable()&&delay++>10){
                    bullet.setPosition(getX()+getWidth()/2,getY()+getHeight()/4);
                    bullet.setMirror(!isMirror());
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



}
