package org.nycu.mc.iwf.Session;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Timer;


public class MediaSessionState {
    public static final int SOMEONE_TALKING = 1;
    public static final int IDLE = 2;
    public static final int REVOKE = 3;
    public static final int GRANTED = 4;
    public static final int RINGBACK = 5;
    public static final int PEERPICKUP = 6;
    public static final int ENDSESSION = 7;
    public static final int NONE = 8;
    private boolean state = false;
    private int currentState;
    private String currentMsg;
    private MediaSipSession mediaSipSession;
    private Timer connectTimer;
    private Timer idleNatTimer;
    private MeaninglessSender rtpMeaninglessSender;
    private DatagramSocket rtpSocket;
    private boolean lock;
    private int Rtpport;//private port
    private String localip;

    public MediaSessionState(MediaSipSession mediaSipSession) {
        this.mediaSipSession = mediaSipSession;
        this.lock = false;
        this.changeState(5, "", null);
    }

    public void changeState(int stateToChange, String message, String localip) {
        this.changeStateAndSendResult(stateToChange, message, false, localip);
    }

    public void changeState(int stateToChange, String message, boolean isEmergency, String localip) {
        this.changeStateAndSendResult(stateToChange, message, isEmergency, localip);
    }

    private void changeStateAndSendResult(int stateToChange, String message, boolean isEmergency, String localip) {
        if (this.connectTimer != null) {
            this.connectTimer.cancel();
            this.connectTimer = null;
        }
        this.stopSendPacketWhenIdle();
        switch (stateToChange) {
            case 1: {
                if (isEmergency) {
                    this.sendEmergencyResult("\u7dca\u6025\u8a0a\u606f!! " + message, 1);
                    break;
                }
                this.sendResult(message, 1);
                this.mediaSipSession.setIsEmergency(false);
                break;
            }
            case 2: {
                this.mediaSipSession.setIsEmergency(false);
                this.setlocalip(localip);
                this.startSendPacketWhenIdle();
                break;
            }
            case 3: {
                this.sendResult(message, 3);
                this.mediaSipSession.setIsEmergency(false);
                break;
            }
            case 4: {
                if (isEmergency) {
                    this.sendEmergencyResult("\u60a8\u6b63\u5728\u767c\u9001\u7dca\u6025\u8a0a\u606f!!", 4);
                    break;
                }
                this.sendResult("\u60a8\u6b63\u5728\u8b1b\u8a71...", 4);
                this.mediaSipSession.setIsEmergency(false);
                break;
            }
            case 5: 
            case 6: 
            case 7: {
                this.mediaSipSession.setIsActive(false);
                this.sendResult("\u672a\u958b\u59cb\u901a\u8a71", 7);
                this.mediaSipSession.setIsEmergency(false);
                break;
            }
            default: {
                this.sendResult("None", 8);
                this.mediaSipSession.setIsEmergency(false);
            }
        }
    }

    public void start() {
        this.startSendPacketWhenIdle();
    }

    public void stop() {
        this.stopSendPacketWhenIdle();
        this.state = false;
    }

    public void setstate(boolean runstate) {
        this.state = runstate;
    }

    public boolean getstate() {
        return this.state;
    }

    public void tempstop() {
        this.stopSendPacketWhenIdle();
    }

    private void stopSendPacketWhenIdle() {
        if (this.rtpMeaninglessSender != null) {
            this.rtpMeaninglessSender.stop();
        }
        this.state = false;
        if (this.idleNatTimer != null) {
            this.idleNatTimer.cancel();
            this.idleNatTimer = null;
        }
    }

    private void startSendPacketWhenIdle() {
//    	System.out.println("[startSendPacketWhenIdle]"+this.rtpMeaninglessSender);
        if (this.rtpMeaninglessSender == null) {
            String ip = this.mediaSipSession.getPeerInfo().getPeerIp();
            int port = this.mediaSipSession.getPeerInfo().getPeerRtpPort();
            System.out.println("[mediasessionstate]server address? :"+ip+":"+port);
            try {
                this.rtpSocket.setReuseAddress(true);
                this.rtpMeaninglessSender = new MeaninglessSender(this.rtpSocket, ip, port);
                this.rtpMeaninglessSender.setTag("SendPacketWhenIdle Meaningless send");
//                this.rtpMeaninglessSender.start();
                
//                String wsUrl = "wss://192.168.0.36:9091/ws";
//        		System.out.println("[wsurl] " + wsUrl); // 建立 Helper 實例並連線
//        		WebSocketHelper helper = new WebSocketHelper();
//        		helper.connect(wsUrl);
//        		this.rtpMeaninglessSender.setOnUdpPacketListener(helper);
//                System.out.println("[MeaninglessSender] rtp socket...");
                this.rtpMeaninglessSender.start();
//                helper.eov(wsUrl);
            }
            catch (SocketException e2) {
                e2.printStackTrace();
            }
        }
        this.rtpMeaninglessSender.printInfo();
        this.rtpMeaninglessSender.start();
        this.state = true;
    }

    public MeaninglessSender getMeaningSender() {
        return this.rtpMeaninglessSender;
    }

    public int getCurrentStateNow() {
        return this.currentState;
    }

    public String getCurrentMsg() {
        return this.currentMsg;
    }

    public void sendResult() {
        this.sendResult(this.currentMsg, this.currentState);
    }

    private void sendResult(String msg, int btnState) {
        this.send(msg, btnState, false);
    }

    private void sendEmergencyResult() {
        this.sendEmergencyResult(this.currentMsg, this.currentState);
    }

    private void sendEmergencyResult(String msg, int btnState) {
        this.send(msg, btnState, true);
        this.mediaSipSession.setIsEmergency(true);
    }

    private void send(String msg, int btnState, boolean isEmergency) {
        this.currentMsg = msg;
        this.currentState = btnState;
    }

    public void setRtpport(int port) {
        this.Rtpport = port;
    }

    public int getRtpport() {
        return this.Rtpport;
    }

    public void lock() {
        this.lock = true;
    }

    public void unlock() {
        this.lock = false;
    }

    public void setlocalip(String localip) {
        this.localip = localip;
    }

    public String getlocalip() {
        return this.localip;
    }

    public DatagramSocket getrtpSocket() {
        return this.rtpSocket;
    }

    public void setrtpSocket(DatagramSocket rtpSocket) {
        this.rtpSocket = rtpSocket;
    }
}

