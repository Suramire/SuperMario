package com.suramire.androidgame25;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.List;

/**
 * Created by Suramire on 2017/11/9.
 */

public class Mario extends Sprite {

    //region Fields
    private boolean isMirror;//是否翻转
    private boolean isRunning;//是否跑动
    private boolean isJumping;//是否跳跃
    private boolean isDead;//是否死亡
    //endregion

    //region Getter & Setter
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
    //endregion

    public Mario(int width, int height, List<Bitmap> bitmaps) {
        super(width, height, bitmaps);
    }

    @Override
    public void logic() {
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

    @Override
    public void draw(Canvas canvas) {
        if(isMirror()){
            canvas.save();
            //翻转画布 相当于翻转人物
            canvas.scale(-1,1,getX()+getWidth()/2,getY()+getHeight()/2);
            super.draw(canvas);
            canvas.restore();
        }else{
            super.draw(canvas);
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
            case 下中:{

                if(collisionWith(sprite)
                        && sy > y
                        && h >=sy-y
                        && x+w/4>=sx
                        && x+w/4 <=sx+sw
                        ){
//                    Log.e("MyView2", "y:" + y+" h:"+h + " sy:"+sy +" sh:"+sh +" x:"+x+" sx:"+sx +" w:"+w+ " sw:"+sw);
                    return true;
                }
            }break;
            case 上中:{

                if(collisionWith(sprite)
                        && sy + sh >= y //砖块高于玛丽最多一行高度
                        && x + w /4>= sx//玛丽右3/4宽度可以顶砖块
                        && x + w /4<= sx + sw//玛丽左3/4宽度可以顶砖块
                        ){
//                    Log.e("MyView2", "y:" + y+" h:"+h + " sy:"+sy +" sh:"+sh +" x:"+x+" sx:"+sx +" w:"+w+ " sw:"+sw);
                    return true;
                }
            }break;


//

            case 右中:{
                if(collisionWith(sprite)
                        &&x+w==sx
                        &&sy - y <h//只和同一行砖块左右碰撞
                        ){
                    return true;
                }
            }break;
            case 左中:{
                if(collisionWith(sprite)
                        &&sx+sw==x
//                        &&y-sy<sh
                        &&sy - y <h//只和同一行砖块左右碰撞
                        ){
                    return true;
                }
            }break;
//


        }

        return false;
    }


}
