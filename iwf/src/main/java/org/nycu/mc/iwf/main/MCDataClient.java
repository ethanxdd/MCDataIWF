package org.nycu.mc.iwf.main;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TooManyListenersException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.msrp.exceptions.InternalErrorException;
//import javax.net.msrp.exceptions.InternalErrorException;
import javax.sip.ClientTransaction;
import javax.sip.Dialog;
import javax.sip.DialogTerminatedEvent;
import javax.sip.IOExceptionEvent;
import javax.sip.InvalidArgumentException;
import javax.sip.ListeningPoint;
import javax.sip.ObjectInUseException;
import javax.sip.PeerUnavailableException;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.SipException;
import javax.sip.SipFactory;
import javax.sip.SipListener;
import javax.sip.SipProvider;
import javax.sip.SipStack;
import javax.sip.TimeoutEvent;
import javax.sip.TransactionAlreadyExistsException;
import javax.sip.TransactionTerminatedEvent;
import javax.sip.TransactionUnavailableException;
import javax.sip.TransportAlreadySupportedException;
import javax.sip.TransportNotSupportedException;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.address.URI;
import javax.sip.header.AllowHeader;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.ContentDispositionHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.EventHeader;
import javax.sip.header.ExpiresHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.Header;
import javax.sip.header.HeaderFactory;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.header.SIPIfMatchHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;
import javax.swing.JOptionPane;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.nycu.mc.iwf.Session.MediaSipSession;
import org.nycu.mc.iwf.Session.MySdpInfo;
import org.nycu.mc.iwf.Session.PeerInfo;
import org.nycu.mc.iwf.main.config.ClientConfig;
import org.nycu.mc.iwf.proxy.Proxy;
import org.nycu.mc.iwf.radio.RadioMessage;
import org.nycu.mc.iwf.radio.WebSocketRouter;
import org.nycu.mc.iwf.sipMessageHandle.RequestObject;
import org.nycu.mc.iwf.sipMessageHandle.ResponseObject;
import org.nycu.mc.iwf.sipMessageHandle.SipMessageUtils;
import org.nycu.mc.iwf.xml.PIDFXmlBuilder;
import org.nycu.mc.iwf.xml.Pidf;
import org.nycu.mc.iwf.xml_generator.try_mcdatainfo_factory;
import org.nycu.mc.iwf.xml_generator.try_mcdatapayload_factory;
import org.nycu.mc.iwf.xml_generator.try_mcdataresource_factory;
import org.nycu.mc.iwf.xml_generator.try_mcdatasignalling_factory;
import org.nycu.mc.iwf.xml_generator.try_mcpttinfo_factory;
import org.nycu.mc.iwf.xml_generator.try_parser;
import org.xml.sax.SAXException;

import gov.nist.javax.sip.SipStackExt;
import gov.nist.javax.sip.Utils;
import gov.nist.javax.sip.address.SipUri;
import gov.nist.javax.sip.clientauthutils.AccountManager;
import gov.nist.javax.sip.clientauthutils.AuthenticationHelper;
import gov.nist.javax.sip.header.ContentType;
import gov.nist.javax.sip.header.HeaderFactoryImpl;
import gov.nist.javax.sip.header.ReferTo;
import gov.nist.javax.sip.header.Require;
import gov.nist.javax.sip.header.UserAgent;
import gov.nist.javax.sip.header.ims.PPreferredIdentityHeader;
import gov.nist.javax.sip.message.Content;
import gov.nist.javax.sip.message.ContentImpl;
import gov.nist.javax.sip.message.MultipartMimeContentImpl;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.message.SIPResponse;
import gov.nist.javax.sip.stack.SIPClientTransaction;
import gov.nist.javax.sip.stack.SIPDialog;
import gov.nist.javax.sip.stack.SIPServerTransactionImpl;



public class MCDataClient implements SipListener{
    private ClientConfig config;
    private WebSocketRouter router;
    private UserSession session;
    
    private String localIp;
    private int localPort;
    private static String RemoteIp;
    private static int RemotePort;
    private String RemoteEndpoint;
    private String sipUserName;
    private String sipPassword;
    private String mySipUri;
    private String localEndpoint;
    private static String transport;
//    private String sipUserName;
//    private String sipPassword;
    
    private Map<String, SubscribeDialog> subscribeMap;
    private Map<String, MediaSipSession> mediaSipSessionMap;
    
    private String StunServer;
    private static AccountManager accountManager;
    public static SipStack sipStack;
    public SipFactory sipFactory;
    public SipProvider sipProvider;
    public SipProvider EMRProvider;
    public HeaderFactory headerFactory;
    public AddressFactory addressFactory;
    public MessageFactory messageFactory;
    public ListeningPoint udpListeningPoint;
    public ListeningPoint tcpListeningPoint;
    public static SipPublishSender sipPublishSender;
    
    private Proxy proxy;
    private DatagramSocket socket;
    
    private String registerContactAddress;
    public String registerCallId;
    public int registerCSeq;
    public String myTag;
    public String publishCallId;
    public int publishCSeq;
    private int ErrorAccount = 0;
    private int RtpPort = 0;

    private ClientTransaction inviteTransaction;
    private ClientTransaction publishTransaction;
    
//    private Map<String, MediaSipSession> mediaSipSessionMap;
//    private Map<String, MediaSipSession> EMRmediaSipSessionMap;
    private Boolean PESessionE= true;
    private int kyinfoValue = 0;
    private String pIP = "";
    private int pRtcpPort = 0;
    private int pRtpPort = 0;

    private long sessinID = -1L;
    private int registerCount = 0;
    private int messageCSeq;
    
    private boolean isRegister = false;

    static {
        transport = "tcp";
        RemoteIp = "n/a";
        RemotePort = -1;
//        mediaSipSessionMap = new HashMap<String, MediaSipSession>();
//        EMRmediaSipSessionMap = new HashMap<String, MediaSipSession>();
//        PESessionE = true;
//        sessinID = -1L;
    }

    
    public MCDataClient(ClientConfig config, WebSocketRouter router, UserSession session) throws SocketException {
        this.config = config;
        this.router = router;
        this.session = session;
        this.initialize(config.getUserId(), config.getPassword(), config.getServerIp(), config.getLocalIp());
    }
    
    private void initialize(String userName, String password, String sipServer, String privateIp) throws SocketException {
        int privatePort = 5060;
        localIp = privateIp;
        localPort = privatePort;
        this.subscribeMap = new HashMap<String, SubscribeDialog>();
        if (sipServer.contains(":")) {
            String[] hostPort = sipServer.split(":");
            RemoteIp = hostPort[0];
            RemotePort = Integer.parseInt(hostPort[1]);
        } else {
            RemoteIp = sipServer;
            RemotePort = 5060;
            this.StunServer = String.valueOf(RemoteIp) + ":3478";
            RemoteEndpoint = String.valueOf(RemoteIp) + ":" + RemotePort;
            sipUserName = userName;
            sipPassword = password;
            mySipUri = "sip:" + userName + "@" + sipServer;
            accountManager = new AccountManagerImpl(userName, RemoteEndpoint, sipPassword);
            sipStack = null;
            this.inviteTransaction = null;
            sipFactory = SipFactory.getInstance();
            sipFactory.setPathName("gov.nist");
            this.setstack(privateIp);
            if (sipStack != null) {
                try {
                    headerFactory = sipFactory.createHeaderFactory();
                    addressFactory = sipFactory.createAddressFactory();
                    messageFactory = sipFactory.createMessageFactory();
                    this.setLocalEnpoint(privateIp, privatePort);
                    this.setListeningPoint(privateIp, privatePort);
                    sipProvider.setAutomaticDialogSupportEnabled(false);
//                    this.setEMRListeningPoint(this.EMR_parameters.LMRLocalIp, Integer.parseInt(this.EMR_parameters.LMRLocalPort));
//                    EMRProvider.setAutomaticDialogSupportEnabled(false);
                    registerCallId = sipProvider.getNewCallId().toString().replace("Call-ID:", "").trim();
                    registerCSeq = 0;
                    myTag = SipMessageUtils.generateNewTag();
                    publishCallId = sipProvider.getNewCallId().toString().replace("Call-ID:", "").trim();
                    publishCSeq = 0;
                }
                catch (PeerUnavailableException e2) {
                    e2.printStackTrace();
                }
                catch (Exception e3) {
                    e3.printStackTrace();
                }
            }
        }
    }
    
