package by.lunamretic.chat.authorization;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
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

    private FirebaseAuth firebaseAuth;

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
        setContentView(R.layout.activity_login);

        editEmail = (EditText) findViewById(R.id.editEmail);
        editPassword = (EditText) findViewById(R.id.editPassword);

        buttonLogin = (Button) findViewById(R.id.buttonLogin);

        linkRegister = (TextView) findViewById(R.id.linkRegister);
        linkResetPassword = (TextView) findViewById(R.id.linkResetPassword);

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
                    dialog.dismiss();

                    dialog.setContentView(R.layout.dialog_welcome);
                    dialog.show();

                    new CountDownTimer(2000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                        }

                        @Override
                        public void onFinish() {
                            dialog.hide();

                            restoreSignInButton();

                            new CountDownTimer(500, 500) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                }

                                @Override
                                public void onFinish() {
                                    finish();

                                    Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(mainIntent);
                                }
                            }.start();
                        }
                    }.start();
                } else {
                    dialog.dismiss();

                    restoreSignInButton();

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

            loginAnimation();
            loginDialog();

            singIn(email, password);
        }
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



    private void loginAnimation() {
        if (animator != null) {
            animator.cancel();
        }

        loadingView = findViewById(R.id.loadingScreen);

        buttonLogin.getGlobalVisibleRect(startBounds);
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

        buttonLogin.setAlpha(0f);
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

    private void loginDialog() {
        dialog = new Dialog(this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(
                    getResources().getDrawable(
                            R.drawable.dialog_progress_background));
        }
        dialog.setContentView(R.layout.dialog_login);
        dialog.setCancelable(false);
        dialog.getWindow().setDimAmount(0);
        dialog.show();

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    private void restoreSignInButton() {
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
                buttonLogin.setAlpha(1f);
                loadingView.setVisibility(View.GONE);
                animator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                buttonLogin.setAlpha(1f);
                loadingView.setVisibility(View.GONE);
                animator = null;
            }
        });

        set.start();
    }

    private boolean checkForms() {

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

        return true;
    }
}
