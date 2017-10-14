package com.thelastpawn.musicapp;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by pawan on 14/10/17.
 */

public class MyPlayer extends MediaPlayer {
    private static final String TAG = "MyPlayer";
    private Context applicationContext;
    private List<String> songList;
    private Context context;
    int curr;

    MyPlayer(Context context) {
        this.context = context;
        init();
    }

    MyPlayer() {
        context = null;
        init();
    }

    private void init() {
        setAudioStreamType(AudioManager.STREAM_MUSIC);
        getSongsFromStorage();
        curr = (new Random().nextInt())% songList.size();
        curr += songList.size();
        curr%= songList.size();
        playSongByPath(songList.get(curr));
    }

    private void getSongsFromStorage() {
        songList = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        if (cursor == null) {
            // query failed, handle error.
        } else if (!cursor.moveToFirst()) {
            Log.i(TAG, "no media on device");
            // no media on the device
        } else {
            do {
                String fullPath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                // ...process entry...
                Log.i("Song Added: ", fullPath);
                songList.add(fullPath);
            } while (cursor.moveToNext());
        }
    }

    void playSongByURI(Uri myUri) {
        try {
//            setDataSource(getApplicationContext(), myUri);
            setDataSource(context, myUri);
            prepare();
            start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void playSongByPath(String path) {
//        playSongByURI(Uri.parse(path));
        try {
            setDataSource(path);
            prepare();
            start();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    void playNext() {
        reset();
        curr = (curr + 1) % songList.size();
        playSongByPath(songList.get(curr));
    }

    void playPrev() {
        reset();
        curr = (curr - 1 + songList.size()) % songList.size();
        playSongByPath(songList.get(curr));
    }

    void togglePlayPause()
    {
        if(isPlaying())
            pause();
        else start();
    }

    public String currentSongName() {
        File file = new File(songList.get(curr));
        return file.getName();
    }

    public int getCurrent() {
        return curr;
    }

    public List<String> getSongList() {
        return songList;
    }
    void sampleSongPlay() {
        String url = "https://www.mfiles.co.uk/mp3-downloads/Earle-of-Salisbury.mp3"; // your URL here
        try {
            setDataSource(url);
            prepare(); // might take long! (for buffering, etc)
            start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}