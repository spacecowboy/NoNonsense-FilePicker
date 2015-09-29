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

package com.nononsenseapps.filepicker.sample;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.nononsenseapps.filepicker.FilePickerFragment;

import java.io.File;

/**
 * A sample which demonstrates how appropriate methods
 * can be overwritten in order to enable enhanced
 * capabilities, in this case showing thumbnails of images.
 * <p/>
 * I am still listing all files, so I extend from the ready made
 * SD-card browser classes. This allows this class to focus
 * entirely on the image side of things.
 * <p/>
 * To load the image I am using the super great Glide library
 * which only requires a single line of code in this file.
 */
public class MultimediaPickerFragment extends FilePickerFragment {

    // Make sure these do not collide with LogicHandler.VIEWTYPE codes.
    // They are 1-2, so 11 leaves a lot of free space in between.
    private static final int VIEWTYPE_IMAGE_CHECKABLE = 11;
    private static final int VIEWTYPE_IMAGE = 12;

    private static final String[] MULTIMEDIA_EXTENSIONS =
            new String[]{".png", ".jpg", ".gif", ".mp4"};

    /**
     * An extremely simple method for identifying multimedia. This
     * could be improved, but it's good enough for this example.
     *
     * @param file which could be an image or a video
     * @return true if the file can be previewed, false otherwise
     */
    protected boolean isMultimedia(File file) {
        //noinspection SimplifiableIfStatement
        if (isDir(file)) {
            return false;
        }

        String path = file.getPath().toLowerCase();
        for (String ext : MULTIMEDIA_EXTENSIONS) {
            if (path.endsWith(ext)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Here we check if the file is an image, and if thus if we should create views corresponding
     * to our image layouts.
     *
     * @param position 0 - n, where the header has been subtracted
     * @param file     to check type of
     * @return the viewtype of the item
     */
    @Override
    public int getItemViewType(int position, File file) {
        if (isMultimedia(file)) {
            if (isCheckable(file)) {
                return VIEWTYPE_IMAGE_CHECKABLE;
            } else {
                return VIEWTYPE_IMAGE;
            }
        } else {
            return super.getItemViewType(position, file);
        }
    }

    /**
     * We override this method and provide some special views for images.
     * This is necessary to work around a bug on older Android versions (4.0.3 for example)
     * where setting a "tint" would just make the entire image a square of solid color.
     * <p/>
     * So the special layouts used here are merely "untinted" copies from the library.
     *
     * @param parent   Containing view
     * @param viewType which the ViewHolder will contain
     * @return a DirViewHolder (or subclass thereof)
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEWTYPE_IMAGE_CHECKABLE:
                return new CheckableViewHolder(LayoutInflater.from(getActivity())
                        .inflate(R.layout.listitem_image_checkable, parent, false));
            case VIEWTYPE_IMAGE:
                return new DirViewHolder(LayoutInflater.from(getActivity())
                        .inflate(R.layout.listitem_image, parent, false));
            default:
                return super.onCreateViewHolder(parent, viewType);
        }
    }

    /**
     * Overriding this method allows us to inject a preview image
     * in the layout
     *
     * @param vh       to bind data from either a file or directory
     * @param position 0 - n, where the header has been subtracted
     * @param file     to show info about
     */
    @Override
    public void onBindViewHolder(DirViewHolder vh, int position, File file) {
        // Let the super method do its thing with checkboxes and text
        super.onBindViewHolder(vh, position, file);

        // Here we load the preview image if it is an image file
        final int viewType = getItemViewType(position, file);
        if (viewType == VIEWTYPE_IMAGE_CHECKABLE || viewType == VIEWTYPE_IMAGE) {
            // Need to set it to visible because the base code will set it to invisible by default
            vh.icon.setVisibility(View.VISIBLE);
            // Just load the image
            Glide.with(this).load(file).into((ImageView) vh.icon);
        }
    }
}
