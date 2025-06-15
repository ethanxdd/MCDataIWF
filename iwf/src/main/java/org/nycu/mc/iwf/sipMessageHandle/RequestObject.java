package org.nycu.mc.iwf.sipMessageHandle;

import java.text.ParseException;
import java.util.Iterator;
import java.util.List;

import javax.sip.PeerUnavailableException;
import javax.sip.SipFactory;
import javax.sip.address.URI;
import javax.sip.header.HeaderFactory;
import javax.sip.message.Request;

import gov.nist.javax.sip.address.GenericURI;
import gov.nist.javax.sip.address.SipUri;
import gov.nist.javax.sip.header.CSeq;
import gov.nist.javax.sip.header.CallID;
import gov.nist.javax.sip.header.Contact;
import gov.nist.javax.sip.header.ContentType;
import gov.nist.javax.sip.header.From;
import gov.nist.javax.sip.header.RequestLine;
import gov.nist.javax.sip.header.SubscriptionState;
import gov.nist.javax.sip.header.To;
import gov.nist.javax.sip.message.Content;
import gov.nist.javax.sip.message.ContentImpl;
import gov.nist.javax.sip.message.MultipartMimeContentImpl;
import gov.nist.javax.sip.message.SIPRequest;

public class RequestObject {
    private static final String TAG = "RequestObject";
    SIPRequest sipRequest;
    SipFactory sipFactory;
    HeaderFactory headerFactory;
    private SIPRequest request;
    private URI requestUri;
    private CallID callId;
    private Contact contact;
    private CSeq cSeq;
    private String event;
    private String contentText;
    private String contentSubType;
    private String contentType;
    private String sdpText;
    private String resourceListText;
    private String groupXml;
    private static String mcptt_info;
    private static String pidf;
    private Integer sdpRtpPort;
    private Integer sdpRtcpPort;
    private String sdpRtpIp;
    private String acceptContact;
    private String requestUriString;
    private List<Integer> sdpRtpMediaFormats;
    private boolean isInvitationForVideo;

    private RequestObject(Request request) throws PeerUnavailableException, ParseException {
        this.sipRequest = (SIPRequest)request;
        this.sipFactory = SipFactory.getInstance();
        this.headerFactory = this.sipFactory.createHeaderFactory();
        this.sipRequest.checkHeaders();
    }

    public static RequestObject parse(Request request) throws PeerUnavailableException, ParseException {
        return new RequestObject(request);
    }

    public SIPRequest getSipRequest() {
        return this.sipRequest;
    }

    public String getMethod() {
        return this.sipRequest.getMethod();
    }

    public String getCallIdString() {
        return this.sipRequest.getCallId().getCallId();
    }

    public From getFrom() {
        return (From)this.sipRequest.getFrom();
    }

    public String getFromHeaderUserName() throws Exception {
        String user = null;
        GenericURI address = (GenericURI)this.getFrom().getAddress().getURI();
        if (address instanceof SipUri) {
            SipUri uri = (SipUri)address;
            user = uri.getUser();
        }
        if (user != null) {
            return user;
        }
        throw new Exception("Bad From Header or User name not found");
    }

    public String getFromTag() {
        return this.getFrom().getTag();
    }

    public String getFromDisplayName() {
        return this.getFrom().getDisplayName();
    }

    public To getTo() {
        return (To)this.sipRequest.getTo();
    }

    public String getToTag() {
        return this.getTo().getTag();
    }

    public String getToDisplayName() {
        return this.getTo().getDisplayName();
    }

    public CSeq getCSeqHeader() {
        return (CSeq)this.sipRequest.getCSeq();
    }

    public long getCSeqNumber() {
        return this.getCSeqHeader().getSeqNumber();
    }

    public RequestLine getRequestLine() {
        return this.sipRequest.getRequestLine();
    }

    public GenericURI getRequestURI() {
        return this.getRequestLine().getUri();
    }

