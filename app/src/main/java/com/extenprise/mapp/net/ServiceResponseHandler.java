package com.extenprise.mapp.net;

import android.os.Handler;
import android.os.Message;

/**
 * Created by ambey on 8/10/15.
 */
public class ServiceResponseHandler extends Handler {
    private ResponseHandler handler;

    public ServiceResponseHandler(ResponseHandler handler) {
        this.handler = handler;
    }

    @Override
    public void handleMessage(Message msg) {
        if(!handler.gotResponse(msg.what, msg.getData())) {
            super.handleMessage(msg);
        }
    }

}
