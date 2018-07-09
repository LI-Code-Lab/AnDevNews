package com.brianroper.andevweekly.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;

/**
 * Created by brianroper on 1/19/17.
 */

public class Util {

    /**
     * error toast displayed to user when there is no available network
     */
    static public void noActiveNetworkToast(Context context){
        Toasty.error(context,
                "There is currently no active network connection.",
                Toast.LENGTH_SHORT,
                true)
                .show();
    }

    /**
     * check the device for an active network connection
     */
    static public boolean activeNetworkCheck(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}
