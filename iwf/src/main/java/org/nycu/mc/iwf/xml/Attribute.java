package org.nycu.mc.iwf.xml;


public class Attribute {
    private String uri;
    private String localName;
    private String qName;
    private String type;
    private String value;

    public String getUri() {
        return this.uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getLocalName() {
        return this.localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    public String getqName() {
        return this.qName;
    }

    public void setqName(String qName) {
        this.qName = qName;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

