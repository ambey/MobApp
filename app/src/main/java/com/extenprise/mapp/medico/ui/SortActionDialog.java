package com.extenprise.mapp.medico.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;

import com.extenprise.mapp.medico.R;

/**
 * Created by ambey on 28/12/15.
 */
public class SortActionDialog extends DialogFragment implements View.OnClickListener {
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
        Dialog dialog = getDialog();
        dialog.setTitle(R.string.sort_options);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.layout_sort_dialog, container);
        mAscButton = (RadioButton) view.findViewById(R.id.ascending);
        mListView = (ListView) view.findViewById(R.id.sortFieldsList);
        mListView.setAdapter(new SortFieldListAdapter(getActivity(), 0, mSortFieldList));

        Button cancelButton = (Button) view.findViewById(R.id.cancelButton);
        cancelButton.setTag(getString(R.string.cancel));
        cancelButton.setOnClickListener(this);
        Button applyButton = (Button) view.findViewById(R.id.applyButton);
        applyButton.setTag(getString(R.string.apply));
        applyButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mListener != null) {
            mListener.onCancelDone(this);
        }
    }

    public String getSortField() {
        return mSortField;
    }

    public boolean isAscending() {
        return mAscending;
    }

    @Override
    public void onClick(View v) {
        super.onDismiss(this.getDialog());
        if (v.getTag().equals(getString(R.string.apply))) {
            SortFieldListAdapter adapter = (SortFieldListAdapter) mListView.getAdapter();
            mAscending = mAscButton.isChecked();
            mSortField = adapter.getSortField();
            if (mListener != null) {
                mListener.onApplyDone(this);
            }
        } else {
            if (mListener != null) {
                mListener.onCancelDone(this);
            }
        }
    }
}
