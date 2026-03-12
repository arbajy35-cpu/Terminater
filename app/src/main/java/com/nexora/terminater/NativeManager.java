package com.nexora.terminater;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import org.json.JSONObject;

import java.io.*;

public class NativeManager {

    private Context context;
    private WebView webView;

    public NativeManager(Context context, WebView webView) {
        this.context = context;
        this.webView = webView;
    }

    // =========================
    // BASE PATH
    // =========================

    private File getBaseDir() {
        File dir = new File(context.getFilesDir(), ".terminater/home");
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    private File getUserHome() {
        File dir = new File(getBaseDir(), "user");
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    private File getUserBinDir() {
        File dir = new File(getUserHome(), "bin");
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    private File getUserCustomDir() {
        File dir = new File(getUserHome(), "custom");
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    private File getSystemBinDir() {
        File dir = new File(getBaseDir(), "system/bin");
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    // =========================
    // SAVE USER SCRIPT
    // =========================

    @JavascriptInterface
    public void saveCustomScript(String fileName, String content) {

        new Thread(() -> {
            try {

                File file = new File(getUserCustomDir(), fileName.toLowerCase());

                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(new FileOutputStream(file))
                );

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
    // RUN SCRIPT
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

                if (userBin.exists()) {

                    target = userBin;

                } else if (userCustom.exists()) {

                    target = userCustom;

                } else if (systemBin.exists()) {

                    target = systemBin;
                    isSystem = true;

                }

                if (target == null) {

                    sendToWebView("⚠️ Script not found: " + scriptName);
                    return;

                }

                ProcessBuilder pb;

                if (isSystem) {

                    pb = new ProcessBuilder(target.getAbsolutePath());

                } else {

                    pb = new ProcessBuilder("sh");

                }

                pb.redirectErrorStream(true);

                // 🔥 FIX: set working directory
                pb.directory(getUserHome());

                // PATH ENVIRONMENT
                pb.environment().put(
                        "PATH",
                        getUserBinDir().getAbsolutePath()
                                + ":" +
                                getSystemBinDir().getAbsolutePath()
                );

                Process process = pb.start();

                if (!isSystem) {

                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(process.getOutputStream())
                    );

                    BufferedReader readerFile = new BufferedReader(
                            new FileReader(target)
                    );

                    String line;

                    while ((line = readerFile.readLine()) != null) {

                        writer.write(line);
                        writer.newLine();

                    }

                    writer.flush();
                    writer.close();
                    readerFile.close();

                }

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

                sendToWebView("❌ Run Script Error: " + e.getMessage());

            }

        }).start();
    }

    // =========================
    // RUN COMMAND
    // =========================

    @JavascriptInterface
    public void runCommand(String commandLine) {

        new Thread(() -> {

            try {

                ProcessBuilder pb = new ProcessBuilder("sh", "-c", commandLine);

                pb.redirectErrorStream(true);

                // 🔥 FIX: working directory
                pb.directory(getUserHome());

                // PATH SUPPORT
                pb.environment().put(
                        "PATH",
                        getUserBinDir().getAbsolutePath()
                                + ":" +
                                getSystemBinDir().getAbsolutePath()
                );

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
