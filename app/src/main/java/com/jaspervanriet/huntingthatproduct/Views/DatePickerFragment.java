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

package com.jaspervanriet.huntingthatproduct.Views;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.jaspervanriet.huntingthatproduct.Views.Activities.MainActivity;

import java.util.Calendar;
import java.util.Date;

public class DatePickerFragment extends DialogFragment {

	@Override
	public Dialog onCreateDialog (Bundle savedInstanceState) {
		final Calendar c = Calendar.getInstance ();
		int year = c.get (Calendar.YEAR);
		int month = c.get (Calendar.MONTH);
		int day = c.get (Calendar.DAY_OF_MONTH);
		DatePickerDialog dialog = new DatePickerDialog (getActivity (),
				(MainActivity) getActivity (), year, month, day);
		dialog.getDatePicker ().setMaxDate (new Date ().getTime ());
		return dialog;
	}
}