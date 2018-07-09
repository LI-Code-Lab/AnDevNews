package com.brianroper.andevweekly.archive;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.brianroper.andevweekly.R;
import com.brianroper.andevweekly.model.Constants;
import com.brianroper.andevweekly.utils.Util;
import com.brianroper.andevweekly.volume.VolumeActivity;
import com.thefinestartist.finestwebview.FinestWebView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by brianroper on 1/11/17.
 */

public class ArchiveAdapter extends RecyclerView.Adapter<ArchiveAdapter.ArchiveViewHolder> {

    private Context mContext;
    private RealmResults<Archive> mRealmResults;

    ArchiveAdapter(Context context) {
        mContext = context;
    }

    @Override
    public ArchiveViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View root = inflater.inflate(R.layout.archive_item, parent, false);
        final ArchiveViewHolder archiveViewHolder = new ArchiveViewHolder(root);

        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setArchiveListener(archiveViewHolder);
            }
        });

        return archiveViewHolder;
    }

    @Override
    public void onBindViewHolder(ArchiveViewHolder holder, int position) {
        holder.mArchiveTitleTextView.setText(mRealmResults.get(position).getTitle());
        holder.mArchiveDateTextView.setText(mRealmResults.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return mRealmResults.size();
    }

    /**
     * view holder
     */
    public static class ArchiveViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.archive_title)
        public TextView mArchiveTitleTextView;
        @BindView(R.id.archive_date)
        public TextView mArchiveDateTextView;

        ArchiveViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    /**
     * retrieves archive data from realm
     */
    public void getArchiveDataFromRealm(){
        Realm realm;
        Realm.init(mContext);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        realm = Realm.getInstance(realmConfiguration);
        mRealmResults = realm.where(Archive.class).findAll();
    }

    /**
     * handles click behavior for recycler view item
     */
    private void setArchiveListener(ArchiveViewHolder holder){
        if(Util.activeNetworkCheck(mContext)){
            if(holder.getAdapterPosition()>=104){
                Intent volumeIntent = new Intent(mContext, VolumeActivity.class);
                volumeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                volumeIntent.putExtra("id", mRealmResults.get(holder.getAdapterPosition()).getId());
                volumeIntent.putExtra("issueNumber", holder.getAdapterPosition() + 1);
                Log.i("Adapter ID: ", mRealmResults.get(holder.getAdapterPosition()).getId() + "");
                mContext.startActivity(volumeIntent);
            }
            else{
                Constants constants = new Constants();
                int issue = holder.getAdapterPosition()+1;
                String issueUrl = constants.ARCHIVE_VOLUME_BASE_URL
                                    + constants.VOLUME_ISSUE_PARAM
                                    + constants.VOLUME_ISSUE_ID_PARAM
                                    + issue;
                new FinestWebView.Builder(mContext)
                        .show(issueUrl);
                Log.i("Issue URL: ", issueUrl);
            }
        }
        else{Util.noActiveNetworkToast(mContext);}
    }
}
