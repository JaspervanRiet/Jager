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

package com.jaspervanriet.huntingthatproduct.Views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.RelativeLayout;

import com.jaspervanriet.huntingthatproduct.Utils.ViewUtils;

public class FeedContextMenuManager extends RecyclerView.OnScrollListener
		implements View.OnAttachStateChangeListener {

	private static FeedContextMenuManager instance;

	private FeedContextMenu contextMenuView;

	private boolean isContextMenuDismissing;
	private boolean isContextMenuShowing;

	public static FeedContextMenuManager getInstance () {
		if (instance == null) {
			instance = new FeedContextMenuManager ();
		}
		return instance;
	}

	private FeedContextMenuManager () {

	}

	public void toggleContextMenuFromView (View openingView, int feedItem,
										   FeedContextMenu.OnFeedContextMenuItemClickListener
												   listener,
										   boolean isMainActivity) {
		if (contextMenuView == null) {
			showContextMenuFromView (openingView, feedItem, listener, isMainActivity);
		} else {
			hideContextMenu ();
		}
	}

	private void showContextMenuFromView (final View openingView, int feedItem,
										  FeedContextMenu.OnFeedContextMenuItemClickListener
												  listener,
										  boolean isMainActivity) {
		if (!isContextMenuShowing) {
			isContextMenuShowing = true;
			contextMenuView = new FeedContextMenu (openingView.getContext ());
			contextMenuView.bindToItem (feedItem);
			contextMenuView.addOnAttachStateChangeListener (this);
			contextMenuView.setOnFeedMenuItemClickListener (listener);
			ViewGroup viewGroup = (ViewGroup) openingView.getRootView ().findViewById (android.R
					.id.content);
			RelativeLayout relativeLayout;
			if (isMainActivity) {
				DrawerLayout drawerLayout = (DrawerLayout) viewGroup.getChildAt (0);
				relativeLayout = (RelativeLayout) drawerLayout.getChildAt (0);
			} else {
				relativeLayout = (RelativeLayout) viewGroup
						.getChildAt (0);
			}
			relativeLayout.addView (contextMenuView);
			contextMenuView.getViewTreeObserver ().addOnPreDrawListener (new ViewTreeObserver
					.OnPreDrawListener () {
				@Override
				public boolean onPreDraw () {
					contextMenuView.getViewTreeObserver ().removeOnPreDrawListener (this);
					setupContextMenuInitialPosition (openingView);
					performShowAnimation ();
					return false;
				}
			});
		}
	}

	private void setupContextMenuInitialPosition (View openingView) {
		final int[] openingViewLocation = new int[2];
		openingView.getLocationOnScreen (openingViewLocation);
		int additionalBottomMargin = ViewUtils.dpToPx (16);
		contextMenuView.setTranslationX (openingViewLocation[0] - contextMenuView.getWidth () / 3);
		contextMenuView.setTranslationY (
				openingViewLocation[1] - contextMenuView.getHeight () - additionalBottomMargin);
	}

	private void performShowAnimation () {
		contextMenuView.setPivotX (contextMenuView.getWidth () / 2);
		contextMenuView.setPivotY (contextMenuView.getHeight ());
		contextMenuView.setScaleX (0.1f);
		contextMenuView.setScaleY (0.1f);
		contextMenuView.animate ()
				.scaleX (1f).scaleY (1f)
				.setDuration (150)
				.setInterpolator (new OvershootInterpolator ())
				.setListener (new AnimatorListenerAdapter () {
					@Override
					public void onAnimationEnd (Animator animation) {
						isContextMenuShowing = false;
					}
				});
	}

	public void hideContextMenu () {
		if (!isContextMenuDismissing) {
			isContextMenuDismissing = true;
			performDismissAnimation ();
		}
	}

	private void performDismissAnimation () {
		contextMenuView.setPivotX (contextMenuView.getWidth () / 2);
		contextMenuView.setPivotY (contextMenuView.getHeight ());
		contextMenuView.animate ()
				.scaleX (0.1f).scaleY (0.1f)
				.setDuration (150)
				.setInterpolator (new AccelerateInterpolator ())
				.setStartDelay (100)
				.setListener (new AnimatorListenerAdapter () {
					@Override
					public void onAnimationEnd (Animator animation) {
						if (contextMenuView != null) {
							contextMenuView.dismiss ();
						}
						isContextMenuDismissing = false;
					}
				});
	}

	public void onScrolled (RecyclerView recyclerView, int dx, int dy) {
		if (contextMenuView != null) {
			hideContextMenu ();
			contextMenuView.setTranslationY (contextMenuView.getTranslationY () - dy);
		}
	}

	@Override
	public void onViewAttachedToWindow (View v) {

	}

	@Override
	public void onViewDetachedFromWindow (View v) {
		contextMenuView = null;
	}
}