package com.tianyue.tv.Activity.Discovery;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.tianyue.tv.Gson.DiscoveryGson;
import com.tianyue.tv.R;

/**
 * 废弃
 * Created by hasee on 2016/8/12.
 */
public class DiscoveryDetails extends Activity {
    private DiscoveryGson.DataList discovery;
    private WebView contentWebView;
    private TextView read;
    private TextView time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discovery_details_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.discovery_articles_toolbar);
        toolbar.inflateMenu(R.menu.discovery_details_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        discovery = (DiscoveryGson.DataList) getIntent().getSerializableExtra("discovery");
        initView();
    }
    private void initView(){
        contentWebView = (WebView) findViewById(R.id.discovery_articles_details_content);
        time = (TextView) findViewById(R.id.discovery_articles_details_time);

        contentWebView.getSettings().setJavaScriptEnabled(true);
        contentWebView.loadDataWithBaseURL(null,discovery.getContent(),"text/html","utf-8",null);
    }

}
