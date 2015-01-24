/*
 * Copyright (c) Jasper van Riet 2015.
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
		getSupportActionBar ().setTitle (getResources ().getStringArray (R
				.array.drawer_items)[2]);
		name.setTypeface (Typeface
				.createFromAsset (getAssets (),
						"fonts/Roboto-Light.ttf"));
	}

	@Override
	protected int getSelfNavDrawerItem () {
		return NAVDRAWER_ITEM_ABOUT;
	}

}
