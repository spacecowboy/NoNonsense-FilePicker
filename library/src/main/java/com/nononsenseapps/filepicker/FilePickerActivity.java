/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.nononsenseapps.filepicker;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SuppressLint("Registered")
public class FilePickerActivity extends AbstractFilePickerActivity<File> {

    public FilePickerActivity() {
        super();
    }

    @Override
    protected AbstractFilePickerFragment<File> getFragment(
            @Nullable final String startPath, final int mode, final boolean allowMultiple,
            final boolean allowCreateDir, final boolean allowExistingFile,
            final boolean singleClick) {
        AbstractFilePickerFragment<File> fragment = new FilePickerFragment();
        // startPath is allowed to be null. In that case, default folder should be SD-card and not "/"
        fragment.setArgs(startPath != null ? startPath : Environment.getExternalStorageDirectory().getPath(),
                mode, allowMultiple, allowCreateDir, allowExistingFile, singleClick);
        return fragment;
    }


    @SuppressWarnings("unused")
    public static List<Uri> getActivityResult(Intent data) {
        List<Uri> result = new ArrayList<>();
        if (data.getBooleanExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false)) {
            ClipData clip;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && (clip = data.getClipData()) != null) {
                for (int i = 0; i < clip.getItemCount(); i++) {
                    result.add(clip.getItemAt(i).getUri());
                }
            } else {
                List<String> paths = data.getStringArrayListExtra(FilePickerActivity.EXTRA_PATHS);
                if (paths != null) {
                    for (String path : paths) {
                        result.add(Uri.parse(path));
                    }
                }
            }
        } else {
            result.add(data.getData());
        }
        return result;
    }
}
