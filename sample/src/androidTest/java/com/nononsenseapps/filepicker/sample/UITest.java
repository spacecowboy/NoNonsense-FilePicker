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

package com.nononsenseapps.filepicker.sample;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiScrollable;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;
import android.test.InstrumentationTestCase;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.By;
import android.test.suitebuilder.annotation.MediumTest;

import java.io.File;
import java.io.IOException;

public class UITest extends InstrumentationTestCase {

    public static final String PKG_SAMPLE = "com.nononsenseapps.filepicker.sample";
    private static final long TIMEOUT = 10;
    private UiDevice mDevice;


    private static final String[] sTestFiles = new String[] {"zz.testfile1.zxvf", "zz.testfile2.zxvf"};

    public void setUp() throws IOException {
        // Initialize UiDevice instance
        mDevice = UiDevice.getInstance(getInstrumentation());
        // Launch sample app
        Context context = getInstrumentation().getContext();
        Intent intent = context.getPackageManager()
                .getLaunchIntentForPackage(PKG_SAMPLE);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // Clear out any previous instances
        context.startActivity(intent);
        mDevice.wait(Until.hasObject(By.pkg(PKG_SAMPLE).depth(0)), TIMEOUT);

        // Create a few files so we know something exists
        createTestFiles();
    }

    private void createTestFiles() throws IOException {
        String folder = Environment.getExternalStorageDirectory().getPath();
        for (String filename: sTestFiles) {
            File file = new File(folder + "/" + filename);
            if (!file.exists()) {
                assertTrue(file.createNewFile());
            }
        }
    }

    @MediumTest
    public void testSelectSingle() throws Exception {
        // Default options are correct, press start
        UiObject sdButton = mDevice.findObject(new UiSelector().resourceId("com.nononsenseapps.filepicker.sample:id/button_sd"));

        assertTrue(sdButton.exists());

        sdButton.click();

        // Scroll to bottom and click on a test file

        UiScrollable list = new UiScrollable(new UiSelector()
                .className("android.support.v7.widget.RecyclerView"));
        UiObject testFileItem = list.getChildByText(new UiSelector()
                        .resourceId("com.nononsenseapps.filepicker.sample:id/nnf_item_container"),
                sTestFiles[0]);
        testFileItem.click();

        // Press oK
        UiObject okButton = mDevice.findObject(new UiSelector().resourceId("com.nononsenseapps.filepicker.sample:id/nnf_button_ok"));
        okButton.click();

        // Make sure result is displayed correctly
        UiObject resultField = mDevice.findObject(new UiSelector().textContains(sTestFiles[0]));
        assertTrue(resultField.exists());
    }
}
