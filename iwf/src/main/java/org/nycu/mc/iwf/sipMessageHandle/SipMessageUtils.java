package org.nycu.mc.iwf.sipMessageHandle;

import java.util.Random;


import gov.nist.core.Host;
import gov.nist.javax.sip.Utils;
import gov.nist.javax.sip.address.Authority;
import gov.nist.javax.sip.address.SipUri;

public class SipMessageUtils {
    public static String generateNewTag() {
        Random random = new Random();
        String tag = "";
        int i2 = 0;
        while (i2 < 8) {
            if (random.nextInt(2) == 0) {
                tag = String.valueOf(tag) + random.nextInt(10);
            } else {
                char[] character;
                int num = random.nextInt(2);
                if (num == 1) {
                    character = new char[]{(char)(Math.random() * 26.0 + 65.0)};
                    tag = String.valueOf(tag) + new String(character);
                } else {
                    character = new char[]{(char)(Math.random() * 26.0 + 97.0)};
                    tag = String.valueOf(tag) + new String(character);
                }
            }
            ++i2;
        }
        return tag;
    }

    public static String generateNewBranchCode() {
        String result = Utils.getInstance().generateBranchId();
        return result;
    }

    public static SipUri createSipUri(String sipUserName, String sipDomain, int port, String password) {
        port = -1;
        SipUri sipUri = new SipUri();
        Host host = new Host(sipDomain);
        Authority a2 = SipMessageUtils.createAuthority(sipUserName, host, port, password);
        sipUri.setAuthority(a2);
        return sipUri;
    }

    public static Authority createAuthority(String sipUserName, Host host, int port, String password) {
        Authority authority = new Authority();
        if (sipUserName != null) {
            authority.setUser(sipUserName);
        }
        authority.setHost(host);
        if (password != null) {
            authority.setPassword(password);
        }
        if (port != -1) {
            authority.setPort(port);
        }
        return authority;
    }
}

