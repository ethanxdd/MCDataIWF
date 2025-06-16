package org.nycu.mc.iwf.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;


public class SimpleSessionDescription {
    private final Fields mFields = new Fields("voscbtka");
    private final ArrayList<Media> mMedia = new ArrayList();

    public SimpleSessionDescription(String name, long sessionId, String address) {
        address = String.valueOf(address.indexOf(58) < 0 ? "IN IP4 " : "IN IP6 ") + address;
        this.mFields.parse("v=0");
        this.mFields.parse(String.format(Locale.US, "o=%s %d %d %s", name, sessionId, System.currentTimeMillis(), address));
        this.mFields.parse("s=-");
        this.mFields.parse("t=0 0");
        this.mFields.parse("c=" + address);
    }

    public SimpleSessionDescription(String message) {
        String[] lines = message.trim().replaceAll(" +", " ").split("[\r\n]+");
        Fields fields = this.mFields;
        String[] stringArray = lines;
        int n2 = lines.length;
        int n3 = 0;
        while (n3 < n2) {
            String line = stringArray[n3];
            try {
                if (line.charAt(1) != '=') {
                    throw new IllegalArgumentException();
                }
                if (line.charAt(0) == 'm') {
                    String[] parts = line.substring(2).split(" ", 4);
                    String[] ports = parts[1].split("/", 2);
                    Media media = this.newMedia(parts[0], Integer.parseInt(ports[0]), ports.length < 2 ? 1 : Integer.parseInt(ports[1]), parts[2]);
                    String[] stringArray2 = parts[3].split(" ");
                    int n4 = stringArray2.length;
                    int n5 = 0;
                    while (n5 < n4) {
                        String format = stringArray2[n5];
                        media.setFormat(format, null);
                        ++n5;
                    }
                    fields = media;
                } else {
                    fields.parse(line);
                }
            }
            catch (Exception e2) {
                throw new IllegalArgumentException("Invalid SDP: " + line);
            }
            ++n3;
        }
    }

    public Media newMedia(String type, int port, int portCount, String protocol) {
        Media media = new Media(type, port, portCount, protocol);
        this.mMedia.add(media);
        return media;
    }

    public Media[] getMedia() {
        return this.mMedia.toArray(new Media[this.mMedia.size()]);
    }

    public String encode() {
        StringBuilder buffer = new StringBuilder();
        this.mFields.write(buffer);
        for (Media media : this.mMedia) {
            media.write(buffer);
        }
        return buffer.toString();
    }

    public String getAddress() {
        return this.mFields.getAddress();
    }

    public void setAddress(String address) {
        this.mFields.setAddress(address);
    }

    public String getEncryptionMethod() {
        return this.mFields.getEncryptionMethod();
    }

    public String getEncryptionKey() {
        return this.mFields.getEncryptionKey();
    }

    public void setEncryption(String method, String key) {
        this.mFields.setEncryption(method, key);
    }

    public String[] getBandwidthTypes() {
        return this.mFields.getBandwidthTypes();
    }

    public int getBandwidth(String type) {
        return this.mFields.getBandwidth(type);
    }

    public void setBandwidth(String type, int value) {
        this.mFields.setBandwidth(type, value);
    }

    public String[] getAttributeNames() {
        return this.mFields.getAttributeNames();
    }

    public String getAttribute(String name) {
        return this.mFields.getAttribute(name);
    }

    public void setAttribute(String name, String value) {
        this.mFields.setAttribute(name, value);
    }

    private static class Fields {
        private final String mOrder;
        private final ArrayList<String> mLines = new ArrayList();

        Fields(String order) {
            this.mOrder = order;
        }

        public String getAddress() {
            String address = this.get("c", '=');
            if (address == null) {
                return null;
            }
            String[] parts = address.split(" ");
            if (parts.length != 3) {
                return null;
            }
            int slash = parts[2].indexOf(47);
            return slash < 0 ? parts[2] : parts[2].substring(0, slash);
        }

        public void setAddress(String address) {
            if (address != null) {
                address = String.valueOf(address.indexOf(58) < 0 ? "IN IP4 " : "IN IP6 ") + address;
            }
            this.set("c", '=', address);
        }

        public String getEncryptionMethod() {
            String encryption = this.get("k", '=');
            if (encryption == null) {
                return null;
            }
            int colon = encryption.indexOf(58);
            return colon == -1 ? encryption : encryption.substring(0, colon);
        }

        public String getEncryptionKey() {
            String encryption = this.get("k", '=');
            if (encryption == null) {
                return null;
            }
            int colon = encryption.indexOf(58);
            return colon == -1 ? null : encryption.substring(0, colon + 1);
        }

        public void setEncryption(String method, String key) {
            this.set("k", '=', method == null || key == null ? method : String.valueOf(method) + ':' + key);
        }

        public String[] getBandwidthTypes() {
            return this.cut("b=", ':');
        }

        public int getBandwidth(String type) {
            String value = this.get("b=" + type, ':');
            if (value != null) {
                try {
                    return Integer.parseInt(value);
                }
                catch (NumberFormatException numberFormatException) {
                    this.setBandwidth(type, -1);
                }
            }
            return -1;
        }

        public void setBandwidth(String type, int value) {
            this.set("b=" + type, ':', value < 0 ? null : String.valueOf(value));
        }

        public String[] getAttributeNames() {
            return this.cut("a=", ':');
        }

        public String getAttribute(String name) {
            return this.get("a=" + name, ':');
        }

