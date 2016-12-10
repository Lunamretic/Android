package by.lunamretic.chat.authorization;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import by.lunamretic.chat.MainActivity;
import by.lunamretic.chat.R;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "RegistrationActivity";

    private EditText editUsername;
    private EditText editEmail;
    private EditText editPassword;
    private EditText editPassword2;

    private TextView linkLogin;

    private FirebaseAuth firebaseAuth;

    private Button buttonRegister;

    private Animator animator;

    private Dialog dialog;

    private View loadingView;

    float startScale;

    final Rect startBounds = new Rect();
    final Rect finalBounds = new Rect();
    final Point globalOffset = new Point();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        editUsername = (EditText) findViewById(R.id.editUsername);
        editEmail = (EditText) findViewById(R.id.editEmail);
        editPassword = (EditText) findViewById(R.id.editPassword);
        editPassword2 = (EditText) findViewById(R.id.editPassword2);


        linkLogin = (TextView) findViewById(R.id.linkLogin);

        buttonRegister = (Button) findViewById(R.id.buttonRegister);

        firebaseAuth = FirebaseAuth.getInstance();


        linkLogin.setOnClickListener(this);
        buttonRegister.setOnClickListener(this);
    }

    private void setUsername(final FirebaseUser USER) {
        final String USERNAME = editUsername.getText().toString();

        new Thread(new Runnable() {

            @Override
            public void run() {
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(USERNAME)
                        .build();

                if (USER.updateProfile(profileUpdates).isSuccessful()) {
                    Log.d(TAG, "new username set");
                } else {
                    Log.d(TAG, "couldn't set new username");
                }
            }
        } ).start();
    }

    private void restoreSignUpButton() {
        float startScaleFinal = startScale;

        AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator
                .ofFloat(loadingView, View.X, startBounds.left))
                .with(ObjectAnimator
                        .ofFloat(loadingView,
                                View.Y,startBounds.top))
                .with(ObjectAnimator
                        .ofFloat(loadingView,
                                View.SCALE_X, startScaleFinal))
                .with(ObjectAnimator
                        .ofFloat(loadingView,
                                View.SCALE_Y, startScaleFinal));
        set.setDuration(500);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                buttonRegister.setAlpha(1f);
                loadingView.setVisibility(View.GONE);
                animator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                buttonRegister.setAlpha(1f);
                loadingView.setVisibility(View.GONE);
                animator = null;
            }
        });

        set.start();
    }

    private void createUser(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            dialog.dismiss();

                            dialog.setContentView(R.layout.dialog_welcome);
                            dialog.show();

                            new CountDownTimer(2000, 1000) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                }

                                @Override
                                public void onFinish() {
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                    if (user != null) {
                                        setUsername(user);
                                    }

                                    dialog.hide();

                                    restoreSignUpButton();

                                    new CountDownTimer(500, 500) {
                                        @Override
                                        public void onTick(long millisUntilFinished) {
                                        }

                                        @Override
                                        public void onFinish() {
                                            Log.d(TAG, "new user registered and signed in");

                                            finish();

                                            Intent mainIntent = new Intent(RegistrationActivity.this, MainActivity.class);
                                            startActivity(mainIntent);
                                        }
                                    }.start();
                                }
                            }.start();


                        } else {
                            Log.d(TAG, "couldn't register new user");

                            dialog.dismiss();

                            restoreSignUpButton();

                            Toast.makeText(RegistrationActivity.this, R.string.registration_failed, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void login() {
        finish();

        Intent loginIntent = new Intent(RegistrationActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }

    private boolean checkForms() {
        if (editUsername.getText().toString().trim().length() == 0) {
            Toast.makeText(this, R.string.enter_username, Toast.LENGTH_SHORT).show();

            return false;
        } else {
            if (editUsername.getText().toString().trim().length() < 3) {
                Toast.makeText(this, R.string.short_username, Toast.LENGTH_SHORT).show();

                return false;
            }
        }

        if (editEmail.getText().toString().trim().length() == 0) {
            Toast.makeText(this, R.string.enter_email, Toast.LENGTH_SHORT).show();

            return false;
        }

        if (editPassword.getText().toString().trim().length() == 0) {
            Toast.makeText(this, R.string.enter_password, Toast.LENGTH_SHORT).show();

            return false;
        } else {
            if (editPassword.getText().toString().trim().length() < 6) {
                Toast.makeText(this, R.string.short_password, Toast.LENGTH_SHORT).show();

                return false;
            }
        }

        if (! editPassword.getText().toString().trim().matches(editPassword2.getText().toString().trim())) {
            Toast.makeText(this, R.string.not_match_passwords, Toast.LENGTH_SHORT).show();

            return false;
        }

        return true;
    }


    private void registerAnimation() {
        if (animator != null) {
            animator.cancel();
        }

        loadingView = findViewById(R.id.loadingScreen);

        buttonRegister.getGlobalVisibleRect(startBounds);
        findViewById(R.id.activity_register)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {

            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {

            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        buttonRegister.setAlpha(0f);
        loadingView.setVisibility(View.VISIBLE);

        loadingView.setPivotX(0f);
        loadingView.setPivotY(0f);

        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(loadingView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(loadingView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(loadingView, View.SCALE_X,
                        startScale, 1f))
                .with(ObjectAnimator.ofFloat(loadingView,
                        View.SCALE_Y, startScale, 1f));

        set.setDuration(500);

        set.setInterpolator(new DecelerateInterpolator());

        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                animator = null;
            }
        });

        set.start();
        animator = set;
    }

    private void registerDialog() {
        dialog = new Dialog(this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(
                    getResources().getDrawable(
                            R.drawable.dialog_progress_background));
        }
        dialog.setContentView(R.layout.progress_dialog);
        dialog.setCancelable(false);
        dialog.getWindow().setDimAmount(0);
        dialog.show();

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    private void registerUser() {
        if (checkForms()) {
            String email = editEmail.getText().toString().trim();
            String password = editPassword.getText().toString().trim();

//            if (TextUtils.isEmpty(email)) {
//                Toast.makeText(this, R.string.enter_email, Toast.LENGTH_SHORT).show();
//                return;
//            }
//            if (TextUtils.isEmpty(password)) {
//                Toast.makeText(this, R.string.enter_password, Toast.LENGTH_SHORT).show();
//                return;
//            }

            registerAnimation();

            registerDialog();

            createUser(email, password);
        }
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
