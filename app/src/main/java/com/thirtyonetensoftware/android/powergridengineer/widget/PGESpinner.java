package com.thirtyonetensoftware.android.powergridengineer.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Spinner;

/**
 * PGESpinner
 * <p/>
 * Power Grid Engineer
 * 31Ten Software
 * <p/>
 * Author: Josh Kendrick
 */
public class PGESpinner extends Spinner {

    public PGESpinner(Context context) {
        super(context);
    }

    public PGESpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PGESpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setSelection(int position, boolean animate) {
        boolean sameSelected = position == getSelectedItemPosition();
        super.setSelection(position, animate);
        if ( sameSelected ) {
            // Spinner does not call the OnItemSelectedListener if the same item is selected, so do it manually now
            getOnItemSelectedListener().onItemSelected(this, getSelectedView(), position, getSelectedItemId());
        }
    }

    @Override
    public void setSelection(int position) {
        boolean sameSelected = position == getSelectedItemPosition();
        super.setSelection(position);
        if ( sameSelected ) {
            // Spinner does not call the OnItemSelectedListener if the same item is selected, so do it manually now
            getOnItemSelectedListener().onItemSelected(this, getSelectedView(), position, getSelectedItemId());
        }
    }
}
