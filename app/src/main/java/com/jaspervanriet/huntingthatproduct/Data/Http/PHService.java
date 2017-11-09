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

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jaspervanriet.huntingthatproduct.Entities.Collection;
import com.jaspervanriet.huntingthatproduct.Entities.Collections;
import com.jaspervanriet.huntingthatproduct.Entities.Comments;
import com.jaspervanriet.huntingthatproduct.Entities.Posts;
import com.jaspervanriet.huntingthatproduct.Entities.Topics;
import com.jaspervanriet.huntingthatproduct.Utils.CollectionDeserializer;
import com.jaspervanriet.huntingthatproduct.Utils.Constants;

import rx.Observable;

public class PHService {

	private static final SessionService sessionService = new SessionService ();
	private static final AccessTokenProvider accessTokenProvider = new AccessTokenProvider ();

	public PHService () {
	}

	public Observable<Posts> getPosts (String topic, String date) {
		PHApi api = ServiceGenerator.createService (PHApi.class, Constants.API_URL);
		if (date == null) {
			if (topic == null) {

				return api.getPosts ().retryWhen (new RetryWithSessionRefresh
						(sessionService, accessTokenProvider)).doOnError
						(Crashlytics::logException);
			} else {
				return api.getPostsByTopic (topic.toLowerCase ()).retryWhen (new
						RetryWithSessionRefresh
						(sessionService, accessTokenProvider)).doOnError
						(Crashlytics::logException);
			}
		} else {
			return api.getPostsByDate (topic.toLowerCase (), date).retryWhen (new
					RetryWithSessionRefresh
					(sessionService, accessTokenProvider)).doOnError (Crashlytics::logException);
		}
	}

	public Observable<Topics> getTopics () {
		PHApi api = ServiceGenerator.createService (PHApi.class, Constants.API_URL);
		return api.getTopics ().retryWhen (new RetryWithSessionRefresh (sessionService,
				accessTokenProvider)).doOnError (Crashlytics::logException);
	}

	public Observable<Comments> getComments (int productId) {
		PHApi api = ServiceGenerator.createService (PHApi.class, Constants.API_URL);
		return api.getComments (productId).retryWhen (new RetryWithSessionRefresh
				(sessionService, accessTokenProvider)).doOnError (Crashlytics::logException);
	}

	public Observable<Collections> getCollections () {
		PHApi api = ServiceGenerator.createService (PHApi.class, Constants.API_URL);
		return api.getCollections ().retryWhen (new RetryWithSessionRefresh
				(sessionService, accessTokenProvider)).doOnError (Crashlytics::logException);
	}

	public Observable<Collection> getCollectionPosts (int collectionId) {
		Gson gson = new GsonBuilder ().registerTypeAdapter (Collection.class, new
				CollectionDeserializer ()).create ();
		PHApi api = ServiceGenerator.createService (PHApi.class, Constants.API_URL, gson);
		return api.getCollectionPosts (collectionId).retryWhen (new RetryWithSessionRefresh
				(sessionService, accessTokenProvider)).doOnError (Crashlytics::logException);
	}
}
