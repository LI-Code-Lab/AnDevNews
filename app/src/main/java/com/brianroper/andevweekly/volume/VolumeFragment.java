package com.brianroper.andevweekly.volume;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.brianroper.andevweekly.R;
import com.brianroper.andevweekly.model.Constants;
import com.brianroper.andevweekly.utils.RecyclerViewDivider;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

/**
 * Created by brianroper on 4/10/17.
 */

public class VolumeFragment extends Fragment implements VolumeView {

    @BindView(R.id.volume_toolbar)
    public Toolbar mToolbar;
    @BindView(R.id.volume_recycler)
    public RecyclerView mRecyclerView;

    private VolumePresenter mVolumePresenter;
    private VolumeAdapter mVolumeAdapter;
    private LinearLayoutManager mLayoutManager;
    private EventBus mEventBus = EventBus.getDefault();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_volume, container, false);

        ButterKnife.bind(this, root);

        initializePresenter();
        initializeAdapter();

        handleAdapterDataSet();

        return root;
    }

    public void initializePresenter() {
        mVolumePresenter = new VolumePresenter(getActivity());
        mVolumePresenter.attachView(this);
        mVolumePresenter.startVolumeService(getVolumeId());
        if(getVolumeId() <= 102){
            Toasty.info(getActivity(),
                    "This issue isn't supported just yet",
                    Toast.LENGTH_LONG,
                    true).show();
        }
    }

    public static VolumeFragment newInstance(){
        VolumeFragment volumeFragment = new VolumeFragment();
        return volumeFragment;
    }


    /**
     * gets the current volume id from the received intent
     */
    @Override
    public int getVolumeId() {
        Intent archiveIntent = getActivity().getIntent();
        int id = archiveIntent.getIntExtra("id", 0)-1;
        setTitleTextView(id+1 + "");
        return id;
    }

    public void initializeAdapter() {
        mVolumeAdapter = new VolumeAdapter(getActivity());
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mVolumeAdapter);
    }

    @Override
    public void getDataFromRealm(){
        mVolumeAdapter.getVolumeDataFromRealm(getVolumeId());
        mVolumeAdapter.notifyDataSetChanged();
    }

    /**
     * handles the data set of the attached adapter
     */
    public void handleAdapterDataSet() {
        mVolumePresenter.getRealmData();
    }

    @Override
    public void setTitleTextView(String issue) {}

    /**
     * watches for VolumeEvent message data change throughout app
     */
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onVolumeMessageEvent(VolumeEvent volumeEvent){
        Constants constants = new Constants();
        if(volumeEvent.message == constants.VOLUME_EVENT_FINISHED) {
            handleAdapterDataSet();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mEventBus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        mEventBus.unregister(this);
    }

    @Override
    public void finish() {}
}
