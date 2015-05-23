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
import com.jaspervanriet.huntingthatproduct.Entities.Collection;
import com.jaspervanriet.huntingthatproduct.Entities.Collections;
import com.jaspervanriet.huntingthatproduct.Entities.Comments;
import com.jaspervanriet.huntingthatproduct.Entities.Posts;
import com.jaspervanriet.huntingthatproduct.Utils.CollectionDeserializer;
import com.jaspervanriet.huntingthatproduct.Utils.Constants;

import rx.Observable;

public class PHService {

	private static final SessionService sessionService = new SessionService ();
	private static final AccessTokenProvider accessTokenProvider = new AccessTokenProvider ();

	public PHService () {
	}

	public Observable<Posts> getPosts (String date) {
		PHApi api = ServiceGenerator.createService (PHApi.class, Constants.API_URL);
		if (date == null) {
			return api.getPosts ().retryWhen (new RetryWithSessionRefresh
					(sessionService, accessTokenProvider));
		} else {
			return api.getPostsByDate (date).retryWhen (new RetryWithSessionRefresh
					(sessionService, accessTokenProvider));
		}
	}

	public Observable<Comments> getComments (int productId) {
		PHApi api = ServiceGenerator.createService (PHApi.class, Constants.API_URL);
		return api.getComments (productId);
	}

	public Observable<Collections> getCollections () {
		PHApi api = ServiceGenerator.createService (PHApi.class, Constants.API_URL);
		return api.getCollections ();
	}

	public Observable<Collection> getCollectionPosts (int collectionId) {
		Gson gson = new GsonBuilder ().registerTypeAdapter (Collection.class, new
				CollectionDeserializer ()).create ();
		PHApi api = ServiceGenerator.createService (PHApi.class, Constants.API_URL, gson);
		return api.getCollectionPosts (collectionId);
	}

}
