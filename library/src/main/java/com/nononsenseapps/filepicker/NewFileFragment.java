/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.nononsenseapps.filepicker;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;

public class NewFileFragment extends NewItemFragment {

    private static final String TAG = "new_file_fragment";

    public static void showDialog(@NonNull final FragmentManager fm,
                                  @Nullable final OnNewItemListener listener) {
        NewItemFragment d = new NewFileFragment();
        d.setListener(listener);
        d.show(fm, TAG);
    }

    @Override
    protected boolean validateName(@Nullable final String itemName) {
        return !TextUtils.isEmpty(itemName)
                && !itemName.contains("/")
                && !itemName.equals(".")
                && !itemName.equals("..");
    }

    @Override
    protected int getLayout() {
        return R.layout.nnf_dialog_folder_name;
    }

    @Override
    protected int getTitle() {
        return R.string.nnf_new_file;
    }

    @Override
    protected boolean isFile() {
        return true;
    }
}
