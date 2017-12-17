package com.suramire.androidgame25;

import android.content.Context;
import android.content.SharedPreferences;
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

import com.suramire.androidgame25.audio.MyMusic;
import com.suramire.androidgame25.audio.MySoundPool;
import com.suramire.androidgame25.enemy.Cannon;
import com.suramire.androidgame25.enemy.Chestunt;
import com.suramire.androidgame25.enemy.Enemy;
import com.suramire.androidgame25.enemy.Turtle;
import com.suramire.androidgame25.enums.GameState;
import com.suramire.androidgame25.enums.ItemType;
import com.suramire.androidgame25.enums.Site;
import com.suramire.androidgame25.item.Brick;
import com.suramire.androidgame25.item.Bullet;
import com.suramire.androidgame25.item.Coin;
import com.suramire.androidgame25.item.CommonBrick;
import com.suramire.androidgame25.item.EnemyBullet;
import com.suramire.androidgame25.item.Flower;
import com.suramire.androidgame25.item.ItemSprite;
import com.suramire.androidgame25.item.Mushroom;
import com.suramire.androidgame25.item.Star;

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
import static com.suramire.androidgame25.enums.Site.左上;
import static com.suramire.androidgame25.enums.Site.左下;
import static com.suramire.androidgame25.enums.Site.左中;


