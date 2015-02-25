package cn.com.xfile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

public class WebViewActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xfile_webview);
        
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        
        WebView wv = (WebView)findViewById(R.id.webview);
        
        /*
        wv.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                view.loadUrl(url);
                return true;
            }
            
        });
        */
        wv.loadUrl(url);
    }

}
