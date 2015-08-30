package com.extenprise.mapp.util;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

/**
 * A Spinner view that does not dismiss the dialog displayed when the control is "dropped down"
 * and the user presses it. This allows for the selection of more than one option.
 */
public class MultiSelectionSpinner extends Spinner implements OnMultiChoiceClickListener {
    String[] mItems = null;
    boolean[] mSelection = null;

    ArrayAdapter<String> mProxyAdapter;

    /**
     * Constructor for use when instantiating directly.
     *
     * @param context
     */
    public MultiSelectionSpinner(Context context) {
        super(context);

        mProxyAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item);
        super.setAdapter(mProxyAdapter);
    }

    /**
     * Constructor used by the layout inflater.
     *
     * @param context
     * @param attrs
     */
    public MultiSelectionSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);

        mProxyAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item);
        super.setAdapter(mProxyAdapter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        if (mSelection != null && which < mSelection.length) {
            mSelection[which] = isChecked;

            mProxyAdapter.clear();
            mProxyAdapter.add(buildSelectedItemString());
            setSelection(0);
        } else {
            throw new IllegalArgumentException("Argument 'which' is out of bounds.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean performClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMultiChoiceItems(mItems, mSelection, this);
        builder.show();
        return true;
    }

    /**
     * Sets the options for this spinner.
     *
     * @param items
     */
    public void setItems(String[] items) {
        mItems = items;
        mSelection = new boolean[mItems.length];

        Arrays.fill(mSelection, false);
    }

    /**
     * Sets the options for this spinner.
     *
     * @param items
     */
    public void setItems(List<String> items) {
        setItems((String[]) items.toArray());
    }

    /**
     * Sets the selected options based on an array of string.
     *
     * @param selection
     */
    public void setSelection(String[] selection) {
        for (String sel : selection) {
            for (int j = 0; j < mItems.length; ++j) {
                if (mItems[j].equals(sel)) {
                    mSelection[j] = true;
                }
            }
        }
    }

    /**
     * Sets the selected options based on a list of string.
     *
     * @param selection
     */
    public void setSelection(List<String> selection) {
        setSelection((String[]) selection.toArray());
    }

    /**
     * Sets the selected options based on an array of positions.
     *
     * @param selectedIndicies
     */
    public void setSelection(int[] selectedIndicies) {
        for (int index : selectedIndicies) {
            try {
                mSelection[index] = true;
            } catch (ArrayIndexOutOfBoundsException x) {
                x.printStackTrace();
            }
        }
    }

    /**
     * Returns a list of strings, one for each selected item.
     *
     * @return
     */
    public List<String> getSelectedStrings() {
        List<String> selection = new LinkedList<>();
        for (int i = 0; i < mItems.length; ++i) {
            if (mSelection[i]) {
                selection.add(mItems[i]);
            }
        }
        return selection;
    }

    /**
     * Returns a list of positions, one for each selected item.
     *
     * @return
     */
    public List<Integer> getSelectedIndices() {
        List<Integer> selection = new LinkedList<>();
        for (int i = 0; i < mItems.length; ++i) {
            if (mSelection[i]) {
                selection.add(i);
            }
        }
        return selection;
    }

    /**
     * Builds the string for display in the spinner.
     *
     * @return comma-separated list of selected items
     */
    private String buildSelectedItemString() {
        StringBuilder sb = new StringBuilder();
        boolean foundOne = false;

        for (int i = 0; i < mItems.length; ++i) {
            if (mSelection[i]) {
                if (foundOne) {
                    sb.append(", ");
                }
                foundOne = true;

                sb.append(mItems[i]);
            }
        }

        return sb.toString();
    }
}