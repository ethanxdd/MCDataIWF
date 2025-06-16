package org.nycu.mc.iwf.Session;


public class MySdpInfo {
    private String ip;
    private int port;
    private int rtcpPort;
    private String mediaType;

    public String getIp() {
        return this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getRtcpPort() {
        return this.rtcpPort;
    }

    public void setRtcpPort(int rtcpPort) {
        this.rtcpPort = rtcpPort;
    }

    public String getMediaType() {
        return this.mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }
}

