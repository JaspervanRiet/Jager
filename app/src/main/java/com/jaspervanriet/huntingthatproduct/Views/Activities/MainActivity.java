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

package com.jaspervanriet.huntingthatproduct.Views.Activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.jaspervanriet.huntingthatproduct.Entities.Product;
import com.jaspervanriet.huntingthatproduct.Presenters.ProductPresenterImpl;
import com.jaspervanriet.huntingthatproduct.R;
import com.jaspervanriet.huntingthatproduct.Utils.ViewUtils;
import com.jaspervanriet.huntingthatproduct.Views.Adapters.ProductListAdapter;
import com.jaspervanriet.huntingthatproduct.Views.FeedContextMenu;
import com.jaspervanriet.huntingthatproduct.Views.FeedContextMenuManager;
import com.jaspervanriet.huntingthatproduct.Views.ProductView;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoTools;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends DrawerActivity
		implements ProductListAdapter.OnProductClickListener,
		DatePickerDialog.OnDateSetListener,
		FeedContextMenu.OnFeedContextMenuItemClickListener,
		ProductView {

	private final static int ANIM_TOOLBAR_INTRO_DURATION = 350;
	private final static String URL_PLAY_STORE = "market://details?id=com.jaspervanriet" +
			".huntingthatproduct";

	private Handler mHandler = new Handler ();
	private Boolean mIsRefreshing = false;
	private Boolean mStartIntroAnimation = true;
	private ProductPresenterImpl mPresenter;

	@InjectView (android.R.id.list)
	RecyclerView mRecyclerView;
	@InjectView (R.id.list_progress_wheel)
	ProgressWheel mProgressWheel;
	@InjectView (R.id.products_empty_view)
	LinearLayout mEmptyView;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		initFabric ();
		setContentView (R.layout.activity_main);
		ButterKnife.inject (this);

		boolean toolbarAnimation = getIntent ().getBooleanExtra
				("toolbar_animation", true);
		mStartIntroAnimation = (savedInstanceState == null) && toolbarAnimation;
		mProgressWheel.setBarColor (getResources ().getColor (R.color.primary_accent));

		mPresenter = new ProductPresenterImpl (this);
		mPresenter.onActivityCreated (savedInstanceState);
	}

	@Override
	public void onSaveInstanceState (Bundle outState) {
		super.onSaveInstanceState (outState);
		mPresenter.onSaveInstanceState (outState);
	}

	@Override
	public void onDestroy () {
		mPresenter.onDestroy ();
		PicassoTools.clearCache (Picasso.with (this));
		super.onDestroy ();
	}

	private void initFabric () {
		if (sendCrashData ()) {
			Fabric.with (this, new Crashlytics ());
		}
	}

	@Override
	public void onDateSet (DatePickerDialog view, int year, int month, int day) {
		mPresenter.onDateSet (year, month, day);
	}

	@Override
	public void onShareClick (int feedItem) {
		mPresenter.onShareClick (feedItem);
	}

	@Override
	public void onCancelClick (int feedItem) {
		hideContextMenu ();
	}

	@Override
	public void onImageClick (View v, int position) {
		mPresenter.onImageClick (v, position);
	}

	@Override
	public void onCommentsClick (View v, Product product) {
		mPresenter.onCommentsClick (v, product);
	}

	@Override
	public void onContextClick (View v, int position) {
		showContextMenu (v, position);
	}

	@Override
	public boolean onCreateOptionsMenu (Menu menu) {
		MenuInflater inflater = getMenuInflater ();
		inflater.inflate (R.menu.main_menu, menu);
		if (mStartIntroAnimation) {
			setToolbarIntroAnimation ();
			mStartIntroAnimation = false;
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected (MenuItem item) {
		int itemId = item.getItemId ();
		if (itemId == R.id.menu_main_refresh) {
			mPresenter.onRefresh ();
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
		return false;
	}

	private void goToPlayStorePage () {
		Intent intent = new Intent (Intent.ACTION_VIEW).setData (Uri
				.parse (URL_PLAY_STORE));
		startActivity (intent);
	}

	@Override
	protected int getSelfNavDrawerItem () {
		return NAVDRAWER_ITEM_TODAYS_PRODUCTS;
	}

	private void setToolbarIntroAnimation () {
		int toolBarSize = ViewUtils.dpToPx (56);
		getToolbar ().setTranslationY (-toolBarSize);
		getToolbar ().animate ()
				.translationY (0)
				.setDuration (ANIM_TOOLBAR_INTRO_DURATION)
				.setStartDelay (300);
	}

	private void showDatePickerDialog () {
		Calendar calendar = Calendar.getInstance ();
		DatePickerDialog dialog = DatePickerDialog.newInstance (
				MainActivity.this,
				calendar.get (Calendar.YEAR),
				calendar.get (Calendar.MONTH),
				calendar.get (Calendar.DAY_OF_MONTH)
		);
		dialog.setMaxDate (calendar);
		dialog.show (getFragmentManager (), "Datepickerdialog");
	}

	private final Runnable refreshingContent = new Runnable () {
		public void run () {
			try {
				if (mIsRefreshing) {
					mHandler.postDelayed (this, 500);
				} else {
					mProgressWheel.stopSpinning ();
					mProgressWheel.setVisibility (View.GONE);
				}
			} catch (Exception error) {
				Crashlytics.logException (error);
			}
		}
	};

	@Override
	public void initializeRecyclerView () {
		mRecyclerView.setHasFixedSize (true);
		mRecyclerView.setItemAnimator (new DefaultItemAnimator ());
		mRecyclerView.setLayoutManager (getLayoutManager ());
		mRecyclerView.addOnScrollListener (new RecyclerView.OnScrollListener () {
			@Override
			public void onScrolled (RecyclerView recyclerView, int dx, int dy) {
				FeedContextMenuManager.getInstance ().onScrolled (recyclerView, dx, dy);
			}
		});
	}

	@Override
	public void setAdapterForRecyclerView (ProductListAdapter adapter) {
		mRecyclerView.setAdapter (adapter);
	}

	@Override
	public void showRefreshingIndicator () {
		mProgressWheel.setVisibility (View.VISIBLE);
		mProgressWheel.spin ();
		mHandler.post (refreshingContent);
		mIsRefreshing = true;
	}

	@Override
	public void hideRefreshingIndicator () {
		mIsRefreshing = false;
	}

	@Override
	public void showEmptyView () {
		mEmptyView.setVisibility (View.VISIBLE);
	}

	@Override
	public void hideEmptyView () {
		mEmptyView.setVisibility (View.GONE);
	}

	@Override
	public void showNoNetworkError () {
		Toast.makeText (this, getResources ().getString (R.string.error_connection),
				Toast.LENGTH_SHORT).show ();
	}

	@Override
	public void showContextMenu (View v, int position) {
		FeedContextMenuManager.getInstance ().toggleContextMenuFromView (v,
				position, this, true);
	}

	@Override
	public void hideContextMenu () {
		FeedContextMenuManager.getInstance ().hideContextMenu ();
	}

	@Override
	public void hideActivityTransition () {
		overridePendingTransition (0, 0);
	}

	@Override
	public void setToolbarTitle (String title) {
		setActionBarTitle (title);
	}

	@Override
	public int getActivity () {
		return ProductPresenterImpl.ACTIVITY_MAIN;
	}

	@Override
	public Context getContext () {
		return this;
	}

	@Override
	public ProductListAdapter.OnProductClickListener getProductClickListener () {
		return this;
	}
}
