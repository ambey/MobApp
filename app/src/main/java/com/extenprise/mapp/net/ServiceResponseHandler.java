package com.extenprise.mapp.net;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class ServiceResponseHandler extends Handler {
    private Context context;
    private ResponseHandler handler;
    private MappServiceConnection connection;
    private boolean unbind;

    public ServiceResponseHandler(Context context, ResponseHandler handler) {
        this.context = context;
        this.handler = handler;
        unbind = true;
    }

    public void setConnection(MappServiceConnection connection) {
        this.connection = connection;
    }

    @Override
    public void handleMessage(Message msg) {
        if(unbind) {
            try {
                context.unbindService(connection);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(!handler.gotResponse(msg.what, msg.getData())) {
            super.handleMessage(msg);
        }
    }

}
