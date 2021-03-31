package com.hearts.customer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class TawkActivity extends AppCompatActivity {


    private WebView tawkWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tawk);

        tawkWebView = findViewById(R.id.tawkWeb);

        tawkWebView.getSettings().setGeolocationEnabled(true);
        tawkWebView.getSettings().setJavaScriptEnabled(true);
        tawkWebView.getSettings().setAllowFileAccess(true);
        tawkWebView.getSettings().getAllowFileAccessFromFileURLs();
        tawkWebView.getSettings().setAllowContentAccess(true);
        tawkWebView.getSettings().setDomStorageEnabled(true);
        tawkWebView.setWebChromeClient(new WebChromeClient());
        tawkWebView.setWebViewClient(new WebViewClient());
        tawkWebView.loadUrl("file:///android_asset/index.html");


    }
}