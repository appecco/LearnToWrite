package com.appecco.learntowrite;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.text.StaticLayout;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import java.io.IOException;

public class MainActivity extends Activity {

    public static Context context;

    public class BackgroundSound extends AsyncTask<Void, Void, Void> {
        MediaPlayer backGroudPlayer;

        @Override
        protected Void doInBackground(Void... params) {
            backGroudPlayer = MediaPlayer.create(MainActivity.this, R.raw.backgroud_sound);
            backGroudPlayer.setLooping(true);
            backGroudPlayer.setVolume(100,100);
            backGroudPlayer.start();
            return null;
        }

        public void stop(){
            backGroudPlayer.stop();
        }
    }

    BackgroundSound mBackgroundSound = new BackgroundSound();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.context = this;

        //Play Intro Video
        Intent intent = new Intent(MainActivity.this,VideoActivity.class);
        startActivity(intent);

        final MediaPlayer mPlayerClick = MediaPlayer.create(this, R.raw.button_click);

        setContentView(R.layout.activity_main);

        ImageButton btnNewGameCursive = (ImageButton)findViewById(R.id.btnNewGameCursive);
        btnNewGameCursive.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View view) {

                mPlayerClick.start();

                Intent intent = new Intent(MainActivity.this,GameActivity.class);
                Bundle b = new Bundle();
                b.putString("game", "Lower Case");
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        ImageButton btnNewGamePrint = (ImageButton)findViewById(R.id.btnNewGamePrint);
        btnNewGamePrint.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View view) {
                mPlayerClick.start();

                Intent intent = new Intent(MainActivity.this,GameActivity.class);
                Bundle b = new Bundle();
                b.putString("game", "Upper Case");
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        ImageButton btnNewGameNumbers = (ImageButton)findViewById(R.id.btnNewGameNumbers);
        btnNewGameNumbers.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View view) {
                mPlayerClick.start();

                Intent intent = new Intent(MainActivity.this,GameActivity.class);
                Bundle b = new Bundle();
                b.putString("game", "Numbers");
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        ImageButton btnSettings = (ImageButton)findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View view) {
                mPlayerClick.start();

                Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        mBackgroundSound.doInBackground();
    }

    @Override
    protected void onPause() {
        mBackgroundSound.stop();
        mBackgroundSound.cancel(true);
        super.onPause();
    }

    @Override
    protected void onStop() {
        mBackgroundSound.stop();
        mBackgroundSound.cancel(true);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mBackgroundSound.stop();
        mBackgroundSound.cancel(true);
        super.onDestroy();
    }
}
