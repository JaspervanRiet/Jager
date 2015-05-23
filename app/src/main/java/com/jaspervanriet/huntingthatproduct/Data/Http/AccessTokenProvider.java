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

import retrofit.RequestInterceptor;

public class AccessTokenProvider implements RequestInterceptor {
	private static String sessionToken = "";

	public AccessTokenProvider () {

	}

	public void setSessionToken (AccessToken token) {
		sessionToken = token.getAccessToken ();
	}

	public void resetSessionToken () {
		sessionToken = "";
	}

	@Override
	public void intercept (RequestFacade requestFacade) {
		requestFacade.addHeader ("Accept", "application/json");
		requestFacade.addHeader ("Authorization", "Bearer " + sessionToken);
	}
}
