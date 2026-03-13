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

    private ScriptRunner scriptRunner;
    private CommandRunner commandRunner;
    private PackageInstaller packageInstaller;
    private ScriptStorage scriptStorage;

    public NativeManager(Context context, WebView webView){

        FileSystemManager fs = new FileSystemManager(context);

        scriptRunner = new ScriptRunner(fs,webView);
        commandRunner = new CommandRunner(fs,webView);

        packageInstaller = new PackageInstaller(fs,webView);
        scriptStorage = new ScriptStorage(fs,webView);

    }

    @JavascriptInterface
    public void runScript(String name){
        scriptRunner.run(name);
    }

    @JavascriptInterface
    public void runCommand(String cmd){
        commandRunner.run(cmd);
    }

    @JavascriptInterface
    public void installSystemPackage(String name,String script){
        packageInstaller.install(name,script);
    }

    @JavascriptInterface
    public void saveCustomScript(String name,String content){
        scriptStorage.save(name,content);
    }

}
