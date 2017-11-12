package com.status.gallery;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DatePresenter {
    private WeakReference<DateView> view;
    private Date model;

    public void setModel(Date model){
        this.model = model;
    }

    public void bindView(DateView view) {
        this.view = new WeakReference<>(view);
    }

    public void unbindView() {
        this.view = null;
    }

    protected DateView view() {
        if (view == null) {
            return null;
        } else {
            return view.get();
        }
    }
////////////////////////////////////////////////////////////////

    public String getTextDate(){
        SimpleDateFormat sdfl = new SimpleDateFormat("  dd MMMM yyyy");
        SimpleDateFormat sdfs1 = new SimpleDateFormat("  dd MMMM");
        SimpleDateFormat sdfs2 = new SimpleDateFormat("  d MMMM");

        if(getYear(model) != getYear(new Date())) {
            return sdfl.format(model);
        }
        else if(getDayOfMonth(model) >= 10){
            return sdfs1.format(model);
        }
        else {
            return sdfs2.format(model);
        }
    }

    private int getYear(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime (date);
        return calendar.get(Calendar.YEAR);
    }

    private int getDayOfMonth(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime (date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public int getCountColPortrait(){
        return Model.countColPortrait;
    }

    public int getCountColLandscape(){
        return Model.countColLandscape;
    }

    public List<MediaFile> getList(){
        int indexStart, indexEnd,i;

        indexStart = -1;
        indexEnd = -1;
        i = 0;
        for (MediaFile mediafile : Model.curAlbum.fileList) {

            if (indexStart == -1 && (mediafile.getDateWithoutTime()).equals(model)) {
                indexStart = i;

            }
            if (indexStart != -1 && indexEnd == -1 && !(mediafile.getDateWithoutTime()).equals(model)) {
                indexEnd = i;
            }
            i++;
        }
        indexEnd = indexEnd==-1?Model.curAlbum.fileList.size():indexEnd;

        return Model.curAlbum.fileList.subList(indexStart, indexEnd);
    }

}
