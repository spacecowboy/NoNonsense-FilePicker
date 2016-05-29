package com.nononsenseapps.filepicker.sample.root;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;

/**
 * A dialog which tells the user that no SU binary is available
 */
public class SUErrorFragment extends DialogFragment {

    private static final String TAG = "SUErrorFragment";

    public static void showDialog(@NonNull final FragmentManager fm) {
        SUErrorFragment d = new SUErrorFragment();
        d.show(fm, TAG);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("No read permisson, root unavailable")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }
}
