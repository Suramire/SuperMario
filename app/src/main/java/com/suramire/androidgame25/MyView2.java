package com.suramire.androidgame25;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.suramire.androidgame25.util.SPUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.suramire.androidgame25.Site.上左;
import static com.suramire.androidgame25.Site.下左;
import static com.suramire.androidgame25.Site.右上;
import static com.suramire.androidgame25.Site.右下;
import static com.suramire.androidgame25.Site.右中;
import static com.suramire.androidgame25.Site.左上;
import static com.suramire.androidgame25.Site.左下;
import static com.suramire.androidgame25.Site.左中;


public class MyView2 extends SurfaceView implements Callback, Runnable {
    private static final long DOUBLE_TAP_TIMEOUT = 200;
    //region 字段区
    private SurfaceHolder holder;
    private boolean isRunning;
    private Canvas lockCanvas;
    private boolean isGameOver;
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
	private TiledLayer tiledLayer_zhebi01;
	private TiledLayer tiledLayer_back01;
	private Bitmap gameoverbitmap;
	private List<Enemy> enemies;
	private List<Bitmap> bitmaps2;
	private boolean isEnemyShown1;
	private boolean isEnemyShown2;
	private boolean isEnemyShown3;
    private Integer score;//用于分数显示
    private Paint mPaint;
    private boolean isLifeNumber;//显示生命数
    private Bitmap marioBitmap;
    private int lifeNumber = 4;
    private boolean isInitDone;
    private boolean isGameOver2;

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
        if(isGameOver2){
            lifeNumber = 3;
            init();
        }else{
            if(isGameOver){
                init();
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
            //手指移开时停止移动
            if(event.getAction() == MotionEvent.ACTION_UP){
                //stop running
                mario.setRunning(false);
                performClick();
            }
        }
        return true;
    }



