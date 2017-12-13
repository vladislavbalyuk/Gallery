package com.status.gallery;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.concurrent.ThreadPoolExecutor;

public class PageViewHolder extends RecyclerView.ViewHolder implements PageView_, Settable, View.OnTouchListener {
    private PagePresenter_ presenter;

    private PageFragment_ fragment;
    private ImageView imageView;
    private VideoView videoView;
    private ImageButton btnDelete, btnCopy, btnDetail;
    private ImageButton btnPlay, btnPause;
    private ViewGroup rl, ll, mediaController;
    private TextView textTime, textDuration;
    private SeekBar seekBar;

    private double duration;
    private VideoProgressTask videoProgress;
    private HideMediaControllerTask hideMediaController;
    private int countHideController;

    private Picasso picasso;

    PageViewHolder(View view, int viewType, PageFragment_ fragment) {

        super(view);
        if (viewType == 1) {
            imageView = (ImageView) view.findViewById(R.id.imageView);
        } else {
            videoView = (VideoView) view.findViewById(R.id.videoView);

            rl = (ViewGroup) view.findViewById(R.id.rl);
            rl.setOnTouchListener(this);
            videoView.setOnTouchListener(this);

            mediaController = (ViewGroup) view.findViewById(R.id.mediaController);
            ll = (ViewGroup) view.findViewById(R.id.ll);

            textTime = (TextView) view.findViewById(R.id.textTime);
            textDuration = (TextView) view.findViewById(R.id.textDuration);

            btnPlay = (ImageButton) view.findViewById(R.id.btnPlay);
            btnPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startVideo();
                }
            });

            btnPause = (ImageButton) view.findViewById(R.id.btnPause);
            btnPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pauseVideo();
                }
            });

            seekBar = (SeekBar) view.findViewById(R.id.seekBar);
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    setVideoProgress(progress, fromUser);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }

        try {
            Picasso.setSingletonInstance(picasso);
        } catch (Exception e) {
        }
        ;

        this.fragment = fragment;

        btnDelete = (ImageButton) view.findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogDelete();
            }
        });

        btnCopy = (ImageButton) view.findViewById(R.id.btnCopy);
        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogCopy();

            }
        });

        btnDetail = (ImageButton) view.findViewById(R.id.btnDetail);
        btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogDetail();

            }
        });
    }

    public void bindPresenter(PagePresenter_ presenter) {
        this.presenter = presenter;
        presenter.bindView(this);

    }

    public void unbindPresenter() {
        presenter = null;
    }

    public void setViews() {
        presenter.setImageView();
    }

    public void setImageViewFromImageFile(File file) {
        int width, height;

        imageView.setOnTouchListener(PageFragment_.zoomImage);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) imageView.getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int widthScreen = displayMetrics.widthPixels;
        int heightScreen = displayMetrics.heightPixels;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        int widthImage = options.outWidth;
        int heightImage = options.outHeight;

        if ((float) widthImage / widthScreen > (float) heightImage / heightScreen) {
            width = widthScreen;
            height = (int) (heightImage * widthScreen / widthImage);
        } else {
            height = heightScreen;
            width = (int) (widthImage * heightScreen / heightImage);
        }

        Picasso.with(((Activity) imageView.getContext()))
                .load(file)
                .resize(width, height)
                .into(imageView);
    }

    public void setImageViewFromVideoFile(File file) {
        Uri myVideoUri = Uri.parse(file.getAbsolutePath());
        videoView.setVideoURI(myVideoUri);
        int currentPosition = fragment.getCurrentPosition();
        videoView.seekTo(currentPosition);
        if(currentPosition != 100 && !fragment.isVideoPaused()){
            videoView.start();
        }
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int actionMask = (event.getAction() & MotionEvent.ACTION_MASK);
        if (actionMask == MotionEvent.ACTION_DOWN) {
            if(fragment.isPlayingMode()){
                mediaController.setVisibility(mediaController.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
                if (mediaController.getVisibility() == View.VISIBLE) {
                    ThreadPoolExecutor tpe = (ThreadPoolExecutor) AsyncTask.THREAD_POOL_EXECUTOR;
                    tpe.setCorePoolSize(10);
                    countHideController++;
                    hideMediaController = new HideMediaControllerTask(countHideController);
                    hideMediaController.executeOnExecutor(tpe);

                }
            } else {
                ll.setVisibility(ll.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
            }
        }
        return true;
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof Boolean) {
            if ((Boolean) value == true) {
                presenter.delete();
            }
        }
        if (value instanceof Pair) {
            presenter.copyFileInAlbum((Album) ((Pair) value).first, (Boolean) ((Pair) value).second);
        }
        fragment.notifyRecyclerView();

    }

    public void setPagingEnabled(boolean enabled) {
        fragment.setPagingEnabled(enabled);
    }

    private void showDialogDelete() {
        DeleteDialog dlg = new DeleteDialog();
        dlg.setSettable(this);
        dlg.setFileName(presenter.getFile().getName());
        dlg.show(fragment.getFragmentManager(), "dlg");
    }

    private void showDialogCopy() {
        CopyDialog dlg = new CopyDialog();
        dlg.setSettable(this);
        dlg.show(fragment.getFragmentManager(), "dlg");
    }

    private void showDialogDetail() {
        DetailDialog dlg = new DetailDialog();
        dlg.setMediaFile(presenter.getMediaFile());
        dlg.show(fragment.getFragmentManager(), "dlg");
    }

    private void startVideo() {
        ll.setVisibility(View.INVISIBLE);
        btnPlay.setVisibility(View.INVISIBLE);

        videoView.start();
        duration = videoView.getDuration();
        textDuration.setText(presenter.convertTime((int) duration));
        mediaController.setVisibility(View.VISIBLE);
        fragment.setPageViewHolder(this);

        setPagingEnabled(false);

        ThreadPoolExecutor tpe = (ThreadPoolExecutor) AsyncTask.THREAD_POOL_EXECUTOR;
        tpe.setCorePoolSize(10);
        videoProgress = new VideoProgressTask();
        videoProgress.executeOnExecutor(tpe);
        countHideController = 1;
        hideMediaController = new HideMediaControllerTask(countHideController);
        hideMediaController.executeOnExecutor(tpe);


        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                fragment.setPageViewHolder(null);
                stopVideo();
            }
        });
    }

    private void pauseVideo() {
        if (videoView.isPlaying()) {
            videoView.pause();
            btnPause.setImageResource(R.drawable.play);
            fragment.setVideoPaused(true);
        } else {
            videoView.start();
            btnPause.setImageResource(R.drawable.pause);
            fragment.setVideoPaused(false);
        }
    }

    public void stopVideo() {
        videoView.stopPlayback();
        videoView.resume();
        seekBar.setProgress(0);
        setPagingEnabled(true);
        btnPlay.setVisibility(View.VISIBLE);

        btnPause.setImageResource(R.drawable.pause);
        mediaController.setVisibility(View.INVISIBLE);
    }

    private void setVideoProgress(int progress, boolean fromUser) {
        if (fromUser) {
            videoView.seekTo((int) (duration * progress / 100));
        }
    }

    public int getCurrentPositionVideo(){
        return videoView.getCurrentPosition();
    }

    private class VideoProgressTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
           while (fragment != null && fragment.isPlayingMode()) {
                publishProgress(null);
                SystemClock.sleep(50);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... items) {
            double time = videoView.getCurrentPosition();
            seekBar.setProgress((int) (time / duration * 100));
            textTime.setText(presenter.convertTime((int) time));
        }
    }

    private class HideMediaControllerTask extends AsyncTask<Void, Void, Void> {
        int count;

        public HideMediaControllerTask(int count) {
            this.count = count;
        }

        @Override
        protected Void doInBackground(Void... params) {
            SystemClock.sleep(4000);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (count == countHideController) {
                mediaController.setVisibility(View.INVISIBLE);
            }
        }
    }


}
