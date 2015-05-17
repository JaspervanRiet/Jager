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
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.jaspervanriet.huntingthatproduct.Presenters.CollectionPresenterImpl;
import com.jaspervanriet.huntingthatproduct.R;
import com.jaspervanriet.huntingthatproduct.Views.Adapters.CollectionListAdapter;
import com.jaspervanriet.huntingthatproduct.Views.CollectionView;
import com.pnikosis.materialishprogress.ProgressWheel;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class CollectionsListActivity extends DrawerActivity implements
		CollectionListAdapter.OnCollectionClickListener, CollectionView {

	@InjectView (android.R.id.list)
	RecyclerView mRecyclerView;
	@InjectView (R.id.collections_list_progress_wheel)
	ProgressWheel progressWheel;

	private CollectionPresenterImpl mPresenter;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_collections_list);
		ButterKnife.inject (this);
		setToolBar ();
		progressWheel.setBarColor (getResources ().getColor (R.color.primary_accent));

		mPresenter = new CollectionPresenterImpl (this);
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
		super.onDestroy ();
	}

	@Override
	protected int getSelfNavDrawerItem () {
		return NAVDRAWER_ITEM_COLLECTIONS;
	}

	@Override
	public void onCollectionClick (View view, int position) {
		mPresenter.onCollectionClick (view, position);
	}

	@Override
	public void initializeRecyclerView () {
		mRecyclerView.setHasFixedSize (true);
		mRecyclerView.setItemAnimator (new DefaultItemAnimator ());
		mRecyclerView.setLayoutManager (getLayoutManager ());
	}

	@Override
	public void setAdapterForRecyclerView (CollectionListAdapter adapter) {
		mRecyclerView.setAdapter (adapter);
	}

	@Override
	public void setToolbarTitle (String title) {

	}

	@Override
	public void showRefreshIndicator () {
		progressWheel.setVisibility (View.VISIBLE);
		progressWheel.spin ();
	}

	@Override
	public void hideRefreshIndicator () {
		progressWheel.stopSpinning ();
		progressWheel.setVisibility (View.INVISIBLE);
	}

	@Override
	public void hideActivityTransition () {
		overridePendingTransition (0, 0);
	}

	@Override
	public Context getContext () {
		return this;
	}

	@Override
	public CollectionListAdapter.OnCollectionClickListener getCollectionClickListener () {
		return this;
	}
}
