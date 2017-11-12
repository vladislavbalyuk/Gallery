package com.status.gallery;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

public class PageActivity extends AppCompatActivity {

    private int position;
    private CustomViewPager viewPager;
    private PageFragment videoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_page);

        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);

        viewPager = (CustomViewPager) findViewById(R.id.pager);
        PageAdapter adapter = new PageAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);

    }

    public void setPosition(int position){
        this.position = position;
    }

    public int getPosition(){
        return position;
    }

    public CustomViewPager getViewPager(){
        return viewPager;
    }

    public boolean isPlayingMode() {
        return videoFragment != null;
    }

    public void setFragment(PageFragment videoFragment) {
        this.videoFragment = videoFragment;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == (KeyEvent.KEYCODE_BACK) && isPlayingMode()) {
            videoFragment.stopVideo();
            videoFragment = null;
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
