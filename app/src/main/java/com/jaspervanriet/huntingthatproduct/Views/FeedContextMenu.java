package com.jaspervanriet.huntingthatproduct.Views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.jaspervanriet.huntingthatproduct.R;
import com.jaspervanriet.huntingthatproduct.Utils.Utils;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class FeedContextMenu extends LinearLayout {
	private static final int CONTEXT_MENU_WIDTH = Utils.dpToPx (240);

	private int feedItem = -1;

	private OnFeedContextMenuItemClickListener onItemClickListener;

	public FeedContextMenu (Context context) {
		super (context);
		init ();
	}

	private void init () {
		LayoutInflater.from (getContext ()).inflate (R.layout.view_context_menu, this, true);
		setBackgroundResource (R.drawable.bg_container_shadow);
		setOrientation (VERTICAL);
		setLayoutParams (new LayoutParams (CONTEXT_MENU_WIDTH,
				ViewGroup.LayoutParams.WRAP_CONTENT));
	}

	public void bindToItem (int feedItem) {
		this.feedItem = feedItem;
	}

	@Override
	protected void onAttachedToWindow () {
		super.onAttachedToWindow ();
		ButterKnife.inject (this);
	}

	public void dismiss () {
		((ViewGroup) getParent ()).removeView (FeedContextMenu.this);
	}

	@OnClick (R.id.btnReport)
	public void onShareClick () {
		if (onItemClickListener != null) {
			onItemClickListener.onShareClick (feedItem);
		}
	}

	@OnClick (R.id.btnCancel)
	public void onCancelClick () {
		if (onItemClickListener != null) {
			onItemClickListener.onCancelClick (feedItem);
		}
	}

	public void setOnFeedMenuItemClickListener (OnFeedContextMenuItemClickListener
														onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

	public interface OnFeedContextMenuItemClickListener {
		public void onShareClick (int feedItem);

		public void onCancelClick (int feedItem);
	}
}