package com.extenprise.mapp.medico.net;

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
        /*if(!connection.isConnected()) {
            return;
        }*/
        boolean bound = connection.isBound();
        if(unbind) {
            try {
                context.unbindService(connection);
                connection.setBound(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (bound && !handler.gotResponse(msg.what, msg.getData())) {
            super.handleMessage(msg);
        }
    }

/*
    public void unbindService(ServiceConnection conn) {
        if(unbind) {
            this.unbindService(conn);
        }
    }
*/
}
