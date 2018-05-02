package br.ufpe.cin.if1001.rss.ui;
import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import br.ufpe.cin.if1001.rss.R;
import br.ufpe.cin.if1001.rss.ServiceTest.DownloadJobService;
import br.ufpe.cin.if1001.rss.ServiceTest.DownloadXmlRssService;
import br.ufpe.cin.if1001.rss.db.SQLiteRSSHelper;
import br.ufpe.cin.if1001.rss.domain.ItemRSS;
import br.ufpe.cin.if1001.rss.util.CustomAdapter;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private SharedPreferences sharedPreferencesJob;
    private SharedPreferences.Editor editor;
    private SQLiteRSSHelper banco;
    private List<ItemRSS> parsedResponse;
    private static final int JOB_ID = 160;
    public static final String KEY_DOWNLOAD="isDownload";

    //ao fazer envio da resolucao, use este link no seu codigo!
    //private final String RSS_FEED = "http://leopoldomt.com/if1001/g1brasil.xml";
    private ListView conteudoRSS;
    // variável usada na leitura do xml
    private List<String> ParserLoad;
    private List<ItemRSS> Listresult;
    private String aux;
    JobScheduler jobScheduler;

    //OUTROS LINKS PARA TESTAR...
    //http://rss.cnn.com/rss/edition.rss
    //http://pox.globo.com/rss/g1/brasil/
    //http://pox.globo.com/rss/g1/ciencia-e-saude/
    //http://pox.globo.com/rss/g1/tecnologia/

    //use ListView ao invés de TextView - deixe o atributo com o mesmo nome

    // definição
    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        banco = SQLiteRSSHelper.getInstance(this);

        //parte que define o jobScheduler
        jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);

        //use ListView ao invés de TextView - deixe o ID no layout XML com o mesmo nome conteudoRSS
        //isso vai exigir o processamento do XML baixado da internet usando o ParserRSS
        //adição da tollbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.rss_toolbar);
        setSupportActionBar(toolbar);
        conteudoRSS = (ListView) findViewById(R.id.conteudoRSS);
        //implementação do sharedPreferences
//        sharedPreferences = getSharedPreferences(getString(R.string.rss_feed), Context.MODE_PRIVATE);
//               editor = sharedPreferences.edit();
//                if (getString(R.string.rss_feed_link).equals("default")){
//                        editor.putString(getString(R.string.rss_feed), getString(R.string.rss_feed_default));
//                    } else {
//                        editor.putString(getString(R.string.rss_feed), getString(R.string.rss_feed_link));
//                    }
//                editor.apply();
        //carregando as preferencias do arquivo xml
