package com.nexora.terminater.runtime;

import java.io.File;
import java.io.FileInputStream;

public class ElfDetector {

    public static boolean isElfBinary(File file){

        try{

            FileInputStream fis = new FileInputStream(file);

            byte[] header = new byte[4];

            fis.read(header);

            fis.close();

            return header[0] == 0x7F &&
                    header[1] == 'E' &&
                    header[2] == 'L' &&
                    header[3] == 'F';

        }catch(Exception e){

            return false;

        }

    }

}
