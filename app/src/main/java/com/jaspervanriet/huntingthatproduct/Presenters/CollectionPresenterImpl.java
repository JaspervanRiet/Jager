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
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.jaspervanriet.huntingthatproduct.Data.Http.PHService;
import com.jaspervanriet.huntingthatproduct.Entities.Collection;
import com.jaspervanriet.huntingthatproduct.Entities.Collections;
import com.jaspervanriet.huntingthatproduct.R;
import com.jaspervanriet.huntingthatproduct.Views.Activities.CollectionActivity;
import com.jaspervanriet.huntingthatproduct.Views.Adapters.CollectionListAdapter;
import com.jaspervanriet.huntingthatproduct.Views.CollectionView;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CollectionPresenterImpl implements
									 CollectionPresenter {

	private CollectionView mCollectionView;
	private CollectionListAdapter mAdapter;
	private PHService mPHService;
	private Collections mCollections;
	private Subscription mSubscription;
	private Observable<Collections> mCollectionsObservable;
	private Observer<Collections> mCollectionsObserver = new Observer<Collections> () {
		@Override
		public void onCompleted () {

		}

		@Override
		public void onError (Throwable e) {
			Crashlytics.logException (e);
			Context context = mCollectionView.getContext ();
			Toast.makeText (context, context.getString (R.string.error_connection), Toast
					.LENGTH_LONG).show ();
		}

		@Override
		public void onNext (Collections collections) {
			mCollections = collections;
			mAdapter = new CollectionListAdapter (mCollectionView
					.getContext (), mCollections);
			mAdapter.setOnCollectionClickListener (
					mCollectionView.getCollectionClickListener ());
			mCollectionView.setAdapterForRecyclerView (mAdapter);
			mCollectionView.hideRefreshIndicator ();
		}
	};

	public CollectionPresenterImpl (CollectionView collectionView) {
		mCollectionView = collectionView;
	}

	@Override
	public void onActivityCreated (Bundle savedInstanceState) {
		mCollectionView.initializeRecyclerView ();
		initializeAdapter ();

		if (savedInstanceState == null) {
			mCollectionView.showRefreshIndicator ();
			getCollections ();
		} else {
			restoreInstanceState (savedInstanceState);
			getCache ();
		}
	}

	private void getCollections () {
		mPHService = new PHService ();
		mCollectionsObservable = mPHService
				.getCollections ()
				.subscribeOn (Schedulers.from (AsyncTask.THREAD_POOL_EXECUTOR))
				.observeOn (AndroidSchedulers.mainThread ())
				.cache ();
		mSubscription = mCollectionsObservable.subscribe (mCollectionsObserver);
	}

	private void getCache () {
		if (mSubscription != null) {
			mSubscription.unsubscribe ();
			mSubscription = null;
		}
		if (mCollectionsObservable != null) {
			mCollectionView.showRefreshIndicator ();
			mSubscription = mCollectionsObservable.subscribe (mCollectionsObserver);
		}
	}

	private void restoreInstanceState (Bundle savedInstanceState) {
		mCollections = Collections.getParcelable (savedInstanceState);

		mAdapter = new CollectionListAdapter (mCollectionView.getContext (), mCollections);
		mAdapter.setOnCollectionClickListener (
				mCollectionView.getCollectionClickListener ());
		mCollectionView.setAdapterForRecyclerView (mAdapter);
	}

	private void initializeAdapter () {
		mCollections = new Collections ();
		mAdapter = new CollectionListAdapter (mCollectionView.getContext (), mCollections);
		mAdapter.setOnCollectionClickListener (
				mCollectionView.getCollectionClickListener ());
		mCollectionView.setAdapterForRecyclerView (mAdapter);
	}

	@Override
	public void onSaveInstanceState (Bundle outState) {
		Collections.putParcelable (outState, mCollections);
	}

	@Override
	public void onDestroy () {
		if (mSubscription != null) {
			mSubscription.unsubscribe ();
			mSubscription = null;
		}
	}

	@Override
	public void onCollectionClick (View view, int position) {
		Collection collection = mCollections.getCollections ().get (position);
		Intent openUrl = new Intent (mCollectionView.getContext (), CollectionActivity.class);
		activityExitAnimation (view, collection, openUrl);
	}

	private void activityExitAnimation (View v, Collection collection, Intent i) {
		int[] startingLocation = new int[2];
		v.getLocationOnScreen (startingLocation);
		i.putExtra (CollectionActivity.ARG_DRAWING_START_LOCATION, startingLocation[1]);
		i.putExtra ("collection", collection);
		mCollectionView.getContext ().startActivity (i);
		mCollectionView.hideActivityTransition ();
	}
}
