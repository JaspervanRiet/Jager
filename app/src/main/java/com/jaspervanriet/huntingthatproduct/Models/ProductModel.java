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

package com.jaspervanriet.huntingthatproduct.Models;

import android.content.Context;
import android.os.Handler.Callback;
import android.os.Message;

import com.crashlytics.android.Crashlytics;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jaspervanriet.huntingthatproduct.Activities.MainActivity;
import com.jaspervanriet.huntingthatproduct.Entities.Product;
import com.jaspervanriet.huntingthatproduct.Utils.Constants;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.nanotasks.BackgroundWork;
import com.nanotasks.Completion;
import com.nanotasks.Tasks;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import io.realm.Realm;
import io.realm.RealmResults;

public class ProductModel {

	private static Callback sCallback;
	private static Context sContext;
	private static String sUrl;

	/**
	 * Retrieves products from Product Hunt API and saves them to Realm
	 *
	 * @param context
	 * @param callback
	 * @param postsUrl url to load to get posts from Product Hunt Api
	 */
	public static void getData (Context context, Callback callback, String postsUrl) {
		sContext = context;
		sCallback = callback;
		sUrl = postsUrl;
		if (Constants.TOKEN_EXPIRES < System.currentTimeMillis ()) {
			getAuthToken ();
		} else {
			getPosts (sUrl);
		}
	}

	/**
	 * Retrieves authtoken from Product Hunt using API keys
	 */
	private static void getAuthToken () {
		JsonObject json = new JsonObject ();
		json.addProperty ("client_id", Constants.CLIENT_ID);
		json.addProperty ("client_secret", Constants.CLIENT_SECRET);
		json.addProperty ("grant_type", Constants.GRANT_TYPE);

		Ion.with (sContext).load (Constants.API_TOKEN_URL)
				.setJsonObjectBody (json)
				.asJsonObject ()
				.setCallback (new FutureCallback<JsonObject> () {
					@Override
					public void onCompleted (Exception e, JsonObject result) {
						if (result != null && result.has ("access_token")) {
							Constants.CLIENT_TOKEN = result.get ("access_token")
									.getAsString ();
							Constants.TOKEN_EXPIRES = System.currentTimeMillis () +
									(long) result.get ("expires_in").getAsInt ();
							getPosts (sUrl);
						}
					}
				});
	}

	/**
	 * Retrieves posts from Product Hunt
	 */
	private static void getPosts (String url) {
		Ion.with (sContext).load (url)
				.setHeader ("Authorization", "Bearer " + Constants.CLIENT_TOKEN)
				.asJsonObject ()
				.setCallback (new FutureCallback<JsonObject> () {
					@Override
					public void onCompleted (Exception e, JsonObject result) {
						if (e != null && e instanceof TimeoutException) {
							return;
						}
						if (result != null && result.has ("posts")) {
							processPosts (result);
						}
					}
				});
	}

	private static void processPosts (final JsonObject jsonResult) {
		Tasks.executeInBackground (sContext,
				new BackgroundWork<Void> () {
					@Override
					public Void doInBackground () throws Exception {
						createProductsFromPosts (sContext, jsonResult);
						return null;
					}
				}, new Completion<Void> () {
					@Override
					public void onSuccess (Context context, Void result) {
						if (!MainActivity.activityIsDestroyed ()) {
							sCallback.handleMessage (new Message ());
						}
					}

					@Override
					public void onError (Context context, Exception e) {
						Crashlytics.logException (e);
					}
				});
	}

	/**
	 * @param realm realm instance to use
	 * @param id    product id
	 * @return product with the specified id
	 */
	public static Product findProductById (Realm realm, int id) {
		RealmResults<Product> result = realm.where (Product.class)
				.equalTo ("id", id)
				.findAll ();

		if (result.size () != 0) {
			return result.get (0);
		}
		return null;
	}

	/**
	 * Creates products from JsonObject and saves them to the Realm Database
	 *
	 * @param context
	 * @param posts   JsonObject filled with posts retrieved from API
	 */
	private static void createProductsFromPosts (Context context, JsonObject posts) {
		Realm realm = Realm.getInstance (context);
		JsonArray products = posts.getAsJsonArray ("posts");
		ArrayList<Integer> readProductIds = ProductDatabase.getReadProductsIds (realm);
		for (int i = 0; i < products.size (); i++) {
			JsonObject obj = products.get (i).getAsJsonObject ();
			Product product = new Product (obj);
			if (readProductIds.contains (product.getId ())) {
				product.setRead (true);
			}
			product.setRank (i);
			ProductDatabase.saveProduct (product, realm);
		}
		realm.close ();
	}
}
