package com.appecco.learntowrite;

import android.app.Activity;
import android.os.Bundle;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.appecco.learntowrite.service.BackgroundMusicServiceControl;
import com.appecco.utils.Foreground;
import com.appecco.utils.LoadedResources;
import com.google.android.gms.ads.MobileAds;
import com.tjeannin.apprate.ExceptionHandler;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Iniciar la carga de recursos lo antes posible para evitar que se intenten utilizar antes de que estén disponibles
        LoadedResources.getInstance().loadResources(this);

        // Manejador de excepciones que registra si la App ha finalizado abruptamente alguna vez para no mostrar el diálogo solicitando calificación
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(Thread.getDefaultUncaughtExceptionHandler(),this));

        // Inicializar el verificador de cambios entre foreground y background
        Foreground.init(getApplication());

        //Si ya esta ejecutandose el servicio de la musica hay que pausar
        BackgroundMusicServiceControl.pauseBackgroundMusic(this);

        //Play Intro Video
        Intent intent = new Intent(MainActivity.this,VideoActivity.class);
        startActivityForResult(intent,1);

        setContentView(R.layout.activity_main);

        //Inicializar ADS
        //Test Id
        //MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");

        //LearnToWrite Id
        MobileAds.initialize(this, "ca-app-pub-1507251474990125~6376776099");

        ImageButton btnNewGameCursive = findViewById(R.id.btnNewGameCursive);
        btnNewGameCursive.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View view) {

                LoadedResources.getInstance().playSound(R.raw.button_click);

                Intent intent = new Intent(MainActivity.this,GameActivity.class);
                startActivity(intent);
            }
        });

        ImageButton btnSettings = findViewById(R.id.btnSettings);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1){
            BackgroundMusicServiceControl.startBackgroundMusicService(this, R.raw.backgroud_sound, 50, 50);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        BackgroundMusicServiceControl.resumeBackgroundMusic(this);
    }

    @Override
    protected void onStop() {
        // TODO: Agregar un dialogo de salida y liberar los recursos
        if (Foreground.isInitialized() && !Foreground.get().isForeground()){
            BackgroundMusicServiceControl.stopBackgroundMusic(this);
        }
        super.onStop();
    }
}
