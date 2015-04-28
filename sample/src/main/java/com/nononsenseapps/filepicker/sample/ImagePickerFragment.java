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

import android.view.View;
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
 *
 * To load the image I am using the super great Glide library
 * which only requires a single line of code in this file.
 */
public class ImagePickerFragment extends FilePickerFragment {

    /**
     * An extremely simple method for identifying images. This
     * could be improved, but it's good enough for this example.
     *
     * @param file which could be an image
     * @return true if the file can be previewed, false otherwise
     */
    protected boolean isImage(File file) {
        if (isDir(file)) {
            return false;
        }

        return file.getPath().endsWith(".png") ||
                file.getPath().endsWith(".PNG") ||
                file.getPath().endsWith(".jpg") ||
                file.getPath().endsWith(".JPG") ||
                file.getPath().endsWith(".gif") ||
                file.getPath().endsWith(".GIF");
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
        // Let the super method do its thing, we only care about the image
        // We could load a specific layout in oncreateviewholder, but
        // every item includes an image to hold the directory icon anyway
        super.onBindViewHolder(vh, position, file);

        // All we need to do is load the imageview with something useful, and make it visible
        if (isImage(file)) {
            vh.icon.setVisibility(View.VISIBLE);
            Glide.with(this).load(file).centerCrop().into((ImageView) vh.icon);
        }
    }
}
