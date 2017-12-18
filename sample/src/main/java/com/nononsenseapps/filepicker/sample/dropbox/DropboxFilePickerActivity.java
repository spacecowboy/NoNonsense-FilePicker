/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.nononsenseapps.filepicker.sample.dropbox;

import android.support.annotation.Nullable;
import android.widget.Toast;

import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.Metadata;
import com.nononsenseapps.filepicker.AbstractFilePickerActivity;
import com.nononsenseapps.filepicker.AbstractFilePickerFragment;


public class DropboxFilePickerActivity extends AbstractFilePickerActivity<Metadata> {
    private DbxClientV2 dropboxClient = null;

    @Override
    protected void onResume() {
        dropboxClient = DropboxHelper.getClient(this);

        super.onResume();
    }

    @Override
    protected AbstractFilePickerFragment<Metadata> getFragment(@Nullable final String startPath,
                                                               final int mode, final boolean allowMultiple,
                                                               final boolean allowCreateDir, final boolean allowExistingFile,
                                                               final boolean singleClick) {
        DropboxFilePickerFragment fragment = null;

        if (dropboxClient != null) {
            fragment = new DropboxFilePickerFragment(dropboxClient);
            fragment.setArgs(startPath, mode, allowMultiple, allowCreateDir, allowExistingFile, singleClick);
        }
        else {
            Toast.makeText(this, "Not authenticated with Dropbox", Toast.LENGTH_LONG).show();
        }

        return fragment;
    }
}
