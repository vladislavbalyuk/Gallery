package com.status.gallery.album;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.status.gallery.R;
import com.status.gallery.Settable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumViewHolder> {

    private LayoutInflater inflater;
    private List<Album> list;

    private Settable settable;

    private Map<Object, AlbumPresenter> presenters;// MVP


    public AlbumAdapter(Context context, List<Album> list) {
        this.list = list;
        this.inflater = LayoutInflater.from(context);
        this.presenters = new HashMap<>();// MVP
    }

    @Override
    public AlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.album, parent, false);
        final AlbumViewHolder holder = new AlbumViewHolder(view);
        if(settable != null){
            holder.hideTextViewCount();
        }
        holder.setSettable(settable);
        return holder;
    }

    @Override
    public void onBindViewHolder(AlbumViewHolder holder, int position) {
        final Album album = list.get(position);

//--------------------------MVP------------------------------
        AlbumPresenter presenter = getPresenter(album);
        if(presenter == null){
            presenter = createPresenter(album);
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
    public void onViewRecycled(AlbumViewHolder holder) {
        super.onViewRecycled(holder);

        holder.unbindPresenter();// MVP
    }

    @Override
    public boolean onFailedToRecycleView(AlbumViewHolder holder) {
        // Sometimes, if animations are running on the itemView's children, the RecyclerView won't
        // be able to recycle the view. We should still unbind the presenter.
        holder.unbindPresenter();// MVP

        return super.onFailedToRecycleView(holder);
    }

    public void setSettable(Settable settable){
        this.settable = settable;
    }


    //--------------------------MVP------------------------------
    private AlbumPresenter getPresenter(Album album) {
        return presenters.get(album);
    }

    protected AlbumPresenter createPresenter(Album album) {
        AlbumPresenter presenter = new AlbumPresenter();
        presenter.setModel(album);
        return presenter;
    }
//--------------------------MVP------------------------------

}