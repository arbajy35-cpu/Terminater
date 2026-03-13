package com.nexora.terminater.runtime;

import android.webkit.WebView;

import com.nexora.terminater.fs.FileSystemManager;

import java.io.*;

public class CommandRunner {

    private FileSystemManager fs;
    private WebView webView;

    public CommandRunner(FileSystemManager fs,WebView webView){
        this.fs = fs;
        this.webView = webView;
    }

    public void run(String commandLine){

        new Thread(() -> {

            try{

                if(commandLine.contains("system/bin")){
                    send("❌ Permission denied");
                    return;
                }

                if(commandLine.contains("..")){
                    send("❌ Access denied");
                    return;
                }

                ProcessBuilder pb = new ProcessBuilder("sh","-c",commandLine);

                pb.redirectErrorStream(true);
                pb.directory(fs.getUserHome());

                pb.environment().put(
                        "PATH",
                        fs.getUserBinDir().getAbsolutePath()
                        + ":" +
                        fs.getUserCustomDir().getAbsolutePath()
                        + ":" +
                        fs.getSystemBinDir().getAbsolutePath()
                );

                Process process = pb.start();

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream())
                );

                StringBuilder output = new StringBuilder();
                String line;

                while((line = reader.readLine()) != null){
                    output.append(line).append("\n");
                }

                process.waitFor();

                send(output.toString());

            }catch(Exception e){

                send("❌ Command Error: "+e.getMessage());

            }

        }).start();

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
