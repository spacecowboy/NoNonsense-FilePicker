+++
date = "2016-07-16T17:21:28+02:00"
title = "Custom item layout"

[menu]
  [menu.main]
    identifier = "customitemlayout"
    parent = "Examples"
    weight = 20

+++

Say you want to browse some files which have really long names. By default, filenames will be cut if they exceed one line in width like `ThisIsAReallyLongFi...`. What if we really wanted it show like in this image?

![Example of a long filename](/screenshots/itemlayout_longfilename.png)

The behavior of the text is defined in the listitem layouts:
[nnf_filepicker_listitem_checkable](https://github.com/spacecowboy/NoNonsense-FilePicker/blob/master/library/src/main/res/layout/nnf_filepicker_listitem_checkable.xml)
and
[nnf_filepicker_listitem_dir](https://github.com/spacecowboy/NoNonsense-FilePicker/blob/master/library/src/main/res/layout/nnf_filepicker_listitem_dir.xml).

There are two kinds of layouts, one with a checkbox to allow selection, and one without a checkbox. The second one is also used for the special header item `..` though you could of course have a special layout for that if you wanted.

### Layouts

Let's create some new layouts which will support longer filenames as follows:

**longer_listitem_checkable.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:background="?android:selectableItemBackground"
    android:minHeight="?android:listPreferredItemHeight"
    android:orientation="horizontal">


    <!--suppress AndroidDomInspection -->
    <ImageView
        android:id="@+id/item_icon"
        android:layout_width="?android:listPreferredItemHeight"
        android:layout_height="?android:listPreferredItemHeight"
        android:adjustViewBounds="true"
        android:scaleType="fitCenter"
        android:src="@drawable/nnf_ic_file_folder"
        android:tint="?attr/colorAccent"
        android:visibility="visible"
        tools:ignore="ContentDescription" />

    <TextView
        android:id="@android:id/text1"
        style="?android:textAppearanceLarge"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:minHeight="?android:listPreferredItemHeight"
        android:layout_weight="1"
        android:ellipsize="end"
        android:gravity="center_vertical"
        android:maxLines="4"
        android:padding="8dp"/>

    <CheckBox
        android:id="@+id/checkbox"
        android:layout_width="wrap_content"
        android:layout_height="match_parent"
        android:paddingEnd="8dp"
        android:paddingRight="8dp"
        tools:ignore="RtlSymmetry" />

</LinearLayout>
```

**longer_listitem_dir.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:background="?android:selectableItemBackground"
    android:minHeight="?android:listPreferredItemHeight"
    android:orientation="horizontal"
    >

    <!--suppress AndroidDomInspection -->
    <ImageView
        android:id="@+id/item_icon"
        android:layout_width="?android:listPreferredItemHeight"
        android:layout_height="?android:listPreferredItemHeight"
        android:adjustViewBounds="true"
        android:scaleType="center"
        android:src="@drawable/nnf_ic_file_folder"
        android:tint="?attr/colorAccent"
        android:visibility="visible"
        tools:ignore="ContentDescription" />

    <TextView
        android:id="@android:id/text1"
        style="?android:textAppearanceLarge"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:minHeight="?android:listPreferredItemHeight"
        android:layout_weight="1"
        android:ellipsize="end"
        android:gravity="center_vertical"
        android:maxLines="4"
        android:padding="8dp"/>
</LinearLayout>
```

Note that I defined the TextViews to have a maximum of 4 lines (actual number is up to you), and a minimum height of `android:listPreferredItemHeight` (this I recommend, otherwise it looks wonky and off-center). And just be clear, the *ids* of these fields must be `@+id/item_icon`, `@android:id/text1`, and `@+id/checkbox`, or the code WILL crash on you.

### Code

To use the new layouts, you need to override the `onCreateViewHolder` method in
[AbstractFilePickerFragment](https://github.com/spacecowboy/NoNonsense-FilePicker/blob/master/library/src/main/java/com/nononsenseapps/filepicker/AbstractFilePickerFragment.java).

Since this example will be browsing the SD-card, I will extend from the built-in FilePickerFragment.

```java
public class CustomLayoutFilePickerFragment extends FilePickerFragment {
    /**
     * @param parent Containing view
     * @param viewType which the ViewHolder will contain. Will be one of:
     * [VIEWTYPE_HEADER, VIEWTYPE_CHECKABLE, VIEWTYPE_DIR]. It is OK, and even expected, to use the same
     * layout for VIEWTYPE_HEADER and VIEWTYPE_DIR.
     * @return a view holder for a file or directory (the difference is presence of checkbox).
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        switch (viewType) {
            case LogicHandler.VIEWTYPE_HEADER:
                v = LayoutInflater.from(getActivity()).inflate(R.layout.longer_listitem_dir,
                        parent, false);
                return new HeaderViewHolder(v);
            case LogicHandler.VIEWTYPE_CHECKABLE:
                v = LayoutInflater.from(getActivity()).inflate(R.layout.longer_listitem_checkable,
                        parent, false);
                return new CheckableViewHolder(v);
            case LogicHandler.VIEWTYPE_DIR:
            default:
                v = LayoutInflater.from(getActivity()).inflate(R.layout.longer_listitem_dir,
                        parent, false);
                return new DirViewHolder(v);
        }
    }
}
```

And as always, to use your custom fragment you need a custom activity which loads it for you:

```java
public class CustomLayoutPickerActivity extends AbstractFilePickerActivity {

    public CustomLayoutPickerActivity() {
        super();
    }

    @Override
    protected AbstractFilePickerFragment<File> getFragment(
            final String startPath, final int mode, final boolean allowMultiple,
            final boolean allowCreateDir) {
        // Load our custom fragment here
        AbstractFilePickerFragment<File> fragment = new CustomLayoutFilePickerFragment();
        // startPath is allowed to be null. In that case, default folder should be SD-card and not "/"
        fragment.setArgs(startPath != null ? startPath : Environment.getExternalStorageDirectory().getPath(),
                mode, allowMultiple, allowCreateDir);
        return fragment;
    }
}
```
