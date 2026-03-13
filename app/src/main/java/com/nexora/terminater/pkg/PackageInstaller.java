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

    public void install(String name, String script){

        try{

            File target = new File(fs.getSystemBinDir(), name);

            FileWriter writer = new FileWriter(target);
            writer.write(script);
            writer.close();

            target.setExecutable(true);

            send("✅ Package installed: " + name);

        }catch(Exception e){

            send("❌ Install error: " + e.getMessage());

        }

    }

    private void send(String message){

        webView.post(() ->
                webView.evaluateJavascript(
                        "printOutput("+org.json.JSONObject.quote(message)+");",
                        null
                )
        );

    }

}
