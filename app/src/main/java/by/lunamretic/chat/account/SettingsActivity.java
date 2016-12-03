package by.lunamretic.chat.account;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import by.lunamretic.chat.R;
import by.lunamretic.chat.account.change.EmailActivity;
import by.lunamretic.chat.account.change.UsernameActivity;
import by.lunamretic.chat.authorization.LoginActivity;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    TextView textEmail;
    TextView textUsername;
    TextView textLogOut;

    FirebaseUser user;
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        textEmail = (TextView) findViewById(R.id.textEmail);
        textUsername = (TextView) findViewById(R.id.textUsername);
        textLogOut = (TextView) findViewById(R.id.textLogOut);

        user = FirebaseAuth.getInstance().getCurrentUser();
        firebaseAuth = FirebaseAuth.getInstance();

        getUserInfo();

        textUsername.setOnClickListener(this);
        textEmail.setOnClickListener(this);
        textLogOut.setOnClickListener(this);
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
        finish();

        Intent usernameIntent = new Intent(SettingsActivity.this, UsernameActivity.class);
        startActivity(usernameIntent);
    }

    private void updateEmail() {
        finish();

        Intent emailIntent = new Intent(SettingsActivity.this, EmailActivity.class);
        startActivity(emailIntent);
    }

    private void logout() {
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

        if (v == textLogOut) {
            logout();
        }
    }
}


