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

package com.jaspervanriet.huntingthatproduct.Utils;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.jaspervanriet.huntingthatproduct.Entities.Collection;

import java.lang.reflect.Type;

public class CollectionDeserializer implements JsonDeserializer<Collection> {

	@Override
	public Collection deserialize (JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonElement collection = json.getAsJsonObject ().get ("collection");
		return new Gson ().fromJson (collection, Collection.class);
	}
}