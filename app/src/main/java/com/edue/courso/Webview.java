package com.edue.courso;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.Objects;

public class Webview extends AppCompatActivity {

    WebView webview;
    String loadUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        loadUrl = Objects.requireNonNull(getIntent().getExtras()).getString("urlForWebView");

        Log.d("urlForWebview : " , loadUrl);

        webview = findViewById(R.id.webView);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().getAllowContentAccess();
        webview.setWebChromeClient(new WebChromeClient());
        webview.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
        webview.loadUrl(loadUrl);
    }
}