    private void setstack(String privateIp) {
        Properties properties = new Properties();
        properties.setProperty("javax.sip.IP_ADDRESS", privateIp);
        properties.setProperty("javax.sip.STACK_NAME", "IWF");
        properties.setProperty("gov.nist.javax.sip.MAX_MESSAGE_SIZE", "1048576");
        properties.setProperty("gov.nist.javax.sip.DEBUG_LOG", String.valueOf(sipUserName) + "clientbug.txt");
        properties.setProperty("gov.nist.javax.sip.SERVER_LOG", String.valueOf(sipUserName) + "clientlog.txt");
        properties.setProperty("gov.nist.javax.sip.CACHE_CLIENT_CONNECTIONS", "True");
        properties.setProperty("gov.nist.javax.sip.DELIVER_UNSOLICITED_NOTIFY", "true");
        try {
            sipStack = sipFactory.createSipStack(properties);
        }
        catch (PeerUnavailableException e2) {
            System.err.println(e2.getMessage());
            e2.printStackTrace();
        }
    }
    
    public void setLocalEnpoint(String localIp, int localPort) {
        registerContactAddress = localEndpoint = (String.valueOf(localIp) + ":" + localPort).trim();
    }
    
    public void setListeningPoint(String privateIp, int port) throws InvalidArgumentException, TransportNotSupportedException, ObjectInUseException, TooManyListenersException, TransportAlreadySupportedException {
        tcpListeningPoint = sipStack.createListeningPoint(privateIp, port, "tcp");
        sipProvider = sipStack.createSipProvider(tcpListeningPoint);
        sipProvider.addSipListener(this);
    }
    
    public void publishsender() {
        sipPublishSender = new SipPublishSender(this, "open", 120);
        sipPublishSender.setName("publishSender");
    }
    
    private ArrayList<ViaHeader> createViaHeader() {
        ArrayList<ViaHeader> viaHeaders = new ArrayList<ViaHeader>();
        try {
            ViaHeader myViaHeader = headerFactory.createViaHeader(registerContactAddress.split(":")[0], Integer.parseInt(registerContactAddress.split(":")[1]), transport, Utils.getInstance().generateBranchId());
            myViaHeader.setRPort();
            viaHeaders.add(myViaHeader);
        }
        catch (ParseException e2) {
            e2.printStackTrace();
        }
        catch (InvalidArgumentException e3) {
            e3.printStackTrace();
        }
        return viaHeaders;
    }
    
    private Address createContactAddress() {
        try {
            return addressFactory.createAddress("sip:" + sipUserName + "@" + registerContactAddress + ";transport=tcp");
        }
        catch (ParseException e2) {
            return null;
        }
    }
    
    private byte[] inputStreamToBytes(InputStream inputStream) throws IOException {
        int nRead;
        byte[] data = new byte[16384];
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }
    
    public void sendPresencePublish(String onlineStatus, int expires, String eTag) {
        try {
            CallIdHeader callIdHeader;
            SipProvider sipProvider = this.sipProvider;
            if (publishCallId == null) {
                callIdHeader = sipProvider.getNewCallId();
                publishCallId = callIdHeader.getCallId();
            } else {
                callIdHeader = headerFactory.createCallIdHeader(publishCallId);
            }
            SipUri requestUri = SipMessageUtils.createSipUri(sipUserName, RemoteIp, RemotePort, null);
            CSeqHeader cSeqHeader = headerFactory.createCSeqHeader((long)(++publishCSeq), "PUBLISH");
            Address fromAddress = addressFactory.createAddress("sip:" + sipUserName + "@" + RemoteIp);
            FromHeader fromHeader = headerFactory.createFromHeader(fromAddress, myTag);
            ToHeader toHeader = headerFactory.createToHeader(fromAddress, null);
            ArrayList<ViaHeader> viaList = this.createViaHeader();
            MaxForwardsHeader maxForwardsHeader = headerFactory.createMaxForwardsHeader(70);
            Address localAddress = this.createContactAddress();
            ContentTypeHeader contentTypeHeader = headerFactory.createContentTypeHeader("application", "pidf+xml");
            Pidf pidf = new Pidf();
            pidf.setTupleId(sipUserName);
            pidf.setNote("My PIDF");
            pidf.setStatus(onlineStatus);
            pidf.setContactPriority("0.8");
            pidf.setEntityName(String.valueOf(sipUserName) + "@" + RemoteIp);
            pidf.setContact(localAddress.toString());
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String dateTime = formatter.format(curDate);
            pidf.setTimeStamp(dateTime);
            pidf.addXmlns("urn:ietf:params:xml:ns:pidf");
            PIDFXmlBuilder pidfXmlBuilder = new PIDFXmlBuilder(pidf, "UTF-8");
            pidfXmlBuilder.setDebugMode(true);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(pidfXmlBuilder.build().toByteArray());
            byte[] xmlRaw = this.inputStreamToBytes(inputStream);
            Request request = messageFactory.createRequest((URI)requestUri, "PUBLISH", callIdHeader, cSeqHeader, fromHeader, toHeader, (List)viaList, maxForwardsHeader, contentTypeHeader, xmlRaw);
            ContactHeader contactHeader = headerFactory.createContactHeader(localAddress);
            request.setHeader(contactHeader);
            EventHeader eventHeader = headerFactory.createEventHeader("presence");
            request.setHeader(eventHeader);
            ExpiresHeader expiresHeader = headerFactory.createExpiresHeader(expires);
            request.setHeader(expiresHeader);
            if (eTag != null) {
                SIPIfMatchHeader sipIfMatchHeader = headerFactory.createSIPIfMatchHeader(eTag);
                request.setHeader(sipIfMatchHeader);
            }
            ClientTransaction clientTransaction = sipProvider.getNewClientTransaction(request);
            clientTransaction.sendRequest();
        }
        catch (ParseException e2) {
            e2.printStackTrace();
        }
        catch (InvalidArgumentException e3) {
            e3.printStackTrace();
        }
        catch (IOException e4) {
            e4.printStackTrace();
        }
        catch (SAXException e5) {
            e5.printStackTrace();
        }
        catch (TransactionUnavailableException e6) {
            e6.printStackTrace();
        }
        catch (SipException e7) {
            e7.printStackTrace();
        }
    }
    
    public void leave() {
        try {
            if (sipProvider != null && sipStack != null && sipFactory != null && EMRProvider != null) {
                sipStack.deleteListeningPoint(tcpListeningPoint);
                sipStack.deleteListeningPoint(udpListeningPoint);
                sipProvider.removeSipListener(this);
                EMRProvider.removeSipListener(this);
                sipStack.deleteSipProvider(sipProvider);
                sipStack.deleteSipProvider(EMRProvider);
                sipFactory.resetFactory();
                sipStack = null;
                sipProvider = null;
                EMRProvider = null;
//                instance = null;
                this.inviteTransaction = null;
                headerFactory = null;
                addressFactory = null;
                messageFactory = null;
                tcpListeningPoint = null;
                udpListeningPoint = null;
                System.out.println("[SipClient][leave()] clear");
            }
        }
        catch (ObjectInUseException e2) {
            e2.printStackTrace();
        }
    }

