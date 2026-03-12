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
    // BASE PATHS
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

                // 🔥 Correct permissions
                file.setReadable(true, false);
                file.setWritable(true, false);
                file.setExecutable(true, false);

                sendToWebView("✅ Custom script saved: " + fileName);

            } catch (Exception e) {

                sendToWebView("❌ Save Error: " + e.getMessage());

            }

        }).start();
    }

    // =========================
    // FIND SCRIPT IN PATH
    // =========================

    private File findScript(String name) {

        File userBin = new File(getUserBinDir(), name);
        if (userBin.exists()) return userBin;

        File userCustom = new File(getUserCustomDir(), name);
        if (userCustom.exists()) return userCustom;

        File systemBin = new File(getSystemBinDir(), name);
        if (systemBin.exists()) return systemBin;

        return null;
    }

    // =========================
    // RUN SCRIPT
    // =========================

    @JavascriptInterface
    public void runScript(String scriptName) {

        new Thread(() -> {

            try {

                File target = findScript(scriptName);

                if (target == null) {

                    sendToWebView("⚠️ Command not found: " + scriptName);
                    return;

                }

                ProcessBuilder pb = new ProcessBuilder(
                        "sh",
                        target.getAbsolutePath()
                );

                pb.redirectErrorStream(true);

                // 🔥 working directory
                pb.directory(getUserHome());

                // PATH ENVIRONMENT
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

                sendToWebView("❌ Run Error: " + e.getMessage());

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

                ProcessBuilder pb = new ProcessBuilder(
                        "sh",
                        "-c",
                        commandLine
                );

                pb.redirectErrorStream(true);

                pb.directory(getUserHome());

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
