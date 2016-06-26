package com.nononsenseapps.filepicker.sample;

import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class Helpers {
    public static void createTestDirsAndFiles() throws IOException {
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
}
