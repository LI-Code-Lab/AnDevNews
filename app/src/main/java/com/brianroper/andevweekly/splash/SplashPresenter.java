package com.brianroper.andevweekly.splash;

import android.content.Context;
import android.content.Intent;

import com.brianroper.andevweekly.archive.ArchiveService;
import com.brianroper.andevweekly.base.Presenter;
import com.brianroper.andevweekly.home.HomeActivity;
import com.brianroper.andevweekly.utils.Util;
import com.brianroper.andevweekly.archive.ArchiveActivity;

/**
 * Created by brianroper on 2/11/17.
 */

public class SplashPresenter implements Presenter<SplashView> {

    private SplashView mSplashView;
    private Context mContext;

    public SplashPresenter(Context context) {
        mContext = context;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void attachView(SplashView view) {
        this.mSplashView = view;
    }

    /**
     * starts the ArchiveService
     */
    public void startArchiveService(){
        if(Util.activeNetworkCheck(mContext)) {
            Intent archiveService = new Intent(mContext, ArchiveService.class);
            mContext.startService(archiveService);
        }
        else{Util.noActiveNetworkToast(mContext);}
    }

    /**
     * starts the ArchiveActivity
     */
    public void startArchiveActivity(){
        Intent archiveIntent = new Intent(mContext, ArchiveActivity.class);
        archiveIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(archiveIntent);
    }

    public void startHomeActivity(){
        Intent archiveIntent = new Intent(mContext, HomeActivity.class);
        archiveIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(archiveIntent);
    }
}