//        PreferenceManager.setDefaultValues(this, R.xml.preferencias, false);
//        sharedPreferencesJob = PreferenceManager.getDefaultSharedPreferences(this);
//        String job = sharedPreferencesJob.getString(PreferenciasActivity.KEY_PREF_JOB_SCHEDULER, "");
//
//        Log.d("job",job);
//
//        //parte que define o jobScheduler
//        jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
//        JobInfo.Builder b = new JobInfo.Builder(JOB_ID, new ComponentName(this, DownloadJobService.class));
//        PersistableBundle pb=new PersistableBundle();
//        pb.putBoolean(KEY_DOWNLOAD, true);
//        b.setExtras(pb);
//        //criterio de rede
//        b.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
//        switch (job){
//            case "4000":
//                b.setPeriodic(4000);
//                Log.d("job1",job);
//                break;
//            case "1800000":
//                b.setPeriodic(1800000);
//                Log.d("job12",job);
//                break;
//            case "3600000":
//                b.setPeriodic(3600000);
//                Log.d("job13",job);
//                break;
//            case "10800000":
//                b.setPeriodic(10800000);
//                Log.d("job14",job);
//                break;
//            case "21600000":
//                b.setPeriodic(21600000);
//                Log.d("job15",job);
//                break;
//            case "43200000":
//                b.setPeriodic(43200000);
//                Log.d("job16",job);
//                break;
//            case "86400000":
//                b.setPeriodic(86400000);
//                Log.d("job17",job);
//                break;
//        }
//
//        jobScheduler.schedule(b.build());

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onStart() {
        super.onStart();

        //carregamento default
        //new CarregaRSStask().execute(RSS_FEED);
        //sharedPreference
        //new CarregaRSStask().execute(sharedPreferences.getString(getString(R.string.rss_feed),getString(R.string.rss_feed_default)));
        //mudança do usuario atraves de settings
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String url = sharedPreferences.getString(PreferenciasActivity.KEY_PREF_RSS_FEED, "");
        //Criar intent para iniciar o servico de download do feed
        Intent downloadService = new Intent(getApplicationContext(), DownloadXmlRssService.class);
        downloadService.putExtra("url", url);
        startService(downloadService);
//        if(aux == null){
//            aux = url;
//        }
//        if(aux != url){
//            banco = SQLiteRSSHelper.getInstance(this);
//            aux = url;
//        }

//        new CarregaRSStask().execute(url);
        sharedPreferencesJob = PreferenceManager.getDefaultSharedPreferences(this);
        String job = sharedPreferencesJob.getString(PreferenciasActivity.KEY_PREF_JOB_SCHEDULER, "");

        Log.d("job",job);

        //parte que define o jobScheduler
        jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        JobInfo.Builder b = new JobInfo.Builder(JOB_ID, new ComponentName(this, DownloadJobService.class));
        PersistableBundle pb=new PersistableBundle();
        pb.putBoolean(KEY_DOWNLOAD, true);
        b.setExtras(pb);
        //criterio de rede
        b.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        switch (job){
            case "900000":
                b.setPeriodic(900000);
                Log.d("job12",job);
                break;
            case "1800000":
                b.setPeriodic(1800000);
                Log.d("job12",job);
                break;
            case "3600000":
                b.setPeriodic(3600000);
                Log.d("job13",job);
                break;
            case "10800000":
                b.setPeriodic(10800000);
                Log.d("job14",job);
                break;
            case "21600000":
                b.setPeriodic(21600000);
                Log.d("job15",job);
                break;
            case "43200000":
                b.setPeriodic(43200000);
                Log.d("job16",job);
                break;
            case "86400000":
                b.setPeriodic(86400000);
                Log.d("job17",job);
                break;
        }

        jobScheduler.schedule(b.build());



//        //Criar intent para iniciar o servico de download do feed
//        Intent downloadService = new Intent(getApplicationContext(), DownloadXmlRssService.class);
//        downloadService.putExtra("url", url);
//        startService(downloadService);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Registrar o broadcastreceiver dinamico quando o usuario estiver com o app em primeiro plano
//        sharedPreferencesJob = PreferenceManager.getDefaultSharedPreferences(this);
//        String job = sharedPreferencesJob.getString(PreferenciasActivity.KEY_PREF_JOB_SCHEDULER, "");
//
//        Log.d("job",job);
//
//        //parte que define o jobScheduler
//        jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
//        JobInfo.Builder b = new JobInfo.Builder(JOB_ID, new ComponentName(this, DownloadJobService.class));
//        PersistableBundle pb=new PersistableBundle();
//        pb.putBoolean(KEY_DOWNLOAD, true);
//        b.setExtras(pb);
//        //criterio de rede
//        b.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
//        switch (job){
//            case "4000":
//                b.setPeriodic(4000);
//                Log.d("job1",job);
//                break;
//            case "1800000":
//                b.setPeriodic(1800000);
//                Log.d("job12",job);
//                break;
//            case "3600000":
//                b.setPeriodic(3600000);
//                Log.d("job13",job);
//                break;
//            case "10800000":
//                b.setPeriodic(10800000);
//                Log.d("job14",job);
//                break;
//            case "21600000":
//                b.setPeriodic(21600000);
//                Log.d("job15",job);
//                break;
//            case "43200000":
//                b.setPeriodic(43200000);
//                Log.d("job16",job);
//                break;
//            case "86400000":
//                b.setPeriodic(86400000);
//                Log.d("job17",job);
//                break;
//        }
//
//        jobScheduler.schedule(b.build());

        IntentFilter intentFilter = new IntentFilter(DownloadXmlRssService.DOWNLOAD_COMPLETO);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(onDownloadCompleteEvent, intentFilter);

    }

    @Override
    protected void onPause() {
        super.onPause();
        //Cancelar o registro do broadcastReceiver
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(onDownloadCompleteEvent);
    }

    //Evento para quando receber o broadcast.
    private BroadcastReceiver onDownloadCompleteEvent = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(getApplicationContext(), "Notícias foram carregadas, exibindo o feed.", Toast.LENGTH_LONG).show();
            new mostrarFeed().execute();
        }
    };
    @Override
    protected void onDestroy() {
        banco.close();
        super.onDestroy();
    }

//    private class CarregaRSStask extends AsyncTask<String, Void, Boolean> {
//
//        @Override
//        protected Boolean doInBackground(String... mudancas) {
//            boolean problema = false;
//            List<ItemRSS> items = null;
//            try {
//                String feed = getRssFeed(mudancas[0]);
//                items = ParserRSS.parse(feed);
//                for (ItemRSS i : items) {
//                    Log.d("banco", "Buscar no Banco por link: " + i.getLink());
//                    ItemRSS item = banco.getItemRSS(i.getLink());
//                    if (item == null) {
//                        Log.d("banco", "Encontrado pela primeira vez: " + i.getTitle());
//                        banco.insertItem(i);
//                    }
//                }
//
//            } catch (IOException e) {
//                e.printStackTrace();
//                problema = true;
//            } catch (XmlPullParserException e) {
//                e.printStackTrace();
//                problema = true;
//            }
//            return problema;
//        }
//
//        @Override
//        protected void onPostExecute(Boolean problemaConfirmado) {
//            if (problemaConfirmado) {
//                Toast.makeText(MainActivity.this, "Teve algum problema ao carregar o feed.", Toast.LENGTH_SHORT).show();
//            } else {
//                //dispara o task que exibe a lista
//                new mostrarFeed().execute();
//            }
//        }
//    }

    private class mostrarFeed extends AsyncTask<Void, Void, List<ItemRSS>> {

        @Override
        protected List<ItemRSS> doInBackground(Void... voids) {
            parsedResponse =  banco.getItems();
            return parsedResponse;
        }

        @Override
        protected void onPostExecute(List<ItemRSS> rssList) {
            if (rssList != null) {
                final CustomAdapter customAdapterRss = new CustomAdapter(getApplicationContext(), rssList);
                conteudoRSS.setAdapter(customAdapterRss);
                conteudoRSS.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg) {
                        String url = customAdapterRss.getLink(position);
                        if (banco.markAsRead(url)) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(url));
                            startActivity(intent);
                        }
                    }
                });
            }
        }
    }


    //mudança do feed
    public void mudarFeed(View view) {
        Intent intent = new Intent(MainActivity.this, PreferenciasActivity.class);
        startActivity(intent);
    }
}

