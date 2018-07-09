package com.brianroper.andevweekly.volume;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.brianroper.andevweekly.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VolumeActivity extends AppCompatActivity {

    @BindView(R.id.volume_toolbar)
    public Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volume);

        ButterKnife.bind(this);

        handleToolbarBehavior(mToolbar);
        initializeFragmentInstance();
    }

    /**
     * handles toolbar behavior
     */
    public void handleToolbarBehavior(Toolbar toolbar){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar()
                .setTitle("Android Weekly "
                        + String.valueOf(getIntent().getIntExtra("issueNumber", 0)));
    }

    /**
     * creates a new fragment instance
     */
    public void initializeFragmentInstance(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, VolumeFragment.newInstance());
        transaction.commit();
    }
}
