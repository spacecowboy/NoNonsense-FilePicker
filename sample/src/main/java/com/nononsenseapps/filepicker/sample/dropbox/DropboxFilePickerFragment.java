/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.nononsenseapps.filepicker.sample.dropbox;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.util.SortedList;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.CreateFolderResult;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.nononsenseapps.filepicker.AbstractFilePickerFragment;
import com.nononsenseapps.filepicker.sample.R;

import java.io.File;
import java.util.List;

@SuppressLint("ValidFragment")
public class DropboxFilePickerFragment extends AbstractFilePickerFragment<Metadata> {
    private static final String TAG = "DbxFilePickerFragment";
    private final DbxClientV2 dropboxClient;
    private ProgressBar progressBar;

    @SuppressLint("ValidFragment")
    public DropboxFilePickerFragment(final DbxClientV2 api) {
        super();
        if (api == null) {
            throw new IllegalArgumentException("Must be authenticated with Dropbox");
        }

        this.dropboxClient = api;
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
    protected void refresh(@NonNull Metadata nextPath) {
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
    public void onLoadFinished(Loader<SortedList<Metadata>> loader, SortedList<Metadata> data) {
        progressBar.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        super.onLoadFinished(loader, data);
    }

    /**
     * Once loading has finished, show the list and hide the progress bar.
     */
    @Override
    public void onLoaderReset(Loader<SortedList<Metadata>> loader) {
        progressBar.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        super.onLoaderReset(loader);
    }

    @Override
    public void onNewFolder(@NonNull final String name) {
        File folder = new File(mCurrentPath.getPathDisplay(), name);
        new FolderCreator().execute(folder.getPath());
    }

    @Override
    public boolean isDir(@NonNull final Metadata file) {
        return file instanceof FolderMetadata;
    }

    @NonNull
    @Override
    public Metadata getParent(@NonNull final Metadata from) {
        String fromPath = from.getPathLower();
        int lastSeparatorIndex = from.getPathLower().lastIndexOf('/');

        String parentPath = "";

        if (lastSeparatorIndex > 0) {
            parentPath = fromPath.substring(0, lastSeparatorIndex);
        }

        return getPath(parentPath);
    }

    @NonNull
    @Override
    public Metadata getPath(@NonNull String path) {
        return FolderMetadata.newBuilder(path, "id")
                .withPathLower(path)
                .build();
    }

    @NonNull
    @Override
    public String getFullPath(@NonNull final Metadata file) {
        return file.getPathDisplay();
    }

    @NonNull
    @Override
    public String getName(@NonNull final Metadata file) {
        return file.getName();
    }

    @NonNull
    @Override
    public Metadata getRoot() {
        return getPath("");
    }

    @NonNull
    @Override
    public Uri toUri(@NonNull final Metadata file) {
        return new Uri.Builder().scheme("dropbox").authority("").path(file.getPathDisplay()).build();
    }

    @NonNull
    @Override
    public Loader<SortedList<Metadata>> getLoader() {
        return new AsyncTaskLoader<SortedList<Metadata>>(getActivity()) {

            @Override
            public SortedList<Metadata> loadInBackground() {
                SortedList<Metadata> files = new SortedList<>(Metadata.class,
                        new SortedListAdapterCallback<Metadata>(null) {
                            @Override
                            public int compare(Metadata lhs, Metadata rhs) {
                                if (isDir(lhs) && !isDir(rhs)) {
                                    return -1;
                                } else if (isDir(rhs) && !isDir(lhs)) {
                                    return 1;
                                } else {
                                    return lhs.getPathLower().compareTo(rhs.getPathLower());
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
                            public boolean areContentsTheSame(Metadata lhs, Metadata rhs) {
                                return lhs.getName().equals(rhs.getName()) &&
                                        (lhs.getClass().equals(rhs.getClass()));
                            }

                            @Override
                            public boolean areItemsTheSame(Metadata lhs, Metadata rhs) {
                                return areContentsTheSame(lhs, rhs);
                            }
                        }, 0);

                try {
                    if (!(mCurrentPath instanceof FolderMetadata)) {
                        mCurrentPath = getRoot();
                    }

                    files.beginBatchedUpdates();

                    String pathToList = mCurrentPath.getPathLower();
                    ListFolderResult listDirResult = dropboxClient.files().listFolder(pathToList);
                    List<Metadata> dirContents = listDirResult.getEntries();

                    for (Metadata entry : dirContents) {
                        if ((mode == MODE_FILE || mode == MODE_FILE_AND_DIR) ||
                                entry instanceof FolderMetadata) {
                            files.add(entry);
                        }
                    }

                    files.endBatchedUpdates();
                } catch (DbxException ignored) {
                    Log.d(TAG, "Failed to list Dropbox folder", ignored);
                    ignored.getMessage();
                }

                return files;
            }

            /**
             * Handles a request to start the Loader.
             */
            @Override
            protected void onStartLoading() {
                super.onStartLoading();

                if (mCurrentPath == null || !(mCurrentPath instanceof FolderMetadata)) {
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
    private class FolderCreator extends AsyncTask<String, Void, Metadata> {
        @Override
        protected void onPreExecute() {
            // Switch to progress bar before starting work
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        }

        @Override
        protected Metadata doInBackground(final String... paths) {
            if (paths.length == 0) {
                return null;
            }

            String path = paths[0];
            try {
                CreateFolderResult createFolderResult =  dropboxClient.files().createFolderV2(path);
                return createFolderResult.getMetadata();
            } catch (DbxException e) {
                Log.d(TAG, getString(R.string.nnf_create_folder_error), e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(@Nullable Metadata path) {
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
