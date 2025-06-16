package org.nycu.mc.iwf.Session;


import gov.nist.javax.sip.address.SipUri;

public class PeerInfo {
    private int peerRtpPort;
    private int peerRtcpPort;
    private String peerIp;
    private String peerUserName;
    private String peerSipDomain;
    private String peerTag;
    private SipUri peerSipUri;
    private int peerSipPort;

    public PeerInfo(SipUri sipUri) {
        SipUri sipUriClone = (SipUri)sipUri.clone();
        sipUriClone.clearUriParms();
        this.peerSipUri = sipUriClone;
    }

    public int getPeerRtpPort() {
        return this.peerRtpPort;
    }

    public void setPeerRtpPort(int peerRtpPort) {
        this.peerRtpPort = peerRtpPort;
    }

    public int getPeerRtcpPort() {
        return this.peerRtcpPort;
    }

    public void setPeerRtcpPort(int peerRtcpPort) {
        this.peerRtcpPort = peerRtcpPort;
    }

    public String getPeerIp() {
        return this.peerIp;
    }

    public void setPeerIp(String peerIp) {
        this.peerIp = peerIp;
    }

    public String getPeerUserName() {
        return this.peerUserName;
    }

    public void setPeerUserName(String peerUserName) {
        this.peerUserName = peerUserName;
    }

    public String getPeerSipDomain() {
        return this.peerSipDomain;
    }

    public void setPeerSipDomain(String peerSipDomain) {
        this.peerSipDomain = peerSipDomain;
    }

    public String getPeerTag() {
        return this.peerTag;
    }

    public void setPeerTag(String peerTag) {
        this.peerTag = peerTag;
    }

    public SipUri getPeerSipUri() {
        return this.peerSipUri;
    }

    public void setPeerSipUri(SipUri peerSipUri) {
        peerSipUri.clearUriParms();
        this.peerSipUri = peerSipUri;
    }

    public int getPeerSipPort() {
        return this.peerSipPort;
    }

    public void setPeerSipPort(int peerSipPort) {
        this.peerSipPort = peerSipPort;
    }

    public void setSDPInfo(MySdpInfo sdpInfo) {
        this.peerIp = sdpInfo.getIp();
        this.peerRtpPort = sdpInfo.getPort();
        this.peerRtcpPort = sdpInfo.getRtcpPort();
        System.out.println("[peeronfo] server address"+this.peerIp+", "+this.peerRtpPort+", "+this.peerRtcpPort);
    }
}

