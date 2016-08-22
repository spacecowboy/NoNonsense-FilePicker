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
import com.nononsenseapps.filepicker.FilePickerFragment;
import java.io.File;

public class BackHandlingFilePickerFragment extends FilePickerFragment {

    /**
     * For consistency, the top level the back button checks against should be the start path.
     * But it will fall back on /.
     */
    public File getBackTop() {
        if (getArguments().containsKey(KEY_START_PATH)) {
            return getPath(getArguments().getString(KEY_START_PATH));
        } else {
            return new File("/");
        }
    }

    /**
     *
     * @return true if the current path is the startpath or /
     */
    public boolean isBackTop() {
        return 0 == compareFiles(mCurrentPath, getBackTop()) || 0 == compareFiles(mCurrentPath, new File("/"));
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

## And finally, add the following to your manifest

And make sure `android-theme` points to the correct theme.

```xml
<activity
    android:name=".BackHandlingFilePickerActivity"
    android:label="@string/select_file"
    android:theme="@style/FilePickerTheme">
    <intent-filter>
        <action android:name="android.intent.action.GET_CONTENT" />
        <category android:name="android.intent.category.DEFAULT" />
    </intent-filter>
</activity>
```
