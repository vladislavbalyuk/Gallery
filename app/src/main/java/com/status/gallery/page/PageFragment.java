package com.status.gallery.page;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.status.gallery.Model;
import com.status.gallery.R;
import com.status.gallery.ZoomImage;
import com.status.gallery.main.MediaFile;

public class PageFragment extends Fragment {

    private View view;
    private PageAdapter adapter;
    private RecyclerView viewPager;
    private CustomLayoutManager layoutManager;
    private PageViewHolder pageViewHolder;

    private boolean videoPaused;
    private int currentPosition;

    public static ZoomImage zoomImage = new ZoomImage();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view != null) {
            if (view.getParent() != null){
                ((ViewGroup)view.getParent()).removeView(view);
            }
        }
        else{
            currentPosition = 100;
            videoPaused = false;

            view = inflater.inflate(R.layout.fragment_page,container,false);
            viewPager = (RecyclerView) view.findViewById(R.id.viewPager);
            layoutManager = new CustomLayoutManager(getActivity());

            SnapHelper snapHelper = new PagerSnapHelper();
            snapHelper.attachToRecyclerView(viewPager);

            viewPager.setLayoutManager(layoutManager);
            adapter = new PageAdapter(getActivity(), Model.curAlbum.fileList, this);
            viewPager.setAdapter(adapter);
            int position = ((PageActivity)getActivity()).getPosition();
            viewPager.getLayoutManager().scrollToPosition(position);
            initZoom(Model.curAlbum.fileList.get(position));

            viewPager.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        int position = layoutManager.findFirstVisibleItemPosition();
                        if(((PageActivity)getActivity()).getPosition() != position) {
                            ((PageActivity) getActivity()).setPosition(position);
                            initZoom(Model.curAlbum.fileList.get(position));
                        }
                    }
                }
            });

        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(pageViewHolder != null) {
            currentPosition = pageViewHolder.getCurrentPositionVideo();
        }
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public boolean isVideoPaused() {
        return videoPaused;
    }

    public void setVideoPaused(boolean videoPaused) {
        this.videoPaused = videoPaused;
    }

    public void setPagingEnabled(boolean enabled) {
        layoutManager.setScrollEnabled(enabled);
        viewPager.setLayoutManager(layoutManager);
    }

    public class CustomLayoutManager extends LinearLayoutManager {
        private boolean isScrollEnabled = true;

        public CustomLayoutManager(Context context) {
            super(context, LinearLayoutManager.HORIZONTAL, false);
        }

        public void setScrollEnabled(boolean flag) {
             this.isScrollEnabled = flag;
        }

        @Override
        public boolean canScrollHorizontally() {
            //Similarly you can customize "canScrollVertically()" for managing horizontal scroll
            return isScrollEnabled && super.canScrollHorizontally();
        }
    }

    void initZoom(MediaFile mediaFile){
        zoomImage.init(this, mediaFile);
    }

    public Context getContext() {
        return getActivity();
    }

    public void notifyRecyclerView(){
        viewPager.getAdapter().notifyDataSetChanged();
    }

    public void setPageViewHolder(PageViewHolder pageViewHolder) {
        this.pageViewHolder = pageViewHolder;
    }

    public boolean isPlayingMode() {
        return pageViewHolder != null;
    }

    public void stopVideo() {
        pageViewHolder.stopVideo();
        pageViewHolder = null;
    }

}