    @Override
    public void run() {
        init();
        while (isRunning) {
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

    private void myLogic() {

        if(!isGameOver && isInitDone){
            mario.logic();
            if(enemies!=null){
                for (int i = 0; i < enemies.size(); i++) {
                    enemies.get(i).logic();
                }
            }
            marioMove();
            myStep();
            collisionWithMap();
            collisionWithEnemy();
        }


    }

    private void collisionWithEnemy() {
        if(!mario.isDead()){
            if(enemies!=null){
                for(int i=0;i<enemies.size();i++){
                    Enemy enemy = enemies.get(i);
                    if(!enemy.isDead()&&mario.collisionWith(enemy)){
                        if(mario.isJumping()){
                            enemy.setDead(true);
                            //杀死敌人时获得100积分
                            score +=100;
                            speedY = -10;
                        }else{
                            mario.setDead(true);
                            speedY = -16;
                        }
                    }
                }

            }
        }
    }

    private void myStep() {
		if(tiledLayer_back01.getX()==0 && !isEnemyShown1){
			for (int i = 0; i < 3; i++) {
				Enemy enemy = new Enemy(32, 32, bitmaps2);
				enemy.setVisiable(true);
				enemy.setMirror(true);
				enemy.setPosition(200+60*i, 328);
				enemies.add(enemy);
			}
			isEnemyShown1 = true;
		}
		if(tiledLayer_back01.getX() == -160 && !isEnemyShown2){
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
		if(tiledLayer_back01.getX() ==-1000 && !isEnemyShown3){
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
                }else if(tiledLayer_back01.getX()>800-tiledLayer_back01.getCols()
                		*tiledLayer_back01.getWidth()){
                	tiledLayer_back01.move(-4, 0);
                	tiledLayer_peng01.move(-4, 0);
                	tiledLayer_zhebi01.move(-4, 0);
                    //到达地图边界 人物移动
                    if(enemies!=null){
                    	for (int i = 0; i < enemies.size(); i++) {
                    		enemies.get(i).move(-4, 0);
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
    			 speedY = -16;
    		 }else{
                 isGameOver = true;
                 isLifeNumber = true;
                 lifeNumber--;
                 if(lifeNumber==0){
                     isGameOver2 = true;
                 }
    		 }
         }
    	
        
    }
    //endregion
    private void init() {
        isInitDone = false;
        isGameOver = false;
        isGameOver2 = false;
        isLifeNumber = true;
        aBitmap = getBitmap("button/a.png");
        bBitmap = getBitmap("button/b.png");
        downBitmap = getBitmap("button/down.png");
        leftBitmap = getBitmap("button/left.png");
        rightBitmap = getBitmap("button/right.png");
        gameoverbitmap = getBitmap("menu/gameover.png");
        marioBitmap = getBitmap("mario/mario0.png");
        List<Bitmap> bitmaps = new ArrayList<Bitmap>();
        for (int i = 0; i < 4; i++) {
            bitmaps.add(getBitmap("mario/mario"+i+".png"));
        }
        bitmaps2 = new ArrayList<Bitmap>();
        for (int i = 0; i < 3; i++) {
            bitmaps2.add(getBitmap("enemy/enemy"+i+".png"));
        }
        enemies = new ArrayList<Enemy>();
        rectF = new RectF();
        mario = new Mario(32, 62, bitmaps);
        mario.setVisiable(true);
        mario.setPosition(0,300);

        isEnemyShown1 = false;
        isEnemyShown2 = false;
        isEnemyShown3 = false;

        mPaint = new Paint();
        score = (Integer) SPUtils.get(context,"hiscore",0);

        //region array
        /************ 第一关***********碰撞层 *******/

        int map_peng01[][] = {
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 28, 29, 0, 0, 0, 0, 0, 0, 0,
						28, 29, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 27, 39, 40, 27, 27, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 56, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 56,
						31, 56, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 56,
						56, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 27, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 56, 56, 56, 56, 56, 56, 31,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 27, 27, 34, 35, 27, 27, 0,
						0, 0, 0, 0, 56, 31, 56, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 27, 27, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 27, 27, 27, 39, 40, 27, 27, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 27, 27, 27, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 34, 35, 27, 27, 27, 34, 35, 27, 27, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 56, 31, 56, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 56, 31,
						56, 31, 56, 0, 0, 0, 0, 56, 31, 56, 56, 0, 0, 0, 0, 56,
						31, 31, 56, 0, 0, 0, 27, 27, 27, 27, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 56, 56, 56, 56, 56,
						56, 56, 56, 31, 0, 0, 0, 0, 27, 39, 40, 27, 27, 27, 39,
						40, 27, 27, 0, 0, 0, 0, 56, 31, 56, 31, 56, 0, 0, 0, 0,
						0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 27, 0, 0, 0, 27, 0, 0, 0, 0, 0,
						0, 0, 0, 34, 35, 0, 0, 0, 0, 0, 0, 34, 35, 0, 0, 0, 0,
						0, 0, 0, 27, 27, 27, 27, 27, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 27, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 27, 27, 34, 35, 27, 27, 27, 34, 35, 27, 27, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 27, 27, 0, 0, 0, 27, 27, 0, 0, 0,
						0, 0, 0, 0, 39, 40, 0, 0, 0, 0, 0, 0, 39, 40, 0, 0, 0,
						0, 0, 0, 27, 27, 27, 27, 27, 27, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 27, 27, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 27, 27, 27, 39, 40, 27, 27, 27, 39, 40, 27,
						27, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 0, 0, 0, 26, 26,
						26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26,
						26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26,
						26, 26, 26, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 26, 26,
						26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26,
						26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 0, 0, 0, 0,
						26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26 },
				{ 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 0, 0, 0, 30, 30,
						30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
						30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
						30, 30, 30, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 30, 30,
						30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
						30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 0, 0, 0, 0,
						30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30 },
				{ 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 0, 0, 0, 30, 30,
						30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
						30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
						30, 30, 30, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 30, 30,
						30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
						30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 0, 0, 0, 0,
						30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30 } };

		/************ 第一关***********背景层 *******/
		int map_back01[][] = {
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 0, 0, 0, 0, 6, 6, 0,
						0, 0, 0, 0, 0, 0, 0, 10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 41, 44, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 21, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 11, 0, 0, 0, 0, 11, 11,
						0, 0, 0, 0, 0, 0, 0, 0, 10, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 41, 42, 43, 44, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 21, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 8,
						9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 21, 0, 0, 0, 16, 21, 21,
						16, 0, 0, 0, 0, 0, 0, 0, 10, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 46, 47, 48, 49, 0 },
				{ 0, 0, 17, 18, 19, 0, 0, 21, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 12,
						13, 14, 0, 0, 0, 0, 0, 1, 2, 3, 4, 21, 0, 0, 0, 21, 21,
						21, 21, 0, 0, 0, 0, 0, 0, 0, 10, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5,
						0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 51, 52, 53, 54, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0 }, };

		/************ 第一关***********遮蔽层 *******/
		int map_zhebi01[][] = {
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 11, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 16, 21, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 21, 0, 16,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 21, 21, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 17, 19, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 21, 0,
						21, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 25, 24, 24, 25, 25, 25, 25, 0, 5, 5, 0,
						5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0 }, };
        //endregion
		
		Bitmap mapBitmap = getBitmap("map/map1.png");
		tiledLayer_peng01 = new TiledLayer(mapBitmap, 100, 12, 40, 40);
		tiledLayer_peng01.setTiledCell(map_peng01);
		tiledLayer_zhebi01 = new TiledLayer(mapBitmap,100, 12, 40, 40);
		tiledLayer_zhebi01.setTiledCell(map_zhebi01);
		tiledLayer_back01 = new TiledLayer(mapBitmap, 100, 12, 40, 40);
		tiledLayer_back01.setTiledCell(map_back01);
        isInitDone = true;

    }
    private void myDraw() {
        try {
            lockCanvas = holder.lockCanvas();
            lockCanvas.save();
            lockCanvas.scale(scaleX, scaleY);
            lockCanvas.drawColor(Color.BLACK);
            mPaint.setTextSize(20);
            mPaint.setColor(Color.WHITE);
            //游戏彻底结束
            if(isGameOver2){
                lockCanvas.drawBitmap(gameoverbitmap,
                400-gameoverbitmap.getWidth()/2,
                240-gameoverbitmap.getHeight()/2,null);
            }else{
                //开始显示剩余生命数
                if(isGameOver){
                    lockCanvas.drawBitmap(marioBitmap,400-marioBitmap.getWidth(),
                            240-marioBitmap.getHeight(),null);
                    lockCanvas.drawText(String.format(Locale.CHINA,"X %02d",lifeNumber),
                            440-marioBitmap.getWidth(),
                            280-marioBitmap.getHeight(),mPaint);

                }else{
                    tiledLayer_back01.draw(lockCanvas);
                    tiledLayer_peng01.draw(lockCanvas);
                    if(enemies!=null){
                        for (int i = 0; i < enemies.size(); i++) {
                            enemies.get(i).draw(lockCanvas);
                        }
                    }
                    mario.draw(lockCanvas);
                    tiledLayer_zhebi01.draw(lockCanvas);
                    lockCanvas.drawBitmap(aBitmap, 680, 420, null);
                    lockCanvas.drawBitmap(bBitmap, 740, 360, null);
                    lockCanvas.drawBitmap(downBitmap, 60, 420, null);
                    lockCanvas.drawBitmap(leftBitmap, 0, 360, null);
                    lockCanvas.drawBitmap(rightBitmap, 120, 360, null);
                    //绘制分数

                    lockCanvas.drawText(String.format(Locale.CHINA,"%06d",score),20,20,mPaint);
                }
            }




            lockCanvas.restore();
        } catch (Exception e) {e.printStackTrace();}
        finally {
            if (lockCanvas != null)
                holder.unlockCanvasAndPost(lockCanvas);
        }
    }

    private  void collisionWithMap(){
        //玛丽碰撞
    	if(!mario.isDead()){
    		//头顶碰撞
            if(mario.siteCollisionWith(tiledLayer_peng01, 上左)||
                    mario.siteCollisionWith(tiledLayer_peng01, 上左)||
                    mario.siteCollisionWith(tiledLayer_peng01, 上左)){
                speedY = Math.abs(speedY);
            }
            //脚部碰撞
            if(mario.siteCollisionWith(tiledLayer_peng01, 下左)||
                    mario.siteCollisionWith(tiledLayer_peng01, 下左)||
                    mario.siteCollisionWith(tiledLayer_peng01, 下左)){
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
        
    }
}
