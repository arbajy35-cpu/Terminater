package com.nexora.terminater;

import android.app.Activity;
import android.os.Bundle;
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
        setContentView(R.layout.main);

        webView = findViewById(R.id.webview);

        // =========================
        // WebView Settings
        // =========================
        WebSettings webSettings = webView.getSettings();

        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);

        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);

        webSettings.setDatabaseEnabled(true);

        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());

        // Enable debugging (dev tools)
        WebView.setWebContentsDebuggingEnabled(true);

        // Clear cache (prevents bridge caching bug)
        webView.clearCache(true);
        webView.clearHistory();

        // =========================
        // Add JS Bridges
        // =========================
        webView.addJavascriptInterface(new NativeRunner(), "AndroidNative");
        webView.addJavascriptInterface(new AndroidBridge(), "AndroidBridge");

        // =========================
        // Load UI (FIXED PATH)
        // =========================
        webView.loadUrl("file:///android_asset/ui/index.html");
    }

    // ====================================================
    // Native Runner (Primary Bridge)
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

        // Save script
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
    // Legacy Bridge
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
    // Command Executor
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
    // Send Output To WebView
    // ====================================================
    private void sendToWebView(String message) {

        String js = "printOutput(" + JSONObject.quote(message) + ");";

        runOnUiThread(() -> webView.evaluateJavascript(js, null));
    }
}
