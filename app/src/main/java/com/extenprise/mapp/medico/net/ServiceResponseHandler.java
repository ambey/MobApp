package com.extenprise.mapp.medico.net;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.extenprise.mapp.medico.util.Utility;

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
        boolean bound = connection.isBound();
        if(unbind) {
            try {
                context.unbindService(connection);
                connection.setBound(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Bundle data = msg.getData();
        if (bound && !handler.gotResponse(msg.what, data)) {
            Utility.showErrorMessage(context, data.getInt("responseCode"));
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
