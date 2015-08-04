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
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.SortedList;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * A fragment representing a list of Files.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link
 * OnFilePickedListener}
 * interface.
 */
public abstract class AbstractFilePickerFragment<T> extends Fragment
        implements LoaderManager.LoaderCallbacks<SortedList<T>>,
        NewItemFragment.OnNewFolderListener, LogicHandler<T> {

    // The different preset modes of operation. This impacts the behaviour
    // and possible actions in the UI.
    public static final int MODE_FILE = 0;
    protected int mode = MODE_FILE;
    public static final int MODE_DIR = 1;
    public static final int MODE_FILE_AND_DIR = 2;
    // Where to display on open.
    public static final String KEY_START_PATH = "KEY_START_PATH";
    // See MODE_XXX constants above for possible values
    public static final String KEY_MODE = "KEY_MODE";
    // If it should be possible to create directories.
    public static final String KEY_ALLOW_DIR_CREATE = "KEY_ALLOW_DIR_CREATE";
    // Allow multiple items to be selected.
    public static final String KEY_ALLOW_MULTIPLE = "KEY_ALLOW_MULTIPLE";
    // Used for saving state.
    protected static final String KEY_CURRENT_PATH = "KEY_CURRENT PATH";
    protected final HashSet<T> mCheckedItems;
    protected final HashSet<CheckableViewHolder> mCheckedVisibleViewHolders;
    protected T mCurrentPath = null;
    protected boolean allowCreateDir = false;
    protected boolean allowMultiple = false;
    protected OnFilePickedListener mListener;
    protected FileItemAdapter<T> mAdapter = null;
    protected TextView mCurrentDirView;
    protected SortedList<T> mFiles = null;
    protected Toast mToast = null;

    protected FileItemAdapter<T> getAdapter() {
        return mAdapter;
    }

    protected FileItemAdapter<T> getDummyAdapter() {
        return new FileItemAdapter<>(this);
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AbstractFilePickerFragment() {
        mCheckedItems = new HashSet<>();
        mCheckedVisibleViewHolders = new HashSet<>();

        // Retain this fragment across configuration changes, to allow
        // asynctasks and such to be used with ease.
        setRetainInstance(true);
    }

    /**
     * Set before making the fragment visible. This method will re-use the existing
     * arguments bundle in the fragment if it exists so extra arguments will not
     * be overwritten. This allows you to set any extra arguments in the fragment
     * constructor if you wish.
     *
     * The key/value-pairs listed below will be overwritten however.
     *
     * @param startPath path to directory the picker will show upon start
     * @param mode what is allowed to be selected (dirs, files, both)
     * @param allowMultiple selecting a single item or several?
     * @param allowDirCreate can new directories be created?
     */
    public void setArgs(final String startPath, final int mode,
                        final boolean allowMultiple, final boolean allowDirCreate) {
        // There might have been arguments set elsewhere, if so do not overwrite them.
        Bundle b = getArguments();
        if (b == null) {
            b = new Bundle();
        }

        if (startPath != null) {
            b.putString(KEY_START_PATH, startPath);
        }
        b.putBoolean(KEY_ALLOW_DIR_CREATE, allowDirCreate);
        b.putBoolean(KEY_ALLOW_MULTIPLE, allowMultiple);
        b.putInt(KEY_MODE, mode);
        setArguments(b);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.nnf_fragment_filepicker, container, false);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.nnf_picker_toolbar);
        if (toolbar != null) {
            setupToolbar(toolbar);
        }

        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(android.R.id.list);
        // improve performance if you know that changes in content
        // do not change the size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        // Set adapter
        mAdapter = new FileItemAdapter<>(this);
        mRecyclerView.setAdapter(mAdapter);

        view.findViewById(R.id.nnf_button_cancel)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        if (mListener != null) {
                            mListener.onCancelled();
                        }
                    }
                });

        view.findViewById(R.id.nnf_button_ok)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        if (mListener == null) {
                            return;
                        }

                        // Some invalid cases first
                        if ((allowMultiple || mode == MODE_FILE) && mCheckedItems.isEmpty()) {
                            if (mToast == null) {
                                mToast = Toast.makeText(getActivity(), R.string.nnf_select_something_first,
                                                        Toast.LENGTH_SHORT);
                            }
                            mToast.show();
                            return;
                        }

                        if (allowMultiple) {
                            mListener.onFilesPicked(toUri(mCheckedItems));
                        } else if (mode == MODE_FILE) {
                            mListener.onFilePicked(toUri(getFirstCheckedItem()));
                        } else if (mode == MODE_DIR) {
                            mListener.onFilePicked(toUri(mCurrentPath));
                        } else {
                            // single FILE OR DIR
                            if (mCheckedItems.isEmpty()) {
                                mListener.onFilePicked(toUri(mCurrentPath));
                            } else {
                                mListener.onFilePicked(toUri(getFirstCheckedItem()));
                            }
                        }
                    }
                });

        mCurrentDirView = (TextView) view.findViewById(R.id.nnf_current_dir);
        // Restore state
        if (mCurrentPath != null && mCurrentDirView != null) {
            mCurrentDirView.setText(getFullPath(mCurrentPath));
        }

        return view;
    }

    /**
     * Configure the toolbar anyway you like here. Default is to set it as the activity's
     * main action bar. Override if you already provide an action bar.
     * Not called if no toolbar was found.
     *
     * @param toolbar from layout with id "picker_toolbar"
     */
    protected void setupToolbar(Toolbar toolbar) {
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    }

    public T getFirstCheckedItem() {
        for (T file : mCheckedItems) {
            return file;
        }
        return null;
    }

    protected List<Uri> toUri(Iterable<T> files) {
        ArrayList<Uri> uris = new ArrayList<>();
        for (T file : files) {
            uris.add(toUri(file));
        }
        return uris;
    }

    public boolean isCheckable(final T data) {
        final boolean checkable;
        if (isDir(data)) {
            checkable = ((mode == MODE_DIR && allowMultiple) ||
                    (mode == MODE_FILE_AND_DIR && allowMultiple));
        } else {
            // File
            checkable = (mode != MODE_DIR);
        }
        return checkable;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFilePickedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                    " must implement OnFilePickedListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    /**
     * Called when the fragment's activity has been created and this
     * fragment's view hierarchy instantiated.  It can be used to do final
     * initialization once these pieces are in place, such as retrieving
     * views or restoring state.  It is also useful for fragments that use
     * {@link #setRetainInstance(boolean)} to retain their instance,
     * as this callback tells the fragment when it is fully associated with
     * the new activity instance.  This is called after {@link #onCreateView}
     * and before {@link #onViewStateRestored(Bundle)}.
     *
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Only if we have no state
        if (mCurrentPath == null) {
            if (savedInstanceState != null) {
                mode = savedInstanceState.getInt(KEY_MODE, mode);
                allowCreateDir = savedInstanceState
                        .getBoolean(KEY_ALLOW_DIR_CREATE, allowCreateDir);
                allowMultiple = savedInstanceState
                        .getBoolean(KEY_ALLOW_MULTIPLE, allowMultiple);
                mCurrentPath =
                        getPath(savedInstanceState.getString(KEY_CURRENT_PATH));
            } else if (getArguments() != null) {
                mode = getArguments().getInt(KEY_MODE, mode);
                allowCreateDir = getArguments()
                        .getBoolean(KEY_ALLOW_DIR_CREATE, allowCreateDir);
                allowMultiple = getArguments()
                        .getBoolean(KEY_ALLOW_MULTIPLE, allowMultiple);
                if (getArguments().containsKey(KEY_START_PATH)) {
                    mCurrentPath =
                            getPath(getArguments().getString(KEY_START_PATH));
                }
            }

            // If still null
            if (mCurrentPath == null) {
                mCurrentPath = getRoot();
            }
        }

        refresh();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.picker_actions, menu);

        MenuItem item = menu.findItem(R.id.nnf_action_createdir);
        item.setVisible(allowCreateDir);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (R.id.nnf_action_createdir == menuItem.getItemId()) {
            Activity activity = getActivity();
            if (activity instanceof AppCompatActivity) {
                NewFolderFragment.showDialog(((AppCompatActivity) activity).getSupportFragmentManager(),
                        AbstractFilePickerFragment.this);
            }
            return true;
        } else {
            return false;
        }
    }


    @Override
    public void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);
        b.putString(KEY_CURRENT_PATH, mCurrentPath.toString());
        b.putBoolean(KEY_ALLOW_MULTIPLE, allowMultiple);
        b.putBoolean(KEY_ALLOW_DIR_CREATE, allowCreateDir);
        b.putInt(KEY_MODE, mode);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Refreshes the list. Call this when current path changes.
     */
    protected void refresh() {
        getLoaderManager()
                .restartLoader(0, null, AbstractFilePickerFragment.this);
    }

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id   The ID whose loader is to be created.
     * @param args Any arguments supplied by the caller.
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<SortedList<T>> onCreateLoader(final int id, final Bundle args) {
        return getLoader();
    }

    /**
     * Called when a previously created loader has finished its load.
     *
     * @param loader The Loader that has finished.
     * @param data   The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(final Loader<SortedList<T>> loader,
                               final SortedList<T> data) {
        mCheckedItems.clear();
        mCheckedVisibleViewHolders.clear();
        mFiles = data;
        mAdapter.setList(data);
        if (mCurrentDirView != null) {
            mCurrentDirView.setText(getFullPath(mCurrentPath));
        }
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(final Loader<SortedList<T>> loader) {
        mAdapter.setList(null);
        mFiles = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating
     * .html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFilePickedListener {
        void onFilePicked(Uri file);

        void onFilesPicked(List<Uri> files);

        void onCancelled();
    }

    /**
     * @param position 0 - n, where the header has been subtracted
     * @param data the actual file or directory
     * @return an integer greater than 0
     */
    @Override
    public int getItemViewType(int position, T data) {
        if (isCheckable(data)) {
            return LogicHandler.VIEWTYPE_CHECKABLE;
        } else {
            return LogicHandler.VIEWTYPE_DIR;
        }
    }

    @Override
    public void onBindHeaderViewHolder(HeaderViewHolder viewHolder) {
        viewHolder.text.setText("..");
    }

    /**
     * @param parent Containing view
     * @param viewType which the ViewHolder will contain
     * @return a view holder for a file or directory
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        switch (viewType) {
            case LogicHandler.VIEWTYPE_HEADER:
                v = LayoutInflater.from(getActivity()).inflate(R.layout.nnf_filepicker_listitem_dir,
                        parent, false);
                return new HeaderViewHolder(v);
            case LogicHandler.VIEWTYPE_CHECKABLE:
                v = LayoutInflater.from(getActivity()).inflate(R.layout.nnf_filepicker_listitem_checkable,
                        parent, false);
                return new CheckableViewHolder(v);
            case LogicHandler.VIEWTYPE_DIR:
            default:
                v = LayoutInflater.from(getActivity()).inflate(R.layout.nnf_filepicker_listitem_dir,
                        parent, false);
                return new DirViewHolder(v);
        }
    }

    /**
     * @param vh       to bind data from either a file or directory
     * @param position 0 - n, where the header has been subtracted
     * @param data the file or directory which this item represents
     */
    @Override
    public void onBindViewHolder(DirViewHolder vh, int position, T data) {
        vh.file = data;
        vh.icon.setVisibility(isDir(data) ? View.VISIBLE : View.GONE);
        vh.text.setText(getName(data));

        if (isCheckable(data)) {
            if (mCheckedItems.contains(data)) {
                mCheckedVisibleViewHolders.add((CheckableViewHolder) vh);
                ((CheckableViewHolder) vh).checkbox.setChecked(true);
            } else {
                //noinspection SuspiciousMethodCalls
                mCheckedVisibleViewHolders.remove(vh);
                ((CheckableViewHolder) vh).checkbox.setChecked(false);
            }
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView text;

        public HeaderViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            text = (TextView) v.findViewById(android.R.id.text1);
        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            mCurrentPath = getParent(mCurrentPath);
            mCheckedItems.clear();
            mCheckedVisibleViewHolders.clear();
            refresh();
        }
    }

    public class DirViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public View icon;
        public TextView text;
        public T file;

        public DirViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            v.setOnLongClickListener(this);
            icon = v.findViewById(R.id.item_icon);
            text = (TextView) v.findViewById(android.R.id.text1);
        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            if (isDir(file)) {
                mCurrentPath = file;
                mCheckedItems.clear();
                mCheckedVisibleViewHolders.clear();
                refresh();
            }
        }

        /**
         * Called when a view has been clicked and held.
         *
         * @param v The view that was clicked and held.
         * @return true if the callback consumed the long click, false otherwise.
         */
        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }

    public class CheckableViewHolder extends DirViewHolder {

        public CheckBox checkbox;

        public CheckableViewHolder(View v) {
            super(v);
            checkbox = (CheckBox) v.findViewById(R.id.checkbox);
            checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onLongClick(v);
                }
            });
        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            if (isDir(file)) {
                mCurrentPath = file;
                mCheckedItems.clear();
                mCheckedVisibleViewHolders.clear();
                refresh();
            } else {
                onLongClick(v);
            }
        }

        /**
         * Called when a view has been clicked and held.
         *
         * @param v The view that was clicked and held.
         * @return true if the callback consumed the long click, false otherwise.
         */
        @Override
        public boolean onLongClick(View v) {
            if (mCheckedItems.contains(file)) {
                checkbox.setChecked(false);
                mCheckedItems.remove(file);
                mCheckedVisibleViewHolders.remove(this);
            } else {
                if (!allowMultiple) {
                    clearSelections();
                }
                checkbox.setChecked(true);
                mCheckedItems.add(file);
                mCheckedVisibleViewHolders.add(this);
            }
            return true;
        }
    }

    /**
     * Animate de-selection of visible views and clear
     * selected set.
     */
    public void clearSelections() {
        for (CheckableViewHolder vh : mCheckedVisibleViewHolders) {
            vh.checkbox.setChecked(false);
        }
        mCheckedVisibleViewHolders.clear();
        mCheckedItems.clear();
    }

}
