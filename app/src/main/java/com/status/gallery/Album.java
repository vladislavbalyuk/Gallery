package com.status.gallery;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Album {
    private File file;
    public List<MediaFile> fileList;
    public List<Date> dateList;

    public Album(File file){
        this.file = file;
        this.fileList = new ArrayList<MediaFile>();
        this.dateList = new ArrayList<Date>();
    }

    public File getFile() {
        return file;
    }

    public int getCount(){
        return file == null?100000:fileList.size();
    }

    public void createDateList(){
        dateList.clear();
        Set<Date> dateSet = new TreeSet<Date>(Collections.reverseOrder());
        for(MediaFile mediafile: fileList){
            dateSet.add(mediafile.getDateWithoutTime());
        }
        dateList.addAll(dateSet);
    }
}
