package com.example.liber;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    EditText urlinput;
    TextView link, liber;

    ScrollView scroll;
    LinearLayout layout;
    LinearLayout linearLayout;
    ImageView clearUrl, webBack, webForward, webRefresh, webShare;
    WebView webView;
    ProgressBar progressBar;



    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        scroll = findViewById(R.id.scrollik);
        link = getLayoutInflater().inflate(R.layout.link, null).findViewById(R.id.name);
        urlinput = findViewById(R.id.url_input);
        clearUrl = findViewById(R.id.clear_icon);
        progressBar = findViewById(R.id.progress_bar);
        webView = findViewById(R.id.web_view);
        webBack = findViewById(R.id.web_back);
        webForward = findViewById(R.id.web_forward);
        webRefresh = findViewById(R.id.web_refresh);
        webShare = findViewById(R.id.web_share);
        layout = findViewById(R.id.link_arr);
        liber = findViewById(R.id.liber);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);

        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressBar.setProgress(newProgress);
            }
        });



        urlinput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_GO || i == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(urlinput.getWindowToken(), 0);
                    webView.setVisibility(View.INVISIBLE);
                    scroll.setVisibility(View.VISIBLE);
                    layout.removeAllViews();
                    liber.setVisibility(View.VISIBLE);
                    try {
                        loadMyUrl(urlinput.getText().toString());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return true;
                }
                return false;
            }
        });



        clearUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                urlinput.setText("");
            }
        });

        webBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (webView.canGoBack()) {
                    webView.goBack();
                    urlinput.setTextColor(111);
                }
            }
        });

        webForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (webView.canGoForward()) {
                    webView.goForward();
                }

            }
        });

        webRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webView.reload();
            }
        });

        webShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, webView.getUrl());
                intent.setType("text/plain");
                startActivity(intent);
            }
        });
    }

    void loadMyUrl(String url) throws IOException {
        boolean matchUrl = Patterns.WEB_URL.matcher(url).matches();
        if (matchUrl) {
            webView.loadUrl(url);

        } else {
            search(url);
        }
    }

    public void search(String url) throws IOException {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String googleUrl = "https://www.google.com/search?q=" + url;

        // Connect to the Google search page
        Document doc = Jsoup.connect(googleUrl).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.4951.54 Safari/537.36").get();
        // Document object represents the HTML dom (Talking about "doc" here)
        Elements results = doc.select("div.g");
        int c = 0;
        ArrayList<String> arr = new ArrayList<String>();





        for (Element result : results) {
            // Extract the title and link of the result
            String title = result.select("h3").text();
            String link = result.select(".yuRUbf > a").attr("href");
            String snippet = result.select(".VwiC3b").text();

            arr.add(link);


            TextView textLink = new TextView(this);

            textLink.setText(title);
//            textLink.setHeight(100);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//            params.setMargins(0, 0, 0, 50);
            textLink.setTextColor(Color.parseColor("#0000ff"));
            textLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    webView.setVisibility(View.VISIBLE);
                    scroll.setVisibility(View.INVISIBLE);
                    liber.setVisibility(View.INVISIBLE);
                    webView.loadUrl(link);

                }
            });
            layout.addView(textLink, params);

            TextView snippetLink = new TextView(this);
            snippetLink.setText(snippet);
            LinearLayout.LayoutParams snippetParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            snippetParams.setMargins(0, 0, 0, 50);
            layout.addView(snippetLink, snippetParams);

            c++;
        }
        System.out.println(arr);
    }

    @Override
    public void onBackPressed() {

        if (webView.canGoBack()) {
            webView.goBack();
            urlinput.setText("");
        } else {
            super.onBackPressed();
        }
    }

    class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            urlinput.setText(webView.getUrl());
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}