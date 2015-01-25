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
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jaspervanriet.huntingthatproduct.Activities.Settings.SettingsActivity;
import com.jaspervanriet.huntingthatproduct.Adapters.ProductListAdapter;
import com.jaspervanriet.huntingthatproduct.Classes.Product;
import com.jaspervanriet.huntingthatproduct.R;
import com.jaspervanriet.huntingthatproduct.Utils.Constants;
import com.jaspervanriet.huntingthatproduct.Utils.Utils;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends BaseActivity
		implements ProductListAdapter.OnProductClickListener {

	private final static int ANIM_TOOLBAR_INTRO_DURATION = 350;

	private ArrayList<Product> mProducts = new ArrayList<> ();
	private ProductListAdapter mListAdapter;
	private Handler mHandler = new Handler ();
	private Boolean mIsRefreshing = false;
	private Boolean startIntroAnimation = true;

	@InjectView (R.id.toolbar)
	Toolbar mToolBar;
	@InjectView (android.R.id.list)
	RecyclerView mRecyclerView;
	@InjectView (R.id.list_progress_wheel)
	ProgressWheel progressWheel;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		if (sendCrashData ()) {
			Fabric.with (this, new Crashlytics ());
		}
		setContentView (R.layout.activity_main);
		super.onCreateDrawer ();
		ButterKnife.inject (this);

		boolean toolbarAnimation = getIntent ().getBooleanExtra
				("toolbar_animation", true);
		startIntroAnimation = savedInstanceState == null && toolbarAnimation;

		setToolBar ();
		getSupportActionBar ().setTitle (getResources ().getStringArray (R
				.array.drawer_items)[0]);

		mListAdapter = new ProductListAdapter (this, mProducts);
		mListAdapter.setOnProductClickListener (this);

		setupRecyclerView ();
		completeRefresh ();
	}

	@Override
	public boolean onCreateOptionsMenu (Menu menu) {
		MenuInflater inflater = getMenuInflater ();
		inflater.inflate (R.menu.main_menu, menu);
		if (startIntroAnimation) {
			setToolbarIntroAnimation ();
			startIntroAnimation = false;
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected (MenuItem item) {
		int itemId = item.getItemId ();
		if (itemId == R.id.menu_main_refresh) {
			completeRefresh ();
			return true;
		}
		return super.onOptionsItemSelected (item);
	}

	@Override
	public void onImageClick (View v, int position) {
		Product product = mProducts.get (position);
		Intent openUrl = new Intent (this, WebActivity.class);
		activityExitAnimation (v, product, openUrl);
	}

	@Override
	public void onDetailsClick (View v, Product product) {
		Intent i = new Intent (this, CommentsActivity.class);
		activityExitAnimation (v, product, i);
	}

	@Override
	protected int getSelfNavDrawerItem () {
		return NAVDRAWER_ITEM_TODAYS_PRODUCTS;
	}

	private void activityExitAnimation (View v, Product product, Intent i) {
		int[] startingLocation = new int[2];
		v.getLocationOnScreen (startingLocation);
		i.putExtra (CommentsActivity.ARG_DRAWING_START_LOCATION, startingLocation[1]);
		i.putExtra ("product", product);
		startActivity (i);
		overridePendingTransition (0, 0);
	}

	private void setToolbarIntroAnimation () {
		int toolBarSize = Utils.dpToPx (56);
		mToolBar.setTranslationY (-toolBarSize);
		mToolBar.animate ()
				.translationY (0)
				.setDuration (ANIM_TOOLBAR_INTRO_DURATION)
				.setStartDelay (300);
	}

	private void completeRefresh () {
		if (mProducts.size () != 0) {
			mProducts.clear ();
			mListAdapter.notifyDataSetChanged ();
		}
		progressWheel.setVisibility (View.VISIBLE);
		progressWheel.spin ();
		getContent ();
	}

	private void setupRecyclerView () {
		mRecyclerView.setHasFixedSize (true);
		mRecyclerView.setItemAnimator (new DefaultItemAnimator ());
		mRecyclerView.setLayoutManager (getLayoutManager ());
		mRecyclerView.setAdapter (mListAdapter);
	}

	private void getContent () {
		mIsRefreshing = true;
		mHandler.post (refreshingContent);
		mProducts.clear ();
		if (Constants.TOKEN_EXPIRES < System.currentTimeMillis ()) {
			getAuthToken ();
		} else {
			getProducts ();
		}
	}

	private void getAuthToken () {
		JsonObject json = new JsonObject ();
		json.addProperty ("client_id", Constants.CLIENT_ID);
		json.addProperty ("client_secret", Constants.CLIENT_SECRET);
		json.addProperty ("grant_type", Constants.GRANT_TYPE);

		Ion.with (this).load (Constants.API_TOKEN_URL)
				.setJsonObjectBody (json)
				.asJsonObject ()
				.setCallback (new FutureCallback<JsonObject> () {
					@Override
					public void onCompleted (Exception e, JsonObject result) {
						if (result != null && result.has ("access_token")) {
							Constants.CLIENT_TOKEN = result.get ("access_token")
									.getAsString ();
							Constants.TOKEN_EXPIRES = System.currentTimeMillis () +
									(long) result.get ("expires_in").getAsInt ();
							getContent ();
						}
					}
				});
	}

	// Retrieves content and adds it to mProducts
	private void getProducts () {
		Ion.with (this).load (Constants.API_URL + "posts")
				.setHeader ("Authorization", "Bearer " + Constants.CLIENT_TOKEN)
				.asJsonObject ()
				.setCallback (new FutureCallback<JsonObject> () {
					@Override
					public void onCompleted (Exception e, JsonObject result) {
						if (e != null && e instanceof TimeoutException) {
							Toast.makeText (MainActivity.this,
									getResources ().getString
											(R.string.error_connection),
									Toast.LENGTH_SHORT).show ();
							return;
						}
						if (result != null && result.has ("posts")) {
							JsonArray products = result.getAsJsonArray ("posts");
							for (int i = 0; i < products.size (); i++) {
								JsonObject obj = products.get (i).getAsJsonObject ();
								Product product = new Product (obj);
								mProducts.add (product);
							}
							mListAdapter.notifyDataSetChanged ();
							mIsRefreshing = false;
						}
					}
				});
	}

	private boolean sendCrashData () {
		SettingsActivity settingsActivity = new SettingsActivity ();
		return settingsActivity.getCrashDataPref (this);
	}

	private LinearLayoutManager getLayoutManager () {
		LinearLayoutManager layoutManager = new LinearLayoutManager (this);
		layoutManager.setOrientation (LinearLayoutManager.VERTICAL);
		return layoutManager;
	}

	private final Runnable refreshingContent = new Runnable () {
		public void run () {
			try {
				// If still refreshing, run again
				if (mIsRefreshing) {
					mHandler.postDelayed (this, 1000);
					// else stop animation
				} else {
					progressWheel.stopSpinning ();
					progressWheel.setVisibility (View.GONE);
				}
			} catch (Exception error) {
				error.printStackTrace ();
			}
		}
	};

}
