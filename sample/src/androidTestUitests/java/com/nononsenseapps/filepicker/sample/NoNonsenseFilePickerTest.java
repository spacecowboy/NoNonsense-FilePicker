/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.nononsenseapps.filepicker.sample;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertTrue;


public class NoNonsenseFilePickerTest extends NoNonsenseFilePicker {
    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0;

    @Override
    protected void onResume() {
        super.onResume();

        // Request permission
        if (hasPermission()) {
            try {
                createTestData();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            requestPermission();
        }
    }

    void createTestData() throws IOException {
        File sdRoot = Environment.getExternalStorageDirectory().getAbsoluteFile();

        File testRoot = new File(sdRoot, "000000_nonsense-tests");

        testRoot.mkdir();
        assertTrue("Failed to create directory", testRoot.isDirectory());

        List<File> subdirs = Arrays.asList(new File(testRoot, "A-dir"),
                new File(testRoot, "B-dir"),
                new File(testRoot, "C-dir"));


        for (File subdir : subdirs) {
            subdir.mkdir();
            assertTrue("Failed to create sub directory", subdir.isDirectory());

            for (int sf = 0; sf < 10; sf++) {
                File subfile = new File(subdir, "file-" + sf + ".txt");

                subfile.createNewFile();

                assertTrue("Failed to create file", subfile.isFile());
            }
        }
    }

    protected boolean hasPermission() {
        return PackageManager.PERMISSION_GRANTED ==
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    protected void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // If arrays are empty, then process was cancelled
        if (permissions.length > 0) {
            if (PackageManager.PERMISSION_GRANTED == grantResults[0]) {
                try {
                    createTestData();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
