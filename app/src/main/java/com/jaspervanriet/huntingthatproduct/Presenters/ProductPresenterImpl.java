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

package com.jaspervanriet.huntingthatproduct.Presenters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.jaspervanriet.huntingthatproduct.Data.Http.PHService;
import com.jaspervanriet.huntingthatproduct.Data.Settings.AppSettings;
import com.jaspervanriet.huntingthatproduct.Entities.Authentication;
import com.jaspervanriet.huntingthatproduct.Entities.Collection;
import com.jaspervanriet.huntingthatproduct.Entities.Posts;
import com.jaspervanriet.huntingthatproduct.Entities.Product;
import com.jaspervanriet.huntingthatproduct.Utils.Constants;
import com.jaspervanriet.huntingthatproduct.Utils.DateUtils;
import com.jaspervanriet.huntingthatproduct.Utils.NetworkUtils;
import com.jaspervanriet.huntingthatproduct.Views.Activities.CommentsActivity;
import com.jaspervanriet.huntingthatproduct.Views.Activities.WebActivity;
import com.jaspervanriet.huntingthatproduct.Views.Adapters.ProductListAdapter;
import com.jaspervanriet.huntingthatproduct.Views.ProductView;

import java.util.Calendar;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ProductPresenterImpl implements ProductPresenter {

	public static final int ACTIVITY_MAIN = 1;
	public static final int ACTIVITY_COLLECTION = 2;

	private Subscription mSubscription;
	private ProductView mProductView;
	private PHService mPHService;
	private ProductListAdapter mAdapter;
	private String mDate;
	private Posts mPosts;
	private int mCollectionId;
	private Observable<Posts> mPostsObservable;
	private Observable<Collection> mCollectionObservable;
	private Observer<Posts> mPostsObserver = new Observer<Posts> () {
		@Override
		public void onCompleted () {

		}

		@Override
		public void onError (Throwable e) {
			Crashlytics.logException (e);
			e.printStackTrace ();
		}

		@Override
		public void onNext (Posts posts) {
			showPosts (posts);
		}
	};

	private Observer<Collection> mCollectionObserver = new Observer<Collection> () {
		@Override
		public void onCompleted () {

		}

		@Override
		public void onError (Throwable e) {

		}

		@Override
		public void onNext (Collection collection) {
			Posts posts = new Posts ();
			posts.setPosts (collection.getPosts ());
			showPosts (posts);
		}
	};

	public ProductPresenterImpl (ProductView productView) {
		this.mProductView = productView;
	}

	public ProductPresenterImpl (ProductView productView, int collectionId) {
		mProductView = productView;
		mCollectionId = collectionId;
	}

	@Override
	public void onActivityCreated (Bundle savedInstanceState) {
		mProductView.initializeRecyclerView ();
		initializeAdapter ();

		if (!NetworkUtils.hasInternetAccess (mProductView.getContext ())) {
			mProductView.showEmptyView ();
			mProductView.showNoNetworkError ();
		} else {
			boolean isMainActivity = mProductView.getActivity () == ACTIVITY_MAIN;
			if (savedInstanceState == null) {
				mProductView.showRefreshingIndicator ();
				if (isMainActivity) {
					getPosts ();
				} else {
					getCollectionPosts ();
				}
			} else {
				restoreInstanceState (savedInstanceState);
				getCache (isMainActivity);
			}
		}
	}

	private void initializeAdapter () {
		mPosts = new Posts ();
		mAdapter = new ProductListAdapter (mProductView.getContext (),
				mPosts.getPosts ());
		mAdapter.setOnProductClickListener (mProductView.getProductClickListener ());
		mProductView.setAdapterForRecyclerView (mAdapter);
	}

	private void getPosts () {
		mPHService = new PHService (new Authentication (Constants.CLIENT_ID,
				Constants.CLIENT_SECRET, Constants.GRANT_TYPE));
		mPostsObservable = mPHService.askForToken ().flatMap (token -> mPHService.getPosts
				(token, mDate)
				.subscribeOn (Schedulers.from (AsyncTask.THREAD_POOL_EXECUTOR))
				.observeOn (AndroidSchedulers.mainThread ()));
		mSubscription = mPostsObservable.subscribe (mPostsObserver);
	}

	private void getCollectionPosts () {
		mPHService = new PHService (new Authentication (Constants.CLIENT_ID,
				Constants.CLIENT_SECRET, Constants.GRANT_TYPE));
		mCollectionObservable = mPHService.askForToken ().flatMap (token -> mPHService
				.getCollectionPosts (token, mCollectionId)
				.subscribeOn (Schedulers.from (AsyncTask.THREAD_POOL_EXECUTOR))
				.observeOn (AndroidSchedulers.mainThread ()));
		mSubscription = mCollectionObservable.subscribe (mCollectionObserver);
	}

	private void showPosts (Posts posts) {
		mPosts = posts;
		mAdapter = new ProductListAdapter (mProductView.getContext (),
				mPosts.getPosts ());
		mAdapter.setOnProductClickListener (mProductView.getProductClickListener ());
		mProductView.setAdapterForRecyclerView (mAdapter);
//		mAdapter.notifyDataSetChanged ();
		mProductView.hideRefreshingIndicator ();
		if (mAdapter.getItemCount () == 0) {
			mProductView.showEmptyView ();
		}
	}

	private void getCache (boolean isMainActivity) {
		if (mSubscription != null) {
			mSubscription.unsubscribe ();
			mSubscription = null;
		}
		if (isMainActivity) {
			if (mPostsObservable != null) {
				mProductView.showRefreshingIndicator ();
				mSubscription = mPostsObservable.subscribe (mPostsObserver);
			}
		} else {
			if (mCollectionObservable != null) {
				mProductView.showRefreshingIndicator ();
				mSubscription = mCollectionObservable.subscribe (mCollectionObserver);
			}
		}
	}

	private void restoreInstanceState (Bundle savedInstanceState) {
		mPosts = Posts.getParcelable (savedInstanceState);
		mAdapter = new ProductListAdapter (mProductView.getContext (),
				mPosts.getPosts ());
		mAdapter.setOnProductClickListener (mProductView.getProductClickListener ());
		mProductView.setAdapterForRecyclerView (mAdapter);
	}

	@Override
	public void onSaveInstanceState (Bundle outState) {
		Posts.putParcelable (outState, mPosts);
	}

	@Override
	public void onDestroy () {
		if (mSubscription != null) {
			mSubscription.unsubscribe ();
			mSubscription = null;
		}
	}

	private void resetProductsIfExist () {
		if (!mPosts.isEmpty ()) {
			mPosts.clear ();
		}
	}

	@Override
	public void onRefresh () {
		mProductView.hideEmptyView ();
		mProductView.showRefreshingIndicator ();
		resetProductsIfExist ();
		getPosts ();
	}

	@Override
	public void onDateSet (int year, int monthOfYear, int dayOfMonth) {
		Calendar chosenCalendar = Calendar.getInstance ();
		chosenCalendar.set (Calendar.YEAR, year);
		chosenCalendar.set (Calendar.MONTH, monthOfYear);
		chosenCalendar.set (Calendar.DAY_OF_MONTH, dayOfMonth);
		mDate = DateUtils.getDateFormattedString (chosenCalendar);
		mProductView.setToolbarTitle (
				DateUtils.getMonth (monthOfYear) + " " + dayOfMonth);
		onRefresh ();
	}

	@Override
	public void onShareClick (int feedItem) {
		Product product = mPosts.getPosts ().get (feedItem);
		Intent share = new Intent (android.content.Intent.ACTION_SEND);
		share.setType ("text/plain");
		share.putExtra (Intent.EXTRA_SUBJECT, product.getName ());
		share.putExtra (Intent.EXTRA_TEXT, product.getProductUrl ());
		mProductView.getContext ().startActivity (
				Intent.createChooser (share, "Share " + "product"));
		mProductView.hideContextMenu ();
	}

	@Override
	public void onImageClick (View v, int feedItem) {
		Product product = mPosts.getPosts ().get (feedItem);
		Context context = mProductView.getContext ();
		if (AppSettings.getOpenInBrowserPref (context)) {
			Intent intent = new Intent (Intent.ACTION_VIEW).setData (Uri
					.parse (product.getProductUrl ()));
			context.startActivity (intent);
		} else {
			Intent openUrl = new Intent (context, WebActivity.class);
			showExitAnimation (v, product, openUrl);
		}
	}

	@Override
	public void onCommentsClick (View v, Product product) {
		Intent i = new Intent (mProductView.getContext (), CommentsActivity.class);
		showExitAnimation (v, product, i);
	}

	private void showExitAnimation (View v, Product product, Intent intent) {
		int[] startingLocation = new int[2];
		v.getLocationOnScreen (startingLocation);
		intent.putExtra (CommentsActivity.ARG_DRAWING_START_LOCATION,
				startingLocation[1]);
		intent.putExtra ("product", product);
		mProductView.getContext ().startActivity (intent);
		mProductView.hideActivityTransition ();
	}

}
