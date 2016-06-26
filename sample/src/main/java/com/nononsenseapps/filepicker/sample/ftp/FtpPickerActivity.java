/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.nononsenseapps.filepicker.sample.ftp;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nononsenseapps.filepicker.AbstractFilePickerActivity;
import com.nononsenseapps.filepicker.AbstractFilePickerFragment;

import org.apache.commons.net.ftp.FTPClient;

/**
 * An example implementation of an FTP file-picker
 */
public class FtpPickerActivity extends AbstractFilePickerActivity<FtpFile> {
    @Override
    protected AbstractFilePickerFragment<FtpFile> getFragment(@Nullable String startPath, int mode,
                                                              boolean allowMultiple,
                                                              boolean allowCreateDir,
                                                              boolean allowExistingFile,
                                                              boolean singleClick) {
        return FtpPickerFragment.newInstance(startPath, mode, allowMultiple, allowCreateDir,
                allowExistingFile, singleClick,
                "debian.simnet.is",
                FTPClient.DEFAULT_PORT,
                null,
                null, "/");
    }
}
