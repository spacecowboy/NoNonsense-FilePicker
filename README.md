# NoNonsense-FilePicker

<p>
<a href="https://flattr.com/submit/auto?user_id=spacecowboy&url=https%3A%2F%2Fgithub.com%2Fspacecowboy%2FNoNonsense-FilePicker" target="_blank"><img src="http://api.flattr.com/button/flattr-badge-large.png" alt="Flattr this" title="Flattr this" border="0"></a>
<a href="https://travis-ci.org/spacecowboy/NoNonsense-FilePicker">
<img src="https://travis-ci.org/spacecowboy/NoNonsense-FilePicker.svg?branch=master">
</a>
<a href='https://bintray.com/spacecowboy/maven/com.nononsenseapps%3Afilepicker/_latestVersion'><img src='https://api.bintray.com/packages/spacecowboy/maven/com.nononsenseapps%3Afilepicker/images/download.svg'></a>
</p>

<p>
<img src="https://raw.githubusercontent.com/spacecowboy/NoNonsense-FilePicker/master/screenshots/Nexus6-picker-dark.png"
width="25%"
</img>

<img src="https://raw.githubusercontent.com/spacecowboy/NoNonsense-FilePicker/master/screenshots/Nexus10-picker-dark.png"
width="50%"
</img>
</p>

<p>
<img src="https://raw.githubusercontent.com/spacecowboy/NoNonsense-FilePicker/master/screenshots/Nexus6-picker-light.png"
width="25%"
</img>

<img src="https://raw.githubusercontent.com/spacecowboy/NoNonsense-FilePicker/master/screenshots/Nexus10-picker-light.png"
width="50%"
</img>
</p>

-   Extendable for sources other than SD-card (Dropbox, Drive, etc)
-   Can select multiple items
-   Select directories or files, or both
-   Create new directories in the picker
-   Material theme with AppCompat

## Yet another file picker library?

I needed a file picker that had two primary properties:

1.  Easy to extend: I needed a file picker that would work for normal
    files on the SD-card, and also for using the Dropbox Sync API.
2.  Able to create a directory in the picker.

This project has both of those qualities. As a bonus, it also scales
nicely to work on any phone or tablet. The core is placed in abstract
classes, so it is fairly easy to extend the picker to create
your own.

The library includes an implementation that allows the user to pick
files from the SD-card. But the picker could easily be extended to get
its file listings from another source, such as Dropbox, FTP, SSH and
so on. The sample app includes an implementation which browses your
Dropbox.

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
    compile 'com.nononsenseapps:filepicker:2.0.3'
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
    <!-- You must inherit from Theme.AppCompat. Dark or light doesn't matter -->
    <style name="FilePickerTheme" parent="Theme.AppCompat.DialogWhenLarge">
        <!-- Set these to match your theme -->
        <item name="colorPrimary">@color/primary</item>
        <item name="colorPrimaryDark">@color/primary_dark</item>
        <item name="colorAccent">@color/accent</item>

        <!-- Need to set this also to style create folder dialog -->
        <item name="alertDialogTheme">@style/FilePickerAlertDialogTheme</item>

        <!-- These are important. Handled by toolbar -->
        <item name="windowActionBar">false</item>
        <item name="windowNoTitle">true</item>
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

## Want to customize further?

See the sample project for examples on dark and light themes, and an
implementation using Dropbox. The minimum required work is as follows:

### Extend AbstractFilePickerActivity

And implement **getFragment**. It should return an instance of
**AbstractFilePickerFragment**. Which naturally leads to:

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

## Not using Gradle yet?

Time to start! To convert your current Eclipse project, have a look at
my brief explanation:
<http://cowboyprogrammer.org/convert-to-android-studio-and-gradle-today/>
