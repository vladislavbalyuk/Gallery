package com.status.gallery;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class MainAdapter extends RecyclerView.Adapter<DateViewHolder> {

    private LayoutInflater inflater;
    private List<Date> dates;
    private Map<Object, DatePresenter> presenters;// MVP

    MainAdapter(Context context, List<Date> dates) {
        this.dates = dates;
        this.inflater = LayoutInflater.from(context);
        presenters = new HashMap<>();// MVP
    }
    @Override
    public DateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new DateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DateViewHolder holder, int position) {
        final Date date = dates.get(position);

//--------------------------MVP------------------------------
        DatePresenter presenter = getPresenter(date);
        if(presenter == null){
            presenter = createPresenter(date);
        }
        holder.bindPresenter(presenter);
//--------------------------MVP------------------------------
        holder.setViews();
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }


    //--------------------------MVP------------------------------
    private DatePresenter getPresenter(Date date) {
        return presenters.get(date);
    }

    protected DatePresenter createPresenter(Date date) {
        DatePresenter presenter = new DatePresenter();
        presenter.setModel(date);
        return presenter;
    }
//--------------------------MVP------------------------------


}