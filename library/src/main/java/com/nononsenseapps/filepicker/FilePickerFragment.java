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

import android.content.AsyncTaskLoader;
import android.content.Loader;
import android.net.Uri;
import android.os.Environment;
import android.os.FileObserver;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FilePickerFragment extends AbstractFilePickerFragment<File> {

    /**
     * Return true if the path is a directory and not a file.
     *
     * @param path
     */
    @Override
    protected boolean isDir(final File path) {
        return path.isDirectory();
    }

    /**
     * Return the path to the parent directory. Should return the root if
     * from is root.
     *
     * @param from
     */
    @Override
    protected File getParent(final File from) {
        if (from.getParentFile() != null) {
            if (from.isFile()) {
                return getParent(from.getParentFile());
            } else {
                return from.getParentFile();
            }
        } else {
            return from;
        }
    }

    /**
     * Convert the path to the type used.
     *
     * @param path
     */
    @Override
    protected File getPath(final String path) {
        return new File(path);
    }

    /**
     * @param path
     * @return the full path to the file
     */
    @Override
    protected String getFullPath(final File path) {
        return path.getPath();
    }

    /**
     * @param path
     * @return the name of this file/folder
     */
    @Override
    protected String getName(final File path) {
        return path.getName();
    }

    /**
     * Get the root path (lowest allowed).
     */
    @Override
    protected File getRoot() {
        return Environment.getExternalStorageDirectory();
    }

    /**
     * Convert the path to a URI for the return intent
     * @param file
     * @return
     */
    @Override
    protected Uri toUri(final File file) {
        return Uri.fromFile(file);
    }

    /**
     * @return a comparator that can sort the items alphabetically
     */
    @Override
    protected Comparator<File> getComparator() {
        return new Comparator<File>() {
            @Override
            public int compare(final File lhs, final File rhs) {
                if (lhs.isDirectory() && !rhs.isDirectory()) {
                    return -1;
                } else if (rhs.isDirectory() && !lhs.isDirectory()) {
                    return 1;
                } else {
                    return lhs.getName().toLowerCase().compareTo(rhs.getName()
                            .toLowerCase());
                }
            }
        };
    }

    /**
     * Get a loader that lists the Files in the current path,
     * and monitors changes.
     */
    @Override
    protected Loader<List<File>> getLoader() {
        return new AsyncTaskLoader<List<File>>(getActivity()) {

            FileObserver fileObserver;

            @Override
            public List<File> loadInBackground() {
                ArrayList<File> files = new ArrayList<File>();
                for (java.io.File f : currentPath.listFiles()) {
                    if ((mode == MODE_FILE || mode == MODE_FILE_AND_DIR)
                            || f.isDirectory()) {
                        files.add(f);
                    }
                }
                return files;
            }

            /**
             * Handles a request to start the Loader.
             */
            @Override
            protected void onStartLoading() {
                super.onStartLoading();

                // handle if directory does not exist. Fall back to root.
                if (currentPath == null || !currentPath.isDirectory()) {
                    currentPath = getRoot();
                }

                // Start watching for changes
                fileObserver = new FileObserver(currentPath.getPath(),
                        FileObserver.CREATE |
                                FileObserver.DELETE
                                | FileObserver.MOVED_FROM | FileObserver.MOVED_TO
                ) {

                    @Override
                    public void onEvent(int event, String path) {
                        // Reload
                        onContentChanged();
                    }
                };
                fileObserver.startWatching();

                forceLoad();
            }

            /**
             * Handles a request to completely reset the Loader.
             */
            @Override
            protected void onReset() {
                super.onReset();

                // Stop watching
                if (fileObserver != null) {
                    fileObserver.stopWatching();
                    fileObserver = null;
                }
            }
        };
    }

    /**
     * Name is validated to be non-null, non-empty and not containing any
     * slashes.
     *
     * @param name The name of the folder the user wishes to create.
     */
    @Override
    public void onNewFolder(final String name) {
        File folder = new File(currentPath, name);

        if (folder.mkdir()) {
            currentPath = folder;
            refresh();
        } else {
            Toast.makeText(getActivity(), R.string.create_folder_error,
                    Toast.LENGTH_SHORT).show();
        }
    }
}
