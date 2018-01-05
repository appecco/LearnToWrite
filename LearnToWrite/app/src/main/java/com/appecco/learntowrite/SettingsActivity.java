package com.appecco.learntowrite;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.appecco.utils.Settings;
import com.appecco.utils.StorageOperations;

import org.json.JSONObject;

public class SettingsActivity extends Activity {

    private ImageButton btnLanguageSpanish;
    private ImageButton btnLanguageEnglish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        btnLanguageSpanish = (ImageButton)findViewById(R.id.btnLanguageSpanish);
        btnLanguageSpanish.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Settings.set(Settings.CURRENT_LANGUAGE, "es");
                updateButtonsStatus();
                /* TODO: Cuando haya más settings se debería agregar un botón para regresar a la pantalla principal */
                finish();
            }
        });

        btnLanguageEnglish = (ImageButton)findViewById(R.id.btnLanguageEnglish);
        btnLanguageEnglish.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Settings.set(Settings.CURRENT_LANGUAGE, "en");
                updateButtonsStatus();
                /* TODO: Cuando haya más settings se debería agregar un botón para regresar a la pantalla principal */
                finish();
            }
        });

        updateButtonsStatus();
    }

    void updateButtonsStatus(){
        String currentLanguage;
        currentLanguage = Settings.get(Settings.CURRENT_LANGUAGE,"es");
        btnLanguageSpanish.setAlpha(("es".equals(currentLanguage))?1.0f:0.5f);
        btnLanguageEnglish.setAlpha(("en".equals(currentLanguage))?1.0f:0.5f);
    }
}
