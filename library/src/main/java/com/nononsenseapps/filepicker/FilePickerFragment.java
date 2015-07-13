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

import android.net.Uri;
import android.os.FileObserver;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.util.SortedList;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.widget.Toast;

import java.io.File;

/**
 * An implementation of the picker which allows you to select a file from the internal/external
 * storage (SD-card) on a device.
 */
public class FilePickerFragment extends AbstractFilePickerFragment<File> {

    public FilePickerFragment() {
    }

    /**
     * Return true if the path is a directory and not a file.
     *
     * @param path either a file or directory
     * @return true if path is a directory, false if file
     */
    @Override
    public boolean isDir(final File path) {
        return path.isDirectory();
    }

    /**
     * @param path either a file or directory
     * @return filename of path
     */
    @Override
    public String getName(File path) {
        return path.getName();
    }

    /**
     * Return the path to the parent directory. Should return the root if
     * from is root.
     *
     * @param from either a file or directory
     * @return the parent directory
     */
    @Override
    public File getParent(final File from) {
        if (from.getPath().equals(getRoot().getPath())) {
            // Already at root, we can't go higher
            return from;
        } else if (from.getParentFile() != null) {
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
     * @param path either a file or directory
     * @return File representation of the string path
     */
    @Override
    public File getPath(final String path) {
        return new File(path);
    }

    /**
     * @param path either a file or directory
     * @return the full path to the file
     */
    @Override
    public String getFullPath(final File path) {
        return path.getPath();
    }

    /**
     * Get the root path.
     *
     * @return the highest allowed path, which is "/" by default
     */
    @Override
    public File getRoot() {
        return new File("/");
    }

    /**
     * Convert the path to a URI for the return intent
     *
     * @param file either a file or directory
     * @return a Uri
     */
    @Override
    public Uri toUri(final File file) {
        return Uri.fromFile(file);
    }

    /**
     * Get a loader that lists the Files in the current path,
     * and monitors changes.
     */
    @Override
    public Loader<SortedList<File>> getLoader() {
        return new AsyncTaskLoader<SortedList<File>>(getActivity()) {

            FileObserver fileObserver;

            @Override
            public SortedList<File> loadInBackground() {
                File[] listFiles = mCurrentPath.listFiles();
                final int initCap = listFiles == null ? 0 : listFiles.length;

                SortedList<File> files = new SortedList<>(File.class, new SortedListAdapterCallback<File>(getDummyAdapter()) {
                    @Override
                    public int compare(File lhs, File rhs) {
                        return compareFiles(lhs, rhs);
                    }

                    @Override
                    public boolean areContentsTheSame(File file, File file2) {
                        return file.getAbsolutePath().equals(file2.getAbsolutePath()) && (file.isFile() == file2.isFile());
                    }

                    @Override
                    public boolean areItemsTheSame(File file, File file2) {
                        return areContentsTheSame(file, file2);
                    }
                }, initCap);


                files.beginBatchedUpdates();
                if (listFiles != null) {
                    for (java.io.File f : listFiles) {
                        if (isItemVisible(f)) {
                            files.add(f);
                        }
                    }
                }
                files.endBatchedUpdates();

                return files;
            }

            /**
             * Handles a request to start the Loader.
             */
            @Override
            protected void onStartLoading() {
                super.onStartLoading();

                // handle if directory does not exist. Fall back to root.
                if (mCurrentPath == null || !mCurrentPath.isDirectory()) {
                    mCurrentPath = getRoot();
                }

                // Start watching for changes
                fileObserver = new FileObserver(mCurrentPath.getPath(),
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
        File folder = new File(mCurrentPath, name);

        if (folder.mkdir()) {
            mCurrentPath = folder;
            refresh();
        } else {
            Toast.makeText(getActivity(), R.string.nnf_create_folder_error,
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Used by the list to determine whether a file should be displayed or not.
     * Default behavior is to always display folders. If files can be selected,
     * then files are also displayed. Override this method to enable other
     * filtering behaviour, like only displaying files with specific extensions (.zip, .txt, etc).
     *
     * @param file to maybe add. Can be either a directory or file.
     * @return True if item should be added to the list, false otherwise
     */
    protected boolean isItemVisible(final File file) {
        return isDir(file) || (mode == MODE_FILE || mode == MODE_FILE_AND_DIR);
    }

    /**
     * Compare two files to determine their relative sort order. This follows the usual
     * comparison interface. Override to determine your own custom sort order.
     *
     * Default behaviour is to place directories before files, but sort them alphabetically
     * otherwise.
     *
     * @param lhs File on the "left-hand side"
     * @param rhs File on the "right-hand side"
     * @return -1 if if lhs should be placed before rhs, 0 if they are equal,
     * and 1 if rhs should be placed before lhs
     */
    protected int compareFiles(File lhs, File rhs) {
        if (lhs.isDirectory() && !rhs.isDirectory()) {
            return -1;
        } else if (rhs.isDirectory() && !lhs.isDirectory()) {
            return 1;
        } else {
            return lhs.getName().compareToIgnoreCase(rhs.getName());
        }
    }
}
