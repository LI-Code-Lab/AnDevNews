package com.brianroper.andevweekly.splash;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.brianroper.andevweekly.R;

public class SplashActivity extends AppCompatActivity implements SplashView {

    private SplashPresenter mSplashPresenter;
    final int SPLASH_DURATION = 1300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initializePresenter();
        handleSplash();
    }

    @Override
    public void initializePresenter(){
        mSplashPresenter = new SplashPresenter(getApplicationContext());
        mSplashPresenter.attachView(this);
        mSplashPresenter.startArchiveService();
    }

    @Override
    public void handleSplash(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
               // mSplashPresenter.startArchiveActivity();
                mSplashPresenter.startHomeActivity();
            }
        }, SPLASH_DURATION);
    }
}
