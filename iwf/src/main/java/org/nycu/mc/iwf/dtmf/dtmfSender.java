package org.nycu.mc.iwf.dtmf;


public class dtmfSender
extends Thread {
    private int event;
    private int duration = 160;
    private udpsender sender;
    private boolean ctrl = true;

    public dtmfSender(int event, String DstIP, int DsrPort) {
        this.event = event;
        this.sender = new udpsender(DstIP, DsrPort);
    }

    public void createDataSocket(String lMRLocalIp, int port) {
        if (this.sender.getSocket() == null) {
            this.sender.createDataSocket(lMRLocalIp, port);
        }
    }

    @Override
    public void run() {
        byte[] p = new byte[16];
        RTPpacket dtmf = new RTPpacket(p, this.event, true, false, this.duration);
        this.sender.sendpacket(dtmf.getRawPacket());
        this.sender.sendpacket(dtmf.getRawPacket());
        this.sender.sendpacket(dtmf.getRawPacket());
        int i2 = 3;
        while (this.ctrl) {
            ++i2;
            byte[] p1 = new byte[16];
            RTPpacket dtmf1 = new RTPpacket(p1, this.event, false, false, this.duration += 160);
            this.sender.sendpacket(dtmf1.getRawPacket());
            try {
                Thread.sleep(160L);
            }
            catch (InterruptedException e2) {
                e2.printStackTrace();
            }
        }
    }

    public void stopSend() {
        this.ctrl = false;
        if (!this.isInterrupted()) {
            this.interrupt();
        }
        byte[] p = new byte[16];
        RTPpacket dtmf = new RTPpacket(p, this.event, false, true, this.duration + 160);
        this.sender.sendpacket(dtmf.getRawPacket());
        this.sender.sendpacket(dtmf.getRawPacket());
        this.sender.sendpacket(dtmf.getRawPacket());
        this.sender.closedSocket();
    }
}

