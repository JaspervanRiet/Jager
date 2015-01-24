/*
 * Copyright (c) Jasper van Riet 2015.
 */

package com.jaspervanriet.huntingthatproduct.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.jaspervanriet.huntingthatproduct.Classes.Product;
import com.jaspervanriet.huntingthatproduct.R;
import com.jaspervanriet.huntingthatproduct.Utils.Utils;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class WebActivity extends ActionBarActivity {

	public static final String ARG_DRAWING_START_LOCATION = "arg_drawing_start_location";
	private static final int ANIM_LAYOUT_INTRO_DURATION = 250;

	@InjectView (R.id.toolbar)
	Toolbar mToolBar;
	@InjectView (R.id.web_webview)
	WebView mWebView;

	private Product mProduct;
	private int mDrawingStartLocation;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_web);
		ButterKnife.inject (this);

		//TODO: Open in browser + share

		mProduct = getIntent ().getParcelableExtra ("product");
		expandAnimation (savedInstanceState);
		setupToolbar ();
		setupWebView ();
	}

	@Override
	public void onBackPressed () {
		goBack ();
	}

	@Override
	public boolean onCreateOptionsMenu (Menu menu) {
		getMenuInflater ().inflate (R.menu.web_menu, menu);
		MenuItem item = menu.findItem (R.id.menu_web_share);
		ShareActionProvider shareActionProvider = new ShareActionProvider (this);
		shareActionProvider.setShareIntent (getShareIntent ());
		MenuItemCompat.setActionProvider (item, shareActionProvider);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected (MenuItem item) {
		int itemId = item.getItemId ();
		switch (itemId) {
			case android.R.id.home:
				goBack ();
				return true;
			case R.id.menu_browser:
				openInBrowser ();
				return true;
			default:
				return false;
		}
	}

	private void openInBrowser () {
		Intent intent = new Intent (Intent.ACTION_VIEW).setData (Uri
				.parse (mProduct.productUrl));
		startActivity (intent);
	}

	private Intent getShareIntent () {
		Intent i = new Intent (Intent.ACTION_SEND);
		i.setType ("text/plain");
		i.putExtra (Intent.EXTRA_SUBJECT, mProduct.productUrl);
		i.putExtra (Intent.EXTRA_TEXT, mProduct.productUrl);
		return i;
	}

	private void goBack () {
		mWebView.animate ()
				.translationY (Utils.getScreenHeight (this))
				.setDuration (200)
				.setListener (new AnimatorListenerAdapter () {
					@Override
					public void onAnimationEnd (Animator animation) {
						WebActivity.super.onBackPressed ();
						overridePendingTransition (0, 0);
					}
				})
				.start ();
	}

	private void setupToolbar () {
		setSupportActionBar (mToolBar);
		ActionBar actionBar = getSupportActionBar ();
		actionBar.setDisplayHomeAsUpEnabled (true);
		actionBar.setTitle (mProduct.title);
		actionBar.setElevation (5);
	}

	private void setupWebView () {
		mWebView.setWebViewClient (new WebViewClient ());
		mWebView.loadUrl (mProduct.productUrl);
		mWebView.getSettings ().setBuiltInZoomControls (true);
		mWebView.getSettings ().setDisplayZoomControls (false);
	}

	private void expandAnimation (Bundle savedInstanceState) {
		mDrawingStartLocation = getIntent ().getIntExtra (ARG_DRAWING_START_LOCATION, 0);
		if (savedInstanceState == null) {
			mWebView.getViewTreeObserver ().addOnPreDrawListener (new ViewTreeObserver
					.OnPreDrawListener () {
				@Override
				public boolean onPreDraw () {
					mWebView.getViewTreeObserver ().removeOnPreDrawListener (this);
					startIntroAnimation ();
					return true;
				}
			});
		}
	}

	private void startIntroAnimation () {
		mWebView.setScaleY (0.1f);
		mWebView.setPivotY (mDrawingStartLocation);
		mWebView.animate ()
				.scaleY (1)
				.setDuration (ANIM_LAYOUT_INTRO_DURATION)
				.setInterpolator (new AccelerateInterpolator ())
				.start ();
	}
}
