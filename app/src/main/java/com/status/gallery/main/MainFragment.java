package com.status.gallery.main;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.status.gallery.Model;
import com.status.gallery.R;
import com.status.gallery.main.MainAdapter;

public class MainFragment extends Fragment {

    View view;
    MainAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        if(view != null) {
            if (view.getParent() != null){
                ((ViewGroup)view.getParent()).removeView(view);
            }
        }
        else {
            view = inflater.inflate(R.layout.fragment_main, container, false);
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
            adapter = new MainAdapter(getActivity(), Model.curAlbum.dateList);
            recyclerView.setAdapter(adapter);

        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

}
