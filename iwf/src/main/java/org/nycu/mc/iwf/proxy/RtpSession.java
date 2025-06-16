package org.nycu.mc.iwf.proxy;

import java.net.InetAddress;

public interface RtpSession {
    public int getCodecId();

    public String getName();

    public int getSampleRate();

    public void startSending();

    public void startReceiving();

    public void setAdjustMicGainFactor(int var1);

    public void setRemoteTarget(InetAddress var1, int var2);

    public void stop();

    public int toggleMute();

    public boolean isMuted();

    public void sendDtmf();
}
