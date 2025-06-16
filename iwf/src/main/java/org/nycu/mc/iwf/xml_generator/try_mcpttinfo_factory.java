package org.nycu.mc.iwf.xml_generator;

import java.io.StringWriter;

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

public class try_mcpttinfo_factory {
    public String string_try_mcpttinfo_factory(String request_type, String client_id) {
        String Strresult = "";
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("mcpttinfo");
            Element URI_type = doc.createElement("mcpttURI");
            Element Boolean_type = doc.createElement("mcpttBoolean");
            doc.appendChild(rootElement);
            rootElement.setAttribute("xmlns", "urn:3gpp:ns:mcpttInfo:1.0");
            rootElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            Element mcptt_params = doc.createElement("mcptt-Params");
            rootElement.appendChild(mcptt_params);
            Element mcptt_req_uri = doc.createElement("mcptt-request-uri");
            mcptt_req_uri.setAttribute("type", request_type);
            mcptt_params.appendChild(mcptt_req_uri);
            Element mcptt_uri_1 = doc.createElement("mcpttURI");
            mcptt_req_uri.appendChild(mcptt_uri_1);
            mcptt_uri_1.setTextContent(client_id);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            transformer.transform(source, result);
            Strresult = writer.toString();
            return Strresult;
        }
        catch (ParserConfigurationException e2) {
            e2.printStackTrace();
        }
        catch (TransformerConfigurationException e3) {
            e3.printStackTrace();
        }
        catch (TransformerException e4) {
            e4.printStackTrace();
        }
        return null;
    }

    public String string_try_mcpttinfo_factory(String request_type, String client_id, String groupID) {
        String Strresult = "";
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("mcpttinfo");
            Element URI_type = doc.createElement("mcpttURI");
            Element Boolean_type = doc.createElement("mcpttBoolean");
            doc.appendChild(rootElement);
            rootElement.setAttribute("xmlns", "urn:3gpp:ns:mcpttInfo:1.0");
            rootElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            Element mcptt_params = doc.createElement("mcptt-Params");
            rootElement.appendChild(mcptt_params);
            Element mcptt_req_uri = doc.createElement("mcptt-request-uri");
            mcptt_req_uri.setAttribute("type", request_type);
            mcptt_params.appendChild(mcptt_req_uri);
            Element mcptt_uri_1 = doc.createElement("mcpttURI");
            mcptt_req_uri.appendChild(mcptt_uri_1);
            mcptt_uri_1.setTextContent(groupID);
            Element mcptt_client_id = doc.createElement("mcptt-client-id");
            mcptt_params.appendChild(mcptt_client_id);
            mcptt_client_id.setTextContent(client_id);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            transformer.transform(source, result);
            Strresult = writer.toString();
            return Strresult;
        }
        catch (ParserConfigurationException e2) {
            e2.printStackTrace();
        }
        catch (TransformerConfigurationException e3) {
            e3.printStackTrace();
        }
        catch (TransformerException e4) {
            e4.printStackTrace();
        }
        return null;
    }

    public String string_try_mcpttinfo_factory(String request_type, String client_id, String group_id, boolean emergency, boolean alert) throws ParserConfigurationException, TransformerException {
        String Strresult = "";
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("mcpttinfo");
        Element URI_type = doc.createElement("mcpttURI");
        Element Boolean_type = doc.createElement("mcpttBoolean");
        doc.appendChild(rootElement);
        rootElement.setAttribute("xmlns", "urn:3gpp:ns:mcpttInfo:1.0");
        rootElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        Element mcptt_params = doc.createElement("mcptt-Params");
        rootElement.appendChild(mcptt_params);
        Element mcptt_req_uri = doc.createElement("mcptt-request-uri");
        mcptt_req_uri.setAttribute("type", request_type);
        mcptt_params.appendChild(mcptt_req_uri);
        Element mcptt_uri_1 = doc.createElement("mcpttURI");
        mcptt_req_uri.appendChild(mcptt_uri_1);
        mcptt_uri_1.setTextContent(client_id);
        Element mcptt_client_id = doc.createElement("mcptt-client-id");
        mcptt_params.appendChild(mcptt_client_id);
        mcptt_client_id.setTextContent(client_id);
        Element mcptt_calling_id = doc.createElement("mcptt-calling-user-id");
        mcptt_params.appendChild(mcptt_calling_id);
        Element mcptt_uri_2 = doc.createElement("mcpttURI");
        mcptt_calling_id.appendChild(mcptt_uri_2);
        mcptt_uri_2.setTextContent(client_id);
        Element mcptt_callinggroup_id = doc.createElement("mcptt-calling-group-id");
        mcptt_params.appendChild(mcptt_callinggroup_id);
        Element mcptt_uri_3 = doc.createElement("mcpttURI");
        mcptt_callinggroup_id.appendChild(mcptt_uri_3);
        mcptt_uri_3.setTextContent(group_id);
        Element mcptt_emergency_ind = doc.createElement("emergency-ind");
        Element mcptt_boolean = doc.createElement("mcpttBoolean");
        mcptt_params.appendChild(mcptt_emergency_ind);
        mcptt_emergency_ind.appendChild(mcptt_boolean);
        if (emergency) {
            mcptt_boolean.setTextContent("true");
        } else {
            mcptt_boolean.setTextContent("false");
        }
        Element mcptt_emergency_alert_ind = doc.createElement("alert-ind");
        Element mcptt_boolean_1 = doc.createElement("mcpttBoolean");
        mcptt_params.appendChild(mcptt_emergency_alert_ind);
        mcptt_emergency_alert_ind.appendChild(mcptt_boolean_1);
        if (alert) {
            mcptt_boolean_1.setTextContent("true");
        } else {
            mcptt_boolean_1.setTextContent("false");
        }
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        transformer.transform(source, result);
        Strresult = writer.toString();
        return Strresult;
    }
}

