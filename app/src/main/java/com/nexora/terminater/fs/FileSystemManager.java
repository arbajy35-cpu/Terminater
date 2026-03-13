package com.nexora.terminater.fs;

import android.content.Context;

import java.io.File;

public class FileSystemManager {

    private Context context;

    public FileSystemManager(Context context){
        this.context = context;
    }

    // =========================
    // ROOT DIRECTORY
    // =========================
    public File getRoot(){

        File root = new File(context.getFilesDir(), ".terminater/home");

        if(!root.exists()){
            root.mkdirs();
        }

        return root;
    }

    // =========================
    // USER HOME
    // =========================
    public File getUserHome(){

        File user = new File(getRoot(), "user");

        if(!user.exists()){
            user.mkdirs();
        }

        return user;
    }

    // =========================
    // USER BIN
    // =========================
    public File getUserBinDir(){

        File bin = new File(getUserHome(), "bin");

        if(!bin.exists()){
            bin.mkdirs();
        }

        return bin;
    }

    // =========================
    // USER CUSTOM SCRIPTS
    // =========================
    public File getUserCustomDir(){

        File custom = new File(getUserHome(), "custom");

        if(!custom.exists()){
            custom.mkdirs();
        }

        return custom;
    }

    // =========================
    // SYSTEM BIN (OFFICIAL COMMANDS)
    // =========================
    public File getSystemBinDir(){

        File systemBin = new File(getRoot(), "system/bin");

        if(!systemBin.exists()){
            systemBin.mkdirs();
        }

        return systemBin;
    }

    // =========================
    // INITIALIZE FILESYSTEM
    // =========================
    public void init(){

        // create directories
        getUserHome();
        getUserBinDir();
        getUserCustomDir();
        getSystemBinDir();

        // protect system folder
        File system = new File(getRoot(), "system");

        if(!system.exists()){
            system.mkdirs();
        }

        system.setReadable(true,false);
        system.setWritable(false,false);
        system.setExecutable(true,false);
    }

}
