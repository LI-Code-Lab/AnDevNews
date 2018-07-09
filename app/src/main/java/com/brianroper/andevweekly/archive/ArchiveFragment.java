package com.brianroper.andevweekly.archive;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import android.view.View;

import com.brianroper.andevweekly.R;
import com.brianroper.andevweekly.model.Constants;
import com.brianroper.andevweekly.utils.RecyclerViewDivider;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by brianroper on 3/27/17.
 */

public class ArchiveFragment extends Fragment implements ArchiveView {

    @BindView(R.id.archive_recycler)
    public RecyclerView mRecyclerView;

    private ArchivePresenter mArchivePresenter;
    private ArchiveAdapter mArchiveAdapter;
    private LinearLayoutManager mLayoutManager;
    private EventBus mEventBus = EventBus.getDefault();

    public static ArchiveFragment newInstance(){
        ArchiveFragment archiveFragment = new ArchiveFragment();
        return archiveFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_archive, container, false);

        ButterKnife.bind(this, root);

        initializePresenter();
        initializeAdapter();

        handleAdapterDataSet();

        return root;
    }

    /**
     * initializes the presenter for this activity and attaches it to the view
     */
    public void initializePresenter(){
        mArchivePresenter = new ArchivePresenter(getActivity());
        mArchivePresenter.attachView(this);
    }

    /**
     * initializes the views adapter
     */
    public void initializeAdapter(){
        mArchiveAdapter = new ArchiveAdapter(getActivity());
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mRecyclerView.addItemDecoration(new RecyclerViewDivider(getActivity()));
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mArchiveAdapter);
    }

    /**
     * update data in the adapter
     */
    public void handleAdapterDataSet(){
        mArchivePresenter.getRealmData();
        mArchiveAdapter.notifyDataSetChanged();
    }

    @Override
    public void getDataFromRealm() {
        mArchiveAdapter.getArchiveDataFromRealm();
    }

    /**
     * watches for ArchiveEvent message data change throughout app
     */
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onArchiveMessageEvent(ArchiveEvent archiveEvent){
        Constants constants = new Constants();
        if(archiveEvent.message.equals(constants.ARCHIVE_EVENT_FINISHED)) {
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
