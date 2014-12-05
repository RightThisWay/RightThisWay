package com.cs465.rightthisway;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.webkit.WebView;

public class HelpActivity extends ActionBarActivity {
    private WebView webView;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);
        webView = (WebView) findViewById(R.id.webView1);
        webView.loadUrl("file:///android_asset/index.html");
    }
    
}
