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

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.jaspervanriet.huntingthatproduct.Activities.Settings.SettingsActivity;
import com.jaspervanriet.huntingthatproduct.Adapters.DrawerAdapter;
import com.jaspervanriet.huntingthatproduct.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class BaseActivity extends ActionBarActivity {

	public static final int NAVDRAWER_ITEM_TODAYS_PRODUCTS = 0;
	public static final int NAVDRAWER_ITEM_SETTINGS = 2;
	public static final int NAVDRAWER_ITEM_ABOUT = 3;
	public static final int NAVDRAWER_ITEM_COLLECTIONS = 1;
	public static final int NAVDRAWER_ITEM_INVALID = -1;

	private static final int NAVDRAWER_LAUNCH_DELAY = 250;

	private ActionBarDrawerToggle mDrawerToggle;
	private Handler mHandler;

	@InjectView (R.id.drawer_layout)
	DrawerLayout mDrawer;
	@InjectView (R.id.toolbar)
	Toolbar mToolBar;
	@InjectView (R.id.drawer_list)
	ListView mDrawerList;

	protected void onCreateDrawer () {
		ButterKnife.inject (this);
		setupDrawer ();
		mHandler = new Handler ();
	}

	private void setupDrawer () {
		setupDrawerList ();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			mDrawer.setElevation (10);
		}
		mDrawerToggle = new ActionBarDrawerToggle (this, mDrawer,
				mToolBar, R.string.open, R.string.close);
		mDrawerToggle.setDrawerIndicatorEnabled (true);
		mDrawer.setDrawerListener (mDrawerToggle);
	}

	private void setupDrawerList () {
		String[] mDrawerData = getResources ().getStringArray (R.array.drawer_items);
		DrawerAdapter drawerAdapter = new DrawerAdapter (this,
				mDrawerData, getSelfNavDrawerItem ());
		mDrawerList.setAdapter (drawerAdapter);
		mDrawerList.setOnItemClickListener (new AdapterView.OnItemClickListener () {
			@Override
			public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
				onDrawerItemClicked (position);
			}
		});
	}

	private void onDrawerItemClicked (final int item) {
		if (!(item == getSelfNavDrawerItem ())) {
			// Wait for drawer to close before starting activity
			mHandler.postDelayed (new Runnable () {
				@Override
				public void run () {
					goToNavDrawerItem (item);
				}
			}, NAVDRAWER_LAUNCH_DELAY);
		}
		closeNavDrawer ();
	}

	private void goToNavDrawerItem (int item) {
		Intent intent;
		switch (item) {
			case NAVDRAWER_ITEM_TODAYS_PRODUCTS:
				intent = new Intent (this, MainActivity.class);
				intent.putExtra ("toolbar_animation", false);
				startActivity (intent);
				overridePendingTransition (0, 0);
				finish ();
				break;
			case NAVDRAWER_ITEM_COLLECTIONS:
				intent = new Intent (this, CollectionsListActivity.class);
				startActivity (intent);
				overridePendingTransition (0, 0);
				finish ();
				break;
			case NAVDRAWER_ITEM_SETTINGS:
				intent = new Intent (this, SettingsActivity.class);
				startActivity (intent);
				overridePendingTransition (0, 0);
				finish ();
				break;
			case NAVDRAWER_ITEM_ABOUT:
				intent = new Intent (this, AboutActivity.class);
				startActivity (intent);
				overridePendingTransition (0, 0);
				finish ();
				break;
		}
	}

	protected void closeNavDrawer () {
		if (mDrawer != null) {
			mDrawer.closeDrawer (Gravity.START);
		}
	}

	protected int getSelfNavDrawerItem () {
		return NAVDRAWER_ITEM_INVALID;
	}

	protected void setToolBar () {
		String[] drawerData = getResources ().getStringArray (R.array
				.drawer_items);
		setSupportActionBar (mToolBar);
		ActionBar actionBar = getSupportActionBar ();
		actionBar.setElevation (5);
		switch (getSelfNavDrawerItem ()) {
			case NAVDRAWER_ITEM_TODAYS_PRODUCTS:
				actionBar.setTitle (drawerData[0]);
				break;
			case NAVDRAWER_ITEM_COLLECTIONS:
				actionBar.setTitle ("Featured " + drawerData[1]);
				break;
			case NAVDRAWER_ITEM_SETTINGS:
				actionBar.setTitle (drawerData[2]);
				break;
			case NAVDRAWER_ITEM_ABOUT:
				actionBar.setTitle (drawerData[3]);
				break;
		}
	}

	@Override
	public boolean onOptionsItemSelected (MenuItem item) {
		return mDrawerToggle.onOptionsItemSelected (item) || super.onOptionsItemSelected (item);
	}

	@Override
	protected void onPostCreate (Bundle savedInstanceState) {
		super.onPostCreate (savedInstanceState);
		mDrawerToggle.syncState ();
	}

	@Override
	public void onConfigurationChanged (Configuration newConfig) {
		super.onConfigurationChanged (newConfig);
		mDrawerToggle.onConfigurationChanged (newConfig);
	}
}