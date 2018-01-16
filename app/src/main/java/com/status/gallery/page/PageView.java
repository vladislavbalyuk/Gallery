package com.status.gallery.page;

import android.content.Context;

import java.io.File;

public interface PageView {
    void setImageViewFromImageFile(File file);

    void setImageViewFromVideoFile(File file);

    void setPagingEnabled(boolean enabled);
}
