+++
date = "2016-07-16T17:36:40+02:00"
title = "Standalone fragment"

[menu]
  [menu.main]
    identifier = "standalone_fragment"
    url = "example/standalone_fragment/"
    parent = "Examples"
    weight = 99

+++


To use the fragment together with an existing toolbar/action bar, a few things should be overridden.

Here's a minimal example where the toolbar is intercepted from being set as the main toolbar. The menu creation is also intercepted and populates the toolbar directly.

```java
public class StandaloneFilePickerFragment extends FilePickerFragment {

    protected Toolbar mToolbar;

    @Override
    protected void setupToolbar(Toolbar toolbar) {
        // Prevent it from being set as main toolbar by NOT calling super.setupToolbar().
        mToolbar = toolbar;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Populate the toolbar with the menu items instead of the action bar.
        mToolbar.inflateMenu(R.menu.picker_actions);

        // Set a menu listener on the toolbar with calls the regular onOptionsItemSelected method.
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return onOptionsItemSelected(item);
            }
        });

        // This is usually handled in onCreateOptions so do it here instead.
        MenuItem item = mToolbar.getMenu().findItem(com.nononsenseapps.filepicker.R.id.nnf_action_createdir);
        item.setVisible(allowCreateDir);
    }
}
```
