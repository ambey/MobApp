package com.extenprise.mapp.net;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

public class MappServiceConnection implements ServiceConnection {
    private Messenger service;
    private int action;
    private ServiceResponseHandler respHandler;
    private Bundle data;
    private boolean connected;

    public boolean isConnected() {
        return connected;
    }

    public MappServiceConnection(ServiceResponseHandler responseHandler) {
        respHandler = responseHandler;
        respHandler.setConnection(this);
    }

    public void setAction(int action) {
        this.action = action;
    }

    public void setData(Bundle data) {
        this.data = data;
    }

    public Bundle getData() {
        return data;
    }

    @Override
    public void onServiceConnected(ComponentName className,
                                   IBinder service) {
        this.service = new Messenger(service);
        Message msg = Message.obtain(null, action);
        msg.replyTo = new Messenger(respHandler);
        msg.setData(data);
        connected = true;

        try {
            if(isConnected()) {
                this.service.send(msg);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName arg0) {
        connected = false;
        service = null;
    }

}
