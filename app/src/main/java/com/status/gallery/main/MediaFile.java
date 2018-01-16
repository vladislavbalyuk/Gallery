package com.status.gallery.main;

import java.io.File;
import java.util.Date;

public class MediaFile implements Cloneable{

    private File file;
    private Date date;
    private Date datetime;
    private String thumb;
    private boolean isImage, isVideo;

    public MediaFile(File file){
        this.file = file;
        this.isImage = isImage(file.getAbsolutePath());
        this.isVideo = isVideo(file.getAbsolutePath());
    }

    public MediaFile(File file, Date date, String thumb){
        this.file = file;
        this.date = date;
        this.thumb = thumb;
        this.isImage = isImage(file.getAbsolutePath());
        this.isVideo = isVideo(file.getAbsolutePath());
    }

    public File getFile(){
        return file;
    }

    public void setFile(File file){

        this.file = file;
    }

    public Date getDateTime(){
        return datetime;
    }

    public void setDateTime(Date datetime){
        this.datetime = datetime;
    }

    public Date getDate(){
        return date;
    }

    public void setDate(Date date){
        this.date = date;
    }

    public String getThumb(){
        return thumb;
    }

    public void setThumb(String thumb){
        this.thumb = thumb;
    }


    public boolean isImage() {
        return isImage;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public Date getDateWithoutTime() {
        long millisInDay = 60 * 60 * 24 * 1000;
        long currentTime = date.getTime();
        long dateOnly = (currentTime / millisInDay) * millisInDay;
        return new Date(dateOnly);
    }



    public static String getFileExtension(String mystr) {
        int index = mystr.indexOf('.');
        return index == -1? "" : mystr.substring(index + 1);
    }

    public static boolean isImage(String mystr) {
        String ex = getFileExtension(mystr).toLowerCase();

        if(ex.equals("jpg") || ex.equals("jpeg")) {
            return true;
        }
        return false;
    }

    public static boolean isVideo(String mystr) {
        String ex = getFileExtension(mystr).toLowerCase();

        if(ex.equals("mp4")) {
            return true;
        }
        return false;
    }

    @Override
    public MediaFile clone() throws CloneNotSupportedException{
        MediaFile mediafile = (MediaFile) super.clone();
        mediafile.setFile(new File(file.getAbsolutePath()));
        mediafile.setDate((Date)date.clone());

        return mediafile;
    }


}