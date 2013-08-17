/*
 * Copyright (C) 2013 The CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.cyanogenmod;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemProperties;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.util.Log;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class VoldSwitchablePair extends SettingsPreferenceFragment {

    private CheckBoxPreference mSwitchStoragePref;

    private static final String TAG = "VoldSwitchablePair";
    private static final String KEY_SWITCH_STORAGE = "key_switch_storage";
    private static final String VOLD_SWITCH_PERSIST_PROP = "persist.sys.vold.switchexternal";
    private static final String VOLD_SWITCH_RO_PROP = "ro.vold.switchablepair";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.device_info_memory);

        String voldswitch = SystemProperties.get(VOLD_SWITCH_PERSIST_PROP, "1");
        mSwitchStoragePref = (CheckBoxPreference) findPreference(KEY_SWITCH_STORAGE);
        mSwitchStoragePref.setChecked("1".equals(voldswitch));
        if (SystemProperties.get("").equals(VOLD_SWITCH_RO_PROP)) {
            removePreference(KEY_SWITCH_STORAGE);
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if(preference == mSwitchStoragePref) {
            Log.d(TAG,"Setting persist.sys.vold.switchexternal to "+(
                    mSwitchStoragePref.isChecked() ? "1" : "0"));
            SystemProperties.set(VOLD_SWITCH_PERSIST_PROP,
                    mSwitchStoragePref.isChecked() ? "1" : "0");
            showRebootPrompt();
        } else {
            // If we didn't handle it, let preferences handle it.
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }

        return true;
    }

    private void showRebootPrompt() {
        AlertDialog dialog = new AlertDialog.Builder(getActivity().getApplicationContext())
                .setTitle(R.string.reboot_prompt_title)
                .setMessage(R.string.reboot_prompt_message)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                        pm.reboot(null);
                    }
                })
                .setNegativeButton(R.string.no, null)
                .create();

        dialog.show();
    }

}
