package com.suramire.androidgame25.enemy;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.suramire.androidgame25.item.EnemyBullet;
import com.suramire.androidgame25.Sprite;

import java.util.ArrayList;
import java.util.List;

/**
 * 敌人类-大炮
 */

public class Cannon extends Enemy {

    private List<Sprite> bullets;
    private long delay2;

    public List<Sprite> getBullets() {
        return bullets;
    }

    public void setBullets(List<Sprite> bullets) {
        this.bullets = bullets;
    }

    public Cannon(int width, int height, List<Bitmap> bitmaps) {
        super(width, height, bitmaps);
        bullets = new ArrayList<>();
    }

    @Override
    public void logic() {
        if(isJumping()){
            move(0,mSpeedY++);
        }
        if(delay2++>90){
            fire();
            delay2=0;
        }
        if(bullets!=null){
            for (int i = 0; i <bullets.size(); i++) {
                bullets.get(i).logic();
            }
        }
    }

    public void fire(){
        if(bullets!=null){
            for (int i = 0; i < bullets.size(); i++) {
                EnemyBullet enemyBullet = (EnemyBullet) bullets.get(i);
                if(!enemyBullet.isVisiable()){
                    enemyBullet.setVisiable(true);
                    enemyBullet.setDead(false);
                    if(enemyBullet.isMirror()){
                        enemyBullet.setPosition(getX()+getWidth()-5,getY()+6);
                    }else{
                        enemyBullet.setPosition(getX()-15,getY()+6);
                    }

                }
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if(bullets!=null){
            for (int i = 0; i < bullets.size(); i++) {
                bullets.get(i).draw(canvas);
            }
        }
    }
}
