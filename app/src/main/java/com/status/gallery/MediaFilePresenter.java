package com.status.gallery;

import java.io.File;
import java.lang.ref.WeakReference;

public class MediaFilePresenter {
    private WeakReference<MediaFileView> view;
    private MediaFile model;

    public void setModel(MediaFile model) {
        this.model = model;
    }

    public void bindView(MediaFileView view) {
        this.view = new WeakReference<>(view);
    }

    public void unbindView() {
        this.view = null;
    }

    protected MediaFileView view() {
        if (view == null) {
            return null;
        } else {
            return view.get();
        }
    }
///////////////////////////////////////////////

    public void setImageView() {
        boolean isEmpty = false;

        String thumb = Model.dataBase.getThumb(model.getFile().getAbsolutePath());

        if (thumb != null) {
            File fileThumb = new File(thumb);
            if (fileThumb.exists()) {
                int pos = Model.curAlbum.fileList.indexOf(model);
                view().setImageViewFromThumb(fileThumb, pos);
            } else {
                isEmpty = true;
            }
        } else {
            isEmpty = true;
        }

        if (isEmpty) {
            if (model.isImage()) {
                int pos = Model.curAlbum.fileList.indexOf(model);
                view().setImageViewFromImageFile(model.getFile(), pos);

                Model.dataBase.update(model.getFile().getAbsolutePath(), null, null, Model.directoryThumbs + "/" + model.getFile().getName());
                Model.dataBase.setThumb(model.getFile().getAbsolutePath(), Model.directoryThumbs + "/" + model.getFile().getName());
            } else {
                int pos = Model.curAlbum.fileList.indexOf(model);
                view().setImageViewFromVideoFile(model.getFile(), pos);

                String fileName = model.getFile().getName();
                int i = fileName.lastIndexOf(".");
                fileName = fileName.substring(0, i) + ".jpg";
                Model.dataBase.update(model.getFile().getAbsolutePath(), null, null, Model.directoryThumbs + "/" + fileName);

            }
        }

    }

    public File getNewFile(){
        String fileName = model.getFile().getName();
        int i = fileName.lastIndexOf(".");
        fileName = fileName.substring(0, i) + ".jpg";
        File file = new File(Model.directoryThumbs, fileName);
        return file;
    }

    int getSizeThumb(){
        return Model.sizeThumb;
    }

}
