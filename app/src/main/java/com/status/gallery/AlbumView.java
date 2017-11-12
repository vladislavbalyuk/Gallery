package com.status.gallery;

import android.content.Context;

import java.io.File;

public interface AlbumView {
    void openAlbum(Album album);
    void setImageViewFromThumb(File file);
    void setImageViewFromImageFile(File file);
    void setImageViewFromVideoFile(File file);
    void openStringDialog();
    Settable getSettable();
    Context getContext();
}
