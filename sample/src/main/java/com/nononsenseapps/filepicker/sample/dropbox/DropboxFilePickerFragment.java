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
    private FolderCreator folderCreator;
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

    /**
     * Copy paste of normal onCreateView, except we load a different layout, and bind the progress
     * bar which was added to it.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Load the specific layout we created for dropbox/ftp
        View view = inflater.inflate(R.layout.fragment_loading_filepicker, container, false);

        // And bind the progress bar
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        Toolbar toolbar =
                (Toolbar) view.findViewById(com.nononsenseapps.filepicker.R.id.nnf_picker_toolbar);
        if (toolbar != null) {
            setupToolbar(toolbar);
        }

        recyclerView = (RecyclerView) view.findViewById(android.R.id.list);
        // improve performance if you know that changes in content
        // do not change the size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        // Set adapter
        mAdapter = new FileItemAdapter<>(this);
        recyclerView.setAdapter(mAdapter);

        view.findViewById(com.nononsenseapps.filepicker.R.id.nnf_button_cancel)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        onClickCancel(v);
                    }
                });

        view.findViewById(com.nononsenseapps.filepicker.R.id.nnf_button_ok)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        onClickOk(v);
                    }
                });

        mCurrentDirView = (TextView) view.findViewById(com.nononsenseapps.filepicker.R.id.nnf_current_dir);
        // Restore state
        if (mCurrentPath != null && mCurrentDirView != null) {
            mCurrentDirView.setText(getFullPath(mCurrentPath));
        }

        return view;
    }

    /**
     * If we are loading, then hide the list and show the progress bar instead.
     */
    @Override
    protected void refresh() {
        super.refresh();
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
    public void onNewFolder(final String name) {
        File folder = new File(mCurrentPath.path, name);

        if (folderCreator == null) {
            folderCreator = new FolderCreator();
        }

        folderCreator.execute(folder.getPath());
    }

    @Override
    public boolean isDir(final DropboxAPI.Entry file) {
        return file.isDir;
    }

    @Override
    public DropboxAPI.Entry getParent(final DropboxAPI.Entry from) {
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

    @Override
    public DropboxAPI.Entry getPath(final String path) {
        final DropboxAPI.Entry entry = new DropboxAPI.Entry();
        entry.path = path;
        entry.isDir = true;
        return entry;

    }

    @Override
    public String getFullPath(final DropboxAPI.Entry file) {
        return file.path;
    }

    @Override
    public String getName(final DropboxAPI.Entry file) {
        return file.fileName();
    }

    @Override
    public DropboxAPI.Entry getRoot() {
        return getPath("/");
    }

    @Override
    public Uri toUri(final DropboxAPI.Entry file) {
        return new Uri.Builder().scheme("dropbox").authority("").path(file.path).build();
    }

    @Override
    public Loader<SortedList<DropboxAPI.Entry>> getLoader() {
        return new AsyncTaskLoader<SortedList<DropboxAPI.Entry>>(getActivity()) {

            @Override
            public SortedList<DropboxAPI.Entry> loadInBackground() {
                SortedList<DropboxAPI.Entry> files = new SortedList<>(DropboxAPI.Entry.class,
                        new SortedListAdapterCallback<DropboxAPI.Entry>(getAdapter()) {
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

    private class FolderCreator extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(final String... paths) {
            for (String path : paths) {
                try {
                    dbApi.createFolder(path);
                    mCurrentPath = dbApi.metadata(path, 1, null, false, null);
                    refresh();
                } catch (DropboxException e) {
                    Toast.makeText(getActivity(), R.string.nnf_create_folder_error,
                            Toast.LENGTH_SHORT).show();
                }
            }
            return null;
        }
    }
}