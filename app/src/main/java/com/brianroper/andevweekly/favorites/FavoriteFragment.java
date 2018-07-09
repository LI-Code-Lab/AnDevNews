package com.brianroper.andevweekly.favorites;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import android.view.View;
import android.widget.RelativeLayout;

import com.brianroper.andevweekly.R;
import com.brianroper.andevweekly.utils.RecyclerViewDivider;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmResults;

public class FavoriteFragment extends Fragment implements FavoriteView {

    @BindView(R.id.favorite_recycler)
    RecyclerView mRecyclerView;
    @BindView(R.id.favorite_emptyview)
    RelativeLayout mEmptyView;

    private FavoritePresenter mFavoritePresenter;
    private FavoriteAdapter mFavoriteAdapter;
    private LinearLayoutManager mLayoutManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_favorite, container, false);

        ButterKnife.bind(this, root);

        initializePresenter();
        initializeAdapter();

        handleAdapterDataSet();

        return root;
    }

    public static FavoriteFragment newInstance(){
        FavoriteFragment favoriteFragment = new FavoriteFragment();
        return favoriteFragment;
    }

    public void initializeAdapter(){
        mFavoriteAdapter = new FavoriteAdapter(getActivity());
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mFavoriteAdapter);
    }

    public void initializePresenter(){
        mFavoritePresenter = new FavoritePresenter();
        mFavoritePresenter.attachView(this);
    }

    @Override
    public void finish() {}

    @Override
    public void getDataFromRealm() {
        RealmResults<Favorite> results = mFavoriteAdapter.getFavoriteDataFromRealm();
        if(results.isEmpty()){
           mEmptyView.setVisibility(View.VISIBLE);
           mRecyclerView.setVisibility(View.GONE);
        }
        else{
            mEmptyView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    public void handleAdapterDataSet(){
        mFavoritePresenter.getRealmData();
    }
}
