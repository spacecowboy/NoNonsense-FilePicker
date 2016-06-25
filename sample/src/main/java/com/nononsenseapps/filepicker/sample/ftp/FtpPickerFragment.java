/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.nononsenseapps.filepicker.sample.ftp;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.util.SortedList;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.util.Log;
import android.widget.Toast;

import com.nononsenseapps.filepicker.AbstractFilePickerFragment;
import com.nononsenseapps.filepicker.sample.R;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.IOException;

/**
 * This example allows you to browse the files on an FTP-server
 */
public class FtpPickerFragment extends AbstractFilePickerFragment<FtpFile> {

    private static final String KEY_FTP_SERVER = "KEY_FTP_SERVER";
    private static final String KEY_FTP_PORT = "KEY_FTP_PORT";
    private static final String KEY_FTP_USERNAME = "KEY_FTP_USERNAME";
    private static final String KEY_FTP_PASSWORD = "KEY_FTP_PASSWORD";
    private static final String KEY_FTP_ROOTDIR = "KEY_FTP_ROOTDIR";
    private static final String TAG = "NoNonsenseFtp";
    private final FTPClient ftp;
    private String server;
    private int port;
    private String username;
    private String password;
    private boolean loggedIn = false;
    private String rootDir = "/";

    public FtpPickerFragment() {
        super();
        ftp = new FTPClient();
    }

    public static AbstractFilePickerFragment<FtpFile> newInstance(String startPath, int mode,
                                                                  boolean allowMultiple,
                                                                  boolean allowCreateDir,
                                                                  boolean singleClick,
                                                                  String server, int port,
                                                                  String username,
                                                                  String password,
                                                                  String rootDir) {
        FtpPickerFragment fragment = new FtpPickerFragment();
        // Add arguments
        fragment.setArgs(startPath, mode, allowMultiple, allowCreateDir, singleClick);
        Bundle args = fragment.getArguments();

        // Add ftp related stuff
        args.putString(KEY_FTP_ROOTDIR, rootDir);
        args.putString(KEY_FTP_SERVER, server);
        args.putInt(KEY_FTP_PORT, port);
        if (username != null && password != null) {
            args.putString(KEY_FTP_USERNAME, username);
            args.putString(KEY_FTP_PASSWORD, password);
        }

        return fragment;
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        Bundle args = getArguments();
        this.server = args.getString(KEY_FTP_SERVER);
        this.port = args.getInt(KEY_FTP_PORT);
        this.username = args.getString(KEY_FTP_USERNAME) != null ? args.getString(KEY_FTP_USERNAME) : "anonymous";
        this.password = args.getString(KEY_FTP_PASSWORD) != null ? args.getString(KEY_FTP_PASSWORD) : "anonymous";
        this.rootDir = args.getString(KEY_FTP_ROOTDIR) != null ? args.getString(KEY_FTP_ROOTDIR) : "/";
    }

    /**
     * Return true if the path is a directory and not a file.
     */
    @Override
    public boolean isDir(@NonNull FtpFile path) {
        return path.isDirectory();
    }

    /**
     * @return filename of path
     */
    @NonNull
    @Override
    public String getName(@NonNull FtpFile path) {
        return path.getName();
    }

    /**
     * Convert the path to a URI for the return intent
     *
     * @return a Uri
     */
    @NonNull
    @Override
    public Uri toUri(@NonNull FtpFile path) {
        String user = "";
        if (!username.isEmpty()) {
            user = username;
            if (!password.isEmpty()) {
                user += ":" + password;
            }
            user += "@";
        }
        return Uri.parse("ftp://" + user + server + ":" + port + path.getPath());

    }

