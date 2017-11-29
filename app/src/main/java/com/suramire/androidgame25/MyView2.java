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
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.suramire.androidgame25.util.L;
import com.suramire.androidgame25.util.SPUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.suramire.androidgame25.Site.上右;
import static com.suramire.androidgame25.Site.上左;
import static com.suramire.androidgame25.Site.下中;
import static com.suramire.androidgame25.Site.下左;
import static com.suramire.androidgame25.Site.右上;
import static com.suramire.androidgame25.Site.右下;
import static com.suramire.androidgame25.Site.右中;
import static com.suramire.androidgame25.Site.左上;
import static com.suramire.androidgame25.Site.左下;
import static com.suramire.androidgame25.Site.左中;


public class MyView2 extends SurfaceView implements Callback, Runnable {
    //region 字段区
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
//	private TiledLayer tiledLayer_zhebi01;
//	private TiledLayer tiledLayer_back01;
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
        if(!isLogo){
            isLogo =true;
        }

        if(isGameOver){
            lifeNumber = 3;
            init();
        }else{
            if(isShowCounter){
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

        if(!isShowCounter && isInited && isLogo){
            mario.logic();
            if(enemies!=null){
                for (int i = 0; i < enemies.size(); i++) {
                    enemies.get(i).logic();
                }
            }
            if(coins!=null){
                for (int i = 0; i < coins.size(); i++) {
                    coins.get(i).logic();
                }
            }
            if(bricks!=null){
                for (int i = 0; i < bricks.size(); i++) {
                    Brick brick = bricks.get(i);
                    brick.logic();
                    Mushroom mushroom = brick.getMushroom();
                    if(mushroom!=null){
                        if(mushroom.isVisiable()){
                            mushroom.logic();
                        }
                    }


                }
            }
            marioMove();
            myStep();

            collisionWithMap();


            MushroomCollisionWithBrick();

            collisionWithEnemy();
            collisionWithCoin();
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
     * 处理玛丽吃金币操作
     */
    private void collisionWithCoin() {
        if(!mario.isDead()){
            if(coins!=null){
                for(int i=0;i<coins.size();i++){
                    Coin coin = coins.get(i);
                    //玛丽吃到可视状态的金币
                    if(coin.isVisiable()&& mario.collisionWith(coin)){
                        coin.setVisiable(false);
                        score +=100;
                        scoreString = "100";
                    }
                }

            }
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
                            scoreString = "100";
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
//		if(tiledLayer_peng01.getX()==0 && !isEnemyShown1){
//			for (int i = 0; i < 3; i++) {
//				Enemy enemy = new Enemy(32, 32, bitmaps2);
//				enemy.setVisiable(true);
//				enemy.setMirror(true);
//				enemy.setPosition(200+60*i, 328);
//				enemies.add(enemy);
//			}
//			isEnemyShown1 = true;
//		}
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
//                	tiledLayer_peng01.move(-4, 0);
//                	tiledLayer_zhebi01.move(-4, 0);
                    //到达地图边界 人物移动
                    if(enemies!=null){
                    	for (int i = 0; i < enemies.size(); i++) {
                    		enemies.get(i).move(-4, 0);
            			}
                    }
                    if(coins!=null){
                        for (int i = 0; i < coins.size(); i++) {
                            coins.get(i).move(-4,0);
                        }
                    }
                    if(bricks!=null){
                        for (int i = 0; i < bricks.size(); i++) {
                            Brick brick = bricks.get(i);
                            brick.move(-4,0);
                            Mushroom mushroom = brick.getMushroom();
                            if(mushroom!=null && mushroom.isVisiable()){
                                mushroom.move(-4,0);
                            }

                        }
                    }
                }else{
                	mario.move(4, 0);
                }
            }
            if(mario.isJumping()){
                L.e("mario.speedY:" + speedY);
                mario.move(0,speedY++);
            }
    	}
        if(mario.getY()>400){
    		 //2 再次落下才表示游戏结束
    		 if(!mario.isDead()){
    			 mario.setDead(true);
                 speedY = -16;
    		 }else{
                 isShowCounter = true;
    		 }
         }


    }

    private  int[][] getMapArray(){
        int[][] ints = {
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
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 27, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 31, 31, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 27, 27, 27, 27, 27, 27, 0, 0, 0, 0, 0, 0, 41,
                        42, 43, 44, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 27, 27, 0, 0, 27, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 34, 35, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 34, 35,
                        0, 0, 27, 27, 27, 27, 27, 27, 27, 0, 0, 0, 0, 0, 0, 46,
                        47, 48, 49, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 27, 27, 27, 0, 0, 27, 27, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 39, 40, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 39, 40,
                        0, 27, 27, 27, 27, 27, 27, 27, 27, 0, 0, 0, 0, 0, 0, 51,
                        52, 53, 54, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0 },
                { 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 0, 0, 26, 26, 26,
                        26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26,
                        26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26,
                        26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26,
                        26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26,
                        26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26,
                        26, 26, 26, 26 },
                { 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 0, 0, 30, 30, 30,
                        30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
                        30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
                        30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
                        30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
                        30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
                        30, 30, 30, 30 },
                { 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 0, 0, 30, 30, 30,
                        30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
                        30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
                        30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
                        30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
                        30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
                        30, 30, 30, 30 }
        };
        return ints;
    }

