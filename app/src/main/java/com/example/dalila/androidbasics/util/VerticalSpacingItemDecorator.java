package com.example.dalila.androidbasics.util;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;


//one of the helper classes for recycler view is item decorator
//in this case we are gonna use it to add spacing between list items

public class VerticalSpacingItemDecorator extends RecyclerView.ItemDecoration {

    private final int verticalSpaceHeight;

    public VerticalSpacingItemDecorator(int verticalSpaceHeight) {
        this.verticalSpaceHeight = verticalSpaceHeight;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        //we are gonna add spacing to the bottom of the item (but we can usually do it for the top, left and right, too)
        outRect.bottom = verticalSpaceHeight;
    }
}
