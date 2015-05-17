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

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.jaspervanriet.huntingthatproduct.Data.Http.PHService;
import com.jaspervanriet.huntingthatproduct.Entities.Authentication;
import com.jaspervanriet.huntingthatproduct.Entities.Comment;
import com.jaspervanriet.huntingthatproduct.Entities.Comments;
import com.jaspervanriet.huntingthatproduct.Entities.Product;
import com.jaspervanriet.huntingthatproduct.Utils.Constants;
import com.jaspervanriet.huntingthatproduct.Views.Adapters.CommentListAdapter;
import com.jaspervanriet.huntingthatproduct.Views.CommentsView;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CommentPresenterImplementation implements CommentPresenter {

	private PHService mPHService;
	private CommentsView mCommentsView;
	private List<Comment> mComments = new ArrayList<> ();
	private CommentListAdapter mAdapter;
	private boolean mBackPressed = false;
	private Product mProduct;
	private Subscription mSubscription;

	public CommentPresenterImplementation (CommentsView commentsView, Product product) {
		mCommentsView = commentsView;
		mProduct = product;
	}

	@Override
	public void onActivityCreated (Bundle savedInstanceState) {
		mCommentsView.initializeToolBar ();
		mCommentsView.setToolbarTitle (mProduct.getName ());
		mCommentsView.initializeRecyclerView ();

		mCommentsView.showRefreshIndicator ();

		if (savedInstanceState == null) {
			getComments (mProduct.getId ());
		}
	}

	private void getComments (int productId) {
		mPHService = new PHService (new Authentication (Constants.CLIENT_ID,
				Constants.CLIENT_SECRET, Constants.GRANT_TYPE));
		mSubscription = mPHService.askForToken ().flatMap (token -> mPHService
				.getComments (token,
						productId)
				.subscribeOn (Schedulers.from (AsyncTask.THREAD_POOL_EXECUTOR))
				.observeOn (AndroidSchedulers.mainThread ()))
				.subscribe (new Observer<Comments> () {
					@Override
					public void onCompleted () {

					}

					@Override
					public void onError (Throwable e) {
						e.printStackTrace ();
					}

					@Override
					public void onNext (Comments comments) {
						if (comments.getCount () == 0) {
							mCommentsView.showEmptyView ();
						} else {
							for (int i = 0; i < comments.getCount (); i++) {
								processComment (comments.getComments ().get (i), 0);
							}
							mAdapter = new CommentListAdapter (mCommentsView.getContext (),
									mComments);
							mCommentsView.setAdapterForRecyclerView (mAdapter);
							mCommentsView.hideRefreshIndicator ();
							if (mComments.isEmpty ()) {
								mCommentsView.showEmptyView ();
							}
						}
					}
				});

	}

	private void processComment (Comment comment, int level) {
		comment.setLevel (level);
		mComments.add (comment);
		if (!comment.getChildComments ().isEmpty ()) {
			++level;
			for (int i = 0; i < comment.getChildCommentCount (); i++) {
				List<Comment> childComments = comment.getChildComments ();
				processComment (childComments.get (i), level);
			}
		}
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
	public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {

	}

	@Override
	public boolean onOptionsItemSelected (MenuItem item) {
		int itemId = item.getItemId ();

		if (itemId == android.R.id.home) {
			if (!mBackPressed) {
				goBack ();
			}
			return true;
		}
		return false;
	}

	@Override
	public void onBackPressed () {
		if (!mBackPressed) {
			goBack ();
		}
	}

	private void goBack () {
		mBackPressed = true;
		mCommentsView.goBack ();
	}
}

