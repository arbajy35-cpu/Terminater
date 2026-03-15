package com.nexora.terminater;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.nexora.terminater.fs.FileSystemManager;
import com.nexora.terminater.pkg.PackageInstaller;
import com.nexora.terminater.runtime.CommandRunner;

import java.io.File;
import java.io.FileWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Bridge {

    private Context context;
    private WebView webView;

    private CommandRunner commandRunner;

    private static final String GITHUB_RAW =
            "https://raw.githubusercontent.com/arbajy35-cpu/vi-scripts/main/";

    public Bridge(Context context, WebView webView){
        this.context = context;
        this.webView = webView;

        FileSystemManager fs = new FileSystemManager(context);

        // ✅ FIX: Pass context as third argument
        commandRunner = new CommandRunner(fs, webView, context);
    }

    // =========================
    // DOWNLOAD OFFICIAL SCRIPT
    // =========================
    @JavascriptInterface
    public void downloadOfficialScript(String pkgName){

        new Thread(() -> {

            if(pkgName == null || pkgName.trim().isEmpty()){
                sendToWebView("Error: No package name provided");
                return;
            }

            try{

                String urlStr = GITHUB_RAW + pkgName;
                URL url = new URL(urlStr);

                HttpURLConnection conn =
                        (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("GET");

                int code = conn.getResponseCode();

                if(code != 200){
                    sendToWebView("Package not found ("+code+")");
                    return;
                }

                Scanner scanner =
                        new Scanner(conn.getInputStream())
                                .useDelimiter("\\A");

                String data = scanner.hasNext() ? scanner.next() : "";
                scanner.close();

                FileSystemManager fs =
                        new FileSystemManager(context);

                PackageInstaller installer =
                        new PackageInstaller(fs, webView);

                installer.install(pkgName.toLowerCase(), data);

            }
            catch(Exception e){
                sendToWebView("Download error: " + e.getMessage());
            }

        }).start();
    }

    // =========================
    // RUN TERMINAL COMMAND
    // =========================
    @JavascriptInterface
    public void runCommand(String commandLine){

        if(commandLine == null || commandLine.trim().isEmpty()){
            sendToWebView("");
            return;
        }

        // security block
        if(commandLine.contains("..") ||
                commandLine.contains(".terminater")){
            sendToWebView("Access denied");
            return;
        }

        commandRunner.run(commandLine.trim());
    }

    // =========================
    // SEND OUTPUT TO WEBVIEW
    // =========================
    private void sendToWebView(String message){

        String js =
                "printOutput(" +
                        org.json.JSONObject.quote(message)
                        + ");";

        webView.post(() ->
                webView.evaluateJavascript(js,null));
    }
}
