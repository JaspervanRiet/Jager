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
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
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
import com.jaspervanriet.huntingthatproduct.Activities.Settings.SettingsActivity;
import com.jaspervanriet.huntingthatproduct.Adapters.ProductListAdapter;
import com.jaspervanriet.huntingthatproduct.Entities.Product;
import com.jaspervanriet.huntingthatproduct.Models.ProductDatabase;
import com.jaspervanriet.huntingthatproduct.Models.ProductModel;
import com.jaspervanriet.huntingthatproduct.R;
import com.jaspervanriet.huntingthatproduct.Utils.Constants;
import com.jaspervanriet.huntingthatproduct.Utils.DateUtils;
import com.jaspervanriet.huntingthatproduct.Utils.NetworkUtils;
import com.jaspervanriet.huntingthatproduct.Utils.ViewUtils;
import com.jaspervanriet.huntingthatproduct.Views.DatePickerFragment;
import com.jaspervanriet.huntingthatproduct.Views.FeedContextMenu;
import com.jaspervanriet.huntingthatproduct.Views.FeedContextMenuManager;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoTools;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.fabric.sdk.android.Fabric;
import io.realm.Realm;

public class MainActivity extends DrawerActivity
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
	private Realm mRealm;

	/**
	 * Date user last used app
	 */
	private String mSavedDate;

	/**
	 * True if user has picked a day to view products for
	 */
	private boolean mDateSet = false;

	/**
	 * True if activity no longer exists
	 */
	private static boolean mIsDestroyed;

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
//		initFabric ();
		setContentView (R.layout.activity_main);
		super.onCreateDrawer ();
		ButterKnife.inject (this);
		mIsDestroyed = false;

		mRealm = Realm.getInstance (this);

		boolean toolbarAnimation = getIntent ().getBooleanExtra
				("toolbar_animation", true);
		startIntroAnimation = (savedInstanceState == null) && toolbarAnimation;
		setToolBar ();
		mDateString = DateUtils.getTodaysDate ();
		mSavedDate = DateUtils.getLastUsedDate (this, mDateString);
		ProductDatabase.removeOldCache (this, mSavedDate, mDateString);

		setProgressWheelColor ();
		createProductList ();
	}

	@Override
	public void onRestart () {
		super.onRestart ();
		completeRefresh ();
	}

	@Override
	public void onDestroy () {
		mIsDestroyed = true;
		PicassoTools.clearCache (Picasso.with (this));
		mRealm.close ();
		super.onDestroy ();
	}

	private void createProductList () {
		createListAdapter ();
		setupRecyclerView ();
		completeRefresh ();
	}

	private void completeRefresh () {
		hideEmptyView ();
		resetProductsIfExist ();
		showProgressWheel ();
		changeRefreshingIndicator (true);
		mHandler.post (refreshingContent);
		getContent ();
	}

	private void getContent () {
		if (!(NetworkUtils.hasInternetAccess (this))) {
			if (appHasBeenUsedToday ()) {
				getLocalCache ();
				showUpdatedList ();
			} else {
				showNoConnectionError ();
				changeRefreshingIndicator (false);
				checkListIsEmpty ();
			}
		} else {
			getProducts ();
		}
	}

	/* Retrieves products from Product Hunt API and saves them to Realm */
	private void getProducts () {
		Callback callback = new Callback () {
			public boolean handleMessage (Message msg) {
				onApiCallDone ();
				return true;
			}
		};
		ProductModel.getData (this, callback, getPostsUrl ());
	}

	/* Retrieves products from Realm and populates mProducts */
	public void onApiCallDone () {
		getProductsFromRealm ();
		showUpdatedList ();
	}

	private void getProductsFromRealm () {
		ProductDatabase.queryForProducts (mProducts, mRealm, mDateString);
	}

	private void resetProductsIfExist () {
		if (mProducts.size () != 0) {
			clearProductsList ();
			refreshAdapter ();
		}
	}

	private void showUpdatedList () {
		changeRefreshingIndicator (false);
		refreshAdapter ();
	}

	private void getLocalCache () {
		showNoConnectionError ();
		getProductsFromRealm ();
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

	@Override
	public void onDateSet (DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		mDateSet = true;
		Calendar chosenCalendar = Calendar.getInstance ();
		chosenCalendar.set (Calendar.YEAR, year);
		chosenCalendar.set (Calendar.MONTH, monthOfYear);
		chosenCalendar.set (Calendar.DAY_OF_MONTH, dayOfMonth);
		mDateString = DateUtils.getDateFormattedString (chosenCalendar);
		setActionBarTitle (DateUtils.getMonth (monthOfYear) + " " + dayOfMonth);
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

	@Override
	public void onImageClick (View v, int position) {
		Product product = mProducts.get (position);
		ProductDatabase.setProductAsRead (product, mRealm);
		refreshAdapter ();
		if (SettingsActivity.getOpenInBrowserPref (this)) {
			Intent intent = new Intent (Intent.ACTION_VIEW).setData (Uri
					.parse (product.getProductUrl ()));
			startActivity (intent);
		} else {
			Intent openUrl = new Intent (this, WebActivity.class);
			activityExitAnimation (v, product, openUrl);
		}
	}

	@Override
	public void onCommentsClick (View v, Product product) {
		ProductDatabase.setProductAsRead (product, mRealm);
		refreshAdapter ();
		Intent i = new Intent (this, CommentsActivity.class);
		activityExitAnimation (v, product, i);
	}

	@Override
	public void onContextClick (View v, int position) {
		FeedContextMenuManager.getInstance ().toggleContextMenuFromView (v,
				position, this, true);
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

	@Override
	protected int getSelfNavDrawerItem () {
		return NAVDRAWER_ITEM_TODAYS_PRODUCTS;
	}

	private void activityExitAnimation (View v, Product product, Intent i) {
		int[] startingLocation = new int[2];
		v.getLocationOnScreen (startingLocation);
		i.putExtra (CommentsActivity.ARG_DRAWING_START_LOCATION, startingLocation[1]);
		i.putExtra ("productId", product.getId ());
		i.putExtra ("collection", false);
		startActivity (i);
		overridePendingTransition (0, 0);
	}

	private void setToolbarIntroAnimation () {
		int toolBarSize = ViewUtils.dpToPx (56);
		mToolBar.setTranslationY (-toolBarSize);
		mToolBar.animate ()
				.translationY (0)
				.setDuration (ANIM_TOOLBAR_INTRO_DURATION)
				.setStartDelay (300);
	}

	private void createListAdapter () {
		mListAdapter = new ProductListAdapter (this, mProducts);
		mListAdapter.setOnProductClickListener (this);
	}

	private void setProgressWheelColor () {
		mProgressWheel.setBarColor (getResources ().getColor (R.color.primary_accent));
	}

	private void changeRefreshingIndicator (boolean isRefreshing) {
		mIsRefreshing = isRefreshing;
	}

	private boolean appHasBeenUsedToday () {
		return mSavedDate.equals (mDateString);
	}

	public static boolean activityIsDestroyed () {
		return mIsDestroyed;
	}

	private void clearProductsList () {
		mProducts.clear ();
	}

	private void hideEmptyView () {
		mEmptyView.setVisibility (View.GONE);
	}

	private void initFabric () {
		if (sendCrashData ()) {
			Fabric.with (this, new Crashlytics ());
		}
	}

	private String getPostsUrl () {
		if (mDateSet) {
			return Constants.API_URL + "posts?day=" + mDateString;
		}
		return Constants.API_URL + "posts";
	}

	private void refreshAdapter () {
		mListAdapter.notifyDataSetChanged ();
	}

	private void showNoConnectionError () {
		Toast.makeText (this, getResources ().getString (R.string.error_connection),
				Toast.LENGTH_SHORT).show ();
	}

	private void checkListIsEmpty () {
		if (mListAdapter.getItemCount () == 0) {
			mEmptyView.setVisibility (View.VISIBLE);
		} else {
			hideEmptyView ();
		}
	}

	private void goToPlayStorePage () {
		Intent intent = new Intent (Intent.ACTION_VIEW).setData (Uri
				.parse (URL_PLAY_STORE));
		startActivity (intent);
	}

	private void showProgressWheel () {
		mProgressWheel.setVisibility (View.VISIBLE);
		mProgressWheel.spin ();
	}

	private void hideProgressWheel () {
		mProgressWheel.stopSpinning ();
		mProgressWheel.setVisibility (View.GONE);
	}

	private void showDatePickerDialog () {
		DialogFragment dialogFragment = new DatePickerFragment ();
		dialogFragment.show (getSupportFragmentManager (), "dataPicker");
	}

	private final Runnable refreshingContent = new Runnable () {
		public void run () {
			try {
				if (mIsRefreshing) {
					mHandler.postDelayed (this, 1000);
				} else {
					hideProgressWheel ();
					checkListIsEmpty ();
				}
			}
			catch (Exception error) {
				Crashlytics.logException (error);
			}
		}
	};
}
