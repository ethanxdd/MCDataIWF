package org.nycu.mc.iwf.main;

public class SipRegisterSender
extends Thread {
    private static final String TAG = "SipRegisterSender";
    private boolean registerCtrl = true;
    private MCDataClient sipClient;
    private static int expires;

    public SipRegisterSender(MCDataClient sipClient, int expires) {
        this.sipClient = sipClient;
        SipRegisterSender.expires = expires;
    }

    @Override
    public void run() {
        while (this.registerCtrl) {
            this.sipClient.sendRegister(expires);
            try {
                if (expires >= 60) {
                    Thread.sleep((long)(expires / 2) * 1000L);
                    continue;
                }
                if (expires >= 60) continue;
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
        }
    }

    public void stopSend() {
        this.registerCtrl = false;
        expires = 0;
        if (!this.isInterrupted()) {
            this.interrupt();
        }
    }

    public static int getExpires() {
        return expires;
    }
}

