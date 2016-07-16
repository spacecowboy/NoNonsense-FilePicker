+++
date = "2016-07-16T17:12:51+02:00"
title = "Customizing the filepicker"

+++

Extend `AbstractFilePickerActivity` and implement `getFragment`. It
should return an instance of `AbstractFilePickerFragment`. This
basically means that the activity is just the same boilerplate with as
single line changed (see the sample app's Dropbox example for an
activity which actually has to do some extra work):

```java
// Making a custom SD-card picker
public class MyPickerActivity extends AbstractFilePickerActivity<File> {

    public MyPickerActivity() {
        super();
    }

    @Override
    protected AbstractFilePickerFragment<File> getFragment(
            final String startPath, final int mode, final boolean allowMultiple,
            final boolean allowCreateDir) {
        // Only the fragment in this line needs to be changed
        AbstractFilePickerFragment<File> fragment = new MyPickerFragment();
        fragment.setArgs(startPath, mode, allowMultiple, allowCreateDir);
        return fragment;
    }
}
```

### Extend AbstractFilePickerFragment

Which requires you to implement

-   onNewFolder
-   isDir
-   getParent
-   getPath
-   getFullPath
-   getName
-   getRoot
-   toUri
-   getLoader

If you only want to make a custom SD-card picker, you can instead extend `FilePickerFragment`, and only override the relevant method you want to change.
