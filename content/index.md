+++
date = "2016-07-16T16:42:44+02:00"
title = "Readme"
type = "index"
+++

<p>
<img src="/screenshots/Nexus6-picker-dark.png" width="25%"/>

<img src="/screenshots/Nexus10-picker-light.png" width="60%"/>
</p>

- Extendable for sources other than SD-card (Dropbox, FTP, Drive, etc)
- Can select multiple items
- Select directories or files, or both
- Create new directories in the picker
- Material theme with AppCompat

## Yet another file picker library?

I needed a file picker that had two primary properties:

1.  Easy to extend: I needed a file picker that would work for normal
    files on the SD-card, and also for using the Dropbox API.
2.  Able to create a directory in the picker.

This project has both of those qualities. As a bonus, it also scales
nicely to work on any phone or tablet. The core is placed in abstract
classes, so it is fairly easy to extend the picker to create
your own.

The library includes an implementation that allows the user to pick
files from the SD-card. But the picker could easily be extended to get
its file listings from another source, such as Dropbox, FTP, SSH and
so on. The sample app includes implementations which browses your
Dropbox and a Linux mirror FTP-server.

By inheriting from an Activity, the picker is able to be rendered as
full screen on small screens and as a dialog on large screens. It does
this through the theme system, so it is very important for the
activity to use a correctly configured theme.

## How to include in your project (with Gradle)

Just add the dependency to your *build.gradle*:

```groovy
repositories {
    jcenter()
}

dependencies {
    compile 'com.nononsenseapps:filepicker:3.0.0'
}
```


## How to use the included SD-card picker:

### Include permission in your manifest

```xml
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

### Include the file picker activity

The intent filter is optional depending on your use case. Note that
the theme set in the manifest is important.

```xml
    <activity
       android:name="com.nononsenseapps.filepicker.FilePickerActivity"
       android:label="@string/app_name"
       android:theme="@style/FilePickerTheme">
       <intent-filter>
          <action android:name="android.intent.action.GET_CONTENT" />
          <category android:name="android.intent.category.DEFAULT" />
       </intent-filter>
    </activity>
```

### Configure the theme

You must **set the theme** on the activity, but you can configure it to
match your existing application theme. You can also name it whatever
you like..

```xml
    <!-- You can also inherit from NNF_BaseTheme.Light -->
    <style name="FilePickerTheme" parent="NNF_BaseTheme">
        <!-- Set these to match your theme -->
        <item name="colorPrimary">@color/primary</item>
        <item name="colorPrimaryDark">@color/primary_dark</item>
        <item name="colorAccent">@color/accent</item>

        <!-- Need to set this also to style create folder dialog -->
        <item name="alertDialogTheme">@style/FilePickerAlertDialogTheme</item>

        <!-- If you want to set a specific toolbar theme, do it here -->
        <!-- <item name="nnf_toolbarTheme">@style/ThemeOverlay.AppCompat.Dark.ActionBar</item> -->
    </style>

    <style name="FilePickerAlertDialogTheme" parent="Theme.AppCompat.Dialog.Alert">
        <item name="colorPrimary">@color/primary</item>
        <item name="colorPrimaryDark">@color/primary_dark</item>
        <item name="colorAccent">@color/accent</item>
    </style>
```

### Starting the picker in your app

```java
    // This always works
    Intent i = new Intent(context, FilePickerActivity.class);
    // This works if you defined the intent filter
    // Intent i = new Intent(Intent.ACTION_GET_CONTENT);

    // Set these depending on your use case. These are the defaults.
    i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
    i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
    i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);

    // Configure initial directory by specifying a String.
    // You could specify a String like "/storage/emulated/0/", but that can
    // dangerous. Always use Android's API calls to get paths to the SD-card or
    // internal memory.
    i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());

    startActivityForResult(i, FILE_CODE);
```

### Handling the result

If you have a minimum requirement of Jelly Bean (API 16) and above,
you can skip the second method.

```java
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_CODE && resultCode == Activity.RESULT_OK) {
            if (data.getBooleanExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false)) {
                // For JellyBean and above
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ClipData clip = data.getClipData();

                    if (clip != null) {
                        for (int i = 0; i < clip.getItemCount(); i++) {
                            Uri uri = clip.getItemAt(i).getUri();
                            // Do something with the URI
                        }
                    }
                // For Ice Cream Sandwich
                } else {
                    ArrayList<String> paths = data.getStringArrayListExtra
                                (FilePickerActivity.EXTRA_PATHS);

                    if (paths != null) {
                        for (String path: paths) {
                            Uri uri = Uri.parse(path);
                            // Do something with the URI
                        }
                    }
                }

            } else {
                Uri uri = data.getData();
                // Do something with the URI
            }
        }
    }
```

## Customizing the picker

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

See the sample app and the examples for some concrete code.
