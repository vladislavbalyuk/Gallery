package com.status.gallery.page;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import com.status.gallery.R;

public class PageActivity extends AppCompatActivity {

    private int position;
    private PageViewHolder pageViewHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();


        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);

        setContentView(R.layout.activity_page);

//        viewPager = (CustomViewPager) findViewById(R.id.pager);//TEST
//        PageAdapter adapter = new PageAdapter(this, getSupportFragmentManager());
//        viewPager.setAdapter(adapter);
//        viewPager.setCurrentItem(position);

    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        PageFragment fragment = (PageFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_page);
        if (keyCode == (KeyEvent.KEYCODE_BACK) && fragment.isPlayingMode()) {
            fragment.stopVideo();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
