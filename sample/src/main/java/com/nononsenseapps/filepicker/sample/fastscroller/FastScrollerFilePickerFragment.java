package com.nononsenseapps.filepicker.sample.fastscroller;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nononsenseapps.filepicker.FilePickerFragment;
import com.nononsenseapps.filepicker.sample.R;

public class FastScrollerFilePickerFragment extends FilePickerFragment {
    @Override
    protected View inflateRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_fastscrollerfilepicker, container, false);
    }
}
