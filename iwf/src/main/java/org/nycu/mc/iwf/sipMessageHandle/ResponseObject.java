package org.nycu.mc.iwf.sipMessageHandle;

import javax.sip.message.Response;


import gov.nist.javax.sip.address.GenericURI;
import gov.nist.javax.sip.address.SipUri;
import gov.nist.javax.sip.header.CSeq;
import gov.nist.javax.sip.header.From;
import gov.nist.javax.sip.header.SIPETag;
import gov.nist.javax.sip.header.To;
import gov.nist.javax.sip.header.Via;
import gov.nist.javax.sip.message.SIPResponse;

public class ResponseObject {
    private SIPResponse sipResponse;
    private static final String TAG = "ResponseObject";

    private ResponseObject(Response response) {
        this.sipResponse = (SIPResponse)response;
    }

    public static ResponseObject parse(Response response) {
        return new ResponseObject(response);
    }

    public String getTopmostViaReceiveAddress() {
        Via via = this.sipResponse.getTopmostVia();
        return String.valueOf(via.getReceived()) + ":" + via.getRPort();
    }

    public SIPResponse getSipResponse() {
        return this.sipResponse;
    }

    public String getFromHeaderUserAtHostPort() {
        return this.getFrom().getUserAtHostPort();
    }

    public String getCallIdString() {
        return this.sipResponse.getCallId().getCallId();
    }

    public String getFromHeaderUserName() throws Exception {
        String user = null;
        GenericURI address = (GenericURI)this.getFrom().getAddress().getURI();
        if (address instanceof SipUri) {
            SipUri uri = (SipUri)address;
            user = uri.getAuthority().getUserInfo().getUser();
        }
        if (user != null) {
            return user;
        }
        throw new Exception("Bad From Header or User name not found");
    }

    public String getEventHeader() {
        String event = null;
        if (this.sipResponse.getHeader("Event") != null && (event = this.sipResponse.getHeader("Event").toString()).contains(" ")) {
            event = event.substring(event.indexOf(" "), event.length());
        }
        return event;
    }

    public To getTo() {
        return (To)this.sipResponse.getTo();
    }

    public From getFrom() {
        return (From)this.sipResponse.getFrom();
    }

    public CSeq getCSeq() {
        return (CSeq)this.sipResponse.getCSeq();
    }

    public int getStatusCode() {
        return this.sipResponse.getStatusCode();
    }

    public int getContactExpires() {
        if (this.sipResponse.getContactHeader() == null) {
            return -1;
        }
        return this.sipResponse.getContactHeader().getExpires();
    }

    public byte[] getRawContent() {
        return this.sipResponse.getRawContent();
    }

    public String getToHeaderUserName() throws Exception {
        String user = null;
        GenericURI address = (GenericURI)this.getTo().getAddress().getURI();
        if (address instanceof SipUri) {
            SipUri uri = (SipUri)address;
            user = uri.getAuthority().getUserInfo().getUser();
        }
        if (user != null) {
            return user;
        }
        throw new Exception("Bad To Header or User name not found");
    }

    public String getToTag() {
        return this.sipResponse.getToTag();
    }

    public SIPETag getSIPETag() {
        if (this.sipResponse.hasHeader("SIP-ETag")) {
            return (SIPETag)this.sipResponse.getHeader("SIP-ETag");
        }
        return null;
    }

    public String getSIPETagString() {
        String et = "";
        if (this.getSIPETag() != null) {
            et = this.getSIPETag().getETag();
        }
        return et;
    }
}

