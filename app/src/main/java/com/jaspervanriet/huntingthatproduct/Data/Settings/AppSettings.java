/*
 * Copyright (C) 2015 Jasper van Riet
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

package com.jaspervanriet.huntingthatproduct.Data.Settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.jaspervanriet.huntingthatproduct.Views.Activities.Settings.SettingsActivity;

public class AppSettings {

	public static boolean getOpenInBrowserPref (Context context) {
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences (context);
		return sharedPrefs.getBoolean (SettingsActivity.KEY_OPEN_SYSTEM_BROWSER, false);
	}

	public static boolean getCrashDataPref (Context context) {
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences (context);
		return sharedPrefs.getBoolean (SettingsActivity.KEY_CRASH_DATA, true);
	}
}
