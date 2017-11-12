package com.status.gallery;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class PageAdapter extends FragmentPagerAdapter {
    private Context context = null;

    public PageAdapter(Context context, FragmentManager mgr) {
        super(mgr);
        this.context = context;

    }
    @Override
    public int getCount() {
        return(Model.curAlbum.fileList.size());
    }
    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
    @Override
    public Fragment getItem(int position) {
        return PageFragment.newInstance(position);
    }


}
