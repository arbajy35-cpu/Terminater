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
        initDirs();
    }

    // =========================
    // INIT DIRECTORIES
    // =========================

    private void initDirs() {

        getBaseDir();
        getUserHome();
        getUserBinDir();
        getUserCustomDir();
        getSystemBinDir();

    }

    // =========================
    // BASE PATHS
    // =========================

    private File getBaseDir() {

        File dir = new File(context.getFilesDir(), ".terminater/home");

        if (!dir.exists()) dir.mkdirs();

        dir.setReadable(true,false);
        dir.setWritable(true,false);
        dir.setExecutable(true,false);

        return dir;
    }

    private File getUserHome() {

        File dir = new File(getBaseDir(),"user");

        if(!dir.exists()) dir.mkdirs();

        dir.setReadable(true,false);
        dir.setWritable(true,false);
        dir.setExecutable(true,false);

        return dir;
    }

    private File getUserBinDir() {

        File dir = new File(getUserHome(),"bin");

        if(!dir.exists()) dir.mkdirs();

        dir.setReadable(true,false);
        dir.setWritable(true,false);
        dir.setExecutable(true,false);

        return dir;
    }

    private File getUserCustomDir() {

        File dir = new File(getUserHome(),"custom");

        if(!dir.exists()) dir.mkdirs();

        dir.setReadable(true,false);
        dir.setWritable(true,false);
        dir.setExecutable(true,false);

        return dir;
    }

    private File getSystemBinDir() {

        File dir = new File(getBaseDir(),"system/bin");

        if(!dir.exists()) dir.mkdirs();

        dir.setReadable(true,false);
        dir.setWritable(false,false);
        dir.setExecutable(true,false);

        return dir;
    }

    // =========================
    // ELF DETECTION
    // =========================

    private boolean isElfBinary(File file) {

        try {

            FileInputStream fis = new FileInputStream(file);

            byte[] header = new byte[4];

            fis.read(header);
            fis.close();

            return header[0] == 0x7F &&
                   header[1] == 'E' &&
                   header[2] == 'L' &&
                   header[3] == 'F';

        } catch(Exception e){

            return false;

        }

    }

    // =========================
    // INSTALL SYSTEM PACKAGE
    // =========================

    @JavascriptInterface
    public void installSystemPackage(String name,String script){

        new Thread(() -> {

            try{

                File file = new File(getSystemBinDir(),name.toLowerCase());

                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(new FileOutputStream(file))
                );

                writer.write(script);
                writer.close();

                file.setReadable(true,false);
                file.setWritable(false,false);
                file.setExecutable(true,false);

                sendToWebView("📦 Installed: "+name);

            }catch(Exception e){

                sendToWebView("❌ Install Error: "+e.getMessage());

            }

        }).start();

    }

    // =========================
    // SAVE CUSTOM SCRIPT
    // =========================

    @JavascriptInterface
    public void saveCustomScript(String name,String content){

        new Thread(() -> {

            try{

                File file = new File(getUserCustomDir(),name.toLowerCase());

                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(new FileOutputStream(file))
                );

                writer.write(content);
                writer.close();

                file.setReadable(true,false);
                file.setWritable(true,false);
                file.setExecutable(true,false);

                sendToWebView("✅ Custom script saved: "+name);

            }catch(Exception e){

                sendToWebView("❌ Save Error: "+e.getMessage());

            }

        }).start();

    }

    // =========================
    // FIND SCRIPT
    // =========================

    private File findScript(String name){

        File userBin = new File(getUserBinDir(),name);
        if(userBin.exists()) return userBin;

        File userCustom = new File(getUserCustomDir(),name);
        if(userCustom.exists()) return userCustom;

        File systemBin = new File(getSystemBinDir(),name);
        if(systemBin.exists()) return systemBin;

        return null;

    }

    // =========================
    // RUN SCRIPT
    // =========================

    @JavascriptInterface
    public void runScript(String scriptName){

        new Thread(() -> {

            try{

                File target = findScript(scriptName);

                if(target == null){

                    sendToWebView("⚠️ Command not found: "+scriptName);
                    return;

                }

                ProcessBuilder pb;

                if(isElfBinary(target)){

                    pb = new ProcessBuilder(
                            target.getAbsolutePath()
                    );

                }else{

                    pb = new ProcessBuilder(
                            "sh",
                            target.getAbsolutePath()
                    );

                }

                pb.redirectErrorStream(true);

                pb.directory(getUserHome());

                pb.environment().put(
                        "PATH",
                        getUserBinDir().getAbsolutePath()
                                + ":" +
                                getUserCustomDir().getAbsolutePath()
                                + ":" +
                                getSystemBinDir().getAbsolutePath()
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

                sendToWebView(output.toString());

            }catch(Exception e){

                sendToWebView("❌ Run Error: "+e.getMessage());

            }

        }).start();

    }

    // =========================
    // RUN COMMAND
    // =========================

    @JavascriptInterface
    public void runCommand(String commandLine){

        new Thread(() -> {

            try{

                if(commandLine.contains("system/bin")){

                    sendToWebView("❌ Permission denied");
                    return;

                }

                if(commandLine.contains("..")){

                    sendToWebView("❌ Access denied");
                    return;

                }

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
                                getUserCustomDir().getAbsolutePath()
                                + ":" +
                                getSystemBinDir().getAbsolutePath()
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

                sendToWebView(output.toString());

            }catch(Exception e){

                sendToWebView("❌ Command Error: "+e.getMessage());

            }

        }).start();

    }

    // =========================
    // SEND OUTPUT
    // =========================

    private void sendToWebView(String message){

        String js = "printOutput("+ JSONObject.quote(message) +");";

        webView.post(() -> webView.evaluateJavascript(js,null));

    }

}
