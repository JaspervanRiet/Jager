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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

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
	private boolean mBackPressed = false;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_web);
		ButterKnife.inject (this);

		mProduct = getIntent ().getParcelableExtra ("product");
		expandAnimation (savedInstanceState);
		setupToolbar ();
		setupWebView ();
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
	public void onBackPressed () {
		if (!mBackPressed) {
			goBack ();
		}
	}

	@Override
	public boolean onOptionsItemSelected (MenuItem item) {
		int itemId = item.getItemId ();
		switch (itemId) {
			case android.R.id.home:
				if (!mBackPressed) {
					goBack ();
				}
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
		i.putExtra (Intent.EXTRA_SUBJECT, mProduct.title);
		i.putExtra (Intent.EXTRA_TEXT, mProduct.productUrl);
		return i;
	}

	private void goBack () {
		mBackPressed = true;
		mWebView.animate ()
				.translationY (Utils.getScreenHeight (this))
				.setDuration (200)
				.setListener (new AnimatorListenerAdapter () {
					@Override
					public void onAnimationEnd (Animator animation) {
						WebActivity.super.onBackPressed ();
						overridePendingTransition (0, 0);
						finish ();
					}
				})
				.start ();
	}

	private void setupToolbar () {
		setSupportActionBar (mToolBar);
		ActionBar actionBar = getSupportActionBar ();
		actionBar.setDisplayHomeAsUpEnabled (true);
		actionBar.setTitle (getString (R.string.actionbar_loading));
		actionBar.setElevation (5);
	}

	private void setupWebView () {
		mWebView.setWebViewClient (new WebViewClient () {
			@Override
			public void onLoadResource (WebView view, String url) {
				if (url.contains ("https://play.google" +
						".com/store/apps/details?")) {
					url.replace ("https://play.google" +
							".com/store/apps/details?", "market://details?");
					view.stopLoading ();
					Intent intent = new Intent (Intent.ACTION_VIEW).setData (Uri
							.parse (url));
					startActivity (intent);
				}
			}

			@Override
			public void onPageFinished (WebView view, String url) {
				getSupportActionBar ().setTitle (mProduct.title);
			}

			@Override
			public void onReceivedSslError (WebView view, @NonNull SslErrorHandler handler,
											SslError error) {
				Toast.makeText (WebActivity.this, getString (R.string.error_ssl),
						Toast.LENGTH_LONG).show ();
			}
		});
		mWebView.loadUrl (mProduct.productUrl);
		mWebView.getSettings ().setBuiltInZoomControls (true);
		mWebView.getSettings ().setDisplayZoomControls (false);
		mWebView.getSettings ().getJavaScriptEnabled ();
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
