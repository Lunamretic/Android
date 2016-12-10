package by.lunamretic.chat.account;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import by.lunamretic.chat.MainActivity;
import by.lunamretic.chat.R;
import by.lunamretic.chat.account.change.EmailActivity;
import by.lunamretic.chat.account.change.UsernameActivity;
import by.lunamretic.chat.account.settings.LanguageActivity;
import by.lunamretic.chat.authorization.LoginActivity;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SettingsActivity";

    TextView textEmail;
    TextView textUsername;
    TextView linkChangeLanguage;
    TextView textLogOut;

    FirebaseUser user;
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setTitle(R.string.title_settings_activity);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        textEmail = (TextView) findViewById(R.id.textEmail);
        textUsername = (TextView) findViewById(R.id.textNavUsername);
        textLogOut = (TextView) findViewById(R.id.textLogOut);
        linkChangeLanguage = (TextView) findViewById(R.id.linkChangeLanguage);

        user = FirebaseAuth.getInstance().getCurrentUser();
        firebaseAuth = FirebaseAuth.getInstance();

        getUserInfo();

        textUsername.setOnClickListener(this);
        textEmail.setOnClickListener(this);
        textLogOut.setOnClickListener(this);
        linkChangeLanguage.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();

            Intent mainIntent = new Intent(SettingsActivity.this, MainActivity.class);
            startActivity(mainIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void getUserInfo() {
        if (user != null) {
            String username = user.getDisplayName();
            String email = user.getEmail();

            textEmail.setText(email);

            if (TextUtils.isEmpty(username)) {
                textUsername.setText(R.string.empty_username);
                Toast.makeText(this, R.string.enter_username, Toast.LENGTH_SHORT).show();
            } else {
                textUsername.setText(username);
            }
        }
    }

    private void updateUsername() {
        Intent usernameIntent = new Intent(SettingsActivity.this, UsernameActivity.class);
        startActivity(usernameIntent);
    }

    private void updateEmail() {
        Intent emailIntent = new Intent(SettingsActivity.this, EmailActivity.class);
        startActivity(emailIntent);
    }

    private void changeLanguage() {
        Intent languageIntent = new Intent(SettingsActivity.this, LanguageActivity.class);
        startActivity(languageIntent);
    }

    private void logout() {
        Log.d(TAG, "user log out");

        firebaseAuth.signOut();

        finish();
        Intent loginIntent = new Intent(SettingsActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }

    @Override
    public void onClick(View v) {
        if (v == textUsername) {
            updateUsername();
        }

        if (v == textEmail) {
            updateEmail();
        }

        if (v == linkChangeLanguage) {
            changeLanguage();
        }

        if (v == textLogOut) {
            logout();
        }
    }
}


