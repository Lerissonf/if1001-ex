package br.ufpe.cin.if1001.rss.ServiceTest;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import br.ufpe.cin.if1001.rss.db.SQLiteRSSHelper;
import br.ufpe.cin.if1001.rss.domain.ItemRSS;
import br.ufpe.cin.if1001.rss.util.ParserRSS;

/**
 * Created by LERISSON on 16/04/2018.
 */

public class DownloadXmlRssService extends IntentService {

    SQLiteRSSHelper banco;
    //Mensagem do broadcast para ser usada no itent de escuta (filter)
    public static final String DOWNLOAD_COMPLETO = "br.ufpe.cin.if1001.rss.action.DOWNLOAD_COMPLETO";
    ////Mensagem do broadcast para ser usada só quando houver uma nova notícia
    public static final String NEW_REPORT = "br.ufpe.cin.if1001.rss.NEW_REPORT";

    public DownloadXmlRssService() {
        super("DownloadXmlRssService");
    }

    @Override
    protected void onHandleIntent( Intent intent) {
        banco = SQLiteRSSHelper.getInstance(getApplicationContext());
        boolean problema = false;
        List<ItemRSS> items = null;
        try {
            String feed = getRssFeed(intent.getStringExtra("url"));
            items = ParserRSS.parse(feed);
            for (ItemRSS i : items) {
                Log.d("banco", "Buscar no Banco por link: " + i.getLink());
                ItemRSS item = banco.getItemRSS(i.getLink());
                if (item == null) {
                    sendBroadcast(new Intent(NEW_REPORT));
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

        if (problema) {
            Log.d("FEED", "Houve algum problema durante o carregar do feed.");
        } else {
            //O feed foi carregado, foi enviado o broadcast
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(DOWNLOAD_COMPLETO));
            Log.d("FEED1", "broadcast.");
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