    //endregion
    private void init() {
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
        Bitmap mushroomBitmap = getBitmap("item/mushroom.png");
        List<Bitmap> bitmaps = new ArrayList<Bitmap>();
        for (int i = 0; i < 4; i++) {
            bitmaps.add(getBitmap("mario/mario"+i+".png"));
        }
        bitmaps2 = new ArrayList<Bitmap>();
        for (int i = 0; i < 3; i++) {
            bitmaps2.add(getBitmap("enemy/enemy"+i+".png"));
        }
        enemies = new ArrayList<Enemy>();
        List<Bitmap> coinBitmaps = new ArrayList<Bitmap>();
        for (int i = 0; i < 4; i++) {
            coinBitmaps.add(getBitmap(String.format(Locale.CHINA,
                    "coin/object_coin_%02d.png",i)));
        }
        coins = new ArrayList<Coin>();

        for (int i = 0; i < 6; i++) {
            Coin coin = new Coin(32, 32, coinBitmaps);
            if(i<3){
                coin.setPosition(87+i*50,208);
            }else{
                coin.setPosition(454+i*50,208);
            }
            coin.setVisiable(true);
            coins.add(coin);
        }

        List<Bitmap> brickBitmaps = new ArrayList<Bitmap>();
        for (int i = 0; i < 5; i++) {
            brickBitmaps.add(getBitmap(String.format(Locale.CHINA, "brick/brick%02d.png", i)));
        }
        //存放砖块行列坐标
        int[][] brickpostions = {
                {3,3},{2,6},{3,6},{4,6},{17,2},{19,2},
                {16,5},{18,5},{20,5},{45,2},{36,5},
                {43,5},{45,5},{47,5}

        };
        bricks = new ArrayList<Brick>();

        for (int i = 0; i < brickpostions.length; i++) {

            int[] brickpostion = brickpostions[i];
            Brick brick = new Brick(40, 40, brickBitmaps);
            brick.setPosition(40*brickpostion[0],40*brickpostion[1]);
            brick.createItem(true, mushroomBitmap);
            brick.setVisiable(true);
            bricks.add(brick);
        }



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

        int map_peng01[][] =getMapArray();

//		/************ 第一关***********背景层 *******/
//		int map_back01[][] = {
//                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//                        0, 0, 0, 0 },
//                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//                        0, 0, 0, 0 },
//                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//                        0, 56, 31, 56, 31, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 56, 0, 0,
//                        0, 0, 0, 0, 0, 0, 31, 31, 31, 31, 0, 0, 0, 0, 0, 0,
//                        0, 0, 0, 0, 0, 0, 0, 27, 27, 0, 0, 0, 0, 0, 0, 0,
//                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//                        0, 0, 0, 0 },
//                { 0, 0, 0, 56, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//                        0, 0, 0, 0, 0, 0, 27, 27, 27, 0, 0, 0, 0, 0, 0, 0,
//                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//                        0, 0, 0, 0 },
//                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//                        0, 0, 0, 0, 0, 27, 27, 27, 27, 0, 0, 0, 0, 0, 0, 0,
//                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//                        0, 0, 0, 0 },
//                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//                        56, 31, 56, 31, 56, 31, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//                        0, 0, 0, 0, 56, 0, 0, 0, 0, 0, 0, 56, 0, 56, 0, 56,
//                        0, 0, 0, 0, 31, 31, 31, 31, 31, 31, 31, 31, 0, 0, 0, 0,
//                        0, 0, 0, 0, 27, 27, 27, 27, 27, 0, 0, 0, 0, 0, 0, 0,
//                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//                        0, 0, 0, 0 },
//                { 0, 0, 56, 31, 56, 0, 0, 0, 0, 0, 27, 0, 0, 0, 0, 0,
//                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//                        0, 0, 31, 31, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//                        0, 0, 0, 27, 27, 27, 27, 27, 27, 0, 0, 0, 0, 0, 0, 41,
//                        42, 43, 44, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//                        0, 0, 0, 0 },
//                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 27, 27, 0, 0, 27, 0, 0,
//                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 34, 35, 0, 0, 0,
//                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 34, 35,
//                        0, 0, 27, 27, 27, 27, 27, 27, 27, 0, 0, 0, 0, 0, 0, 46,
//                        47, 48, 49, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//                        0, 0, 0, 0 },
//                { 0, 0, 0, 0, 0, 0, 0, 0, 27, 27, 27, 0, 0, 27, 27, 0,
//                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 39, 40, 0, 0, 0,
//                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 39, 40,
//                        0, 27, 27, 27, 27, 27, 27, 27, 27, 0, 0, 0, 0, 0, 0, 51,
//                        52, 53, 54, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//                        0, 0, 0, 0 },
//                { 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 0, 0, 26, 26, 26,
//                        26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26,
//                        26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26,
//                        26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26,
//                        26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26,
//                        26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26,
//                        26, 26, 26, 26 },
//                { 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 0, 0, 30, 30, 30,
//                        30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
//                        30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
//                        30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
//                        30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
//                        30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
//                        30, 30, 30, 30 },
//                { 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 0, 0, 30, 30, 30,
//                        30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
//                        30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
//                        30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
//                        30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
//                        30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
//                        30, 30, 30, 30 }
//        };
//
//		/************ 第一关***********遮蔽层 *******/
//		int map_zhebi01[][] = {
//				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//						0, 0, 0, 0, 0, 0, 0, 0 },
//				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//						0, 0, 0, 0, 0, 0, 0, 0 },
//				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//						0, 0, 0, 0, 0, 0, 0, 0 },
//				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//						0, 0, 0, 0, 0, 0, 0, 0 },
//				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//						0, 0, 0, 0, 0, 0, 0, 0 },
//				{ 0, 0, 0, 0, 0, 0, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//						0, 0, 0, 0, 0, 0, 0, 0 },
//				{ 0, 0, 0, 0, 0, 0, 11, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//						0, 0, 0, 0, 0, 0, 0, 0 },
//				{ 0, 0, 0, 0, 0, 16, 21, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 21, 0, 16,
//						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//						0, 0, 0, 0, 0, 0, 0, 0 },
//				{ 0, 0, 0, 0, 0, 21, 21, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//						0, 17, 19, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 21, 0,
//						21, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//						0, 0, 0, 0, 0, 25, 24, 24, 25, 25, 25, 25, 0, 5, 5, 0,
//						5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
//				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//						0, 0, 0, 0, 0, 0, 0, 0 },
//				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//						0, 0, 0, 0, 0, 0, 0, 0 },
//				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//						0, 0, 0, 0, 0, 0, 0, 0 }, };


        //endregion

		Bitmap mapBitmap = getBitmap("map/map1.png");
		tiledLayer_peng01 = new TiledLayer(mapBitmap, 100, 12, 40, 40);
		tiledLayer_peng01.setTiledCell(map_peng01);
//		tiledLayer_zhebi01 = new TiledLayer(mapBitmap,100, 12, 40, 40);
//		tiledLayer_zhebi01.setTiledCell(map_zhebi01);
//		tiledLayer_back01 = new TiledLayer(mapBitmap, 100, 12, 40, 40);
//		tiledLayer_back01.setTiledCell(map_back01);
        isInited = true;


    }
    private void myDraw() {
        try {
            lockCanvas = holder.lockCanvas();
            lockCanvas.save();
            lockCanvas.scale(scaleX, scaleY);
            lockCanvas.drawColor(Color.BLACK);
            mPaint.setTextSize(20);
            mPaint.setColor(Color.WHITE);
            if(!isLogo){
                lockCanvas.drawBitmap(logoBitmap,0,0,null);
            }else{
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
                                280-marioBitmap.getHeight(),mPaint);

                    }else{
//                        tiledLayer_back01.draw(lockCanvas);
                        tiledLayer_peng01.draw(lockCanvas);

                        if(enemies!=null){
                            for (int i = 0; i < enemies.size(); i++) {
                                enemies.get(i).draw(lockCanvas);
                            }
                        }

                        mario.draw(lockCanvas);
                        if(scoreString!=null){
                            lockCanvas.drawText(scoreString,mario.getX()+30,mario.getY()-20-3*delay,mPaint);
                            if(delay++==7){
                                scoreString=null;
                                delay=0;
                            }

                        }
//                        tiledLayer_zhebi01.draw(lockCanvas);
                        lockCanvas.drawBitmap(aBitmap, 680, 420, null);
                        lockCanvas.drawBitmap(bBitmap, 740, 360, null);
                        lockCanvas.drawBitmap(downBitmap, 60, 420, null);
                        lockCanvas.drawBitmap(leftBitmap, 0, 360, null);
                        lockCanvas.drawBitmap(rightBitmap, 120, 360, null);
                        //绘制分数

                        lockCanvas.drawText(String.format(Locale.CHINA,"%06d",score),20,20,mPaint);
                        if(bricks!=null){
                            for (int i = 0; i < bricks.size(); i++) {
                                Brick brick = bricks.get(i);
                                brick.draw(lockCanvas);
                                //绘制砖块内的道具
                                Mushroom mushroom = brick.getMushroom();
                                if(mushroom!=null){
                                    if(mushroom.isVisiable()){
                                        mushroom.draw(lockCanvas);
                                    }
                                }


                            }
                        }

                        if(coins!=null){
                            for (int i = 0; i < coins.size(); i++) {
                                coins.get(i).draw(lockCanvas);
                            }
                        }
                    }
                }
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

