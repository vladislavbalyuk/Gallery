package com.status.gallery;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Comparator;

public class PagePresenter_ {
    private WeakReference<PageView_> view;
    private MediaFile model;

    public void setModel(MediaFile model) {
        this.model = model;
    }

    public void bindView(PageView_ view) {
        this.view = new WeakReference<>(view);
    }

    public void unbindView() {
        this.view = null;
    }

    protected PageView_ view() {
        if (view == null) {
            return null;
        } else {
            return view.get();
        }
    }
///////////////////////////////////////////////

    public void setImageView() {
        if (model.isImage()) {
            view().setImageViewFromImageFile(model.getFile());
        } else if (model.isVideo()) {
            view().setImageViewFromVideoFile(model.getFile());
        }
    }

    public File getFile(){
        return model.getFile();
    }

    public MediaFile getMediaFile(){
        return model;
    }
    public void delete(){
        File fileThumb = new File(Model.directoryThumbs, model.getThumb() + ".jpg");
        if(fileThumb.exists()){
            fileThumb.delete();
        }
        model.getFile().delete();
        Model.curAlbum.fileList.remove(model);
        Model.curAlbum.createDateList();
        Model.dataBase.delete(model.getFile().getAbsolutePath());

    }

    public void copyFileInAlbum(Album album, Boolean deleteSource){
        File destination = new File(album.getFile(), model.getFile().getName());
        File source = model.getFile();
        try {
            FileUtils.copyFile(source, destination);
        } catch (IOException e) {
        }
        ;

        try {
            MediaFile mediafile = model.clone();
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
            delete();
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
