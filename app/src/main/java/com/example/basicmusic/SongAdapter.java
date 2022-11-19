package com.example.basicmusic;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import phucdv.android.musichelper.Song;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
    private final Context mContext;
    private final MusicController mMusicController;
    private OnItemClickListener mItemClickListener;

    public SongAdapter(Context context) {
        mContext = context;
        mMusicController = new MusicController(context);
    }

    public void setData(List<Song> data) {
        mMusicController.setData(data);
    }

    public MusicController getMusicController() {
        return mMusicController;
    }

    public void start() {
        mMusicController.start();
        notifyItemChanged(mMusicController.getCurrentIndex());
    }

    public void pause() {
        mMusicController.pause();
        notifyItemChanged(mMusicController.getCurrentIndex());
    }

    public void playNext() {
        int lastIndex = mMusicController.getCurrentIndex();
        mMusicController.playNext();
        notifyItemChanged(lastIndex);
        notifyItemChanged(mMusicController.getCurrentIndex());
    }

    public void playPrev() {
        int lastIndex = mMusicController.getCurrentIndex();
        mMusicController.playPrev();
        notifyItemChanged(lastIndex);
        notifyItemChanged(mMusicController.getCurrentIndex());
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.song_item, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        holder.bindView(mMusicController.getData().get(position));
    }

    @Override
    public int getItemCount() {
        return mMusicController.getData().size();
    }

    public void setItemClickListener(OnItemClickListener Listener) {
        mItemClickListener = Listener;
    }

    public interface OnItemClickListener {
        void onClick(View v, Song song, int pos);
    }

    public class SongViewHolder extends RecyclerView.ViewHolder {
        ImageView mAlbumArt;
        TextView mTitle;
        View mItemView;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            mItemView = itemView;
            mAlbumArt = itemView.findViewById(R.id.album_art);
            mTitle = itemView.findViewById(R.id.txt_title);
            itemView.setOnClickListener(v -> {
                int lastIndex = mMusicController.getCurrentIndex();
                mMusicController.playSongAt(getLayoutPosition());
                notifyItemChanged(lastIndex);
                notifyItemChanged(mMusicController.getCurrentIndex());
                if (mItemClickListener != null) {
                    int pos = getLayoutPosition();
                    mItemClickListener.onClick(v, mMusicController.getData().get(pos), pos);
                }
            });

        }

        public void bindView(Song song) {
            if (mMusicController.getCurrentIndex() == getAdapterPosition()) {
                if (mMusicController.isPlaying() || mMusicController.isPreparing()) {
                    mItemView.setBackgroundColor(Color.GREEN);

                } else {
                    mItemView.setBackgroundColor(Color.YELLOW);
                }
            } else {
                mItemView.setBackgroundColor(Color.TRANSPARENT);
            }

            mTitle.setText(song.getTitle());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, song.getId());
                try {
                    Bitmap bitmap = mContext.getContentResolver().loadThumbnail(trackUri, new Size(72, 72), null);
                    mAlbumArt.setImageBitmap(bitmap);
                } catch (Exception e) {
                }
            } else {
                mAlbumArt.setImageURI(song.getAlbumUri());
            }
        }
    }

}
