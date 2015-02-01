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

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
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
import com.jaspervanriet.huntingthatproduct.Views.DatePickerFragment;
import com.jaspervanriet.huntingthatproduct.Views.FeedContextMenu;
import com.jaspervanriet.huntingthatproduct.Views.FeedContextMenuManager;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeoutException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends BaseActivity
		implements ProductListAdapter.OnProductClickListener,
		DatePickerDialog.OnDateSetListener,
		FeedContextMenu.OnFeedContextMenuItemClickListener {

	private final static int ANIM_TOOLBAR_INTRO_DURATION = 350;
	private final static String URL_PLAY_STORE = "market://details?id=com.jaspervanriet.huntingthatproduct";

	private ArrayList<Product> mProducts = new ArrayList<> ();
	private ProductListAdapter mListAdapter;
	private Handler mHandler = new Handler ();
	private Boolean mIsRefreshing = false;
	private Boolean startIntroAnimation = true;
	private String mDateString;

	@InjectView (R.id.toolbar)
	Toolbar mToolBar;
	@InjectView (android.R.id.list)
	RecyclerView mRecyclerView;
	@InjectView (R.id.list_progress_wheel)
	ProgressWheel mProgressWheel;
	@InjectView (R.id.products_empty_view)
	LinearLayout mEmptyView;

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
		startIntroAnimation = (savedInstanceState == null) && toolbarAnimation;

		setToolBar ();
		getTodaysDate ();

		mProgressWheel.setBarColor (getResources ().getColor (R.color.primary_accent));
		mListAdapter = new ProductListAdapter (this, mProducts);
		mListAdapter.setOnProductClickListener (this);
		setupRecyclerView ();
		completeRefresh ();
	}

	@Override
	public void onRestart () {
		super.onRestart ();
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
		if (itemId == R.id.menu_calendar) {
			showDatePickerDialog ();
			return true;
		}
		if (itemId == R.id.menu_rate) {
			goToPlayStorePage ();
			return true;
		}
		return super.onOptionsItemSelected (item);
	}

	private void goToPlayStorePage () {
		Intent intent = new Intent (Intent.ACTION_VIEW).setData (Uri
				.parse (URL_PLAY_STORE));
		startActivity (intent);
	}

	@Override
	public void onImageClick (View v, int position) {
		Product product = mProducts.get (position);
		Intent openUrl = new Intent (this, WebActivity.class);
		activityExitAnimation (v, product, openUrl);
	}

	@Override
	public void onCommentsClick (View v, Product product) {
		Intent i = new Intent (this, CommentsActivity.class);
		activityExitAnimation (v, product, i);
	}

	@Override
	public void onContextClick (View v, int position) {
		FeedContextMenuManager.getInstance ().toggleContextMenuFromView (v,
				position, this, true);
	}

	@Override
	protected int getSelfNavDrawerItem () {
		return NAVDRAWER_ITEM_TODAYS_PRODUCTS;
	}

	private void showDatePickerDialog () {
		DialogFragment dialogFragment = new DatePickerFragment ();
		dialogFragment.show (getSupportFragmentManager (), "dataPicker");
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
		mEmptyView.setVisibility (View.GONE);
		if (mProducts.size () != 0) {
			mProducts.clear ();
			mListAdapter.notifyDataSetChanged ();
		}
		mProgressWheel.setVisibility (View.VISIBLE);
		mProgressWheel.spin ();
		getContent ();
	}

	private void setupRecyclerView () {
		mRecyclerView.setHasFixedSize (true);
		mRecyclerView.setItemAnimator (new DefaultItemAnimator ());
		mRecyclerView.setLayoutManager (getLayoutManager ());
		mRecyclerView.setAdapter (mListAdapter);
		mRecyclerView.setOnScrollListener (new RecyclerView.OnScrollListener () {
			@Override
			public void onScrolled (RecyclerView recyclerView, int dx, int dy) {
				FeedContextMenuManager.getInstance ().onScrolled (recyclerView, dx, dy);
			}
		});
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
		Ion.with (this).load (Constants.API_URL + "posts?day=" + mDateString)
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
							int i;
							JsonArray products = result.getAsJsonArray ("posts");
							for (i = 0; i < products.size (); i++) {
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

	private void setActionBarTitle (String title) {
		ActionBar actionBar = getSupportActionBar ();
		actionBar.setTitle (title);
	}

	private LinearLayoutManager getLayoutManager () {
		LinearLayoutManager layoutManager = new LinearLayoutManager (this);
		layoutManager.setOrientation (LinearLayoutManager.VERTICAL);
		return layoutManager;
	}

	private void getTodaysDate () {
		Calendar todayCalendar = Calendar.getInstance ();
		mDateString = getDateFormattedString (todayCalendar);
	}

	public String getMonth (int month) {
		return new DateFormatSymbols ().getMonths ()[month];
	}

	private String getDateFormattedString (Calendar calendar) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat (
				"yyyy-MM-dd");
		return simpleDateFormat.format (calendar.getTime ());
	}

	private void checkEmpty () {
		if (mListAdapter.getItemCount () == 0) {
			mEmptyView.setVisibility (View.VISIBLE);
		} else {
			mEmptyView.setVisibility (View.GONE);
		}
	}

	private final Runnable refreshingContent = new Runnable () {
		public void run () {
			try {
				// If still refreshing, run again
				if (mIsRefreshing) {
					mHandler.postDelayed (this, 1000);
					// else stop animation
				} else {
					mProgressWheel.stopSpinning ();
					mProgressWheel.setVisibility (View.GONE);
					checkEmpty ();
				}
			} catch (Exception error) {
				error.printStackTrace ();
			}
		}
	};

	@Override
	public void onDateSet (DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		Calendar chosenCalendar = Calendar.getInstance ();
		chosenCalendar.set (Calendar.YEAR, year);
		chosenCalendar.set (Calendar.MONTH, monthOfYear);
		chosenCalendar.set (Calendar.DAY_OF_MONTH, dayOfMonth);
		mDateString = getDateFormattedString (chosenCalendar);
		setActionBarTitle (getMonth (monthOfYear) + " " + dayOfMonth);
		completeRefresh ();
	}

	@Override
	public void onShareClick (int feedItem) {
		Product product = mProducts.get (feedItem);
		Intent share = new Intent (android.content.Intent.ACTION_SEND);
		share.setType ("text/plain");
		share.putExtra (Intent.EXTRA_SUBJECT, product.title);
		share.putExtra (Intent.EXTRA_TEXT, product.productUrl);
		startActivity (Intent.createChooser (share, "Share product"));
		FeedContextMenuManager.getInstance ().hideContextMenu ();
	}

	@Override
	public void onCancelClick (int feedItem) {
		FeedContextMenuManager.getInstance ().hideContextMenu ();
	}
}