    /**
     * Return the path to the parent directory. Should return the root if
     * from is root.
     */
    @NonNull
    @Override
    public FtpFile getParent(@NonNull FtpFile from) {
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
     * @return the full path to the file
     */
    @NonNull
    @Override
    public String getFullPath(@NonNull FtpFile path) {
        return path.getPath();
    }

    /**
     * Convert the path to the type used.
     */
    @NonNull
    @Override
    public FtpFile getPath(@NonNull String path) {
        return new FtpFile(path);
    }

    /**
     * Get the root path (lowest allowed).
     */
    @NonNull
    @Override
    public FtpFile getRoot() {
        return new FtpDir(rootDir);
    }

    @Override
    public void onDestroy() {
        if (ftp.isConnected()) {
            try {
                ftp.disconnect();
            } catch (IOException ignored) {
            }
        }
        super.onDestroy();
    }

    /**
     * Get a loader that lists the files in the current path,
     * and monitors changes.
     */
    @NonNull
    @Override
    public Loader<SortedList<FtpFile>> getLoader() {
        return new AsyncTaskLoader<SortedList<FtpFile>>(getContext()) {
            @Override
            public SortedList<FtpFile> loadInBackground() {
                SortedList<FtpFile> sortedList = new SortedList<>(FtpFile.class, new SortedListAdapterCallback<FtpFile>(getDummyAdapter()) {
                    @Override
                    public int compare(FtpFile lhs, FtpFile rhs) {
                        if (lhs.isDirectory() && !rhs.isDirectory()) {
                            return -1;
                        } else if (rhs.isDirectory() && !lhs.isDirectory()) {
                            return 1;
                        } else {
                            return lhs.getName().compareToIgnoreCase(rhs.getName());
                        }
                    }

                    @Override
                    public boolean areContentsTheSame(FtpFile oldItem, FtpFile newItem) {
                        return oldItem.getName().equals(newItem.getName());
                    }

                    @Override
                    public boolean areItemsTheSame(FtpFile item1, FtpFile item2) {
                        return item1.getName().equals(item2.getName());
                    }
                });


                if (!ftp.isConnected()) {
                    // Connect
                    try {
                        ftp.connect(server, port);

                        ftp.setFileType(FTP.ASCII_FILE_TYPE);
                        ftp.enterLocalPassiveMode();
                        ftp.setUseEPSVwithIPv4(false);

                        if (!(loggedIn = ftp.login(username, password))) {
                            ftp.logout();
                            Log.e(TAG, "Login failed");
                        }
                    } catch (IOException e) {
                        if (ftp.isConnected()) {
                            try {
                                ftp.disconnect();
                            } catch (IOException ignored) {
                            }
                        }
                        Log.e(TAG, "Could not connect to server.");
                    }
                }

                if (loggedIn) {
                    try {
                        // handle if directory does not exist. Fall back to root.
                        if (mCurrentPath == null || !mCurrentPath.isDirectory()) {
                            mCurrentPath = getRoot();
                        }

                        sortedList.beginBatchedUpdates();
                        for (FTPFile f : ftp.listFiles(mCurrentPath.getPath())) {
                            FtpFile file;
                            if (f.isDirectory()) {
                                file = new FtpDir(mCurrentPath, f.getName());
                            } else {
                                file = new FtpFile(mCurrentPath, f.getName());
                            }
                            if (isItemVisible(file)) {
                                sortedList.add(file);
                            }
                        }
                        sortedList.endBatchedUpdates();
                    } catch (IOException e) {
                        Log.e(TAG, "IOException: " + e.getMessage());
                    }
                }

                return sortedList;
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

                forceLoad();
            }
        };
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
    protected boolean isItemVisible(final FtpFile file) {
        return file.isDirectory() || (mode == MODE_FILE || mode == MODE_FILE_AND_DIR);
    }

    /**
     * Name is validated to be non-null, non-empty and not containing any
     * slashes.
     *
     * @param name The name of the folder the user wishes to create.
     */
    @Override
    public void onNewFolder(@NonNull String name) {
        AsyncTask<String, Void, FtpFile> task = new AsyncTask<String, Void, FtpFile>() {

            @Override
            protected FtpFile doInBackground(String... names) {
                FtpFile result = null;
                if (names.length > 0) {
                    result = onNewFolderAsync(names[0]);
                }
                return result;
            }

            @Override
            protected void onPostExecute(FtpFile folder) {
                if (folder != null) {
                    refresh(folder);
                } else {
                    Toast.makeText(getContext(), R.string.nnf_create_folder_error, Toast.LENGTH_SHORT).show();
                }
            }
        };
        task.execute(name);
    }

    /**
     * @param name The name of the folder the user wishes to create.
     */
    public FtpFile onNewFolderAsync(String name) {
        FtpDir folder = new FtpDir(mCurrentPath, name);
        try {
            if (ftp.makeDirectory(folder.getPath())) {
                // Success, return result
                return folder;
            }
        } catch (IOException e) {
            Log.e(TAG, "IO Exception: " + folder.getPath());
        }
        return null;
    }
}
