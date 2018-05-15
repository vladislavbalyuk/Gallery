package com.status.gallery.splash;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.status.gallery.R;
import com.status.gallery.album.AlbumActivity;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ThreadPoolExecutor;

public class SplashFragment extends Fragment implements SplashView {

    private View view;

    private SplashPresenter presenter;

    private ThreadPoolExecutor tpe;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new SplashPresenter(this);
        setRetainInstance(true);

        tpe = (ThreadPoolExecutor)AsyncTask.THREAD_POOL_EXECUTOR;
        tpe.setMaximumPoolSize(1000);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view != null) {
            if (view.getParent() != null) {
                ((ViewGroup) view.getParent()).removeView(view);
            }
        } else {
            view = inflater.inflate(R.layout.fragment_splash, container, false);

            if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        0);
            }
            else
                startMyAsyncTask();
        }
        return view;
    }

    public void startMyAsyncTask() {
        new MyAsyncTask().execute();
    }

    public HashMap<String, Integer> getScreenResolution() {
        int width, height;
        HashMap resolution = new HashMap<String, Integer>();
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width = metrics.widthPixels;
        height = metrics.heightPixels;
        resolution.put("width", width > height ? height : width);
        resolution.put("height", height > width ? height : width);

        return resolution;
    }

    public File getAplicationDirectory(String name) {
        return getActivity().getDir(name, Context.MODE_PRIVATE);
    }

    public File getExternalStorageDirectory() {
        return new File(Environment.getExternalStorageDirectory().getPath());
    }

    public String getDateFromVideo(File file) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(file.getAbsolutePath());
        String d = mediaMetadataRetriever
                .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE);
        return d;
    }

    public String getDateFromImage(File file) {
        String value = null;
        try {

            ExifInterface exif = null;
            exif = new ExifInterface(file.getAbsolutePath());
            if (exif != null) {
                value = exif.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL);
                if (value == null) {
                    value = exif.getAttribute(ExifInterface.TAG_DATETIME);
                }
            }
        } catch (IOException e) {
        }
        ;
        return value;
    }

    public Context getContext() {
        return getActivity();
    }

    public void startReadDyrectory(File dir){

        new ReadDirectoryTask(dir).executeOnExecutor(tpe);
    }

    private class MyAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            presenter.setSizeThumb();
            presenter.createDirectoryThumbs();
            startReadDyrectory(new File(Environment.getExternalStorageDirectory().getPath()));
            return null;
        }

    }

    private class ReadDirectoryTask extends AsyncTask<Void, Void, Void> {

        private File dir;

        public ReadDirectoryTask(File dir){
            this.dir = dir;
        }

        @Override
        protected Void doInBackground(Void... params) {
            presenter.readFilesInDirectory(dir);
            if(presenter.isReadingFinished()) {
                presenter.readAlbums();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(presenter.isReadingFinished()) {
                Intent intent = new Intent(getActivity(), AlbumActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        }
    }



}
