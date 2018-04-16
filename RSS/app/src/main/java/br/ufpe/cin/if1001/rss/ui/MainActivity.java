package br.ufpe.cin.if1001.rss.ui;
import br.ufpe.cin.if1001.rss.R;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import android.content.SharedPreferences;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import br.ufpe.cin.if1001.rss.db.SQLiteRSSHelper;
import br.ufpe.cin.if1001.rss.domain.ItemRSS;
import br.ufpe.cin.if1001.rss.util.CustomAdapter;
import br.ufpe.cin.if1001.rss.util.ParserRSS;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private SQLiteRSSHelper banco;
    private List<ItemRSS> parsedResponse;

    //ao fazer envio da resolucao, use este link no seu codigo!
    //private final String RSS_FEED = "http://leopoldomt.com/if1001/g1brasil.xml";
    private ListView conteudoRSS;
    // variável usada na leitura do xml
    private List<String> ParserLoad;
    private List<ItemRSS> Listresult;


    //OUTROS LINKS PARA TESTAR...
    //http://rss.cnn.com/rss/edition.rss
    //http://pox.globo.com/rss/g1/brasil/
    //http://pox.globo.com/rss/g1/ciencia-e-saude/
    //http://pox.globo.com/rss/g1/tecnologia/

    //use ListView ao invés de TextView - deixe o atributo com o mesmo nome

    // definição
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        banco = SQLiteRSSHelper.getInstance(this);
        //use ListView ao invés de TextView - deixe o ID no layout XML com o mesmo nome conteudoRSS
        //isso vai exigir o processamento do XML baixado da internet usando o ParserRSS
        //adição da tollbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.rss_toolbar);
        setSupportActionBar(toolbar);
        conteudoRSS = (ListView) findViewById(R.id.conteudoRSS);
        //impliementação do sharedPreferences
//        sharedPreferences = getSharedPreferences(getString(R.string.rss_feed), Context.MODE_PRIVATE);
//               editor = sharedPreferences.edit();
//                if (getString(R.string.rss_feed_link).equals("default")){
//                        editor.putString(getString(R.string.rss_feed), getString(R.string.rss_feed_default));
//                    } else {
//                        editor.putString(getString(R.string.rss_feed), getString(R.string.rss_feed_link));
//                    }
//                editor.apply();
        //carregando as preferencias do arquivo xml
        PreferenceManager.setDefaultValues(this, R.xml.preferencias, false);

    }

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
        new CarregaRSStask().execute(url);
    }

    @Override
    protected void onDestroy() {
        banco.close();
        super.onDestroy();
    }

    private class CarregaRSStask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... mudancas) {
            boolean problema = false;
            List<ItemRSS> items = null;
            try {
                String feed = getRssFeed(mudancas[0]);
                items = ParserRSS.parse(feed);
                for (ItemRSS i : items) {
                    Log.d("banco", "Buscar no Banco por link: " + i.getLink());
                    ItemRSS item = banco.getItemRSS(i.getLink());
                    if (item == null) {
                        Log.d("banco", "Encontrado pela primeira vez: " + i.getTitle());
                        banco.insertItem(i);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                problema = true;
            } catch (XmlPullParserException e) {
                e.printStackTrace();
                problema = true;
            }
            return problema;
        }

        @Override
        protected void onPostExecute(Boolean problemaConfirmado) {
            if (problemaConfirmado) {
                Toast.makeText(MainActivity.this, "Teve algum problema ao carregar o feed.", Toast.LENGTH_SHORT).show();
            } else {
                //dispara o task que exibe a lista
                new mostrarFeed().execute();
            }
        }
    }

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

    //Opcional - pesquise outros meios de obter arquivos da internet
    private String getRssFeed(String feed) throws IOException {
        InputStream in = null;
        String rssFeed = "";
        try {
            URL url = new URL(feed);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            in = conn.getInputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for (int count; (count = in.read(buffer)) != -1; ) {
                out.write(buffer, 0, count);
            }
            byte[] response = out.toByteArray();
            rssFeed = new String(response, "UTF-8");
        } finally {
            if (in != null) {
                in.close();
            }
        }
        return rssFeed;
    }

    //mudança do feed
    public void mudarFeed(View view) {
        Intent intent = new Intent(this, PreferenciasActivity.class);
        startActivity(intent);
    }
}

