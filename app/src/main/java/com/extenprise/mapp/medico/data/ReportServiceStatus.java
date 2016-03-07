package com.extenprise.mapp.medico.data;

import android.content.Context;

import com.extenprise.mapp.medico.R;

/**
 * Created by ambey on 30/10/15.
 */
public abstract class ReportServiceStatus {
    public static final int STATUS_NEW = 0;
    public static final int STATUS_PENDING = 1;
    public static final int STATUS_INPROCESS = 2;
    public static final int STATUS_FEEDBACK_SENT = 3;
    public static final int STATUS_PACKED = 4;
    public static final int STATUS_DISPATCHED = 5;
    public static final int STATUS_DELIVERED = 6;

    public static String getStatusString(Context context, int status) {
        int resId = R.string.new_state;
        if (status == STATUS_PENDING) {
            resId = R.string.pending_state;
        } else if (status == STATUS_INPROCESS) {
            resId = R.string.inprocess_state;
        } else if (status == STATUS_FEEDBACK_SENT) {
            resId = R.string.feedback_state;
        } else if (status == STATUS_PACKED) {
            resId = R.string.packed_state;
        } else if (status == STATUS_DISPATCHED) {
            resId = R.string.dispatched_state;
        } else if (status == STATUS_DELIVERED) {
            resId = R.string.delivered_state;
        }
        return context.getString(resId);
    }
}
