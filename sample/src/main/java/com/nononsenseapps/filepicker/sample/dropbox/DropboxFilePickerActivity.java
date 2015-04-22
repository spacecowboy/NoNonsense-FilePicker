/*
 * Copyright (c) 2014 Jonas Kalderstam
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.nononsenseapps.filepicker.sample.dropbox;

import android.os.Bundle;

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
            final String startPath, final int mode, final boolean allowMultiple,
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
