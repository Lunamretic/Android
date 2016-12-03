package by.lunamretic.chat.account.change;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import by.lunamretic.chat.R;
import by.lunamretic.chat.account.SettingsActivity;

public class EmailActivity extends AppCompatActivity implements View.OnClickListener{
    EditText editEmail;
    Button buttonChangeEmail;

    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

        editEmail = (EditText) findViewById(R.id.editEmail);
        buttonChangeEmail = (Button) findViewById(R.id.buttonChangeEmail);

        getUserInfo();

        buttonChangeEmail.setOnClickListener(this);
    }

    private void getUserInfo() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String email = user.getEmail();

            editEmail.setText(email);
        }
    }

    private void updateEmail() {
        user.updateEmail(editEmail.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //Log.d(TAG, "User email address updated.");
                            finish();

                            Intent settingsIntent = new Intent(EmailActivity.this, SettingsActivity.class);
                            EmailActivity.this.startActivity(settingsIntent);
                        }
                    }
                });
    }

    private void changeEmail() {
        if (TextUtils.isEmpty(editEmail.getText().toString())) {
            AlertDialog.Builder adb = new AlertDialog.Builder(EmailActivity.this);
            adb.setMessage(R.string.enter_email);
            adb.setPositiveButton(R.string.ok, new AlertDialog.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            adb.show();
        } else {
            updateEmail();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == buttonChangeEmail) {
            changeEmail();
        }
    }
}
