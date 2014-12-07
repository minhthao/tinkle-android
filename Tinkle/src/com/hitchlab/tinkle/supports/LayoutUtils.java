package com.hitchlab.tinkle.supports;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

public class LayoutUtils {

	public enum Layout {
		WidthFill_HeightFill,
		WidthWrap_HeightWrap,
		WidthWrap_HeightFill,
		WidthFill_HeightWrap;

		public void applyViewGroupParams(View component) {
			applyViewGroupLayoutParamsTo(this, component);
		}

		public void applyLinearLayoutParams(View linearlayout) {
			applyLinearLayoutParamsTo(this, linearlayout);
		}

		public void applyTableRowParams(View cell) {
			applyTableRowLayoutParamsTo(this, cell);
		}

		public void applyTableLayoutParams(View row) {
			applyTableLayoutParamsTo(this, row);
		}
	}

	private static void applyLinearLayoutParamsTo(Layout layout, View view) {

		switch (layout) {
		case WidthFill_HeightFill:
			view.setLayoutParams(new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT));
			break;
		case WidthFill_HeightWrap:
			view.setLayoutParams(new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT));
			break;
		case WidthWrap_HeightFill:
			view.setLayoutParams(new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.MATCH_PARENT));
			break;
		case WidthWrap_HeightWrap:
			view.setLayoutParams(new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT));
			break;
		}

	}

	private static void applyViewGroupLayoutParamsTo(Layout layout, View view) {

		switch (layout) {
		case WidthFill_HeightFill:
			view.setLayoutParams(new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT));
			break;
		case WidthFill_HeightWrap:
			view.setLayoutParams(new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT));
			break;
		case WidthWrap_HeightFill:
			view.setLayoutParams(new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.MATCH_PARENT));
			break;
		case WidthWrap_HeightWrap:
			view.setLayoutParams(new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT));
			break;
		}

	}

	private static void applyTableRowLayoutParamsTo(Layout layout, View view) {

		switch (layout) {
		case WidthFill_HeightFill:
			view.setLayoutParams(new TableRow.LayoutParams(
					TableRow.LayoutParams.MATCH_PARENT,
					TableRow.LayoutParams.MATCH_PARENT));
			break;
		case WidthFill_HeightWrap:
			view.setLayoutParams(new TableRow.LayoutParams(
					TableRow.LayoutParams.MATCH_PARENT,
					TableRow.LayoutParams.WRAP_CONTENT));
			break;
		case WidthWrap_HeightFill:
			view.setLayoutParams(new TableRow.LayoutParams(
					TableRow.LayoutParams.WRAP_CONTENT,
					TableRow.LayoutParams.MATCH_PARENT));
			break;
		case WidthWrap_HeightWrap:
			view.setLayoutParams(new TableRow.LayoutParams(
					TableRow.LayoutParams.WRAP_CONTENT,
					TableRow.LayoutParams.WRAP_CONTENT));
			break;
		}

	}

	private static void applyTableLayoutParamsTo(Layout layout, View view) {

		switch (layout) {
		case WidthFill_HeightFill:
			view.setLayoutParams(new TableLayout.LayoutParams(
					TableLayout.LayoutParams.MATCH_PARENT,
					TableLayout.LayoutParams.MATCH_PARENT));
			break;
		case WidthFill_HeightWrap:
			view.setLayoutParams(new TableLayout.LayoutParams(
					TableLayout.LayoutParams.MATCH_PARENT,
					TableLayout.LayoutParams.WRAP_CONTENT));
			break;
		case WidthWrap_HeightFill:
			view.setLayoutParams(new TableLayout.LayoutParams(
					TableLayout.LayoutParams.WRAP_CONTENT,
					TableLayout.LayoutParams.MATCH_PARENT));
			break;
		case WidthWrap_HeightWrap:
			view.setLayoutParams(new TableLayout.LayoutParams(
					TableLayout.LayoutParams.WRAP_CONTENT,
					TableLayout.LayoutParams.WRAP_CONTENT));
			break;
		}

	}

}
