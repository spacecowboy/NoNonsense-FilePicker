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

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.nononsenseapps.filepicker.AbstractFilePickerFragment;
import com.nononsenseapps.filepicker.FilePickerActivity;
import com.nononsenseapps.filepicker.sample.dropbox.DropboxFilePickerActivity;
import com.nononsenseapps.filepicker.sample.dropbox.DropboxFilePickerActivity2;
import com.nononsenseapps.filepicker.sample.dropbox.DropboxSyncHelper;

import java.util.ArrayList;


public class NoNonsenseFilePicker extends Activity {

    private static final int CODE_SD = 0;
    private static final int CODE_DB = 1;
    private TextView textView;
    private DropboxAPI<AndroidAuthSession> mDBApi = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_nonsense_file_picker);

        final CheckBox checkAllowCreateDir =
                (CheckBox) findViewById(R.id.checkAllowCreateDir);
        final CheckBox checkAllowMultiple =
                (CheckBox) findViewById(R.id.checkAllowMultiple);
        final CheckBox checkLightTheme =
                (CheckBox) findViewById(R.id.checkLightTheme);
        final RadioGroup radioGroup =
                (RadioGroup) findViewById(R.id.radioGroup);
        textView = (TextView) findViewById(R.id.text);

        findViewById(R.id.button_sd)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        Intent i;

                        if (checkLightTheme.isChecked()) {
                            i = new Intent(NoNonsenseFilePicker.this,
                                    FilePickerActivity2.class);
                        } else {
                            i = new Intent(NoNonsenseFilePicker.this,
                                    FilePickerActivity.class);
                        }
                        i.setAction(Intent.ACTION_GET_CONTENT);

                        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE,
                                checkAllowMultiple.isChecked());
                        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR,
                                checkAllowCreateDir.isChecked());

                        // What mode is selected
                        final int mode;
                        switch (radioGroup.getCheckedRadioButtonId()) {
                            case R.id.radioDir:
                                mode = AbstractFilePickerFragment.MODE_DIR;
                                break;
                            case R.id.radioFilesAndDirs:
                                mode =
                                        AbstractFilePickerFragment.MODE_FILE_AND_DIR;
                                break;
                            case R.id.radioFile:
                            default:
                                mode = AbstractFilePickerFragment.MODE_FILE;
                                break;
                        }

                        i.putExtra(FilePickerActivity.EXTRA_MODE, mode);


                        startActivityForResult(i, CODE_SD);
                    }
                });

        findViewById(R.id.button_image)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        Intent i;

                        if (checkLightTheme.isChecked()) {
                            i = new Intent(NoNonsenseFilePicker.this,
                                    MultimediaPickerActivity2.class);
                        } else {
                            i = new Intent(NoNonsenseFilePicker.this,
                                    MultimediaPickerActivity.class);
                        }
                        i.setAction(Intent.ACTION_GET_CONTENT);

                        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE,
                                checkAllowMultiple.isChecked());
                        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR,
                                checkAllowCreateDir.isChecked());

                        // What mode is selected (makes no sense to restrict to folders here)
                        final int mode;
                        switch (radioGroup.getCheckedRadioButtonId()) {
                            case R.id.radioFilesAndDirs:
                                mode =
                                        AbstractFilePickerFragment.MODE_FILE_AND_DIR;
                                break;
                            case R.id.radioFile:
                            default:
                                mode = AbstractFilePickerFragment.MODE_FILE;
                                break;
                        }

                        i.putExtra(FilePickerActivity.EXTRA_MODE, mode);


                        startActivityForResult(i, CODE_SD);
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
                                i = new Intent(NoNonsenseFilePicker.this,
                                        DropboxFilePickerActivity2.class);
                            } else {
                                i = new Intent(NoNonsenseFilePicker.this,
                                        DropboxFilePickerActivity.class);
                            }

                            i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE,
                                    checkAllowMultiple.isChecked());
                            i.putExtra(
                                    FilePickerActivity.EXTRA_ALLOW_CREATE_DIR,
                                    checkAllowCreateDir.isChecked());

                            // What mode is selected
                            final int mode;
                            switch (radioGroup.getCheckedRadioButtonId()) {
                                case R.id.radioDir:
                                    mode = AbstractFilePickerFragment.MODE_DIR;
                                    break;
                                case R.id.radioFilesAndDirs:
                                    mode =
                                            AbstractFilePickerFragment.MODE_FILE_AND_DIR;
                                    break;
                                case R.id.radioFile:
                                default:
                                    mode = AbstractFilePickerFragment.MODE_FILE;
                                    break;
                            }

                            i.putExtra(FilePickerActivity.EXTRA_MODE, mode);

                            startActivityForResult(i, CODE_DB);
                        }
                    }
                });
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
        if ((CODE_SD == requestCode || CODE_DB == requestCode) &&
                resultCode == Activity.RESULT_OK) {
            if (data.getBooleanExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE,
                    false)) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ClipData clip = data.getClipData();
                    StringBuilder sb = new StringBuilder();

                    if (clip != null) {
                        for (int i = 0; i < clip.getItemCount(); i++) {
                            sb.append(clip.getItemAt(i).getUri().toString());
                            sb.append("\n");
                        }
                    }

                    textView.setText(sb.toString());
                } else {
                    ArrayList<String> paths = data.getStringArrayListExtra(
                            FilePickerActivity.EXTRA_PATHS);
                    StringBuilder sb = new StringBuilder();

                    if (paths != null) {
                        for (String path : paths) {
                            sb.append(path);
                            sb.append("\n");
                        }
                    }
                    textView.setText(sb.toString());
                }
            } else {
                textView.setText(data.getData().toString());
            }
        }
    }

}
