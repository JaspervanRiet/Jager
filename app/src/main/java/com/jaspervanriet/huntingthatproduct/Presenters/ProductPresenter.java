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

package com.jaspervanriet.huntingthatproduct.Presenters;

import android.os.Bundle;
import android.view.View;

import com.jaspervanriet.huntingthatproduct.Entities.Product;

public interface ProductPresenter {

	void onActivityCreated (Bundle savedInstanceState);

	void onSaveInstanceState (Bundle outState);

	void onDestroy ();

	void onRefresh ();

	void onDateSet (int year, int monthOfYear, int dayOfMonth);

	void onShareClick (int feedItem);

	void onImageClick (View v, int feedItem);

	void onCommentsClick (View v, Product product);
}
