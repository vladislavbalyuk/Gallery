package com.status.gallery.album;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.status.gallery.main.MainActivity;
import com.status.gallery.R;
import com.status.gallery.Settable;
import com.status.gallery.StringDialog;

import java.io.File;

public class AlbumViewHolder extends RecyclerView.ViewHolder implements AlbumView, Settable {
    final ImageView imageView;
    final TextView textViewName;
    final TextView textViewCount;
    private AlbumPresenter presenter;

    private Settable settable;


    private Picasso picasso;


    AlbumViewHolder(View view) {
        super(view);
        imageView = (ImageView) view.findViewById(R.id.imageView);
        textViewName = (TextView) view.findViewById(R.id.textViewName);
        textViewCount = (TextView) view.findViewById(R.id.textViewCount);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onAlbumClicked();
            }
        });

        try {
            Picasso.setSingletonInstance(picasso);
        } catch (Exception e) {
        };
    }

    public void bindPresenter(AlbumPresenter presenter) {
        this.presenter = presenter;
        presenter.bindView(this);

        if(presenter.IsEmptyAlbum() && settable == null){
            setHeightItems(0);
        }
        else{
            setHeightItems(presenter.getSizeThumb());
        }
    }

    public void unbindPresenter() {
        presenter = null;
    }


    public void setViews(){
        if(presenter.IsEmptyAlbum()){
            imageView.setImageResource(R.drawable.plus);
        }
        else {
            presenter.setImageView();
        }
        textViewName.setText(presenter.getName());
        textViewCount.setText(presenter.getCount());
    }

    public void setImageViewFromThumb(File file){
        picasso.with(imageView.getContext())
                .load(file)
                .into(imageView);
    }

    public void setImageViewFromImageFile(File file){
        int sizeThumb = presenter.getSizeThumb();
        picasso.with(imageView.getContext())
                .load(file)
                .resize(sizeThumb, sizeThumb)
                .centerCrop()
                .into(imageView);
    }

    public void setImageViewFromVideoFile(File file){
        int sizeThumb = presenter.getSizeThumb();
        Bitmap thumbVideo = ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(),
                MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);

        if(thumbVideo != null) {
            int height = thumbVideo.getHeight();
            int width = thumbVideo.getWidth();
            thumbVideo = Bitmap.createScaledBitmap(thumbVideo
                    , height > width ? sizeThumb : (int) (sizeThumb * width / height), width > height ? sizeThumb : (int) (sizeThumb * height / width), true);
            height = thumbVideo.getHeight();
            width = thumbVideo.getWidth();
            thumbVideo = Bitmap.createBitmap(thumbVideo, height > width ? 0 : (int) ((width - sizeThumb) / 2), width > height ? 0 : (int) ((height - sizeThumb) / 2), sizeThumb, sizeThumb);
            imageView.setImageBitmap(thumbVideo);
        }
    }

    private void setHeightItems(int sizeThumb){
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) imageView.getLayoutParams();
        layoutParams.width = sizeThumb;
        layoutParams.height = sizeThumb;
        imageView.setLayoutParams(layoutParams);

        layoutParams = (LinearLayout.LayoutParams) textViewName.getLayoutParams();
        layoutParams.height = sizeThumb;
        textViewName.setLayoutParams(layoutParams);

        layoutParams = (LinearLayout.LayoutParams) textViewCount.getLayoutParams();
        layoutParams.height = sizeThumb;
        textViewCount.setLayoutParams(layoutParams);
    }

    public void hideTextViewCount(){
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) textViewCount.getLayoutParams();
        layoutParams.weight = 0;
        textViewCount.setLayoutParams(layoutParams);
    }

    public void openAlbum(Album album){
        Intent intent = new Intent(imageView.getContext(), MainActivity.class);
        imageView.getContext().startActivity(intent);
    }


    public Context getContext(){
        return imageView.getContext();
    }

    public void openStringDialog(){
        StringDialog dlg = new StringDialog();
        dlg.setSettable(this);
        dlg.show(((Activity) getContext()).getFragmentManager(), "dlg");

    }

    public void setSettable(Settable settable){
        this.settable = settable;
    }

    public Settable getSettable(){
        return settable;
    }

    @Override
    public void setValue(Object value) {
        presenter.createNewAlbum((String)value);
    }
}
