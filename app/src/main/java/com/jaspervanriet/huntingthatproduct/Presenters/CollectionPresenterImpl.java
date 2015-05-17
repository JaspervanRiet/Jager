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

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.jaspervanriet.huntingthatproduct.Data.Http.PHService;
import com.jaspervanriet.huntingthatproduct.Entities.Authentication;
import com.jaspervanriet.huntingthatproduct.Entities.Collection;
import com.jaspervanriet.huntingthatproduct.Entities.Collections;
import com.jaspervanriet.huntingthatproduct.Utils.Constants;
import com.jaspervanriet.huntingthatproduct.Views.Activities.CollectionActivity;
import com.jaspervanriet.huntingthatproduct.Views.Adapters.CollectionListAdapter;
import com.jaspervanriet.huntingthatproduct.Views.CollectionView;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CollectionPresenterImpl implements CollectionPresenter {

	private CollectionView mCollectionView;
	private CollectionListAdapter mAdapter;
	private PHService mPHService;
	private Collections mCollections;
	private Subscription mSubscription;

	public CollectionPresenterImpl (CollectionView collectionView) {
		mCollectionView = collectionView;
	}

	@Override
	public void onActivityCreated (Bundle savedInstanceState) {
		mCollectionView.initializeRecyclerView ();
		mCollectionView.showRefreshIndicator ();

		if (savedInstanceState == null) {
			getCollections ();
		}
	}

	private void getCollections () {
		mPHService = new PHService (new Authentication (Constants.CLIENT_ID,
				Constants.CLIENT_SECRET, Constants.GRANT_TYPE));
		mSubscription = mPHService.askForToken ().flatMap (token -> mPHService
				.getCollections (token)
				.subscribeOn (Schedulers.from (AsyncTask.THREAD_POOL_EXECUTOR))
				.observeOn (AndroidSchedulers.mainThread ()))
				.subscribe (new Observer<Collections> () {
					@Override
					public void onCompleted () {

					}

					@Override
					public void onError (Throwable e) {

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
				});
	}

	@Override
	public void onResume () {

	}

	@Override
	public void onPause () {

	}

	@Override
	public void onSaveInstanceState (Bundle outState) {

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
