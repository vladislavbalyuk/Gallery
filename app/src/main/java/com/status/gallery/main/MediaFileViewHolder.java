package com.status.gallery.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.status.gallery.Model;
import com.status.gallery.page.PageActivity;
import com.status.gallery.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MediaFileViewHolder extends RecyclerView.ViewHolder implements MediaFileView {
    final GridViewItem imageView;
    private MediaFilePresenter presenter;

    private Picasso picasso;

    MediaFileViewHolder(View view) {
        super(view);
        imageView = (GridViewItem) view.findViewById(R.id.imageView);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) imageView.getLayoutParams();
        layoutParams.width = Model.sizeThumb;
        layoutParams.height = Model.sizeThumb;
        imageView.setLayoutParams(layoutParams);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent pageIntent = new Intent(imageView.getContext(), PageActivity.class);
                pageIntent.putExtra("position", ((GridViewItem) v).getPosition());
                imageView.getContext().startActivity(pageIntent);

            }
        });
        try {
            Picasso.setSingletonInstance(picasso);
        } catch (Exception e) {
        };
    }

    public void bindPresenter(MediaFilePresenter presenter) {
        this.presenter = presenter;
        presenter.bindView(this);
    }

    public void unbindPresenter() {
        presenter = null;
    }

    public void setViews(){
        presenter.setImageView();
    }

    public void setImageViewFromThumb(File file, int pos){
        picasso.with(imageView.getContext())
                .load(file)
//              .memoryPolicy(MemoryPolicy.NO_CACHE,MemoryPolicy.NO_STORE)
                .into(imageView);
        imageView.setPosition(pos);
    }

    public void setImageViewFromImageFile(File file, final int pos){
        final Target target = new Target() {

            @Override
            public void onPrepareLoad(Drawable arg0) {
                return;
            }

            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom arg1) {

                try {
                    File file = presenter.getNewFile();

                    file.createNewFile();
                    FileOutputStream ostream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, ostream);
                    ostream.close();
                    imageView.setImageBitmap(bitmap);
                    imageView.setPosition(pos);

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

        int sizeThumb = presenter.getSizeThumb();

        picasso.with(imageView.getContext())
                .load(file)
                .resize(sizeThumb, sizeThumb)
                .centerCrop()
                .into(target);
    }

    public void setImageViewFromVideoFile(File file, int pos){
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
            try {
                File newfile = presenter.getNewFile();

                newfile.createNewFile();
                FileOutputStream ostream = new FileOutputStream(newfile);
                thumbVideo.compress(Bitmap.CompressFormat.JPEG, 50, ostream);
                ostream.close();
            } catch (IOException e) {
            }
            ;
            imageView.setImageBitmap(thumbVideo);
        }
        imageView.setPosition(pos);
    }


}
