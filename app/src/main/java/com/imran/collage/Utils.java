package com.imran.collage;

import android.os.Environment;

import java.io.File;
import java.io.IOException;

/**
 * Created by imran on 21/07/14.
 */
public class Utils {

    public static File getTempFile(String fileName) {

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File file = new File(Environment.getExternalStorageDirectory(), fileName);
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return file;
        }
        return null;
    }
}
