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
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;
import android.test.InstrumentationTestCase;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.By;
import android.test.suitebuilder.annotation.MediumTest;

public class UITest extends InstrumentationTestCase {

    public static final String PKG_SAMPLE = "com.nononsenseapps.filepicker.sample";
    private static final long TIMEOUT = 10;
    private UiDevice mDevice;

    public void setUp() {
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
    }

    @MediumTest
    public void testSelectSingle() throws Exception {
        // Default options are correct, press start
        UiObject sdButton = mDevice.findObject(new UiSelector().resourceId("com.nononsenseapps.filepicker.sample:id/button_sd"));

        assertTrue(sdButton.exists());

        sdButton.click();
    }
}
