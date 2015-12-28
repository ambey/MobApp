package com.extenprise.mapp.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RadioButton;

import com.extenprise.mapp.R;

/**
 * Created by ambey on 28/12/15.
 */
public class SortActionDialog extends DialogFragment {
    private ListView mListView;
    private DialogDismissListener mListener;
    private String[] mSortFieldList;
    private String mSortField;
    private boolean mAscending;
    private RadioButton mAscButton;

    public void setListener(DialogDismissListener listener) {
        this.mListener = listener;
    }

    public void setSortFieldList(String[] sortFieldList) {
        this.mSortFieldList = sortFieldList;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle(R.string.apply_sort);
        View view = inflater.inflate(R.layout.layout_sort_dialog, container);
        mAscButton = (RadioButton) view.findViewById(R.id.ascending);
        mListView = (ListView) view.findViewById(R.id.sortFieldsList);
        mListView.setAdapter(new SortFieldListAdapter(getActivity(), 0, mSortFieldList));
        return view;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        SortFieldListAdapter adapter = (SortFieldListAdapter) mListView.getAdapter();
        mAscending = mAscButton.isChecked();
        mSortField = adapter.getSortField();
        if (mListener != null) {
            mListener.onDialogDismissed(this);
        }
    }

    public String getSortField() {
        return mSortField;
    }

    public boolean isAscending() {
        return mAscending;
    }
}