                if(mario.siteCollisionWith(brick, 下中) && mario.isJumping()  ){
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
                if(mario.siteCollisionWith(brick,Site.上中) && mario.isJumping()){
                    speedY = Math.abs(speedY);

                    if(!brick.isJumping()){
                        brick.setSpeedY(-4);

                        brick.setJumping(true);
                    }
                }
                if(mario.siteCollisionWith(brick,Site.左中)){
                    mario.move(4,0);
                }
                if(mario.siteCollisionWith(brick,Site.右中)){
                    mario.move(-4,0);
                }

            }
        }
    }

    private  void MushroomCollisionWithBrick(){

        if(bricks!=null){
            for (int i = 0; i < bricks.size(); i++) {
                Brick brick = bricks.get(i);
                Mushroom mushroom = bricks.get(i).getMushroom();

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
    	if(bricks!=null){
            for(int i=0;i<bricks.size();i++){
                Brick brick = bricks.get(i);
                Mushroom mushroom = brick.getMushroom();
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
                            mushroom.setDirection(上右);
                        }
                        //右边碰撞
                        if(mushroom.siteCollisionWith(tiledLayer_peng01,右上)||
                                mushroom.siteCollisionWith(tiledLayer_peng01,右中)||
                                mushroom.siteCollisionWith(tiledLayer_peng01,右下)){
                            mushroom.setDirection(上左);

                        }
                    }
                }

            }
        }

    }
}
