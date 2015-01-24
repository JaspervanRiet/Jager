/*
 * Copyright (c) Jasper van Riet 2015.
 */

package com.jaspervanriet.huntingthatproduct.Activities.Settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.jaspervanriet.huntingthatproduct.Activities.BaseActivity;
import com.jaspervanriet.huntingthatproduct.R;

public class SettingsActivity extends BaseActivity {

	public static final String KEY_HIGH_QUALITY_IMAGES =
			"preference_high_quality_images";
	public static final String KEY_CRASH_DATA = "preference_crash_data";
	public static final String KEY_OPEN_SOURCE_LICENSES =
			"preference_open_source_licenses";

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_preferences);
		super.onCreateDrawer ();
		setToolBar ();
		getSupportActionBar ().setTitle (getResources ().getStringArray (R
				.array.drawer_items)[1]);


		getFragmentManager ().beginTransaction ().replace (R.id.content_frame,
				new SettingsFragment ()).commit ();
	}

	@Override
	protected int getSelfNavDrawerItem () {
		return NAVDRAWER_ITEM_SETTINGS;
	}

	public boolean getHighQualityImagesPref (Context context) {
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences (context);
		return sharedPrefs.getBoolean (SettingsActivity.KEY_HIGH_QUALITY_IMAGES, false);
	}

	public boolean getCrashDataPref (Context context) {
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences (context);
		return sharedPrefs.getBoolean (SettingsActivity.KEY_CRASH_DATA, true);
	}
}
