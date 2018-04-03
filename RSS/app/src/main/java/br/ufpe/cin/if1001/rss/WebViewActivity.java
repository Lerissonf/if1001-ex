package br.ufpe.cin.if1001.rss;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class WebViewActivity extends Activity {
    private WebView clevercode;
    private WebSettings webSettings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        clevercode = (WebView) findViewById(R.id.myWebView);
        clevercode.loadUrl("https://www.google.com.br");
        webSettings = clevercode.getSettings();
        webSettings.setJavaScriptEnabled(true);

    }
}
