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

import com.jaspervanriet.huntingthatproduct.Entities.Product;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Container for interacting with the Realm database
 */
public class ProductDatabase {

	/**
	 * Saves Product to Realm or updates the Realm entry if db contains Product already.
	 *
	 * @param product product to save
	 * @param realm   realm instance to use
	 */
	public static void saveProduct (final Product product, Realm realm) {
		realm.executeTransaction (new Realm.Transaction () {
			@Override
			public void execute (Realm realm) {
				Product realmProduct = realm.copyToRealmOrUpdate (product);
			}
		});
	}

	/**
	 * Retrieves products from Realm and populates ArrayList with them
	 *
	 * @param products list to populate with products
	 * @param realm    realm instance to use
	 * @param date     what date to retrieve products for
	 */
	public static void queryForProducts (ArrayList<Product> products, Realm realm, String date) {
		RealmResults<Product> resultProducts = realm.where (Product.class)
				.equalTo ("date", date)
				.findAll ();
		resultProducts.sort ("rank");
		for (Product product : resultProducts) {
			products.add (product);
		}
	}

	/**
	 * Removes cache older than a day
	 *
	 * @param context
	 * @param savedDate   date app was last used
	 * @param todayString today's date
	 */
	public static void removeOldCache (Context context, final String savedDate,
	                                   String todayString) {
		if (!savedDate.equals (todayString)) {
			Realm realm = Realm.getInstance (context);
			realm.executeTransaction (new Realm.Transaction () {
				@Override
				public void execute (Realm realm) {
					RealmResults<Product> result = realm.where (Product.class)
							.equalTo ("date", savedDate)
							.findAll ();
					result.clear ();
				}
			});
			context.getSharedPreferences ("PREFERENCE", Context.MODE_PRIVATE)
					.edit ()
					.putString ("saved_date", todayString)
					.apply ();
		}
	}

	/**
	 * @param realm realm instance to use
	 * @return ArrayList populated with ids of read products
	 */
	public static ArrayList<Integer> getReadProductsIds (Realm realm) {
		ArrayList<Integer> list = new ArrayList<> ();
		RealmResults<Product> result = realm.where (Product.class)
				.equalTo ("read", true)
				.findAll ();
		for (Product product : result) {
			list.add (product.getId ());
		}
		return list;
	}

	/**
	 * Marks a product as read
	 *
	 * @param product product to mark as read
	 * @param realm   realm instance to use
	 */
	public static void setProductAsRead (final Product product, Realm realm) {
		realm.beginTransaction ();
		RealmResults<Product> result = realm.where (Product.class)
				.equalTo ("id", product.getId ())
				.findAll ();

		for (int i = 0; i < result.size (); i++) {
			Product resultProduct = result.get (i);
			resultProduct.setRead (true);
		}
		realm.commitTransaction ();
	}
}
