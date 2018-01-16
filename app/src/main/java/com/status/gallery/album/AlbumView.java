package com.status.gallery.album;

import android.content.Context;

import com.status.gallery.Settable;
import com.status.gallery.album.Album;

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
