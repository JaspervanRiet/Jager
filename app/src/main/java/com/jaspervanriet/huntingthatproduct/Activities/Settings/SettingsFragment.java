/*
 * Copyright (c) Jasper van Riet 2015.
 */

package com.jaspervanriet.huntingthatproduct.Activities.Settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.jaspervanriet.huntingthatproduct.R;

import de.psdev.licensesdialog.LicensesDialog;


public class SettingsFragment extends PreferenceFragment {


	@Override
	public void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		addPreferencesFromResource (R.xml.settings);

		setupHighQualityImagesPref ();
		setupCrashDataPref ();
		setupOpenSourceLicenses ();
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

	private void setupHighQualityImagesPref () {
		CheckBoxPreference highQualityImages = (CheckBoxPreference)
				getPreferenceScreen ().findPreference (SettingsActivity.KEY_HIGH_QUALITY_IMAGES);
		highQualityImages.setOnPreferenceChangeListener (new Preference.OnPreferenceChangeListener () {
			@Override
			public boolean onPreferenceChange (Preference preference, Object newValue) {
				if (newValue instanceof Boolean) {
					boolean bool = (boolean) newValue;
					setHighQualityImagesPref (bool);
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

	private void setHighQualityImagesPref (boolean bool) {
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences (getActivity ());
		sharedPrefs.edit ().putBoolean (SettingsActivity.KEY_HIGH_QUALITY_IMAGES,
				bool).apply ();
	}

}
