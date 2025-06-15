package org.nycu.mc.iwf.ui;

import java.util.HashMap;
import java.util.Map;

import org.nycu.mc.iwf.main.UserSession;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class UIManager {
    private Map<String, UserUI> uiMap = new HashMap<>();

    public void showUI(String userId, UserSession session) {
        UserUI ui = uiMap.get(userId);
        if (ui == null) {
            ui = new UserUI(session);
            uiMap.put(userId, ui);
            session.setUI(ui); // 將 UI 傳回給 Session
        }
        ui.setVisible(true);
    }

    public void closeUI(String userId) {
        UserUI ui = uiMap.get(userId);
        if (ui != null) {
            ui.dispose();
            uiMap.remove(userId);
        }
    }

    public void closeAll() {
        for (UserUI ui : uiMap.values()) {
            ui.dispose();
        }
        uiMap.clear();
    }

    public UserUI getUI(String userId) {
        return uiMap.get(userId);
    }
}