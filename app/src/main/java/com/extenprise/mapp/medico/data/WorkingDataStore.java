package com.extenprise.mapp.medico.data;

import android.os.Bundle;

/**
 * Created by ambey on 16/12/15.
 */
public abstract class WorkingDataStore {
    private static Bundle bundle = new Bundle();
    private static Object loginRef;

    public static Bundle getBundle() {
        return bundle;
    }

    public static Object getLoginRef() {
        return loginRef;
    }

    public static void setLoginRef(Object loginRef) {
        WorkingDataStore.loginRef = loginRef;
    }
}
