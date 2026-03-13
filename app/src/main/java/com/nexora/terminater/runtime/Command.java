package com.nexora.terminater.runtime;

import com.nexora.terminater.fs.FileSystemManager;

import java.io.File;

public class Command {

    private FileSystemManager fs;

    public Command(FileSystemManager fs){
        this.fs = fs;
    }

    // ======================
    // LS
    // ======================
    public String ls(){

        File dir = fs.getUserHome();

        File[] files = dir.listFiles();

        if(files == null) return "";

        StringBuilder out = new StringBuilder();

        for(File f : files){
            out.append(f.getName()).append("\n");
        }

        return out.toString();
    }

    // ======================
    // PWD
    // ======================
    public String pwd(){

        return fs.getUserHome().getAbsolutePath();

    }

    // ======================
    // CD
    // ======================
    public String cd(String path){

        File newDir;

        if(path.equals("~")){
            newDir = fs.getUserHome();
        }
        else{
            newDir = new File(fs.getUserHome(), path);
        }

        if(!newDir.exists() || !newDir.isDirectory()){
            return "cd: no such directory";
        }

        fs.setUserHome(newDir);

        return "";
    }

}
