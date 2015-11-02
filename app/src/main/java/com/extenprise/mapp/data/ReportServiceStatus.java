package com.extenprise.mapp.data;

import android.content.Context;

import com.extenprise.mapp.R;

/**
 * Created by ambey on 30/10/15.
 */
public enum ReportServiceStatus {
    STATUS_NEW,
    STATUS_PENDING,
    STATUS_INPROCESS,
    STATUS_PACKED,
    STATUS_DISPATCHED,
    STATUS_DELIVERED;

    public static String getStatusString(Context context, int status) {
        int resId = R.string.new_state;
        if(status == STATUS_PENDING.ordinal()) {
            resId = R.string.pending_state;
        } else if(status == STATUS_INPROCESS.ordinal()) {
            resId = R.string.inprocess_state;
        } else if (status == STATUS_PACKED.ordinal()) {
            resId = R.string.packed_state;
        } else if (status == STATUS_DISPATCHED.ordinal()) {
            resId = R.string.dispatched_state;
        } else if (status == STATUS_DELIVERED.ordinal()) {
            resId = R.string.delivered_state;
        }
        return context.getString(resId);
    }
}
