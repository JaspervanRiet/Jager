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

package com.jaspervanriet.huntingthatproduct.Activities;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

import com.jaspervanriet.huntingthatproduct.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class AboutActivity extends BaseActivity {

	@InjectView (R.id.about_name)
	TextView name;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_about);
		super.onCreateDrawer ();
		ButterKnife.inject (this);

		setToolBar ();
		name.setTypeface (Typeface
				.createFromAsset (getAssets (),
						"fonts/Roboto-Light.ttf"));
	}

	@Override
	protected int getSelfNavDrawerItem () {
		return NAVDRAWER_ITEM_ABOUT;
	}

}
