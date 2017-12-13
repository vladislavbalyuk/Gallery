package com.status.gallery;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class PageAdapter_ extends RecyclerView.Adapter<PageViewHolder> {

    private LayoutInflater inflater;
    private List<MediaFile> list;
    private PageFragment_ fragment;

    private Settable settable;

    private Map<Object, PagePresenter_> presenters;// MVP

    PageAdapter_(Context context, List<MediaFile> list, PageFragment_ fragment) {
        this.list = list;
        this.inflater = LayoutInflater.from(context);
        this.fragment = fragment;
        this.presenters = new HashMap<>();// MVP
    }

    @Override
    public int getItemViewType(int position) {
        if (Model.curAlbum.fileList.get(position).isImage()){
            return 1;
        } else {
            return 2;
        }
    }

    @Override
    public PageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;

        switch (viewType) {
            case 1:
                view = inflater.inflate(R.layout.fragment_page_image, parent, false);
                break;
            case 2:
                view = inflater.inflate(R.layout.fragment_page_video, parent, false);
                break;
        }
        return new PageViewHolder(view, viewType, fragment);
    }

    @Override
    public void onBindViewHolder(PageViewHolder holder, int position) {
        final MediaFile mediaFile = list.get(position);

//--------------------------MVP------------------------------
        PagePresenter_ presenter = getPresenter(mediaFile);
        if(presenter == null){
            presenter = createPresenter(mediaFile);
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
    public void onViewRecycled(PageViewHolder holder) {
        super.onViewRecycled(holder);

        holder.unbindPresenter();// MVP
    }

    @Override
    public boolean onFailedToRecycleView(PageViewHolder holder) {
        // Sometimes, if animations are running on the itemView's children, the RecyclerView won't
        // be able to recycle the view. We should still unbind the presenter.
        holder.unbindPresenter();// MVP

        return super.onFailedToRecycleView(holder);
    }



    //--------------------------MVP------------------------------
    private PagePresenter_ getPresenter(MediaFile mediaFile) {
        return presenters.get(mediaFile);
    }

    protected PagePresenter_ createPresenter(MediaFile mediaFile) {
        PagePresenter_ presenter = new PagePresenter_();
        presenter.setModel(mediaFile);
        return presenter;
    }
//--------------------------MVP------------------------------

}