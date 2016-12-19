package com.tianyue.tv.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.tianyue.tv.R;

import butterknife.BindView;

/**
 * Created by hasee on 2016/11/10.
 */
public class LoadWebActivity extends BaseActivity{
    @BindView(R.id.load_web)
    WebView loadWeb;
    @BindView(R.id.load_web_progress)
    ProgressBar progress;
    String url;
    @Override
    protected void initView(){
        setContentView(R.layout.load_web_layout);
        Bundle bundle = getIntent().getExtras();
        url = bundle.getString("url");
        loadWeb.getSettings().setJavaScriptEnabled(true);
        loadWeb.getSettings().setUseWideViewPort(true);
        loadWeb.getSettings().setLoadWithOverviewMode(true);
        loadWeb.loadUrl(url);
        loadWeb.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        loadWeb.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progress.setProgress(newProgress);
                if (progress.getProgress() == 100) {
                    progress.setVisibility(View.GONE);
                }
            }
        });
    }
}
