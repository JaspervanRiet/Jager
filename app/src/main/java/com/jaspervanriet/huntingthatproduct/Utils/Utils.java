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
import android.content.res.Resources;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

public class Utils {
	private static int screenWidth = 0;
	private static int screenHeight = 0;

	public static int dpToPx (int dp) {
		return (int) (dp * Resources.getSystem ().getDisplayMetrics ().density);
	}

	public static int getScreenHeight (Context c) {
		if (screenHeight == 0) {
			WindowManager wm = (WindowManager) c.getSystemService (Context.WINDOW_SERVICE);
			Display display = wm.getDefaultDisplay ();
			Point size = new Point ();
			display.getSize (size);
			screenHeight = size.y;
		}

		return screenHeight;
	}

	public static int getScreenWidth (Context c) {
		if (screenWidth == 0) {
			WindowManager wm = (WindowManager) c.getSystemService (Context.WINDOW_SERVICE);
			Display display = wm.getDefaultDisplay ();
			Point size = new Point ();
			display.getSize (size);
			screenWidth = size.x;
		}

		return screenWidth;
	}
}
