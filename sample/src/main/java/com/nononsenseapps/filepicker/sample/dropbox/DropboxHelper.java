/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.nononsenseapps.filepicker.sample.dropbox;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.android.Auth;
import com.dropbox.core.v2.DbxClientV2;

/**
 * This class has some utility functions for dealing with Dropbox. You need to input your
 * API APP_KEY below, as well as in the com.dropbox.core.android.AuthActivity Activity
 * element of AndroidManifest.xml
 *
 * Create an app and generate a key for it here:
 * https://www.dropbox.com/developers
 */
class DropboxHelper {
    private static final String APP_KEY = "replace_me_with_your_own_app_key";
    private static final String PREF_DROPBOX_TOKEN = "dropboxtoken";
    private static final String CLIENT_IDENTIFIER = "no-nonsense-file-picker";

    static DbxClientV2 getClient(final Context context) {
        DbxClientV2 client = null;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String accessToken = prefs.getString(PREF_DROPBOX_TOKEN, null);
        if (accessToken == null) {
            accessToken = Auth.getOAuth2Token();
            if (accessToken != null) {
                saveToken(context, accessToken);
            }
            else {
                Auth.startOAuth2Authentication(context, APP_KEY);
            }
        }

        if (accessToken != null) {
            DbxRequestConfig requestConfig = new DbxRequestConfig(CLIENT_IDENTIFIER);
            client = new DbxClientV2(requestConfig, accessToken);
        }

        return client;
    }

    private static void saveToken(final Context context, final String token) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor preferencesEditor = prefs.edit();

        preferencesEditor.putString(PREF_DROPBOX_TOKEN, token);
        preferencesEditor.apply();
    }
}
