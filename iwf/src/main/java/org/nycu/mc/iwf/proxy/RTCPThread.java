package org.nycu.mc.iwf.proxy;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import org.nycu.mc.iwf.RTCP.*;
import org.nycu.mc.iwf.Session.MediaSipSession;
import org.nycu.mc.iwf.Session.PeerInfo;
import org.nycu.mc.iwf.Session.PermissionState;
import org.nycu.mc.iwf.dtmf.dtmfSender;
import org.nycu.mc.iwf.redirect.handler;

public class RTCPThread
extends Thread {
    private static final String TAG = "RTCPThread";
    private boolean runThread;
    private DatagramSocket rtcpSocket;
    private boolean onlistening = false;
    private boolean onsending = false;
    public static int remoteRTCPport;
    private handler ModeHandler;
    private int count;

    RTCPThread(DatagramSocket rtcpSocket) {
        this.rtcpSocket = rtcpSocket;
    }

    public void setting(handler handler2) {
        this.ModeHandler = handler2;
    }

    public handler getHandler() {
    	return this.ModeHandler;
    }

    @Override
    public synchronized void start() {
        this.runThread = true;
    }

    @Override
    public void run() {
        byte[] byte1024 = new byte[2048];
        DatagramPacket dPacket = new DatagramPacket(byte1024, 2048);
        try {
            while (this.runThread) {
            	rtcp_granted grantMsg;
                rtcp_deny denyMsg;
                rtcp_taken takenMsg;
                Short Ind;
                MediaSipSession EMRmediaSipSession;
                String target;
                rtcp_revoke revokeMsg;
                rtcp_idle idleMsg;
                rtcp_connect connectMsg;
                rtcp_disconnect disconnectMsg;
                rtcp_queue_position_info queueInfoMsg;
                rtcp_ack ack;
                rtcp_queue_position_request queuePosReqMsg;
                rtcp_request request;
                rtcp_release release;
            	System.out.println("RTCP Thread rurnning ... .s");
                this.rtcpSocket.receive(dPacket);
                String[] remoteIP = dPacket.getAddress().toString().split("/");
                remoteRTCPport = dPacket.getPort();
                MediaSipSession mediaSipSession = this.getCorrectSession(dPacket);
                if (mediaSipSession == null || dPacket.getLength() == 0) {
                    System.out.println("can't find correct session or meaningless , drop rtcp packet ...");
                }
                Rtcp rtcp = new Rtcp();
                rtcp.Rtcp_parse(dPacket.getData());
//                SipClient user = SipClient.getInstance();
                System.out.println("[rtcp.ptt_state]" +rtcp.ptt_state);
                switch (rtcp.ptt_state) {
                    case 1: {
                        System.out.println("RTCPReceiver RTCP State = Granted");
                        if (this.onlistening) {
                            this.onlistening = false;
                        }
                        this.ModeHandler.changemode(1);
                        this.onsending = true;
//                        user.sendEmrInviteOKResponse();
                        grantMsg = new rtcp_granted();
                        grantMsg.parse_all_field(dPacket.getData());
                        break;
                    }
                    case 3: {
                        System.out.println("RTCPReceiver RTCP State = Deny");
//                        if (this.getEMRMediaSipSession(user) != null) {
//                            user.sendEmrInviteOKResponse();
//                            user.sendEMRBye(this.getEMRMediaSipSession(user));
//                        }
                        this.ModeHandler.changemode(2);
                        this.onlistening = false;
                        this.onsending = false;
                        denyMsg = new rtcp_deny();
                        denyMsg.parse_all_field(dPacket.getData());
                        break;
                    }
                    case 2: {
                    	System.out.println("--------------------------------");
                        System.out.println("RTCPReceiver RTCP State = Taken");
                        System.out.println("--------------------------------");
                        takenMsg = new rtcp_taken();
                        takenMsg.parse_all_field(dPacket.getData());
                        Ind = takenMsg.get_Floor_Indicator_field(takenMsg.Floor_Indicator_field);
//                        EMRmediaSipSession = this.getEMRMediaSipSession(user);
//                        if (EMRmediaSipSession != null && MediaSipSession.getCallId().split("@")[1].equals(user.EMR_parameters.EMRIp)) {
//                            System.out.println("RTCP thread floor taken: Session has been set up by EMR");
//                            break;
//                        }
//                        target = String.valueOf(user.EMR_parameters.EMRCardNo) + "@" + user.EMR_parameters.EMRIp;
//                        System.out.println("[onlistening] :"+this.onlistening+", "+Ind+", "+target);
//                        if (!this.onlistening) {
//                            if (Ind == 4096) {
//                                user.sendEMRInvite(target, true);
//                            } else {
//                                user.sendEMRInvite(target, false);
//                            }
//                            this.count = 1;
//                            this.onlistening = true;
//                            System.out.println("[onlistening] :"+this.onlistening);
//                            break;
//                        }
                        ++this.count;
                        if (this.count >= 3) {
                            System.out.println("[RTCPThread][RTCP_MCPT_TAKEN] count >= 3");
//                            if (this.getEMRMediaSipSession(user) != null) {
//                                user.sendEMRBye(this.getEMRMediaSipSession(user));
//                            }
                            try {
                                Thread.sleep(150L);
                            }
                            catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                            if (Ind == 4096) {

                                System.out.println("[RTCPThread][sendEMRInvite] 1");
//                                user.sendEMRInvite(target, true);
                            } else {

                                System.out.println("[RTCPThread][sendEMRInvite] 2");
//                                user.sendEMRInvite(target, false);
                            }
                            this.count = 0;
                            break;
                        }
                        System.out.println("[RTCPThread][RTCP_MCPT_TAKEN] count < 3");
                        Ind.shortValue();
                        this.onsending = false;
                        this.onlistening = true;
                        break;
                    }
                    case 6: {
                        System.out.println("RTCPReceiver RTCP State = Revoke");
//                        if (this.getEMRMediaSipSession(user) != null) {
//                            user.sendEMRBye(this.getEMRMediaSipSession(user));
//                        }
                        if (this.onsending || this.onlistening) {
                            this.ModeHandler.changemode(2);
                            this.onsending = false;
                            this.onlistening = false;
                        } else {
                            this.ModeHandler.changemode(2);
                            this.onsending = false;
                            this.onlistening = false;
                        }
                        revokeMsg = new rtcp_revoke();
                        revokeMsg.parse_all_field(dPacket.getData());
                        break;
                    }
                    case 5: {
                        System.out.println("RTCPReceiver RTCP State = Idle");
                        if (this.onlistening) {
                            dtmfSender dtmf2sender = new dtmfSender(2, this.getHandler().getE_mIP(), this.getHandler().getE_mPort());
                            dtmf2sender.setName("dtmf 2 senfer");
//                            dtmf2sender.createDataSocket(user.EMR_parameters.LMRLocalIp, 22001);
                            dtmf2sender.start();
                            try {
                                Thread.sleep(160L);
                            }
                            catch (InterruptedException e2) {
                                e2.printStackTrace();
                            }
                            dtmf2sender.stopSend();
                        }
//                        if (this.getEMRMediaSipSession(user) != null) {
//                            user.sendEMRBye(this.getEMRMediaSipSession(user));
//                        }
                        this.count = 0;
                        if (this.onsending || this.onlistening) {
                            this.ModeHandler.changemode(2);
                            this.onsending = false;
                            this.onlistening = false;
                        } else {
                            this.ModeHandler.changemode(2);
                            this.onsending = false;
                            this.onlistening = false;
                        }
                        idleMsg = new rtcp_idle();
                        idleMsg.parse_all_field(dPacket.getData());
                        break;
                    }
                    case 16: {
                    	System.out.println("RTCPReceiver RTCP State = Connect");
                        mediaSipSession.setIsConnect(true);
                        replyAck(remoteIP[1], remoteRTCPport, Proxy.getInstance().getRTCPSocket(), (PermissionState)null);
//                        IWFUI.settitle();
//                        IWFUI.Session_Refresh.doClick();
                        if (!this.onlistening) {
                        	System.out.println("[onlistening]");
                          this.ModeHandler.changemode(2);
                          this.onlistening = false;
                        } else {
                        	System.out.println("[onlistening]1");
                          this.ModeHandler.changemode(2);
                        } 
//                        if (!user.getPESessionE()) {
//                        	System.out.println("[getPESessionE]");
//                          user.UpgradeCancel();
//                          user.FloorRequest(this.rtcpSocket);
//                          user.setPESessionE(true);
//                        } 
                        connectMsg = new rtcp_connect();
                        connectMsg.parse_all_field(dPacket.getData());
                        break;
                    }
                    case 17: {
                        System.out.println("RTCP Receiver RTCP State = Disconnect");
                        mediaSipSession.setIsConnect(false);
                        this.replyAck(remoteIP[1], remoteRTCPport, Proxy.getInstance().getRTCPSocket(), null);
                        if (this.onlistening) {
                            this.onlistening = false;
                        }
                        if (this.onsending) {
                            this.onsending = false;
                        }
                        disconnectMsg = new rtcp_disconnect();
                        disconnectMsg.parse_all_field(dPacket.getData());
                        break;
                    }
                    case 9: {
                        System.out.println("RTCPReceiverRTCP State = Queue position Info");
                        queueInfoMsg = new rtcp_queue_position_info();
                        queueInfoMsg.parse_all_field(dPacket.getData());
                        break;
                    }
                    case 10: {
                        System.out.println("RTCPReceiver RTCP State = MCPT ACK");
                        ack = new rtcp_ack();
                        ack.parse_allfield(dPacket.getData());
                        break;
                    }
                    case 8: {
                        System.out.println("RTCPReceiver RTCP State = Queue position request");
                        queuePosReqMsg = new rtcp_queue_position_request();
                        queuePosReqMsg.parse_allfield(dPacket.getData());
                        break;
                    }
                    case 0: {
                        System.out.println("RTCPReceiver RTCP State = Floor request");
                        request = new rtcp_request();
                        request.parse_allfield(dPacket.getData());
                        break;
                    }
                    case 4: {
                        System.out.println("RTCPReceiver RTCP State = Floor release");
                        release = new rtcp_release();
                        release.parse_allfield(dPacket.getData());
                        break;
                    }
                    default: {
                        System.out.println("RTCPReceiverRTCP State = UnKnown ");
                    }
                }
            }
        }
        catch (Throwable e3) {
            e3.printStackTrace();
        }
    }

    private MediaSipSession getCorrectSession(DatagramPacket dPacket) {
        String peerIp = dPacket.getAddress().getHostAddress();
        int peerPort = dPacket.getPort();
//        for (String key : SipClient.mediaSipSessionMap.keySet()) {
//            MediaSipSession mediaSipSession = SipClient.mediaSipSessionMap.get(key);
//            PeerInfo peerInfo = mediaSipSession.getPeerInfo();
//            if ((!mediaSipSession.isActive() || !peerInfo.getPeerIp().equals(peerIp) || peerInfo.getPeerRtcpPort() != peerPort) && (!mediaSipSession.isActive() || !peerIp.equals("127.0.0.1") || peerInfo.getPeerRtcpPort() != peerPort)) continue;
//            System.out.println("[find mediasipsession]");
//            return mediaSipSession;
//        }
        System.out.println("[no getCorrectSession]");
        return null;
    }

//    private MediaSipSession getEMRMediaSipSession(SipClient sipclient) {
//    	 System.out.println("EMRmediaSipSessionMap size = " + SipClient.EMRmediaSipSessionMap.size());
//        MediaSipSession EMRmediaSipSession = null;
//        EMRmediaSipSession = SipClient.EMRmediaSipSessionMap.containsKey(sipclient.getEMRinviteCallID()) ? SipClient.EMRmediaSipSessionMap.get(sipclient.getEMRinviteCallID()) : null;
//        return EMRmediaSipSession;
//    }

    private void replyAck(String peerIP, int peerPort, DatagramSocket socket, PermissionState permissionState) {
        this.sendRTCPAck(peerIP, peerPort, socket, permissionState);
    }

    public void sendRTCPAck(String peerIP, int peerPort, DatagramSocket socket, PermissionState permissionState) {
        RTCPSender sender = new RTCPSender(peerIP, peerPort, socket, 2, permissionState);
        sender.start();
    }

    public void sendRTCPAck2(String peerIP, int peerPort, DatagramSocket socket, PermissionState permissionState) {
        RTCPSender sender = new RTCPSender(peerIP, peerPort, socket, 0, permissionState);
        sender.start();
    }
    
    public void stopThread() {
        this.runThread = false;
        this.ModeHandler.stopThread();
        this.ModeHandler.clearsocket();
        if (!this.isInterrupted()) {
            this.interrupt();
        }
    }

    public void clearsocket() {
        if (!this.rtcpSocket.isClosed()) {
            this.rtcpSocket.close();
        }
    }
}

