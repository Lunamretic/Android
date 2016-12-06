package by.lunamretic.chat.authorization;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import android.content.res.Configuration;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

import by.lunamretic.chat.MainActivity;
import by.lunamretic.chat.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editEmail;
    private EditText editPassword;

    private Button buttonLogin;
    private TextView linkRegister;
    private TextView linkResetPassword;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editEmail = (EditText) findViewById(R.id.editEmail);
        editPassword = (EditText) findViewById(R.id.editPassword);

        buttonLogin = (Button) findViewById(R.id.buttonLogin);

        linkRegister = (TextView) findViewById(R.id.linkRegister);
        linkResetPassword = (TextView) findViewById(R.id.linkResetPassword);

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();

        loadLocalization();

        checkSignedIn();

        linkRegister.setOnClickListener(this);
        linkResetPassword.setOnClickListener(this);
        buttonLogin.setOnClickListener(this);
    }

    private boolean isSignedIn() {
        return firebaseAuth.getCurrentUser() != null;
    }

    private void checkSignedIn() {
        if (isSignedIn()) {
            finish();

            Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(mainIntent);
        }
    }

    private void singIn(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    finish();

                    Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                } else {
                    progressDialog.dismiss();

                    Toast.makeText(LoginActivity.this, R.string.login_failed, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void userRegister() {
        Intent registerIntent = new Intent(LoginActivity.this, RegistrationActivity.class);
        startActivity(registerIntent);
    }

    private void resetPassword() {
        Intent resetPasswordIntent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
        startActivity(resetPasswordIntent);
    }

    private void userLogin() {
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, R.string.enter_email, Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, R.string.enter_password, Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage(this.getResources().getString(R.string.login));
        progressDialog.show();

        singIn(email, password);
    }

    @Override
    public void onClick(View v) {
        if (v == linkRegister) {
            userRegister();
        }

        if (v == linkResetPassword) {
            resetPassword();
        }

        if (v == buttonLogin) {
            userLogin();
        }
    }

    private void setLocalization(String localization) {
        Locale locale = new Locale(localization);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        this.setContentView(R.layout.activity_login);
    }

    private void loadLocalization() {
        SharedPreferences sPref = getApplicationContext().getSharedPreferences("by.lunamretic.chat", Context.MODE_PRIVATE);
        final String LANGUAGE = "LOCALIZATION";

        String localization = sPref.getString(LANGUAGE, "");

        if (!localization.matches("") && isSignedIn()) {
            setLocalization(localization);
        }
    }
}
