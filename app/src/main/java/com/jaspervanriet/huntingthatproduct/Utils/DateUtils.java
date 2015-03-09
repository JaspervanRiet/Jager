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

import android.content.Context;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtils {

	public static String getTodaysDate () {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat ("yyyy-MM-dd");
		simpleDateFormat.setTimeZone (TimeZone.getTimeZone ("America/Los_Angeles"));
		return simpleDateFormat.format (new Date ()) + "";
	}

	public static String getLastUsedDate (Context context, String date) {
		return context.getSharedPreferences ("PREFERENCE",
				Context.MODE_PRIVATE).getString ("saved_date", date);
	}

	public static String getMonth (int month) {
		return new DateFormatSymbols ().getMonths ()[month];
	}

	public static String getDateFormattedString (Calendar calendar) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat ("yyyy-MM-dd");
		return simpleDateFormat.format (calendar.getTime ());
	}
}
