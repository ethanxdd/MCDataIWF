package org.nycu.mc.iwf.xml;

import org.xml.sax.Attributes;

public class Element {
    public static final int START = 1001;
    public static final int END = 1002;
    public static final int START_WITH_CHARACTOR = 1003;
    private String uri;
    private String localName;
    private String qName;
    private Attributes atts;
    private int elementState;
    private Character character;

    Element(String uri, String localName, String qName, Attributes atts, int elementState) {
        this.uri = uri;
        this.localName = localName;
        this.qName = qName;
        this.atts = atts;
        this.elementState = elementState;
    }

    public String getUri() {
        return this.uri;
    }

    public String getLocalName() {
        return this.localName;
    }

    public String getqName() {
        return this.qName;
    }

    public Attributes getAtts() {
        return this.atts;
    }

    public int getElementState() {
        return this.elementState;
    }

    public Character getCharacter() {
        return this.character;
    }

    public void setCharacter(Character character) {
        this.character = character;
    }
}

