package org.nycu.mc.iwf.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.nycu.mc.iwf.main.config.ClientConfig;

public class ConfigLoader {
    private List<ClientConfig> users = new ArrayList<>();
    private String Radioserverip = "";

    public ConfigLoader(String filePath) {
        loadConfig(filePath);
    }

    private void loadConfig(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue; // 跳過空行和註解
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String userId = parts[0].trim();
                    String password = parts[1].trim();
                    String displayName = parts[2].trim();
                    String localip = parts[3].trim();
                    String serverip = parts[4].trim();
                    String radioserverip = parts[5].trim();
                    Radioserverip = radioserverip;
                    users.add(new ClientConfig(userId, password, displayName, localip, serverip, radioserverip));
                }
            }
        } catch (IOException e) {
            System.err.println("讀取 config 檔案失敗：" + e.getMessage());
        }
    }

    public List<ClientConfig> getUsers() {
        return users;
    }
    
    public String getRadioServerIp() {
        return Radioserverip;
    }
}

