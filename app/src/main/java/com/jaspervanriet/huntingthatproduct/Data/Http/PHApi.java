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

import com.jaspervanriet.huntingthatproduct.Entities.AccessToken;
import com.jaspervanriet.huntingthatproduct.Entities.Authentication;
import com.jaspervanriet.huntingthatproduct.Entities.Collection;
import com.jaspervanriet.huntingthatproduct.Entities.Collections;
import com.jaspervanriet.huntingthatproduct.Entities.Comments;
import com.jaspervanriet.huntingthatproduct.Entities.Posts;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public interface PHApi {

	@POST ("/oauth/token")
	Observable<AccessToken> getAccessToken (@Body Authentication auth);

	@GET ("/posts")
	Observable<Posts> getPosts ();

	@GET ("/posts")
	Observable<Posts> getPostsByDate (@Query ("day") String date);

	@GET ("/posts/{product-id}/comments")
	Observable<Comments> getComments (@Path ("product-id") int productId);

	@GET ("/collections?search[featured]=true")
	Observable<Collections> getCollections ();

	@GET ("/collections/{collection-id}")
	Observable<Collection> getCollectionPosts (@Path ("collection-id") int collectionId);
}
