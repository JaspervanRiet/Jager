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

public class Authentication {

	@SerializedName ("client_id")
	private String clientId;
	@SerializedName ("client_secret")
	private String clientSecret;
	@SerializedName ("grant_type")
	private String grantType;

	public Authentication (String clientId, String clientSecret, String grantType) {
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.grantType = grantType;
	}

	public String getGrantType () {
		return grantType;
	}

	public void setGrantType (String grantType) {
		this.grantType = grantType;
	}

	public String getClientId () {
		return clientId;
	}

	public void setClientId (String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret () {
		return clientSecret;
	}

	public void setClientSecret (String clientSecret) {
		this.clientSecret = clientSecret;
	}
}