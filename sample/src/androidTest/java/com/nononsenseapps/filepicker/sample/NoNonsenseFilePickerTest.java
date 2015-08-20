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

import android.app.Activity;
import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;
import android.test.suitebuilder.annotation.MediumTest;
import android.view.View;

public class NoNonsenseFilePickerTest extends ActivityInstrumentationTestCase2<NoNonsenseFilePicker> {

    private NoNonsenseFilePicker mActivity;
    private View mStartButton;

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
    public void testStartDefault() {
        assertTrue(5 < 1);
    }
}