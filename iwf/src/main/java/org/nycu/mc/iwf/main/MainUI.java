package org.nycu.mc.iwf.main;

import java.awt.GridLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;

import org.nycu.mc.iwf.main.config.ClientConfig;
import org.nycu.mc.iwf.radio.WebSocketRouter;
import org.nycu.mc.iwf.ui.UIManager;

public class MainUI extends JFrame {
    private UserSessionManager sessionManager;
    private UIManager uiManager;

    public MainUI(List<ClientConfig> userConfigs, WebSocketRouter router) {
        this.sessionManager = new UserSessionManager(router);
        this.uiManager = new UIManager();

        setTitle("MCData 多使用者面板");
        setLayout(new GridLayout(userConfigs.size(), 1));
        setSize(300, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        for (ClientConfig config : userConfigs) {
            JButton userButton = new JButton("啟動使用者：" + config.getUserId());
            add(userButton);

            userButton.addActionListener(e -> {
                UserSession session = sessionManager.getOrCreateSession(config);
                uiManager.showUI(config.getUserId(), session); // 僅這裡建立與顯示 UI
            });
        }

        setVisible(true);
    }
}