package com.example.basicmusic;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Binder;
import android.os.IBinder;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class MusicService extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel("my_chanel", "my_chanel_name");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) {
            String action = intent.getAction();
            if(action != null) {
                switch (action) {
                    case "STOP_SERVICE":
                        stopForeground(true);
                        break;
                    default:
                        startForeground(111, createNotification());
                }
            }
            else {
                startForeground(111, createNotification());

            }
        }
        return START_STICKY;
    }

    private String createNotificationChannel(String channelId, String channelName) {
        NotificationChannel chan = new NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager service =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        service.createNotificationChannel(chan);
        return channelId;
    }

    public Notification createNotification(){
        RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.notification_layout);
        Intent stopIntent = new Intent(this, MusicService.class);
        stopIntent.setAction("STOP_SERVICE");
        PendingIntent stopPendingIntent =
                PendingIntent.getService(this, 0, stopIntent,
                        0);
        notificationLayout.setOnClickPendingIntent(R.id.btn_stop, stopPendingIntent);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent,
                        0);

        Notification notification =
                new Notification.Builder(this, "my_chanel")
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentIntent(pendingIntent)
                        .setCustomContentView(notificationLayout)
                        .build();

        return notification;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMusicBinder;
    }

    private MusicBinder mMusicBinder = new MusicBinder();

    public class MusicBinder extends Binder {
        MusicService getService(){
            return MusicService.this;
        }
    }

    public void play(){
        Toast.makeText(this, "Play", Toast.LENGTH_SHORT).show();
    }

    public void pause(){
        Toast.makeText(this, "Pause", Toast.LENGTH_SHORT).show();
    }

    public void next(){
        Toast.makeText(this, "Next", Toast.LENGTH_SHORT).show();
    }

    public void prev(){
        Toast.makeText(this, "Previus", Toast.LENGTH_SHORT).show();
    }

    public void playAt(int index){
        Toast.makeText(this, "Play at " + index, Toast.LENGTH_SHORT).show();
    }

    public void stopAt(int index){
        Toast.makeText(this, "Stop at " + index, Toast.LENGTH_SHORT).show();
    }
}
