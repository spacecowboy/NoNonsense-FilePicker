/*
 * Copyright (c) 2014 Jonas Kalderstam
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.nononsenseapps.filepicker;


import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Window;
import android.view.WindowManager;

public abstract class AbstractFilePickerActivity<T> extends Activity implements
        AbstractFilePickerFragment.OnFilePickedListener<T> {
    public static final String RESULT_PATH = "result_path";
    private static final String TAG = "filepicker_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setupFauxDialog();
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_filepicker);

        String startPath = null;
        Intent intent = getIntent();
        if (intent != null) {
            startPath = intent.getStringExtra(RESULT_PATH);
        }

        FragmentManager fm = getFragmentManager();
        AbstractFilePickerFragment<T> fragment = (AbstractFilePickerFragment<T>) fm.findFragmentByTag(TAG);

        if (fragment == null) {
            fragment = getFragment(startPath);
            fm.beginTransaction().replace(R.id.fragment,
                    fragment, TAG).commit();
        }

        // Default to cancelled
        setResult(Activity.RESULT_CANCELED);
    }

    private void setupFauxDialog() {
        // Check if this should be a dialog
        TypedValue tv = new TypedValue();
        if (!getTheme().resolveAttribute(R.attr.isDialog, tv, true) || tv.data == 0) {
            return;
        }

        // Should be a dialog; set up the window parameters.
        DisplayMetrics dm = getResources().getDisplayMetrics();

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = getResources().getDimensionPixelSize(R.dimen.configure_dialog_width);
        params.height = Math.min(
                getResources().getDimensionPixelSize(R.dimen.configure_dialog_max_height),
                dm.heightPixels * 3 / 4);
        params.alpha = 1.0f;
        params.dimAmount = 0.5f;
        getWindow().setAttributes(params);
    }

    protected abstract AbstractFilePickerFragment<T> getFragment(final String
                                                                         startPath);

    @Override
    public void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);
    }

    @Override
    public void onFilePicked(final T file) {
        Intent i = new Intent();
        i.putExtra(RESULT_PATH, file.toString());
        setResult(Activity.RESULT_OK, i);
        finish();
    }

    @Override
    public void onCancelled() {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }
}
