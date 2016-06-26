package com.nononsenseapps.filepicker.sample.fastscroller;

import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v7.util.SortedList;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nononsenseapps.filepicker.FilePickerFragment;
import com.nononsenseapps.filepicker.sample.R;
import com.nononsenseapps.filepicker.sample.fastscroller.FastScrollerFileItemAdapter;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.io.File;

public class FastScrollerFilePickerFragment extends FilePickerFragment {

    private FastScrollRecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fastscrollerfilepicker, container, false);

        Toolbar toolbar = (Toolbar) view.findViewById(com.nononsenseapps.filepicker.R.id.nnf_picker_toolbar);
        if (toolbar != null) {
            setupToolbar(toolbar);
        }

        recyclerView = (FastScrollRecyclerView) view.findViewById(android.R.id.list);
        // improve performance if you know that changes in content
        // do not change the size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        // Set adapter
        mAdapter = new FastScrollerFileItemAdapter(this);
        recyclerView.setAdapter(mAdapter);

        view.findViewById(com.nononsenseapps.filepicker.R.id.nnf_button_cancel)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        onClickCancel(v);
                    }
                });

        view.findViewById(com.nononsenseapps.filepicker.R.id.nnf_button_ok)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        onClickOk(v);
                    }
                });

        mCurrentDirView = (TextView) view.findViewById(com.nononsenseapps.filepicker.R.id.nnf_current_dir);
        // Restore state
        if (mCurrentPath != null && mCurrentDirView != null) {
            mCurrentDirView.setText(getFullPath(mCurrentPath));
        }

        return view;
    }
}
