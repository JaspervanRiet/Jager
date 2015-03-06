package com.jaspervanriet.huntingthatproduct.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jaspervanriet.huntingthatproduct.Adapters.CollectionListAdapter;
import com.jaspervanriet.huntingthatproduct.Classes.Collection;
import com.jaspervanriet.huntingthatproduct.R;
import com.jaspervanriet.huntingthatproduct.Utils.Constants;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class CollectionsListActivity extends BaseActivity implements
		CollectionListAdapter.OnCollectionClickListener {

	@InjectView (android.R.id.list)
	RecyclerView mRecyclerView;
	@InjectView (R.id.collections_list_progress_wheel)
	ProgressWheel progressWheel;

	private CollectionListAdapter mListAdapter;
	private ArrayList<Collection> mCollections = new ArrayList<> ();

	private void completeRefresh () {
		if (mCollections.size () != 0) {
			mCollections.clear ();
			mListAdapter.notifyDataSetChanged ();
		}
		progressWheel.setVisibility (View.VISIBLE);
		progressWheel.spin ();
		getCollections ();
	}

	private void getCollections () {
		Ion.with (this).load (Constants.API_URL + "collections?search[featured]=true")
				.setHeader ("Authorization", "Bearer " + Constants.CLIENT_TOKEN)
				.asJsonObject ()
				.setCallback (new FutureCallback<JsonObject> () {
					@Override
					public void onCompleted (Exception e, JsonObject result) {
						if (e != null && e instanceof TimeoutException) {
							Toast.makeText (CollectionsListActivity.this,
									getResources ().getString
											(R.string.error_connection),
									Toast.LENGTH_SHORT).show ();
							return;
						}
						if (result != null && result.has ("collections")) {
							int i;
							JsonArray collections = result.getAsJsonArray
									("collections");
							for (i = 0; i < collections.size (); i++) {
								JsonObject obj = collections.get (i)
										.getAsJsonObject ();
								Collection collection = new Collection (obj);
								mCollections.add (collection);
							}
							mListAdapter.notifyDataSetChanged ();
							progressWheel.stopSpinning ();
							progressWheel.setVisibility (View.INVISIBLE);
						}
					}
				});
	}

	private void setupRecyclerView () {
		mRecyclerView.setHasFixedSize (true);
		mRecyclerView.setItemAnimator (new DefaultItemAnimator ());
		mRecyclerView.setLayoutManager (getLayoutManager ());
		mRecyclerView.setAdapter (mListAdapter);
	}

	private LinearLayoutManager getLayoutManager () {
		LinearLayoutManager layoutManager = new LinearLayoutManager (this);
		layoutManager.setOrientation (LinearLayoutManager.VERTICAL);
		return layoutManager;
	}

	private void activityExitAnimation (View v, Collection collection, Intent i) {
		int[] startingLocation = new int[2];
		v.getLocationOnScreen (startingLocation);
		i.putExtra (CollectionActivity.ARG_DRAWING_START_LOCATION, startingLocation[1]);
		i.putExtra ("collection", collection);
		startActivity (i);
		overridePendingTransition (0, 0);
	}

	@Override
	protected int getSelfNavDrawerItem () {
		return NAVDRAWER_ITEM_COLLECTIONS;
	}

	/*
	 * Adapter onClickListener
	 */

	@Override
	public void onCollectionClick (View view, int position) {
		Collection collection = mCollections.get (position);
		Intent openUrl = new Intent (this, CollectionActivity.class);
		activityExitAnimation (view, collection, openUrl);
	}

	/*
	 * App lifecycle
	 */

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_collections_list);
		super.onCreateDrawer ();
		ButterKnife.inject (this);
		setToolBar ();

		mListAdapter = new CollectionListAdapter (this, mCollections);
		mListAdapter.setOnCollectionClickListener (this);
		progressWheel.setBarColor (getResources ().getColor (R.color.primary_accent));
		setupRecyclerView ();
	}

	@Override
	public void onStart () {
		super.onStart ();
		completeRefresh ();
	}
}
