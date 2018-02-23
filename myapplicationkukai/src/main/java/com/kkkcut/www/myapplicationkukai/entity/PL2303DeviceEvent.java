package com.kkkcut.www.myapplicationkukai.entity;

/**
 * Created by Administrator on 2017/10/24.
 */

public class PL2303DeviceEvent {
    private   boolean  open;
    public  PL2303DeviceEvent(boolean open){
           this.open=open;
    }
    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }
}
