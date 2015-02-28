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
import android.content.Context;
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
import com.nanotasks.BackgroundWork;
import com.nanotasks.Completion;
import com.nanotasks.Tasks;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeoutException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends BaseActivity
		implements ProductListAdapter.OnProductClickListener,
		DatePickerDialog.OnDateSetListener,
		FeedContextMenu.OnFeedContextMenuItemClickListener {

	private final static int ANIM_TOOLBAR_INTRO_DURATION = 350;
	private final static String URL_PLAY_STORE = "market://details?id=com.jaspervanriet" +
			".huntingthatproduct";

	private ArrayList<Product> mProducts = new ArrayList<> ();
	private ProductListAdapter mListAdapter;
	private Handler mHandler = new Handler ();
	private Boolean mIsRefreshing = false;
	private Boolean startIntroAnimation = true;
	private String mDateString;
	private JsonObject mJsonResult;

	// Date for last time user used the app
	private String mSavedDate;

	// true if user has picked a day to view products for
	private boolean mDateSet = false;

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

		// If you do not use Fabric, remove this. Be sure to remove the references in build.gradle
		// as well.
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
		removeOldCache ();

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

	private void removeOldCache () {
		mSavedDate = getSharedPreferences ("PREFERENCE",
				MODE_PRIVATE).getString ("saved_date", mDateString);
		if (!mSavedDate.equals (mDateString)) {
			Toast.makeText (this, "not equal", Toast.LENGTH_SHORT).show ();
			Realm realm = Realm.getInstance (this);
			realm.executeTransaction (new Realm.Transaction () {
				@Override
				public void execute (Realm realm) {
					RealmResults<Product> result = realm.where (Product.class)
							.equalTo ("date", mSavedDate)
							.findAll ();
					result.clear ();
				}
			});
			getSharedPreferences ("PREFERENCE", MODE_PRIVATE)
					.edit ()
					.putString ("saved_date", mDateString)
					.apply ();
		}
	}

	private void showDatePickerDialog () {
		DialogFragment dialogFragment = new DatePickerFragment ();
		dialogFragment.show (getSupportFragmentManager (), "dataPicker");
	}

	private void activityExitAnimation (View v, Product product, Intent i) {
		int[] startingLocation = new int[2];
		v.getLocationOnScreen (startingLocation);
		i.putExtra (CommentsActivity.ARG_DRAWING_START_LOCATION, startingLocation[1]);
		i.putExtra ("productId", product.getId ());
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

		if (!(Utils.hasInternetAccess (this))) {
			if (mSavedDate.equals (mDateString)) {
				getLocalCache ();
				showUpdatedList ();
			} else {
				showNoConnectionError ();
				mIsRefreshing = false;
				checkEmpty ();
			}
		} else {
			if (Constants.TOKEN_EXPIRES < System.currentTimeMillis ()) {
				getAuthToken ();
			} else {
				getProducts ();
			}
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

	private void getProducts () {
		String url;
		if (mDateSet) {
			url = Constants.API_URL + "posts?day=" + mDateString;
		} else {
			url = Constants.API_URL + "posts";
		}
		Ion.with (this).load (url)
				.setHeader ("Authorization", "Bearer " + Constants.CLIENT_TOKEN)
				.asJsonObject ()
				.setCallback (new FutureCallback<JsonObject> () {
					@Override
					public void onCompleted (Exception e, JsonObject result) {
						if (e != null && e instanceof TimeoutException) {
							getLocalCache ();
							return;
						}
						if (result != null && result.has ("posts")) {
							mJsonResult = result;
							Tasks.executeInBackground (getApplicationContext (),
									new BackgroundWork<Void> () {
										@Override
										public Void doInBackground () throws Exception {
											processPosts ();
											return null;

										}
									}, new Completion<Void> () {
										@Override
										public void onSuccess (Context context, Void result) {
											queryRealmForProducts ();
											showUpdatedList ();
										}

										@Override
										public void onError (Context context, Exception e) {
											Crashlytics.logException (e);
										}
									});

						}
					}
				});
	}

	private void showUpdatedList () {
		mListAdapter.notifyDataSetChanged ();
		mIsRefreshing = false;
	}

	private void getLocalCache () {
		showNoConnectionError ();
		queryRealmForProducts ();
	}

	private void showNoConnectionError () {
		Toast.makeText (this, getResources ().getString (R.string.error_connection),
				Toast.LENGTH_SHORT).show ();
	}

	private void processPosts () {
		int i;
		JsonArray products = mJsonResult.getAsJsonArray ("posts");
		for (i = 0; i < products.size (); i++) {
			JsonObject obj = products.get (i).getAsJsonObject ();
			Product product = new Product (obj);
			cacheProduct (product);
		}
	}

	private void queryRealmForProducts () {
		Realm realm = Realm.getInstance (this);
		RealmResults<Product> resultProducts = realm.where (Product.class)
				.equalTo ("date", mDateString)
				.findAll ();
		resultProducts.sort ("votes", RealmResults.SORT_ORDER_DESCENDING);
		for (Product product : resultProducts) {
			mProducts.add (product);
		}
	}

	// Saves Product to Realm or updates the Realm entry if db contains Product already.
	private void cacheProduct (final Product product) {
		Realm realm = Realm.getInstance (this);
		realm.executeTransaction (new Realm.Transaction () {
			@Override
			public void execute (Realm realm) {
				Product realmProduct = realm.copyToRealmOrUpdate (product);
			}
		});
	}

	private boolean sendCrashData () {
		return SettingsActivity.getCrashDataPref (this);
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
		todayCalendar.setTimeZone (TimeZone.getTimeZone ("PST"));
		mDateString = getDateFormattedString (todayCalendar);
	}

	public String getMonth (int month) {
		return new DateFormatSymbols ().getMonths ()[month];
	}

	private String getDateFormattedString (Calendar calendar) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat ("yyyy-MM-dd");
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
				if (mIsRefreshing) {
					mHandler.postDelayed (this, 1000);
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
		mDateSet = true;
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
		share.putExtra (Intent.EXTRA_SUBJECT, product.getTitle ());
		share.putExtra (Intent.EXTRA_TEXT, product.getProductUrl ());
		startActivity (Intent.createChooser (share, "Share product"));
		FeedContextMenuManager.getInstance ().hideContextMenu ();
	}

	@Override
	public void onCancelClick (int feedItem) {
		FeedContextMenuManager.getInstance ().hideContextMenu ();
	}
}
