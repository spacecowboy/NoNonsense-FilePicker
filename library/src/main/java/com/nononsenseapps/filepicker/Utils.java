package com.nononsenseapps.filepicker;

import android.support.annotation.Nullable;
import android.text.TextUtils;

/**
 * Some utility methods
 */
public class Utils {

    /**
     * Name is validated to be non-null, non-empty and not containing any
     * slashes.
     *
     * @param name The name of the folder the user wishes to create.
     */
    public static boolean isValidFileName(@Nullable String name) {
        return !TextUtils.isEmpty(name)
                && !name.contains("/")
                && !name.equals(".")
                && !name.equals("..");
    }
}
