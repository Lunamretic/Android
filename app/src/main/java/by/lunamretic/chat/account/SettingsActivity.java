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

import org.w3c.dom.Text;

import by.lunamretic.chat.R;
import by.lunamretic.chat.account.change.EmailActivity;
import by.lunamretic.chat.account.change.UsernameActivity;

public class SettingsActivity extends AppCompatActivity {
    TextView textEmail;
    TextView textUsername;

    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        textEmail = (TextView) findViewById(R.id.textEmail);
        textUsername = (TextView) findViewById(R.id.textUsername);

        getUserInfo();

        textUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent usernameIntent = new Intent(SettingsActivity.this, UsernameActivity.class);
                SettingsActivity.this.startActivity(usernameIntent);
            }
        });

        textEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(SettingsActivity.this, EmailActivity.class);
                SettingsActivity.this.startActivity(emailIntent);
            }
        });
    }

    private void getUserInfo() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String username = user.getDisplayName();
            String email = user.getEmail();

            textEmail.setText(email);

            if (TextUtils.isEmpty(username)) {
                textUsername.setText("None");
                Toast.makeText(this, "Please enter username", Toast.LENGTH_SHORT).show();
            } else {
                textUsername.setText(username);
            }
        }
    }
}


