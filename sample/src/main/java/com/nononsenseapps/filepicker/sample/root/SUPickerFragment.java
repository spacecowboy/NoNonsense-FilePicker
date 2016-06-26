package com.nononsenseapps.filepicker.sample.root;

import android.support.annotation.NonNull;
import android.util.Log;

import com.nononsenseapps.filepicker.FilePickerFragment;

import java.io.File;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;

/**
 * An example picker which calls out to LibSU to get Root-permissions to view otherwise hidden files.
 */
public class SUPickerFragment extends FilePickerFragment {

    @Override
    protected boolean hasPermission(@NonNull File path) {
        // Return the combination of normal file permissions and SU permissions
        return super.hasPermission(path) & (!needSUPermission(path) | hasSUPermission());
    }

    @Override
    protected void handlePermission(@NonNull File path) {
        // Only call super if we don't have normal file permissions
        if (!super.hasPermission(path)) {
            super.handlePermission(path);
        }
        // Only if we need SU permissions
        if (needSUPermission(path) && !hasSUPermission()) {
            handleSUPermission();
        }
    }

    private boolean haveReadPermission(@NonNull File file) {
        List<String> result =
                Shell.SH.run("test -r " + file.getAbsolutePath() + " && echo \"rootsuccess\"");
        return result != null && !result.isEmpty() && "rootsuccess".equals(result.get(0));
    }

    private boolean needSUPermission(@NonNull File path) {
        return !haveReadPermission(path);
    }

    private boolean isSUAvailable() {
        return Shell.SU.available();
    }

    private boolean hasSUPermission() {
        if (isSUAvailable()) {
            List<String> result = Shell.SU.run("ls -l /");
            if (result != null && !result.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private void handleSUPermission() {
        if (isSUAvailable()) {
            // request
            String suVersion = Shell.SU.version(false);
            String suVersionInternal = Shell.SU.version(true);
            Log.d("libsuperuser: ", "suVersion:"+suVersion+" suVersionInternal:"+suVersionInternal);
        } else {
            // Notify that no root access available
            SUErrorFragment.showDialog(getFragmentManager());
        }
    }
}
