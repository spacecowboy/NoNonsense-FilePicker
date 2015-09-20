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


import com.nononsenseapps.filepicker.AbstractFilePickerActivity;
import com.nononsenseapps.filepicker.AbstractFilePickerFragment;

import org.apache.commons.net.ftp.FTPClient;

/**
 * An example implementation of an FTP file-picker
 */
public class FtpPickerActivity extends AbstractFilePickerActivity<FtpFile> {
    @Override
    protected AbstractFilePickerFragment<FtpFile> getFragment(String startPath, int mode, boolean allowMultiple, boolean allowCreateDir) {
        return FtpPickerFragment.newInstance(startPath, mode, allowMultiple, allowCreateDir,
                "debian.simnet.is",
                FTPClient.DEFAULT_PORT,
                null,
                null,
                "/");
    }
}
