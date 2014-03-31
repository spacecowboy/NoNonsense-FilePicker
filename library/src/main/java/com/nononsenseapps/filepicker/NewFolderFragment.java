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
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class NewFolderFragment extends DialogFragment {

    private static final String TAG = "new_folder_fragment";
    private String folderName = null;
    private View okButton = null;
    private OnNewFolderListener listener = null;

    public NewFolderFragment() {
    }

    public static void showDialog(final FragmentManager fm, final OnNewFolderListener listener) {
        NewFolderFragment d = new NewFolderFragment();
        d.setListener(listener);
        d.show(fm, TAG);
    }

    public void setListener(final OnNewFolderListener listener) {
        this.listener = listener;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle(R.string.new_folder);

        final View view = inflater.inflate(R.layout.dialog_new_folder, null);

        okButton = view.findViewById(R.id.button_ok);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (listener != null) {
                    listener.onNewFolder(folderName);
                }
                dismiss();
            }
        });

        view.findViewById(R.id.button_cancel).setOnClickListener(new View
                .OnClickListener() {
            @Override
            public void onClick(final View v) {
                dismiss();
            }
        });

        final EditText editText = (EditText) view.findViewById(R.id.edit_text);
        if (folderName == null) {
            okButton.setEnabled(false);
        } else {
            editText.setText(folderName);
            validateFolderName();
        }

        editText.addTextChangedListener
                (new TextWatcher() {
                    @Override
                    public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
                    }

                    @Override
                    public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                    }

                    @Override
                    public void afterTextChanged(final Editable s) {
                        folderName = s.toString();
                        validateFolderName();
                    }
                });

        return view;
    }

    private void validateFolderName() {
        if (okButton != null) {
            okButton.setEnabled(folderName != null && !folderName.isEmpty()
                    && !folderName.contains("/"));
        }
    }

    public interface OnNewFolderListener {
        /**
         * Name is validated to be non-null, non-empty and not containing any
         * slashes.
         *
         * @param name The name of the folder the user wishes to create.
         */
        public void onNewFolder(final String name);
    }
}
