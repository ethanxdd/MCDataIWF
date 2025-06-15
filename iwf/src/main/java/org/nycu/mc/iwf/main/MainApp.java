package org.nycu.mc.iwf.main;

import javax.swing.SwingUtilities;

import org.nycu.mc.iwf.radio.WebSocketRouter;

public class MainApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ConfigLoader loader = new ConfigLoader("config.txt");
            WebSocketRouter router = new WebSocketRouter(loader.getRadioServerIp());
            router.connect("wss://192.168.0.36:9091/ws");
//            router.connect("wss://"+loader.getRadioServerIp()+"/ws");
            new MainUI(loader.getUsers(), router);  // 顯示主介面
        });
    }
}
