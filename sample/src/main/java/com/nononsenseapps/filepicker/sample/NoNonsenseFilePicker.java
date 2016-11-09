/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.nononsenseapps.filepicker.sample;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.nononsenseapps.filepicker.AbstractFilePickerActivity;
import com.nononsenseapps.filepicker.AbstractFilePickerFragment;
import com.nononsenseapps.filepicker.FilePickerActivity;
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

import java.util.ArrayList;


public class NoNonsenseFilePicker extends Activity {

    static final int CODE_SD = 0;
    static final int CODE_DB = 1;
    static final int CODE_FTP = 2;
    TextView textView;
    DropboxAPI<AndroidAuthSession> mDBApi = null;
    CheckBox checkAllowCreateDir;
    CheckBox checkAllowMultiple;
    CheckBox checkSingleClick;
    CheckBox checkLightTheme;
    RadioGroup radioGroup;
    CheckBox checkAllowExistingFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_nonsense_file_picker);

        checkAllowCreateDir =
                (CheckBox) findViewById(R.id.checkAllowCreateDir);
        checkAllowMultiple =
                (CheckBox) findViewById(R.id.checkAllowMultiple);
        checkAllowExistingFile =
                (CheckBox) findViewById(R.id.checkAllowExistingFile);
        checkSingleClick =
                (CheckBox) findViewById(R.id.checkSingleClick);
        checkLightTheme =
                (CheckBox) findViewById(R.id.checkLightTheme);
        radioGroup =
                (RadioGroup) findViewById(R.id.radioGroup);
        textView = (TextView) findViewById(R.id.text);

        findViewById(R.id.button_sd)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        if (checkLightTheme.isChecked()) {
                            startActivity(CODE_SD, FilePickerActivity2.class);
                        } else {
                            startActivity(CODE_SD, FilePickerActivity.class);
                        }
                    }
                });

        findViewById(R.id.button_image)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        if (checkLightTheme.isChecked()) {
                            startActivity(CODE_SD, MultimediaPickerActivity2.class);
                        } else {
                            startActivity(CODE_SD, MultimediaPickerActivity.class);
                        }
                    }
                });

        findViewById(R.id.button_ftp)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        if (checkLightTheme.isChecked()) {
                            startActivity(CODE_FTP, FtpPickerActivity2.class);
                        } else {
                            startActivity(CODE_FTP, FtpPickerActivity.class);
                        }
                    }
                });

        findViewById(R.id.button_dropbox)
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
                            Intent i;
                            if (checkLightTheme.isChecked()) {
                                startActivity(CODE_DB, DropboxFilePickerActivity2.class);
                            } else {
                                startActivity(CODE_DB, DropboxFilePickerActivity.class);
                            }
                        }
                    }
                });

        findViewById(R.id.button_root).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLightTheme.isChecked()) {
                    startActivity(CODE_SD, SUPickerActivity.class);
                } else {
                    startActivity(CODE_SD, SUPickerActivity2.class);
                }
            }
        });

        findViewById(R.id.button_fastscroll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLightTheme.isChecked()) {
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
                checkAllowMultiple.isChecked());
        i.putExtra(FilePickerActivity.EXTRA_SINGLE_CLICK,
                checkSingleClick.isChecked());
        i.putExtra(SUPickerActivity.EXTRA_ALLOW_CREATE_DIR,
                checkAllowCreateDir.isChecked());
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_EXISTING_FILE,
                checkAllowExistingFile.isChecked());

        // What mode is selected
        final int mode;
        switch (radioGroup.getCheckedRadioButtonId()) {
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
        if ((CODE_SD == requestCode || CODE_DB == requestCode || CODE_FTP == requestCode) &&
                resultCode == Activity.RESULT_OK) {
            StringBuilder sb = new StringBuilder();
            for (Uri uri : FilePickerActivity.getActivityResult(data)){
                sb.append(uri).append('\n');
            }
            textView.setText(sb.toString());
        }
    }

}
