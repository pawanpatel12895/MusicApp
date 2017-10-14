package com.thelastpawn.musicapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.security.Permission;

import static android.R.attr.duration;
import static java.lang.Math.abs;

public class MainActivity extends AppCompatActivity implements
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener {
    private static final String TAG = "MainActivity";
    private static final int MY_PERMISSIONS_REQUEST_READ_STORAGE = 1;
    MyPlayer player;
    SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (permissions()) {
            init();
        }
    }
    void init()
    {player = new MyPlayer(getApplicationContext());
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                playNext();
            }
        });

        // Instantiate the gesture detector with the
        // application context and an implementation of
        // GestureDetector.OnGestureListener
        mDetector = new GestureDetectorCompat(this, (GestureDetector.OnGestureListener) this);
        // Set the gesture detector as the double tap
        // listener.
        mDetector.setOnDoubleTapListener(this);
        ((TextView) findViewById(R.id.songname)).setText(player.currentSongName());}

    private boolean permissions() {
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) return true;
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                MY_PERMISSIONS_REQUEST_READ_STORAGE);
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        init();
                } else {
//                    permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void onClickPlay(View view) {
        togglePlayPause();
    }

    private void togglePlayPause() {
        player.togglePlayPause();
        if (player.isPlaying())
            ((Button) findViewById(R.id.play)).setText("Pause");
        else
            ((Button) findViewById(R.id.play)).setText("Play");
//        Toast.makeText(this, "Playing: " + player.currentSongName(), Toast.LENGTH_SHORT).show();
    }

    public void onClickPrev(View view) {
        playPrev();
    }

    public void onClickNext(View view) {
        playNext();
    }


    private float x1, x2, y1, y2;
    static final int MIN_DISTANCE = 250;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.mDetector.onTouchEvent(event);
        // Be sure to call the superclass implementation
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                y1 = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                y2 = event.getY();
                if (rightToLeftSwipe(x1, y1, x2, y2))
                    playNext();
                if (leftToRightSwipe(x1, y1, x2, y2))
                    playPrev();
                break;
        }
        return super.onTouchEvent(event);
    }

    private void playPrev() {
        player.playPrev();
        ((TextView) findViewById(R.id.songname)).setText(player.currentSongName());
        Toast.makeText(this, "Playing: " + player.currentSongName(), Toast.LENGTH_SHORT).show();

    }

    private void playNext() {
        player.playNext();
        ((TextView) findViewById(R.id.songname)).setText(player.currentSongName());
        Toast.makeText(this, "Playing: " + player.currentSongName(), Toast.LENGTH_SHORT).show();
    }

    private boolean rightToLeftSwipe(float x1, float y1, float x2, float y2) {
        if (manhattanDistance(x1, y1, x2, y2) < MIN_DISTANCE)
            return false; //not a swipe operation
        if (abs(x1 - x2) < abs(y1 - y2)) return false;
//        Log.i(TAG,x1 + " " + x2);
        return (x2 - x1) < 0;
    }

    private boolean leftToRightSwipe(float x1, float y1, float x2, float y2) {
        if (manhattanDistance(x1, y1, x2, y2) < MIN_DISTANCE)
            return false; //not a swipe operation
        if (abs(x1 - x2) < abs(y1 - y2)) return false;
//        Log.i(TAG,x1 + " " + x2);
        return (x2 - x1) > 0;
    }

    private float manhattanDistance(float x1, float y1, float x2, float y2) {
        return abs(x1 - x2) + abs(y1 - y2);
    }

    private GestureDetectorCompat mDetector;


    @Override
    public boolean onDoubleTap(MotionEvent event) {
        Log.d(TAG, "onDoubleTap: " + event.toString());
        player.togglePlayPause();
        if (player.isPlaying())
            ((Button) findViewById(R.id.play)).setText("Pause");
        else
            ((Button) findViewById(R.id.play)).setText("Play");
//        Toast.makeText(this, "Playing: " + player.currentSongName(), Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent event) {
//        Log.d(TAG, "onDoubleTapEvent: " + event.toString());
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
//        Log.d(TAG, "onSingleTapConfirmed: " + event.toString());
        return true;
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }
}