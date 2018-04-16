package br.ufpe.cin.if1001.rss;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import org.xmlpull.v1.XmlPullParserException;
import android.content.SharedPreferences;
import android.content.Context;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class MainActivity extends Activity {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

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
        //use ListView ao invés de TextView - deixe o ID no layout XML com o mesmo nome conteudoRSS
        //isso vai exigir o processamento do XML baixado da internet usando o ParserRSS
        conteudoRSS = (ListView) findViewById(R.id.conteudoRSS);
        sharedPreferences = getSharedPreferences(getString(R.string.rss_feed), Context.MODE_PRIVATE);
               editor = sharedPreferences.edit();
                if (getString(R.string.rss_feed_link).equals("default")){
                        editor.putString(getString(R.string.rss_feed), getString(R.string.rss_feed_default));
                    } else {
                        editor.putString(getString(R.string.rss_feed), getString(R.string.rss_feed_link));
                    }
                editor.apply();

    }

    @Override
    protected void onStart() {
        super.onStart();
        //new CarregaRSStask().execute(RSS_FEED);
        new CarregaRSStask().execute(sharedPreferences.getString(getString(R.string.rss_feed),getString(R.string.rss_feed_default)));
    }

    private class CarregaRSStask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            Toast.makeText(getApplicationContext(), "iniciando...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... params) {
            String conteudo = "provavelmente deu erro...";
            try {
                conteudo = getRssFeed(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return conteudo;
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(getApplicationContext(), "terminando...", Toast.LENGTH_SHORT).show();

            //ajuste para usar uma ListView
            //o layout XML a ser utilizado esta em res/layout/itemlista.xml
            try {
                //ParserLoad=ParserRSS.parserSimples(s);
                Log.i("conteudo", s);
                //carregando a lista usando o parser
                Listresult = ParserRSS.parse(s);
                //conteudoRSS.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, ParserLoad));
                //conteudoRSS.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, Listresult));
                //criação do custom adapter para lidar com as especificaçoes do exercicio.
                final CustomAdapter CustomAdapter = new CustomAdapter(getApplicationContext(), Listresult);
                conteudoRSS.setAdapter(CustomAdapter);
                conteudoRSS.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                   @Override
                   public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg) {
                       String url = CustomAdapter.getLink(position);
                       Intent intent = new Intent(Intent.ACTION_VIEW);
                       intent.setData(Uri.parse(url));
                       startActivity(intent);
                   }
                });


            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
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
}
