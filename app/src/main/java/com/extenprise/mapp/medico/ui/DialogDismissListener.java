package com.extenprise.mapp.medico.ui;

import android.support.v4.app.DialogFragment;

/**
 * Created by ambey on 19/12/15.
 */
public interface DialogDismissListener {
    void onDialogDismissed(DialogFragment dialog);

    void onApplyDone(DialogFragment dialog);

    void onCancelDone(DialogFragment dialog);
}
