package com.wq.tec.frame.web;

import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.wq.tec.WQActivity;

/**
 * Created by N on 2017/3/3.
 */

public class WebAcitivity extends WQActivity {

    WebView mWebView ;

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        setContentView(mWebView = new WebView(this));
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(mClient);
        initWebSetting();
    }

    void initWebSetting(){
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setDatabaseEnabled(true);

        webSettings.setSupportZoom(true);
        webSettings.setDefaultTextEncodingName("utf-8");
        webSettings.setLoadsImagesAutomatically(true);
    }

    private WebViewClient mClient = new WebViewClient(){
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            mWebView.loadUrl(url);
            return true;
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mWebView.loadUrl(getIntent().getStringExtra("webUrl"));
    }

    @Override
    public void onBackPressed() {
        if(mWebView.canGoBack()){
            mWebView.goBack();
        }else{
            super.onBackPressed();
        }
    }
}
