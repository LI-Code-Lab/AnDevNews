package com.brianroper.andevweekly.about;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.brianroper.andevweekly.R;
import com.danielstone.materialaboutlibrary.MaterialAboutActivity;
import com.danielstone.materialaboutlibrary.items.MaterialAboutActionItem;
import com.danielstone.materialaboutlibrary.items.MaterialAboutTitleItem;
import com.danielstone.materialaboutlibrary.model.MaterialAboutCard;
import com.danielstone.materialaboutlibrary.model.MaterialAboutList;
import com.thefinestartist.finestwebview.FinestWebView;

public class AboutActivity extends MaterialAboutActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * handles the list of cards contained in the about activity
     */
    @Override
    protected MaterialAboutList getMaterialAboutList(Context context) {
        MaterialAboutCard.Builder infoBuilder = new MaterialAboutCard.Builder();
        infoBuilder.addItem(new MaterialAboutTitleItem.Builder()
                .text("AnDev News")
                .icon(R.mipmap.ic_launcher)
                .build());

        infoBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text("Version")
                .subText("1.0.0")
                .icon(R.mipmap.ic_info_outline_black_24dp)
                .build());

        MaterialAboutCard.Builder devCard = new MaterialAboutCard.Builder();
        devCard.title("Author");
        devCard.addItem(new MaterialAboutActionItem.Builder()
                .text("LI Code Lab")
                .subText("United States")
                .icon(R.mipmap.ic_person_black_24dp)
                .build());

        devCard.addItem(new MaterialAboutActionItem.Builder()
                .text("GitHub")
                .subText("View this project on GitHub")
                .icon(R.mipmap.github_circle)
                .setOnClickListener(new MaterialAboutActionItem.OnClickListener() {
                    @Override
                    public void onClick() {
                        new FinestWebView.Builder(getApplicationContext())
                                .show("https://github.com/Edeneast13/AndroidWeeklyHelper");
                    }
                })
                .build());

        MaterialAboutCard.Builder bugCard = new MaterialAboutCard.Builder();
        bugCard.title("Support Development");
        bugCard.addItem(new MaterialAboutActionItem.Builder()
                .text("Submit Feedback")
                .subText("Report bugs or request new features.")
                .icon(R.mipmap.ic_bug_report_black_24dp)
                .setOnClickListener(new MaterialAboutActionItem.OnClickListener() {
                    @Override
                    public void onClick() {
                        sendFeedback();
                    }
                })
                .build());

        return new MaterialAboutList.Builder()
                .addCard(infoBuilder.build())
                .addCard(devCard.build())
                .addCard(bugCard.build())
                .build();
    }

    /**
     * returns the name for the activity on the toolbar
     */
    @Override
    protected CharSequence getActivityTitle() {
        return "About";
    }

    /**
     * send feedback using email intent
     */
    public void sendFeedback(){
        Intent feedbackIntent = new Intent(Intent.ACTION_SENDTO);
        String uriText = "mailto:" + Uri.encode("appdevbri@gmail.com") +
                "?subject=" + Uri.encode("Android Weekly Feedback");

        Uri uri = Uri.parse(uriText);
        feedbackIntent.setData(uri);

        //allows for us to return to app from play store using back button
        feedbackIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

        startActivity(Intent.createChooser(feedbackIntent, "Send mail......"));
    }
}
