package com.cs465.rightthisway;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
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
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

    	switch (item.getItemId()) {
        // Respond to the action bar's Up/Home button
        case R.id.home:
            finish();
            return true;
        }
    	
        return super.onOptionsItemSelected(item);
    }
    
}
