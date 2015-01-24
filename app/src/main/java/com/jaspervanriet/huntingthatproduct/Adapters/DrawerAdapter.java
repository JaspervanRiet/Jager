/*
 * Copyright (c) Jasper van Riet 2015.
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

	public DrawerAdapter (Context context, String[] data) {
		this.mContext = context;
		this.mDrawerData = data;
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
				"fonts/Roboto-Light.ttf"));
		holder.item.setText (mDrawerData[position]);
		return view;
	}

	static class ViewHolder {
		@InjectView (R.id.main_drawer_item)
		TextView item;

		public ViewHolder (View view) {
			ButterKnife.inject (this, view);
		}
	}

}
