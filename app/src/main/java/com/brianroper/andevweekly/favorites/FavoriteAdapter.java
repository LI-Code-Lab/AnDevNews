package com.brianroper.andevweekly.favorites;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.brianroper.andevweekly.R;
import com.brianroper.andevweekly.volume.Volume;
import com.brianroper.andevweekly.utils.Util;
import com.thefinestartist.finestwebview.FinestWebView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by brianroper on 3/16/17.
 */

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>{

    private Context mContext;
    private RealmResults<Favorite> mRealmResults;

    FavoriteAdapter(Context context) {
        mContext = context;
    }

    @Override
    public FavoriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View root = inflater.inflate(R.layout.volume_item, parent, false);
        return new FavoriteViewHolder(root);
    }

    @Override
    public void onBindViewHolder(FavoriteAdapter.FavoriteViewHolder holder, final int position) {
        holder.mFavoriteHeadline.setText(mRealmResults.get(position).getHeadline());
        holder.mFavoriteSummary.setText(mRealmResults.get(position).getSummary()
                + " " + mRealmResults.get(position).getSource());

        holder.mFavoriteLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //TODO: add dialog asking user to confirm delete
                deleteArticle(position);
                return true;
            }
        });

        holder.mFavoriteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openArticleWebView(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRealmResults.size();
    }

    public class FavoriteViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.volume_headline)
        TextView mFavoriteHeadline;
        @BindView(R.id.volume_summary)
        TextView mFavoriteSummary;
        @BindView(R.id.volume_item)
        ConstraintLayout mFavoriteLayout;

        FavoriteViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    /**
     * retrieves favorite data from realm database
     * @return realm results containing volumes
     */
    public RealmResults<Favorite> getFavoriteDataFromRealm(){
        Realm realm;
        Realm.init(mContext);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        realm = Realm.getInstance(realmConfiguration);
        mRealmResults = realm.where(Favorite.class).findAll();
        return mRealmResults;
    }

    /**
     * opens article in  a webview using object url
     * @param position
     */
    private void openArticleWebView(int position){
        if(Util.activeNetworkCheck(mContext)){
            new FinestWebView.Builder(mContext).show(mRealmResults.get(position).getLink());
        }
        else{Util.noActiveNetworkToast(mContext);}
    }

    /**
     * deletes the article in realm database of favorites
     * @param position
     */
    private void deleteArticle(final int position){
        try{
            Realm realm;
            Realm.init(mContext);
            RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                    .deleteRealmIfMigrationNeeded()
                    .build();
            realm = Realm.getInstance(realmConfiguration);
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Favorite favorite = realm.where(Favorite.class)
                            .equalTo("id", mRealmResults.get(position).getId())
                            .findFirst();
                    int id = favorite.getId();
                    favorite.deleteFromRealm();
                    Volume volume = realm.where(Volume.class)
                            .equalTo("id", id)
                            .findFirst();
                    volume.setSaved(false);
                    realm.copyToRealmOrUpdate(volume);
                    realm.close();
                    notifyDataSetChanged();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
