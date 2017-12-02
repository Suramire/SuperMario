package com.suramire.androidgame25;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.suramire.androidgame25.item.Coin;
import com.suramire.androidgame25.item.Flower;
import com.suramire.androidgame25.item.Mushroom;
import com.suramire.androidgame25.item.Star;
import com.suramire.androidgame25.util.L;
import com.suramire.androidgame25.util.SPUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.suramire.androidgame25.Site.上中;
import static com.suramire.androidgame25.Site.上右;
import static com.suramire.androidgame25.Site.上左;
import static com.suramire.androidgame25.Site.下;
import static com.suramire.androidgame25.Site.下中;
import static com.suramire.androidgame25.Site.下右;
import static com.suramire.androidgame25.Site.下左;
import static com.suramire.androidgame25.Site.右;
import static com.suramire.androidgame25.Site.右上;
import static com.suramire.androidgame25.Site.右下;
import static com.suramire.androidgame25.Site.右中;
import static com.suramire.androidgame25.Site.左;
import static com.suramire.androidgame25.Site.左上;
import static com.suramire.androidgame25.Site.左下;
import static com.suramire.androidgame25.Site.左中;


public class MyView2 extends SurfaceView implements Callback, Runnable {
    //region Field
    private boolean isPause;
    private SurfaceHolder holder;
    private boolean isRunning;
    private Canvas lockCanvas;
    private boolean isShowCounter;//是否显示剩余生命数
    private Point outSize;
    private float scaleX;
    private float scaleY;
    private Context context;
	private Bitmap aBitmap;
	private Bitmap bBitmap;
	private Bitmap downBitmap;
	private Bitmap leftBitmap;
	private Bitmap rightBitmap;
    private RectF rectF;
    private Mario mario;
    private int speedY;
	private TiledLayer tiledLayer_peng01;
	private Bitmap gameoverbitmap;
	private List<Enemy> enemies;
	private List<Bitmap> bitmaps2;
	private boolean isEnemyShown1;
	private boolean isEnemyShown2;
	private boolean isEnemyShown3;
    private Integer score;//用于记录分数
    private Paint mPaint;
    private Bitmap marioBitmap;
    private int lifeNumber = 4;
    private boolean isInited;
    private boolean isGameOver;
    private List<Coin> coins;
    private String scoreString;
    private int delay;
    private boolean isLogo;
    private Bitmap logoBitmap;
    private List<Brick> bricks;
    private boolean isDiscountLife;
    private List<Bitmap> marioBitmaps;
    private List<Bitmap> marioFireBitmaps;
    private Bitmap fireBallBitmap;
    private int time;//表示剩余时间
    private Thread thread;
    private List<Bitmap> marioSmallBitmaps;
    private Bitmap finishBitmap;
    private boolean isFinished;
	private boolean isThreadStop;
	private boolean isTouchEnable;//标志是否响应触摸事件
	private ArrayList<Bitmap> marioFireInvBitmaps;
	private ArrayList<Bitmap> marioInvBitmaps;
	private ArrayList<Bitmap> marioSmallInvBitmaps;
	
	public boolean isPause() {
        return isPause;
    }

