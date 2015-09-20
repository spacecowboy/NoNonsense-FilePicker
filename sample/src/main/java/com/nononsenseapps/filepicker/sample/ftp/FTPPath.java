/*
 * Copyright (c) 2015 Jonas Kalderstam
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
