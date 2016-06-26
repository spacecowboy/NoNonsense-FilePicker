/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.nononsenseapps.filepicker;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


public class FilePickerFragmentTest {

    private static FilePickerFragment fragment;
    private static File somePath;
    private static String someName;

    @BeforeClass
    public static void runBeforeClass() {
        // Runs ONCE, before all tests
        fragment = new FilePickerFragment();
        someName = "FileName";
        somePath = new File("/path/to/some/" + someName);
    }

    @AfterClass
    public static void runAfterClass() {
    }

    @Test
    public void testGetName() throws Exception {
        assertEquals(someName, fragment.getName(somePath));
    }

    @Test
    public void testGetParent() throws Exception {
        assertEquals("/path/to/some", fragment.getParent(somePath).getPath());

        // Self
        assertEquals(fragment.getRoot().getPath(), fragment.getParent(new File("/path")).getPath());
        assertEquals(fragment.getRoot().getPath(), fragment.getParent(new File("/")).getPath());
    }

    @Test
    public void testGetPath() throws Exception {
        assertEquals("/some/path", fragment.getPath("/some/path").getPath());
    }

    @Test
    public void testGetFullPath() throws Exception {
        assertEquals("/some/path", fragment.getFullPath(new File("/some/path")));
    }

    @Test
    public void testGetRoot() throws Exception {
        assertEquals("/", fragment.getRoot().getPath());
    }

    @Test
    public void testSetArgsMultipleNewFiles() throws Exception {
        try {
            fragment.setArgs(null, AbstractFilePickerFragment.MODE_NEW_FILE, true, false, true, false);
            fail("Expected exception");
        } catch (IllegalArgumentException e) {
            assertTrue("Should mention the mode limitations: " + e.getMessage(),
                    e.getMessage().contains("MODE_NEW_FILE"));
        }
    }

    @Test
    public void testCompareFiles() throws Exception {
        assertEquals(0, fragment.compareFiles(new File("/A/A"), new File("/A/A")));
        assertEquals(-1, fragment.compareFiles(new File("/A/A"), new File("/A/B")));
        assertEquals(1, fragment.compareFiles(new File("/A/B"), new File("/A/A")));

        // Dir is assumed to be the same
        assertEquals(1, fragment.compareFiles(new File("/A/B"), new File("/B/A")));
        assertEquals(-1, fragment.compareFiles(new File("/B/A"), new File("/A/B")));
        assertEquals(0, fragment.compareFiles(new File("/A/B"), new File("/B/B")));
    }
}