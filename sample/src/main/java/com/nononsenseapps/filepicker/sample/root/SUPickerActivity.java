package com.nononsenseapps.filepicker.sample.root;

import android.os.Environment;
import android.support.annotation.Nullable;

import com.nononsenseapps.filepicker.AbstractFilePickerActivity;
import com.nononsenseapps.filepicker.AbstractFilePickerFragment;

import java.io.File;

public class SUPickerActivity extends AbstractFilePickerActivity<File> {

    public SUPickerActivity() {
        super();
    }

    @Override
    protected AbstractFilePickerFragment<File> getFragment(@Nullable String startPath,
                                                           int mode,
                                                           boolean allowMultiple,
                                                           boolean allowCreateDir,
                                                           boolean allowExistingFile,
                                                           boolean singleClick) {
        AbstractFilePickerFragment<File> fragment = new SUPickerFragment();
        // startPath is allowed to be null. In that case, default folder should be SD-card and
        // not "/"
        fragment.setArgs(
                startPath != null ? startPath : Environment.getExternalStorageDirectory().getPath(),
                mode, allowMultiple, allowCreateDir, allowExistingFile, singleClick);
        return fragment;
    }
}
