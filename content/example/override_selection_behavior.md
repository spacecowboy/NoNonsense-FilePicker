+++
date = "2016-07-16T17:35:01+02:00"
title = "Override selection behavior"

[menu]
  [menu.main]
    identifier = "override_selection_behavior"
    url = "example/override_selection_behavior/"
    parent = "Examples"
    weight = 99

+++

New in [2.4.0](https://github.com/spacecowboy/NoNonsense-FilePicker/releases/tag/v2.4.0) are overridable methods to handle UI-interactions. The following methods are now available for augmentation:

- onClickOK, handles ok button.
- onClickCancel, handles cancel button.
- onClickHeader, handles clicks on "..".
- onClickDir, handles clicks on non-selectable items (usually directories).
- onLongClickDir, handles long clicks on non-selectable items.
- onClickCheckable, handles clicks on selectable items.
- onLongClickCheckable, handles long clicks on selectable items.
- onClickCheckBox, handles clicks on the checkbox of selectable items.

Please see the existing implementations before you override any of them.

## Simple example, make clicks instantly select items

As asked in [#48](https://github.com/spacecowboy/NoNonsense-FilePicker/issues/48), what if the picker is configured for selecting a single file and you want a click on that to instantly return the result. The default implementation will mark the item as selected, and then the user is required to press the OK button. This small change will make the operation a single click action, returning instantly once the user selects something.

```java
public class SingleFilePickerFragment extends FilePickerFragment {
    @Override
    public void onClickCheckable(View v, CheckableViewHolder vh) {
        if (!allowMultiple) {
            // Clear is necessary, in case user clicked some checkbox directly
            mCheckedItems.clear();
            mCheckedItems.add(vh.file);
            onClickOk(null);
        } else {
            super.onClickCheckable(v, vh);
        }
    }
}
```

Now the astute reader might wonder, if my filepicker is only going to be used for selecting single files, why not just do:

```java
public void onClickCheckable(View v, CheckableViewHolder vh) {
    super.onClickCheckable(v, vh);
    onClickOk(null);
}
```

The reason is that the default implementation will animate the checkbox being selected on press. If you are closing the picker directly once the user selects something, you are basically animating something which isn't going to be seen and thus you are wasting (not that much) resources. Better to not animate at all in that case.
