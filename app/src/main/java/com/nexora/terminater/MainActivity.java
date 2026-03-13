package com.nexora.terminater;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;

// ✅ Correct imports for NativeManager and Bridge
import com.nexora.terminater.core.NativeManager;
import com.nexora.terminater.Bridge;

public class MainActivity extends Activity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // =========================
        // REMOVE TITLE BAR
        // =========================
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // =========================
        // TRUE FULLSCREEN MODE
        // =========================
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );

        setContentView(R.layout.main);

        webView = findViewById(R.id.webview);

        // =========================
        // WEBVIEW SETTINGS
        // =========================
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);

        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);

        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);

        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        settings.setMediaPlaybackRequiresUserGesture(false);

        // PERFORMANCE BOOST
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        // GPU HARDWARE RENDERING
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());

        // Enable DevTools debugging
        WebView.setWebContentsDebuggingEnabled(true);

        // Clear cache for clean start
        webView.clearCache(true);
        webView.clearHistory();

        // =========================
        // JAVASCRIPT BRIDGES
        // =========================
        webView.addJavascriptInterface(new Bridge(this, webView), "AndroidBridge");
        webView.addJavascriptInterface(new NativeManager(this, webView), "AndroidNative");

        // =========================
        // LOAD TERMINAL UI
        // =========================
        webView.loadUrl("file:///android_asset/ui/index.html");
    }
}
