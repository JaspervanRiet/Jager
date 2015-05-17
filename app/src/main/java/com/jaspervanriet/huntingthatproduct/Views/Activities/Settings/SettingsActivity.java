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

package com.jaspervanriet.huntingthatproduct.Views.Activities.Settings;

import android.os.Bundle;

import com.jaspervanriet.huntingthatproduct.R;
import com.jaspervanriet.huntingthatproduct.Views.Activities.DrawerActivity;

public class SettingsActivity extends DrawerActivity {

	public static final String KEY_OPEN_SYSTEM_BROWSER =
			"preference_open_link_in_browser";
	public static final String KEY_CRASH_DATA = "preference_crash_data";
	public static final String KEY_OPEN_SOURCE_LICENSES =
			"preference_open_source_licenses";
	public static final String KEY_SEND_FEEDBACK = "preference_send_feedback";

	@Override
	protected int getSelfNavDrawerItem () {
		return NAVDRAWER_ITEM_SETTINGS;
	}

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_preferences);
		setToolBar ();
		getFragmentManager ().beginTransaction ().replace (R.id.content_frame,
				new SettingsFragment ()).commit ();
	}
}
