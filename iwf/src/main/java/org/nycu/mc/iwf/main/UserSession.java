package org.nycu.mc.iwf.main;

import java.net.SocketException;
import java.text.ParseException;
import java.util.UUID;

import javax.sip.InvalidArgumentException;
import javax.sip.SipException;

import org.nycu.mc.iwf.main.config.ClientConfig;
import org.nycu.mc.iwf.radio.WebSocketRouter;
import org.nycu.mc.iwf.ui.UserUI;

public class UserSession {
    private ClientConfig config;
    private MCDataClient client;
    private UserUI ui; // 不主動建立
    private SipRegisterSender sipRegisterSender;

    public UserSession(ClientConfig config, WebSocketRouter router) throws SocketException {
        this.config = config;
        this.client = new MCDataClient(config, router, this);
        this.client.setisRegister(true);
		sipRegisterSender = new SipRegisterSender(this.client, 120);
		sipRegisterSender.setName("registerSender");
		sipRegisterSender.start();
		this.client.publishsender();
    }

    // UIManager 呼叫後設定 UI
    public void setUI(UserUI ui) {
        this.ui = ui;
    }

    public void sendText(String text) {
//        client.sendText(text);
    }

    public void receiveMessage(String text, int dialType, String groupname) throws ParseException, InvalidArgumentException, SipException {//解析無線電端來的值 在選擇要值轄下一ˋㄨ
//        if (ui != null) {
//            ui.appendMessage(text);
//        }
    	UUID conversationID_uuid = UUID.randomUUID();
	     String conversationID_uuidAsString = conversationID_uuid.toString();
    	String grouporone = "group";
    	System.out.println("[receiveMessage]"+groupname);
    	client.sendMESSAGE(groupname, text, grouporone, "text", groupname, true, conversationID_uuidAsString);
    }

    public String getDisplayName() {
        return config.getDisplayName();
    }

    public String getUserId() {
        return config.getUserId();
    }

    public void close() {
        if (ui != null) ui.dispose();
    }
}