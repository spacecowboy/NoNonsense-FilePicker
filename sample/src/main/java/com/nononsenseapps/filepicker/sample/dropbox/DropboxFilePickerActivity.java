/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.nononsenseapps.filepicker.sample.dropbox;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.nononsenseapps.filepicker.AbstractFilePickerActivity;
import com.nononsenseapps.filepicker.AbstractFilePickerFragment;


public class DropboxFilePickerActivity
            extends AbstractFilePickerActivity<DropboxAPI.Entry> {

    // In the class declaration section:
    private DropboxAPI<AndroidAuthSession> mDBApi;

    @Override
    public void onCreate(Bundle b) {
        mDBApi = DropboxSyncHelper.getDBApi(this);
        if (!mDBApi.getSession().isLinked()) {
            // No valid authentication
            finish();
        }

        super.onCreate(b);
    }

    @Override
    protected AbstractFilePickerFragment<DropboxAPI.Entry> getFragment(
            @NonNull final String startPath, final int mode, final boolean allowMultiple,
            final boolean allowCreateDir) {
        if (mDBApi == null || !mDBApi.getSession().isLinked()) {
            // No valid authentication
            finish();
            return null;
        }

        DropboxFilePickerFragment fragment =
                new DropboxFilePickerFragment(mDBApi);
        fragment.setArgs(startPath, mode, allowMultiple, allowCreateDir);
        return fragment;
    }
}
