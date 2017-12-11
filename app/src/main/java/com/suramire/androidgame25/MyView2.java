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
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.suramire.androidgame25.enemy.Cannon;
import com.suramire.androidgame25.enemy.Enemy;
import com.suramire.androidgame25.enemy.Turtle;
import com.suramire.androidgame25.enums.GameState;
import com.suramire.androidgame25.enums.ItemType;
import com.suramire.androidgame25.enums.Site;
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

import static com.suramire.androidgame25.enums.GameState.FINISH;
import static com.suramire.androidgame25.enums.GameState.GAMEOVER;
import static com.suramire.androidgame25.enums.GameState.GAMING;
import static com.suramire.androidgame25.enums.GameState.LIFTCOUNTER;
import static com.suramire.androidgame25.enums.GameState.LOGO;
import static com.suramire.androidgame25.enums.GameState.TIMEUP;
import static com.suramire.androidgame25.enums.Site.上中;
import static com.suramire.androidgame25.enums.Site.上右;
import static com.suramire.androidgame25.enums.Site.上左;
import static com.suramire.androidgame25.enums.Site.下;
import static com.suramire.androidgame25.enums.Site.下中;
import static com.suramire.androidgame25.enums.Site.下右;
import static com.suramire.androidgame25.enums.Site.下左;
import static com.suramire.androidgame25.enums.Site.右;
import static com.suramire.androidgame25.enums.Site.右上;
import static com.suramire.androidgame25.enums.Site.右下;
import static com.suramire.androidgame25.enums.Site.右中;
import static com.suramire.androidgame25.enums.Site.左;
import static com.suramire.androidgame25.enums.Site.左上;
import static com.suramire.androidgame25.enums.Site.左下;
import static com.suramire.androidgame25.enums.Site.左中;


