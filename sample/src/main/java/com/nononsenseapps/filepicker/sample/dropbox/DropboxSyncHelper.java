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

package com.nononsenseapps.filepicker.sample.dropbox;

import android.content.Context;
import android.preference.PreferenceManager;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;

/**
 * This class has some utility functions for dealing with Dropbox. You need
 * to input your API keys below.
 * See Dropbox for more information:
 * https://www.dropbox.com/developers/core/start/android
 * <p/>
 * You also need to drop your APP_KEY in the manifest in
 * com.dropbox.client2.android.AuthActivity
 * See here for info:
 * https://www.dropbox.com/developers/core/sdks/android
 */
public class DropboxSyncHelper {
    // Change these two lines to your app's stuff
    final static public String APP_KEY = "sm57t7s6lmgj745";
    final static public String APP_SECRET = "eie6mq0lvcw9t7x";

    public static final String PREF_DROPBOX_TOKEN = "dropboxtoken";

    public static DropboxAPI<AndroidAuthSession> getDBApi(
            final Context context) {
        final DropboxAPI<AndroidAuthSession> mDBApi;

        final AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        final AndroidAuthSession session;

        if (PreferenceManager.getDefaultSharedPreferences(context)
                .contains(PREF_DROPBOX_TOKEN)) {
            session = new AndroidAuthSession(appKeys,
                    PreferenceManager.getDefaultSharedPreferences(context)
                            .getString(PREF_DROPBOX_TOKEN, ""));
        } else {
            session = new AndroidAuthSession(appKeys);
        }
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);
        return mDBApi;
    }

    /**
     * Save the dropbox oauth token so we can reuse the session without
     * logging in again.
     * @param context
     * @param token
     */
    public static void saveToken(final Context context, final String token) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_DROPBOX_TOKEN, token).apply();
    }
}
