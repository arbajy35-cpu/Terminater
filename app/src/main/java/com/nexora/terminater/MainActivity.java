package com.nexora.terminater;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.webkit.JavascriptInterface;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileWriter;

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
        WebSettings webSettings = webView.getSettings();

        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);

        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);

        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);

        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        webSettings.setMediaPlaybackRequiresUserGesture(false);

        // PERFORMANCE BOOST
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        // GPU HARDWARE RENDERING
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());

        // Enable DevTools debugging
        WebView.setWebContentsDebuggingEnabled(true);

        // Clear cache
        webView.clearCache(true);
        webView.clearHistory();

        // =========================
        // JAVASCRIPT BRIDGES
        // =========================
        webView.addJavascriptInterface(new NativeRunner(), "AndroidNative");
        webView.addJavascriptInterface(new AndroidBridge(), "AndroidBridge");

        // =========================
        // LOAD TERMINAL UI
        // =========================
        webView.loadUrl("file:///android_asset/ui/index.html");
    }

    // ====================================================
    // NATIVE COMMAND RUNNER
    // ====================================================
    public class NativeRunner {

        @JavascriptInterface
        public void runNativeCommand(String commandLine) {

            new Thread(() -> {

                try {

                    ProcessBuilder pb = new ProcessBuilder("sh", "-c", commandLine);
                    pb.redirectErrorStream(true);

                    Process process = pb.start();

                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(process.getInputStream())
                    );

                    StringBuilder output = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        output.append(line).append("\n");
                    }

                    process.waitFor();

                    sendToWebView(output.toString());

                } catch (Exception e) {

                    sendToWebView("Error: " + e.getMessage());

                }

            }).start();
        }

        // =========================
        // SAVE SCRIPT SYSTEM
        // =========================
        @JavascriptInterface
        public void saveScript(String fileName, String content) {

            try {

                File dir = new File(getFilesDir(), "vi_scripts");

                if (!dir.exists()) dir.mkdirs();

                File file = new File(dir, fileName.toLowerCase());

                FileWriter writer = new FileWriter(file);
                writer.write(content);
                writer.close();

                sendToWebView("Saved: " + fileName);

            } catch (Exception e) {

                sendToWebView("Save Error: " + e.getMessage());

            }
        }
    }

    // ====================================================
    // LEGACY COMMAND BRIDGE
    // ====================================================
    public class AndroidBridge {

        @JavascriptInterface
        public void runCommand(String command) {

            String output = executeNativeCommand(command);

            runOnUiThread(() -> {

                try {

                    webView.evaluateJavascript(
                            "printOutput(" + JSONObject.quote(output) + ");",
                            null
                    );

                } catch (Exception e) {

                    webView.evaluateJavascript(
                            "printOutput(" + JSONObject.quote("Error: " + e.getMessage()) + ");",
                            null
                    );

                }

            });
        }
    }

    // ====================================================
    // COMMAND EXECUTOR
    // ====================================================
    private String executeNativeCommand(String commandLine) {

        StringBuilder output = new StringBuilder();

        try {

            ProcessBuilder pb = new ProcessBuilder("sh", "-c", commandLine);
            pb.redirectErrorStream(true);

            Process process = pb.start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            String line;

            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            process.waitFor();

        } catch (Exception e) {

            output.append("Error: ").append(e.getMessage());

        }

        return output.toString();
    }

    // ====================================================
    // SEND OUTPUT TO WEBVIEW
    // ====================================================
    private void sendToWebView(String message) {

        String js = "printOutput(" + JSONObject.quote(message) + ");";

        runOnUiThread(() -> webView.evaluateJavascript(js, null));
    }
}
