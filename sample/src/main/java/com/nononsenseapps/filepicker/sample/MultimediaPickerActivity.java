/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.nononsenseapps.filepicker.sample;

import android.os.Environment;
import android.support.annotation.NonNull;

import com.nononsenseapps.filepicker.AbstractFilePickerActivity;
import com.nononsenseapps.filepicker.AbstractFilePickerFragment;

import java.io.File;

/**
 * All this class does is return a suitable fragment.
 */
public class MultimediaPickerActivity extends AbstractFilePickerActivity {

    public MultimediaPickerActivity() {
        super();
    }

    @Override
    protected AbstractFilePickerFragment<File> getFragment(
            @NonNull final String startPath, final int mode, final boolean allowMultiple,
            final boolean allowCreateDir) {
        AbstractFilePickerFragment<File> fragment = new MultimediaPickerFragment();
        // startPath is allowed to be null. In that case, default folder should be SD-card and not "/"
        fragment.setArgs(startPath != null ? startPath : Environment.getExternalStorageDirectory().getPath(),
                mode, allowMultiple, allowCreateDir);
        return fragment;
    }
}
