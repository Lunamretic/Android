package by.lunamretic.chat.account.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import by.lunamretic.chat.R;
import by.lunamretic.chat.account.SettingsActivity;

public class LanguageActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView rusTextView;
    private TextView engTextView;

    private SharedPreferences sPref;
    private String localization;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        setTitle(R.string.title_settings_activity);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        rusTextView = (TextView) findViewById(R.id.russianTextView);
        engTextView = (TextView) findViewById(R.id.englishTextView);

        sPref = getApplicationContext().getSharedPreferences("by.lunamretic.chat", Context.MODE_PRIVATE);

        rusTextView.setOnClickListener(this);
        engTextView.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();

            Intent settingsIntent = new Intent(LanguageActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);
            //Log.d(TAG, "action bar clicked");
        }

        return super.onOptionsItemSelected(item);
    }


    private void setLocalization() {
        Locale locale = new Locale(localization);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        this.setContentView(R.layout.activity_language);
    }

    private void saveLocalization(String language) {
        final String LANGUAGE = "LOCALIZATION";

        SharedPreferences.Editor editor = sPref.edit();
        editor.putString(LANGUAGE, language);
        editor.commit();

        Toast.makeText(this, "Changes saved", Toast.LENGTH_SHORT).show();
    }

    private void onClickRus() {
        saveLocalization("ru");
        localization = "ru";
        setLocalization();
    }

    private void onClickEng() {
        saveLocalization("en");
        localization = "en";
        setLocalization();
    }

    @Override
    public void onClick(View v) {
        if (v == rusTextView) {
            onClickRus();
        }

        if (v == engTextView) {
            onClickEng();
        }
    }
}
