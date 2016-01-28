package com.extenprise.mapp.medico.net;

import android.os.Bundle;

public interface ResponseHandler {
    boolean gotResponse(int action, Bundle data);
}
