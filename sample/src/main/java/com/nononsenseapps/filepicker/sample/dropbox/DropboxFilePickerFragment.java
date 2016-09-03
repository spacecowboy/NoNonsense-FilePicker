/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.nononsenseapps.filepicker.sample.dropbox;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.util.SortedList;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.nononsenseapps.filepicker.AbstractFilePickerFragment;
import com.nononsenseapps.filepicker.FileItemAdapter;
import com.nononsenseapps.filepicker.sample.R;

import java.io.File;

@SuppressLint("ValidFragment")
public class DropboxFilePickerFragment
        extends AbstractFilePickerFragment<DropboxAPI.Entry> {

    private final DropboxAPI<AndroidAuthSession> dbApi;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;

    @SuppressLint("ValidFragment")
    public DropboxFilePickerFragment(final DropboxAPI<AndroidAuthSession> api) {
        super();
        if (api == null) {
            throw new NullPointerException("FileSystem may not be null");
        } else if (!api.getSession().isLinked()) {
            throw new IllegalArgumentException("Must be linked with Dropbox");
        }

        this.dbApi = api;
    }

    @Override
    protected View inflateRootView(LayoutInflater inflater, ViewGroup container) {
        // Load the specific layout we created for dropbox/ftp
        View view = inflater.inflate(R.layout.fragment_loading_filepicker, container, false);
        // And bind the progress bar
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        return view;
    }

    /**
     * If we are loading, then hide the list and show the progress bar instead.
     *
     * @param nextPath path to list files for
     */
    @Override
    protected void refresh(DropboxAPI.Entry nextPath) {
        super.refresh(nextPath);
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Once loading has finished, show the list and hide the progress bar.
     */
    @Override
    public void onLoadFinished(Loader<SortedList<DropboxAPI.Entry>> loader, SortedList<DropboxAPI
            .Entry> data) {
        progressBar.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        super.onLoadFinished(loader, data);
    }

    /**
     * Once loading has finished, show the list and hide the progress bar.
     */
    @Override
    public void onLoaderReset(Loader<SortedList<DropboxAPI.Entry>> loader) {
        progressBar.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        super.onLoaderReset(loader);
    }

    @Override
    public void onNewFolder(@NonNull final String name) {
        File folder = new File(mCurrentPath.path, name);
        new FolderCreator().execute(folder.getPath());
    }

    @Override
    public boolean isDir(@NonNull final DropboxAPI.Entry file) {
        return file.isDir;
    }

    @NonNull
    @Override
    public DropboxAPI.Entry getParent(@NonNull final DropboxAPI.Entry from) {
        // Take care of a slight limitation in Dropbox code:
        if (from.path.length() > 1 && from.path.endsWith("/")) {
            from.path = from.path.substring(0, from.path.length() - 1);
        }
        String parent = from.parentPath();
        if (TextUtils.isEmpty(parent)) {
            parent = "/";
        }

        return getPath(parent);

    }

    @NonNull
    @Override
    public DropboxAPI.Entry getPath(@NonNull final String path) {
        final DropboxAPI.Entry entry = new DropboxAPI.Entry();
        entry.path = path;
        entry.isDir = true;
        return entry;

    }

    @NonNull
    @Override
    public String getFullPath(@NonNull final DropboxAPI.Entry file) {
        return file.path;
    }

    @NonNull
    @Override
    public String getName(@NonNull final DropboxAPI.Entry file) {
        return file.fileName();
    }

    @NonNull
    @Override
    public DropboxAPI.Entry getRoot() {
        return getPath("/");
    }

    @NonNull
    @Override
    public Uri toUri(@NonNull final DropboxAPI.Entry file) {
        return new Uri.Builder().scheme("dropbox").authority("").path(file.path).build();
    }

    @NonNull
    @Override
    public Loader<SortedList<DropboxAPI.Entry>> getLoader() {
        return new AsyncTaskLoader<SortedList<DropboxAPI.Entry>>(getActivity()) {

            @Override
            public SortedList<DropboxAPI.Entry> loadInBackground() {
                SortedList<DropboxAPI.Entry> files = new SortedList<>(DropboxAPI.Entry.class,
                        new SortedListAdapterCallback<DropboxAPI.Entry>(null) {
                            @Override
                            public int compare(DropboxAPI.Entry lhs, DropboxAPI.Entry rhs) {
                                if (isDir(lhs) && !isDir(rhs)) {
                                    return -1;
                                } else if (isDir(rhs) && !isDir(lhs)) {
                                    return 1;
                                } else {
                                    return lhs.fileName().toLowerCase()
                                            .compareTo(rhs.fileName().toLowerCase());
                                }
                            }

                            @Override
                            public void onInserted(int position, int count) {
                                // Ignore (DO NOT MODIFY ADAPTER HERE!)
                            }

                            @Override
                            public void onRemoved(int position, int count) {
                                // Ignore (DO NOT MODIFY ADAPTER HERE!)
                            }

                            @Override
                            public void onMoved(int fromPosition, int toPosition) {
                                // Ignore (DO NOT MODIFY ADAPTER HERE!)
                            }

                            @Override
                            public void onChanged(int position, int count) {
                                // Ignore (DO NOT MODIFY ADAPTER HERE!)
                            }

                            @Override
                            public boolean areContentsTheSame(DropboxAPI.Entry lhs, DropboxAPI.Entry rhs) {
                                return lhs.fileName().equals(rhs.fileName()) && (lhs.isDir == rhs.isDir);
                            }

                            @Override
                            public boolean areItemsTheSame(DropboxAPI.Entry lhs, DropboxAPI.Entry rhs) {
                                return areContentsTheSame(lhs, rhs);
                            }
                        }, 0);

                try {

                    if (!dbApi.metadata(mCurrentPath.path, 1, null, false,
                            null).isDir) {
                        mCurrentPath = getRoot();
                    }

                    DropboxAPI.Entry dirEntry =
                            dbApi.metadata(mCurrentPath.path, 0, null, true,
                                    null);

                    files.beginBatchedUpdates();

                    for (DropboxAPI.Entry entry : dirEntry.contents) {
                        if ((mode == MODE_FILE || mode == MODE_FILE_AND_DIR) ||
                                entry.isDir) {
                            files.add(entry);
                        }
                    }

                    files.endBatchedUpdates();
                } catch (DropboxException ignored) {
                }

                return files;
            }

            /**
             * Handles a request to start the Loader.
             */
            @Override
            protected void onStartLoading() {
                super.onStartLoading();

                if (mCurrentPath == null || !mCurrentPath.isDir) {
                    mCurrentPath = getRoot();
                }

                forceLoad();
            }

            /**
             * Handles a request to completely reset the Loader.
             */
            @Override
            protected void onReset() {
                super.onReset();
            }
        };
    }

    /**
     * Dropbox requires stuff to be done in a background thread. Refreshing has to be done on the
     * UI thread however (it restarts the loader so actual work is done in the background).
     */
    private class FolderCreator extends AsyncTask<String, Void, DropboxAPI.Entry> {
        @Override
        protected void onPreExecute() {
            // Switch to progress bar before starting work
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        }

        @Override
        protected DropboxAPI.Entry doInBackground(final String... paths) {
            if (paths.length == 0) {
                return null;
            }

            String path = paths[0];
            try {
                dbApi.createFolder(path);
                return dbApi.metadata(path, 1, null, false, null);
            } catch (DropboxException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(@Nullable DropboxAPI.Entry path) {
            if (path != null) {
                goToDir(path);
            } else {
                progressBar.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(), R.string.nnf_create_folder_error,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
