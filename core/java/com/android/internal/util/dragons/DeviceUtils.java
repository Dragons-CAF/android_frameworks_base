/*
* Copyright (C) 2014 SlimRoms Project
* Copyright (C) 2017 Dragons OOS

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.android.internal.util.dragons;

import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.hardware.display.DisplayManager;
import android.hardware.display.WifiDisplayStatus;
import android.net.ConnectivityManager;
import android.nfc.NfcAdapter;
import android.provider.Settings;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.DisplayInfo;
import android.view.WindowManager;
import android.util.Log;

import com.android.internal.telephony.PhoneConstants;

import java.util.ArrayList;
import java.util.List;

public class DeviceUtils {

    private static final String SETTINGS_METADATA_NAME = "com.android.settings";

    // Device types
    private static final int DEVICE_PHONE  = 0;
    private static final int DEVICE_HYBRID = 1;
    private static final int DEVICE_TABLET = 2;

    public static boolean deviceSupportNavigationBar(Context context) {
        return deviceSupportNavigationBarForUser(context, UserHandle.USER_CURRENT);
    }

    public static boolean deviceSupportNavigationBarForUser(Context context, int userId) {
        final boolean showByDefault = context.getResources().getBoolean(
                com.android.internal.R.bool.config_showNavigationBar);
        final int hasNavigationBar = Settings.System.getIntForUser(
                context.getContentResolver(),
                Settings.System.NAVIGATION_BAR_SHOW, -1,
                userId);

        if (hasNavigationBar == -1) {
            String navBarOverride = SystemProperties.get("qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                return false;
            } else if ("0".equals(navBarOverride)) {
                return true;
            } else {
                return showByDefault;
            }
        } else {
            return hasNavigationBar == 1;
        }
    }

    private static int getScreenType(Context con) {
        WindowManager wm = (WindowManager)con.getSystemService(Context.WINDOW_SERVICE);
        DisplayInfo outDisplayInfo = new DisplayInfo();
        wm.getDefaultDisplay().getDisplayInfo(outDisplayInfo);
        int shortSize = Math.min(outDisplayInfo.logicalHeight, outDisplayInfo.logicalWidth);
        int shortSizeDp =
            shortSize * DisplayMetrics.DENSITY_DEFAULT / outDisplayInfo.logicalDensityDpi;
        if (shortSizeDp < 600) {
            return DEVICE_PHONE;
        } else if (shortSizeDp < 720) {
            return DEVICE_HYBRID;
        } else {
            return DEVICE_TABLET;
        }
    }

    public static boolean isPhone(Context con) {
        return getScreenType(con) == DEVICE_PHONE;
    }

    public static boolean isHybrid(Context con) {
        return getScreenType(con) == DEVICE_HYBRID;
    }

    public static boolean isTablet(Context con) {
        return getScreenType(con) == DEVICE_TABLET;
    }

}
