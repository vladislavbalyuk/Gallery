package com.status.gallery;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.squareup.picasso.Picasso;

import java.util.concurrent.ThreadPoolExecutor;


public class PageFragment extends Fragment implements PageView, Settable, View.OnTouchListener {

    private View view;

    private PagePresenter presenter;

    private int pageNumber;
    private VideoView videoView;
    private ImageButton btnPause, btnPlay;
    private TextView textTime, textDuration;
    private ViewGroup mediaController;
    private ViewGroup ll;
    private SeekBar seekBar;
    private double duration;
    private VideoProgressTask videoProgress;
    private HideMediaControllerTask hideMediaController;
    private int countHideController;

    public static PageFragment newInstance(int page) {
        PageFragment fragment = new PageFragment();
        Bundle args = new Bundle();
        args.putInt("num", page);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNumber = getArguments() != null ? getArguments().getInt("num") : 1;
        presenter = new PagePresenter(this);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (presenter.isImage(pageNumber)) {
            View result = inflater.inflate(R.layout.fragment_page_image, container, false);
            return result;
        } else {
            View result = inflater.inflate(R.layout.fragment_page_video, container, false);
            return result;
        }
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        int width, height;
        super.onViewCreated(view, savedInstanceState);
        if (presenter.isImage(pageNumber)) {
            ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int widthScreen = displayMetrics.widthPixels;
            int heightScreen = displayMetrics.heightPixels;

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(Model.curAlbum.fileList.get(pageNumber).getFile().getAbsolutePath(), options);
            int widthImage = options.outWidth;
            int heightImage = options.outHeight;

            if ((float) widthImage / widthScreen > (float) heightImage / heightScreen) {
                width = widthScreen;
                height = (int) (heightImage * widthScreen / widthImage);
            } else {
                height = heightScreen;
                width = (int) (widthImage * heightScreen / heightImage);
            }

            Picasso.with(getContext())
                    .load(presenter.getFile(pageNumber))
                    .resize(width, height)
                    .into(imageView);


            if ((getParent()).getPosition() == pageNumber) {
                setUserVisibleHint(true);
            }

            imageView.setOnTouchListener(presenter.getListener());

        } else {
            videoView = (VideoView) view.findViewById(R.id.videoView);
            Uri myVideoUri = Uri.parse(presenter.getFile(pageNumber).getAbsolutePath());
            videoView.setVideoURI(myVideoUri);
            if ((getParent()).getPosition() == pageNumber) {
                (getParent()).setPosition(-1);
                setUserVisibleHint(true);
            }
            videoView.seekTo(100);

            ViewGroup rl = (ViewGroup)view.findViewById(R.id.rl);
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

        ImageButton btnDelete = (ImageButton) view.findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogDelete();
            }
        });

        ImageButton btnCopy = (ImageButton) view.findViewById(R.id.btnCopy);
        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogCopy();

            }
        });

        ImageButton btnDetail = (ImageButton) view.findViewById(R.id.btnDetail);
        btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogDetail();

            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (this.isVisible()) {
            if (!isVisibleToUser)   // If we are becoming invisible, then...
            {
                //pause or stop video
                if (videoView != null) {
                }
            }

            if (isVisibleToUser) {
                if (presenter.isVideo(pageNumber)) {
                } else {
                }
                presenter.initZoom(presenter.getMediaFile(pageNumber));
            }

        }
    }


    public CustomViewPager getViewPager() {
        return getParent().getViewPager();
    }

    public void setPagingEnabled(boolean enabled) {
        getViewPager().setPagingEnabled(enabled);
    }

    public Context getContext() {
        return getActivity();
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof Boolean) {
            if ((Boolean) value == true) {
                presenter.delete(pageNumber);
                getViewPager().getAdapter().notifyDataSetChanged();
            }
        }
        if (value instanceof Pair) {
            presenter.copyFileInAlbum((Album) ((Pair) value).first, (Boolean) ((Pair) value).second, pageNumber);
            getViewPager().getAdapter().notifyDataSetChanged();
        }
    }

    private void showDialogDelete() {
        DeleteDialog dlg = new DeleteDialog();
        dlg.setSettable(this);
        dlg.setFileName(presenter.getFile(pageNumber).getName());
        dlg.show(getFragmentManager(), "dlg");
    }

    private void showDialogCopy() {
        CopyDialog dlg = new CopyDialog();
        dlg.setSettable(this);
        dlg.show(getFragmentManager(), "dlg");
    }

    private void showDialogDetail() {
        DetailDialog dlg = new DetailDialog();
        dlg.setMediaFile(presenter.getMediaFile(pageNumber));
        dlg.show(getFragmentManager(), "dlg");
    }

    private void startVideo() {
        ll.setVisibility(View.INVISIBLE);
        btnPlay.setVisibility(View.INVISIBLE);

        videoView.start();
        duration = videoView.getDuration();
        textDuration.setText(presenter.convertTime((int)duration));
        mediaController.setVisibility(View.VISIBLE);
        getParent().setFragment(this);

        setPagingEnabled(false);

        ThreadPoolExecutor tpe = (ThreadPoolExecutor)AsyncTask.THREAD_POOL_EXECUTOR;
        tpe.setCorePoolSize(10);
        videoProgress = new VideoProgressTask();
        videoProgress.executeOnExecutor(tpe);
        countHideController = 1;
        hideMediaController = new HideMediaControllerTask(countHideController);
        hideMediaController.executeOnExecutor(tpe);


        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                getParent().setFragment(null);
                stopVideo();
            }
        });
    }

    private void pauseVideo() {
        if (videoView.isPlaying()) {
            videoView.pause();
            btnPause.setImageResource(R.drawable.play);
        } else {
            videoView.start();
            btnPause.setImageResource(R.drawable.pause);
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

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int actionMask = (event.getAction() & MotionEvent.ACTION_MASK);
        if (actionMask == MotionEvent.ACTION_DOWN) {
            if(getParent().isPlayingMode()){
                mediaController.setVisibility(mediaController.getVisibility() == View.VISIBLE?View.INVISIBLE:View.VISIBLE);
                if(mediaController.getVisibility() == View.VISIBLE){
                    ThreadPoolExecutor tpe = (ThreadPoolExecutor)AsyncTask.THREAD_POOL_EXECUTOR;
                    tpe.setCorePoolSize(10);
                    countHideController++;
                    hideMediaController = new HideMediaControllerTask(countHideController);
                    hideMediaController.executeOnExecutor(tpe);

                }
            }
            else {
                ll.setVisibility(ll.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
            }
        }
        return true;
    }

    private PageActivity getParent(){
        return (PageActivity)getActivity();
    }

    private class VideoProgressTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            while (getParent() != null && getParent().isPlayingMode()) {
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

        public HideMediaControllerTask(int count){
            this.count = count;
        }

        @Override
        protected Void doInBackground(Void... params) {
            SystemClock.sleep(4000);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(count == countHideController) {
                mediaController.setVisibility(View.INVISIBLE);
            }
        }
    }

}
