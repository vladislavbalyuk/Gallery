package com.status.gallery.splash;

import android.content.Context;

import java.io.File;
import java.util.HashMap;

public interface SplashView {

    HashMap<String, Integer> getScreenResolution();

    File getAplicationDirectory(String name);

    File getExternalStorageDirectory();

    String getDateFromVideo(File file);

    String getDateFromImage(File file);

    void startReadDyrectory(File dir);

    Context getContext();

}
