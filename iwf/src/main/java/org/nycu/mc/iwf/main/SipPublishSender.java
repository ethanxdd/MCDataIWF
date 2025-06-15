package org.nycu.mc.iwf.main;


public class SipPublishSender
extends Thread {
    private String TAG = "SipPublishSender";
    private boolean ctrl = true;
    private MCDataClient sipClient;
    private String status;
    private int expires;
    private String eTag;
    private String settingPublishETag;
    private String settingXml;

    public SipPublishSender(MCDataClient sipClient, String status, int expires) {
        this.sipClient = sipClient;
        this.status = status;
        this.expires = expires;
        this.eTag = null;
        this.settingPublishETag = null;
    }

    @Override
    public void run() {
        while (this.ctrl) {
            this.sipClient.sendPresencePublish(this.status, this.expires, this.eTag);
            try {
                if (this.expires >= 60) {
                    Thread.sleep((long)(this.expires / 2) * 1000L);
                    continue;
                }
                if (this.expires >= 60) continue;
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
        }
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setETag(String eTag) {
        this.eTag = eTag;
    }

    public String getETag() {
        return this.eTag;
    }

    public void setSettingPublishETag(String settingPublishETag) {
        this.settingPublishETag = settingPublishETag;
    }

    public void setSettingXml(String settingXml) {
        this.settingXml = settingXml;
    }

    public void stopSend() {
        this.ctrl = false;
        this.expires = 0;
        if (!this.isInterrupted()) {
            this.sipClient.sendPresencePublish("closed", this.expires, this.eTag);
        }
        this.interrupt();
    }
}