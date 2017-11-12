package com.status.gallery;

import android.view.View;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;

public class PagePresenter {

    private static ZoomImage zoomImage = new ZoomImage();

    PageView view;

    public PagePresenter(PageView view){
        this.view = view;
    }

    public boolean isImage(int pageNumber){
        return Model.curAlbum.fileList.get(pageNumber).isImage();
    }

    public boolean isVideo(int pageNumber){
        return Model.curAlbum.fileList.get(pageNumber).isVideo();
    }

    public File getFile(int pageNumber){
        return Model.curAlbum.fileList.get(pageNumber).getFile();
    }

    public MediaFile getMediaFile(int pageNumber){
        return Model.curAlbum.fileList.get(pageNumber);
    }

    void initZoom(MediaFile mediafile){
        zoomImage.init(view, mediafile);
    }

    View.OnTouchListener getListener(){
        return zoomImage;
    }

    public void delete(int pageNumber){
        File fileThumb = new File(Model.directoryThumbs, Model.curAlbum.fileList.get(pageNumber).getThumb() + ".jpg");
        if(fileThumb.exists()){
            fileThumb.delete();
        }
        Model.curAlbum.fileList.get(pageNumber).getFile().delete();
        Model.curAlbum.fileList.remove(pageNumber);
        Model.curAlbum.createDateList();
        Model.dataBase.delete(Model.curAlbum.fileList.get(pageNumber).getFile().getAbsolutePath());

    }

    public void copyFileInAlbum(Album album, Boolean deleteSource, int pageNumber){
        File destination = new File(album.getFile(), Model.curAlbum.fileList.get(pageNumber).getFile().getName());
        File source = Model.curAlbum.fileList.get(pageNumber).getFile();
        try {
            FileUtils.copyFile(source, destination);
        } catch (IOException e) {
        }
        ;

        try {
            MediaFile mediafile = Model.curAlbum.fileList.get(pageNumber).clone();
            mediafile.setFile(destination);
            album.fileList.add(mediafile);
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
            album.createDateList();

        } catch (CloneNotSupportedException e) {
        }
        ;

        if(deleteSource){
            delete(pageNumber);
        }
    }

    public String convertTime(int milliseconds){
        int hour = milliseconds/(60*60*1000);
        int min = (milliseconds - hour*(60*60*1000))/(60*1000);
        int sec = (milliseconds - hour*(60*60*1000) - min*(60*1000))/1000;

        return (hour == 0?"":String.valueOf(hour) + ":")
                + (hour > 0 && min < 10?"0" + min:min)
                + ":" + (sec < 10?"0" + sec:sec);
    }

}
