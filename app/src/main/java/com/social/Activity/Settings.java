package com.social.Activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.social.BuildConfig;
import com.social.Helpers.Utlity;
import com.social.R;
import com.social.databinding.ActivitySettingsBinding;

public class Settings extends AppCompatActivity implements View.OnClickListener {
    ActivitySettingsBinding binding;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        FirebaseAuth instance = FirebaseAuth.getInstance();
        mAuth = instance;
        firebaseUser = mAuth.getCurrentUser();
        //clickc
        click();

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void click() {
        binding.profile.setOnClickListener(this);
        binding.intermange.setOnClickListener(this);
        binding.logout.setOnClickListener(this);
        binding.rate.setOnClickListener(this);
        binding.share.setOnClickListener(this);
        binding.privacy.setOnClickListener(this);
        binding.policy.setOnClickListener(this);
        binding.support.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profile:
                startActivity(new Intent(this,Myaccount.class));
                break;
            case R.id.intermange:
                startActivity(new Intent(this,Interset.class));
                break;
            case R.id.logout:
                if (mAuth != null && firebaseUser != null) {
                    mAuth.signOut();
                    MainActivity.firebaseUser = null;
                    MainActivity.binding.navview.setSelectedItemId(R.id.navigation_home);
                    Utlity.show_toast(this,"Logout successfully !");
                    finish();
                }
                break;
            case R.id.support:
                composeEmail();
                break;
            case R.id.rate:
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                // To count with Play market backstack, After pressing back button,
                // to taken back to our application, we need to add following flags to intent.
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                }
                break;
            case R.id.share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "Hey check out my app at: https://play.google.com/store/apps/details?id=" + getPackageName());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;
            case R.id.privacy:
                openWebPage("https://www.google.com/webhp?hl=en&sa=X&ved=0ahUKEwiMgty_18rqAhXO7XMBHTnvCxQQPAgH");
                break;
            case R.id.policy:
                openWebPage("https://www.google.com/webhp?hl=en&sa=X&ved=0ahUKEwiMgty_18rqAhXO7XMBHTnvCxQQPAgH");
                break;
        }
    }

    public void openWebPage(String url) {
        try {
            Uri webpage = Uri.parse(url);
            Intent myIntent = new Intent(Intent.ACTION_VIEW, webpage);
            startActivity(myIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No application can handle this request. Please install a web browser or check your URL.",  Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void composeEmail() {
        Intent mIntent = new Intent(Intent.ACTION_SENDTO);
        mIntent.setData(Uri.parse("mailto:"));
        mIntent.putExtra(Intent.EXTRA_EMAIL  , new String[] {"Support@opusys.com"});
        mIntent.putExtra(Intent.EXTRA_SUBJECT, "Type your subject");
        mIntent.putExtra(Intent.EXTRA_TEXT, "Please Describe your issues");
        startActivity(Intent.createChooser(mIntent, "Send your feedback..."));
    }
}