package com.brianroper.andevweekly.volume;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.brianroper.andevweekly.archive.Archive;
import com.brianroper.andevweekly.model.Constants;

import org.greenrobot.eventbus.EventBus;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.exceptions.RealmPrimaryKeyConstraintException;

/**
 * Created by brianroper on 1/12/17.
 */

public class VolumeService extends Service {

    private int mVolumeId;
    private EventBus mEventBus = EventBus.getDefault();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent!=null){
            mVolumeId = intent.getExtras().getInt("id" ,0);
            getVolumeDataFromRealm();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * retrieves volume data required for network call from realm
     */
    public void getVolumeDataFromRealm(){
        Thread thread = new Thread(){
            @Override
            public void run() {
                Realm realm;
                Realm.init(getApplicationContext());
                RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                        .deleteRealmIfMigrationNeeded()
                        .build();
                realm = Realm.getInstance(realmConfiguration);
                RealmResults<Archive> results = realm.where(Archive.class).findAll();
                Archive volumeArchive = results.get(mVolumeId);

                getVolumeData(volumeArchive);
            }
        };
        thread.start();
    }

    /**
     * retrieves volume data from androidweekly.net
     */
    public void getVolumeData(final Archive volumeArchive){
        try{
            Constants constants = new Constants();

            Document document = Jsoup
                    .connect(constants.ARCHIVE_VOLUME_BASE_URL + volumeArchive.getUrl())
                    .get();

            Log.i("ID: ", volumeArchive.getId() + "");

            //html format changes after issue 102
            if(volumeArchive.getId() >= 103){
                Elements body = document.select("p");
                Elements source = document.getElementsByClass("main-url");
                Elements headline = document.getElementsByClass("article-headline");
                cleanVolumeData(body, source, headline);
            }
            else if(volumeArchive.getId() <= 102){
                Elements body = document.select("p");
                Elements source = document.select("span");
                Elements headline = document.select("a");
                //cleanVolumeData(body, source, headline);
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * organizes volume data into manageable stacks
     */
    public void cleanVolumeData(Elements body, Elements source, Elements headline){
        Stack<String> volumeBody = new Stack<>();
        Stack<String> volumeSource = new Stack<>();
        Stack<String> volumeHeadline = new Stack<>();
        Stack<String> volumeHeadlineLink = new Stack<>();

        for (Element element : body){
            volumeBody.push(element.text());
        }

        volumeBody.pop();
        volumeBody.pop();
        volumeBody.pop();
        volumeBody.pop();

        for (Element element : source){
            volumeSource.push(element.text());
        }

        for (Element element : headline){
            volumeHeadline.push(element.text());
            volumeHeadlineLink.push(element.attr("href"));
        }

        Log.i("BodySize: ", volumeBody.size() + "");
        Log.i("SourceSize: ", volumeSource.size() + "");
        Log.i("LinkSize: ", volumeHeadlineLink.size() + "");
        Log.i("HeadlineSize: ", volumeHeadline.size() + "");

        formatVolumeData(volumeBody, volumeSource, volumeHeadlineLink, volumeHeadline);
    }

    /**
     * formats volume data into Issue object
     */
    public void formatVolumeData(Stack<String> body,
                                 Stack<String> source,
                                 Stack<String> link,
                                 Stack<String> headline){

        ArrayList<Volume> volumeList = new ArrayList<>();
        int sourceSize = source.size();

        for (int i = 0; i < sourceSize; i++) {
            Volume volume = new Volume();
            volume.setHeadline(headline.pop());
            volume.setSource(source.pop());
            volume.setSummary(body.pop());
            volume.setLink(link.pop());
            volume.setIssue(mVolumeId);
            volume.setId(i);
            volumeList.add(volume);
        }
        Log.i("VolumeList: ", volumeList.size() + "");
        storeVolumeDataInRealm(volumeList);
    }

    /**
     * stores volume data in realm
     */
    public void storeVolumeDataInRealm(ArrayList<Volume> volumes){
        Realm realm;
        Realm.init(getApplicationContext());
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        realm = Realm.getInstance(realmConfiguration);

        try {
            for (int i = 0; i < volumes.size(); i++) {
                final Volume volume = volumes.get(i);
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        String volumeId = volume.getIssue() + "000" + volume.getId();
                        Volume managedVolume = realm.createObject(Volume.class, Integer.parseInt(volumeId));
                        managedVolume.setIssue(volume.getIssue());
                        managedVolume.setLink(volume.getLink());
                        managedVolume.setSummary(volume.getSummary());
                        managedVolume.setHeadline(volume.getHeadline());
                        managedVolume.setSource(volume.getSource());
                    }
                });
            }
            Constants constants = new Constants();
            mEventBus.postSticky(new VolumeEvent(constants.VOLUME_EVENT_FINISHED));

            realm.close();
        }
        catch (RealmPrimaryKeyConstraintException e){
        }
    }
}
