package org.nycu.mc.iwf.RTCP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.nycu.mc.iwf.Session.PermissionState;


public class RTCPSender
extends Thread {
    private String peerAddress;
    private String clienturi;
    private String sessionid;
    private int peerRTCPPort;
    private int count = 0;
    private int localPort;
    DatagramSocket socket = null;
    int packetState;
    private PermissionState permissionState;
    private int tbPriority;
    private int lastSeq;
    private short ind = Short.MIN_VALUE;
//    private SipClient sipclient;

    public RTCPSender(String peerAddress, int peerRTCPPort, DatagramSocket socket, int packetState, PermissionState permissionState) {
        this.peerAddress = peerAddress;
        this.peerRTCPPort = peerRTCPPort;
        this.socket = socket;
        this.packetState = packetState;
        this.permissionState = permissionState;
        System.out.println("[RTCPSender]----------");
    }

    public void set(String clientUri, String sessionId, int priority) {
        this.clienturi = clientUri;
        this.sessionid = sessionId;
        this.tbPriority = priority;
    }

    /*
     * Exception decompiling
     */
    @Override
    public void run() {
    	System.out.println("[run]----------");
        try {
          rtcp_request requestmsg;
          int ignoreSeqNum;
          rtcp_release releaseMsg;
          rtcp_acknowledgement mcpcAck;
          rtcp_queue_position_request queuereqMsg;
          rtcp_ack mcptAck;
          rtcp_connect mcpcConnect;
          rtcp_disconnect disconnectMsg;
          rtcp_idle idleMsg;
          rtcp_deny denyMsg;
          rtcp_granted grantMsg;
          rtcp_queue_position_info queueInfoMsg;
          rtcp_revoke revokeMsg;
          InetAddress peerIP = InetAddress.getByName(this.peerAddress);
          Rtcp rtcp = new Rtcp();
          TimeStamp ts = TimeStamp.getCurrentTime();
          System.out.println("[packetState]----------"+packetState+";");
          switch (this.packetState) {
            case 0:
            	requestmsg = new rtcp_request(rtcp.gen_ssrc().longValue(), getTBPriority(), getclienturi(), this.ind);
                sendRtcp(rtcp, requestmsg.message, peerIP);
                System.out.println("===== sent RTCP-MCPT-Floor Request =====>");
              break;
            case 4:
            	System.out.println("[case 4]----------");
              ignoreSeqNum = 0;
              if (this.lastSeq == -1)
                ignoreSeqNum = 1; 
              releaseMsg = new rtcp_release(rtcp.gen_ssrc().longValue(), getclienturi(), this.ind);
              sendRtcp(rtcp, releaseMsg.message, peerIP);
              System.out.println("===== sent RTCP-MCPT-Floor-Release Message=====>");
              break;
            case 2:
              mcpcAck = new rtcp_acknowledgement(rtcp.gen_ssrc().longValue(), 0);
              sendRtcp(rtcp, rtcp_acknowledgement.message, peerIP);
              System.out.println("===== sent RTCP-MCPC-Acknowledgement =====>");

              break;
            case 8:
              queuereqMsg = new rtcp_queue_position_request(rtcp.gen_ssrc().longValue(), getclienturi());
              sendRtcp(rtcp, queuereqMsg.message, peerIP);
              System.out.println("===== sent RTCP Floor Queue Position Request =====>");
              break;
            case 10:
              mcptAck = new rtcp_ack(rtcp.gen_ssrc().longValue(), 0);
              sendRtcp(rtcp, rtcp_ack.message, peerIP);
              System.out.println("===== sent RTCP Floor Ack =====>");
              break;
            case 16:
              mcpcConnect = new rtcp_connect(rtcp.gen_ssrc().longValue(), getclienturi(), getsessionId(), 1, 2, 0);
              sendRtcp(rtcp, rtcp_connect.message, peerIP);
              System.out.println("===== sent MCPC Connect Message =====>");
              break;
            case 17:
              disconnectMsg = new rtcp_disconnect(rtcp.gen_ssrc().longValue(), getsessionId());
              sendRtcp(rtcp, disconnectMsg.message, peerIP);
              System.out.println("===== sent RTCP-MCPC-disconnect Request =====>");
              break;
            case 5:
              idleMsg = new rtcp_idle(rtcp.gen_ssrc().longValue(), (short)10, this.ind);
              sendRtcp(rtcp, idleMsg.message, peerIP);
              System.out.println("===== sent RTCP-MCPT-IDLE Request =====>");
              break;
            case 3:
              denyMsg = new rtcp_deny(rtcp.gen_ssrc().longValue(), 4, getclienturi(), this.ind);
              sendRtcp(rtcp, denyMsg.message, peerIP);
              System.out.println("===== sent RTCP-MCPT-DENY Request =====>");
              break;
            case 1:
              grantMsg = new rtcp_granted(rtcp.gen_ssrc().longValue(), rtcp.gen_ssrc().longValue(), (short)60, 4, this.ind);
              sendRtcp(rtcp, grantMsg.message, peerIP);
              System.out.println("===== sent RTCP-MCPT-Granted Request =====>");
              break;
            case 9:
              queueInfoMsg = new rtcp_queue_position_info(rtcp.gen_ssrc().longValue(), getclienturi(), 2, 4, this.ind);
              sendRtcp(rtcp, queueInfoMsg.message, peerIP);
              System.out.println("===== sent RTCP-MCPT-QUEUE Position Info Request =====>");
              break;
            case 6:
              revokeMsg = new rtcp_revoke(rtcp.gen_ssrc().longValue(), (short)255, this.ind);
              sendRtcp(rtcp, revokeMsg.message, peerIP);
              System.out.println("===== sent RTCP-MCPT-Revoke Request =====>");
              break;
          } 
        } catch (SocketException e) {
          e.printStackTrace();
        } catch (UnknownHostException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        } catch (Exception exception) {
        
        } finally {
          Thread.currentThread().interrupt();
        } 
      }

    public void setLastSeq(int lastSeq) {
        this.lastSeq = lastSeq;
    }

    private void sendRtcp(Rtcp rtcp, byte[] rtcpMsg, InetAddress peerIP) throws IOException {
    	System.out.println("[sendRtcp]"+this.count);
        DatagramPacket packet = new DatagramPacket(rtcpMsg, rtcpMsg.length, peerIP, this.peerRTCPPort);
        this.socket.send(packet);
        ++this.count;
    }

    public void setclienturi(String client) {
        this.clienturi = client;
    }

    public String getclienturi() {
        return this.clienturi;
    }

    public void setsessionId(String sessionId) {
        this.sessionid = sessionId;
    }

    public String getsessionId() {
        if (this.sessionid == null) {
            this.sessionid = "setsessionId@domain";
        }
        return this.sessionid;
    }

    public void setTBPriority(int tbPriority) {
        this.tbPriority = tbPriority;
    }

    public int getTBPriority() {
        if (this.tbPriority == 0) {
            this.tbPriority = 4;
        }
        return this.tbPriority;
    }

    public boolean isFRRequest() {
        return this.packetState == 0;
    }

    public void setInd(short Ind) {
        this.ind = Ind;
    }

    public short getInd() {
        return this.ind;
    }
}

