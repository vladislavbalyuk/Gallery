package com.status.gallery;

import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;

public class SplashPresenter {

    SplashView view;

    private int startedTask, finishedFask;

    public SplashPresenter(SplashView view) {
        this.view = view;
        Model.listAlbums = new ArrayList<Album>();
        Model.dataBase = DataBase.getInstance(view.getContext());
        startedTask = 0;
        finishedFask = 0;
    }

    protected void setSizeThumb() {
        Map<String, Integer> resolution = view.getScreenResolution();
        Model.countColPortrait = 4;
        Model.sizeThumb = resolution.get("width") / Model.countColPortrait;
        Model.countColLandscape = resolution.get("height") / Model.sizeThumb;
    }

    protected void createDirectoryThumbs() {
        Model.directoryThumbs = view.getAplicationDirectory("thumb");
        if (!Model.directoryThumbs.exists()) {
            Model.directoryThumbs.mkdir();
        }
    }

    protected void readAlbums() {
        Model.listAlbums.add(new Album(null));
        for (Album item : Model.listAlbums) {
            readFiles(item);
        }
        Collections.sort(Model.listAlbums, new Comparator<Album>() {
            @Override
            public int compare(Album o1, Album o2) {
                if (o1.getCount() > o2.getCount()) {
                    return -1;
                } else if (o1.getCount() < o2.getCount()) {
                    return 1;
                }
                return 0;
            }
        });
    }

    public void readFilesInDirectory(File dir) {
        boolean isAlbum = false;
        increaceStartedTask();
        File[] list = dir.listFiles();

        for (File item : list) {
            if (item.isDirectory()) {
                view.startReadDyrectory(item);
            } else if (!isAlbum) {
                if (MediaFile.isImage(item.getAbsolutePath()) || MediaFile.isVideo(item.getAbsolutePath())) {
                    isAlbum = true;
                }
            }

        }
        if (isAlbum) {
            Model.listAlbums.add(new Album(dir));
        }
        increaceFinishedTask();

    }

    private synchronized void increaceStartedTask(){
        startedTask++;
    }

    private synchronized void increaceFinishedTask(){
        finishedFask++;
    }

    protected void readFiles(Album album) {
        if (album.getFile() != null) {
            MediaFile mediafile;
            File dir = album.getFile();

            File[] list = dir.listFiles();

            Model.dataBase.db.beginTransaction();
            for (File item : list) {
                if (MediaFile.isImage(item.getAbsolutePath()) || MediaFile.isVideo(item.getAbsolutePath())) {
                    mediafile = new MediaFile(item);
                    getExif(mediafile);
                    album.fileList.add(mediafile);
                }
            }
            Model.dataBase.db.setTransactionSuccessful();
            Model.dataBase.db.endTransaction();

            album.createDateList();
            Collections.sort(album.fileList, new Comparator<MediaFile>() {
                @Override
                public int compare(MediaFile file1, MediaFile file2) {
                    if (file1.getDate().before(file2.getDate()))
                        return 1;
                    else if (file1.getDate().after(file2.getDate()))
                        return -1;
                    else
                        return 0;
                }
            });

        }
    }

    protected void getExif(MediaFile mediafile) {

        File file = mediafile.getFile();
        String dt;

        if(mediafile.getFile().getAbsolutePath().contains("105912")){
            int o = 0;
        }

        Date date = Model.dataBase.getDate(file.getAbsolutePath());
        Date datetime = Model.dataBase.getDateTime(file.getAbsolutePath());

        if (date == null || datetime == null) {
            date = new Date(file.lastModified());

            if (MediaFile.isVideo(file.getAbsolutePath())) {
                dt = view.getDateFromVideo(file);
                if (dt != null && !dt.isEmpty() && dt.substring(0, 1).equals("2")) {
                    StringBuilder sb = new StringBuilder(dt.substring(0, 15));
                    sb.insert(4, ':');
                    sb.insert(7, ':');
                    sb.replace(10, 11, " ");
                    sb.insert(13, ':');
                    sb.insert(16, ':');
                    dt = sb.toString();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
                    try {
                        date = formatter.parse(dt);
                        datetime = date;
                    } catch (Exception e) {
                    }
                }
            } else {
                dt = view.getDateFromImage(file);
                if (dt != null) {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
                    try {
                        date = formatter.parse(dt);
                        datetime = date;
                    } catch (Exception e) {
                    }
                }
            }


            Model.dataBase.update(file.getAbsolutePath(), date, datetime, null);
        }

        mediafile.setDate(date);
        mediafile.setDateTime(datetime);
    };

    public boolean isReadingFinished() {

        return (startedTask == finishedFask);
    }

}
