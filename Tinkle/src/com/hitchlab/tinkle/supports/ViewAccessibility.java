package com.hitchlab.tinkle.supports;

import android.view.View;
import android.view.ViewGroup;

public class ViewAccessibility {
	/**
	 * Enables/Disables all child views in a view group.
	 * 
	 * @param viewGroup the view group
	 * @param enabled <code>true</code> to enable, <code>false</code> to disable
	 * the views.
	 */
	public static void enableDisableViewGroup(ViewGroup viewGroup, boolean enabled) {
		int childCount = viewGroup.getChildCount();
		for (int i = 0; i < childCount; i++) {
			View view = viewGroup.getChildAt(i);
			view.setEnabled(enabled);
			if (view instanceof ViewGroup) {
				enableDisableViewGroup((ViewGroup) view, enabled);
			}
		}
	}
}
