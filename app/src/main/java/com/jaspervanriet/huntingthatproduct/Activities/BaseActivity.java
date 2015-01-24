/*
 * Copyright (c) Jasper van Riet 2015.
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
	public static final int NAVDRAWER_ITEM_SETTINGS = 1;
	public static final int NAVDRAWER_ITEM_ABOUT = 2;
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
				mDrawerData);
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
		setSupportActionBar (mToolBar);
		ActionBar actionBar = getSupportActionBar ();
		actionBar.setElevation (5);
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