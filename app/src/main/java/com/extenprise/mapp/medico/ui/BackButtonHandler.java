package com.extenprise.mapp.medico.ui;

/**
 * Created by ambey on 14/2/16.
 */
public class BackButtonHandler {
    private static BackButtonHandler instance;
    private boolean backPressed;

    private BackButtonHandler() {
    }

    public synchronized static BackButtonHandler getInstance() {
        if (instance == null) {
            instance = new BackButtonHandler();
        }
        return instance;
    }

    public boolean isBackPressed() {
        return backPressed;
    }

    public void setBackPressed(boolean backPressed) {
        this.backPressed = backPressed;
    }
}