    public void setPause(boolean pause) {
        isPause = pause;
    }
    //endregion
    //region 通用方法
    public MyView2(Context context) {
        super(context);
        this.context = context;
        holder = getHolder();
        holder.addCallback(this);
        outSize = new Point();
        //获取屏幕尺寸以便进行缩放调整
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getRealSize(outSize);
        //获取对应在缩放比例
        scaleX = (float) outSize.x / 800;
        scaleY = (float) outSize.y / 480;
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isRunning = true;
	    isTouchEnable = true;
        new Thread(this).start();

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    	isRunning = false;
        //游戏退出时记录最高分
        Integer hiscore = (Integer) SPUtils.get(context, "hiscore", 0);
        if(hiscore<score){
            SPUtils.put(context,"hiscore",score);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
		if(isTouchEnable){
			if(isFinished){
				isFinished=false;
				init();
			}
			
			if(!isLogo){
				isLogo =true;
				isGameOver = false;
				init();
				if(isInited&&isThreadStop){
					thread.start();
				}
				
			}else{
				if(isGameOver){
					lifeNumber = 3;
					isLogo = false;
					waitAMoment();
				}
				else{
					if(isShowCounter){
						
						init();
						L.e("isInited" + isInited + " " + isThreadStop);
						if(isInited&&isThreadStop){
							thread.start();
							
						}
						
					}
				}
				
				int pointCount = event.getPointerCount();
				for (int i = 0; i < pointCount; i++) {
					rectF.set(0,360*scaleY,60*scaleX,420*scaleY);
					if(rectF.contains(event.getX(i),event.getY(i))){
						//left running
						mario.setMirror(true);
						mario.setRunning(true);
					}
					
					
					rectF.set(120,360*scaleY,180*scaleX,420*scaleY);
					if(rectF.contains(event.getX(i),event.getY(i))){
						//right running
						mario.setMirror(false);
						mario.setRunning(true);
					}
					rectF.set(740,360*scaleY,800*scaleX,420*scaleY);
					if(rectF.contains(event.getX(i),event.getY(i))){
						//jump
						if(!mario.isJumping()){
							mario.setJumping(true);
							speedY =-16;
						}
					}
					
					rectF.set(680,420*scaleY,740*scaleX,4800*scaleY);
					if(rectF.contains(event.getX(i),event.getY(i))){

//                L.e("mario.getY:" + mario.getY());
						mario.fire();
					}
					//手指移开时停止移动
					if(event.getAction() == MotionEvent.ACTION_UP){
						//stop running
						mario.setRunning(false);
						performClick();
					}
				}
			}
			
			
		}
        
        return true;
    }



    @Override
    public void run() {
        init();
        while (isRunning ) {
            //游戏暂停
            if(!isPause()){
                long startTime = System.currentTimeMillis();
                myLogic();
                myDraw();
                long endTime = System.currentTimeMillis();
                long time = endTime - startTime;
                if (time < 1000 / 30) {
                    SystemClock.sleep(1000 / 30 - time);
                }
            }

        }
    }

    /**
     * 加载图片
     *
     * @param fileName 图片文件名
     * @return Bitmap
     */
    public Bitmap getBitmap(String fileName) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(context.getAssets().open(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private  int[][] getMapArray(){
        return new int[][]{
		        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				        0, 0, 0, 0 },
		        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				        0, 0, 0, 0 },
		        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				        0, 0, 31, 0, 31, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				        0, 0, 0, 0, 0, 0, 31, 31, 31, 31, 0, 0, 0, 0, 0, 0,
				        0, 0, 0, 0, 0, 0, 0, 27, 27, 0, 0, 0, 0, 0, 0, 0,
				        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				        0, 0, 0, 0 },
		        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				        0, 0, 0, 0, 0, 0, 27, 27, 27, 0, 0, 0, 0, 0, 0, 0,
				        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				        0, 0, 0, 0 },
		        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				        0, 0, 0, 0, 0, 27, 27, 27, 27, 0, 0, 0, 0, 0, 0, 0,
				        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				        0, 0, 0, 0 },
		        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				        0, 31, 0, 31, 0, 31, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				        0, 0, 0, 0, 31, 31, 31, 31, 31, 31, 31, 31, 0, 0, 0, 0,
				        0, 0, 0, 0, 27, 27, 27, 27, 27, 0, 0, 0, 0, 0, 0, 0,
				        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				        0, 0, 0, 0 },
		        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				        0, 0, 31, 31, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				        0, 0, 0, 27, 27, 27, 27, 27, 27, 0, 0, 0, 0, 0, 0, 41,
				        42, 43, 44, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				        0, 0, 0, 0 },
		        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 34, 35, 0, 0, 0,
				        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 34, 35,
				        0, 0, 27, 27, 27, 27, 27, 27, 27, 0, 0, 0, 0, 0, 0, 46,
				        47, 48, 49, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				        0, 0, 0, 0 },
		        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 39, 40, 0, 0, 0,
				        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 39, 40,
				        0, 27, 27, 27, 27, 27, 27, 27, 27, 0, 0, 0, 0, 0, 0, 51,
				        52, 53, 54, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				        0, 0, 0, 0 },
		        { 26, 26, 26, 26, 26, 26, 26, 26, 0, 26, 26, 26, 26, 26, 26, 26,
				        26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26,
				        26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26,
				        26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26,
				        26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26,
				        26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26,
				        26, 26, 26, 26 },
		        { 30, 30, 30, 30, 30, 30, 30, 30, 0, 30, 30, 30, 30, 30, 30, 30,
				        30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
				        30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
				        30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
				        30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
				        30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
				        30, 30, 30, 30 },
		        { 30, 30, 30, 30, 30, 30, 30, 30, 0, 30, 30, 30, 30, 30, 30, 30,
				        30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
				        30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
				        30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
				        30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
				        30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
				        30, 30, 30, 30 }
        };
    }

    //endregion

    private void myLogic() {
        //超时则死亡
        if(time<=0){
	        mario.setDead(true);
	        isThreadStop = false;
	        waitAMoment();
        }

        if(!isShowCounter && isInited && isLogo){
            mario.logic();
            if(enemies!=null){
                for (int i = 0; i < enemies.size(); i++) {
                    enemies.get(i).logic();
                }
            }
            if(mario.getBullets()!=null){
                for (int i = 0; i < mario.getBullets().size(); i++) {
                    mario.getBullets().get(i).logic();
                }
            }
            if(bricks!=null){
                for (int i = 0; i < bricks.size(); i++) {
                    Brick brick = bricks.get(i);
                    brick.logic();
                    MySprite item = brick.getItem();
                    if(item!=null){
                        if(item.isVisiable()){
                            item.logic();
                            //玛丽吃到道具(初始状态)
                            if(!mario.isDead()&&item.collisionWith(mario)){
                                //判断道具类型
                                //蘑菇
	                            int status = mario.getStatus();
	                            if(item instanceof Mushroom){
                                    if(status ==0){
//                                        marioShapeShift(1);
	                                    mario.shapeShift(1);
                                    }
                                }else if(item instanceof Flower){
                                    //花
                                    if(status !=2){
                                        //非第三状态都执行
//                                        marioShapeShift(2);
	                                    mario.shapeShift(2);
                                        //设置弹夹
                                        List<Bullet> bullets = new ArrayList<Bullet>();
                                        for (int j = 0; j < 5; j++) {
                                            Bullet bullet = new Bullet(fireBallBitmap);
                                            if(mario.isMirror()){
                                                bullet.setDirection(Site.左);
                                            }else{
                                                bullet.setDirection(Site.右);
                                            }
                                            bullets.add(bullet);
                                        }
                                        mario.setBullets(bullets);
                                    }
                                }else if(item instanceof Star){
                                    //玛丽吃到无敌星后变成无敌状态
                                    mario.setInvincible(true);
	                                // TODO: 2017/12/1 加音效
                                }
                                //吃到后蘑菇消失
                                item.setVisiable(false);
                                updateScore(100);
                            }
                        }
                    }
                }
            }
            marioMove();
            myStep();
            collisionWithMap();
            MushroomCollisionWithBrick();
            collisionWithEnemy();
            collisionWithBrick();

        }
        if(isShowCounter &&!isDiscountLife){

            lifeNumber--;
            isDiscountLife = true;
            if(lifeNumber==0){
                isGameOver = true;
            }
        }


    }

    /**
     * 更新分数
     * @param scoreValue 分数值
     */
    private void updateScore(int scoreValue){
        if(scoreValue>0){
            score+=scoreValue;
            scoreString = "+"+scoreValue;
        }
        if(scoreValue<0){
            score-=scoreValue;
            scoreString = "-"+scoreValue;
        }

    }


    

    private void collisionWithEnemy() {
        if(!mario.isDead()){
            if(enemies!=null){
                for(int i=0;i<enemies.size();i++){
                    Enemy enemy = enemies.get(i);
                    if(!enemy.isDead()&&mario.collisionWith(enemy)){
                        if(mario.isInvincible()){
                            enemy.setDead(true);
                            //杀死敌人时获得100积分
                            updateScore(100);
                        }else if(mario.isJumping()){
                            enemy.setDead(true);
                            //杀死敌人时获得100积分
                            updateScore(100);
                            speedY = -10;
                        }else{

                            if(!mario.isZeroDamage()){
	                            int status = mario.getStatus();
	                            if(status !=0){
                                    switch (status){
	                                    case  1:{
//	                                    	marioShapeShift(0);
		                                    mario.shapeShift(0);
	                                    }break;
	                                    case 2:{
//	                                    	marioShapeShift(1);
		                                    mario.shapeShift(1);
	                                    }break;
                                    }
		                            mario.setZeroDamage(true);
                                }else{
                                    mario.setDead(true);
                                    isThreadStop = false;
                                    speedY = -16;
		                            waitAMoment();
                                }
                            }
                        }
                    }
                }

            }
        }
    }
	
	private void waitAMoment() {
		isTouchEnable = false;
		new Thread(new Runnable() {
			@Override
			public void run() {
				SystemClock.sleep(2000);
				isTouchEnable = true;
			}
		}).start();
	}
	
	private void myStep() {
		if(tiledLayer_peng01.getX()==0 && !isEnemyShown1){
			for (int i = 0; i < 3; i++) {
				Enemy enemy = new Enemy(32, 32, bitmaps2);
				enemy.setVisiable(true);
				enemy.setMirror(true);
				enemy.setPosition(200+60*i, 328);
				enemies.add(enemy);
			}
			isEnemyShown1 = true;
		}
		if(tiledLayer_peng01.getX() == -160 && !isEnemyShown2){
			enemies.clear();
			for (int i = 0; i < 3; i++) {
				Enemy enemy = new Enemy(32, 32, bitmaps2);
				enemy.setVisiable(true);
				enemy.setMirror(true);
				enemy.setPosition(600+60*i, 328);
				enemies.add(enemy);
			}
			isEnemyShown2 = true;

		}
		if(tiledLayer_peng01.getX() ==-1000 && !isEnemyShown3){
            enemies.clear();
            for (int i = 0; i < 3; i++) {
                Enemy enemy = new Enemy(32, 32, bitmaps2);
                enemy.setVisiable(true);
                enemy.setMirror(true);
                enemy.setPosition(500+60*i, 0);
                enemies.add(enemy);
            }
            isEnemyShown3 = true;
		}
		if(tiledLayer_peng01.getX() == -2800){
		    isFinished = true;
			waitAMoment();
		}
    }

	/**
     * 处理移动与跳跃的逻辑
     */
    private void marioMove() {

    	//1 死亡前先起跳
    	if(mario.isDead()){
    		mario.move(0, speedY++);
		    
    	}else{
    		if(mario.isRunning()){
                //左移
                if(mario.isMirror()){
                    mario.move(-4, 0);
                    //未到达屏幕中点
                }else if(mario.getX()<400){
                	mario.move(4, 0);
                    //越过屏幕中点 地图移动
                }else if(tiledLayer_peng01.getX()>800-tiledLayer_peng01.getCols()
                		*tiledLayer_peng01.getWidth()){
                    tiledLayer_peng01.move(-4, 0);
                    //到达地图边界 人物移动
                    if(enemies!=null){
                    	for (int i = 0; i < enemies.size(); i++) {
                    		enemies.get(i).move(-4, 0);
            			}
                    }
                    if(bricks!=null){
                        for (int i = 0; i < bricks.size(); i++) {
                            Brick brick = bricks.get(i);
                            brick.move(-4,0);
                            MySprite item = brick.getItem();
                            if(item!=null && item.isVisiable()){
                                item.move(-4,0);
                            }

                        }
                    }
                }else{
                	mario.move(4, 0);
                }
            }
            if(mario.isJumping()){
                mario.move(0,speedY++);
            }
    	}
	
	    if(mario.getY()>400){
		    //2 再次落下才表示游戏结束
		    if(!mario.isDead()){
			    mario.setDead(true);
			    isThreadStop = false;
			    speedY = -16;
			    waitAMoment();
		    }else{
			    if(!isShowCounter){
				    new Thread(new Runnable() {
					    @Override
					    public void run() {
						    SystemClock.sleep(300);
						    isShowCounter = true;
					    }
				    }).start();
			    }
		    }
	    }
	    


    }

    private void init() {
	    isThreadStop = true;
        isFinished = false;
        time = 6*100;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
            	//当显示过开场图片并且时间不为负数且线程停止标志位不为真时
                while (isThreadStop&& time > 0 && isLogo) {
                    SystemClock.sleep(1000);
                    time--;
                }
            }
        });
        isInited = false;
        isShowCounter = false;
        isGameOver = false;
        isDiscountLife = false;
        aBitmap = getBitmap("button/a.png");
        bBitmap = getBitmap("button/b.png");
        downBitmap = getBitmap("button/down.png");
        leftBitmap = getBitmap("button/left.png");
        rightBitmap = getBitmap("button/right.png");
        gameoverbitmap = getBitmap("menu/gameover.png");
        marioBitmap = getBitmap("mario/mario0.png");
        logoBitmap = getBitmap("logo/logo.jpg");
        finishBitmap = getBitmap("logo/finish.jpg");
        Bitmap mushroomBitmap = getBitmap("item/mushroom.png");
        Bitmap flowerBitmap = getBitmap("item/flower.png");
        Bitmap starBitmap = getBitmap("item/object_star.png");
        fireBallBitmap = getBitmap("item/object_fireball.png");

        marioSmallBitmaps = new ArrayList<Bitmap>();
        marioFireBitmaps = new ArrayList<Bitmap>();
        marioFireInvBitmaps = new ArrayList<Bitmap>();
        marioSmallInvBitmaps = new ArrayList<Bitmap>();
        marioInvBitmaps = new ArrayList<Bitmap>();


        marioBitmaps = new ArrayList<Bitmap>();
        for (int i = 0; i < 4; i++) {
            marioBitmaps.add(getBitmap("mario/mario"+i+".png"));
            marioSmallBitmaps.add(getBitmap("mario/mario_small_"+i+".png"));
            marioFireBitmaps.add(getBitmap("mario/mario_fire_"+i+".png"));
            marioFireInvBitmaps.add(getBitmap("mario/mario_fire_invincible_"+i+".png"));
            marioSmallInvBitmaps.add(getBitmap("mario/mario_small_invincible_"+i+".png"));
	        marioInvBitmaps.add(getBitmap("mario/mario_invincible_"+i+".png"));
        }
        
        bitmaps2 = new ArrayList<Bitmap>();
        for (int i = 0; i < 3; i++) {
            bitmaps2.add(getBitmap("enemy/enemy"+i+".png"));
        }
        enemies = new ArrayList<Enemy>();
        List<Bitmap> coinBitmaps = new ArrayList<Bitmap>();
        for (int i = 0; i < 4; i++) {
            coinBitmaps.add(getBitmap(String.format(Locale.CHINA,
                    "coin/coin%01d.png",i)));
        }
        coins = new ArrayList<Coin>();


        List<Bitmap> brickBitmaps = new ArrayList<Bitmap>();
        for (int i = 0; i < 5; i++) {
            brickBitmaps.add(getBitmap(String.format(Locale.CHINA, "brick/brick%02d.png", i)));
        }
        //存放砖块行列坐标
        int[][] brickpostions = {
                {3,3},{17,2},{19,2},
                {16,5},{18,5},{20,5},{45,2},{36,5},
                {43,5},{45,5},{47,5}

        };
        int[][] brickpostions2 = {
                {2,6},{3,6},{4,6}

        };
        bricks = new ArrayList<Brick>();

        for (int[] brickpostion : brickpostions) {
            Brick brick = new Brick(40, 40, brickBitmaps);
            brick.setPosition(40 * brickpostion[0], 40 * brickpostion[1]);
//          brick.createItem(true, flowerBitmap,ItemType.Flower);
            brick.createItem(true, coinBitmaps,ItemType.Coin);
            brick.setVisiable(true);
            bricks.add(brick);
        }

        Brick brick = new Brick(40, 40, brickBitmaps);
        brick.setPosition(40 * brickpostions2[0][0], 40 * brickpostions2[0][1]);
        brick.createItem(true, mushroomBitmap,ItemType.Mushroom);
        brick.setVisiable(true);
        bricks.add(brick);
        Brick brick2 = new Brick(40, 40, brickBitmaps);
        brick2.setPosition(40 * brickpostions2[1][0], 40 * brickpostions2[1][1]);
        brick2.createItem(true, flowerBitmap,ItemType.Flower);
        brick2.setVisiable(true);
        bricks.add(brick2);
        Brick brick3 = new Brick(40, 40, brickBitmaps);
        brick3.setPosition(40 * brickpostions2[2][0], 40 * brickpostions2[2][1]);
        brick3.createItem(true, starBitmap,ItemType.Star);
        brick3.setVisiable(true);
        bricks.add(brick3);



        rectF = new RectF();
        mario = new Mario(20, 20, marioSmallBitmaps);
        mario.setVisiable(true);
        mario.setPosition(0,342);

        isEnemyShown1 = false;
        isEnemyShown2 = false;
        isEnemyShown3 = false;

        mPaint = new Paint();
	    mPaint.setTextSize(20);//设置字号
	    mPaint.setColor(Color.WHITE);//画笔颜色
        mPaint.setTypeface(Typeface.createFromAsset(context.getAssets(),"font/Bit8.ttf"));//加载字体文件
        score = (Integer) SPUtils.get(context,"hiscore",0);//设置画笔字体


        //碰撞层
        int map_peng01[][] =getMapArray();



		Bitmap mapBitmap = getBitmap("map/map1.png");
		tiledLayer_peng01 = new TiledLayer(mapBitmap, 100, 12, 40, 40);
		tiledLayer_peng01.setTiledCell(map_peng01);

        isInited = true;
		List<List<Bitmap>> bitmapsList = new ArrayList<List<Bitmap>>();
	    bitmapsList.add(marioSmallBitmaps);
	    bitmapsList.add(marioBitmaps);
	    bitmapsList.add(marioFireBitmaps);
	    bitmapsList.add(marioSmallInvBitmaps);
	    bitmapsList.add(marioInvBitmaps);
	    bitmapsList.add(marioFireInvBitmaps);
	    mario.setBitmapsList(bitmapsList);
		

    }
    private void myDraw() {
        try {
            lockCanvas = holder.lockCanvas();
            lockCanvas.save();
            lockCanvas.scale(scaleX, scaleY);
            lockCanvas.drawColor(Color.BLACK);
            if(!isLogo){
                lockCanvas.drawBitmap(logoBitmap,0,0,null);
            }else if(!isFinished){
                //游戏彻底结束
                if(isGameOver){
                    lockCanvas.drawBitmap(gameoverbitmap,
                            400-gameoverbitmap.getWidth()/2,
                            240-gameoverbitmap.getHeight()/2,null);
                }else{
                    //开始显示剩余生命数
                    if(isShowCounter){
                        lockCanvas.drawBitmap(marioBitmap,400-marioBitmap.getWidth(),
                                240-marioBitmap.getHeight(),null);
                        lockCanvas.drawText(String.format(Locale.CHINA,"X %02d",lifeNumber),
                                440-marioBitmap.getWidth(),
                                285-marioBitmap.getHeight(),mPaint);
                        lockCanvas.drawText("单击屏幕继续",350,300,mPaint);

                    }else{
//                        tiledLayer_back01.draw(lockCanvas);
                        tiledLayer_peng01.draw(lockCanvas);

                        if(enemies!=null){
                            for (int i = 0; i < enemies.size(); i++) {
                                enemies.get(i).draw(lockCanvas);
                            }
                        }

                        mario.draw(lockCanvas);
                        lockCanvas.drawBitmap(aBitmap, 680, 420, null);
                        lockCanvas.drawBitmap(bBitmap, 740, 360, null);
                        lockCanvas.drawBitmap(downBitmap, 60, 420, null);
                        lockCanvas.drawBitmap(leftBitmap, 0, 360, null);
                        lockCanvas.drawBitmap(rightBitmap, 120, 360, null);
                        //绘制砖块
                        if(bricks!=null){
                            for (int i = 0; i < bricks.size(); i++) {
                                Brick brick = bricks.get(i);
                                brick.draw(lockCanvas);
                                //绘制砖块内的道具
                                MySprite mushroom = brick.getItem();
                                if(mushroom!=null){
                                    if(mushroom.isVisiable()){
                                        mushroom.draw(lockCanvas);
                                    }
                                }
                            }
                        }
                        
	                    //绘制单次得分
	                    if(scoreString!=null){
		                    lockCanvas.drawText(scoreString,mario.getX()+25,mario.getY()-20-3*delay,mPaint);
		                    if(delay++==15){
			                    scoreString=null;
			                    delay=0;
		                    }
	                    }
                        //绘制总分数
                        lockCanvas.drawText(String.format(Locale.CHINA,"SCORE:%06d",score),30,30,mPaint);
                        if(mario.getBullets()!=null){
                            for (int i = 0; i < mario.getBullets().size(); i++) {
                                mario.getBullets().get(i).draw(lockCanvas);
                            }
                        }
                        //绘制剩余时间
                        lockCanvas.drawText(String.format(Locale.CHINA,"TIME:%03d",time),700,30,mPaint);
                    }
                }
            }else{
                lockCanvas.drawBitmap(finishBitmap,0,0,null);
            }

            lockCanvas.restore();
        } catch (Exception e) {e.printStackTrace();}
        finally {
            if (lockCanvas != null)
                holder.unlockCanvasAndPost(lockCanvas);
        }
    }

    /**
     * 玛丽奥与砖块碰撞
     */
    private  void collisionWithBrick(){
        if(bricks!=null){
            for (int i = 0; i < bricks.size(); i++) {
                Brick brick = bricks.get(i);

                if(mario.siteCollisionWith(brick, 下) && mario.isJumping()  ){
                    mario.setJumping(false);
                            //坐标修正
                            //取得脚部坐标
                            int footY = mario.getY() + mario.getHeight();
                            //取整
                            int newY = footY / tiledLayer_peng01.getHeight()
                                    * tiledLayer_peng01.getHeight()
                                    - mario.getHeight();
                            mario.setPosition(mario.getX(),newY);
                }
                if(mario.siteCollisionWith(brick,Site.上) && mario.isJumping()){
                    speedY = Math.abs(speedY);

                    if(!brick.isJumping()){
                        brick.setSpeedY(-4);
                        brick.setJumping(true);
                    }
                }
                if(mario.siteCollisionWith(brick,Site.左)){
                    mario.move(4,0);
                }
                if(mario.siteCollisionWith(brick,Site.右)){
                    mario.move(-4,0);
                }

            }
        }
    }

    private  void MushroomCollisionWithBrick(){

        if(bricks!=null){
            for (int i = 0; i < bricks.size(); i++) {
                MySprite mushroom = bricks.get(i).getItem();

                if(mushroom!=null && mushroom.isVisiable()){
                    for (int j = 0; j < bricks.size(); j++) {
                        Brick brick1 = bricks.get(j);
                        if(mushroom.siteCollisionWith(brick1,下中)){
                            if(mushroom.isJumping()){
                                mushroom.setJumping(false);
                                int footY = mushroom.getY() + mushroom.getHeight();
                                //取整
                                int newY = footY / tiledLayer_peng01.getHeight()
                                        * tiledLayer_peng01.getHeight()
                                        - mushroom.getHeight();
                                mushroom.setPosition(mushroom.getX(),newY);
                            }
                        }
                    }

                }

            }
        }
    }


    /**
     * 精灵与地图碰撞
     * 玛丽
     * 敌人
     * 砖块道具
     */
    private  void collisionWithMap(){
        //玛丽碰撞
    	if(!mario.isDead()){
            
    		//头顶碰撞
            if(mario.siteCollisionWith(tiledLayer_peng01, 上左)||
                    mario.siteCollisionWith(tiledLayer_peng01, 上中)||
                    mario.siteCollisionWith(tiledLayer_peng01, 上右)){
                speedY = Math.abs(speedY);
            }
            //脚部碰撞
            if(mario.siteCollisionWith(tiledLayer_peng01, 下左)||
                    mario.siteCollisionWith(tiledLayer_peng01, 下中)||
                    mario.siteCollisionWith(tiledLayer_peng01, 下右)){
                mario.setJumping(false);
                //坐标修正
                //取得脚部坐标
                int footY = mario.getY() + mario.getHeight();
                //取整
                int newY = footY / tiledLayer_peng01.getHeight()
                        * tiledLayer_peng01.getHeight()
                        - mario.getHeight();
                mario.setPosition(mario.getX(),newY);
            }
            //落地判断
            else if(!mario.isJumping()){
                mario.setJumping(true);
                speedY = 0 ;
            }

            //左边碰撞
            if(mario.siteCollisionWith(tiledLayer_peng01,左上)||
                    mario.siteCollisionWith(tiledLayer_peng01,左中)||
                    mario.siteCollisionWith(tiledLayer_peng01,左下)){
                mario.move(4,0);
            }
            //右边碰撞
            if(mario.siteCollisionWith(tiledLayer_peng01,右上)||
                    mario.siteCollisionWith(tiledLayer_peng01,右中)||
                    mario.siteCollisionWith(tiledLayer_peng01,右下)){
                mario.move(-4,0);
            }
            if(mario.getBullets()!=null){
                for (int i = 0; i < mario.getBullets().size(); i++) {
                    Bullet bullet = mario.getBullets().get(i);
                    if(bullet.isVisiable()){
                        //子弹与地图碰撞则消失
                        if(bullet.siteCollisionWith(tiledLayer_peng01,上左)||
                           bullet.siteCollisionWith(tiledLayer_peng01,上中)||
                           bullet.siteCollisionWith(tiledLayer_peng01,上右)||
                           bullet.siteCollisionWith(tiledLayer_peng01,下左)||
                           bullet.siteCollisionWith(tiledLayer_peng01,下中)||
                           bullet.siteCollisionWith(tiledLayer_peng01,下右)||
                           bullet.siteCollisionWith(tiledLayer_peng01,左上)||
                           bullet.siteCollisionWith(tiledLayer_peng01,左中)||
                           bullet.siteCollisionWith(tiledLayer_peng01,左下)||
                           bullet.siteCollisionWith(tiledLayer_peng01,右上)||
                           bullet.siteCollisionWith(tiledLayer_peng01,右中)||
                           bullet.siteCollisionWith(tiledLayer_peng01,右下)){
                           bullet.setVisiable(false);
                        }
//                        else if(bullet.siteCollisionWith(tiledLayer_peng01, 下左)||
//                                bullet.siteCollisionWith(tiledLayer_peng01, 下中)||
//                                bullet.siteCollisionWith(tiledLayer_peng01, 下右)){
////                                bullet.setSpeedY(-10);
//                                L.e("collision 下");
////                                bullet.setJumping(true);
//                        }
                        if(enemies!=null){
                            for (int j = 0; j < enemies.size(); j++) {
                                Enemy enemy = enemies.get(j);
                                if(bullet.collisionWith(enemy)){
                                    //子弹碰到敌人
                                    //加分
                                    bullet.setVisiable(false);
                                    enemy.setVisiable(false);
                                    updateScore(100);
                                }
                            }
                        }
                        if(bricks!=null){
                            for (int j = 0; j < bricks.size(); j++) {
                                Brick brick = bricks.get(j);
                                if(bullet.collisionWith(brick)){
                                    //子弹碰到砖块消失
                                    bullet.setVisiable(false);
                                }
                            }
                        }

                    }
                }
            }
    	}
    	//敌人碰撞
    	if(enemies!=null){
    		for(int i=0;i<enemies.size();i++){
                Enemy enemy = enemies.get(i);
                if(!enemy.isDead()&& enemy.isVisiable()){
                    //脚部碰撞
                    if(enemy.siteCollisionWith(tiledLayer_peng01, 下左)||
                            enemy.siteCollisionWith(tiledLayer_peng01, 下左)||
                            enemy.siteCollisionWith(tiledLayer_peng01, 下左)){
                        enemy.setJumping(false);
                        //坐标修正
                        //取得脚部坐标
                        int footY = enemy.getY() + enemy.getHeight();
                        //取整
                        int newY = footY / tiledLayer_peng01.getHeight()
                                * tiledLayer_peng01.getHeight()
                                - enemy.getHeight();
                        enemy.setPosition(enemy.getX(),newY);
                    }
                    //落地判断
                    else if(!enemy.isJumping()){
                        enemy.setJumping(true);
                        speedY = 0 ;
                    }

                    //左边碰撞
                    if(enemy.siteCollisionWith(tiledLayer_peng01,左上)||
                            enemy.siteCollisionWith(tiledLayer_peng01,左中)||
                            enemy.siteCollisionWith(tiledLayer_peng01,左下)){
                        enemy.setMirror(false);
                    }
                    //右边碰撞
                    if(enemy.siteCollisionWith(tiledLayer_peng01,右上)||
                            enemy.siteCollisionWith(tiledLayer_peng01,右中)||
                            enemy.siteCollisionWith(tiledLayer_peng01,右下)){
                        enemy.setMirror(true);
                    }
    			}
    		}
    	}
    	if(bricks!=null){
            for(int i=0;i<bricks.size();i++){
                Brick brick = bricks.get(i);
                MySprite mushroom = brick.getItem();
                if(mushroom!=null){
                    if(mushroom.isVisiable()){

//                    脚部碰撞
                        if(mushroom.siteCollisionWith(tiledLayer_peng01, 下左)||
                                mushroom.siteCollisionWith(tiledLayer_peng01, 下左)||
                                mushroom.siteCollisionWith(tiledLayer_peng01, 下左)){
                            mushroom.setJumping(false);
                            //坐标修正
                            //取得脚部坐标
                            int footY = mushroom.getY() + mushroom.getHeight();
                            //取整
                            int newY = footY / tiledLayer_peng01.getHeight()
                                    * tiledLayer_peng01.getHeight()
                                    - mushroom.getHeight();
                            mushroom.setPosition(mushroom.getX(),newY);
                        }
//                    落地判断
                        else if(!mushroom.isJumping()){
                            mushroom.setJumping(true);
                            mushroom.setSpeedY(0);
                        }

                        //左边碰撞
                        if(mushroom.siteCollisionWith(tiledLayer_peng01,左上)||
                                mushroom.siteCollisionWith(tiledLayer_peng01,左中)||
                                mushroom.siteCollisionWith(tiledLayer_peng01,左下)){
//                        mushroom.setMirror(false);
                            mushroom.setDirection(右);
                        }
                        //右边碰撞
                        if(mushroom.siteCollisionWith(tiledLayer_peng01,右上)||
                                mushroom.siteCollisionWith(tiledLayer_peng01,右中)||
                                mushroom.siteCollisionWith(tiledLayer_peng01,右下)){
                            mushroom.setDirection(左);

                        }
                    }
                }

            }
        }

    }
	
}
