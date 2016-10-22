package com.nononsenseapps.filepicker.sample;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.nononsenseapps.filepicker.AbstractFilePickerActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.nononsenseapps.filepicker.sample.PermissionGranter.allowPermissionsIfNeeded;
import static org.hamcrest.Matchers.allOf;

/**
 * In this class, the activity is launched using an intent pointing to a file.
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class SelectNewFileStartPathIsFile {

    @Rule
    public ActivityTestRule<NoNonsenseFilePickerTest> mActivityTestRule =
            new ActivityTestRule<NoNonsenseFilePickerTest>(NoNonsenseFilePickerTest.class) {
                @Override
                protected Intent getActivityIntent() {
                    Context targetContext = InstrumentationRegistry.getInstrumentation()
                            .getTargetContext();
                    Intent result = new Intent(targetContext, NoNonsenseFilePickerTest.class);
                    String path = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(),
                            "000000_nonsense-tests/A-dir/file-3.txt").getAbsolutePath();
                    result.putExtra(AbstractFilePickerActivity.EXTRA_START_PATH, path);
                    return result;
                }
            };

    @Before
    public void allowPermissions() {
        allowPermissionsIfNeeded(mActivityTestRule.getActivity());
    }

    @Test
    public void selectNewFileWithStartPath() throws IOException {
        ViewInteraction radioButton = onView(
                allOf(withId(R.id.radioNewFile), withText("Select new file"),
                        withParent(withId(R.id.radioGroup)),
                        isDisplayed()));
        radioButton.perform(click());

        ViewInteraction button = onView(
                allOf(withId(R.id.button_sd), withText("Pick SD-card"), isDisplayed()));
        button.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.nnf_text_filename),
                        withParent(allOf(withId(R.id.nnf_newfile_button_container),
                                withParent(withId(R.id.nnf_buttons_container)))),
                        isDisplayed()));

        appCompatEditText.check(matches(withText("file-3.txt")));

        ViewInteraction appCompatImageButton = onView(
                allOf(withId(R.id.nnf_button_ok_newfile),
                        withParent(allOf(withId(R.id.nnf_newfile_button_container),
                                withParent(withId(R.id.nnf_buttons_container)))),
                        isDisplayed()));
        // Click ok
        appCompatImageButton.perform(click());

        ViewInteraction textView = onView(withId(R.id.text));
        textView.check(matches(withText("/storage/emulated/0/000000_nonsense-tests/A-dir/file-3.txt")));
    }
}
