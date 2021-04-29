package com.example.myapplication.register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "EmailPassword";
    private ImageButton btn_back;
    private Button btn_register_save;
    private CheckBox cb_target;

    private EditText et_register_id, et_register_pw, et_register_pw_check, et_register_name, et_register_phone, et_register_birth, et_register_no_check;
    private Button btn_check_id, btn_check_phone, btn_check_submit;

    private FirebaseAuth mAuth;
    private String mVerificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private String smsCode = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Intent intent = getIntent();

        btn_register_save = findViewById(R.id.btn_register_save);
        btn_back = findViewById(R.id.btn_back);
        cb_target = findViewById(R.id.cb_target_check);

        et_register_id = findViewById(R.id.et_register_id);
        et_register_pw = findViewById(R.id.et_register_pw);
        et_register_pw_check = findViewById(R.id.et_register_pw_check);
        et_register_name = findViewById(R.id.et_register_name);
        et_register_phone = findViewById(R.id.et_register_phone);
        et_register_no_check = findViewById(R.id.et_register_no_check);
        et_register_birth = findViewById(R.id.et_register_birth);

        btn_check_id = findViewById(R.id.btn_check_id);
        btn_check_phone = findViewById(R.id.btn_check_phone);
        btn_check_submit = findViewById(R.id.btn_check_submit);

        mAuth = FirebaseAuth.getInstance();

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_check_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 중복확인 로직 필요
                /*
                if 중복
                    Toast
                esle 안중복
                    중복확인버튼(btn_check_id) 체크표시로 변경
                 */
            }
        });

        // Initialize phone auth callbacks
        // [START phone_auth_callbacks]
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);
                Log.d(TAG, "onCodeSent: " + token);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
            }

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);
                Log.d(TAG, "onVerificationCompleted: " + credential.getSmsCode());

                smsCode = credential.getSmsCode();
                btn_check_phone.setText("문자 재전송");
                et_register_no_check.setVisibility(View.VISIBLE);
                btn_check_submit.setVisibility(View.VISIBLE);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(RegisterActivity.this, "휴대폰 번호를 확인해 주세요", Toast.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Toast.makeText(RegisterActivity.this, "인증 가능 횟수를 초과하였습니다", Toast.LENGTH_SHORT).show();
                }
                // Show a message and update the UI
            }
        };
        // [END phone_auth_callbacks]

        btn_check_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPhoneNumberVerification("+82" + String.valueOf(et_register_phone.getText()));
            }
        });

        btn_check_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (String.valueOf(et_register_no_check.getText()).equals(smsCode)) {
                    Toast.makeText(RegisterActivity.this, "인증되었습니다", Toast.LENGTH_SHORT).show();
                    et_register_phone.setEnabled(false);
                    et_register_no_check.setEnabled(false);
                    btn_check_submit.setEnabled(false);
                    btn_check_submit.setText("인증 완료");
                } else {
                    Toast.makeText(RegisterActivity.this, "인증번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                }
                Log.d(TAG, "submit.onClick: " + smsCode);
            }
        });


        btn_register_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = String.valueOf(et_register_id.getText());
                String password = String.valueOf(et_register_pw.getText());
                if (email.length() != 0 && password.length() != 0) {
                    createAccount(email, password);
                } else {
                    Toast.makeText(RegisterActivity.this, "정보를 마저 입력해 주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            reload();
        }
    }

    private void startPhoneNumberVerification(String phoneNumber) {

        // [START start_phone_auth]
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
        // [END start_phone_auth]
    }

    private PhoneAuthCredential verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        return credential;
    }


    private void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    Toast.makeText(RegisterActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
            }
        });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // 후원 대상 체크 시
            if (cb_target.isChecked()) {
                // setResult 및 Intent 수정 필요
                setResult(0);
                Intent intent = new Intent(RegisterActivity.this, UserDetailsActivity.class);
                startActivity(intent);

                //콜백 받으면 finish
                finish();
            }
            // 일반 회원일 경우
            else {
                //CallBack으로 처리해
//                    Intent intent = new Intent(RegisterActivity.this, SurveyActivity.class);
//                    startActivity(intent);
                finish();
            }
        }
    }

    private void reload() {
        mAuth.signOut();
    }
}