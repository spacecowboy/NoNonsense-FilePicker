/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.nononsenseapps.filepicker.sample;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.nononsenseapps.filepicker.AbstractFilePickerFragment;
import com.nononsenseapps.filepicker.FilePickerActivity;
import com.nononsenseapps.filepicker.Utils;
import com.nononsenseapps.filepicker.sample.databinding.ActivityNoNonsenseFilePickerBinding;
import com.nononsenseapps.filepicker.sample.dropbox.DropboxFilePickerActivity;
import com.nononsenseapps.filepicker.sample.dropbox.DropboxFilePickerActivity2;
import com.nononsenseapps.filepicker.sample.dropbox.DropboxSyncHelper;
import com.nononsenseapps.filepicker.sample.fastscroller.FastScrollerFilePickerActivity;
import com.nononsenseapps.filepicker.sample.fastscroller.FastScrollerFilePickerActivity2;
import com.nononsenseapps.filepicker.sample.ftp.FtpPickerActivity;
import com.nononsenseapps.filepicker.sample.ftp.FtpPickerActivity2;
import com.nononsenseapps.filepicker.sample.multimedia.MultimediaPickerActivity;
import com.nononsenseapps.filepicker.sample.multimedia.MultimediaPickerActivity2;
import com.nononsenseapps.filepicker.sample.root.SUPickerActivity;
import com.nononsenseapps.filepicker.sample.root.SUPickerActivity2;

import java.util.List;


public class NoNonsenseFilePicker extends Activity {

    // How to handle multiple return data
    public static boolean useClipData = true;

    static final int CODE_SD = 0;
    static final int CODE_DB = 1;
    static final int CODE_FTP = 2;
    DropboxAPI<AndroidAuthSession> mDBApi = null;
    ActivityNoNonsenseFilePickerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_no_nonsense_file_picker);

        binding.buttonSd
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        if (binding.checkLightTheme.isChecked()) {
                            startActivity(CODE_SD, FilePickerActivity2.class);
                        } else {
                            startActivity(CODE_SD, FilePickerActivity.class);
                        }
                    }
                });

        binding.buttonImage
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        if (binding.checkLightTheme.isChecked()) {
                            startActivity(CODE_SD, MultimediaPickerActivity2.class);
                        } else {
                            startActivity(CODE_SD, MultimediaPickerActivity.class);
                        }
                    }
                });

        binding.buttonFtp
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        if (binding.checkLightTheme.isChecked()) {
                            startActivity(CODE_FTP, FtpPickerActivity2.class);
                        } else {
                            startActivity(CODE_FTP, FtpPickerActivity.class);
                        }
                    }
                });

        binding.buttonDropbox
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {

                        // First we must authorize the user
                        if (mDBApi == null) {
                            mDBApi = DropboxSyncHelper
                                    .getDBApi(NoNonsenseFilePicker.this);
                        }

                        // If not authorized, then ask user for login/permission
                        if (!mDBApi.getSession().isLinked()) {
                            mDBApi.getSession().startOAuth2Authentication(
                                    NoNonsenseFilePicker.this);
                        } else {  // User is authorized, open file picker
                            if (binding.checkLightTheme.isChecked()) {
                                startActivity(CODE_DB, DropboxFilePickerActivity2.class);
                            } else {
                                startActivity(CODE_DB, DropboxFilePickerActivity.class);
                            }
                        }
                    }
                });

        binding.buttonRoot
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (binding.checkLightTheme.isChecked()) {
                            startActivity(CODE_SD, SUPickerActivity.class);
                        } else {
                            startActivity(CODE_SD, SUPickerActivity2.class);
                        }
                    }
                });

        binding.buttonFastscroll
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (binding.checkLightTheme.isChecked()) {
                            startActivity(CODE_SD, FastScrollerFilePickerActivity.class);
                        } else {
                            startActivity(CODE_SD, FastScrollerFilePickerActivity2.class);
                        }
                    }
                });
    }

    protected void startActivity(final int code, final Class<?> klass) {
        final Intent i = new Intent(this, klass);

        i.setAction(Intent.ACTION_GET_CONTENT);

        i.putExtra(SUPickerActivity.EXTRA_ALLOW_MULTIPLE,
                binding.checkAllowMultiple.isChecked());
        i.putExtra(FilePickerActivity.EXTRA_SINGLE_CLICK,
                binding.checkSingleClick.isChecked());
        i.putExtra(SUPickerActivity.EXTRA_ALLOW_CREATE_DIR,
                binding.checkAllowCreateDir.isChecked());
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_EXISTING_FILE,
                binding.checkAllowExistingFile.isChecked());

        // What mode is selected
        final int mode;
        switch (binding.radioGroup.getCheckedRadioButtonId()) {
            case R.id.radioDir:
                mode = AbstractFilePickerFragment.MODE_DIR;
                break;
            case R.id.radioFilesAndDirs:
                mode = AbstractFilePickerFragment.MODE_FILE_AND_DIR;
                break;
            case R.id.radioNewFile:
                mode = AbstractFilePickerFragment.MODE_NEW_FILE;
                break;
            case R.id.radioFile:
            default:
                mode = AbstractFilePickerFragment.MODE_FILE;
                break;
        }

        i.putExtra(FilePickerActivity.EXTRA_MODE, mode);

        // This line is solely so that test classes can override intents given through UI
        i.putExtras(getIntent());

        startActivityForResult(i, code);
    }

    /**
     * This is entirely for Dropbox's benefit
     */
    protected void onResume() {
        super.onResume();

        if (mDBApi != null && mDBApi.getSession().authenticationSuccessful()) {
            try {
                // Required to complete auth, sets the access token on the session
                mDBApi.getSession().finishAuthentication();

                String accessToken = mDBApi.getSession().getOAuth2AccessToken();
                DropboxSyncHelper.saveToken(this, accessToken);
            } catch (IllegalStateException e) {
                Log.i("DbAuthLog", "Error authenticating", e);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.no_nonsense_file_picker, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        // Always check the resultCode!
        // Checking for the requestCodes is a bit redundant but good style
        if (resultCode == Activity.RESULT_OK &&
                (CODE_SD == requestCode || CODE_DB == requestCode || CODE_FTP == requestCode)) {
            // Use the provided utility method to parse the result
            List<Uri> files = Utils.getSelectedFilesFromResult(data);

            // Do something with your list of files here
            StringBuilder sb = new StringBuilder();
            for (Uri uri : files) {
                if (sb.length() > 0) {
                    sb.append("\n");
                }
                sb.append(CODE_SD == requestCode ?
                        Utils.getFileForUri(uri).toString() :
                        uri.toString());
            }
            binding.text.setText(sb.toString());
        }
    }
}
