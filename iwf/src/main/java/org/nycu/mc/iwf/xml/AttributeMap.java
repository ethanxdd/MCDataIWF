package org.nycu.mc.iwf.xml;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class AttributeMap {
    private Map<String, Attribute> attrMap = new HashMap<String, Attribute>();

    public void addAttribute(String uri, String localName, String qName, String type, String value) {
        Attribute attribute = new Attribute();
        attribute.setUri(uri);
        attribute.setLocalName(localName);
        attribute.setqName(qName);
        attribute.setType(type);
        attribute.setValue(value);
        this.attrMap.put(qName, attribute);
    }

    public void removeAttribute(String qName) {
        this.attrMap.remove(qName);
    }

    public Map<String, Attribute> getAttrMap() {
        return this.attrMap;
    }

    public Attribute getAttribute(String qName) throws NullPointerException {
        if (this.attrMap.containsKey(qName)) {
            return this.attrMap.get(qName);
        }
        throw new NullPointerException("Attribute " + qName + " not exist");
    }

    public Collection<Attribute> getAttributeList() {
        return this.attrMap.values();
    }
}

