package com.suramire.androidgame25.audio;

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
    private int cannotbreakSound;
    private int hurtSound;
    private int cannonSound;
    private int transferSound;
    private int brokenSound;

    public int getBrokenSound() {
        return brokenSound;
    }

    public int getTransferSound() {
        return transferSound;
    }

    public int getCannonSound() {
        return cannonSound;
    }

    public int getHurtSound() {
        return hurtSound;
    }

    public int getCannotbreakSound() {
        return cannotbreakSound;
    }

    private int itemSound;

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

	
	public int getItemSound() {
		return itemSound;
	}
	

	
	public MySoundPool(Context mContext) {
		super();
		this.mContext = mContext;
		mSoundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        cannotbreakSound =getSoundId("sounds/cannotbreak.mp3");
		itemSound = getSoundId("sounds/mushroom.mp3");
		hitbrickSound = getSoundId("sounds/duang.mp3");
		coinSound = getSoundId("sounds/coin.mp3");
		hurryUpSound = getSoundId("sounds/hurryup.mp3");
		jumpSound = getSoundId("sounds/jump.mp3");
		hitEnemySound = getSoundId("sounds/hitenemy.mp3");
        hurtSound = getSoundId("sounds/hurt.mp3");
        cannonSound = getSoundId("sounds/cannon.mp3");
        transferSound = getSoundId("sounds/transfer.mp3");
        brokenSound = getSoundId("sounds/broken.mp3");


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
