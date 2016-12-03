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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import by.lunamretic.chat.R;
import by.lunamretic.chat.account.SettingsActivity;

public class UsernameActivity extends AppCompatActivity implements View.OnClickListener{
    EditText editUsername;
    Button buttonChangeUsername;

    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_username);

        editUsername = (EditText) findViewById(R.id.editEmail);
        buttonChangeUsername = (Button) findViewById(R.id.buttonChangeUsername);

        getUserInfo();

        buttonChangeUsername.setOnClickListener(this);
    }

    private void getUserInfo() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String username = user.getDisplayName();

            if (TextUtils.isEmpty(username)) {
                editUsername.setText(R.string.empty_username);
                Toast.makeText(this, R.string.enter_username, Toast.LENGTH_SHORT).show();
            } else {
                editUsername.setText(username);
            }
        }
    }

    private void updateUsername() {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(editUsername.getText().toString())
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //Log.d(TAG, "User profile updated.");

                            Intent settingsIntent = new Intent(UsernameActivity.this, SettingsActivity.class);
                            UsernameActivity.this.startActivity(settingsIntent);
                        }
                    }
                });
    }

    private void changeUsername() {
        if (TextUtils.isEmpty(editUsername.getText().toString())) {
            AlertDialog.Builder adb=new AlertDialog.Builder(UsernameActivity.this);
            adb.setMessage(R.string.enter_username);
            adb.setPositiveButton(R.string.ok, new AlertDialog.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }});
            adb.show();
        } else {
            updateUsername();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == buttonChangeUsername) {
            changeUsername();
        }
    }
}
