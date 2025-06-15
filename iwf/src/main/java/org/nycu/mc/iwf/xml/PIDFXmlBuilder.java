package org.nycu.mc.iwf.xml;

import java.io.ByteArrayOutputStream;

import org.xml.sax.SAXException;

public class PIDFXmlBuilder {
    private Pidf pidf;
    private XMLBuilder xmlBuilder;
    private String encoding;

    public PIDFXmlBuilder(Pidf pidf, String encoding) {
        this.pidf = pidf;
        this.xmlBuilder = new XMLBuilder();
        this.encoding = encoding;
    }

    public ByteArrayOutputStream build() throws SAXException {
        this.xmlBuilder.setOutputProperty("encoding", this.encoding);
        this.xmlBuilder.setOutputProperty("indent", "yes");
        AttributeMap presenceAttributeMap = new AttributeMap();
        for (String str : this.pidf.getXmlnsList()) {
            presenceAttributeMap.addAttribute("", "", "xmlns", "String", str);
        }
        presenceAttributeMap.addAttribute("", "", "entity", "String", this.pidf.getEntityName());
        this.xmlBuilder.startElement("", "", "presence", presenceAttributeMap);
        AttributeMap tupleAttributeMap = new AttributeMap();
        tupleAttributeMap.addAttribute("", "", "id", "String", this.pidf.getTupleId());
        this.xmlBuilder.startElement("", "", "tuple", tupleAttributeMap);
        this.xmlBuilder.startElement("", "", "status");
        Character characterBasic = new Character(this.pidf.getStatus().toCharArray(), 0, this.pidf.getStatus().toCharArray().length);
        this.xmlBuilder.startElement("", "", "basic", characterBasic);
        this.xmlBuilder.endElement("", "", "basic");
        this.xmlBuilder.endElement("", "", "status");
        AttributeMap contactAttributeMap = new AttributeMap();
        contactAttributeMap.addAttribute("", "", "priority", "String", this.pidf.getContactPriority());
        Character characterContact = new Character(this.pidf.getContact().toCharArray(), 0, this.pidf.getContact().toCharArray().length);
        this.xmlBuilder.startElement("", "", "contact", contactAttributeMap, characterContact);
        this.xmlBuilder.endElement("", "", "contact");
        Character characterTime = new Character(this.pidf.getTimeStamp().toCharArray(), 0, this.pidf.getTimeStamp().toCharArray().length);
        this.xmlBuilder.startElement("", "", "timestamp", characterTime);
        this.xmlBuilder.endElement("", "", "timestamp");
        this.xmlBuilder.endElement("", "", "tuple");
        this.xmlBuilder.endElement("", "", "presence");
        return this.xmlBuilder.build();
    }

    public void setDebugMode(boolean debugMode) {
        this.xmlBuilder.setDebugMode(debugMode);
    }
}

