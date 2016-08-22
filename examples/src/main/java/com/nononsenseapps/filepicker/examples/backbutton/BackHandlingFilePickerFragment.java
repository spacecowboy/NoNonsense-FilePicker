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