/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.nononsenseapps.filepicker.sample.dropbox;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.Metadata;
import com.nononsenseapps.filepicker.AbstractFilePickerActivity;
import com.nononsenseapps.filepicker.AbstractFilePickerFragment;


public class DropboxFilePickerActivity extends AbstractFilePickerActivity<Metadata> {
    private DropboxHelper dropboxHelper = new DropboxHelper();

    @Override
    protected void onResume() {
        if (dropboxHelper.authenticationFailed(this)) {
            Toast.makeText(this, "Dropbox authentication failed", Toast.LENGTH_LONG).show();
            setResult(Activity.RESULT_CANCELED);
            finish();
        }

        super.onResume();
    }

    @Override
    protected AbstractFilePickerFragment<Metadata> getFragment(@Nullable final String startPath,
                                                               final int mode, final boolean allowMultiple,
                                                               final boolean allowCreateDir, final boolean allowExistingFile,
                                                               final boolean singleClick) {
        DropboxFilePickerFragment fragment = null;

        if (!dropboxHelper.authenticationFailed(this)) {
            DbxClientV2 dropboxClient = dropboxHelper.getClient(this);

            if (dropboxClient != null) {
                fragment = new DropboxFilePickerFragment(dropboxClient);
                fragment.setArgs(startPath, mode, allowMultiple, allowCreateDir, allowExistingFile, singleClick);
            }
        }

        return fragment;
    }
}
