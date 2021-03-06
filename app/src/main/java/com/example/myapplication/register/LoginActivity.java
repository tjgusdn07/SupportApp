package com.example.myapplication.register;

import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginPage";
    private static final int GOOGLE_SIGN_IN = 9001;
    private static final int FB_SIGN_IN = 64206;
    private static final int SIGN_UP = 0;
    private static final int LOGIN = 1;
    private static final int LOGIN_SNS = 2;
    private static final int FAIL = -1;

    private Toast toast;

    private Button btn_signup;
    private Button btn_google;
    private LoginButton btn_fb;
    private Button btn_login;
    private FirebaseAuth mAuth;

    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager mCallbackManager;

    private EditText et_email, et_pw;
    private TextView btn_find_id_pw;
    private ImageView logo;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        btn_signup = findViewById(R.id.btn_signup);
        btn_google = findViewById(R.id.btn_google);
        btn_fb = findViewById(R.id.btn_facebook);
        btn_login = findViewById(R.id.btn_login);

        logo = findViewById(R.id.img_logo);
        logo.setBackground(new ShapeDrawable(new OvalShape()));
        logo.setClipToOutline(true);

        et_email = findViewById(R.id.et_email);
        et_pw = findViewById(R.id.et_pw);
        btn_find_id_pw = findViewById(R.id.btn_find_id_pw);

        btn_find_id_pw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_email.getText().toString().length() == 0 || et_pw.getText().toString().length() == 0) {
                    Toast.makeText(LoginActivity.this, "?????????/??????????????? ??????????????????", Toast.LENGTH_SHORT).show();
                } else {
                    signIn(et_email.getText().toString(), et_pw.getText().toString());
                }
            }
        });

        clickBtn(btn_signup);
        btn_google.setOnClickListener(this::clickSNS);
        btn_fb.setOnClickListener(this::clickSNS);
    }

    //??????
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Log.d(TAG, "onStart: " + mAuth.getUid() + " " + mAuth.getCurrentUser());
        if (currentUser != null) {
            Log.d(TAG, "onStart: currentUser = " + currentUser.getEmail() + " is SignOuted");
            mAuth.signOut();
        }
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if (isLoggedIn) {
            LoginManager.getInstance().logOut();
        }
    }

    // ?????????
    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user, LOGIN);
                            Toast.makeText(LoginActivity.this, "????????? ??????", Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            updateUI(null, FAIL);
                            Toast.makeText(LoginActivity.this, "????????? ??????????????? ????????? ?????????", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // ????????????
    private void clickBtn(Button btn) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivityForResult(intent, SIGN_UP);  //  activity ??????
            }
        });
    }

    private void clickSNS(View view) {
        switch (view.getId()) {
            case R.id.btn_google:
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
                mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
                google_signIn();
                break;
            case R.id.btn_facebook:
                mCallbackManager = CallbackManager.Factory.create();
                btn_fb.setReadPermissions("email", "public_profile");
                btn_fb.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d(TAG, "facebook:onSuccess:" + loginResult);
                        firebaseAuthWithFacebook(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "facebook:onCancel");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d(TAG, "facebook:onError", error);
                    }
                });
                Log.d(TAG, "clickSNS: FACEBOOK_CLICKED");
                break;
        }
    }

    // SNS ????????????
    private void signUpWithCredential(AuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user, LOGIN_SNS);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null, LOGIN_SNS);
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: " + requestCode + " and " + resultCode);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
        if (requestCode == FB_SIGN_IN) {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
        if (requestCode == SIGN_UP && mAuth.getCurrentUser() != null) {
            et_email.setText(mAuth.getCurrentUser().getEmail());
        }
    }

    private void google_signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(String idToken) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + idToken);
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        signUpWithCredential(credential);
    }

    private void firebaseAuthWithFacebook(AccessToken token) {
        Log.d(TAG, "firebaseAuthWithFacebook:" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        signUpWithCredential(credential);
    }

    private void updateUI(FirebaseUser user, int key) {
        if (user != null && key != FAIL) {
            FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                    if (key == LOGIN) {
                        skipSurvey(task.getResult().child("like").getValue());
                    } else if (key == LOGIN_SNS && task.getResult().getValue() != null) {
                        skipSurvey(task.getResult().child("like").getValue());
                    } else {
                        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                        intent.putExtra("userInfo", user);
                        startActivityForResult(intent, SIGN_UP);  //  activity ??????
                    }
                }
            });
        }
    }

    public void skipSurvey(Object like) {
        Intent intent;
        if (like != null) {
            intent = new Intent(LoginActivity.this, MainActivity.class);
        } else {
            intent = new Intent(LoginActivity.this, SurveyActivity.class);
        }
        startActivity(intent);
        finish();
    }

    private void resetPassword() {
        Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
        startActivity(intent);
    }

    // ??????????????? ?????? ?????? ????????? ????????? ?????? ??????
    private long backKeyPressedTime = 0;

    // ??? ?????? ?????? ?????? ????????? ?????? ??? ??????
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        // ?????? ?????? ?????? ????????? ????????? ?????? ?????? ?????? ?????? ?????? ??????

        // ??????????????? ?????? ?????? ????????? ????????? ????????? 2?????? ?????? ?????? ????????? ?????? ???
        // ??????????????? ?????? ?????? ????????? ????????? ????????? 2?????? ???????????? Toast ??????
        // 2000 milliseconds = 2 seconds
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(this, "?????? ?????? ????????? ??? ??? ??? ???????????? ???????????????.", Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        // ??????????????? ?????? ?????? ????????? ????????? ????????? 2?????? ?????? ?????? ????????? ?????? ???
        // ??????????????? ?????? ?????? ????????? ????????? ????????? 2?????? ????????? ???????????? ??????
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            finish();
            toast.cancel();
            toast = Toast.makeText(this, "????????? ????????? ???????????????.", Toast.LENGTH_LONG);
            toast.show();
        }
    }
}