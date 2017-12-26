package com.suramire.androidgame25.audio;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

import java.io.IOException;

public class MyMusic {
	
	private Context mContext;
	private MediaPlayer mediaPlayer;
	private String mFileName = "";
		
	public MyMusic(Context mContext) {
		super();
		this.mContext = mContext;
		mediaPlayer = new MediaPlayer();
	}

    public void play(String fileName,boolean looping){
            if(mFileName.equals(fileName)){
                return;
            }else{
                try {
                    mediaPlayer.reset();
                    mFileName = fileName;
                    AssetFileDescriptor fd = mContext.getAssets().openFd(fileName);
                    mediaPlayer.setDataSource(fd.getFileDescriptor(),
                            fd.getStartOffset(),
                            fd.getLength());
                    mediaPlayer.setLooping(looping);
                    mediaPlayer.prepare();
                    mediaPlayer.start();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }
	public void stop(){
		if(mediaPlayer.isPlaying()){
			mediaPlayer.stop();
		}
	}
	
	public void pause(){
        try {
            if(mediaPlayer.isPlaying()){
                mediaPlayer.pause();
            }
        } catch (IllegalStateException e) {
        }
    }



}
