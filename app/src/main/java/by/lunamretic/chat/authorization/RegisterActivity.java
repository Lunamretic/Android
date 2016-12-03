package by.lunamretic.chat.authorization;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import by.lunamretic.chat.R;
import by.lunamretic.chat.account.SettingsActivity;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editEmail;
    private EditText editPassword;

    private TextView linkLogin;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

    private Button buttonRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setTitle(R.string.title_register_activity);

        editEmail = (EditText) findViewById(R.id.editEmail);
        editPassword = (EditText) findViewById(R.id.editPassword);

        linkLogin = (TextView) findViewById(R.id.linkLogin);

        buttonRegister = (Button) findViewById(R.id.buttonRegister);

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();


        linkLogin.setOnClickListener(this);
        buttonRegister.setOnClickListener(this);
    }

    private void createUser(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, R.string.registered, Toast.LENGTH_SHORT).show();

                            finish();
                            Intent settingsIntent = new Intent(RegisterActivity.this, SettingsActivity.class);
                            startActivity(settingsIntent);
                        } else {
                            Toast.makeText(RegisterActivity.this, R.string.registration_failed, Toast.LENGTH_SHORT).show();
                        }

                        progressDialog.hide();
                    }
                });
    }

    private void login() {
        finish();

        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }

    private void registerUser() {
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

        progressDialog.setMessage(this.getResources().getString(R.string.registration));
        progressDialog.show();

        createUser(email, password);
    }
    @Override
    public void onClick(View v) {
        if (v == linkLogin) {
            login();
        }

        if (v == buttonRegister) {
            registerUser();
        }
    }
}
