package com.status.gallery;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.status.gallery.main.MediaFile;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class DataBase {

    private static DataBase instance;

    private HashMap<String, MediaFile> mediafiles;

    public DBHelper dbh;
    public SQLiteDatabase db;

    private DataBase(Context context) {
        dbh = new DBHelper(context);
        try {
            db = dbh.getWritableDatabase();
        }
        catch (SQLiteCantOpenDatabaseException e){}
        setMediaFiles();
    }

    public static synchronized DataBase getInstance(Context context) {
        if (instance == null) {
            instance = new DataBase(context);
        }
        return instance;
    }

    private void setMediaFiles() {
        MediaFile mediafile;
        Date created_at, filmed_at;
        mediafiles = new HashMap<String, MediaFile>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Cursor c = db.rawQuery("select * from files", null);
        if (c.moveToFirst()) {
            do {
                int colIndexPath = c.getColumnIndex("path");
                int colIndexThumb = c.getColumnIndex("thumb");
                int colIndexDate = c.getColumnIndex("created_at");
                int colIndexDateTime = c.getColumnIndex("filmed_at");
                created_at = null;
                String textCreated_at = c.getString(colIndexDate);
                filmed_at = null;
                String textFilmed_at = c.getString(colIndexDateTime);
                if(textCreated_at != null){
                    try {
                        created_at = sdf.parse(textCreated_at);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    ;
                }
                if(textFilmed_at != null){
                    try {
                        filmed_at = sdf.parse(textFilmed_at);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    ;
                }mediafile = new MediaFile(new File(c.getString(colIndexPath)));
                mediafile.setDate(created_at);
                mediafile.setDateTime(filmed_at);
                mediafile.setThumb(c.getString(colIndexThumb));

                mediafiles.put(c.getString(colIndexPath), mediafile);
            }
            while (c.moveToNext());
        }

    }

    public HashMap<String, MediaFile> getMediaFiles() {
        return mediafiles;
    }

    public void delete(String path) {
        db.delete("files", "path = ?", new String[]{path});
    }

    public void update(String path, Date date, Date datetime, String thumb) {
        ContentValues cv = new ContentValues();
        cv.put("path", path);

        if (date != null) {
            String dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(date);
            cv.put("created_at", dateStr);
        }
        if (datetime != null) {
            String dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(datetime);
            cv.put("filmed_at", dateStr);
        }
        if (thumb != null) {
            cv.put("thumb", thumb);
        }

        if (db.update("files", cv, "path = ?", new String[]{path}) == 0) {
            insert(path, date, datetime, thumb);
        }
    }

    public void insert(String path, Date date, Date datetime, String thumb) {
        ContentValues cv = new ContentValues();
        cv.put("path", path);

        if (date != null) {
            String dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(date);
            cv.put("created_at", dateStr);
        }
        if (datetime != null) {
            String dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(datetime);
            cv.put("filmed_at", dateStr);
        }
        if (thumb != null) {
            cv.put("thumb", thumb);
        }

        db.insert("files", null, cv);
    }

    public Date getDate(String path) {
        MediaFile mediafile = mediafiles.get(path);
        if (mediafile == null) {
            return null;
        } else {
            return mediafiles.get(path).getDate();
        }
    }

    public Date getDateTime(String path) {
        MediaFile mediafile = mediafiles.get(path);
        if (mediafile == null) {
            return null;
        } else {
            return mediafiles.get(path).getDateTime();
        }
    }

    public String getThumb(String path) {
        MediaFile mediafile = mediafiles.get(path);
        if (mediafile == null) {
            return null;
        } else {
            return mediafiles.get(path).getThumb();
        }
    }

    public void setDate(String path, Date date) {
        MediaFile mediafile = mediafiles.get(path);
        if (mediafile == null) {
            mediafile = new MediaFile(new File(path), date, null);
        } else {
            mediafile.setDate(date);
        }

        mediafiles.put(path, mediafile);
    }

    public void setThumb(String path, String thumb) {
        MediaFile mediafile = mediafiles.get(path);
        if (mediafile == null) {
            mediafile = new MediaFile(new File(path), null, thumb);
        } else {
            mediafile.setThumb(thumb);
        }

        mediafiles.put(path, mediafile);
    }


    class DBHelper extends SQLiteOpenHelper {
        Context context;

        public DBHelper(Context context) {
            super(context, "myGallery", null, 1);
            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table files ("
                    + "path text primary key,"
                    + "thumb text,"
                    + "created_at datetime,"
                    + "filmed_at datetime" + ");");

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}
