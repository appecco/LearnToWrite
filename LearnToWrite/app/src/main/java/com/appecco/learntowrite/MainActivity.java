package com.appecco.learntowrite;

import android.app.Activity;
import android.os.Bundle;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.appecco.utils.LoadedResources;
import com.appecco.utils.Settings;
import com.google.android.gms.ads.MobileAds;
import com.tjeannin.apprate.ExceptionHandler;

import java.util.Set;

public class MainActivity extends Activity {

    public class BackgroundSound extends AsyncTask<Void, Void, Void> {
        MediaPlayer backGroudPlayer;

        @Override
        protected Void doInBackground(Void... params) {
            if (Settings.isMusicEnabled(MainActivity.this)){
                backGroudPlayer = MediaPlayer.create(MainActivity.this, R.raw.backgroud_sound);
                backGroudPlayer.setLooping(true);
                backGroudPlayer.setVolume(100,100);
                backGroudPlayer.start();
            }
            return null;
        }

        public void stop(){
            if (backGroudPlayer != null) {
                backGroudPlayer.stop();
            }
        }
    }

    BackgroundSound mBackgroundSound = new BackgroundSound();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Iniciar la carga de recursos lo antes posible para evitar que se intenten utilizar antes de que estén disponibles
        LoadedResources.getInstance().loadResources(this);

        // Manejador de excepciones que registra si la App ha finalizado abruptamente alguna vez para no mostrar el diálogo solicitando calificación
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(Thread.getDefaultUncaughtExceptionHandler(),this));

        //Play Intro Video
         Intent intent = new Intent(MainActivity.this,VideoActivity.class);
         startActivity(intent);

        setContentView(R.layout.activity_main);

        //Inicializar ADS
        //Test Id
        //MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");

        //LearnToWrite Id
        MobileAds.initialize(this, "ca-app-pub-1507251474990125~6376776099");

        ImageButton btnNewGameCursive = (ImageButton)findViewById(R.id.btnNewGameCursive);
        btnNewGameCursive.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View view) {

                LoadedResources.getInstance().playSound(R.raw.button_click);

                Intent intent = new Intent(MainActivity.this,GameActivity.class);
                startActivity(intent);
            }
        });

        ImageButton btnSettings = (ImageButton)findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View view) {

                LoadedResources.getInstance().playSound(R.raw.button_click);

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
