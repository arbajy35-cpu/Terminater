package com.nexora.terminater.runtime;

import android.content.Context;
import android.webkit.WebView;
import com.nexora.terminater.fs.FileSystemManager;
import java.io.*;

public class ScriptRunner {

    private FileSystemManager fs;
    private WebView webView;
    private File safeBinDir;

    public ScriptRunner(FileSystemManager fs, WebView webView, Context context){
        this.fs = fs;
        this.webView = webView;
        safeBinDir = new File(context.getFilesDir(), "bin");
        if(!safeBinDir.exists()) safeBinDir.mkdirs();
    }

    public void run(String scriptName){
        new Thread(() -> {
            try{
                File target = findScript(scriptName);
                if(target == null){
                    send("⚠️ Command not found: "+scriptName);
                    return;
                }

                ProcessBuilder pb;
                if(ElfDetector.isElfBinary(target)){
                    // Safe path for ELF
                    pb = new ProcessBuilder(new File(safeBinDir, target.getName()).getAbsolutePath());
                } else {
                    pb = new ProcessBuilder("sh", target.getAbsolutePath());
                }

                pb.redirectErrorStream(true);
                pb.directory(fs.getUserHome());
                pb.environment().put(
                        "PATH",
                        fs.getUserBinDir().getAbsolutePath() + ":" +
                        fs.getUserCustomDir().getAbsolutePath() + ":" +
                        fs.getSystemBinDir().getAbsolutePath() + ":" +
                        safeBinDir.getAbsolutePath()
                );

                Process process = pb.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                StringBuilder output = new StringBuilder();
                String line;
                while((line = reader.readLine()) != null){
                    output.append(line).append("\n");
                }
                process.waitFor();
                send(output.toString());
            } catch(Exception e){
                send("❌ Run Error: "+e.getMessage());
            }
        }).start();
    }

    private File findScript(String name){
        File userBin = new File(fs.getUserBinDir(),name);
        if(userBin.exists()) return userBin;
        File userCustom = new File(fs.getUserCustomDir(),name);
        if(userCustom.exists()) return userCustom;
        File systemBin = new File(fs.getSystemBinDir(),name);
        if(systemBin.exists()) return systemBin;
        File safe = new File(safeBinDir, name);
        if(safe.exists()) return safe;
        return null;
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
