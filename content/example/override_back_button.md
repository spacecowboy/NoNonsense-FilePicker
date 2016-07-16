+++
date = "2016-07-16T17:33:43+02:00"
title = "Override the back button"

[menu]
  [menu.main]
    identifier = "override_back_button"
    parent = "Examples"
    weight = 70

+++

In case you want the back button navigate the hierarchy instead of instantly exiting the activity, this
is one approach you might take.

## Create an activity which overrides the back button, and loads a custom fragment

```java
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
            final boolean allowCreateDir) {
        currentFragment = new BackHandlingFilePickerFragment();
        // startPath is allowed to be null. In that case, default folder should be SD-card and not "/"
        currentFragment.setArgs(startPath != null ? startPath : Environment.getExternalStorageDirectory().getPath(),
                mode, allowMultiple, allowCreateDir);
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

## In you custom fragment, implement the goUp and isBackTop methods

```java
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
        refresh();
    }
}
```
