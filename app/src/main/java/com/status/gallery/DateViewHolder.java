package com.status.gallery;

import android.content.res.Configuration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class DateViewHolder extends RecyclerView.ViewHolder implements DateView {
    final TextView textView;
    final RecyclerView gridView;
    private DatePresenter presenter;
    MediaFileAdapter gridViewAdapter;

    DateViewHolder(View view){
        super(view);
        textView = (TextView)view.findViewById(R.id.textView);
        gridView = (RecyclerView) view.findViewById(R.id.gridView);
    }

    public void bindPresenter(DatePresenter presenter) {
        this.presenter = presenter;
        presenter.bindView(this);
    }

    public void unbindPresenter() {
        presenter = null;
    }

    public void setViews(){
        textView.setText(presenter.getTextDate());

        int countCol;
        if(((MainActivity)gridView.getContext()).getOrientation() == Configuration.ORIENTATION_PORTRAIT){
            countCol = presenter.getCountColPortrait();
        }
        else {
            countCol = presenter.getCountColLandscape();
        }

        gridView.setLayoutManager(new GridLayoutManager(gridView.getContext(), countCol));
        gridViewAdapter = new MediaFileAdapter(gridView.getContext(), presenter.getList());
        gridView.setAdapter(gridViewAdapter);

    }
}