public class MyView2 extends SurfaceView implements Callback, Runnable {
    private final MyMusic myMusic;
    private final MySoundPool mySoundPool;
    private final SharedPreferences sp;
    //region Field
    private boolean isPause;
    private SurfaceHolder holder;
    private boolean isRunning;
    private Canvas lockCanvas;
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
    private TiledLayer tiledLayer_peng01;
    private Bitmap gameoverbitmap;
    private List<Sprite> enemies;
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
    private List<Sprite> bricks;
    private Bitmap fireBallBitmap;
    private int time;//表示剩余时间
    private Thread thread;
    private Bitmap finishBitmap;
    private boolean threadRunning;
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
    private List<Sprite> commonBricks;
    private TiledLayer tiledLayer_cover;
    private Bitmap enemyBulletBitmap;
    private List<Bitmap> cannonBitmaps;
    private List<Bitmap> turtleBitmaps;
    private List<Sprite> cannons;
    private List<Sprite> turtles;
    private List<Sprite> items;
    private boolean isEnemyShown4;

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
        sp = context.getSharedPreferences("sp", Context.MODE_PRIVATE);
        holder = getHolder();
        holder.addCallback(this);
        Point outSize = new Point();
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
        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isRunning = false;
        myMusic.stop();
        //游戏退出时记录最高分
        Integer hiscore = sp.getInt("hiscore",0);
        if (hiscore < score) {
            sp.edit().putInt("hiscore",score).apply();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (gameState) {
            case LOGO: {
                if (stateDelay_logo > 50) {
                    init();
                    stateDelay_logo = 0;
                    coinNumber = 0;
                }
            }
            break;
            case FINISH: {
                if (stateDelay_finish > 50) {
                    gameState = LOGO;
                    lifeNumber = 3;
                    stateDelay_finish = 0;
                }
            }
            break;
            case GAMEOVER: {
                if (stateDelay_gameover > 50) {
                    gameState = LOGO;
                    lifeNumber = 3;
                    stateDelay_gameover = 0;
                }

            }
            break;
        }

        int pointCount = event.getPointerCount();
        for (int i = 0; i < pointCount; i++) {
            rectF.set(0, 360 * scaleY, 60 * scaleX, 420 * scaleY);
            if (rectF.contains(event.getX(i), event.getY(i))) {
                //left running
                mario.setMirror(true);
                mario.setRunning(true);
            }


            rectF.set(120, 360 * scaleY, 180 * scaleX, 420 * scaleY);
            if (rectF.contains(event.getX(i), event.getY(i))) {
                //right running
                mario.setMirror(false);
                mario.setRunning(true);
            }
            rectF.set(740, 360 * scaleY, 800 * scaleX, 420 * scaleY);
            if (rectF.contains(event.getX(i), event.getY(i))) {
                //jump
                if (!mario.isDead() && !mario.isJumping()) {
                    mario.setJumping(true);
                    mySoundPool.play(mySoundPool.getJumpSound());
                    mario.setSpeedY(-16);
                }
            }

            rectF.set(680, 420 * scaleY, 740 * scaleX, 4800 * scaleY);
            if (rectF.contains(event.getX(i), event.getY(i))) {
                mario.fire();
            }
            //手指移开时停止移动
            if (event.getAction() == MotionEvent.ACTION_UP) {
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
        while (isRunning) {
            //游戏暂停
            if (!isPause()) {
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

    private int[][] getMapArray() {
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
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 34, 35, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 34, 35, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 34, 35,
                        0, 0, 27, 27, 27, 27, 27, 27, 27, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 39, 40, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 39, 40, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 39, 40,
                        0, 27, 27, 27, 27, 27, 27, 27, 27, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0 },
                { 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26,
                        26, 26, 26, 26, 26, 0, 0, 26, 26, 26, 26, 26, 26, 26, 26, 26,
                        26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26,
                        26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26,
                        26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26,
                        26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26,
                        26, 26, 26, 26 },
                { 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
                        30, 30, 30, 30, 30, 0, 0, 30, 30, 30, 30, 30, 30, 30, 30, 30,
                        30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
                        30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
                        30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
                        30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
                        30, 30, 30, 30 },
                { 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
                        30, 30, 30, 30, 30, 0, 0, 30, 30, 30, 30, 30, 30, 30, 30, 30,
                        30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
                        30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
                        30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
                        30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
                        30, 30, 30, 30 }
        };
    }

    private int[][] getMapCoverArray() {
        return new int[][]{
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 45, 50, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0 },
                { 0, 0, 0, 55, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 45, 50, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 45, 50, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        55, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 55, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
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
                        0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 0, 0, 0, 0,
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

    private void spritesMove(List<Sprite> sprites,int distenceX){
        if(sprites!=null){
            for (int i = 0; i < sprites.size(); i++) {
                sprites.get(i).move(distenceX,0);
            }
        }
    }

    private void spritesDraw(List<Sprite> sprites,Canvas canvas){
        if(sprites!=null){
            for (int i = 0; i < sprites.size(); i++) {
                sprites.get(i).draw(canvas);
            }
        }
    }

    private void spritesLogic(List<Sprite> sprites){
        if(sprites!=null){
            for (int i = 0; i < sprites.size(); i++) {
                sprites.get(i).logic();
            }
        }
    }



    //endregion

    private void myLogic() {
        switch (gameState) {
            case GAMING: {
                if (!threadRunning && isInited) {
                    thread.start();
                    threadRunning = true;
                }
                mario.logic();
                if (!mario.isDead()) {
                    if(mario.isInvincible()){
                        myMusic.play("music/invincible.mp3",true);
                    }else{
                        myMusic.play("music/bgm.mp3",true);
                    }

                    spritesLogic(cannons);
                    spritesLogic(enemies);
                    spritesLogic(mario.getBullets());
                    spritesLogic(commonBricks);
                    spritesLogic(items);
                    spritesLogic(turtles);
                    spritesLogic(bricks);
                } else {
                    if (!isMaioDieSoundPlayed) {
                        myMusic.play("music/over.mp3",false);
                        isMaioDieSoundPlayed = true;
                    }
                }
                marioMove();
                myStep();
                collisionWithMap();
                myCollision();
                myTime();
            }
            break;

            case TIMEUP: {
                if (stateDelay_timeup++ > 100) {
                    gameState = LIFTCOUNTER;
                    stateDelay_timeup = 0;
                }
                lifeNumber--;
                gameState = LIFTCOUNTER;
            }
            break;
            case LIFTCOUNTER: {
		    	myMusic.stop();
                if (lifeNumber < 1) {
                    gameState = GAMEOVER;
                } else {
                    if (stateDelay_lifecounter++ > 50) {
                        gameState = GAMING;
                        init();
                        stateDelay_lifecounter = 0;
                    }
                }
            }
            break;
            case GAMEOVER: {
                stateDelay_gameover++;
            }
            break;
            case FINISH: {
                myMusic.play("music/finish.mp3",false);
                stateDelay_finish++;
            }
            break;
            case LOGO: {
                stateDelay_logo++;
                myMusic.stop();
            }
            break;
        }
    }

    private void myTime() {
        if (time < 0) {
            gameState = TIMEUP;
        }
    }

    /**
     * 更新分数
     *
     * @param scoreValue 分数值
     */
    private void updateScore(int scoreValue) {
        if (scoreValue > 0) {
            score += scoreValue;
            scoreString = "+" + scoreValue;
        }
        if (scoreValue < 0) {
            score -= scoreValue;
            scoreString = "-" + scoreValue;
        }

    }



    private void myStep() {
        if (tiledLayer_peng01.getX() == 0 && !isEnemyShown1) {
            for (int i = 0; i < 3; i++) {
				Enemy enemy = new Chestunt(32, 32, bitmaps2);
				enemy.setVisiable(true);
				enemy.setPosition(200+60*i, 328);
				enemies.add(enemy);
            }
            isEnemyShown1 = true;
        }
        if (tiledLayer_peng01.getX() == -480 && !isEnemyShown2) {
            for (int i = 0; i < 3; i++) {
                Enemy enemy = new Chestunt(32, 32, bitmaps2);
                enemy.setVisiable(true);
                enemy.setPosition(720 + 60 * i, 328);
                enemies.add(enemy);

                Cannon cannon = new Cannon(40, 80, cannonBitmaps);
                cannon.setVisiable(true);
                cannon.setPosition(720+80*i, 200-40*i);
                EnemyBullet enemyBullet = new EnemyBullet(enemyBulletBitmap);
                enemyBullet.setMirror(false);
                List<Sprite> bullets = new ArrayList<>();
                bullets.add(enemyBullet);
                cannon.setBullets(bullets);
                cannons.add(cannon);
            }

            isEnemyShown2 = true;
        }
        if (tiledLayer_peng01.getX() == -1000 && !isEnemyShown3) {
            for (int i = 0; i < 3; i++) {
                Enemy enemy = new Chestunt(32, 32, bitmaps2);
                enemy.setVisiable(true);
                enemy.setPosition(500 + 60 * i, 0);
                enemies.add(enemy);
            }
            isEnemyShown3 = true;
        }
        if (tiledLayer_peng01.getX() == -2200 && !isEnemyShown4) {
            for (int i = 0; i < 7; i++) {
                Turtle turtle = new Turtle(30, 47, turtleBitmaps);
                turtle.setVisiable(true);
                turtle.setMirror(true);
                turtle.setCanFly(true);
                turtle.setPosition(360 + 40 * i, 240-40*i);
                turtles.add(turtle);
            }
            isEnemyShown4 = true;
        }


        if (tiledLayer_peng01.getX() == -2800) {
            gameState = FINISH;
        }
    }

    /**
     * 处理移动与跳跃的逻辑
     */
    private void marioMove() {

        //1 死亡前先起跳
        if (mario.isDead()) {
            mario.move(0, mario.mSpeedY++);

        } else {
            if (mario.isRunning()) {
                //左移
                if (mario.isMirror()) {
                    mario.move(-4, 0);
                    //未到达屏幕中点
                } else if (mario.getX() < 400) {
                    mario.move(4, 0);
                    //越过屏幕中点 地图移动
                } else if (tiledLayer_peng01.getX() > 800 - tiledLayer_peng01.getCols()
                        * tiledLayer_peng01.getWidth()) {
                    tiledLayer_peng01.move(-4, 0);
                    tiledLayer_cover.move(-4, 0);
                    //到达地图边界 人物移动
                    spritesMove(enemies,-4);
                    spritesMove(cannons,-4);
                    spritesMove(turtles,-4);
                    spritesMove(bricks,-4);
                    spritesMove(commonBricks,-4);
                    spritesMove(items,-4);


                } else {
                    mario.move(4, 0);
                }
            }
            if (mario.isJumping()) {
                mario.move(0, mario.mSpeedY++);
            }
        }
        if (mario.getY() > 400) {
            //2 再次落下才表示游戏结束
            if (!mario.isDead()) {
                mario.setDead(true);
                mario.setSpeedY(-16);
            } else {
                lifeNumber--;
                gameState = LIFTCOUNTER;
            }
        }


    }

    private void init() {
        isInited = false;
        isMaioDieSoundPlayed = false;
        time = 300;
        isEnemyShown1 = false;
        isEnemyShown2 = false;
        isEnemyShown3 = false;
        isEnemyShown4 = false;

        mPaint = new Paint();
        mPaint.setTextSize(20);//设置字号
        mPaint.setColor(Color.WHITE);//画笔颜色
        mPaint.setTypeface(Typeface.createFromAsset(context.getAssets(), "font/Bit8.ttf"));//加载字体文件
        score = sp.getInt("hiscore", 0);//设置画笔字体
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //当显示过开场图片并且时间不为负数且线程停止标志位不为真时
                while (threadRunning && time > 0) {
                    SystemClock.sleep(1000);
                    time--;
                    if (time == 100) {
                        mySoundPool.play(mySoundPool.getHurryUpSound());
                    }
                }
            }
        });

        //加载图片资源
        aBitmap = getBitmap("button/a.png");
        bBitmap = getBitmap("button/b.png");
        downBitmap = getBitmap("button/down.png");
        leftBitmap = getBitmap("button/left.png");
        rightBitmap = getBitmap("button/right.png");
        gameoverbitmap = getBitmap("menu/gameover.png");
        marioBitmap = getBitmap("mario/small/mario_01.png");
        logoBitmap = getBitmap("logo/logo.jpg");
        finishBitmap = getBitmap("logo/finish.jpg");
        Bitmap mushroomBitmap = getBitmap("item/mushroom.png");
        fireBallBitmap = getBitmap("item/object_fireball.png");
        coinBitmap = getBitmap("coin/coin_00.png");
        Bitmap cannonBitmap = getBitmap("enemy/cannon.png");
        enemyBulletBitmap = getBitmap("enemy/enemy_bullet.png");

        cannons = new ArrayList<>();
        turtles = new ArrayList<>();
        List<Bitmap> marioSmallBitmaps = new ArrayList<>();
        ArrayList<Bitmap> marioFireInvBitmaps = new ArrayList<>();
        ArrayList<Bitmap> marioSmallInvBitmaps = new ArrayList<>();
        ArrayList<Bitmap> marioBigInvincibleBitmaps = new ArrayList<>();
        commonBricks = new ArrayList<>();
        List<Bitmap> commonbrickbitmaps = new ArrayList<>();
        bricks = new ArrayList<>();
        items = new ArrayList<>();
        List<Bitmap> marioBigBitmaps = new ArrayList<>();
        List<Bitmap> marioFireBitmaps = new ArrayList<>();
        List<Bitmap> flowerBitmaps = new ArrayList<>();
        cannonBitmaps = new ArrayList<>();
        //设置数组型图片资源
        for (int i = 0; i < 7; i++) {
            marioBigBitmaps.add(getBitmap("mario/big/mario_0"+i+".png"));
            marioSmallBitmaps.add(getBitmap("mario/small/mario_0"+i+".png"));
            marioFireBitmaps.add(getBitmap("mario/fire/mario_0"+i+".png"));
            marioFireInvBitmaps.add(getBitmap("mario/fire/mario_inv_0"+i+".png"));
            marioSmallInvBitmaps.add(getBitmap("mario/small/mario_inv_0"+i+".png"));
            marioBigInvincibleBitmaps.add(getBitmap("mario/big/mario_inv_0"+i+".png"));
        }
        bitmaps2 = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            bitmaps2.add(getBitmap("enemy/enemy" + i + ".png"));
        }
        enemies = new ArrayList<>();
        List<Bitmap> coinBitmaps = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            coinBitmaps.add(getBitmap(String.format(Locale.CHINA,
                    "coin/coin_%02d.png", i)));
        }
        for (int i = 0; i < 4; i++) {
            flowerBitmaps.add(getBitmap("item/flower/flower_0"+i+".png"));
        }
        cannonBitmaps.add(cannonBitmap);
        List<Bitmap> brickBitmaps = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            brickBitmaps.add(getBitmap(String.format(Locale.CHINA, "brick/brick_%02d.png", i)));
        }
        turtleBitmaps = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            turtleBitmaps.add(getBitmap(String.format(Locale.CHINA, "turtle/turtle_%01d.png", i)));
        }

        List<Bitmap> starBitmaps = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            starBitmaps.add(getBitmap(String.format(Locale.CHINA,"item/star/item_star_%02d.png",
                    i)));
        }

        //存放问号砖块行列坐标
        int[][] itemBrickPositions = {
                {4,3},{4,6},{47,6},{15,6}
        };

        int[][] coinsPostions={
                {10,0},{10,1},{10,2},{10,3},{10,4},{11,2},{12,0},{12,1},{12,2},{12,3},{12,4},
                {14,0},{14,1},{14,2},{14,3},{14,4},{15,0},{16,0},{15,2},{16,2},{15,4},{16,4},
                {18,0},{18,1},{18,2},{18,3},{18,4},{19,4},{20,4},
                {22,0},{22,1},{22,2},{22,3},{22,4},{23,4},{24,4},
                {26,1},{26,2},{26,3},{28,1},{28,2},{28,3},{27,0},{27,4},
                {36,1},{37,1},{38,1},{39,1},{40,1},{41,1},{42,1},{43,1}

        };
        //创建金币
        for (int[] coinposition:coinsPostions){
            Coin coin = new Coin(40, 40, coinBitmaps);
            coin.setVisiable(true);
            coin.setPosition(coinposition[0]*40,coinposition[1]*40);
            items.add(coin);
        }

        int[][] commonBrickPostions = {
                {3,6},{5,6},{30,7},{32,6},{34,5},{36,3},{43,3},{36,4},{37,4},{38,4},{39,4},{40,4}
                ,{41,4},{42,4},{43,4},{14,6},{16,6},{21,6},{22,6},{50,6},{51,6},{52,6},{53,6},{54,6}

        };

        //创建砖块
        Brick brick = new Brick(40, 40, brickBitmaps);
        brick.setPosition(40 * itemBrickPositions[0][0], 40 * itemBrickPositions[0][1]);
        brick.createItem(true, starBitmaps, ItemType.Star);
        items.add(brick.getItemSprite());
        brick.setVisiable(true);
        bricks.add(brick);
        Brick brick2 = new Brick(40, 40, brickBitmaps);
        brick2.setPosition(40 * itemBrickPositions[1][0], 40 * itemBrickPositions[1][1]);

        brick2.createItem(true, mushroomBitmap, ItemType.Mushroom);
        items.add(brick2.getItemSprite());
        brick2.setVisiable(true);
        bricks.add(brick2);

        Brick brick3 = new Brick(40, 40, brickBitmaps);
        brick3.setPosition(40 * itemBrickPositions[2][0], 40 * itemBrickPositions[2][1]);
        brick3.createItem(true, starBitmaps, ItemType.Star);
        items.add(brick3.getItemSprite());
        brick3.setVisiable(true);
        bricks.add(brick3);

        Brick brick4 = new Brick(40, 40, brickBitmaps);
        brick4.setPosition(40 * itemBrickPositions[3][0], 40 * itemBrickPositions[3][1]);
        brick4.createItem(true, flowerBitmaps, ItemType.Flower);
        items.add(brick4.getItemSprite());
        brick4.setVisiable(true);
        bricks.add(brick4);
        

        commonbrickbitmaps.add(getBitmap("brick/commonBrick.png"));

        for (int[] position : commonBrickPostions) {
            CommonBrick commonBrick = new CommonBrick(40, 40, commonbrickbitmaps);
            commonBrick.setVisiable(true);
            commonBrick.setCanBroken(false);
            commonBrick.setPosition(40 * position[0], 40 * position[1]);
            commonBricks.add(commonBrick);
        }


        rectF = new RectF();
        //初始化玛丽
        mario = new Mario(32, 32, marioSmallBitmaps);
        mario.setVisiable(true);
        mario.setPosition(0, 330);
        List<List<Bitmap>> bitmapsList = new ArrayList<>();
        bitmapsList.add(marioSmallBitmaps);
        bitmapsList.add(marioBigBitmaps);
        bitmapsList.add(marioFireBitmaps);
        bitmapsList.add(marioSmallInvBitmaps);
        bitmapsList.add(marioBigInvincibleBitmaps);
        bitmapsList.add(marioFireInvBitmaps);
        mario.setBitmapsList(bitmapsList);



        //初始化地图
        int map_peng01[][] = getMapArray();
        int map_cover[][] = getMapCoverArray();
        Bitmap mapBitmap = getBitmap("map/map1.png");
        tiledLayer_peng01 = new TiledLayer(mapBitmap, 100, 12, 40, 40);
        tiledLayer_cover = new TiledLayer(mapBitmap, 100, 12, 40, 40);
        tiledLayer_peng01.setTiledCell(map_peng01);
        tiledLayer_cover.setTiledCell(map_cover);


        if (isFirstRun) {
            gameState = LOGO;
            isFirstRun = false;
        } else {
            gameState = GAMING;
        }
        isInited = true;


    }

    private void myDraw() {
        try {
            lockCanvas = holder.lockCanvas();
            lockCanvas.save();
            lockCanvas.scale(scaleX, scaleY);
            lockCanvas.drawColor(Color.parseColor("#3786ef"));
            switch (gameState) {
                case LOGO: {
                    lockCanvas.drawBitmap(logoBitmap, 0, 0, null);
                }
                break;
                case GAMEOVER: {
                    lockCanvas.drawColor(Color.BLACK);
                    lockCanvas.drawBitmap(gameoverbitmap,
                            400 - gameoverbitmap.getWidth() / 2,
                            250 - gameoverbitmap.getHeight() / 2, null);
                }
                break;
                case FINISH: {
                    lockCanvas.drawBitmap(finishBitmap, 0, 0, null);
                }
                break;
                case LIFTCOUNTER: {
                    lockCanvas.drawColor(Color.BLACK);
                    lockCanvas.drawBitmap(marioBitmap, 400 - marioBitmap.getWidth(),
                            250 - marioBitmap.getHeight(), null);
                    lockCanvas.drawText(String.format(Locale.CHINA, "X %02d", lifeNumber),
                            440 - marioBitmap.getWidth(),
                            285 - marioBitmap.getHeight(), mPaint);
                }
                break;
                case GAMING: {
                    tiledLayer_cover.draw(lockCanvas);
                    tiledLayer_peng01.draw(lockCanvas);
                    spritesDraw(enemies, lockCanvas);
                    spritesDraw(cannons, lockCanvas);
                    spritesDraw(turtles, lockCanvas);
                    spritesDraw(bricks, lockCanvas);
                    spritesDraw(commonBricks, lockCanvas);
                    spritesDraw(items, lockCanvas);
                    mario.draw(lockCanvas);
                    lockCanvas.drawBitmap(aBitmap, 680, 420, null);
                    lockCanvas.drawBitmap(bBitmap, 740, 360, null);
//		            lockCanvas.drawBitmap(downBitmap, 60, 420, null);
                    lockCanvas.drawBitmap(leftBitmap, 0, 360, null);
                    lockCanvas.drawBitmap(rightBitmap, 120, 360, null);

                    //绘制单次得分
                    if (scoreString != null) {
                        lockCanvas.drawText(scoreString, mario.getX() + 25, mario.getY() - 20 - 3 * delay, mPaint);
                        if (delay++ == 15) {
                            scoreString = null;
                            delay = 0;
                        }
                    }
                    //绘制总分数
                    lockCanvas.drawText(String.format(Locale.CHINA, "SCORE:%06d", score), 30, 30, mPaint);
                    if (mario.getBullets() != null) {
                        for (int i = 0; i < mario.getBullets().size(); i++) {
                            mario.getBullets().get(i).draw(lockCanvas);
                        }
                    }
                    //绘制金币数
                    lockCanvas.drawBitmap(coinBitmap, 300, 0, null);
                    lockCanvas.drawText(String.format(Locale.CHINA, "x %02d", coinNumber), 340, 30, mPaint);
                    //绘制剩余时间
                    lockCanvas.drawText(String.format(Locale.CHINA, "TIME:%03d", time), 700, 30, mPaint);
                }
                break;
            }

            lockCanvas.restore();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (lockCanvas != null)
                holder.unlockCanvasAndPost(lockCanvas);
        }
    }

    /**
     * 玛丽奥与砖块碰撞
     */
    private void myCollision() {
        //玛丽与普通砖块
        sptiteCollisionSprite(mario, commonBricks);
        //玛丽与问号砖块
        sptiteCollisionSprite(mario, bricks);
        //玛丽与大炮
        sptiteCollisionSprite(mario, cannons);
        //道具与砖块碰撞
        sptitesCollisionSprites(items, bricks);
        //道具与普通砖块碰撞
        sptitesCollisionSprites(items, commonBricks);
        //乌龟与砖块碰撞
        sptitesCollisionSprites(turtles, bricks);
        //乌龟与问号砖块
        sptitesCollisionSprites(turtles, commonBricks);
        //敌人与砖块
        sptitesCollisionSprites(enemies, bricks);
        sptitesCollisionSprites(enemies, commonBricks);
        //玛丽子弹与砖块
        sptitesCollisionSprites(mario.getBullets(), bricks);
        sptitesCollisionSprites(mario.getBullets(), commonBricks);

        if (!mario.isDead()) {
            //玛丽与板栗碰撞
            marioCollisionWith(enemies);
            marioCollisionWith(turtles);
            marioCollisionWith(items);
            //玛丽与大炮子弹碰撞
            if (cannons != null) {
                for (int w = 0; w < cannons.size(); w++) {
                    List<Sprite> bullets = ((Cannon) cannons.get(w)).getBullets();
                    marioCollisionWith(bullets);
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
    private void collisionWithMap() {
        //玛丽碰撞
        if (!mario.isDead()) {
            spriteCollisionMap(mario);
            spritesCollisionMap(mario.getBullets());
        }
        //敌人碰撞
        spritesCollisionMap(enemies);
        spritesCollisionMap(turtles);
        //道具与地图碰撞
        spritesCollisionMap(items);
    }

    //碰撞
    //精灵与精灵
    //玛丽与敌人
    //精灵与地图

    /**
     * 玛丽与一组精灵
     *
     * @param sprites 精灵数组
     */
    private void marioCollisionWith(List<Sprite> sprites) {
        if (sprites != null) {
            for (int i = 0; i < sprites.size(); i++) {
                Sprite sprite = sprites.get(i);
                marioCollisionWith(sprite);
                if(!(sprite instanceof ItemSprite)){
                    marioBulletCollisionWithEnemy(sprite);
                }
            }
        }
    }



    /**
     * 玛丽与精灵碰撞
     *
     * @param sprite 单个精灵
     */
    private void marioCollisionWith(Sprite sprite) {

        if (mario.collisionWith(sprite)) {
            //与道具碰撞
            if(sprite instanceof ItemSprite){
                if(sprite instanceof EnemyBullet){
                    if (mario.isInvincible()) {
                        updateScore(100);
                        sprite.setVisiable(false);
                    } else if (mario.isJumping() && mario.siteCollisionWith(sprite,下)) {
                            mySoundPool.play(mySoundPool.getHitEnemySound());
                            //杀死敌人时获得100积分
                            updateScore(100);
                            mario.setSpeedY(-10);
                            sprite.setVisiable(false);
                    } else {
                        if (!mario.isZeroDamage()&&sprite.isVisiable()) {
                            int status = mario.getStatus();
                            if (status != 0) {
                                mario.shapeShift(status-1);
                                mario.setZeroDamage(true);
                            } else {
                                mario.setDead(true);
                                mario.setSpeedY(-16);
                            }
                        }
                    }
                }else{
                    int sound = mySoundPool.getItemSound();
                    int status = mario.getStatus();
                    if(sprite instanceof Mushroom){
                        mySoundPool.play(mySoundPool.getItemSound());
                        if (status == 0) {
                            mario.shapeShift(1);
                        }
                    }
                    if (sprite instanceof Flower) {
                        //花
                        if (status != 2) {
                            //非第三状态都执行
                            mario.shapeShift(2);
                            //设置弹夹
                            List<Sprite> bullets = new ArrayList<>();
                            for (int j = 0; j < 5; j++) {
                                Bullet bullet = new Bullet(fireBallBitmap);
                                if (mario.isMirror()) {
                                    bullet.setMirror(false);
                                } else {
                                    bullet.setMirror(true);
                                }
                                bullets.add(bullet);
                            }
                            mario.setBullets(bullets);
                        }
                    }
                    if (sprite instanceof Star) {
                        //玛丽吃到无敌星后变成无敌状态
                        mario.setInvincible(true);
                    }
                    if(sprite instanceof Coin) {
                        coinNumber++;
                        sound = mySoundPool.getCoinSound();
                    }
                    mySoundPool.play(sound);
                    sprite.setVisiable(false);
                    updateScore(100);
                }



            }else{
                //与敌人碰撞
                if (mario.isInvincible()) {
                    //如果乌龟带翅膀则切换状态
                    if (sprite instanceof Turtle) {
                        //乌龟在带翅膀状态被击中有免伤时间
                        if(!((Turtle) sprite).isZeroDamage()){
                            if (((Turtle) sprite).isCanFly()) {
                                ((Turtle) sprite).setCanFly(false);
                                ((Turtle) sprite).setZeroDamage(true);
                                updateScore(100);
                                mario.setSpeedY(-10);
                                mario.setJumping(true);
                            } else {
                                sprite.setDead(true);
                                mySoundPool.play(mySoundPool.getHitEnemySound());
                                updateScore(100);
                                mario.setSpeedY(-10);
                                mario.setJumping(true);
                            }
                        }
                    } else {
                        sprite.setDead(true);
                        mySoundPool.play(mySoundPool.getHitEnemySound());
                        //杀死敌人时获得100积分
                        updateScore(100);
                    }
                } else if (mario.isJumping()) {
                    if (sprite instanceof Turtle) {
                        //乌龟在带翅膀状态被击中有免伤时间
                        if(!((Turtle) sprite).isZeroDamage()){
                            if (((Turtle) sprite).isCanFly()) {
                                ((Turtle) sprite).setCanFly(false);
                                ((Turtle) sprite).setZeroDamage(true);
                                updateScore(100);
                                mario.setSpeedY(-10);
                            } else {
                                sprite.setDead(true);
                                mySoundPool.play(mySoundPool.getHitEnemySound());
                                updateScore(100);
                            }
                        }
                    } else {
                        sprite.setDead(true);
                        mySoundPool.play(mySoundPool.getHitEnemySound());
                        //杀死敌人时获得100积分
                        updateScore(100);
                        mario.setSpeedY(-10);
                    }
                } else {
                    if (!mario.isZeroDamage()&&!sprite.isDead()) {
                        int status = mario.getStatus();
                        if (status != 0) {
                            mario.shapeShift(status-1);
                            mario.setZeroDamage(true);
                        } else {
                            mario.setDead(true);
                            mario.setSpeedY(-16);
                        }
                    }
                }
            }
        }
    }


    private void marioBulletCollisionWithEnemy(Sprite sprite) {
        List<Sprite> bullets = mario.getBullets();
        if (bullets != null) {
            for (int i = 0; i < bullets.size(); i++) {
                Sprite sprite1 = bullets.get(i);
                //玛丽子弹不能消灭敌人子弹
                if (sprite1.isVisiable() && sprite1.collisionWith(sprite) && !(sprite instanceof EnemyBullet)) {
                    //乌龟在带翅膀状态被击中有免伤时间
                    if(sprite instanceof  Turtle){
                        if(!((Turtle) sprite).isZeroDamage()){
                            if (((Turtle) sprite).isCanFly()) {
                                ((Turtle) sprite).setCanFly(false);
                                ((Turtle) sprite).setZeroDamage(true);
                                updateScore(100);
                            } else {
                                sprite.setDead(true);
                                mySoundPool.play(mySoundPool.getHitEnemySound());
                                updateScore(100);
                            }
                        }
                    }
                    else{
                        sprite1.setVisiable(false);
                        sprite.setVisiable(false);
                        sprite.setDead(true);
                        updateScore(100);
                    }
                }
            }
        }
    }

    /**
     * 精灵与地图（碰撞层）碰撞
     *
     * @param sprites 精灵数组
     */

    private void spritesCollisionMap(List<Sprite> sprites) {
        if (sprites != null) {
            for (int i = 0; i < sprites.size(); i++) {
                spriteCollisionMap(sprites.get(i));
            }
        }
    }

    /**
     * 精灵与地图（碰撞层）碰撞
     *
     * @param sprite 单个精灵
     */
    private void spriteCollisionMap(Sprite sprite) {
        if (sprite.isVisiable()) {
            //头顶碰撞
            if (sprite.siteCollisionWith(tiledLayer_peng01, 上左) ||
                    sprite.siteCollisionWith(tiledLayer_peng01, 上中) ||
                    sprite.siteCollisionWith(tiledLayer_peng01, 上右)) {
                if (sprite instanceof Bullet) {
                    sprite.setVisiable(false);
                } else {
                    sprite.setSpeedY(Math.abs(sprite.getSpeedY()));
                }
            }
            //脚部碰撞
            if (sprite.siteCollisionWith(tiledLayer_peng01, 下左) ||
                    sprite.siteCollisionWith(tiledLayer_peng01, 下中) ||
                    sprite.siteCollisionWith(tiledLayer_peng01, 下右)) {
                //乌龟特殊处理
                if (sprite instanceof Turtle) {
                    if (((Turtle) sprite).isCanFly()) {
                        sprite.setJumping(true);
                        sprite.setSpeedY(-10);
                    } else {
                        sprite.setJumping(false);
                        int footY = sprite.getY() + sprite.getHeight();
                        int newY = footY / tiledLayer_peng01.getHeight()
                                * tiledLayer_peng01.getHeight()
                                - sprite.getHeight();
                        sprite.setPosition(sprite.getX(), newY);
                    }
                    //子弹特殊处理
                } else if (sprite instanceof Bullet || sprite instanceof Star) {
                    sprite.setSpeedY(-10);
                    sprite.setJumping(true);
                } else {
                    sprite.setJumping(false);
                    //坐标修正
                    //取得脚部坐标
                    int footY = sprite.getY() + sprite.getHeight();
                    //取整
                    int newY = footY / tiledLayer_peng01.getHeight()
                            * tiledLayer_peng01.getHeight()
                            - sprite.getHeight();
                    sprite.setPosition(sprite.getX(), newY);
                }
            }
            //落地判断
            else if (!sprite.isJumping()) {
                sprite.setJumping(true);
                sprite.setSpeedY(0);
            }
            //左边碰撞
            if (sprite.siteCollisionWith(tiledLayer_peng01, 左上) ||
                    sprite.siteCollisionWith(tiledLayer_peng01, 左中) ||
                    sprite.siteCollisionWith(tiledLayer_peng01, 左下)) {
                if (sprite instanceof Bullet) {
                    sprite.setVisiable(false);
                } else {
                    sprite.setMirror(true);
                    sprite.move(4, 0);
                }
            }
            //右边碰撞
            if (sprite.siteCollisionWith(tiledLayer_peng01, 右上) ||
                    sprite.siteCollisionWith(tiledLayer_peng01, 右中) ||
                    sprite.siteCollisionWith(tiledLayer_peng01, 右下)) {
                if (sprite instanceof Bullet) {
                    sprite.setVisiable(false);
                } else {
                    sprite.setMirror(false);
                    sprite.move(-4, 0);
                }
            }
        }
    }


    /**
     * 精灵碰撞
     *
     * @param sprites0 主动碰撞的精灵数组
     * @param sprites1 被碰精灵数组
     */


    private void sptitesCollisionSprites(List<Sprite> sprites0, List<Sprite> sprites1) {
        if (sprites0 != null) {
            for (int i = 0; i < sprites0.size(); i++) {
                sptiteCollisionSprite(sprites0.get(i), sprites1);
            }
        }
    }


    /**
     * 精灵碰撞
     *
     * @param sprite0 主动碰撞的精灵
     * @param sprites 被碰精灵数组
     */

    private void sptiteCollisionSprite(Sprite sprite0, List<Sprite> sprites) {
        if (sprites != null) {
            for (int i = 0; i < sprites.size(); i++) {
                Sprite sprite1 = sprites.get(i);
                sptiteCollisionSprite(sprite0, sprite1);
            }
        }
    }

    /**
     * 精灵碰撞
     *
     * @param sprite0 主动碰撞的精灵
     * @param sprite1 被碰精灵
     */
    private void sptiteCollisionSprite(Sprite sprite0, Sprite sprite1) {
        if (sprite0.isVisiable() && sprite1.isVisiable()) {
            if (sprite0.siteCollisionWith(sprite1, 下) && sprite0.isJumping()) {
                //乌龟特殊处理
                if (sprite0 instanceof Turtle) {
                    sprite0.setJumping(true);
                    sprite0.setSpeedY(-10);
                }
                //子弹与无敌星弹跳处理
                if (sprite0 instanceof Bullet || sprite0 instanceof Star) {
                    sprite0.setSpeedY(-10);
                    sprite0.setJumping(true);
                } else {
                    sprite0.setJumping(false);
                    //坐标修正
                    //取得脚部坐标
                    int footY = sprite0.getY() + sprite0.getHeight();
                    //取整
                    int newY = footY / tiledLayer_peng01.getHeight()
                            * tiledLayer_peng01.getHeight()
                            - sprite0.getHeight();
                    sprite0.setPosition(sprite0.getX(), newY);
                }
            }
            if (sprite0.siteCollisionWith(sprite1, Site.上)) {
                //玛丽顶砖块
                if (sprite0 instanceof Mario && sprite0.isJumping()) {
                    if (sprite1 instanceof CommonBrick) {
                        sprite0.setSpeedY(Math.abs(sprite0.getSpeedY()));
                        if (!sprite1.isJumping()) {
                            if (mario.getStatus() != 0) {
                                ((CommonBrick) sprite1).setCanBroken(true);
                                sprite1.setSpeedY(-4);
                                sprite1.setJumping(true);
                                mySoundPool.play(mySoundPool.getHitbrickSound());
                            }else{
                                sprite1.setSpeedY(-4);
                                sprite1.setJumping(true);
                                mySoundPool.play(mySoundPool.getCannotbreakSound());
                            }
                        }
                    } else if (sprite1 instanceof Brick) {
                        sprite0.setSpeedY(Math.abs(sprite0.getSpeedY()));
                        if (((Brick) sprite1).hasItem()) {
                            mySoundPool.play(mySoundPool.getHitbrickSound());
                            sprite1.setSpeedY(-4);
                            sprite1.setJumping(true);
                        }
                    }
                } else {
                    if (sprite0 instanceof Bullet) {
                        sprite0.setVisiable(false);
                    } else {
                        sprite0.setSpeedY(Math.abs(sprite0.getSpeedY()));
                    }
                }

            }
            if (sprite0.siteCollisionWith(sprite1, Site.左)) {
                if (sprite0 instanceof Bullet) {
                    sprite0.setVisiable(false);
                } else {
                    sprite0.setMirror(true);
                    sprite0.move(4, 0);
                }
            }
            if (sprite0.siteCollisionWith(sprite1, 右)) {
                if (sprite0 instanceof Bullet) {
                    sprite0.setVisiable(false);
                } else {
                    sprite0.setMirror(false);
                    sprite0.move(-4, 0);
                }
            }

        }
    }
}
