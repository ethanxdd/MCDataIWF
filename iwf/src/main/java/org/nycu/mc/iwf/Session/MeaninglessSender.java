package org.nycu.mc.iwf.Session;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;




public class MeaninglessSender{
	
    private Timer timer;///
    private long timePeriod;
    private String remoteIp;
    private int remotePort;
    private DatagramSocket udpSocket;
    private String tag;
    private boolean isStarted;
   
    public MeaninglessSender(DatagramSocket udpSocket, String remoteIp, int remotePort) {
    	this.udpSocket = udpSocket;
    	this.remoteIp = remoteIp;
    	this.remotePort = remotePort;
	    init();
    }
   
    private void init() {
    	this.isStarted = false;
    	this.timePeriod = 20000L;
    	this.tag = null;
    }
   
	 public void start() {
		 if (!this.isStarted) {
			 this.timer = new Timer();
			 this.timer.schedule(new SenderTimerTask(), 0L, this.timePeriod);
			 this.isStarted = true;
		 } 
	 }
  
	 public void stop() {
		 if (this.isStarted) {
			 this.isStarted = false;
			 this.timer.cancel();
			 this.timer = null;
		 } 
	 }
  
	 public void setTag(String tag) {
		 this.tag = tag;
	 }
  
	 public long getTimePeriod() {
		 return this.timePeriod;
	 }
 
	 public void setTimePeriod(long timePeriod) {
		 this.timePeriod = timePeriod;
	 }
 
	 private class SenderTimerTask extends TimerTask {
		 public void run() {
			 DatagramPacket packet = new DatagramPacket(new byte[0], 0);
	   
			 try {
				 packet.setAddress(InetAddress.getByName(MeaninglessSender.this.remoteIp));
				 packet.setPort(MeaninglessSender.this.remotePort);
				 MeaninglessSender.this.udpSocket.send(packet);
			 } catch (UnknownHostException e) {
				 e.printStackTrace();
			 } catch (IOException e) {
				 e.printStackTrace();
				 MeaninglessSender.this.stop();
			 } 
		 }
		 
		 private SenderTimerTask() {}
	 }

	 public void printInfo() {}
}