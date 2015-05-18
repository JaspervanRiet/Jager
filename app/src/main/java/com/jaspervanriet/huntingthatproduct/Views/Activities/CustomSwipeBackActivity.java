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

package com.jaspervanriet.huntingthatproduct.Views.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.Utils;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityBase;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityHelper;

public class CustomSwipeBackActivity extends AppCompatActivity implements SwipeBackActivityBase {
	private SwipeBackActivityHelper mHelper;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		mHelper = new SwipeBackActivityHelper (this);
		mHelper.onActivityCreate ();
	}

	@Override
	protected void onPostCreate (Bundle savedInstanceState) {
		super.onPostCreate (savedInstanceState);
		mHelper.onPostCreate ();
	}

	@Override
	public View findViewById (int id) {
		View v = super.findViewById (id);
		if (v == null && mHelper != null)
			return mHelper.findViewById (id);
		return v;
	}

	@Override
	public SwipeBackLayout getSwipeBackLayout () {
		return mHelper.getSwipeBackLayout ();
	}

	@Override
	public void setSwipeBackEnable (boolean enable) {
		getSwipeBackLayout ().setEnableGesture (enable);
	}

	@Override
	public void scrollToFinishActivity () {
		Utils.convertActivityToTranslucent (this);
		getSwipeBackLayout ().scrollToFinishActivity ();
	}
}