public class MyView2 extends SurfaceView implements Callback, Runnable {
	private final MyMusic myMusic;
	private final MySoundPool mySoundPool;
	//region Field
    private boolean isPause;
    private SurfaceHolder holder;
    private boolean isRunning;
    private Canvas lockCanvas;

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
    private int lifeNumber = 3;
    private boolean isInited;
    private String scoreString;
    private int delay;
    private Bitmap logoBitmap;
    private List<Brick> bricks;
    private List<Bitmap> marioBitmaps;
    private List<Bitmap> marioFireBitmaps;
    private Bitmap fireBallBitmap;
    private int time;//表示剩余时间
    private Thread thread;
    private List<Bitmap> marioSmallBitmaps;
    private Bitmap finishBitmap;
	private boolean threadRunning;
	private boolean isTouchEnable;//标志是否响应触摸事件
	private ArrayList<Bitmap> marioFireInvBitmaps;
	private ArrayList<Bitmap> marioInvBitmaps;
	private ArrayList<Bitmap> marioSmallInvBitmaps;
	private Bitmap coinBitmap;
    private int coinNumber;
    private GameState gameState;//表示当前游戏状态
	private int stateDelay_timeup;//状态切换间的延迟
	private int stateDelay_lifecounter;
	private int stateDelay_finish;
	private int stateDelay_logo;
	private int stateDelay_gameover;
	private boolean isFirstRun;
	private boolean isMaioDieSoundPlayed;
    private List<CommonBrick> commonBricks;
    private TiledLayer tiledLayer_cover;
    private Bitmap cannonBitmap;
    private Bitmap enemyBulletBitmap;
    private List<Bitmap> cannonBitmaps;
    private List<Bitmap> turtleBitmaps;
    private List<Cannon> cannons;
    private List<Turtle> turtles;

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
	    myMusic = new MyMusic(context);
	    mySoundPool = new MySoundPool(context);
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
	    L.e("surfaceDestroyed");
	    myMusic.stop();
        //游戏退出时记录最高分
        Integer hiscore = (Integer) SPUtils.get(context, "hiscore", 0);
        if(hiscore<score){
            SPUtils.put(context,"hiscore",score);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
		switch (gameState){
			case LOGO:{
				if (stateDelay_logo>100) {
					init();
					stateDelay_logo=0;
				}
			}break;
			case FINISH:{
				if(stateDelay_finish>100){
					gameState = LOGO;
					lifeNumber = 3;
					stateDelay_finish=0;
				}
			}break;
			case GAMEOVER:{
				if(stateDelay_gameover>100){
					gameState = LOGO;
					lifeNumber = 3;
					stateDelay_gameover=0;
				}
				
			}break;
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
				if(!mario.isDead()&&!mario.isJumping()){
					mario.setJumping(true);
					mySoundPool.play(mySoundPool.getJumpSound());
					speedY =-16;
				}
			}
			
			rectF.set(680,420*scaleY,740*scaleX,4800*scaleY);
			if(rectF.contains(event.getX(i),event.getY(i))){
				mario.fire();
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
		isFirstRun = true;
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
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
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
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 27, 27, 27, 27, 27, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 27, 27, 27, 27, 27, 27, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 34, 35, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 34, 35,
                    0, 0, 27, 27, 27, 27, 27, 27, 27, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 39, 40, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 39, 40,
                    0, 27, 27, 27, 27, 27, 27, 27, 27, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
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

    private  int[][] getMapCoverArray(){
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
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 41,
                        42, 43, 44, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 7, 8, 9, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 46,
                        47, 48, 49, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 17,
                        18, 19, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 12, 13, 14, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 51,
                        52, 53, 54, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0 },
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
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0 }
        };
    }





    //endregion

    private void myLogic() {
    	switch (gameState){
		    case GAMING:{

			    if(!threadRunning&&isInited){

				    thread.start();
				    threadRunning = true;
			    }
			    mario.logic();
			    if(!mario.isDead()){
                    myMusic.play("music/bgm.mp3",true);
                    if(cannons!=null){
                        for (int i = 0; i < cannons.size(); i++) {
                            cannons.get(i).logic();
                        }
                    }
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
                    if(commonBricks!=null){
                        for (int i = 0; i < commonBricks.size(); i++) {
                            commonBricks.get(i).logic();
                        }
                    }
                    if(turtles!=null){
                        for (int i = 0; i < turtles.size(); i++) {
                            turtles.get(i).logic();
                        }
                    }
                    if(bricks!=null){
                        for (int i = 0; i < bricks.size(); i++) {
                            Brick brick = bricks.get(i);
                            brick.logic();
                            ItemSprite itemSprite = brick.getItemSprite();
                            if(itemSprite !=null){
                                if(itemSprite.ismVisiable()){
                                    itemSprite.logic();
                                    //玛丽吃到道具(初始状态)
                                    if(!mario.isDead()&& itemSprite.collisionWith(mario)){
                                        //判断道具类型
                                        //蘑菇
                                        int status = mario.getStatus();
                                        if(itemSprite instanceof Mushroom){
                                            mySoundPool.play(mySoundPool.getMushroomSound());
                                            if(status ==0){
//                                        marioShapeShift(1);
                                                mario.shapeShift(1);
                                            }
                                        }else if(itemSprite instanceof Flower){
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
                                                        bullet.setDirection(右);
                                                    }
                                                    bullets.add(bullet);
                                                }
                                                mario.setBullets(bullets);
                                            }
                                        }else if(itemSprite instanceof Star){
                                            //玛丽吃到无敌星后变成无敌状态
                                            mario.setInvincible(true);
                                            // TODO: 2017/12/1 加音效
                                        }else if(itemSprite instanceof Coin){
                                            coinNumber++;
                                            mySoundPool.play(mySoundPool.getCoinSound());
                                        }
                                        //吃到后蘑菇消失
                                        itemSprite.setmVisiable(false);
                                        updateScore(100);
                                    }
                                }
                            }
                        }
                    }
			    }else{
				    if(!isMaioDieSoundPlayed){
					    myMusic.stop();
					    mySoundPool.play(mySoundPool.getMarioDieSound());
					    isMaioDieSoundPlayed = true;
				    }
			    	
			    }

			    marioMove();
			    myStep();
			    collisionWithMap();
			    spriteCollisionWithBrick();
			    maroiCollisionWithEnemy();
			    marioCollisionWithSth();
			    myTime();
		    }break;
		    
		    case TIMEUP:{
		    	if(stateDelay_timeup++>100){
				    gameState = LIFTCOUNTER;
				    stateDelay_timeup = 0;
			    }
			    lifeNumber--;
		    	gameState = LIFTCOUNTER;
		    }break;
		    case LIFTCOUNTER:{
//		    	myMusic.stop();
			    if(lifeNumber<1){
				    gameState = GAMEOVER;
			    }else{
				    if(stateDelay_lifecounter++>50){
					    gameState = GAMING;
					    init();
					    stateDelay_lifecounter = 0;
				    }
			    }
		    }break;
		    case GAMEOVER:{
		    	stateDelay_gameover++;
		    }break;
		    case FINISH:{
			    stateDelay_finish++;
		    }break;
		    case LOGO:{
		    	stateDelay_logo++;
		    }break;
	    }
    }
	
	private void myTime() {
		if(time<0){
			gameState = TIMEUP;
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


    

    private void maroiCollisionWithEnemy() {
        if(!mario.isDead()){
            //玛丽与板栗碰撞
            if(enemies!=null){
                for(int i=0;i<enemies.size();i++){
                    Enemy enemy = enemies.get(i);
                    if(!enemy.isDead()&&mario.collisionWith(enemy)){
                        if(mario.isInvincible()){
                            enemy.setDead(true);
                            mySoundPool.play(mySoundPool.getHitEnemySound());
                            //杀死敌人时获得100积分
                            updateScore(100);
                        }else if(mario.isJumping()){
                            enemy.setDead(true);
	                        mySoundPool.play(mySoundPool.getHitEnemySound());
                            //杀死敌人时获得100积分
                            updateScore(100);
                            speedY = -10;
                        }else{

                            if(!mario.isZeroDamage()){
	                            int status = mario.getStatus();
	                            if(status !=0){
                                    switch (status){
	                                    case  1:{
		                                    mario.shapeShift(0);
	                                    }break;
	                                    case 2:{
		                                    mario.shapeShift(1);
	                                    }break;
                                    }
		                            mario.setZeroDamage(true);
                                }else{
                                    mario.setDead(true);
                                    speedY = -16;
                                }
                            }
                        }
                    }
                }

            }
            //玛丽与大炮子弹碰撞
            if(cannons!=null){
                for (int w = 0; w < cannons.size(); w++) {
                    Cannon cannon = cannons.get(w);
                    if(cannon!=null&&cannon.getBullets()!=null){
                        for (int i = 0; i < cannon.getBullets().size(); i++) {
                            EnemyBullet enemyBullet = cannon.getBullets().get(i);
                            //大炮子弹碰到玛丽
                            if(enemyBullet.ismVisiable()&& mario.collisionWith(enemyBullet)){
                                //玛丽无敌或踩到子弹
                                if(mario.isInvincible()){
                                    enemyBullet.setmVisiable(false);
                                    updateScore(200);
                                }else if(mario.siteCollisionWith(enemyBullet,下)){
                                    speedY = -10;
                                    enemyBullet.setmVisiable(false);
                                    updateScore(200);
                                }else{
                                    if(!mario.isZeroDamage()){
                                        int status = mario.getStatus();
                                        if(status !=0){
                                            switch (status){
                                                case  1:{
                                                    mario.shapeShift(0);
                                                }break;
                                                case 2:{
                                                    mario.shapeShift(1);
                                                }break;
                                            }
                                            mario.setZeroDamage(true);
                                        }else{
                                            mario.setDead(true);
                                            speedY = -16;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
	

	
	private void myStep() {
		if(tiledLayer_peng01.getX()==0 && !isEnemyShown1){
			for (int i = 0; i < 3; i++) {
//				Enemy enemy = new Enemy(32, 32, bitmaps2);
//				enemy.setmVisiable(true);
//				enemy.setMirror(true);
//				enemy.setPosition(200+60*i, 328);
//				enemies.add(enemy);
                Turtle turtle = new Turtle(20, 31, turtleBitmaps);
                turtle.setmVisiable(true);
                turtle.setMirror(true);
                turtle.setCanFly(true);
                turtle.setPosition(150+50*i,229);
                turtles.add(turtle);
			}

            isEnemyShown1 = true;
		}
		if(tiledLayer_peng01.getX() == -160 && !isEnemyShown2){
			enemies.clear();
			for (int i = 0; i < 3; i++) {
				Enemy enemy = new Enemy(32, 32, bitmaps2);
				enemy.setmVisiable(true);
				enemy.setMirror(true);
				enemy.setPosition(600+60*i, 328);
				enemies.add(enemy);
			}
            Cannon cannon = new Cannon(40, 80, cannonBitmaps);
            cannon.setmVisiable(true);
            cannon.setPosition(720,280);
            EnemyBullet enemyBullet = new EnemyBullet(enemyBulletBitmap);
            enemyBullet.setDirection(左);
            enemyBullet.setMirror(false);
            List<EnemyBullet> bullets = new ArrayList<>();
            bullets.add(enemyBullet);
            cannon.setBullets(bullets);
            cannons.add(cannon);
            isEnemyShown2 = true;

		}
		if(tiledLayer_peng01.getX() ==-1000 && !isEnemyShown3){
            enemies.clear();
            for (int i = 0; i < 3; i++) {
                Enemy enemy = new Enemy(32, 32, bitmaps2);
                enemy.setmVisiable(true);
                enemy.setMirror(true);
                enemy.setPosition(500+60*i, 0);
                enemies.add(enemy);
            }
            isEnemyShown3 = true;
		}
		if(tiledLayer_peng01.getX() == -2800){
		    gameState = FINISH;
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
                    tiledLayer_cover.move(-4,0);
                    //到达地图边界 人物移动
                    if(enemies!=null){
                    	for (int i = 0; i < enemies.size(); i++) {
                    		enemies.get(i).move(-4, 0);
            			}
                    }
                    if(cannons!=null){
                        for (int i = 0; i < cannons.size(); i++) {
                            cannons.get(i).move(-4,0);
                        }
                    }
                    if(turtles!=null){
                        for (int i = 0; i < turtles.size(); i++) {
                            turtles.get(i).move(-4,0);
                        }
                    }
                    if(bricks!=null){
                        for (int i = 0; i < bricks.size(); i++) {
                            Brick brick = bricks.get(i);
                            brick.move(-4,0);
                            ItemSprite itemSprite = brick.getItemSprite();
                            if(itemSprite !=null && itemSprite.ismVisiable()){
                                itemSprite.move(-4,0);
                            }

                        }
                    }
                    if(commonBricks!=null){
                        for (int i = 0; i < commonBricks.size(); i++) {
                            commonBricks.get(i).move(-4,0);
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
		    	lifeNumber--;
			    gameState = LIFTCOUNTER;
		    }
	    }
	    


    }

    private void init() {
	    isMaioDieSoundPlayed = false;

        time = 300;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
            	//当显示过开场图片并且时间不为负数且线程停止标志位不为真时
                while (threadRunning && time > 0 ) {
                    SystemClock.sleep(1000);
                    time--;
                    if(time==100){
                    	mySoundPool.play(mySoundPool.getHurryUpSound());
                    }
                }
            }
        });
        isInited = false;
        cannons = new ArrayList<>();
        turtles = new ArrayList<>();
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
		coinBitmap = getBitmap("coin/coin4.png");
        cannonBitmap = getBitmap("enemy/cannon.png");
        enemyBulletBitmap = getBitmap("enemy/enemy_bullet.png");

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
        cannonBitmaps = new ArrayList<>();
        cannonBitmaps.add(cannonBitmap);
        List<Bitmap> brickBitmaps = new ArrayList<Bitmap>();
        for (int i = 0; i < 5; i++) {
            brickBitmaps.add(getBitmap(String.format(Locale.CHINA, "brick/brick%02d.png", i)));
        }
        turtleBitmaps = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            turtleBitmaps.add(getBitmap(String.format(Locale.CHINA, "turtle/turtle_%01d.png", i)));
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

        int[][] commonBrickPostions ={

                {18,2},{20,2},{17,5},{19,5},{21,5},{37,5},
                {44,5},{46,5},{48,5},
                {54,2},{55,2},{56,2},{57,2},
                {52,5},{53,5},{54,5},{55,5},{56,5},{57,5},{58,5},{59,5},{60,5},
                {50,6},{51,6},{60,4},{52,4}

        };
        bricks = new ArrayList<Brick>();

        for (int[] brickpostion : brickpostions) {
            Brick brick = new Brick(40, 40, brickBitmaps);
            brick.setPosition(40 * brickpostion[0], 40 * brickpostion[1]);
//          brick.createItem(true, flowerBitmap,ItemType.Flower);
            brick.createItem(true, coinBitmaps, ItemType.Coin);
            brick.setmVisiable(true);
            bricks.add(brick);
        }

        Brick brick = new Brick(40, 40, brickBitmaps);
        brick.setPosition(40 * brickpostions2[0][0], 40 * brickpostions2[0][1]);
        brick.createItem(true, mushroomBitmap,ItemType.Mushroom);
        brick.setmVisiable(true);
        bricks.add(brick);
        Brick brick2 = new Brick(40, 40, brickBitmaps);
        brick2.setPosition(40 * brickpostions2[1][0], 40 * brickpostions2[1][1]);
        brick2.createItem(true, flowerBitmap,ItemType.Flower);
        brick2.setmVisiable(true);
        bricks.add(brick2);
        Brick brick3 = new Brick(40, 40, brickBitmaps);
        brick3.setPosition(40 * brickpostions2[2][0], 40 * brickpostions2[2][1]);
        brick3.createItem(true, starBitmap,ItemType.Star);
        brick3.setmVisiable(true);
        bricks.add(brick3);

        commonBricks = new ArrayList<>();
        List<Bitmap> commonbrickbitmaps = new ArrayList<>();
        commonbrickbitmaps.add(getBitmap("brick/commonBrick.png"));

        for (int[] position:commonBrickPostions){
            CommonBrick commonBrick = new CommonBrick(40, 40, commonbrickbitmaps);
            commonBrick.setmVisiable(true);
            commonBrick.setCanBroken(false);
            commonBrick.setPosition(40*position[0],40*position[1]);
            commonBricks.add(commonBrick);
        }


        rectF = new RectF();
        mario = new Mario(20, 20, marioSmallBitmaps);
        mario.setmVisiable(true);
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
        int map_cover[][] = getMapCoverArray();



		Bitmap mapBitmap = getBitmap("map/map1.png");
		tiledLayer_peng01 = new TiledLayer(mapBitmap, 100, 12, 40, 40);
		tiledLayer_cover = new TiledLayer(mapBitmap, 100, 12, 40, 40);
		tiledLayer_peng01.setTiledCell(map_peng01);
        tiledLayer_cover.setTiledCell(map_cover);

        isInited = true;
		List<List<Bitmap>> bitmapsList = new ArrayList<List<Bitmap>>();
	    bitmapsList.add(marioSmallBitmaps);
	    bitmapsList.add(marioBitmaps);
	    bitmapsList.add(marioFireBitmaps);
	    bitmapsList.add(marioSmallInvBitmaps);
	    bitmapsList.add(marioInvBitmaps);
	    bitmapsList.add(marioFireInvBitmaps);
	    mario.setBitmapsList(bitmapsList);
	
	    if(isFirstRun){
		    gameState = LOGO;
		    isFirstRun = false;
	    }else{
		    gameState = GAMING;
	    }
	    
		

    }
    private void myDraw() {
        try {
            lockCanvas = holder.lockCanvas();
            lockCanvas.save();
            lockCanvas.scale(scaleX, scaleY);
            lockCanvas.drawColor(Color.BLACK);
            switch (gameState){
	            case LOGO:{
		            lockCanvas.drawBitmap(logoBitmap,0,0,null);
	            }break;
	            case GAMEOVER:{
		            lockCanvas.drawBitmap(gameoverbitmap,
				            400-gameoverbitmap.getWidth()/2,
				            240-gameoverbitmap.getHeight()/2,null);
	            }break;
	            case FINISH:{
		            lockCanvas.drawBitmap(finishBitmap,0,0,null);
	            }break;
	            case LIFTCOUNTER:{
		            lockCanvas.drawBitmap(marioBitmap,400-marioBitmap.getWidth(),
				            240-marioBitmap.getHeight(),null);
		            lockCanvas.drawText(String.format(Locale.CHINA,"X %02d",lifeNumber),
				            440-marioBitmap.getWidth(),
				            285-marioBitmap.getHeight(),mPaint);
	            }break;
	            case GAMING:{
                    tiledLayer_cover.draw(lockCanvas);
		            tiledLayer_peng01.draw(lockCanvas);
		            if(enemies!=null){
			            for (int i = 0; i < enemies.size(); i++) {
				            enemies.get(i).draw(lockCanvas);
			            }
		            }
                    if(cannons!=null){
                        for (int i = 0; i < cannons.size(); i++) {
                            cannons.get(i).draw(lockCanvas);
                        }
                    }
                    if(turtles!=null){
                        for (int i = 0; i < turtles.size(); i++) {
                            turtles.get(i).draw(lockCanvas);
                        }
                    }
		            mario.draw(lockCanvas);
		            lockCanvas.drawBitmap(aBitmap, 680, 420, null);
		            lockCanvas.drawBitmap(bBitmap, 740, 360, null);
//		            lockCanvas.drawBitmap(downBitmap, 60, 420, null);
		            lockCanvas.drawBitmap(leftBitmap, 0, 360, null);
		            lockCanvas.drawBitmap(rightBitmap, 120, 360, null);

		            //绘制砖块
		            if(bricks!=null){
			            for (int i = 0; i < bricks.size(); i++) {
				            Brick brick = bricks.get(i);
				            brick.draw(lockCanvas);
				            //绘制砖块内的道具
				            ItemSprite mushroom = brick.getItemSprite();
				            if(mushroom!=null){
					            if(mushroom.ismVisiable()){
						            mushroom.draw(lockCanvas);
					            }
				            }
			            }
		            }
                    if(commonBricks!=null){
                        for (int i = 0; i < commonBricks.size(); i++) {
                            commonBricks.get(i).draw(lockCanvas);
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
		            //绘制金币数
		            lockCanvas.drawBitmap(coinBitmap,300,0,null);
		            lockCanvas.drawText(String.format(Locale.CHINA,"x %02d",coinNumber),340,30,mPaint);
		            //绘制剩余时间
		            lockCanvas.drawText(String.format(Locale.CHINA,"TIME:%03d",time),700,30,mPaint);
	            }break;
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
    private  void marioCollisionWithSth(){
        if(commonBricks!=null){
            for (int i = 0; i < commonBricks.size(); i++) {
                CommonBrick commonBrick = commonBricks.get(i);
                if(mario.siteCollisionWith(commonBrick, 下) && mario.isJumping()){
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
                if(mario.siteCollisionWith(commonBrick,Site.上) && mario.isJumping()){
                    speedY = Math.abs(speedY);
//                    if(commonBrick.isHasItem()){
                    if(!commonBrick.isJumping()){
                        if(mario.getStatus()!=0){
                            commonBrick.setCanBroken(true);
                        }
//                            mySoundPool.play(mySoundPool.getHitcommonBrickSound());
                        commonBrick.setSpeedY(-4);
                        commonBrick.setJumping(true);
                    }
//                    }

                }
                if(mario.siteCollisionWith(commonBrick,Site.左)){
                    mario.move(4,0);
                }
                if(mario.siteCollisionWith(commonBrick, 右)){
                    mario.move(-4,0);
                }
            }
        }
        
        
        if(bricks!=null){
            for (int i = 0; i < bricks.size(); i++) {
                Brick brick = bricks.get(i);
                if(mario.siteCollisionWith(brick, 下) && mario.isJumping()){
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
					if(brick.isHasItem()){
						if(!brick.isJumping()){
							mySoundPool.play(mySoundPool.getHitbrickSound());
							brick.setSpeedY(-4);
							brick.setJumping(true);
						}
					}

                }
                if(mario.siteCollisionWith(brick,Site.左)){
                    mario.move(4,0);
                }
                if(mario.siteCollisionWith(brick, 右)){
                    mario.move(-4,0);
                }

            }
        }
        if(cannons!=null){
            for (int i = 0; i < cannons.size(); i++) {
                Cannon cannon = cannons.get(i);
                if(cannon!=null){
                    if(mario.siteCollisionWith(cannon, 下) && mario.isJumping()){
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
                    if(mario.siteCollisionWith(cannon,Site.左)){
                        mario.move(4,0);
                    }
                    if(mario.siteCollisionWith(cannon, 右)){
                        mario.move(-4,0);
                    }
                }
            }
        }
        

    }
    
    private void enemyCollionWithSth(){


        

    }

    private  void spriteCollisionWithBrick(){
        //道具与砖块碰撞
        if(bricks!=null){
            for (int i = 0; i < bricks.size(); i++) {
                ItemSprite itemSprite = bricks.get(i).getItemSprite();
                if(itemSprite !=null && itemSprite.ismVisiable()){
                    for (int j = 0; j < bricks.size(); j++) {
                        Brick brick1 = bricks.get(j);
                        if(itemSprite.siteCollisionWith(brick1,下中)){
                            if(itemSprite.isJumping()){
                                itemSprite.setJumping(false);
                                int footY = itemSprite.getY() + itemSprite.getHeight();
                                //取整
                                int newY = footY / tiledLayer_peng01.getHeight()
                                        * tiledLayer_peng01.getHeight()
                                        - itemSprite.getHeight();
                                itemSprite.setPosition(itemSprite.getX(),newY);
                            }
                        }
                    }

                }

            }
        }
        //道具与普通砖块碰撞
        if(commonBricks!=null){
            for (int i = 0; i < commonBricks.size(); i++) {
                ItemSprite itemSprite = commonBricks.get(i).getItemSprite();
                if(itemSprite !=null && itemSprite.ismVisiable()){
                    for (int j = 0; j < commonBricks.size(); j++) {
                        Brick brick1 = commonBricks.get(j);
                        if(itemSprite.siteCollisionWith(brick1,下中)){
                            if(itemSprite.isJumping()){
                                itemSprite.setJumping(false);
                                int footY = itemSprite.getY() + itemSprite.getHeight();
                                //取整
                                int newY = footY / tiledLayer_peng01.getHeight()
                                        * tiledLayer_peng01.getHeight()
                                        - itemSprite.getHeight();
                                itemSprite.setPosition(itemSprite.getX(),newY);
                            }
                        }
                    }

                }

            }
        }

        //乌龟与砖块碰撞
        if(bricks!=null&&turtles!=null){
            for (int i = 0; i < bricks.size(); i++) {
                Brick brick = bricks.get(i);
                for (int j = 0; j < turtles.size(); j++) {
                    Turtle turtle = turtles.get(j);
                    
                    
                    if(turtle.siteCollisionWith(brick, 下) && turtle.isJumping()){
                        if(turtle.isCanFly()){
                            turtle.setJumping(true);
                            turtle.speedY = -10;
                        }else{
                            turtle.setJumping(false);
                            //坐标修正
                            //取得脚部坐标
                            int footY = turtle.getY() + turtle.getHeight();
                            //取整
                            int newY = footY / tiledLayer_peng01.getHeight()
                                    * tiledLayer_peng01.getHeight()
                                    - turtle.getHeight();
                            turtle.setPosition(turtle.getX(),newY);
                        }


                    }
                    if(turtle.siteCollisionWith(brick,Site.上)){
                        speedY = Math.abs(speedY);

                    }
                    if(turtle.siteCollisionWith(brick,Site.左)){
                        turtle.move(4,0);
                    }
                    if(turtle.siteCollisionWith(brick, 右)){
                        turtle.move(-4,0);
                    }
                }
               

            }
        }
        if(commonBricks!=null&&turtles!=null){
            for (int i = 0; i < commonBricks.size(); i++) {
                CommonBrick brick = commonBricks.get(i);
                for (int j = 0; j < turtles.size(); j++) {
                    Turtle turtle = turtles.get(j);


                    if(turtle.siteCollisionWith(brick, 下) && turtle.isJumping()){
                        if(turtle.isCanFly()){
                            turtle.setJumping(true);
                            turtle.speedY = -10;
                        }else{
                            turtle.setJumping(false);
                            //坐标修正
                            //取得脚部坐标
                            int footY = turtle.getY() + turtle.getHeight();
                            //取整
                            int newY = footY / tiledLayer_peng01.getHeight()
                                    * tiledLayer_peng01.getHeight()
                                    - turtle.getHeight();
                            turtle.setPosition(turtle.getX(),newY);
                        }


                    }
                    if(turtle.siteCollisionWith(brick,Site.上) ){
                        L.e("turtle collision up");
                        speedY = Math.abs(speedY);

                    }
                    if(turtle.siteCollisionWith(brick,Site.左)){
                        turtle.move(4,0);
                    }
                    if(turtle.siteCollisionWith(brick, 右)){
                        turtle.move(-4,0);
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
                    if(bullet.ismVisiable()){
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
                           bullet.setmVisiable(false);
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
                                    bullet.setmVisiable(false);
                                    enemy.setmVisiable(false);
                                    updateScore(100);
                                }
                            }
                        }
                        if(bricks!=null){
                            for (int j = 0; j < bricks.size(); j++) {
                                Brick brick = bricks.get(j);
                                if(bullet.collisionWith(brick)){
                                    //子弹碰到砖块消失
                                    bullet.setmVisiable(false);
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
                if(!enemy.isDead()&& enemy.ismVisiable()){
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
        if(turtles!=null){
            for(int i=0;i<turtles.size();i++){
                Turtle turtle = turtles.get(i);
                if(!turtle.isDead()&& turtle.ismVisiable()){
                    //脚部碰撞
                    if(turtle.siteCollisionWith(tiledLayer_peng01, 下左)||
                            turtle.siteCollisionWith(tiledLayer_peng01, 下左)||
                            turtle.siteCollisionWith(tiledLayer_peng01, 下左)){
                        if(turtle.isCanFly()){
                            turtle.setJumping(true);
                            turtle.speedY = -10;
                        }else{
                            turtle.setJumping(false);
                            //坐标修正
                            //取得脚部坐标
                            int footY = turtle.getY() + turtle.getHeight();
                            //取整
                            int newY = footY / tiledLayer_peng01.getHeight()
                                    * tiledLayer_peng01.getHeight()
                                    - turtle.getHeight();
                            turtle.setPosition(turtle.getX(),newY);
                        }
                    }
                    //落地判断
                    else if(!turtle.isJumping()){
                        turtle.setJumping(true);
                        speedY = 0 ;
                    }

                    //左边碰撞
                    if(turtle.siteCollisionWith(tiledLayer_peng01,左上)||
                            turtle.siteCollisionWith(tiledLayer_peng01,左中)||
                            turtle.siteCollisionWith(tiledLayer_peng01,左下)){
                        turtle.setMirror(false);
                    }
                    //右边碰撞
                    if(turtle.siteCollisionWith(tiledLayer_peng01,右上)||
                            turtle.siteCollisionWith(tiledLayer_peng01,右中)||
                            turtle.siteCollisionWith(tiledLayer_peng01,右下)){
                        turtle.setMirror(true);
                    }
                }
            }
        }
        //道具与地图碰撞
        if(bricks!=null){
            for(int i=0;i<bricks.size();i++){
                Brick brick = bricks.get(i);
                ItemSprite mushroom = brick.getItemSprite();
                if(mushroom!=null){
                    if(mushroom.ismVisiable()){

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
