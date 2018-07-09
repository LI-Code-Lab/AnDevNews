package com.brianroper.andevweekly.archive;

import android.content.Context;
import android.content.Intent;

import com.brianroper.andevweekly.base.Presenter;
import com.brianroper.andevweekly.utils.Util;

/**
 * Created by brianroper on 1/9/17.
 */

public class ArchivePresenter implements Presenter<ArchiveView> {

    private ArchiveView mArchiveView;
    private Context mContext;

    public ArchivePresenter(Context context) {
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
    public void attachView(ArchiveView view) {
        this.mArchiveView = view;
    }

    /**
     * starts the ArchiveService
     */
    public void startArchiveService(){
        if(Util.activeNetworkCheck(mContext)==true) {
            Intent archiveService = new Intent(mContext, ArchiveService.class);
            mContext.startService(archiveService);
        }
        else{Util.noActiveNetworkToast(mContext);}
    }

    public void getRealmData(){
        mArchiveView.getDataFromRealm();
    }
}
