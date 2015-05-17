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

package com.jaspervanriet.huntingthatproduct.Data.Http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jaspervanriet.huntingthatproduct.Entities.AccessToken;
import com.jaspervanriet.huntingthatproduct.Entities.Authentication;
import com.jaspervanriet.huntingthatproduct.Entities.Collection;
import com.jaspervanriet.huntingthatproduct.Entities.Collections;
import com.jaspervanriet.huntingthatproduct.Entities.Comments;
import com.jaspervanriet.huntingthatproduct.Entities.Posts;
import com.jaspervanriet.huntingthatproduct.Utils.CollectionDeserializer;
import com.jaspervanriet.huntingthatproduct.Utils.Constants;

import rx.Observable;

public class PHService {

	private Authentication mAuthentication;

	public PHService (Authentication authentication) {
		mAuthentication = authentication;
	}

	public Observable<AccessToken> askForToken () {
		PHApi api = ServiceGenerator.createService (PHApi.class, Constants.API_URL);
		return api.getAccessToken (mAuthentication);
	}

	public Observable<Posts> getPosts (AccessToken token, String date) {
		PHApi api = ServiceGenerator.createService (PHApi.class, Constants.API_URL,
				token);
		if (date == null) {
			return api.getPosts ();
		} else {
			return api.getPostsByDate (date);
		}
	}

	public Observable<Comments> getComments (AccessToken token, int productId) {
		PHApi api = ServiceGenerator.createService (PHApi.class, Constants.API_URL,
				token);
		return api.getComments (productId);
	}

	public Observable<Collections> getCollections (AccessToken token) {
		PHApi api = ServiceGenerator.createService (PHApi.class, Constants.API_URL,
				token);
		return api.getCollections ();
	}

	public Observable<Collection> getCollectionPosts (AccessToken token, int collectionId) {
		Gson gson = new GsonBuilder ().registerTypeAdapter (Collection.class, new
				CollectionDeserializer ()).create ();
		PHApi api = ServiceGenerator.createService (PHApi.class, Constants.API_URL,
				token, gson);
		return api.getCollectionPosts (collectionId);
	}

}
