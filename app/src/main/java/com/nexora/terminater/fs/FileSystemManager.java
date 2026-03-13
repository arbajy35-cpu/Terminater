package com.nexora.terminater.fs;

import android.content.Context;
import java.io.File;

public class FileSystemManager {

    private Context context;

    public FileSystemManager(Context context){
        this.context = context;
        initDirs();
    }

    private void initDirs(){

        getBaseDir();
        getUserHome();
        getUserBinDir();
        getUserCustomDir();
        getSystemBinDir();

    }

    public File getBaseDir(){

        File dir = new File(context.getFilesDir(), ".terminater/home");

        if(!dir.exists()) dir.mkdirs();

        return dir;
    }

    public File getUserHome(){

        File dir = new File(getBaseDir(),"user");

        if(!dir.exists()) dir.mkdirs();

        return dir;
    }

    public File getUserBinDir(){

        File dir = new File(getUserHome(),"bin");

        if(!dir.exists()) dir.mkdirs();

        return dir;
    }

    public File getUserCustomDir(){

        File dir = new File(getUserHome(),"custom");

        if(!dir.exists()) dir.mkdirs();

        return dir;
    }

    public File getSystemBinDir(){

        File dir = new File(getBaseDir(),"system/bin");

        if(!dir.exists()) dir.mkdirs();

        return dir;
    }

}
