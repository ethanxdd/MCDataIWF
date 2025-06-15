package org.nycu.mc.iwf.main;

import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

import org.nycu.mc.iwf.main.config.ClientConfig;
import org.nycu.mc.iwf.radio.WebSocketRouter;

public class UserSessionManager {
    private Map<String, UserSession> sessionMap = new HashMap<>();
    private WebSocketRouter router;

    public UserSessionManager(WebSocketRouter router) {
        this.router = router;
    }

    public UserSession getOrCreateSession(ClientConfig config) {
        return sessionMap.computeIfAbsent(config.getUserId(), id -> {
            UserSession session = null;
			try {
				session = new UserSession(config, router);
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            router.registerSession(id, session); // 建立 WebSocket
            return session;
        });
    }

    public void closeSession(String userId) {
        UserSession session = sessionMap.remove(userId);
        if (session != null) {
            session.close();
        }
    }

    public void closeAllSessions() {
        for (UserSession session : sessionMap.values()) {
            session.close();
        }
        sessionMap.clear();
    }
}
