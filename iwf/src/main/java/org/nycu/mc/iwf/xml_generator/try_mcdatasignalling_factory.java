package org.nycu.mc.iwf.xml_generator;

import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class try_mcdatasignalling_factory {
	public String string_try_mcdatasignalling_factory(String request_type, String client_id, String conversationid) {
		String Strresult = "";
	    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
	    try {
	      DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
	      Document doc = docBuilder.newDocument();
	      Element rootElement = doc.createElement("mcdatasognalling");
	      doc.appendChild(rootElement);
	      rootElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
	      Element mcdata_params = doc.createElement("mcdata-Params");
	      rootElement.appendChild(mcdata_params);
	      Element type_request = doc.createElement("message-type");
	      mcdata_params.appendChild(type_request);
	      type_request.setTextContent("00000001");
	      Element mcdata_req_uri = doc.createElement("time");
	      mcdata_params.appendChild(mcdata_req_uri);
	      long time1 = Calendar.getInstance().getTimeInMillis();
	      String time = Long.toString(time1);
	      mcdata_req_uri.setTextContent(time);
	      
	      Element id_request = doc.createElement("mcdata-request-id");
	      mcdata_params.appendChild(id_request);
	      id_request.setAttribute("type", "normal");
//	      mcdata_params.appendChild(type_request);
	      Element messageID = doc.createElement("messageID");
	      UUID messageID_uuid = UUID.randomUUID();
	      String messageID_uuidAsString = messageID_uuid.toString();
	      messageID.setTextContent(messageID_uuidAsString);
	      id_request.appendChild(messageID);
	      Element conversationID = doc.createElement("conversationID");
//	      UUID conversationID_uuid = UUID.randomUUID();
//	      String conversationID_uuidAsString = conversationID_uuid.toString();
//	      conversationID.setTextContent(conversationID_uuidAsString);
	      conversationID.setTextContent(conversationid);
	      id_request.appendChild(conversationID);
	      Element senderID = doc.createElement("sendermcdatauserID");
	      senderID.setTextContent(client_id);
	      id_request.appendChild(senderID);
	      /////2021/07/15 04:27:37 
	      TransformerFactory transformerFactory = TransformerFactory.newInstance();
	      Transformer transformer = transformerFactory.newTransformer();
	      DOMSource source = new DOMSource(doc);
	      StringWriter writer = new StringWriter();
	      StreamResult result = new StreamResult(writer);
	      transformer.transform(source, result);
	      Strresult = writer.toString();
	      return Strresult;
	    } catch (ParserConfigurationException e) {
	      e.printStackTrace();
	    } catch (TransformerConfigurationException e) {
	      e.printStackTrace();
	    } catch (TransformerException e) {
	      e.printStackTrace();
	    } 
	    return null;
	   }
	
	
	public String string_try_mcdatasig_factory(String request_type, String receiver, String groupID, String initiater) {
	    String Strresult = "";
	    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
	    try {
	      DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
	      Document doc = docBuilder.newDocument();
	      Element rootElement = doc.createElement("mcdatasognalling");
	      doc.appendChild(rootElement);
	      rootElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
	      Element mcdata_params = doc.createElement("mcdata-Params");
	      rootElement.appendChild(mcdata_params);
	      Element type_request = doc.createElement("message-type");
	      mcdata_params.appendChild(type_request);
	      type_request.setTextContent("SDS SIGNALLING PAYLOAD");
	      Element mcdata_req_uri = doc.createElement("time");
	      mcdata_params.appendChild(mcdata_req_uri);
	      DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
	      String time = dtf.format(LocalDateTime.now());
	      mcdata_req_uri.setTextContent(time);
	      
	      Element id_request = doc.createElement("mcdata-request-id");
	      id_request.setAttribute("type", "normal");
	      mcdata_params.appendChild(type_request);
	      Element messageID = doc.createElement("messageID");
	      messageID.setTextContent("01010101-0101-0101-1001-010101010101");
	      id_request.appendChild(messageID);
	      Element conversationID = doc.createElement("conversationID");
	      conversationID.setTextContent("01010101-0101-0101-1001-010101010101");
	      id_request.appendChild(conversationID);
	      /////2021/07/15 04:27:37 
	      TransformerFactory transformerFactory = TransformerFactory.newInstance();
	      Transformer transformer = transformerFactory.newTransformer();
	      DOMSource source = new DOMSource(doc);
	      StringWriter writer = new StringWriter();
	      StreamResult result = new StreamResult(writer);
	      transformer.transform(source, result);
	      Strresult = writer.toString();
	      return Strresult;
	    } catch (ParserConfigurationException e) {
	      e.printStackTrace();
	    } catch (TransformerConfigurationException e) {
	      e.printStackTrace();
	    } catch (TransformerException e) {
	      e.printStackTrace();
	    } 
	    return null;
	  }
}

