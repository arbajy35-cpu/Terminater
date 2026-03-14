package com.nexora.terminater.core;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.nexora.terminater.fs.FileSystemManager;
import com.nexora.terminater.runtime.ScriptRunner;
import com.nexora.terminater.runtime.CommandRunner;
import com.nexora.terminater.pkg.PackageInstaller;
import com.nexora.terminater.storage.ScriptStorage;

public class NativeManager {

    private final ScriptRunner scriptRunner;
    private final CommandRunner commandRunner;
    private final PackageInstaller packageInstaller;
    private final ScriptStorage scriptStorage;

    public NativeManager(Context context, WebView webView){

        FileSystemManager fs = new FileSystemManager(context);

        // ✅ FIXED CONSTRUCTORS
        scriptRunner = new ScriptRunner(fs, webView, context);
        commandRunner = new CommandRunner(fs, webView, context);

        packageInstaller = new PackageInstaller(fs, webView);
        scriptStorage = new ScriptStorage(fs, webView);
    }

    // ================================
    // Run Script
    // ================================

    @JavascriptInterface
    public void runScript(String name){

        if(name == null || name.trim().isEmpty()){
            return;
        }

        scriptRunner.run(name.trim());
    }

    // ================================
    // Run Command
    // ================================

    @JavascriptInterface
    public void runCommand(String cmd){

        if(cmd == null || cmd.trim().isEmpty()){
            return;
        }

        commandRunner.run(cmd.trim());
    }

    // ================================
    // Install System Package
    // ================================

    @JavascriptInterface
    public void installSystemPackage(String name, String script){

        if(name == null || script == null){
            return;
        }

        if(name.contains("/") || name.contains("..")){
            return;
        }

        packageInstaller.install(name.trim(), script);
    }

    // ================================
    // Save Custom Script
    // ================================

    @JavascriptInterface
    public void saveCustomScript(String name, String content){

        if(name == null || content == null){
            return;
        }

        if(name.contains("/") || name.contains("..")){
            return;
        }

        scriptStorage.save(name.trim(), content);
    }
}
