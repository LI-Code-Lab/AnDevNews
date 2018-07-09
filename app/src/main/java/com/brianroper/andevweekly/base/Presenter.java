package com.brianroper.andevweekly.base;

/**
 * Created by brianroper on 1/9/17.
 */

public interface Presenter<T extends View> {
    void onCreate();
    void onStart();
    void onStop();
    void onPause();
    void attachView(T view);
}
