package com.nexora.terminater.runtime;

import android.webkit.WebView;

import com.nexora.terminater.fs.FileSystemManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class CommandRunner {

    private FileSystemManager fs;
    private WebView webView;

    public CommandRunner(FileSystemManager fs, WebView webView){
        this.fs = fs;
        this.webView = webView;
    }

    // =========================
    // RESOLVE COMMAND PATH
    // =========================
    public String resolve(String command){

        File user = new File(fs.getUserBinDir(), command);
        File custom = new File(fs.getUserCustomDir(), command);
        File system = new File(fs.getSystemBinDir(), command);

        if(user.exists()) return user.getAbsolutePath();

        if(custom.exists()) return custom.getAbsolutePath();

        if(system.exists()) return system.getAbsolutePath();

        return null;
    }

    // =========================
    // ELF DETECTOR
    // =========================
    private boolean isElf(File file){

        try{

            FileInputStream fis = new FileInputStream(file);

            byte[] header = new byte[4];

            fis.read(header);

            fis.close();

            return header[0] == 0x7F &&
                    header[1] == 'E' &&
                    header[2] == 'L' &&
                    header[3] == 'F';

        }
        catch(Exception e){
            return false;
        }

    }

    // =========================
    // RUN TERMINAL COMMAND
    // =========================
    public void run(String commandLine){

        new Thread(() -> {

            try{

                if(commandLine == null || commandLine.trim().isEmpty()){
                    send("");
                    return;
                }

                // security sandbox
                if(commandLine.contains("..") ||
                        commandLine.contains(".terminater")){
                    send("❌ Access denied");
                    return;
                }

                // split command
                String[] parts = commandLine.trim().split(" ");
                String command = parts[0];

                // =====================
                // BUILTIN COMMANDS
                // =====================

                Command builtin = new Command(fs);

                if(command.equals("ls")){
                    send(builtin.ls());
                    return;
                }

                if(command.equals("pwd")){
                    send(builtin.pwd());
                    return;
                }

                if(command.equals("cd")){

                    if(parts.length < 2){
                        send("");
                        return;
                    }

                    send(builtin.cd(parts[1]));
                    return;
                }

                // =====================
                // EXTERNAL COMMAND
                // =====================

                String resolvedPath = resolve(command);

                if(resolvedPath == null){
                    send("command not found: " + command);
                    return;
                }

                File cmdFile = new File(resolvedPath);

                ProcessBuilder pb;

                // ELF BINARY
                if(isElf(cmdFile)){

                    String[] exec = new String[parts.length];
                    exec[0] = resolvedPath;

                    for(int i=1;i<parts.length;i++){
                        exec[i] = parts[i];
                    }

                    pb = new ProcessBuilder(exec);

                }
                // SCRIPT
                else{

                    String[] exec = new String[parts.length + 1];
                    exec[0] = "sh";
                    exec[1] = resolvedPath;

                    for(int i=1;i<parts.length;i++){
                        exec[i+1] = parts[i];
                    }

                    pb = new ProcessBuilder(exec);

                }

                pb.directory(fs.getUserHome());

                pb.redirectErrorStream(true);

                pb.environment().put(
                        "PATH",
                        fs.getUserBinDir().getAbsolutePath()
                                + ":" +
                                fs.getUserCustomDir().getAbsolutePath()
                                + ":" +
                                fs.getSystemBinDir().getAbsolutePath()
                );

                Process process = pb.start();

                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(
                                        process.getInputStream()
                                )
                        );

                StringBuilder output = new StringBuilder();
                String line;

                while((line = reader.readLine()) != null){
                    output.append(line).append("\n");
                }

                process.waitFor();

                send(output.toString());

            }
            catch(Exception e){
                send("❌ Command Error: " + e.getMessage());
            }

        }).start();

    }

    // =========================
    // SEND OUTPUT TO WEBVIEW
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
