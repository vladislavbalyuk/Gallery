package com.status.gallery.main;

import java.io.File;

public interface MediaFileView {
    void setImageViewFromThumb(File file, int pos);

    void setImageViewFromImageFile(File file, int pos);

    void setImageViewFromVideoFile(File file, int pos);
}
