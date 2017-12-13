package com.status.gallery;

import android.content.Context;

import java.io.File;

public interface PageView_ {
    void setImageViewFromImageFile(File file);

    void setImageViewFromVideoFile(File file);

    void setPagingEnabled(boolean enabled);
}
