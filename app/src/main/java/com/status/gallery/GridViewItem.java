package com.status.gallery;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class GridViewItem extends ImageView {

    private int position;

    public GridViewItem(Context context) {
        super(context);
    }

    public GridViewItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GridViewItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec); // This is the key that will make the height equivalent to its width
    }

    public void setPosition(int position){

        this.position = position;
    }

    public int getPosition(){
        return position;
    }
}
