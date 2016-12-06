package by.lunamretic.chat.authorization;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import by.lunamretic.chat.R;

public class ResetPasswordActivity extends AppCompatActivity implements View.OnClickListener{

    FirebaseAuth auth;
    EditText etEmail;

    Button buttonSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        auth = FirebaseAuth.getInstance();

        etEmail = (EditText) findViewById(R.id.editSendToEmail);

        buttonSend = (Button) findViewById(R.id.buttonResetPassword);

        buttonSend.setOnClickListener(this);



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();

            Intent loginIntent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            //Log.d(TAG, "action bar clicked");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v == buttonSend) {
            auth.sendPasswordResetEmail(etEmail.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                AlertDialog.Builder adb = new AlertDialog.Builder(ResetPasswordActivity.this);
                                adb.setMessage(R.string.success_reset_password);
                                adb.setPositiveButton(R.string.ok, null);
                                adb.show();
                                //Log.d(TAG, "Email sent.");

                            } else {
                                AlertDialog.Builder adb = new AlertDialog.Builder(ResetPasswordActivity.this);
                                adb.setMessage(R.string.error_reset_password);
                                adb.setPositiveButton(R.string.ok, null);
                                adb.show();
                            }
                        }
                    });
        }
    }
}
