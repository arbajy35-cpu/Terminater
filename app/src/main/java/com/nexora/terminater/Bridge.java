package com.nexora.terminater;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.nexora.terminater.fs.FileSystemManager;
import com.nexora.terminater.pkg.PackageInstaller;

import java.io.File;
import java.io.FileWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Bridge {

    private Context context;
    private WebView webView;
    private static final String GITHUB_RAW =
            "https://raw.githubusercontent.com/arbajy35-cpu/vi-scripts/main/";

    public Bridge(Context context, WebView webView){
        this.context = context;
        this.webView = webView;
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

            try {
                String urlStr = GITHUB_RAW + pkgName;
                URL url = new URL(urlStr);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(15000);

                int code = conn.getResponseCode();
                if(code != 200){
                    sendToWebView("Error: Package not found on GitHub ("+code+")");
                    return;
                }

                Scanner scanner = new Scanner(conn.getInputStream()).useDelimiter("\\A");
                String data = scanner.hasNext() ? scanner.next() : "";
                scanner.close();

                // Save downloaded script to temp bin dir
                File binDir = new File(
                        context.getFilesDir(),
                        ".terminater/home/user/bin"
                );
                if(!binDir.exists()) binDir.mkdirs();

                File tempFile = new File(binDir, pkgName.toLowerCase());
                FileWriter writer = new FileWriter(tempFile);
                writer.write(data);
                writer.close();

                // Call PackageInstaller for final install
                FileSystemManager fs = new FileSystemManager(context);
                PackageInstaller installer = new PackageInstaller(fs, webView);
                installer.install(pkgName.toLowerCase(), data);

            } catch(Exception e){
                sendToWebView("Download Error: "+e.getMessage());
            }

        }).start();

    }

    // =========================
    // RUN SHELL COMMAND
    // =========================
    @JavascriptInterface
    public void runCommand(String commandLine){
        new Thread(() -> {
            StringBuilder output = new StringBuilder();
            try{
                ProcessBuilder pb = new ProcessBuilder("sh","-c", commandLine);
                pb.redirectErrorStream(true);
                Process process = pb.start();

                java.io.BufferedReader reader =
                        new java.io.BufferedReader(
                                new java.io.InputStreamReader(process.getInputStream())
                        );

                String line;
                while((line = reader.readLine()) != null){
                    output.append(line).append("\n");
                }

                process.waitFor();

            }catch(Exception e){
                output.append("Command Error: "+e.getMessage());
            }

            sendToWebView(output.toString());
        }).start();
    }

    // =========================
    // SEND OUTPUT TO TERMINAL
    // =========================
    private void sendToWebView(String message){
        String js = "printOutput("+org.json.JSONObject.quote(message)+");";
        webView.post(() -> webView.evaluateJavascript(js, null));
    }

}
