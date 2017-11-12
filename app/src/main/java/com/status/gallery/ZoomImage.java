package com.status.gallery;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ZoomImage implements View.OnTouchListener {

    private WeakReference<Bitmap> tmpBitmap;
    private Bitmap bitmap;
    private ImageView imageView;

    private boolean fileLoaded;                 //флаг подгрузки полного файла
    private boolean moovable;                   // флаг возможности move
    private boolean touched;                   // флаг одиночного клика
    private boolean finishedTouch;                   // флаг окончания одиночного клика

    private int bmpWidth, bmpHeight;    //размеры bitmap
    private int curWidth, curHeight;    //размеры вырезанного bitmap
    private int cur_M_X, cur_M_Y;             //координаты средней точки в bitmap
    private int curX, curY;             //координаты первого пальца в bitmap
    private int beginX, beginY;             //координаты начала вырезанного bitmap
    private int widthView, heightView;  //размеры ImageList
    private int widthViewBitmap, heightViewBitmap;  //размеры отрисованной области ImageList
    private int width, height;                  //размеры исходного bitmap в imageView

    private double x1, y1, x2, y2;              //координаты пальцев на экране
    private double xM, yM;                      //координаты средней точки на экране
    private double startLength, curLength;      //днина начального и текущего отрезков между пальцами
    private double zoom;                        //масштаб
    private double zoomStart;                   //масштаб изначальный

    private Date dateTap;

    PageView view;
    private ViewGroup ll;
    private MediaFile mediafile;


    public void init(PageView view, MediaFile mediafile) {
        this.view = view;
        if (bitmap != null && imageView != null) {
            if (fileLoaded) {
                tmpBitmap = new WeakReference<Bitmap>(Bitmap.createScaledBitmap(bitmap, width, height, false));
                imageView.setImageBitmap(tmpBitmap.get());
                bitmap.recycle();
                tmpBitmap = null;
            }
        }

        fileLoaded = false;
        bitmap = null;
        imageView = null;
        zoom = 1;
        beginX = 0;
        beginY = 0;
        startLength = 0;
        curLength = 0;
        widthViewBitmap = 0;
        heightViewBitmap = 0;
        moovable = true;
        touched = false;
        this.mediafile = mediafile;
        Runtime.getRuntime().gc();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (imageView == null) {
            imageView = (ImageView) v;
        }
        int actionMask = (event.getAction() & MotionEvent.ACTION_MASK);
        int pointerCount = event.getPointerCount();

        switch (actionMask) {
            case MotionEvent.ACTION_DOWN:
                checkSingleDoubleClick();
                x1 = event.getX();
                y1 = event.getY();
                doubleClick();


                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (pointerCount == 2) {

                    touched = false;

                    loadFullBitmap();

                    if (bitmap == null) {
                        initBitmap(((BitmapDrawable) imageView.getDrawable()).getBitmap());
                    }
                    moovable = false;
                    x2 = event.getX(1);
                    y2 = event.getY(1);
                    beforeMove();                }
                break;
            case MotionEvent.ACTION_UP: // прерывание последнего касания
                finishedTouch = true;
            case MotionEvent.ACTION_POINTER_UP: // прерывания касаний
                if (pointerCount == 1) {
                    moovable = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (pointerCount == 1 && zoom < 1 && moovable && dateTap != null) {
                    if(x1 == event.getX(0) && y1 == event.getY(0)){
                        break;
                    }
                    x1 = event.getX(0);
                    y1 = event.getY(0);

                    move();

                } else if (pointerCount > 1) {
                    x1 = event.getX(0);
                    y1 = event.getY(0);
                    x2 = event.getX(1);
                    y2 = event.getY(1);

                    zoom();
                }
                break;
        }
        return true;
    }

    private void initBitmap(Bitmap b) {
        if (bitmap != null) {
            double relative = (double) b.getWidth() / (double) bmpWidth;
            curWidth = (int) (curWidth * relative);
            curHeight = (int) (curHeight * relative);
            cur_M_X = (int) (cur_M_X * relative);
            cur_M_Y = (int) (cur_M_Y * relative);
            beginX = (int) (beginX * relative);
            beginY = (int) (beginY * relative);
            bmpHeight = b.getHeight();
            bmpWidth = b.getWidth();

            bitmap = b;

            if ((double) bmpWidth / (double) bmpHeight > (double) widthView / (double) heightView) {
                zoomStart = (double) bmpWidth / (double) widthView;
            } else {
                zoomStart = (double) bmpHeight / (double) heightView;
            }
            setSizeViewBitmap();

            if (curHeight > heightViewBitmap && curWidth > widthViewBitmap) {
                tmpBitmap = new WeakReference<Bitmap>(Bitmap.createBitmap(bitmap, beginX, beginY, curWidth, curHeight));
                tmpBitmap = new WeakReference<Bitmap>(Bitmap.createScaledBitmap(tmpBitmap.get(), widthViewBitmap, heightViewBitmap, false));
                imageView.setImageBitmap(tmpBitmap.get());
                tmpBitmap = null;
                Runtime.getRuntime().gc();
            } else {
                imageView.setImageBitmap(Bitmap.createBitmap(bitmap, beginX, beginY, curWidth, curHeight));
            }

        } else {
            bitmap = b;
            bmpHeight = bitmap.getHeight();
            bmpWidth = bitmap.getWidth();
            curWidth = bmpWidth;
            curHeight = bmpHeight;
            widthView = imageView.getWidth();
            heightView = imageView.getHeight();
            width = bmpWidth;
            height = bmpHeight;
            if ((double) bmpWidth / (double) bmpHeight > (double) widthView / (double) heightView) {
                zoomStart = (double) bmpWidth / (double) widthView;
            } else {
                zoomStart = (double) bmpHeight / (double) heightView;
            }
            setSizeViewBitmap();
        }
    }

    private void setSizeViewBitmap() {
        if ((double) bmpWidth / (double) bmpHeight > (double) widthView / (double) heightView) {
            widthViewBitmap = widthView;
            heightViewBitmap = (int) (widthViewBitmap * curHeight / curWidth);
            heightViewBitmap = heightViewBitmap > heightView ? heightView : heightViewBitmap;
        } else {
            heightViewBitmap = heightView;
            widthViewBitmap = (int) (heightViewBitmap * curWidth / curHeight);
            widthViewBitmap = widthViewBitmap > widthView ? widthView : widthViewBitmap;
        }
    }

    private void checkSingleDoubleClick(){
        if(!touched){
            new CheckTouch().execute();
        }
    }

    private void doubleClick(){
        curX = beginX + (int) ((x1 - (widthView - widthViewBitmap) / 2) * zoom * zoomStart);
        curY = beginY + (int) ((y1 - (heightView - heightViewBitmap) / 2) * zoom * zoomStart);

        if (dateTap == null) {
            dateTap = new Date();
        } else if ((new Date()).getTime() - dateTap.getTime() < 300) {
            touched = false;
            if (zoom == 1) {
                loadFullBitmap();
                if (bitmap == null) {
                    initBitmap(((BitmapDrawable) imageView.getDrawable()).getBitmap());
                }
                setSizeViewBitmap();
                cur_M_X = beginX + (int) ((x1 - (widthView - widthViewBitmap) / 2) * zoom * zoomStart);
                cur_M_Y = beginY + (int) ((y1 - (heightView - heightViewBitmap) / 2) * zoom * zoomStart);
                xM = x1;
                yM = y1;
                zoom = 0.4;
                view.setPagingEnabled(false);
                curWidth = (int) (bmpWidth * zoom);
                curHeight = (int) (bmpHeight * zoom);

                if ((double) curWidth / (zoom * zoomStart) < widthView) {
                    if ((double) bmpWidth / (zoom * zoomStart) < widthView) {
                        curWidth = bmpWidth;
                    } else {
                        curWidth = (int) (widthView * zoom * zoomStart);
                    }
                }
                if ((double) curHeight / (zoom * zoomStart) < heightView) {
                    if ((double) bmpHeight / (zoom * zoomStart) < heightView) {
                        curHeight = bmpHeight;
                    } else {
                        curHeight = (int) (heightView * zoom * zoomStart);
                    }
                }


                setSizeViewBitmap();

                beginX = cur_M_X - (int) (curWidth * (xM - (widthView - widthViewBitmap) / 2) / widthViewBitmap);
                beginY = cur_M_Y - (int) (curHeight * (yM - (heightView - heightViewBitmap) / 2) / heightViewBitmap);
                if (beginX < 0) {
                    beginX = 0;
                    view.setPagingEnabled(true);
                }
                if (beginX + curWidth > bmpWidth) {
                    beginX = bmpWidth - curWidth;
                    view.setPagingEnabled(true);
                }
                if (beginY < 0) {
                    beginY = 0;
                }
                if (beginY + curHeight > bmpHeight) {
                    beginY = bmpHeight - curHeight;
                }

                if (curHeight > heightViewBitmap && curWidth > widthViewBitmap) {
                    tmpBitmap = new WeakReference<Bitmap>(Bitmap.createBitmap(bitmap, beginX, beginY, curWidth, curHeight));
                    tmpBitmap = new WeakReference<Bitmap>(Bitmap.createScaledBitmap(tmpBitmap.get(), widthViewBitmap, heightViewBitmap, false));
                    imageView.setImageBitmap(tmpBitmap.get());
                    tmpBitmap = null;
                    Runtime.getRuntime().gc();
                } else {
                    imageView.setImageBitmap(Bitmap.createBitmap(bitmap, beginX, beginY, curWidth, curHeight));
                }

            } else {
                beginX = 0;
                beginY = 0;
                zoom = 1;
                curWidth = bmpWidth;
                curHeight = bmpHeight;
                view.setPagingEnabled(true);

                tmpBitmap = new WeakReference<Bitmap>(Bitmap.createBitmap(bitmap, beginX, beginY, curWidth, curHeight));
                tmpBitmap = new WeakReference<Bitmap>(Bitmap.createScaledBitmap(tmpBitmap.get(), width, height, false));
                imageView.setImageBitmap(tmpBitmap.get());
                tmpBitmap = null;
                Runtime.getRuntime().gc();
            }
            dateTap = null;

        } else {
            dateTap = new Date();
        }
    }

    private void beforeMove(){
        xM = (x1 + x2) / 2;
        yM = (y1 + y2) / 2;
        startLength = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2)) * zoom;
        cur_M_X = beginX + (int) ((xM - (widthView - widthViewBitmap) / 2) * zoom * zoomStart);
        cur_M_Y = beginY + (int) ((yM - (heightView - heightViewBitmap) / 2) * zoom * zoomStart);
    }

    private void move(){
        touched = false;

        setSizeViewBitmap();

        view.setPagingEnabled(zoom == 1);
        beginX = curX - (int) (curWidth * (x1 - (widthView - widthViewBitmap) / 2) / widthViewBitmap);
        beginY = curY - (int) (curHeight * (y1 - (heightView - heightViewBitmap) / 2) / heightViewBitmap);
        if (beginX < 0) {
            beginX = 0;
            view.setPagingEnabled(true);
        }
        if (beginX + curWidth > bmpWidth) {
            beginX = bmpWidth - curWidth;
            view.setPagingEnabled(true);
        }
        if (beginY < 0) {
            beginY = 0;
        }
        if (beginY + curHeight > bmpHeight) {
            beginY = bmpHeight - curHeight;
        }

        if (curHeight > heightViewBitmap && curWidth > widthViewBitmap) {
            tmpBitmap = new WeakReference<Bitmap>(Bitmap.createBitmap(bitmap, beginX, beginY, curWidth, curHeight));
            tmpBitmap = new WeakReference<Bitmap>(Bitmap.createScaledBitmap(tmpBitmap.get(), widthViewBitmap, heightViewBitmap, false));
            imageView.setImageBitmap(tmpBitmap.get());
            tmpBitmap = null;
            Runtime.getRuntime().gc();
        } else {
            imageView.setImageBitmap(Bitmap.createBitmap(bitmap, beginX, beginY, curWidth, curHeight));
        }
    }

    private void zoom(){
        curLength = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
        if (curLength < startLength) {
            curLength = startLength;
        }
        zoom = startLength / curLength;
        if(zoom < 0.2){
            zoom = 0.2;
            curLength = startLength/zoom;
        }

        view.setPagingEnabled(zoom == 1);

        curWidth = (int) (bmpWidth * zoom);
        curHeight = (int) (bmpHeight * zoom);

        if ((double) curWidth / (zoom * zoomStart) < widthView) {
            if ((double) bmpWidth / (zoom * zoomStart) < widthView) {
                curWidth = bmpWidth;
            } else {
                curWidth = (int) (widthView * zoom * zoomStart);
            }
        }
        if ((double) curHeight / (zoom * zoomStart) < heightView) {
            if ((double) bmpHeight / (zoom * zoomStart) < heightView) {
                curHeight = bmpHeight;
            } else {
                curHeight = (int) (heightView * zoom * zoomStart);
            }
        }


        setSizeViewBitmap();

        beginX = cur_M_X - (int) (curWidth * (xM - (widthView - widthViewBitmap) / 2) / widthViewBitmap);
        beginY = cur_M_Y - (int) (curHeight * (yM - (heightView - heightViewBitmap) / 2) / heightViewBitmap);
        if (beginX < 0) {
            beginX = 0;
            view.setPagingEnabled(true);
        }
        if (beginX + curWidth > bmpWidth) {
            beginX = bmpWidth - curWidth;
            view.setPagingEnabled(true);
        }
        if (beginY < 0) {
            beginY = 0;
        }
        if (beginY + curHeight > bmpHeight) {
            beginY = bmpHeight - curHeight;
        }

        if (curHeight > heightViewBitmap && curWidth > widthViewBitmap) {
            tmpBitmap = new WeakReference<Bitmap>(Bitmap.createBitmap(bitmap, beginX, beginY, curWidth, curHeight));
            tmpBitmap = new WeakReference<Bitmap>(Bitmap.createScaledBitmap(tmpBitmap.get(), widthViewBitmap, heightViewBitmap, false));
            imageView.setImageBitmap(tmpBitmap.get());
            tmpBitmap = null;
            Runtime.getRuntime().gc();
        } else {
            imageView.setImageBitmap(Bitmap.createBitmap(bitmap, beginX, beginY, curWidth, curHeight));
        }
    }


    private void loadFullBitmap(){
        if (!fileLoaded) {
            fileLoaded = true;
            Target target = new Target() {

                @Override
                public void onPrepareLoad(Drawable arg0) {
                    return;
                }

                @Override
                public void onBitmapLoaded(final Bitmap b, Picasso.LoadedFrom arg1) {

                    try {
                        initBitmap(b);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onBitmapFailed(Drawable arg0) {
                    return;
                }

            };
            imageView.setTag(target);
            Picasso.with(view.getContext())
                    .load(mediafile.getFile())
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(target);
        }

    }

    private class CheckTouch extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            touched = true;
            finishedTouch = false;
            try{
                TimeUnit.MILLISECONDS.sleep(300);
            }
            catch (InterruptedException e){}
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            if(touched && finishedTouch){

                ll = (ViewGroup) ((ViewGroup)imageView.getParent()).findViewById(R.id.ll);
                ll.setVisibility(ll.getVisibility() == View.VISIBLE?View.INVISIBLE:View.VISIBLE);


                touched = false;
            }
        }
    }

}
