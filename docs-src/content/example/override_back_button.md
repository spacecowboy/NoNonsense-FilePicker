+++
date = "2016-07-16T17:33:43+02:00"
title = "Override the back button"

[menu]
  [menu.main]
    identifier = "override_back_button"
    url = "example/override_back_button/"
    parent = "Examples"
    weight = 70

+++

In case you want the back button to navigate the hierarchy instead of
instantly exiting the activity, this is one approach you might take.

## Create an activity which overrides the back button and loads a custom fragment

```java
package com.nononsenseapps.filepicker.examples.backbutton;

import android.os.Environment;

import com.nononsenseapps.filepicker.AbstractFilePickerFragment;
import com.nononsenseapps.filepicker.FilePickerActivity;

import java.io.File;

public class BackHandlingFilePickerActivity extends FilePickerActivity {

    /**
     * Need access to the fragment
     */
    BackHandlingFilePickerFragment currentFragment;

    /**
     * Return a copy of the new fragment and set the variable above.
     */
    @Override
    protected AbstractFilePickerFragment<File> getFragment(
            final String startPath, final int mode, final boolean allowMultiple,
            final boolean allowDirCreate, final boolean allowExistingFile,
            final boolean singleClick) {

        // startPath is allowed to be null.
        // In that case, default folder should be SD-card and not "/"
        String path = (startPath != null ? startPath
                : Environment.getExternalStorageDirectory().getPath());

        currentFragment = new BackHandlingFilePickerFragment();
        currentFragment.setArgs(path, mode, allowMultiple, allowDirCreate,
                allowExistingFile, singleClick);
        return currentFragment;
    }

    /**
     * Override the back-button.
     */
    @Override
    public void onBackPressed() {
        // If at top most level, normal behaviour
        if (currentFragment.isBackTop()) {
            super.onBackPressed();
        } else {
            // Else go up
            currentFragment.goUp();
        }
    }
}
```

## In your custom fragment, implement the goUp and isBackTop methods

```java
package com.nononsenseapps.filepicker.examples.backbutton;

import com.nononsenseapps.filepicker.FilePickerFragment;

import java.io.File;

public class BackHandlingFilePickerFragment extends FilePickerFragment {

    /**
     * For consistency, the top level the back button checks against should be the start path.
     * But it will fall back on /.
     */
    public File getBackTop() {
        return getPath(getArguments().getString(KEY_START_PATH, "/"));
    }

    /**
     * @return true if the current path is the startpath or /
     */
    public boolean isBackTop() {
        return 0 == compareFiles(mCurrentPath, getBackTop()) ||
                0 == compareFiles(mCurrentPath, new File("/"));
    }

    /**
     * Go up on level, same as pressing on "..".
     */
    public void goUp() {
        mCurrentPath = getParent(mCurrentPath);
        mCheckedItems.clear();
        mCheckedVisibleViewHolders.clear();
        refresh(mCurrentPath);
    }
}
```

## Example manifest

Make sure `android-theme` points to the correct theme.

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.nononsenseapps.filepicker.examples">

    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <!-- Only needed to create sub directories. -->
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:supportsRtl="true"
        android:theme="@style/FilePickerTheme">

        <activity
            android:name=".backbutton.BackHandlingFilePickerActivity"
            android:label="Override back button"
            android:theme="@style/FilePickerTheme">
        </activity>
    </application>

</manifest>
```
