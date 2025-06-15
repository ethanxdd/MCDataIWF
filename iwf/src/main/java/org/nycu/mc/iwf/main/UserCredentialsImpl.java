package org.nycu.mc.iwf.main;

import gov.nist.javax.sip.clientauthutils.UserCredentials;

public class UserCredentialsImpl
implements UserCredentials {
    private String userName;
    private String sipDomain;
    private String password;

    public UserCredentialsImpl(String userName, String sipDomain, String password) {
        this.userName = userName;
        this.sipDomain = sipDomain;
        this.password = password;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getSipDomain() {
        return this.sipDomain;
    }

    @Override
    public String getUserName() {
        return this.userName;
    }
}