        public void setAttribute(String name, String value) {
            this.set("a=" + name, ':', value);
        }

        private void write(StringBuilder buffer) {
            int i2 = 0;
            while (i2 < this.mOrder.length()) {
                char type = this.mOrder.charAt(i2);
                for (String line : this.mLines) {
                    if (line.charAt(0) != type) continue;
                    buffer.append(line).append("\r\n");
                }
                ++i2;
            }
        }

        private void parse(String line) {
            char type = line.charAt(0);
            if (this.mOrder.indexOf(type) == -1) {
                return;
            }
            char delimiter = '=';
            if (line.startsWith("a=rtpmap:") || line.startsWith("a=fmtp:")) {
                delimiter = ' ';
            } else if (type == 'b' || type == 'a') {
                delimiter = ':';
            }
            int i2 = line.indexOf(delimiter);
            if (i2 == -1) {
                this.set(line, delimiter, "");
            } else {
                this.set(line.substring(0, i2), delimiter, line.substring(i2 + 1));
            }
        }

        private String[] cut(String prefix, char delimiter) {
            String[] names = new String[this.mLines.size()];
            int length = 0;
            for (String line : this.mLines) {
                if (!line.startsWith(prefix)) continue;
                int i2 = line.indexOf(delimiter);
                if (i2 == -1) {
                    i2 = line.length();
                }
                names[length] = line.substring(prefix.length(), i2);
                ++length;
            }
            return Arrays.copyOf(names, length);
        }

        private int find(String key, char delimiter) {
            int length = key.length();
            int i2 = this.mLines.size() - 1;
            while (i2 >= 0) {
                String line = this.mLines.get(i2);
                if (line.startsWith(key) && (line.length() == length || line.charAt(length) == delimiter)) {
                    return i2;
                }
                --i2;
            }
            return -1;
        }

        private void set(String key, char delimiter, String value) {
            int index = this.find(key, delimiter);
            if (value != null) {
                if (value.equals("inactive")) {
                    this.mLines.add(key);
                    return;
                }
                if (value.length() != 0) {
                    key = String.valueOf(key) + delimiter + value;
                }
                if (index == -1) {
                    this.mLines.add(key);
                } else {
                    this.mLines.set(index, key);
                }
            } else if (index != -1) {
                this.mLines.remove(index);
            }
        }

        private String get(String key, char delimiter) {
            int index = this.find(key, delimiter);
            if (index == -1) {
                return null;
            }
            String line = this.mLines.get(index);
            int length = key.length();
            return line.length() == length ? "" : line.substring(length + 1);
        }
    }

    public static class Media
    extends Fields {
        private final String mType;
        private final int mPort;
        private final int mPortCount;
        private final String mProtocol;
        private ArrayList<String> mFormats = new ArrayList();

        private Media(String type, int port, int portCount, String protocol) {
            super("icbka");
            this.mType = type;
            this.mPort = port;
            this.mPortCount = portCount;
            this.mProtocol = protocol;
        }

        public String getType() {
            return this.mType;
        }

        public int getPort() {
            return this.mPort;
        }

        public int getPortCount() {
            return this.mPortCount;
        }

        public String getProtocol() {
            return this.mProtocol;
        }

        public String[] getFormats() {
            return this.mFormats.toArray(new String[this.mFormats.size()]);
        }

        public String getFmtp(String format) {
            return ((Fields)this).get("a=fmtp:" + format, ' ');
        }

        public void setFormat(String format, String fmtp) {
            this.mFormats.remove(format);
            this.mFormats.add(format);
            ((Fields)this).set("a=rtpmap:" + format, ' ', null);
            ((Fields)this).set("a=fmtp:" + format, ' ', fmtp);
        }

        public void removeFormat(String format) {
            this.mFormats.remove(format);
            ((Fields)this).set("a=rtpmap:" + format, ' ', null);
            ((Fields)this).set("a=fmtp:" + format, ' ', null);
        }

        public int[] getRtpPayloadTypes() {
            int[] types = new int[this.mFormats.size()];
            int length = 0;
            for (String format : this.mFormats) {
                try {
                    types[length] = Integer.parseInt(format);
                    ++length;
                }
                catch (NumberFormatException numberFormatException) {
                    // empty catch block
                }
            }
            return Arrays.copyOf(types, length);
        }

        public String getRtpmap(int type) {
            return ((Fields)this).get("a=rtpmap:" + type, ' ');
        }

        public String getFmtp(int type) {
            return ((Fields)this).get("a=fmtp:" + type, ' ');
        }

        public void setRtpPayload(int type, String rtpmap, String fmtp) {
            String format = String.valueOf(type);
            this.mFormats.remove(format);
            this.mFormats.add(format);
            ((Fields)this).set("a=rtpmap:" + format, ' ', rtpmap);
            ((Fields)this).set("a=fmtp:" + format, ' ', fmtp);
        }

        public void removeRtpPayload(int type) {
            this.removeFormat(String.valueOf(type));
        }

        private void write(StringBuilder buffer) {
            buffer.append("m=").append(this.mType).append(' ').append(this.mPort);
            if (this.mPortCount != 1) {
                buffer.append('/').append(this.mPortCount);
            }
            buffer.append(' ').append(this.mProtocol);
            for (String format : this.mFormats) {
                buffer.append(' ').append(format);
            }
            buffer.append("\r\n");
            ((Fields)this).write(buffer);
        }
    }
}