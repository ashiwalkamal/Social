package com.social.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.social.Helpers.Utlity;
import com.social.R;
import com.social.databinding.ActivityOTPBinding;

import java.util.concurrent.TimeUnit;

public class OTP extends AppCompatActivity implements View.OnClickListener {
    ActivityOTPBinding binding;
    FirebaseAuthSettings firebaseAuthSettings;
    FirebaseAuth mAuth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String mVerificationId = "";
    int timeforsecond = 60000;
    CountDownTimer timer;
    String TAG = "login_screen";
    String mobileno="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_o_t_p);
        FirebaseAuth instance = FirebaseAuth.getInstance();
        mAuth = instance;
        this.firebaseAuthSettings = instance.getFirebaseAuthSettings();

        //click listner
        click();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Log.d(TAG, "onVerificationCompleted:" + credential);
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                }
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                // ...
            }
        };
        if(getIntent()!=null)
        {
            mobileno=getIntent().getStringExtra("mobile");
            //sing in with mobile no
            auth_no(mobileno);
        }
    }

    private void click() {
        binding.btnresend.setOnClickListener(this);
        binding.btnsubmit.setOnClickListener(this);
    }

    private void auth_no(String str) {
        Utlity.show_progress(this);
        PhoneAuthProvider instance = PhoneAuthProvider.getInstance();
        StringBuilder sb = new StringBuilder();
        sb.append("+91");
        sb.append(str);
        instance.verifyPhoneNumber(sb.toString(), 60, TimeUnit.SECONDS, this, this.mCallbacks);
    }

    /* access modifiers changed from: private */
    public void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            public void onComplete(Task<AuthResult> task) {
                Utlity.dismiss_dilog(OTP.this);
                if (task.isSuccessful()) {
                   MainActivity.firebaseUser=mAuth.getCurrentUser();
                   finish();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btnresend:
                auth_no(mobileno);
                break;
            case R.id.btnsubmit:
                Utlity.show_progress(this);
                signInWithPhoneAuthCredential(PhoneAuthProvider.getCredential(mVerificationId, binding.edtotp.getText().toString()));
                break;
        }
    }
}