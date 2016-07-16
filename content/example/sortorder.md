+++
date = "2016-07-16T17:10:46+02:00"
title = "Change the sort order"

[menu]
  [menu.main]
    parent = "Examples"
    identifier = "sortorder"
    weight = 0
+++

By default, the SD-card picker will display all files in alphabetical order. But what if you want a different sort-order?

You can override the sorting by overriding the `compareFiles`-method:

```java
    @Override
    protected int compareFiles(File lhs, File rhs) {
        if (lhs.isDirectory() && !rhs.isDirectory()) {
            return -1;
        } else if (rhs.isDirectory() && !lhs.isDirectory()) {
            return 1;
        }
        // This was the previous behaviour for all file-file comparisons. Now it's
        // only done if the files have the same extension, or no extension.
        else if (getExtension(lhs) != null && getExtension(lhs).equalsIgnoreCase(getExtension(rhs)) ||
                getExtension(lhs) == null && getExtension(rhs) == null) {
            return lhs.getName().compareToIgnoreCase(rhs.getName());
        }
        // Otherwise, we sort on extension placing files with no extension last.
        else if (getExtension(lhs) != null && getExtension(rhs) != null) {
            // Both have extension, just compare extensions
            return getExtension(lhs).compareToIgnoreCase(getExtension(rhs));
        } else if (getExtension(lhs) != null) {
            // Left has extension, place it first
            return -1;
        } else {
            // Right has extension, place it first
            return 1;
        }
    }
```

### Before and After
<img src="/screenshots/sorting_before.png" width="30%" alt="Before"/>
<img src="/screenshots/sorting_after.png" width="30%" alt="After"/>

### Full Fragment code

```java
import com.nononsenseapps.filepicker.FilePickerFragment;

import java.io.File;

public class SortedFilePickerFragment extends FilePickerFragment {

    /**
     *
     * @param file
     * @return The file extension. If file has no extension, it returns null.
     */
    private String getExtension(@NonNull File file) {
        String path = file.getPath();
        int i = path.lastIndexOf(".");
        if (i < 0) {
            return null;
        } else {
            return path.substring(i);
        }
    }

    /**
     * Compare two files to determine their relative sort order. This follows the usual
     * comparison interface. Override to determine your own custom sort order.
     *
     * @param lhs File on the "left-hand side"
     * @param rhs File on the "right-hand side"
     * @return -1 if if lhs should be placed before rhs, 0 if they are equal,
     * and 1 if rhs should be placed before lhs
     */
    @Override
    protected int compareFiles(File lhs, File rhs) {
        if (lhs.isDirectory() && !rhs.isDirectory()) {
            return -1;
        } else if (rhs.isDirectory() && !lhs.isDirectory()) {
            return 1;
        }
        // This was the previous behaviour for all file-file comparisons. Now it's
        // only done if the files have the same extension, or no extension.
        else if (getExtension(lhs) != null && getExtension(lhs).equalsIgnoreCase(getExtension(rhs)) ||
                getExtension(lhs) == null && getExtension(rhs) == null) {
            return lhs.getName().compareToIgnoreCase(rhs.getName());
        }
        // Otherwise, we sort on extension placing files with no extension last.
        else if (getExtension(lhs) != null && getExtension(rhs) != null) {
            // Both have extension, just compare extensions
            return getExtension(lhs).compareToIgnoreCase(getExtension(rhs));
        } else if (getExtension(lhs) != null) {
            // Left has extension, place it first
            return -1;
        } else {
            // Right has extension, place it first
            return 1;
        }
    }
}
```
