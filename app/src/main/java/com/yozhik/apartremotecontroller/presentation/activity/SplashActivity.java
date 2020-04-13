package com.yozhik.apartremotecontroller.presentation.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.yozhik.R;
import com.yozhik.apartremotecontroller.data.repository.SharedPreferencesRepository;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(
                () -> {
                    if (SharedPreferencesRepository.isFirstLaunch(this)) {
                        SharedPreferencesRepository.setFirstLaunch(this, false);
                        navigateToOptionsActivity();
                    } else {
                        navigateToSelectorActivity();
                    }
                },
                SPLASH_DELAY
        );
    }

    void navigateToSelectorActivity() {
        finish();
        startActivity(new Intent(this, SelectorActivity.class));
    }

    void navigateToOptionsActivity() {
        finish();
        startActivity(new Intent(this, OptionsActivity.class));
    }
}
