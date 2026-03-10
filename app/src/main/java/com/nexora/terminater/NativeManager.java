package com.nexora.terminater;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;

public class NativeManager {

    private Context context;
    private WebView webView;

    public NativeManager(Context context, WebView webView) {
        this.context = context;
        this.webView = webView;
    }

    // =========================
    // HIDDEN STORAGE PATHS
    // =========================
    private File getBinDir() {
        File bin = new File(context.getFilesDir(), ".terminater/home/user/bin");
        if (!bin.exists()) bin.mkdirs();
        return bin;
    }

    private File getCustomDir() {
        File custom = new File(context.getFilesDir(), ".terminater/home/user/custom");
        if (!custom.exists()) custom.mkdirs();
        return custom;
    }

    // =========================
    // SAVE USER SCRIPT (CUSTOM)
    // =========================
    @JavascriptInterface
    public void saveCustomScript(String fileName, String content) {
        new Thread(() -> {
            try {
                File file = new File(getCustomDir(), fileName.toLowerCase());
                FileWriter writer = new FileWriter(file);
                writer.write(content);
                writer.close();
                sendToWebView("Custom script saved: " + fileName);
            } catch (Exception e) {
                sendToWebView("Save Error: " + e.getMessage());
            }
        }).start();
    }

    // =========================
    // RUN SCRIPT (BIN or CUSTOM)
    // =========================
    @JavascriptInterface
    public void runScript(String scriptName) {
        new Thread(() -> {
            try {
                File binFile = new File(getBinDir(), scriptName);
                File customFile = new File(getCustomDir(), scriptName);
                File targetFile = binFile.exists() ? binFile : (customFile.exists() ? customFile : null);

                if (targetFile == null) {
                    sendToWebView("Error: Script not found: " + scriptName);
                    return;
                }

                // Run shell command
                ProcessBuilder pb = new ProcessBuilder("sh", "-c", targetFile.getAbsolutePath());
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
                sendToWebView("Run Script Error: " + e.getMessage());
            }
        }).start();
    }

    // =========================
    // EXECUTE ARBITRARY COMMAND
    // =========================
    @JavascriptInterface
    public void runCommand(String commandLine) {
        new Thread(() -> {
            try {
                ProcessBuilder pb = new ProcessBuilder("sh", "-c", commandLine);
                pb.redirectErrorStream(true);

                Process process = pb.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                StringBuilder output = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }

                process.waitFor();
                sendToWebView(output.toString());

            } catch (Exception e) {
                sendToWebView("Command Error: " + e.getMessage());
            }
        }).start();
    }

    // =========================
    // SEND OUTPUT TO WEBVIEW
    // =========================
    private void sendToWebView(String message) {
        String js = "printOutput(" + JSONObject.quote(message) + ");";
        webView.post(() -> webView.evaluateJavascript(js, null));
    }
}
