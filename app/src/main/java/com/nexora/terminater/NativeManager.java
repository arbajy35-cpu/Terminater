package com.nexora.terminater;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;

public class NativeManager {

    private Context context;
    private WebView webView;

    public NativeManager(Context context, WebView webView) {
        this.context = context;
        this.webView = webView;
    }

    // =========================
    // STORAGE PATHS
    // =========================
    private File getUserBinDir() {
        File dir = new File(context.getFilesDir(), ".terminater/home/user/bin");
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    private File getUserCustomDir() {
        File dir = new File(context.getFilesDir(), ".terminater/home/user/custom");
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    private File getSystemBinDir() {
        File dir = new File(context.getFilesDir(), ".terminater/home/system/bin");
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    // =========================
    // SAVE USER SCRIPT (CUSTOM)
    // =========================
    @JavascriptInterface
    public void saveCustomScript(String fileName, String content) {
        new Thread(() -> {
            try {
                File file = new File(getUserCustomDir(), fileName.toLowerCase());
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new java.io.FileOutputStream(file)));
                writer.write(content);
                writer.close();

                file.setReadable(true);
                file.setWritable(true);
                file.setExecutable(true);

                sendToWebView("✅ Custom script saved: " + fileName);
            } catch (Exception e) {
                sendToWebView("❌ Save Error: " + e.getMessage());
            }
        }).start();
    }

    // =========================
    // RUN SCRIPT (USER OR SYSTEM)
    // =========================
    @JavascriptInterface
    public void runScript(String scriptName) {
        new Thread(() -> {
            try {
                File userBin = new File(getUserBinDir(), scriptName);
                File userCustom = new File(getUserCustomDir(), scriptName);
                File systemBin = new File(getSystemBinDir(), scriptName);

                File target = null;
                boolean isSystem = false;

                if (userBin.exists()) target = userBin;
                else if (userCustom.exists()) target = userCustom;
                else if (systemBin.exists()) {
                    target = systemBin;
                    isSystem = true;  // system script, feed via stdin
                }

                if (target == null) {
                    sendToWebView("⚠️ Script not found: " + scriptName);
                    return;
                }

                ProcessBuilder pb = new ProcessBuilder("sh");
                pb.redirectErrorStream(true);
                Process process = pb.start();

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

                // System script → feed via stdin, no read
                if (isSystem) {
                    BufferedReader readerFile = new BufferedReader(new FileReader(target));
                    String line;
                    while ((line = readerFile.readLine()) != null) {
                        writer.write(line);
                        writer.newLine();
                    }
                    writer.flush();
                    readerFile.close();
                } else {
                    // User scripts → normal feed
                    BufferedReader readerFile = new BufferedReader(new FileReader(target));
                    String line;
                    while ((line = readerFile.readLine()) != null) {
                        writer.write(line);
                        writer.newLine();
                    }
                    writer.flush();
                    readerFile.close();
                }

                writer.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                StringBuilder output = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }

                process.waitFor();
                sendToWebView(output.toString());

            } catch (Exception e) {
                sendToWebView("❌ Run Script Error: " + e.getMessage());
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
                sendToWebView("❌ Command Error: " + e.getMessage());
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
