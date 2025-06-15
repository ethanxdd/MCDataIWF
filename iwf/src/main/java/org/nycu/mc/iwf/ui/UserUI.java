package org.nycu.mc.iwf.ui;

import org.nycu.mc.iwf.main.UserSession;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import java.awt.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

public class UserUI extends JFrame {
    private JPanel rightPanel;
    private CardLayout cardLayout;
    private Map<String, JPanel> functionPanels = new HashMap<>();
    private UserSession session;

    public UserUI(UserSession session) {
        this.session = session;
        setTitle("Client 操作介面");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // 左邊：功能選單（用 JList 或按鈕面板）
        String[] functions = {"登入登出", "傳送訊息", "設定狀態"};
        JList<String> functionList = new JList<>(functions);
        functionList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        functionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane leftScroll = new JScrollPane(functionList);
        leftScroll.setPreferredSize(new Dimension(150, 0));

        // 右邊：操作畫面面板（CardLayout）
        cardLayout = new CardLayout();
        rightPanel = new JPanel(cardLayout);

        // 加入各功能對應的畫面（Panel 可自訂）
        functionPanels.put("登入登出", createLoginPanel());
        functionPanels.put("傳送訊息", createSendMessagePanel());
        functionPanels.put("設定狀態", createStatusPanel());

        for (Map.Entry<String, JPanel> entry : functionPanels.entrySet()) {
            rightPanel.add(entry.getValue(), entry.getKey());
        }

        // 分割面板
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftScroll, rightPanel);
        splitPane.setDividerLocation(150);
        splitPane.setDividerSize(4);

        add(splitPane);

        // 點選切換功能畫面
        functionList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selected = functionList.getSelectedValue();
                cardLayout.show(rightPanel, selected);
            }
        });

        functionList.setSelectedIndex(0); // 預設選第一個
    }

    // ==== 各功能區塊 Panel 建立方法 ====

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        JButton loginBtn = new JButton("Log In");
        loginBtn.addActionListener(e -> {
            System.out.println("登入邏輯處理...");
        });

        JButton logoutBtn = new JButton("Log Out");
        logoutBtn.addActionListener(e -> {
            System.out.println("登出邏輯處理...");
        });

        panel.add(loginBtn);
        panel.add(logoutBtn);
        return panel;
    }

    private JPanel createSendMessagePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JTextArea messageArea = new JTextArea();
        JButton sendBtn = new JButton("送出");
        sendBtn.addActionListener(e -> {
            String msg = messageArea.getText();
            System.out.println("傳送訊息：" + msg);
        });

        panel.add(new JScrollPane(messageArea), BorderLayout.CENTER);
        panel.add(sendBtn, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel();
        panel.add(new JLabel("選擇狀態:"));

        String[] statuses = {"Available", "Busy", "Offline"};
        JComboBox<String> combo = new JComboBox<>(statuses);
        panel.add(combo);

        return panel;
    }

	public void appendMessage(String text) {
		// TODO Auto-generated method stub
		
	}
}

