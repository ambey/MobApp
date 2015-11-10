package com.extenprise.mapp.net;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

/**
 * Created by ambey on 4/11/15.
 */
public class MappServiceConnection implements ServiceConnection {
    private Messenger service;
    private int action;
    private ServiceResponseHandler respHandler;
    private Bundle data;

    public MappServiceConnection(ServiceResponseHandler responseHandler) {
        this.respHandler = responseHandler;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public void setData(Bundle data) {
        this.data = data;
    }

    @Override
    public void onServiceConnected(ComponentName className,
                                   IBinder service) {
        this.service = new Messenger(service);
        Message msg = Message.obtain(null, action);
        msg.replyTo = new Messenger(respHandler);
        msg.setData(data);

        try {
            this.service.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName arg0) {
        service = null;
    }

}