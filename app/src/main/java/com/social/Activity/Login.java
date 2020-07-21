package com.social.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.social.Helpers.Utlity;
import com.social.R;
import com.social.databinding.ActivityLoginBinding;

import java.util.Arrays;

public class Login extends AppCompatActivity implements View.OnClickListener {
    private static final int RC_SIGN_IN = 200;
    ActivityLoginBinding binding;
    String mobileno = "";
    FirebaseAuth mAuth;
    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        //click listner
        callbackManager = CallbackManager.Factory.create();
        binding.loginbutton.setReadPermissions(Arrays.asList("public_profile"));
        FirebaseAuth instance = FirebaseAuth.getInstance();
        mAuth = instance;
        // Configure Google Sign In
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        //click
        click();

    }

    private void click() {
        binding.btnotp.setOnClickListener(this);
        binding.google.setOnClickListener(this);
        binding.facebook.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnotp:
                mobileno = binding.mobileno.getText().toString();
                if (!Utlity.is_empty(mobileno) && mobileno.length() == 10) {
                    if(Utlity.is_online(this)) {
                        startActivity(new Intent(Login.this, OTP.class).putExtra("mobile", mobileno));
                        finish();
                    }
                    else
                        Utlity.show_toast(this,"No Internet !");
                } else {
                    Utlity.show_toast(this, getString(R.string.invald));
                }
                break;
            case R.id.google:
                if(Utlity.is_online(this))
                signIn();
                else
                  Utlity.show_toast(this,"No Internet !");
                break;
            case R.id.facebook:
                // Callback registration
                binding.loginbutton.setReadPermissions("email", "public_profile");
                binding.loginbutton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        Log.d("facebook", "facebook:onSuccess:" + loginResult);
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @SuppressLint("WrongConstant")
                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Snackbar.make(binding.btnotp, exception.toString(), Snackbar.LENGTH_SHORT).show();
                    }
                });
                break;
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        Utlity.show_progress(this);
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("Google", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Utlity.dismiss_dilog(Login.this);
                // Google Sign In failed, update UI appropriately
                Log.w("Google", "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @SuppressLint("WrongConstant")
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Utlity.dismiss_dilog(Login.this);
                            // Sign in success, update UI with the signed-in user's information
                            MainActivity.firebaseUser=mAuth.getCurrentUser();
                            finish();

                        } else {
                            Utlity.dismiss_dilog(Login.this);
                            // If sign in fails, display a message to the user.
                            Log.w("gogoler", "signInWithCredential:failure", task.getException());
                            Snackbar.make(binding.btnotp, "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("facebook", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        LoginManager.getInstance().logOut();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            startActivity(new Intent(Login.this, MainActivity.class));
                            finishAffinity();
                        } else {
                            // If sign in fails, display a message to the user.
                            Snackbar.make(binding.btnotp, "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }


}