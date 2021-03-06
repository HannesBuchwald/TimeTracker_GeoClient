/*
 * Copyright 2012 - 2017 Anton Tananaev (anton.tananaev@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cc.timetracker.geotracker;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.TwoStatePreference;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@SuppressWarnings("deprecation")
public class MainActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String KEY_DEVICE = "id";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_PORT = "port";
    public static final String KEY_SECURE = "secure";
    public static final String KEY_INTERVAL = "interval";
    public static final String KEY_DISTANCE = "distance";
    public static final String KEY_ANGLE = "angle";
    public static final String KEY_PROVIDER = "provider";
    public static final String KEY_STATUS = "status";

    static String SERVER_ADDRESS = "https://server-timetracker.herokuapp.com/a5c8e07368efde43/status/";

    private static final int PERMISSIONS_REQUEST_LOCATION = 2;

    private SharedPreferences sharedPreferences;

    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (BuildConfig.HIDDEN_APP) {
//            removeLauncherIcon();
//        }


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        addPreferencesFromResource(R.xml.preferences);
        initPreferences();

        findPreference(KEY_DEVICE).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                return newValue != null && !newValue.equals("");
            }
        });

        findPreference(KEY_ADDRESS).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                return newValue != null && !newValue.equals("");
            }


//            @Override
//            public boolean onPreferenceChange(Preference preference, Object newValue) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
//                    return newValue != null && Patterns.DOMAIN_NAME.matcher((String) newValue).matches();
//                } else {
//                    return newValue != null && !((String) newValue).isEmpty();
//                }
//            }
        });

//        findPreference(KEY_PORT).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//            @Override
//            public boolean onPreferenceChange(Preference preference, Object newValue) {
//                if (newValue != null) {
//                    try {
//                        int value = Integer.parseInt((String) newValue);
//                        return value > 0 && value <= 65536;
//                    } catch (NumberFormatException e) {
//                        Log.w(TAG, e);
//                    }
//                }
//                return false;
//            }
//        });

        findPreference(KEY_INTERVAL).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (newValue != null) {
                    try {
                        int value = Integer.parseInt((String) newValue);
                        return value > 0;
                    } catch (NumberFormatException e) {
                        Log.w(TAG, e);
                    }
                }
                return false;
            }
        });
        findPreference(KEY_INTERVAL).setSummary(sharedPreferences.getString(KEY_INTERVAL, null));


        findPreference(KEY_DISTANCE).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (newValue != null) {
                    try {
                        int value = Integer.parseInt((String) newValue);
                        return value > 0;
                    } catch (NumberFormatException e) {
                        Log.w(TAG, e);
                    }
                }
                return false;
            }
        });
        findPreference(KEY_DISTANCE).setSummary(sharedPreferences.getString(KEY_DISTANCE, null));


        findPreference(KEY_ANGLE).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (newValue != null) {
                    try {
                        int value = Integer.parseInt((String) newValue);
                        return value > 0;
                    } catch (NumberFormatException e) {
                        Log.w(TAG, e);
                    }
                }
                return false;
            }
        });
        findPreference(KEY_ANGLE).setSummary(sharedPreferences.getString(KEY_ANGLE, null));


//        findPreference(KEY_PROVIDER).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//            @Override
//            public boolean onPreferenceChange(Preference preference, Object newValue) {
//                if (newValue != null) {
//                    try {
//                        int value = Integer.parseInt((String) newValue);
//                        return value > 0;
//                    } catch (NumberFormatException e) {
//                        Log.w(TAG, e);
//                    }
//                }
//                return false;
//            }
//        });
        findPreference(KEY_PROVIDER).setSummary(sharedPreferences.getString(KEY_PROVIDER, null));


        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmIntent = PendingIntent.getBroadcast(this, 0, new Intent(this, AutostartReceiver.class), 0);

        if (sharedPreferences.getBoolean(KEY_STATUS, false)) {
            startTrackingService(true, false);
        }
    }

    private void removeLauncherIcon() {
        String className = MainActivity.class.getCanonicalName().replace(".MainActivity", ".Launcher");
        ComponentName componentName = new ComponentName(getPackageName(), className);
        PackageManager packageManager = getPackageManager();
        if (packageManager.getComponentEnabledSetting(componentName) != PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
            packageManager.setComponentEnabledSetting(
                    componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(android.R.drawable.ic_dialog_alert);
            builder.setMessage(getString(R.string.hidden_alert));
            builder.setPositiveButton(android.R.string.ok, null);
            builder.show();
        }
    }

    private void addShortcuts(String action, int name) {
        Intent shortcutIntent = new Intent(Intent.ACTION_MAIN);
        shortcutIntent.setComponent(new ComponentName(getPackageName(), ShortcutActivity.class.getCanonicalName()));
        shortcutIntent.putExtra(ShortcutActivity.EXTRA_ACTION, action);
        Intent installShortCutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        installShortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        installShortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(name));
        installShortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(this, R.mipmap.ic_launcher));

        sendBroadcast(installShortCutIntent);
    }

    private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            return true;
        }
        return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onResume() {
        super.onResume();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    private void setPreferencesEnabled(boolean enabled) {
        findPreference(KEY_DEVICE).setEnabled(enabled);
        findPreference(KEY_ADDRESS).setEnabled(enabled);
//        findPreference(KEY_PORT).setEnabled(enabled);
//        findPreference(KEY_SECURE).setEnabled(enabled);
        findPreference(KEY_INTERVAL).setEnabled(enabled);
        findPreference(KEY_DISTANCE).setEnabled(enabled);
        findPreference(KEY_ANGLE).setEnabled(enabled);
        findPreference(KEY_PROVIDER).setEnabled(enabled);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(KEY_STATUS)) {
            if (sharedPreferences.getBoolean(KEY_STATUS, false)) {
                startTrackingService(true, false);
            } else {
                stopTrackingService();
            }
        }
        // !!! start own code !!!
        else if (key.equals(KEY_DEVICE)) {
            findPreference(KEY_DEVICE).setSummary(sharedPreferences.getString(KEY_DEVICE, null));
        } else if (key.equals(KEY_ADDRESS)) {
            findPreference(KEY_ADDRESS).setSummary(sharedPreferences.getString(KEY_ADDRESS, null));
        } else if (key.equals(KEY_INTERVAL)) {
            findPreference(KEY_INTERVAL).setSummary(sharedPreferences.getString(KEY_INTERVAL, null));
        } else if (key.equals(KEY_DISTANCE)) {
            findPreference(KEY_DISTANCE).setSummary(sharedPreferences.getString(KEY_DISTANCE, null));
        } else if (key.equals(KEY_ANGLE)) {
            findPreference(KEY_ANGLE).setSummary(sharedPreferences.getString(KEY_ANGLE, null));
        } else if (key.equals(KEY_PROVIDER)) {
            findPreference(KEY_PROVIDER).setSummary(sharedPreferences.getString(KEY_PROVIDER, null));
        }
        // !!! end own code !!!
    }


    //public static MenuItem gps;

    static public void setGPSText(float on) {

        if (menu != null) {
            MenuItem gps = menu.findItem(R.id.gps);
            if (gps != null) {
                gps.setTitle((int) on + "m");
            }
        }
    }

    static public void setGPSText(String text) {

        if (menu != null) {
            MenuItem gps = menu.findItem(R.id.gps);
            if (gps != null) {
                gps.setTitle(text);
            }
        }
    }

    public static Menu menu;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        this.menu = menu;

        // MenuItem gps = menu.getItem(R.id.gps);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if (item.getItemId() == R.id.status) {
            startActivity(new Intent(this, StatusActivity.class));
            return true;
        } else if (item.getItemId() == R.id.about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initPreferences() {
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        if (!sharedPreferences.contains(KEY_DEVICE)) {
            String id = String.valueOf(new Random().nextInt(900000) + 100000);
            sharedPreferences.edit().putString(KEY_DEVICE, id).commit();
            ((EditTextPreference) findPreference(KEY_DEVICE)).setText(id);
        }
        // !!! start own Code !!!
        findPreference(KEY_DEVICE).setSummary(sharedPreferences.getString(KEY_DEVICE, null));


        if (!sharedPreferences.contains(KEY_ADDRESS)) {

            String address = SERVER_ADDRESS;

            sharedPreferences.edit().putString(KEY_ADDRESS, address).commit();
            ((EditTextPreference) findPreference(KEY_ADDRESS)).setText(address);
        }
        findPreference(KEY_ADDRESS).setSummary(sharedPreferences.getString(KEY_ADDRESS, null));

        // !!! end own Code !!!

    }

    private void startTrackingService(boolean checkPermission, boolean permission) {
        if (checkPermission) {
            Set<String> missingPermissions = new HashSet<>();
            if (!hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
                missingPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (!hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                missingPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            if (missingPermissions.isEmpty()) {
                permission = true;
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(missingPermissions.toArray(new String[missingPermissions.size()]),
                            PERMISSIONS_REQUEST_LOCATION);
                }
                return;
            }
        }

        if (permission) {
            setPreferencesEnabled(false);
            startService(new Intent(this, TrackingService.class));
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    15000, 15000, alarmIntent);
        } else {
            sharedPreferences.edit().putBoolean(KEY_STATUS, false).commit();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                TwoStatePreference preference = (TwoStatePreference) findPreference(KEY_STATUS);
                preference.setChecked(false);
            } else {
                CheckBoxPreference preference = (CheckBoxPreference) findPreference(KEY_STATUS);
                preference.setChecked(false);
            }
        }
    }

    private void stopTrackingService() {
        alarmManager.cancel(alarmIntent);
        stopService(new Intent(this, TrackingService.class));
        setPreferencesEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_LOCATION) {
            boolean granted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    granted = false;
                    break;
                }
            }
            startTrackingService(false, granted);
        }
    }

}
