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
import com.jaspervanriet.huntingthatproduct.Utils.Constants;

import retrofit.RestAdapter;
import rx.Observable;

public class SessionService {

	public static final Authentication authentication = new Authentication (
			Constants.CLIENT_ID, Constants.CLIENT_SECRET, Constants.GRANT_TYPE);

	public Observable<AccessToken> askForToken () {
		PHApi api = new RestAdapter.Builder ()
				.setEndpoint (Constants.API_URL)
				.build ()
				.create (PHApi.class);
		return api.getAccessToken (authentication);
	}
}
