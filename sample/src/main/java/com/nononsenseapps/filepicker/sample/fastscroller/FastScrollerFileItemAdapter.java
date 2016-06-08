package com.nononsenseapps.filepicker.sample.fastscroller;

import android.support.annotation.NonNull;

import com.nononsenseapps.filepicker.FileItemAdapter;
import com.nononsenseapps.filepicker.LogicHandler;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.io.File;


public class FastScrollerFileItemAdapter extends FileItemAdapter<File> implements
        FastScrollRecyclerView.SectionedAdapter {

    public FastScrollerFileItemAdapter(
            @NonNull LogicHandler<File> logic) {
        super(logic);
    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        File path = getItem(position);
        if (path == null) {
            return "..";
        }
        return mLogic.getName(path).substring(0, 1).toLowerCase();
    }
}
