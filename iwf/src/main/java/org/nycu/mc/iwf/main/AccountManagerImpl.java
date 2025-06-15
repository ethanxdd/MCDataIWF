package org.nycu.mc.iwf.main;

import javax.sip.ClientTransaction;


import gov.nist.javax.sip.clientauthutils.AccountManager;
import gov.nist.javax.sip.clientauthutils.UserCredentials;

public class AccountManagerImpl
implements AccountManager {
    private String username;
    private String sipDomain;
    private String password;

    public AccountManagerImpl(String username, String sipDomain, String password) {
        this.username = username;
        this.sipDomain = sipDomain;
        this.password = password;
    }

    @Override
    public UserCredentials getCredentials(ClientTransaction challengedTransaction, String realm) {
        return new UserCredentialsImpl(this.username, this.sipDomain, this.password);
    }
}