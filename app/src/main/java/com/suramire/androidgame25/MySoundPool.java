package com.suramire.androidgame25;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import java.io.IOException;

public class MySoundPool {
	private Context mContext;
	private SoundPool mSoundPool;
    private int hitbrickSound;
    private int coinSound;
    private int hurryUpSound;
    private int hitEnemySound;
    private int jumpSound;
	private int marioDieSound;
	private int mushroomSound;
	
	public int getHitEnemySound() {
		return hitEnemySound;
	}
	
	public int getJumpSound() {
		return jumpSound;
	}
	
	public int getHurryUpSound() {
		return hurryUpSound;
	}
	
	public int getCoinSound() {
		return coinSound;
	}
	
	public int getHitbrickSound() {
		return hitbrickSound;
	}

	
	public int getMushroomSound() {
		return mushroomSound;
	}
	
	public int getMarioDieSound() {
		return marioDieSound;
	}
	
	public MySoundPool(Context mContext) {
		super();
		this.mContext = mContext;
		mSoundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
		marioDieSound = getSoundId("sounds/over.mp3");
		mushroomSound = getSoundId("sounds/mushroom.mp3");
		hitbrickSound = getSoundId("sounds/duang.mp3");
		coinSound = getSoundId("sounds/coin.mp3");
		hurryUpSound = getSoundId("sounds/hurryup.mp3");
		jumpSound = getSoundId("sounds/jump.mp3");
		hitEnemySound = getSoundId("sounds/hitenemy.mp3");
		
		
	}
	
	public void play(int soundID){
		mSoundPool.play(soundID, 1, 1, 1, 1, 1);
	}
	
	public int getSoundId(String fileName){
		int soundId = 0;
		try {
            soundId = mSoundPool.load(mContext.getAssets().openFd(fileName), 1);
		} catch (IOException e) {
            Log.d("MySoundPool", e.getMessage());
        }
		return soundId;
	}
}
