package com.example.basicmusic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageButton;
import java.util.List;

import phucdv.android.musichelper.MediaHelper;
import phucdv.android.musichelper.Song;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private SongAdapter mAdapter;
    private ImageButton mPrev;
    private ImageButton mPlayPause;
    private ImageButton mNext;
    private MusicService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 999);
        } else {
            doRetrieveAllSong();
        }

        mRecyclerView = findViewById(R.id.rcy_song);
        mAdapter = new SongAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setItemClickListener(new SongAdapter.OnItemClickListener() {
            @Override
            public void onClick(View v, Song song, int pos) {
                mPlayPause.setImageResource(R.drawable.ic_baseline_pause_24);
                mService.playAt(pos);
            }
        });

        mPrev = findViewById(R.id.btn_prev);
        mPlayPause = findViewById(R.id.btn_play_pause);
        mNext = findViewById(R.id.btn_next);

        mPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAdapter.getMusicController().getCurrentIndex() >= 0) {
                    if (mAdapter.getMusicController().isPlaying()) {
                        mAdapter.pause();
                        mPlayPause.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                        mService.pause();
                    } else {
                        mAdapter.start();
                        mPlayPause.setImageResource(R.drawable.ic_baseline_pause_24);
                        mService.play();
                    }
                }
            }
        });

        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.playNext();
                mPlayPause.setImageResource(R.drawable.ic_baseline_pause_24);
                mService.next();
            }
        });

        mPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.playPrev();
                mPlayPause.setImageResource(R.drawable.ic_baseline_pause_24);
                mService.prev();
            }
        });

        Intent intent = new Intent(this, MusicService.class);
        startForegroundService(intent);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MusicService.MusicBinder musicBinder = (MusicService.MusicBinder) iBinder;
            mService = musicBinder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 999) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                doRetrieveAllSong();
            }
        }
    }

    private void doRetrieveAllSong(){
        MediaHelper.retrieveAllSong(this, new MediaHelper.OnFinishRetrieve() {
            @Override
            public void onFinish(List<Song> list) {
                mAdapter.setData(list);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }
}