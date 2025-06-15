package org.nycu.mc.iwf.main.config;

public class ClientConfig {
    private String userId;
    private String password;
    private String displayName;
    private String localip;
    private String serverip;
    private String radioserverip;

    public ClientConfig(String userId, String password, String displayName, String localip, String serverip, String radioserverip) {
        this.userId = userId;
        this.password = password;
        this.displayName = displayName;
        this.localip = localip;
        this.serverip = serverip;
        this.radioserverip = radioserverip;
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public String getDisplayName() {
        return displayName;
    }
    
    public String getLocalIp() {
        return localip;
    }
    
    public String getServerIp() {
        return serverip;
    }
    
    public String getRadioServerIp() {
    	return radioserverip;
    }

    @Override
    public String toString() {
        return displayName + " (" + userId + "), localip: "+localip ;
    }
}

