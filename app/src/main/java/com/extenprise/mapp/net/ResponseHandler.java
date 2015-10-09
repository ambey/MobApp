package com.extenprise.mapp.net;

import android.os.Bundle;

/**
 * Created by ambey on 8/10/15.
 */
public interface ResponseHandler {
    boolean gotResponse(int action, Bundle data);
}
