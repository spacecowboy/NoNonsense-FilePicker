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

import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;
import android.test.suitebuilder.annotation.MediumTest;
import android.view.View;

import java.io.File;
import java.io.IOException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class NoNonsenseFilePickerTest extends ActivityInstrumentationTestCase2<NoNonsenseFilePicker> {

    private NoNonsenseFilePicker mActivity;
    private View mStartButton;
    private static final String[] sTestFiles = new String[] {"zz.testfile1.zxvf", "zz.testfile2.zxvf"};

    public NoNonsenseFilePickerTest() {
        super(NoNonsenseFilePicker.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());

        setActivityInitialTouchMode(true);

        mActivity = getActivity();
        mStartButton = mActivity.findViewById(R.id.button_sd);

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

    /**
     * Load app, and make sure it loaded
     */
    @MediumTest
    public void testStart() {
        final View mainView = mActivity.findViewById(R.id.scrollView);
        assertNotNull(mainView);

        final View radioButtons = mActivity.findViewById(R.id.radioGroup);
        assertNotNull(radioButtons);

        ViewAsserts.assertOnScreen(mainView, radioButtons);

        ViewAsserts.assertOnScreen(mainView, mStartButton);
    }

    /**
     * Start picker with default options
     */
    @MediumTest
    public void testNoSelectionOnOk() {
        getActivity();
        onView(withId(R.id.button_sd)).perform(ViewActions.click());

        // Activity should now be started
        // Verify that OK-button is present
        //onView(withId(R.id.nnf_button_ok)).check(ViewAssertions.matches(withText(android.R.string.ok)));
        // Press it
        onView(withId(R.id.nnf_button_ok)).perform(ViewActions.click());

        // Should have given a toast, asking to select something first
        // Press cancel to go back
        onView(withId(R.id.nnf_button_cancel)).perform(ViewActions.click());
    }

    @MediumTest
    public void testSelectOne() {
        getActivity();
        onView(withId(R.id.button_sd)).perform(ViewActions.click());

        // Select a file

        // Activity should now be started
        // Verify that OK-button is present
        //onView(withId(R.id.nnf_button_ok)).check(ViewAssertions.matches(withText(android.R.string.ok)));
        // Press it
        onView(withId(R.id.nnf_button_ok)).perform(ViewActions.click());
    }
}