package com.status.gallery.album;

import com.status.gallery.main.MediaFile;
import com.status.gallery.Model;
import com.status.gallery.R;

import java.io.File;
import java.lang.ref.WeakReference;

public class AlbumPresenter {
    private WeakReference<AlbumView> view;
    private Album model;

    public void setModel(Album model){
        this.model = model;
    }

    public void bindView(AlbumView view) {
        this.view = new WeakReference<>(view);
    }

    public void unbindView() {
        this.view = null;
    }

    protected AlbumView view() {
        if (view == null) {
            return null;
        } else {
            return view.get();
        }
    }
///////////////////////////////////////////////

    public void setImageView(){
        if(model.getFile() != null){
            MediaFile mediafile = model.fileList.get(0);
            File file = mediafile.getFile();

            boolean isEmpty = false;

            String thumb = Model.dataBase.getThumb(mediafile.getFile().getAbsolutePath());
            if (thumb != null) {
                File fileThumb = new File(thumb);
                if (fileThumb.exists()) {
                    view().setImageViewFromThumb(fileThumb);
                }
                else {
                    isEmpty = true;
                }
            } else {
                isEmpty = true;
            }
            if (isEmpty) {
                if (mediafile.isImage()) {
                    view().setImageViewFromImageFile(file);
                } else if (mediafile.isVideo()) {
                    view().setImageViewFromVideoFile(file);            }
            }
        }

    }

    public String getName()
    {
        String newAlbum = view().getContext().getResources().getString(R.string.newAlbum);
        return model.getFile() == null?newAlbum:model.getFile().getName();
    }

    public String getCount()
    {
        return String.valueOf(model.getFile() == null?"":model.getCount() + " >");
    }

    public int getSizeThumb(){
        return Model.sizeThumb;
    }

    public void onAlbumClicked() {
        if(view().getSettable() != null){
            if(model.getFile() != null) {
                view().getSettable().setValue(model);
            }
            else{
                view().openStringDialog();
            }
        }
        else {
            Model.curAlbum = model;
            view().openAlbum(model);
        }
    }

    public boolean IsEmptyAlbum(){
        return (model.getFile() == null);
    }

    public void createNewAlbum(String name){
        if(!name.isEmpty()) {
            File newFile = new File(Model.curAlbum.getFile(), name);
            if(!newFile.exists()) {
                newFile.mkdir();
            }
            model = new Album(newFile);
            Model.listAlbums.add(model);

            view().getSettable().setValue(model);
        }
        else{
            view().getSettable().setValue(null);
        }

    }

}
