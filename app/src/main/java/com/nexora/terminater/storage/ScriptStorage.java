package com.nexora.terminater.storage;

import android.webkit.WebView;

import com.nexora.terminater.fs.FileSystemManager;

import java.io.File;
import java.io.FileWriter;

public class ScriptStorage {

    private FileSystemManager fs;
    private WebView webView;

    public ScriptStorage(FileSystemManager fs, WebView webView){
        this.fs = fs;
        this.webView = webView;
    }

    public void save(String name, String content){

        try{

            File target = new File(fs.getUserCustomDir(), name);

            FileWriter writer = new FileWriter(target);
            writer.write(content);
            writer.close();

            target.setExecutable(true);

            send("💾 Script saved: " + name);

        }catch(Exception e){

            send("❌ Save error: " + e.getMessage());

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
