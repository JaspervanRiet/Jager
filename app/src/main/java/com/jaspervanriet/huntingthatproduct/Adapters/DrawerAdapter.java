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

package com.jaspervanriet.huntingthatproduct.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jaspervanriet.huntingthatproduct.R;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class DrawerAdapter extends BaseAdapter {

	private String[] mDrawerData;
	private Context mContext;
	private int mCurrentActivity;

	public DrawerAdapter (Context context, String[] data, int currentActivity) {
		this.mContext = context;
		this.mDrawerData = data;
		this.mCurrentActivity = currentActivity;
	}

	@Override
	public int getCount () {
		return 3;
	}

	@Override
	public Object getItem (int position) {
		return mDrawerData[position];
	}

	@Override
	public long getItemId (int position) {
		return position;
	}

	@Override
	public View getView (int position, View view, ViewGroup parent) {
		ViewHolder holder;
		if (view != null) {
			holder = (ViewHolder) view.getTag ();
		} else {
			view = LayoutInflater.from (mContext).inflate (R.layout.item_drawer,
					null);
			holder = new ViewHolder (view);
			view.setTag (holder);
		}
		holder.item.setTypeface (Typeface.createFromAsset (
				mContext.getAssets (),
				"fonts/Roboto-Medium.ttf"));
		holder.item.setText (mDrawerData[position]);
		if (mCurrentActivity == position) {
			holder.item.setTextColor (getColor (R.color.primary_color));
			holder.item.setBackgroundColor (getColor (R.color.ripple_color));
		}
		return view;
	}

	private int getColor (int id) {
		return mContext.getResources ().getColor (id);
	}

	static class ViewHolder {
		@InjectView (R.id.main_drawer_item)
		TextView item;

		public ViewHolder (View view) {
			ButterKnife.inject (this, view);
		}
	}

}
