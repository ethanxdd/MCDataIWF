package org.nycu.mc.iwf.xml;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;

import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class XMLBuilder {
    private static String TAG = "XMLBuilder";
    private ByteArrayOutputStream outputStream;
    private Result result;
    private SAXTransformerFactory saxTransformerFactory;
    private TransformerHandler transformerHandle;
    private Transformer transformer;
    private LinkedList<Element> elementList;
    private boolean debugMode = false;

    public XMLBuilder() {
        try {
            this.initialize();
        }
        catch (TransformerConfigurationException transformerConfigurationException) {
            // empty catch block
        }
    }

    private void initialize() throws TransformerConfigurationException {
        this.outputStream = new ByteArrayOutputStream();
        this.result = new StreamResult(this.outputStream);
        this.saxTransformerFactory = (SAXTransformerFactory)SAXTransformerFactory.newInstance();
        this.transformerHandle = this.saxTransformerFactory.newTransformerHandler();
        this.transformerHandle.setResult(this.result);
        this.transformer = this.transformerHandle.getTransformer();
        this.elementList = new LinkedList();
    }

    public void setOutputProperty(String name, String value) {
        this.transformer.setOutputProperty(name, value);
    }

    public void startElement(String uri, String localName, String qName) throws SAXException {
        AttributesImpl attributesImpl = new AttributesImpl();
        Element element = new Element(uri, localName, qName, attributesImpl, 1001);
        this.elementList.add(element);
    }

    public void startElement(String uri, String localName, String qName, AttributeMap attributeMap) throws SAXException {
        AttributesImpl attributesImpl = new AttributesImpl();
        for (Attribute a2 : attributeMap.getAttributeList()) {
            attributesImpl.addAttribute(a2.getUri(), a2.getLocalName(), a2.getqName(), a2.getType(), a2.getValue());
        }
        Element element = new Element(uri, localName, qName, attributesImpl, 1001);
        this.elementList.add(element);
    }

    public void startElement(String uri, String localName, String qName, Character character) throws SAXException {
        AttributesImpl attributesImpl = new AttributesImpl();
        Element element = new Element(uri, localName, qName, attributesImpl, 1003);
        element.setCharacter(character);
        this.elementList.add(element);
    }

    public void startElement(String uri, String localName, String qName, AttributeMap attributeMap, Character character) throws SAXException {
        AttributesImpl attributesImpl = new AttributesImpl();
        for (Attribute a2 : attributeMap.getAttributeList()) {
            attributesImpl.addAttribute(a2.getUri(), a2.getLocalName(), a2.getqName(), a2.getType(), a2.getValue());
        }
        Element element = new Element(uri, localName, qName, attributesImpl, 1003);
        element.setCharacter(character);
        this.elementList.add(element);
    }

    public void endElement(String uri, String localName, String qName) {
        Element element = new Element(uri, localName, qName, null, 1002);
        this.elementList.add(element);
    }

    public ByteArrayOutputStream build() throws SAXException {
        try {
            this.printDebugMSG("XML Build Start");
            this.transformerHandle.startDocument();
            while (!this.elementList.isEmpty()) {
                Element e2 = this.elementList.pop();
                switch (e2.getElementState()) {
                    case 1001: {
                        this.transformerHandle.startElement(e2.getUri(), e2.getLocalName(), e2.getqName(), e2.getAtts());
                        this.printDebugMSG("Start Element : " + e2.getqName());
                        break;
                    }
                    case 1003: {
                        Character c2 = e2.getCharacter();
                        this.transformerHandle.startElement(e2.getUri(), e2.getLocalName(), e2.getqName(), e2.getAtts());
                        this.transformerHandle.characters(c2.getCh(), c2.getStart(), c2.getLength());
                        this.printDebugMSG("Start Element with Char : " + e2.getqName() + " , " + String.valueOf(c2.getCh()));
                        break;
                    }
                    case 1002: {
                        this.transformerHandle.endElement(e2.getUri(), e2.getLocalName(), e2.getqName());
                        this.printDebugMSG("End Element : " + e2.getqName());
                    }
                }
            }
            this.transformerHandle.endDocument();
            this.printDebugMSG("XML Build End");
            return this.outputStream;
        }
        catch (SAXException e3) {
            throw new SAXException("XML build fail : " + e3);
        }
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public boolean isDebugModeOn() {
        return this.debugMode;
    }

    private void printDebugMSG(String debugMessage) {
        this.isDebugModeOn();
    }
}

