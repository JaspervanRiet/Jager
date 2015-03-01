package com.jaspervanriet.huntingthatproduct.Views;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.jaspervanriet.huntingthatproduct.Activities.MainActivity;

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