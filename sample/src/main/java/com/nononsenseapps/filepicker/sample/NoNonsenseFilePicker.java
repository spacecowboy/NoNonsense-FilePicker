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

package com.nononsenseapps.filepicker.sample;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.nononsenseapps.filepicker.FilePickerActivity;

import java.util.ArrayList;


public class NoNonsenseFilePicker extends Activity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_nonsense_file_picker);

        final CheckBox checkOnlyDir = (CheckBox) findViewById(R.id.checkOnlyDir);
        final CheckBox checkAllowMultiple = (CheckBox) findViewById(R.id
                .checkAllowMultiple);
        textView = (TextView) findViewById(R.id.text);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                //Intent i = new Intent(NoNonsenseFilePicker.this,
                //        FilePickerActivity.class);
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);

                i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE,
                        checkAllowMultiple.isChecked());
                i.putExtra(FilePickerActivity.EXTRA_ONLY_DIRS,
                        checkOnlyDir.isChecked());

                startActivityForResult(i, 0);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            if (data.getBooleanExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE,
                    false)) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ClipData clip = data.getClipData();
                    StringBuilder sb = new StringBuilder();

                    for (int i = 0; i < clip.getItemCount(); i++) {
                        sb.append(clip.getItemAt(i).getUri().toString());
                        sb.append("\n");
                    }

                    textView.setText(sb.toString());
                } else {
                    ArrayList<String> paths = data.getStringArrayListExtra
                            (FilePickerActivity.EXTRA_PATHS);
                    StringBuilder sb = new StringBuilder();

                    if (paths != null) {
                        for (String path: paths) {
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
