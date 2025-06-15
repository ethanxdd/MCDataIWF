package org.nycu.mc.iwf.xml;

import java.util.ArrayList;
import java.util.List;

public class Pidf {
    public static final String STATUS_OPEN = "open";
    public static final String STATUS_CLOSED = "closed";
    public static final String PRESENCE = "presence";
    public static final String UTF8 = "UTF-8";
    public static final String TUPLE = "tuple";
    public static final String STATUS = "status";
    public static final String BASIC = "basic";
    public static final String CONTACT = "contact";
    public static final String TIMESTAMP = "timestamp";
    public static final String XMLNS = "xmlns";
    public static final String ENTITY = "entity";
    public static final String ID = "id";
    public static final String PRIORITY = "priority";
    public static final String NOTE = "note";
    private List<String> xmlnsList = new ArrayList<String>();
    private String entityName;
    private String tupleId;
    private String status;
    private String contact;
    private String contactPriority;
    private String note;
    private String timeStamp;

    public String getEntityName() {
        return this.entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getTupleId() {
        return this.tupleId;
    }

    public void setTupleId(String tupleId) {
        this.tupleId = tupleId;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getContactPriority() {
        return this.contactPriority;
    }

    public void setContactPriority(String contactPriority) {
        this.contactPriority = contactPriority;
    }

    public String getNote() {
        return this.note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getTimeStamp() {
        return this.timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getContact() {
        return this.contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String toString() {
        return "PIDF : " + this.entityName + "\n TupleId : " + this.tupleId + "\n Status : " + this.status + "\n Contact : " + this.contact + " ContactPriority : " + this.contactPriority + "\n Note : " + this.note + "\n TimeStamp : " + this.timeStamp;
    }

    public void addXmlns(String xmlns) {
        this.xmlnsList.add(xmlns);
    }

    public List<String> getXmlnsList() {
        return this.xmlnsList;
    }
}