    public void sendRegister(int expires) {
        if (sipStack == null) {
            return;
        }
        AddressFactory addressFactory = this.addressFactory;
        SipProvider sipProvider = this.sipProvider;
        HeaderFactory headerFactory = this.headerFactory;
        try {
            Request request = this.createRegisterRequest();
            SIPRequest sipRequest = (SIPRequest)request;
            Address contactAddress = addressFactory.createAddress("sip:" + sipUserName + "@" + registerContactAddress + ";transport=tcp");
            request.addHeader(headerFactory.createContactHeader(contactAddress));
            this.addRequireHeader(sipRequest, "pref");
            ArrayList<String> uAgentList = new ArrayList<String>();
            uAgentList.add("IWF-Client");
            uAgentList.add("/3GPP");
            this.addUserAgentHeader(sipRequest, uAgentList);
            ExpiresHeader eh = headerFactory.createExpiresHeader(expires);
            request.addHeader(eh);
            ClientTransaction transaction = sipProvider.getNewClientTransaction(request);
            transaction.sendRequest();
            if (expires == 0) {
                this.registerCount = 0;
            }
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
    }
    
    private Request createRegisterRequest() throws ParseException, InvalidArgumentException {
        CallIdHeader callIdHeader;
        Address fromAddress = addressFactory.createAddress("sip:" + sipUserName + "@" + RemoteIp);
        FromHeader fromHeader = headerFactory.createFromHeader(fromAddress, myTag);
        Address toAddress = addressFactory.createAddress("sip:" + sipUserName + "@" + RemoteIp);
        ToHeader toHeader = headerFactory.createToHeader(toAddress, null);
        ArrayList<ViaHeader> viaHeaders = this.createViaHeader();
        URI requestURI = addressFactory.createAddress("sip:" + RemoteEndpoint).getURI();
        if (registerCallId == null) {
            callIdHeader = sipProvider.getNewCallId();
            registerCallId = callIdHeader.getCallId();
        } else {
            callIdHeader = headerFactory.createCallIdHeader(registerCallId);
        }
        CSeqHeader cSeqHeader = headerFactory.createCSeqHeader((long)(++registerCSeq), "REGISTER");
        MaxForwardsHeader maxForwardsHeader = headerFactory.createMaxForwardsHeader(70);
        return messageFactory.createRequest(requestURI, "REGISTER", callIdHeader, cSeqHeader, fromHeader, toHeader, viaHeaders, maxForwardsHeader);
    }
    
    public void sendMESSAGE(String targetname, String content, Boolean sendToGroup, String conversationid) throws ParseException, InvalidArgumentException, SipException {
	    AddressFactory addressFactory = this.addressFactory;
	    SipProvider sipProvider = this.sipProvider;
	    MessageFactory messageFactory = this.messageFactory;
	    HeaderFactory headerFactory = this.headerFactory;
	    
	    String siptargetname ="";
	    if (targetname.contains("@")) {
	    	siptargetname = "sip:" + targetname;
	    } else {
	    	siptargetname = "sip:" + targetname + "@" + RemoteIp;
	    }
    
	    CallIdHeader callIdheader = sipProvider.getNewCallId();
	    messageCSeq++;
	    CSeqHeader cSeqHeader = headerFactory.createCSeqHeader(messageCSeq, "MESSAGE");
	    Address fromAddress = addressFactory.createAddress("sip:" + sipUserName + "@" + RemoteIp);
	    String tag = Utils.getInstance().generateTag();
	    FromHeader fromHeader = headerFactory.createFromHeader(fromAddress, tag);
	    String sipServer="";
	    if (RemoteIp.contains(":")) {
	        // 如果 RemoteIp 包含端口號，提取出 IP 地址部分
	        sipServer = RemoteIp.split(":")[0];
	    } else {
	        sipServer = RemoteIp;
	    }	
	    Address toAddress = addressFactory.createAddress(siptargetname);
	    ToHeader toHeader = headerFactory.createToHeader(toAddress, null);
	    URI publishURI = addressFactory.createAddress(siptargetname).getURI();
//	    if(!sendToGroup) {
//	    	toHeader = headerFactory.createToHeader(addressFactory.createAddress("sip:"+receiver+"@" + RemoteIp), null);
//	    	publishURI = addressFactory.createAddress("sip:" + receiver + "@" + RemoteIp).getURI();
//	    }
	    ArrayList<ViaHeader> viaList = createMessageViaHeader();
	    MaxForwardsHeader maxForwardsHeader = headerFactory.createMaxForwardsHeader(70);
	    Request request = messageFactory.createRequest(publishURI, "MESSAGE", callIdheader, cSeqHeader, 
	        fromHeader, toHeader, viaList, maxForwardsHeader);
	    
	    Address localAddress = addressFactory.createAddress("sip:" + sipUserName + "@" + localIp + ":" + localPort + ";transport=tcp");
	    ContactHeader contactHeader = headerFactory.createContactHeader(localAddress);
//	    contactHeader.setParameter("isfocus", null);
	    request.setHeader((Header)contactHeader);
	    SIPRequest sipRequest = (SIPRequest)request;
	    
	    String acceptContactValue = "*;g.3gpp.mcdata.sds;require;explicit";
        Header acceptContactHeader = headerFactory.createHeader("Accept-Contact", acceptContactValue);
        String acceptContactValue2 = "*;g.3gpp.icsi-ref=\"urn:urn-7:3gpp-service.ims.icsi.mcdata.sds\";require;explicit"; // 替换为实际需要的值
        Header acceptContactHeader2 = headerFactory.createHeader("Accept-Contact", acceptContactValue2);
        request.addHeader(acceptContactHeader);
        request.addHeader(acceptContactHeader2);
	    

	    sipRequest.setHeader(headerFactory.createHeader("P-Preferred-Service", "urn:urn-7:3gpp-service.ims.icsi.mcdata.sds"));
	    List<Content> contents = new ArrayList<>();
	    if(!sendToGroup) {
	    	Content resourceContent = MessageDataresourcecontent(siptargetname);
	    	contents.add(resourceContent);
	    }
	    Content mcdatainfoContent = MessageMcdatainfocContent(targetname, sendToGroup, "sds");
	    Content signallingContent = MessageDatasignallingcontent(conversationid);
	    Content DataPayloadContent = MessageDataPayloadcontent(content, "text", true);//?text
    
	    contents.add(mcdatainfoContent);//
	    contents.add(signallingContent);
	    contents.add(DataPayloadContent);
	    setMutiPartContents((Request)sipRequest, contents);
	 // 印出 SIP Request 內容（原始字串）
	    System.out.println("====== SIP REQUEST START ======");
	    System.out.println(sipRequest.toString()); // 或 sipRequest.encode()
	    System.out.println("====== SIP REQUEST END ========");

	    SIPClientTransaction transaction = (SIPClientTransaction)sipProvider.getNewClientTransaction((Request)sipRequest);
	    transaction.sendRequest();
	    	    	    	 
    }
    
    private ArrayList<ViaHeader> createMessageViaHeader() {
	    ArrayList<ViaHeader> viaHeaders = new ArrayList<>();
	    try {
	      ViaHeader myViaHeader = headerFactory.createViaHeader(registerContactAddress.split(":")[0], 
	          localPort, transport, SipMessageUtils.generateNewBranchCode());
	      myViaHeader.setRPort();
	      viaHeaders.add(myViaHeader);
	    } catch (ParseException e) {
	      e.printStackTrace();
	    } catch (InvalidArgumentException e) {
	      e.printStackTrace();
	    } 
	    return viaHeaders;
	  }
    
    private Content MessageMcdatainfocContent(String GroupURI, Boolean sendToGroup) {
	    ArrayList<String> group_list = new ArrayList<>();
	    group_list.add(GroupURI);
	    try_mcdatainfo_factory pidf = new try_mcdatainfo_factory();
	    String pidfxml="";
	    if(sendToGroup) {
	    	pidfxml = pidf.string_try_mcdatainfo_factory("Normal","",GroupURI, String.valueOf(sipUserName) + "@" + RemoteIp);
	    }else {
	    	pidfxml = pidf.string_try_mcdatainfo_factory("Normal",String.valueOf(sipUserName) + "@" + RemoteIp,"sds");
	    }
	    try {
	    	return createContent(pidfxml, "application", "vnd.3gpp.mcdata-info+xml", null);
	    } catch (ParseException e) {
	    	e.printStackTrace();
	      	return null;
	    } 
	}
    
    private Content MessageMcdatainfocContent(String GroupURI, Boolean sendToGroup, String mcdatatype) {
	    try_mcdatainfo_factory pidf = new try_mcdatainfo_factory();
	    String pidfxml="";
	    if(sendToGroup) {
	    	pidfxml = pidf.string_try_mcdatainfo_factory("Normal",GroupURI, String.valueOf(sipUserName) + "@" + RemoteIp, mcdatatype);
	    }else {
	    	pidfxml = pidf.string_try_mcdatainfo_factory("Normal",String.valueOf(sipUserName) + "@" + RemoteIp, mcdatatype);
	    }
	    try {
	        return createContent(pidfxml, "application", "vnd.3gpp.mcdata-info+xml", null);
	    } catch (ParseException e) {
	        e.printStackTrace();
	        return null;
	    } 
	}
    

    private Content MessageDataresourcecontent(String receiver) {
  	  	try_mcdataresource_factory mcdatainfo = new try_mcdataresource_factory();
  		String mcpdatainfoxml = mcdatainfo.string_try_mcdataresource_factory("Normal", String.valueOf(receiver));
	    try {
	    	return createContent(mcpdatainfoxml, "application", "resource-lists+xml", null);
	    } catch (ParseException e) {
	    	e.printStackTrace();
	    	return null;
	    } 
    }
    
    private Content MessageDatasignallingcontent(String conversationid) {
  	  	try_mcdatasignalling_factory mcdatasignalling = new try_mcdatasignalling_factory();
  	    String mcpdatasignallingxml = mcdatasignalling.string_try_mcdatasignalling_factory("Normal", String.valueOf(sipUserName) + "@" + RemoteIp, conversationid);
  	    try {
  	    	return createContent(mcpdatasignallingxml, "application", "vnd.3gpp.mcdata-signalling", null);
  	    } catch (ParseException e) {
  	    	e.printStackTrace();
  	    	return null;//application/vnd.3gpp.mcdata-payload
  	    } 
    }
    
    private Content MessageDataPayloadcontent(String content, String payloadtype, Boolean type) {
  	  	try_mcdatapayload_factory mcdatapayload = new try_mcdatapayload_factory();
  	    String mcpdatapayloadxml = mcdatapayload.string_try_mcdatapaylaod_factory("Normal", String.valueOf(sipUserName) + "@" + RemoteIp, content, payloadtype, type);
  	    System.out.println(mcpdatapayloadxml);
  	    try {
  	    	return createContent(mcpdatapayloadxml, "application", "vnd.3gpp.mcdata-payload", null);
  	    } catch (ParseException e) {
  	    	e.printStackTrace();
  	      	return null;//application/vnd.3gpp.mcdata-payload
  	    } 
  	}
    
    private void setMutiPartContents(Request request, List<Content> contents) throws ParseException {
        ContentType contentType = new ContentType("multipart", "mixed");
        contentType.setParameter("boundary", "boundary1");
        MultipartMimeContentImpl multipartMimeContent = new MultipartMimeContentImpl(contentType);
        for (Content c2 : contents) {
            if (c2 == null) continue;
            multipartMimeContent.addContent(c2);
        }
        request.setContent(multipartMimeContent, contentType);
    }
    
    private Content createContent(String contentStr, String cType, String cSubType, String contentDisPosition) throws ParseException {
        ContentTypeHeader contentType = headerFactory.createContentTypeHeader(cType, cSubType);
        ContentImpl content = new ContentImpl(contentStr);
        content.setContentTypeHeader(contentType);
        if (contentDisPosition != null) {
            ContentDispositionHeader contentDispositionHeader = headerFactory.createContentDispositionHeader(contentDisPosition);
            content.setContentDispositionHeader(contentDispositionHeader);
        }
        return content;
    }
    
    private void onRegisterOK(ResponseObject responseObj) {
        System.gc();
        this.ErrorAccount = 0;
        System.out.println("[SipClient][getisRegister()]"+this.getisRegister());
        if (this.getisRegister()) {
            ++this.registerCount;
            if (this.registerCount == 1) {
                sipPublishSender.start();
                System.out.println("[SipClient][onRegisterOK()]");
            }
            this.setisRegister(true);
//            IWFUI.Log_Out.setVisible(true);
        } else {
            this.setisRegister(false);
        }
    }

    private void onMessageRequest(RequestObject requestObj, SIPServerTransactionImpl sipServerTransaction) throws ParseException, InvalidArgumentException, SipException, URISyntaxException {
    	try {

            System.out.println("onMessageRequest" );
    		SIPRequest sIPRequest = requestObj.getSipRequest();
    		
    		Header preferredServiceHeader = sIPRequest.getHeader("P-Preferred-Service");
    		String extractedValue = "";
            // 检查是否存在该头部
            if (preferredServiceHeader != null) {
                String preferredServiceValue = preferredServiceHeader.toString();
                System.out.println("P-Preferred-Service: " + preferredServiceValue);
                Pattern pattern = Pattern.compile("mcdata\\.(\\S+)");
                Matcher matcher = pattern.matcher(preferredServiceValue);

                if (matcher.find()) {
                    extractedValue = matcher.group(1);
                    System.out.println("Extracted value: " + extractedValue);
                } else {
                    System.out.println("No match found.");
                }
            } else {
            	String xmlString = requestObj.getContentString();
        	  	try_parser get1 = new try_parser();
        	  	String sdp = get1.string_try_mcdata_sdp(xmlString);
        	  	System.out.println("[sdp]"+sdp);
                System.out.println("P-Preferred-Service header not found.");
//    	                System.out.println("[session]"+sessionMap.size());
//    	        	  	System.out.println("[session]"+sessionMap.get("1"));
//    	                Session activesession = sessionMap.get("1");
//    	                
//    	                
//    	                
//    	                ArrayList<java.net.URI> toList = new ArrayList<java.net.URI>();
//    	                java.net.URI sdpUri = new java.net.URI(sdp);
//    	    	    	toList.add(sdpUri);
//    	    	    	System.out.println("[addlist]");
//    	    	    	try {
//    	    	    		activesession.setToPath(toList);
//    	    			} catch (Exception e) {
//    	    				e.printStackTrace();
//    	    			}
//    	    	    	System.out.println("[sendmessage begin]");
//    	    	    	String message = "test message";
//    	    	    	Message sendMsg = activeSession.sendMessage("text/plain", (message).getBytes());
//    	    	    	System.out.println("[sendmessage finish]");
            }
    		
            if(extractedValue.equals("sds")) {
	    		String eventType = getMessageEventType(requestObj);
	    		String xmlString = requestObj.getContentString();
	    		try_parser get1 = new try_parser();
	    		String emergencyind1 = get1.string_try_mcdata_payload(xmlString);
	    		String emergencyind2 = get1.string_try_mcdata_payload_content_type(xmlString);
	    		String emergencyind3 = get1.string_try_mcdata_signalling_senderid(xmlString);
	    		String emergencyind4 = get1.string_try_mcdata_signalling_conversationid(xmlString);
	    		String emergencyind5 = get1.string_try_mcdata_info_requesttype(xmlString);
	    		String emergencyind6 = "";
	    		Boolean  grouporone=false;
	    		if(emergencyind5.equals("one-to-one-sds")) {
	//    	  		emergencyind6 = get1.string_try_mcdata_info_senderid(xmlString);
	    			System.out.println("grouporone");
	    		}else {
	    			emergencyind6 = get1.string_try_mcdata_info_groupuri(xmlString);
	    			grouporone = true;
	    		}
	    		System.out.println("response"+grouporone);
	//      System.out.println("66"+emergencyind6);
	    		String[] parts = emergencyind1.split(",");
	    		String contenttype="";
	    		switch(emergencyind2) {
	    			case "00000001":
			      		contenttype="TEXT";
			      		break;
			      	case "00000010":
			      		contenttype="BINARY";
			      		break;
			      	case "00000011":
			      		contenttype="HYPERLINKS";
			      		break;
			      	case "00000100":
			      		contenttype="FILEURL";
			      		break;
		        }
//    		    		router.sendMessage(sipUserName,"sds", parts[1], grouporone);
	    		if(emergencyind5.equals("one-to-one-sds")) {
	    			router.sendMessage(sipUserName,"sds", parts[1], grouporone);
	    		}else {
	    			System.out.println("emergencyind6"+emergencyind6);
	    			router.sendMessage(emergencyind6,"sds", parts[1], grouporone);
	    		}
	    		String[] emergencyData = {parts[0], parts[1], contenttype, emergencyind3,emergencyind4,emergencyind5,emergencyind6};
//    		    		support.firePropertyChange("MCDatasipMessage", null, emergencyData);
//    		    		sendHttpsPostRequest(parts[1]);
		        Response response = null;
		        try {
		        	response = messageFactory.createResponse(200, (Request)sIPRequest);
		        	System.out.println("response"+response);
		        	sipServerTransaction.sendResponse(response);
		        } catch (ParseException e) {
		        	e.printStackTrace();
		        } catch (SipException e) {
		        	e.printStackTrace();
		        } 
		        System.out.println("[message]: 1");
		        
//    	            }else {
//    	            	
//    	            	sendOKFromRequest((Request)requestObj.getSipRequest(), sipServerTransaction);
//    	            	String xmlString = requestObj.getContentString();
//    		    		try_parser get1 = new try_parser();
//    		    		if(get1.string_try_mcdata_notification(xmlString)=="false") {
//    			    		String emergencyind = get1.string_try_mcdata_signalling_mandatory(xmlString);
//    			    		if(emergencyind=="true") {
//    			    			String content =get1.string_try_mcdata_payload_fd_payload_content(xmlString);
//    			    			String[] parts = content.split(", ", 2);
//    			    			String path = parts.length > 1 ? parts[1] : "";
//    			    			String callinguser = get1.string_try_mcdata_info_callinguser(xmlString);
//    			    			// 取得 "FDfile/" 之後的值
//    			    			String[] pathParts = path.split("FDfile/", 2);
//    			    			String fileName = pathParts.length > 1 ? pathParts[1] : "";
//    			    			String conversationid = get1.string_try_mcdata_signalling_conversationid(xmlString);
//    			    			String emergencyind5 = get1.string_try_mcdata_signalling_applicationid(xmlString);
//    			    			String useruri = get1.string_try_mcdata_payload_fd_useruri(xmlString);
//    			    			if(emergencyind5=="true") {
//    			    				//todo
//    				    		}else {
//    		//		    			String emergencyind6 = get1.string_try_mcdata_signalling_payloadcontent(xmlString);
//    				    			//notify usere autodownload
//    				    		}
//    		//	    			generate an FD NOTIFICATION indicating
//    		//	    			shall attempt to download the file
//    			    			String emergencyind6 = get1.string_try_mcdata_info_requesttype(xmlString);
//    			    			String emergencyind1 = get1.string_try_mcdata_payload(xmlString);
//    			    			String[] emergencyData = {emergencyind1, emergencyind6, "mandatory download", content, useruri};
//    			    			support.firePropertyChange("MCDatasipMessageFD", null, emergencyData);
//    			    			String groupuri = null;
//    			    			if(emergencyind6.equals("group")){
//    			    				groupuri=get1.string_try_mcdata_info_groupuri(xmlString);
//    			    			}
//    			    			startDownload(path, fileName, groupuri, conversationid,callinguser);
//    			    		}else {
//    			    			String content =get1.string_try_mcdata_payload_fd_payload_content(xmlString);
//    		//	    			System.out.println("[test1]"+content);
//    			    			String[] parts = content.split(", ", 2);
//    			    			String path = parts.length > 1 ? parts[1] : "";
//    		
//    			    			// 取得 "FDfile/" 之後的值
//    			    			String[] pathParts = path.split("FDfile/", 2);
//    			    			String fileName = pathParts.length > 1 ? pathParts[1] : "";
//    		
//    			    			System.out.println("完整路徑: " + path);
//    			    			System.out.println("文件名稱: " + fileName);
//    			    			String useruri = get1.string_try_mcdata_payload_fd_useruri(xmlString);
//    		//	    			System.out.println("[test2]"+useruri);
//    			    			String conversationid = get1.string_try_mcdata_signalling_conversationid(xmlString);
//    			    			String apporuser = get1.string_try_mcdata_signalling_applicationid(xmlString);
//    			    			if(apporuser=="true") {
//    			    				//todo
//    				    		}else {		    			
//    				    			String emergencyind6 = get1.string_try_mcdata_signalling_payloadcontent(xmlString);//detect for user or not	    			
//    				    		}
//    		
//    			    			String emergencyind6 = get1.string_try_mcdata_info_requesttype(xmlString);//null
//    			    			String callinguser = get1.string_try_mcdata_info_callinguser(xmlString);
//    			    			String emergencyind1 = get1.string_try_mcdata_payload(xmlString);
//    			    			System.out.println("[emergencyind1]"+emergencyind1);
//    			    			String[] emergencyData = {emergencyind1, emergencyind6, "not mandatory download", content, useruri};
//    			    			support.firePropertyChange("MCDatasipMessageFD", null, emergencyData);
//    			    			String groupuri = null;
//    			    			if(emergencyind6.equals("group")){
//    			    				groupuri=get1.string_try_mcdata_info_groupuri(xmlString);//
//    			    			}
//    			    			
//    			    			//start  timer
//    			
//    			            	clientUI.showDownloadDialog(fileName, instance);
//    			            	System.out.println("[start download]");
//    			            	System.out.println("[downloadDecision2]"+instance.downloadDecision);
//    			            	DownloadDecision  down = getDownloadDecision();
//    			            	System.out.println("[start download]"+down);
//    			            	switch (down) {
//    			            		
//    				                case ACCEPTED:
//    				                    // 用户接受请求，执行下载
//    				                	System.out.println("[getDownloadDecision] ACCEPTED");
//    				                    startDownload(path, fileName, groupuri, conversationid,callinguser);
//    				                    break;
//    				                case REJECTED:
//    				                    // 用户拒绝请求，取消操作
//    				                	System.out.println("[getDownloadDecision] REJECTED");
//    				                    cancelDownload(groupuri, conversationid,callinguser);
//    				                    break;
//    				                case DEFERRED:
//    				                    // 用户选择稍后下载，可能存储状态或者提醒用户
//    				                	System.out.println("[getDownloadDecision] DEFERRED");
//    				                    deferDownload(groupuri, conversationid,callinguser);
//    				                    break;
//    				                default:
//    				                    throw new IllegalStateException("Unexpected value: " + down);
//    				            }
//    		//	            	HTTP_fd_download(path,fileName);
//    		//	    			sendmessage_notification("mcdataserver", groupuri, conversationid);
//    			    		}
//    			    		
//    		            }else {
//    		    			String notificationtype = get1.string_try_mcdata_notification(xmlString);
//    		    			String senderuser = get1.string_try_mcdata_sender(xmlString);
//    		    			System.out.println("[senderuser] "+senderuser);
//    		    			if(notificationtype.equals("00000001")) {
//    		    				System.out.println("[getDownloadDecision] ACCEPTED"+senderuser);
//    		    				String[] emergencyData = {"ACCEPTED",senderuser};
//    		    				support.firePropertyChange("MCDatasipMessageFDnotification", null, emergencyData);
////    		    				accepted
//    		    			}else if(notificationtype.equals("00000010")) {
//    		    				System.out.println("[getDownloadDecision] REJECTED"+senderuser);
//
//    		    				String[] emergencyData = {"REJECTED",senderuser};
//    		    				support.firePropertyChange("MCDatasipMessageFDnotification", null, emergencyData);
////    		    				rejected
//    		    			}else if(notificationtype.equals("00000011")) {
//    		    				System.out.println("[getDownloadDecision] complete"+senderuser);
//
//    		    				String[] emergencyData = {"complete",senderuser};
//    		    				support.firePropertyChange("MCDatasipMessageFDnotification", null, emergencyData);
////    		    				completed
//    		    			}else if(notificationtype.equals("00000100")) {
//    		    				System.out.println("[getDownloadDecision] DEFERRED"+senderuser);
//    		    				String[] emergencyData = {"DEFERRED",senderuser};
//    		    				support.firePropertyChange("MCDatasipMessageFDnotification", null, emergencyData);
////    		    				deffered
//    		    			}
//    		    		}
    		}
	    } catch (ParseException e) {
	        e.printStackTrace();
	    } 
    }
    
    private void onInviteOK(ResponseObject responseObj, ResponseEvent responseEvent) {
    	String domain = responseObj.getCallIdString();
    	domain = domain.substring(domain.indexOf("@") + 1, domain.length());
    	try {
    		if (responseObj.getEventHeader() != null) {
    			String EventHeader = responseObj.getEventHeader();
    			EventHeader = EventHeader.replaceAll("\\s+", "");
    			if (EventHeader.equals("MEGC")) {
    				MediaSipSession mediaSipSession = mediaSipSessionMap.get(responseObj.getCallIdString());
    				if (mediaSipSession == null)
    					return; 
    				SIPDialog sipDialog = (SIPDialog)mediaSipSession.getDialog();
    				sendACK(responseObj, (Dialog)sipDialog);
    			} 
    		} else {
    			MySdpInfo mySdpInfo = getSDPInfo(new String(responseObj.getRawContent()));//將Server端rtcp存到mysdpinfo中
//      		Map<String, MediaSipSession> sessionMap = mediaSipSessionMap;
    			MediaSipSession mediaSipSession = mediaSipSessionMap.get(responseObj.getCallIdString());
    			System.out.println("[oninviteok]"+responseObj.getCallIdString()+" session"+mediaSipSession);
    			if (mediaSipSession == null)
    				return; 
    			if (mediaSipSession.isActive())
    				return; 
    			String GroupURI = mediaSipSession.getPeerInfo().getPeerSipUri().toString().split("sip:")[1];
//      		this.GroupName = GroupURI.split("@")[0];
    			SIPDialog sipDialog = (SIPDialog)responseEvent.getDialog();
    			sendACK(responseObj, (Dialog)sipDialog);//單純sendack
    			setPeerInfoFromResponse(responseObj, mediaSipSession);
    			activeMediaSipSession(mediaSipSession);
    			String GroupName = getTarget(MediaSipSession.getSipTarget()).split("@")[0];
    			String ServerIP = getTarget(MediaSipSession.getSipTarget()).split("@")[1];
    			sendReferPRG(GroupName, ServerIP);
    		} 
    	} catch (Exception e) {
    		e.printStackTrace();
    	} 
    }

    private void sendACK(ResponseObject responseObject, Dialog dialog) {
        try {
            SIPRequest ack = (SIPRequest)dialog.createAck(responseObject.getCSeq().getSeqNumber());
            ack.setRequestURI(SipMessageUtils.createSipUri(sipUserName, RemoteIp, RemotePort, null));
            dialog.sendAck(ack);
        }
        catch (InvalidArgumentException e2) {
            e2.printStackTrace();
        }
        catch (SipException e3) {
            e3.printStackTrace();
        }
    }
    
    private MySdpInfo getSDPInfo(String sessionDescription) throws Exception {
        String[] lines = sessionDescription.trim().replaceAll(" +", " ").split("[\r\n]+");
        String mediaType = "";
        String peerIP = "n/a";
        int peerPort = -1;
        int peerRtcpPort = -1;
        String[] stringArray = lines;
        int n2 = lines.length;
        int n3 = 0;
        while (n3 < n2) {
            String lineRtcp;
            List<String> matched;
            String line = stringArray[n3];
            if (line.charAt(0) == 'c') {
                String linec = line;
                matched = this.getMatched(linec, "IP[0-9]* [0-9.]*");
                if (matched.size() != 1) throw new Exception("content/IP error!!");
                peerIP = matched.get(0).replace("IP4", "").replace("IP6", "").trim();
            } else if (line.charAt(0) == 'm') {
                String linem = line;
                matched = this.getMatched(linem, "(m=audio [0-9]* RTP/AVP)|(m=video [0-9]* RTP/AVP)");
                if (matched.size() == 1) {
                    mediaType = this.getMatched(matched.get(0), "audio|video").get(0);
                    peerPort = Integer.parseInt(matched.get(0).replace("m=audio", "").replace("m=video", "").replace("RTP/AVP", "").trim());
                }
            } else if (line.charAt(0) == 'a' && (matched = this.getMatched(lineRtcp = line, "a=rtcp:[0-9]*")).size() == 1) {
                peerRtcpPort = Integer.parseInt(matched.get(0).replace("a=rtcp:", "").trim());
            }
            ++n3;
        }
        MySdpInfo mySdpInfo = new MySdpInfo();
        mySdpInfo.setIp(peerIP);
        mySdpInfo.setPort(peerPort);
        mySdpInfo.setRtcpPort(peerRtcpPort);
        mySdpInfo.setMediaType(mediaType);
        return mySdpInfo;
    }
    
    private List<String> getMatched(String s, String regex1) {
        ArrayList<String> matchList = new ArrayList<String>();
        Pattern pattern1 = Pattern.compile(regex1);
        Matcher matcher = pattern1.matcher(s);
        while (matcher.find()) {
            matchList.add(matcher.group());
        }
        return matchList;
    }
    
    private void setPeerInfoFromResponse(ResponseObject responseObj, MediaSipSession mediaSipSession) throws Exception {//設定接收到的SDP
        MySdpInfo mySdpInfo = this.getSDPInfo(new String(responseObj.getRawContent()));
        PeerInfo peerInfo = mediaSipSession.getPeerInfo();//MeaninglessSender使用
        peerInfo.setSDPInfo(mySdpInfo);
        peerInfo.setPeerUserName(responseObj.getToHeaderUserName());
        peerInfo.setPeerTag(responseObj.getToTag());
    }

    private void activeMediaSipSession(MediaSipSession mediaSipSession) {
        try {
            PeerInfo peerInfo = mediaSipSession.getPeerInfo();
            if (!peerInfo.getPeerIp().equals("n/a") && peerInfo.getPeerRtpPort() != -1) {
                String peerIp = peerInfo.getPeerIp();
                int peerPort = peerInfo.getPeerRtcpPort();
                mediaSipSession.getMediaSessionState().setRtpport(this.getRtpPort());
                mediaSipSession.getMediaSessionState().setrtpSocket(proxy.getRTCPProxy().getRTCPThread().getHandler().getRtpSocket());
//                mediaSipSession.getMediaSessionState().changeState(2, "", this.EMR_parameters.MCPTTUserlocalIp);//啟動RTP
                mediaSipSession.setIsActive(true);//啟動RTcP
            }
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
    }
    
//    public void sendText(String text) {
//        RadioMessage msg = new RadioMessage();
//        msg.setFrom(config.getUserId());
//        msg.setType("text");
//        msg.setContent(text);
//
////        router.sendMessage(config.getUserId(), msg);
//    }
    
    public void sendReferPRG(String groupName, String sipServer) throws ParseException, InvalidArgumentException, SipException, ParserConfigurationException, TransformerException {
        AddressFactory addressFactory = this.addressFactory;
        SipProvider sipProvider = this.sipProvider;
        MessageFactory messageFactory = this.messageFactory;
        HeaderFactory headerFactory = this.headerFactory;
        String groupuri = String.valueOf(groupName) + "@" + sipServer;
        String temp = "sip:" + groupuri;
        MediaSipSession mediaSipSession = null;
        for (String key : mediaSipSessionMap.keySet()) {
            MediaSipSession session = mediaSipSessionMap.get(key);
            if (!session.getPeerInfo().getPeerSipUri().toString().equals(temp)) continue;
            mediaSipSession = session;
        }
        SIPDialog sipDialog = (SIPDialog)mediaSipSession.getDialog();
        SIPRequest sipRequest = (SIPRequest)sipDialog.createRequest("REFER");
        sipRequest.getRequestLine().setUri(mediaSipSession.getPeerInfo().getPeerSipUri());
        Address localAddress = this.createContactAddress();
        ContactHeader contactHeader = headerFactory.createContactHeader(localAddress);
        contactHeader.setParameter("isfocus", null);
        sipRequest.setHeader(contactHeader);
        String referToTarget = MediaSipSession.getSipTarget();
        Address referToAddr = addressFactory.createAddress(referToTarget);
        ReferTo referTo = (ReferTo)headerFactory.createReferToHeader(referToAddr);
        referTo.setParameter("session", "prearranged");
        sipRequest.setHeader(referTo);
        SipURI userURI = addressFactory.createSipURI(sipUserName, localIp);
        Address userURIaddr = addressFactory.createAddress(userURI);
        HeaderFactoryImpl headerfactoryImpl = new HeaderFactoryImpl();
        PPreferredIdentityHeader PPI = headerfactoryImpl.createPPreferredIdentityHeader(userURIaddr);
        sipRequest.setHeader(PPI);
        sipRequest.setHeader(headerFactory.createHeader("P-Preferred-Service", "urn:urn-7:3gpp-service.ims.icsi.mcptt"));
        Content mcpttinfoContent = this.ReferMcpttinfocContent(groupName);
        Content sdpContent = this.createReferSDPContent(sessinID, this.socket, null);
        ArrayList<Content> contents = new ArrayList<Content>();
        contents.add(mcpttinfoContent);
        contents.add(sdpContent);
        this.setMutiPartContents(sipRequest, contents);
        sipRequest.getTopmostViaHeader().setRPort();
        SIPClientTransaction transaction = (SIPClientTransaction)sipProvider.getNewClientTransaction(sipRequest);
        mediaSipSession.getDialog().sendRequest(transaction);
        System.out.println("[refer]");
//        if (!this.getPESessionE()) {//應該不會走這
        if(false) {
        	System.out.println("[refer]2");
            proxy.getRTCPProxy().getRTCPThread().getHandler().setSession(mediaSipSession);
            proxy.start();
//            instance = this;
//            proxy.getRTCPProxy().getRTCPThread().getHandler().setTwoModeHandler(1, mySdpInfo.getIp(), mySdpInfo.getPort(), this.EMR_parameters.MCPTTUserlocalIp, this.getRtpPort(), this.EMR_parameters.LMRLocalIp, 12001, EMRSdpInfo.getIp(), EMRSdpInfo.getPort());
            System.out.println("[refer]22");
        } else {
        	System.out.println("[refer]3");
//            proxy.getRTCPProxy().getRTCPThread().getHandler().setAllParm(1024, mySdpInfo.getIp(), mySdpInfo.getPort(), this.EMR_parameters.MCPTTUserlocalIp, this.getRtpPort(), this.EMR_parameters.LMRLocalIp, 12001, "255.255.255.255", 30000);
//            proxy.getRTCPProxy().getRTCPThread().getHandler().setSession(mediaSipSession);
            proxy.start();
            System.out.println("[refer]33");
        }
    }

    private Content ReferMcpttinfocContent(String groupName) throws ParserConfigurationException, TransformerException {
        String clientURI = String.valueOf(sipUserName) + "@" + RemoteIp;
        String groupURI = String.valueOf(groupName) + "@" + RemoteIp;
        try_mcpttinfo_factory mcpttinfo2 = new try_mcpttinfo_factory();
        String mcpttinfoxml = mcpttinfo2.string_try_mcpttinfo_factory("Normal", clientURI, groupURI);
        try {
            return this.createContent(mcpttinfoxml, "application", "vnd.3gpp.mcptt-info+xml", null);
        }
        catch (ParseException e2) {
            e2.printStackTrace();
            return null;
        }
    }
    
    private Content createReferSDPContent(long sessionId, DatagramSocket socket, MediaSipSession mediaSipSession) throws ParseException {
        SimpleSessionDescription offerSDP = null;
        offerSDP = this.createOffer(sessionId, this.pRtpPort, this.pRtcpPort, this.pIP);
        if (offerSDP != null) {
        	System.out.println("[offerSDP]"+offerSDP);
            return this.createContent(offerSDP.encode(), "application", "sdp", null);
        }
        return null;
    }
    
    private SimpleSessionDescription createOffer(long sessionId, int streamLocalPort, int rtcpPort, String localIPAddr) {
        SimpleSessionDescription offer = new SimpleSessionDescription("IWF", sessionId, localIPAddr);
        SimpleSessionDescription.Media media = null;
        media = offer.newMedia("audio", streamLocalPort, 1, "RTP/AVP");
        media.setRtpPayload(0, "PCMU/8000", null);
        media.setRtpPayload(8, "PCMA/8000", null);
        SimpleSessionDescription.Media mediaRTCP = offer.newMedia("application", rtcpPort, 1, "udp RTCP");
        media.setRtpPayload(127, "telephone-event/8000", "0-15");
        offer.setAttribute("rtcp", String.valueOf(rtcpPort));
        offer.setAttribute("fmtp", "MCPTT mc_queueing;mc_priority=4;mc_granted;mc_implicit_request");
        offer.setAttribute("inactive", "inactive");
        return offer;
    }
    
    public void onMessage(RadioMessage msg) {
//        session.receiveMessage("From " + msg.getFrom() + ": " + msg.getContent());
    }
    
    private String getMessageEventType(RequestObject requestObj) {
        if (!requestObj.getSipRequest().hasHeader("Event"))
            return null; 
        return ((EventHeader)requestObj.getSipRequest().getHeader("Event")).getEventType().replaceAll("\\s+", "");
    }

    public void processRequest(RequestEvent requestEvent) {
    	System.out.println("processrequest");
        RequestObject requestObj = this.parseRequestObj(requestEvent);
        String domain = requestObj.getCallIdString();
        domain = domain.substring(domain.indexOf("@") + 1, domain.length());
        SIPServerTransactionImpl sipServerTransaction = null;
        System.out.println("processrequest");
        SIPDialog sipDialog = null;
        if (requestObj == null) {
            return;
        }
        String requestMethod = requestObj.getMethod();
        System.out.println("processrequest");
        if (requestMethod.equals("INVITE")) {
            try {
            	sipServerTransaction = getSipServerTransaction(requestEvent);
//    	        onInviteRequest(requestObj, sipServerTransaction, sipDialog);
//                this.onInviteRequest(requestObj, sipServerTransaction, sipDialog);
            }
            catch (Throwable e) {
				e.printStackTrace();
			}
        } else if (requestMethod.equals("BYE")) {
            try {
            	sipServerTransaction = getSipServerTransaction(requestEvent);
//				this.onByeRequest(requestObj, sipServerTransaction);
//            	onByeRequest(requestObj, sipServerTransaction);
            } catch (Throwable e) {
				e.printStackTrace();
			}
        } else if (requestMethod.equals("NOTIFY")) {
            sipServerTransaction = getSipServerTransaction(requestEvent);
//                this.onNotifyRequest(requestObj, sipServerTransaction);
//                onNotifyRequest(requestObj, sipServerTransaction);
        } else if (requestMethod.equals("MESSAGE")) {
            try {
            	sipServerTransaction = getSipServerTransaction(requestEvent);
    	        onMessageRequest(requestObj, sipServerTransaction);
//                this.onMessageRequest(requestObj, sipServerTransaction);
            }
            catch (ParseException e8) {
                e8.printStackTrace();
            } catch (InvalidArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SipException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        } else if (!requestMethod.equals("REGISTER") && !requestMethod.equals("ACK")) {
            if (requestMethod.equals("CANCEL")) {
//                this.onCancel(requestObj, sipServerTransaction);
            } else if (requestMethod.equals("INFO")) {
//                this.onInfo(requestObj, sipServerTransaction);
            }
        }
    }

    @Override
    public void processResponse(ResponseEvent responseEvent) {
        ClientTransaction tid = null;
        tid = responseEvent.getClientTransaction() != null ? responseEvent.getClientTransaction() : null;
        ResponseObject responseObj = ResponseObject.parse(responseEvent.getResponse());
        System.out.println("[processResponse]"+responseObj.getStatusCode());
        if (responseObj.getStatusCode() == 200 || responseObj.getStatusCode() == 202) {
            try {
				this.on2xxResponse(responseObj, responseEvent);
			} catch (UnknownHostException | InternalErrorException | ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        } else if (responseObj.getStatusCode() == 180) {
//            this.onRingingResponse(responseObj);
        } else if (responseObj.getStatusCode() == 100) {
//            this.onTryingResponse(responseObj);
        } else if (responseObj.getStatusCode() == 404) {
//            this.on404Response(responseObj, responseEvent);
        } else if (responseObj.getStatusCode() == 412) {
//            this.on412Response(responseObj, responseEvent);
        } else if (responseObj.getStatusCode() == 401 || responseObj.getStatusCode() == 407) {
            this.on401or407Response(responseObj, tid);
        } else if (responseObj.getStatusCode() == 487) {
//            this.on487Response(responseObj, responseEvent);
        } else if (responseObj.getStatusCode() == 480) {
//            this.on480Response(responseObj, responseEvent);
        } else if (responseObj.getStatusCode() == 403) {
//            this.on403Response(responseObj, responseEvent);
        } else if (responseObj.getStatusCode() == 486) {
//            this.on486Response(responseObj, responseEvent);
        } else if (responseObj.getStatusCode() == 503) {
//            this.on503Response(responseObj, responseEvent);
        } else if (responseObj.getStatusCode() == 481) {
//            this.on481Response(responseObj, responseEvent);
        }
    }

    @Override
    public void processTimeout(TimeoutEvent timeout) {
        Request request = timeout.getClientTransaction().getRequest();
        String requestMethod = request.getMethod();
        if (requestMethod.equals("INVITE")) {
            String[] CallID2 = request.getHeader("Call-ID").toString().split("Call-ID:");
            String callID = CallID2[1].replaceAll("\\s+", "");
//            MediaSipSession mediaSipSession = mediaSipSessionMap.get(callID);
//            MediaSipSession EMRmediaSipSession = mediaSipSessionMap.get(callID);
//            if (mediaSipSession != null) {
//                this.endSession(mediaSipSession);
//            }
//            if (EMRmediaSipSession != null) {
//                this.endEMRSession(EMRmediaSipSession);
//            }
        } else if (!(requestMethod.equals("REGISTER") || requestMethod.equals("BYE") || requestMethod.equals("REFER"))) {
            requestMethod.equals("PUBLISH");
        }
        JOptionPane.showMessageDialog(null, "Timeout: SIP " + requestMethod + " request", "Error: <" + sipUserName + "> ", 0);
    }

	@Override
	public void processIOException(IOExceptionEvent exceptionEvent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processTransactionTerminated(TransactionTerminatedEvent transactionTerminatedEvent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processDialogTerminated(DialogTerminatedEvent dialogTerminatedEvent) {
		// TODO Auto-generated method stub
		
	}
	
	private void on2xxResponse(ResponseObject responseObj, ResponseEvent responseEvent) throws UnknownHostException, InternalErrorException, ParseException {
        String cSeqMethod = responseObj.getCSeq().getMethod();
        if (cSeqMethod.equals("REGISTER")) {
            this.onRegisterOK(responseObj);
        } 
        else if (cSeqMethod.equals("INVITE")) {
            this.onInviteOK(responseObj, responseEvent);
//        } else if (!cSeqMethod.equals("SUBSCRIBE")) {
//            if (cSeqMethod.equals("PUBLISH")) {
//                this.onPublishOK(responseObj);
//            } else if (!cSeqMethod.equals("CANCEL") && !cSeqMethod.equals("MESSAGE")) {
//                if (cSeqMethod.equals("BYE")) {
//                    this.onByeok(responseObj);
//                } else if (!cSeqMethod.equals("INFO")) {
//                    cSeqMethod.equals("REFER");
//                }
//            }
        }
    }
	
	private void on401or407Response(ResponseObject responseObj, ClientTransaction tid) {
        ViaHeader viaHeader = responseObj.getSipResponse().getTopmostViaHeader();
        String viaReceivedRPort = String.valueOf(viaHeader.getReceived()) + ":" + viaHeader.getRPort();
        if (registerContactAddress.equals(viaReceivedRPort)) {
            ++this.ErrorAccount;
            if (this.ErrorAccount >= 3) {
            	System.out.println("45---6");
//                IWFUI.Log_Out.setVisible(false);
                this.setisRegister(false);
//                IWFUI.sipRegisterSender.stopSend();
                JOptionPane.showMessageDialog(null, "MCPTT user name is not correct. Please check the config.txt.", "Error: " + sipUserName, 0);
            } else {
            	System.out.println("456");
                this.doAuthentication(responseObj, tid);
            }
        } else {
            registerContactAddress = viaReceivedRPort;
            localIp = registerContactAddress.split(":")[0];
            localPort = Integer.parseInt(registerContactAddress.split(":")[1]);
            this.sendRegister(SipRegisterSender.getExpires());
        }
    }
	
	private void doAuthentication(ResponseObject responseObj, ClientTransaction tid) {
        AuthenticationHelper authenticationHelper = ((SipStackExt)sipStack).getAuthenticationHelper(accountManager, headerFactory);
        try {
            if (tid != null) {
                ClientTransaction inviteTid = authenticationHelper.handleChallenge(responseObj.getSipResponse(), tid, sipProvider, 0, true);
//                inviteTid.getRequest();
                Request authRequest = inviteTid.getRequest();

                // ✅ 印出要送出的 Request 詳細內容（包含 Authorization header）
                System.out.println("======= AUTHENTICATED REGISTER REQUEST =======");
                System.out.println(authRequest.toString());
                System.out.println("==============================================");
                inviteTid.sendRequest();
            }
        }
        catch (SipException e2) {
            e2.printStackTrace();
        }
    }
	
	private SIPServerTransactionImpl getSipServerTransaction(RequestEvent requestEvent) {
        SIPServerTransactionImpl sipServerTransaction = (SIPServerTransactionImpl)requestEvent.getServerTransaction();
        if (sipServerTransaction == null) {
            try {
                sipServerTransaction = (SIPServerTransactionImpl)sipProvider.getNewServerTransaction(requestEvent.getRequest());
            }
            catch (TransactionAlreadyExistsException e2) {
                e2.printStackTrace();
            }
            catch (TransactionUnavailableException e3) {
                e3.printStackTrace();
            }
        }
        return sipServerTransaction;
    }

	
	 private RequestObject parseRequestObj(RequestEvent requestEvent) {
        RequestObject requestObj = null;
        try {
            requestObj = RequestObject.parse(requestEvent.getRequest());
        }
        catch (PeerUnavailableException e2) {
            e2.printStackTrace();
        }
        catch (ParseException e3) {
            e3.printStackTrace();
        }
        return requestObj;
    }
	 
	 private void addRequireHeader(Request request, String OptionTag2) throws ParseException {
	        Require require = new Require();
	        require.setOptionTag(OptionTag2);
	        request.setHeader(require);
	 }

	 private void addUserAgentHeader(Request request, List<String> userAgentList) throws ParseException {
	        UserAgent userAgent = new UserAgent();
	        userAgent.setProduct(userAgentList);
	        request.setHeader(userAgent);
	 }
	 
	public void setisRegister(boolean R) {
        this.isRegister = R;
    }
	
 	public boolean getisRegister() {
        return this.isRegister;
    }
 	
 	private String getTarget(String target) {
        return target.split("sip:")[1];
    }
 	
 	public int getRtpPort() {
        return this.RtpPort;
    }

    public void setRtpPort(int port) {
        this.RtpPort = port;
    }
}