package com.status.gallery.main;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.status.gallery.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MediaFileAdapter extends RecyclerView.Adapter<MediaFileViewHolder> {

    private LayoutInflater inflater;
    private List<MediaFile> list;
    private Map<Object, MediaFilePresenter> presenters;// MVP

    public MediaFileAdapter(Context context, List<MediaFile> list) {
        this.list = list;
        this.inflater = LayoutInflater.from(context);
        presenters = new HashMap<>();// MVP
    }

    @Override
    public MediaFileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.photo, parent, false);
        return new MediaFileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MediaFileViewHolder holder, int position) {
        final MediaFile mediafile = list.get(position);

//--------------------------MVP------------------------------
        MediaFilePresenter presenter = getPresenter(mediafile);
        if (presenter == null) {
            presenter = createPresenter(mediafile);
        }
        holder.bindPresenter(presenter);
//--------------------------MVP------------------------------

        holder.setViews();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onViewRecycled(MediaFileViewHolder holder) {
        super.onViewRecycled(holder);

        holder.unbindPresenter();// MVP
    }

    @Override
    public boolean onFailedToRecycleView(MediaFileViewHolder holder) {
        // Sometimes, if animations are running on the itemView's children, the RecyclerView won't
        // be able to recycle the view. We should still unbind the presenter.
        holder.unbindPresenter();// MVP

        return super.onFailedToRecycleView(holder);
    }


    //--------------------------MVP------------------------------
    private MediaFilePresenter getPresenter(MediaFile mediafile) {
        return presenters.get(mediafile);
    }

    protected MediaFilePresenter createPresenter(MediaFile mediafile) {
        MediaFilePresenter presenter = new MediaFilePresenter();
        presenter.setModel(mediafile);
        return presenter;
    }
//--------------------------MVP------------------------------

}