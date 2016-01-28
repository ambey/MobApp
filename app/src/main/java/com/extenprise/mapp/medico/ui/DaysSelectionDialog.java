package com.extenprise.mapp.medico.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.extenprise.mapp.medico.R;
import com.extenprise.mapp.medico.util.Utility;

/**
 * Created by ambey on 19/12/15.
 */
public class DaysSelectionDialog extends DialogFragment {

    private String selectedDays;
    private ListView listView;
    private DialogDismissListener listener;

    public void setListener(DialogDismissListener listener) {
        this.listener = listener;
    }

    public String getSelectedDays() {
        return selectedDays;
    }

    public void setSelectedDays(String selectedDays) {
        this.selectedDays = selectedDays;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle(R.string.days_avail_pref);
        View view = inflater.inflate(R.layout.layout_days_selection, container);
        listView = (ListView) view.findViewById(R.id.availableDaysList);
        listView.setAdapter(new DaysListAdapter(getActivity(), 0, selectedDays));
        return view;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        DaysListAdapter adapter = (DaysListAdapter) listView.getAdapter();
        boolean[] selection = adapter.getSelection();
        String[] days = Utility.getDaysOptions(getActivity());
        int i = 1;
        for (; i < selection.length; i++) {
            if (selection[i]) {
                selectedDays = days[i];
                break;
            }
        }
        for (i += 1; i < selection.length; i++) {
            if (selection[i]) {
                selectedDays += "," + days[i];
            }
        }

        //DialogDismissListener listener = (DialogDismissListener) getActivity();;
        /*try {
            listener = (DialogDismissListener) getActivity();
        } catch (Exception e) {
            listener = (DialogDismissListener) getParentFragment();
            //getTargetFragment();
        }*/

        if(listener != null) {
            listener.onDialogDismissed(this);
        }
    }
}
