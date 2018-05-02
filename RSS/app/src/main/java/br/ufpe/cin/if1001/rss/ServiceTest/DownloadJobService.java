package br.ufpe.cin.if1001.rss.ServiceTest;

import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Build;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;

import br.ufpe.cin.if1001.rss.R;
import br.ufpe.cin.if1001.rss.ui.MainActivity;
import br.ufpe.cin.if1001.rss.ui.PreferenciasActivity;

/**
 * Created by Lerisson on 02/05/2018.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class DownloadJobService extends JobService{
    private SharedPreferences sharedPreferences;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    public boolean onStartJob(JobParameters params) {
        //carregando as preferencias do arquivo xml
        PreferenceManager.setDefaultValues(this, R.xml.preferencias, false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String url = sharedPreferences.getString(PreferenciasActivity.KEY_PREF_RSS_FEED, "");


        PersistableBundle pb=params.getExtras();
        if (pb.getBoolean(MainActivity.KEY_DOWNLOAD, false)) {
            Intent downloadService = new Intent (getApplicationContext(),DownloadXmlRssService.class);
            downloadService.putExtra("url", url);
            getApplicationContext().startService(downloadService);
            return true;
        }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Intent downloadService = new Intent (getApplicationContext(),DownloadXmlRssService.class);
        getApplicationContext().stopService(downloadService);
        return true;
    }
}

