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

package com.jaspervanriet.huntingthatproduct.Entities;

import com.google.gson.annotations.SerializedName;

public class AccessToken {

	@SerializedName ("access_token")
	private String accessToken;
	@SerializedName ("token_type")
	private String tokenType;
	@SerializedName ("expires_in")
	private long expiresIn;
	private String scope;

	public String getAccessToken () {
		return accessToken;
	}

	public void setAccessToken (String accessToken) {
		this.accessToken = accessToken;
	}

	public String getTokenType () {
		return tokenType;
	}

	public void setTokenType (String tokenType) {
		this.tokenType = tokenType;
	}

	public long getExpiresIn () {
		return expiresIn;
	}

	public void setExpiresIn (long expiresIn) {
		this.expiresIn = expiresIn;
	}

	public String getScope () {
		return scope;
	}

	public void setScope (String scope) {
		this.scope = scope;
	}
}
