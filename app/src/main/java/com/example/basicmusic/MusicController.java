package com.example.basicmusic;

import android.content.ContentUris;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import phucdv.android.musichelper.Song;

public class MusicController {
    private Context mContext;
    private MediaPlayer mMediaPlayer;
    private List<Song> mData;
    private int mCurrentIndex;
    private boolean mIsPreparing;

    public MusicController(Context context) {
        mContext = context;

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
                mIsPreparing = false;
            }
        });

        mData = new ArrayList<>();
        mCurrentIndex = -1;
        mIsPreparing = false;
    }

    public void setData(List<Song> data) {
        mData = data;
    }

    public List<Song> getData() {
        return mData;
    }

    public int getCurrentIndex() {
        return mCurrentIndex;
    }

    public boolean isPlaying(){
        return mMediaPlayer.isPlaying();
    }

    public void isFinishing(){
        mMediaPlayer.release();
    }
    public boolean isPreparing(){
        return mIsPreparing;
    }

    public void playNext() {
        if(mData.size() != 0) {
            if (mCurrentIndex < mData.size() - 1) {
                mCurrentIndex++;
            } else {
                mCurrentIndex = 0;
            }
            playSongAt(mCurrentIndex);
        }
    }

    public void playPrev() {
        if(mData.size() != 0) {
            if (mCurrentIndex > 0) {
                mCurrentIndex--;
            } else {
                mCurrentIndex = mData.size() - 1;
            }
            playSongAt(mCurrentIndex);
        }
    }

    public void pause() {
        mMediaPlayer.pause();
    }

    public void start() {
        mMediaPlayer.start();
    }

    public void playSongAt(int index) {
        mMediaPlayer.reset();
        Uri trackUri = ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                mData.get(index).getId());
        try {
            mMediaPlayer.setDataSource(mContext, trackUri);
            mCurrentIndex = index;
        } catch (Exception e) {
            Log.e("MUSIC SERVICE", "Error starting data source", e);
        }
        mMediaPlayer.prepareAsync();
        mIsPreparing = true;
    }

    public void destroyMusic(){
        assert mMediaPlayer != null;
        if (mMediaPlayer.isPlaying()){
            mMediaPlayer.stop();
            isFinishing();
            mMediaPlayer = null;
        }
    }
}