    public Contact getContact() {
        return this.sipRequest.getContactHeader();
    }

    public SubscriptionState getSubscriptionState() throws Exception {
        if (this.sipRequest.hasHeader("Subscription-State")) {
            return (SubscriptionState)this.sipRequest.getHeader("Subscription-State");
        }
        throw new Exception("Subscription header not found");
    }

    public String getContentString() throws ParseException {
        if (this.sipRequest.getRawContent() != null) {
            return new String(this.sipRequest.getRawContent());
        }
        return null;
    }

    public void onMutiPartContent(SIPRequest sipRequest) throws ParseException {
        MultipartMimeContentImpl multipartMimeContentImpl = (MultipartMimeContentImpl)sipRequest.getMultipartMimeContent();
        Iterator<Content> contants = multipartMimeContentImpl.getContents();
        while (contants.hasNext()) {
            ContentImpl partContent = (ContentImpl)contants.next();
            this.onPartContent(partContent);
        }
    }

    public String onPartContent(Content content) {
        ContentType contentType = (ContentType)content.getContentTypeHeader();
        if (contentType != null) {
            String subType = contentType.getContentSubType();
            if (subType.equals("sdp")) {
                this.sdpText = content.getContent().toString();
            } else if (subType.equals("resource-list+xml")) {
                this.resourceListText = content.getContent().toString();
            } else if (subType.equals("group+xml")) {
                this.groupXml = content.getContent().toString();
            } else if (subType.equals("vnd.3gpp.mcptt-info+xml")) {
                mcptt_info = content.getContent().toString();
            } else if (subType.equals("pidf+xml")) {
                pidf = content.getContent().toString();
            }
        }
        return null;
    }

    public String getGroupXml() {
        if (this.sipRequest.getContentTypeHeader() == null) {
            return null;
        }
        try {
            if (this.sipRequest.getContentTypeHeader().getContentType().contains("multipart")) {
                Iterator<Content> it = this.sipRequest.getMultipartMimeContent().getContents();
                while (it.hasNext()) {
                    Content content = it.next();
                    if ((content.getContentTypeHeader() == null || content.getContentTypeHeader().getContentSubType() == null) && !content.getContent().toString().contains("group+xml") || !content.getContent().toString().contains("group+xml") && !content.getContentTypeHeader().getContentSubType().contains("group+xml")) continue;
                    return content.getContent().toString();
                }
            } else if (this.sipRequest.getContentTypeHeader().getContentSubType().contains("group+xml") || this.sipRequest.getContent().toString().contains("group+xml")) {
                return this.sipRequest.getContent().toString();
            }
        }
        catch (ParseException e2) {
            e2.printStackTrace();
        }
        return null;
    }

    public String getSdp() {
        block7: {
            if (this.sipRequest.getContentTypeHeader() != null) break block7;
            return null;
        }
        try {
            if (this.sipRequest.getContentTypeHeader().getContentType().contains("multipart")) {
                Iterator<Content> it = this.sipRequest.getMultipartMimeContent().getContents();
                while (it.hasNext()) {
                    Content content = it.next();
                    if ((content.getContentTypeHeader() == null || content.getContentTypeHeader().getContentSubType() == null) && !content.getContent().toString().contains("sdp") || !content.getContent().toString().contains("sdp") && !content.getContentTypeHeader().getContentSubType().contains("sdp")) continue;
                    return content.getContent().toString();
                }
            } else if (this.sipRequest.getContentTypeHeader().getContentType().contains("sdp") || this.sipRequest.getContentTypeHeader().getContentSubType().contains("sdp")) {
                return new String(this.sipRequest.getRawContent());
            }
        }
        catch (ParseException e2) {
            e2.printStackTrace();
        }
        return null;
    }

    public static String get_mcptt_info() {
        return mcptt_info;
    }

    public static String get_pidf() {
        return pidf;
    }
}

