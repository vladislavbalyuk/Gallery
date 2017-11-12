package com.status.gallery;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;

public class CopyDialog extends DialogFragment implements Settable{

    private Settable settable;
    private CheckBox checkBoxDeleteSource;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View v = inflater.inflate(R.layout.fragment_album, null);

        checkBoxDeleteSource = (CheckBox)v.findViewById(R.id.checkboxDelete);
        checkBoxDeleteSource.setChecked(true);

        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.listView);
        AlbumAdapter adapter = new AlbumAdapter(getActivity(), Model.listAlbums);
        adapter.setSettable(this);
        recyclerView.setAdapter(adapter);

        return v;
    }

    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }

    public void setSettable(Settable settable){
        this.settable = settable;
    }

    @Override
    public void setValue(Object value) {
        if(!((String)value).trim().equals("")) {

            Pair pair = new Pair(value, new Boolean(checkBoxDeleteSource.isChecked()));
            settable.setValue(pair);
        }
        dismiss();
    }


}
