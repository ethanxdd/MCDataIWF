package org.nycu.mc.iwf.main;


public class SubscribeDialog {
    private String remoteName;
    private String cSeq;
    private String myTag;
    private String peerTag;
    private String callId;
    private String callIDIp;
    private String remoteSipUri;

    public String getRemoteName() {
        return this.remoteName;
    }

    public void setRemoteName(String remoteName) {
        this.remoteName = remoteName;
    }

    public String getCSeq() {
        return this.cSeq;
    }

    public void setCSeq(String cSeq) {
        this.cSeq = cSeq;
    }

    public String getMyTag() {
        return this.myTag;
    }

    public void setMyTag(String myTag) {
        this.myTag = myTag;
    }

    public String getCallId() {
        return this.callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public String getPeerTag() {
        return this.peerTag;
    }

    public void setPeerTag(String peerTag) {
        this.peerTag = peerTag;
    }

    public String getRemoteSipUri() {
        return this.remoteSipUri;
    }

    public void setRemoteSipUri(String remoteSipUri) {
        this.remoteSipUri = remoteSipUri;
    }
}

