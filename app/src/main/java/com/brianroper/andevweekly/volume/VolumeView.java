package com.brianroper.andevweekly.volume;

import com.brianroper.andevweekly.base.View;

/**
 * Created by brianroper on 1/12/17.
 */

public interface VolumeView extends View {
    int getVolumeId();
    void getDataFromRealm();
    void setTitleTextView(String issue);
}
