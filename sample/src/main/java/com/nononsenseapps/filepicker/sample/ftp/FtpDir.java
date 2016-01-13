/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.nononsenseapps.filepicker.sample.ftp;

public class FtpDir extends FtpFile {

    public FtpDir(FtpFile dir, String name) {
        super(dir, name);
    }

    public FtpDir(String path) {
        super(path);
    }

    public FtpDir(String dirPath, String name) {
        super(dirPath, name);
    }

    @Override
    public boolean isDirectory() {
        return true;
    }

    @Override
    public boolean isFile() {
        return false;
    }
}
