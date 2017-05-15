package com.devsideas.leapchat.util;

import android.view.View;

public class RecyclerListener {
    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }
}