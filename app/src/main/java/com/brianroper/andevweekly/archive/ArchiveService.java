package com.brianroper.andevweekly.archive;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.brianroper.andevweekly.archive.Archive;
import com.brianroper.andevweekly.archive.ArchiveEvent;
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
import io.realm.exceptions.RealmPrimaryKeyConstraintException;

/**
 * Created by brianroper on 1/9/17.
 */

public class ArchiveService extends Service {

    private EventBus mEventBus = EventBus.getDefault();

    @Override
    public void onCreate() {
        super.onCreate();
        getArchiveData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * retrieves archive issues list from androidweekly.net
     */
    public void getArchiveData(){
        Thread thread = new Thread(){
            @Override
            public void run() {
                try{
                    Constants constants = new Constants();

                    Document document = Jsoup.connect(constants.ARCHIVE_BASE_URL).get();

                    String title = document.title();
                    Elements elements = document.select("a[href]");

                    Elements dates = document.select("span");

                    cleanArchiveData(elements, dates);
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    /**
     * removes useless content from archive data
     */
    public void cleanArchiveData(Elements elements, Elements dates){
        Stack<String> archiveUrls = new Stack<>();
        Stack<String> archiveTitles = new Stack<>();
        Stack<String> archiveDates = new Stack<>();

        for (Element element : elements){
            if(element.attr("href").startsWith("/issues")){
                String archiveUrl = element.attr("href");
                archiveUrls.push(archiveUrl);
            }
            if(element.text().startsWith("Issue")){
                String archiveTitle = element.text();
                archiveTitles.push(archiveTitle);
            }
        }

        for(Element date: dates){
            if(date.text().startsWith("20")){
                String archiveDate = date.text();
                archiveDates.push(archiveDate);
            }
        }

        Log.i("Link Size: ", archiveUrls.size() + "");
        Log.i("Title Size: ", archiveTitles.size() + "");
        Log.i("Dates Size: ", archiveDates.size() + "");

        formatArchiveData(archiveTitles, archiveUrls, archiveDates);
    }

    /**
     * puts archive data into archive objects
     */
    public void formatArchiveData(Stack<String> titles,
                                  Stack<String> urls,
                                  Stack<String> dates){

        ArrayList<Archive> archives = new ArrayList<>();
        int issueNum = titles.size();

        for (int i = 0; i < issueNum; i++) {
            Archive archive = new Archive();
            archive.setDate(dates.pop());
            archive.setTitle(titles.pop());
            archive.setUrl(urls.pop());
            archives.add(archive);
        }

        Log.i("Archive Size: ", archives.size() + "");

        for (int i = 0; i < archives.size(); i++) {
            int id = i+1;
            archives.get(i).setId(id);
        }

        storeArchiveInRealm(archives);
    }

    /**
     * stores the archive data in realm
     */
    public void storeArchiveInRealm(final ArrayList<Archive> archives){
        Realm realm;
        Realm.init(getApplicationContext());
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        realm = Realm.getInstance(realmConfiguration);

        try {
            for (int i = 0; i < archives.size(); i++) {
                final Archive archive = archives.get(i);

                if (realm.where(Archive.class).equalTo("id", archive.getId()).findFirst()==null){
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            Archive managedArchive = realm.createObject(Archive.class, archive.getId());
                            managedArchive.setTitle(archive.getTitle());
                            managedArchive.setDate(archive.getDate());
                            managedArchive.setUrl(archive.getUrl());
                        }
                    });
                }
            }

            //notifies subscribers that this service has finished its operations
            Constants constants = new Constants();
            mEventBus.postSticky(new ArchiveEvent(constants.ARCHIVE_EVENT_FINISHED));

            realm.close();

        }catch (RealmPrimaryKeyConstraintException e){
            //e.printStackTrace();
        }
    }
}
