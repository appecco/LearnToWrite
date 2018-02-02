package com.appecco.learntowrite;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ToggleButton;

import com.appecco.utils.LoadedResources;
import com.appecco.utils.Settings;
import com.appecco.utils.StorageOperations;

import org.json.JSONObject;

import java.util.Set;

public class SettingsActivity extends Activity {

    private ImageButton btnLanguageSpanish;
    private ImageButton btnLanguageEnglish;
    private ToggleButton tglMusic;
    private ToggleButton tglSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        ImageButton btnBack = findViewById(R.id.backButton);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadedResources.getInstance().playSound(R.raw.button_click);
                finish();
            }
        });

        btnLanguageSpanish = findViewById(R.id.btnLanguageSpanish);
        btnLanguageSpanish.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                LoadedResources.getInstance().playSound(R.raw.button_click);
                Settings.setCurrentLanguage(SettingsActivity.this, "es");
                updateButtonsStatus();
            }
        });

        btnLanguageEnglish = findViewById(R.id.btnLanguageEnglish);
        btnLanguageEnglish.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                LoadedResources.getInstance().playSound(R.raw.button_click);
                Settings.setCurrentLanguage(SettingsActivity.this, "en");
                updateButtonsStatus();
            }
        });

        tglMusic = (ToggleButton)findViewById(R.id.tglMusic);
        tglMusic.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                LoadedResources.getInstance().playSound(R.raw.button_click);
                Settings.setMusicEnabled(SettingsActivity.this, tglMusic.isChecked());
            }
        });

        tglSound = (ToggleButton)findViewById(R.id.tglSound);
        tglSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Settings.setSoundEnabled(SettingsActivity.this, tglSound.isChecked());
                LoadedResources.getInstance().playSound(R.raw.button_click);
            }
        });

        updateButtonsStatus();
    }

    void updateButtonsStatus(){
        String currentLanguage;

        currentLanguage = Settings.getCurrentLanguage(SettingsActivity.this);
        btnLanguageSpanish.setAlpha(("es".equals(currentLanguage))?1.0f:0.5f);
        btnLanguageEnglish.setAlpha(("en".equals(currentLanguage))?1.0f:0.5f);

        tglMusic.setChecked(Settings.isMusicEnabled(SettingsActivity.this));
        tglSound.setChecked(Boolean.valueOf(Settings.isSoundEnabled(SettingsActivity.this)));

    }
}
