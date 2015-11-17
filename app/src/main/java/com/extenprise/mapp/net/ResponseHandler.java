package com.extenprise.mapp.net;

import android.os.Bundle;

public interface ResponseHandler {
    boolean gotResponse(int action, Bundle data);
}
