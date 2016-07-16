+++
date = "2016-07-16T17:32:07+02:00"
title = "Filter based on file extension"

[menu]
  [menu.main]
    identifier = "filter_file_extension"
    parent = "Examples"
    weight = 10

+++


By default, the SD-card picker will display all files in alphabetical order. But let's say that your app can only handle a specific type of file, like `.txt`-files. Here's a minimal example which will only display such files.

First, a convenience method to get the extension of files:

```java
    // File extension to filter on, including the initial dot.
    private static final String EXTENSION = ".txt";

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
```

The decision to display files or not is done with the `isItemVisible` method. Just add a check for the file-extension:

```java
    @Override
    protected boolean isItemVisible(final File file) {
        // simplified behavior   (see below full code)
        // return isDir(file) || (mode == MODE_FILE || mode == MODE_FILE_AND_DIR);
        if (!isDir(file) && (mode == MODE_FILE || mode == MODE_FILE_AND_DIR)) {
            String ext = getExtension(file);
            return ext != null && EXTENSION.equalsIgnoreCase(ext);
        }
        return isDir(file);
    }
```

### Before and After
<img src="/screenshots/filter_before.png" width="30%" alt="Before"/>
<img src="/screenshots/filter_after.png" width="30%" alt="After"/>

### Full Fragment code

```java
import com.nononsenseapps.filepicker.FilePickerFragment;

import java.io.File;

public class FilteredFilePickerFragment extends FilePickerFragment {

    // File extension to filter on
    private static final String EXTENSION = ".txt";

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

    @Override
    protected boolean isItemVisible(final File file) {
        boolean ret = super.isItemVisible(file);
        if (ret && !isDir(file) && (mode == MODE_FILE || mode == MODE_FILE_AND_DIR)) {
            String ext = getExtension(file);
            return ext != null && EXTENSION.equalsIgnoreCase(ext);
        }
        return ret;
    }
}
```
