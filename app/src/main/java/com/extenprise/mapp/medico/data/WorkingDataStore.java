package com.extenprise.mapp.medico.data;

import android.os.Bundle;

/**
 * Created by ambey on 16/12/15.
 */
public abstract class WorkingDataStore {
    private static Bundle bundle = new Bundle();

    public static Bundle getBundle() {
        return bundle;
    }
}
