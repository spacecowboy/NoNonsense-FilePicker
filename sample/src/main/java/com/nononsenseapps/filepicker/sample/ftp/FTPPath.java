/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.nononsenseapps.filepicker.sample.ftp;

import android.support.annotation.NonNull;

import org.apache.commons.net.ftp.FTPFile;

/**
 * Adds path information to FtpFile objects
 */
public class FTPPath {
    public final String path;
    public final FTPFile file;

    public FTPPath(@NonNull String path, @NonNull FTPFile file) {
        this.path = path;
        this.file = file;
    }

    public FTPPath(@NonNull FTPPath mCurrentPath, @NonNull FTPFile file) {
        this.file = file;
        if (mCurrentPath.path.endsWith("/")) {
            this.path = mCurrentPath + file.getName();
        } else {
            this.path = mCurrentPath.path + "/" + file.getName();
        }
    }

    public boolean isDirectory() {
        return file.isDirectory();
    }

    public String getName() {
        return file.getName();
    }

    public String appendToDir(@NonNull String name) {
        if (this.path.endsWith("/")) {
            return path + name;
        } else {
            return path + "/" + name;
        }
    }
}
