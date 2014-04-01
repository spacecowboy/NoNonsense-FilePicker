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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

public class BindableArrayAdapter<T> extends ArrayAdapter<T> {

    private final LayoutInflater mInflater;
    private final int mResource;
    private ViewBinder<T> viewBinder = null;

    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     */
    public BindableArrayAdapter(final Context context, final int resource) {
        super(context, resource);
        mResource = resource;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Constructor
     *
     * @param context            The current context.
     * @param resource           The resource ID for a layout file containing a layout to use when
     *                           instantiating views.
     * @param textViewResourceId The id of the TextView within the layout resource to be populated
     */
    public BindableArrayAdapter(final Context context, final int resource, final int textViewResourceId) {
        super(context, resource, textViewResourceId);
        mResource = resource;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     *                 instantiating views.
     * @param objects  The objects to represent in the ListView.
     */
    public BindableArrayAdapter(final Context context, final int resource, final T[] objects) {
        super(context, resource, objects);
        mResource = resource;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Constructor
     *
     * @param context            The current context.
     * @param resource           The resource ID for a layout file containing a layout to use when
     *                           instantiating views.
     * @param textViewResourceId The id of the TextView within the layout resource to be populated
     * @param objects            The objects to represent in the ListView.
     */
    public BindableArrayAdapter(final Context context, final int resource, final int textViewResourceId, final T[] objects) {
        super(context, resource, textViewResourceId, objects);
        mResource = resource;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     *                 instantiating views.
     * @param objects  The objects to represent in the ListView.
     */
    public BindableArrayAdapter(final Context context, final int resource, final List<T> objects) {
        super(context, resource, objects);
        mResource = resource;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Constructor
     *
     * @param context            The current context.
     * @param resource           The resource ID for a layout file containing a layout to use when
     *                           instantiating views.
     * @param textViewResourceId The id of the TextView within the layout resource to be populated
     * @param objects            The objects to represent in the ListView.
     */
    public BindableArrayAdapter(final Context context, final int resource, final int textViewResourceId, final List<T> objects) {
        super(context, resource, textViewResourceId, objects);
        mResource = resource;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getItemViewType(int position) {
        if (viewBinder != null) {
            if (viewBinder.isDir(position, getItem(position))) {
                return 1;
            }
        }
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        // Files and dirs, so 2
        return 2;
    }

    /**
     * Set the view binder to use to bind data to the list item views.
     * @param binder
     */
    public void setViewBinder(ViewBinder binder) {
        this.viewBinder = binder;
    }

    /**
     * Handle more complex views than standard implementation.
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (viewBinder == null) {
            return super.getView(position, convertView, parent);
        }
        else {
            View view;

            if (convertView == null && viewBinder != null) {
                view = viewBinder.inflateView(position, mResource, mInflater,
                        parent);
            }
            else if (convertView == null) {
                view = mInflater.inflate(mResource, parent, false);
            } else {
                view = convertView;
            }

            viewBinder.setViewValue(view, position, getItem(position));
            return view;
        }
    }

    public interface ViewBinder<T> {
        /**
         * Called if convertView is null. If this returns null,
         * the specified resource is used. Use this to return multiple views
         * depending on type.
         * @param position
         * @param resource
         * @param inflater
         * @param parent
         * @return
         */
        public View inflateView(final int position,
                                final int resource,
                                final LayoutInflater inflater,
                                final ViewGroup parent);

        /**
         * Used to determine the view's type. Returning false will use same
         * type for all rows.
         * @param position
         * @param data
         * @return
         */
        public boolean isDir(final int position, final T data);

        /**
         * Fill the content in the row
         * @param view
         * @param position
         * @param data
         */
        public void setViewValue(final View view, final int position, final T
                data);
    }
}
