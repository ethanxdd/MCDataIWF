package org.nycu.mc.iwf.Session;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

import javax.sip.Dialog;
import javax.sip.Transaction;

import org.nycu.mc.iwf.main.MCDataClient;

import gov.nist.javax.sip.address.SipUri;
import gov.nist.javax.sip.stack.SIPClientTransactionImpl;

public class MediaSipSession {
    private static String TAG = "MediaSipSession";
    private static String sipTarget;
    private static String callId;
    private String myTag;
    private String callIdIp;
    private boolean isActive;
    private boolean isfirstTalk;
//    private static PermissionState permissionState;
    private Dialog dialog;
//    private MediaSessionState mediaSessionState;
    private Transaction inviteTransacation;
    private boolean isEmergency;
    private Map<String, String> userActiveMap;
//    private PeerInfo peerInfo;
//    private MeaninglessSender rtcpMeaninglesSender;
    private boolean isConnect = false;
    private boolean isGroup;

    public MediaSipSession(String callId, String tag, Dialog dialog, String sipTarget, SipUri peerSipUri, boolean isGroup) throws SocketException {
        MediaSipSession.callId = callId;
        this.myTag = tag;
        this.dialog = dialog;
        MediaSipSession.sipTarget = sipTarget;
        this.isGroup = isGroup;
        this.createSipSession(peerSipUri);
    }

    private void createSipSession(SipUri peerSipUri) {
        this.isfirstTalk = true;
        this.isActive = false;
//        permissionState = new PermissionState(this);
        this.userActiveMap = new HashMap<String, String>();
//        this.mediaSessionState = new MediaSessionState(this);
//        this.peerInfo = new PeerInfo(peerSipUri);
    }

    public Dialog getDialog() {
        return this.dialog;
    }

    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
    }

    public static String getCallId() {
        return callId;
    }

    public String getMyTag() {
        return this.myTag;
    }

    public String getCallIdIp() {
        return this.callIdIp;
    }

    public boolean isfirstTalk() {
        return this.isfirstTalk;
    }

    public void setIsfirstTalk(boolean isfirstTalk) {
        this.isfirstTalk = isfirstTalk;
    }

    public boolean isActive() {
        return this.isActive;
    }

    public void setIsActive(boolean isActive) {
//        this.isActive = isActive;
//        if (isActive) {
//            SipClient sipClient = SipClient.getInstance();
//            if (sipClient != null) {
//                this.subscribeGroup(sipClient);
//            }
//            if (this.rtcpMeaninglesSender == null) {
//            	System.out.println("[setIsActive]-rtcp");
//                DatagramSocket socket = Proxy.getInstance().getRTCPSocket();
//                this.rtcpMeaninglesSender = new MeaninglessSender(socket, this.peerInfo.getPeerIp(), this.peerInfo.getPeerRtcpPort(), "0");
//                this.rtcpMeaninglesSender.setTag("setIsActive: true  Meaningless send");
//                this.rtcpMeaninglesSender.printInfo();
//            }
//            this.rtcpMeaninglesSender.start();
//        } else if (this.rtcpMeaninglesSender != null) {
//            this.rtcpMeaninglesSender.stop();
//        }
//        if (!this.isGroup) {
//            this.isConnect = isActive;
//        }
    }

    private void subscribeGroup(final MCDataClient sipClient) {
//        Runnable runnable = new Runnable(){
//
//            @Override
//            public void run() {
//                sipClient.sendConferenceSubscribe(sipTarget);
//                Thread.currentThread().interrupt();
//            }
//        };
//        Thread thread = new Thread(runnable);
//        thread.setName("subscribeGroup");
//        thread.start();
    }

//    public MeaninglessSender getMeaningSender() {
//        return this.rtcpMeaninglesSender;
//    }
//
//    public static PermissionState getPermissionState() {
//        return permissionState;
//    }

    public static String getSipTarget() {
        return sipTarget;
    }

//    public MediaSessionState getMediaSessionState() {
//        return this.mediaSessionState;
//    }

    public Transaction getInviteTransacation() {
        return this.inviteTransacation;
    }

    public void setInviteTransacation(Transaction inviteTransacation) {
        this.inviteTransacation = inviteTransacation;
    }

    public boolean isUAC() {
        return this.inviteTransacation instanceof SIPClientTransactionImpl;
    }

    public boolean isEmergency() {
        return this.isEmergency;
    }

    public void setIsEmergency(boolean isEmergency) {
        this.isEmergency = isEmergency;
    }

    public Map<String, String> getUserActiveMap() {
        return this.userActiveMap;
    }

    public void setUserActiveMap(Map<String, String> userActiveMap) {
        this.userActiveMap = userActiveMap;
    }

//    public PeerInfo getPeerInfo() {
//        return this.peerInfo;
//    }

    public boolean isConnect() {
        return this.isConnect;
    }

    public void setIsConnect(boolean isConnect) {
        this.isConnect = isConnect;
    }

    public boolean isGroup() {
        return this.isGroup;
    }

    public void setIsGroup(boolean isGroup) {
        this.isGroup = isGroup;
    }
}

