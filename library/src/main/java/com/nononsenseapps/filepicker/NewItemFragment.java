/*
 * Copyright (c) 2015 Jonas Kalderstam
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.nononsenseapps.filepicker;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public abstract class NewItemFragment extends DialogFragment {

    private OnNewFolderListener listener = null;

    public NewItemFragment() {
        super();
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
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(R.layout.dialog_folder_name)
                .setTitle(R.string.new_folder)
                .setNegativeButton(android.R.string.cancel,
                        null)
                .setPositiveButton(android.R.string.ok,
                        null);

        final AlertDialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog1) {
                final AlertDialog dialog = (AlertDialog) dialog1;
                final EditText editText = (EditText) dialog.findViewById(R.id.edit_text);

                Button cancel = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                cancel.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                    }
                });

                final Button ok = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                // Start disabled
                ok.setEnabled(false);
                ok.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        String itemName = editText.getText().toString();
                        if (validateName(itemName)) {
                            if (listener != null) {
                                listener.onNewFolder(itemName);
                            }
                            dialog.dismiss();
                        }
                    }
                });

                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(final CharSequence s, final int start,
                                                  final int count, final int after) {
                    }

                    @Override
                    public void onTextChanged(final CharSequence s, final int start,
                                              final int before, final int count) {
                    }

                    @Override
                    public void afterTextChanged(final Editable s) {
                        ok.setEnabled(validateName(s.toString()));
                    }
                });
            }
        });


        return dialog;
    }

    protected abstract boolean validateName(final String itemName);

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
