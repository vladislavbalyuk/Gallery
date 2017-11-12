package com.status.gallery;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;

public class AlbumFragment extends Fragment {

    private View view;
    private AlbumAdapter adapter;

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
            view = inflater.inflate(R.layout.fragment_album,container,false);
            RecyclerView listView = (RecyclerView) view.findViewById(R.id.listView);
            adapter = new AlbumAdapter(getActivity(), Model.listAlbums);
            listView.setAdapter(adapter);
            CheckBox checkBoxDelete = (CheckBox) view.findViewById(R.id.checkboxDelete);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) checkBoxDelete.getLayoutParams();
            layoutParams.height = 0;
            checkBoxDelete.setLayoutParams(layoutParams);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
}
