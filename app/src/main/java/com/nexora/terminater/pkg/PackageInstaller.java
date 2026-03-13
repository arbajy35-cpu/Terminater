package com.nexora.terminater.pkg;

import android.webkit.WebView;
import com.nexora.terminater.fs.FileSystemManager;

import java.io.File;
import java.io.FileWriter;

public class PackageInstaller {

    private FileSystemManager fs;
    private WebView webView;

    public PackageInstaller(FileSystemManager fs, WebView webView){
        this.fs = fs;
        this.webView = webView;
    }

    // =========================
    // INSTALL PACKAGE TO SYSTEM BIN (SECURE)
    // =========================
    public void install(String name, String script){

        try{
            // Ensure system/bin exists
            File systemBin = fs.getSystemBinDir();
            if(!systemBin.exists()) systemBin.mkdirs();

            // Create target file in system/bin
            File target = new File(systemBin, name.toLowerCase());

            // Write script content
            FileWriter writer = new FileWriter(target);
            writer.write(script);
            writer.close();

            // =========================
            // SET SECURE PERMISSIONS
            // =========================
            target.setReadable(true, false);    // owner & world can read
            target.setWritable(false, false);   // non-writable for everyone
            target.setExecutable(true, false);  // executable by everyone

            // Force chmod 555 for absolute security
            Runtime.getRuntime().exec("chmod 555 " + target.getAbsolutePath());

            send("✅ Package installed securely: " + name);

        }catch(Exception e){
            send("❌ Install error: " + e.getMessage());
        }

    }

    // =========================
    // SEND MESSAGE TO WEBVIEW TERMINAL
    // =========================
    private void send(String message){
        webView.post(() ->
                webView.evaluateJavascript(
                        "printOutput("+org.json.JSONObject.quote(message)+");",
                        null
                )
        );
    }

}
