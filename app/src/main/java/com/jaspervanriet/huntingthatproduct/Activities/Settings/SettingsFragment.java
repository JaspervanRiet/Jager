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

package com.jaspervanriet.huntingthatproduct.Activities.Settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.jaspervanriet.huntingthatproduct.R;

import de.psdev.licensesdialog.LicensesDialog;


public class SettingsFragment extends PreferenceFragment {

	private static final String EMAIL_DEVELOPER = "jaspervanriet@gmail.com";

	@Override
	public void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		addPreferencesFromResource (R.xml.settings);
		setupShowAsReadPref ();
		setupCrashDataPref ();
		setupOpenSourceLicenses ();
		setupFeedbackPref ();
	}

	private void setupFeedbackPref () {
		Preference feedback = getPreferenceScreen ()
				.findPreference (SettingsActivity.KEY_SEND_FEEDBACK);
		feedback.setOnPreferenceClickListener (new Preference.OnPreferenceClickListener () {
			@Override
			public boolean onPreferenceClick (Preference preference) {
				createEmail ();
				return true;
			}
		});
	}

	private void createEmail () {
		Intent send = new Intent (Intent.ACTION_SENDTO);
		String uriText = "mailto:" + Uri.encode (EMAIL_DEVELOPER) +
				"?subject=" + Uri.encode ("Feedback for Jager") +
				"&body=" + Uri.encode ("");
		send.setData (Uri.parse (uriText));
		startActivity (Intent.createChooser (send, "Send mail..."));
	}

	private void setupOpenSourceLicenses () {
		Preference openSource = getPreferenceScreen ()
				.findPreference (SettingsActivity.KEY_OPEN_SOURCE_LICENSES);
		openSource.setOnPreferenceClickListener (new Preference.OnPreferenceClickListener () {
			@Override
			public boolean onPreferenceClick (Preference preference) {
				createLicensesDialog ();
				return true;
			}
		});
	}

	private void createLicensesDialog () {
		new LicensesDialog.Builder (getActivity ()).setNotices (R.raw.licenses)
				.build ()
				.show ();
	}

	private void setupShowAsReadPref () {
		CheckBoxPreference markAsRead = (CheckBoxPreference)
				getPreferenceScreen ().findPreference (
						SettingsActivity.KEY_SHOW_READ);
		markAsRead.setOnPreferenceChangeListener (new Preference.OnPreferenceChangeListener
				() {
			@Override
			public boolean onPreferenceChange (Preference preference, Object newValue) {
				if (newValue instanceof Boolean) {
					boolean bool = (boolean) newValue;
					setShowAsReadPref (bool);
				}
				return true;
			}
		});
	}

	private void setShowAsReadPref (boolean bool) {
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences (getActivity ());
		sharedPrefs.edit ().putBoolean (SettingsActivity.KEY_SHOW_READ,
				bool).apply ();
	}

	private void setupCrashDataPref () {
		CheckBoxPreference crashData = (CheckBoxPreference)
				getPreferenceScreen ().findPreference (
						SettingsActivity.KEY_CRASH_DATA);
		crashData.setOnPreferenceChangeListener (new Preference.OnPreferenceChangeListener () {
			@Override
			public boolean onPreferenceChange (Preference preference, Object newValue) {
				if (newValue instanceof Boolean) {
					boolean bool = (boolean) newValue;
					setCrashDataPref (bool);
				}
				return true;
			}
		});
	}

	private void setCrashDataPref (boolean bool) {
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences (getActivity ());
		sharedPrefs.edit ().putBoolean (SettingsActivity.KEY_CRASH_DATA,
				bool).apply ();
	}

}
