package org.nycu.mc.iwf.RTCP;

public class message_tools {
    public void bits_to_bytes(byte[] header, int id, byte b2) {
        int n2 = id;
        header[n2] = (byte)(header[n2] + b2);
        int n3 = id;
        header[n3] = (byte)(header[n3] << 1);
    }

    public void length_transf(byte[] header, int len) {
        byte[] bytes = new byte[16];
        int i2 = 15;
        int k2 = 0;
        while (i2 >= 0) {
            bytes[k2] = (len >> i2 & 1) == 1 ? (byte)1 : 0;
            --i2;
            ++k2;
        }
        i2 = 0;
        while (i2 < 8) {
            this.bits_to_bytes(header, 2, bytes[i2]);
            ++i2;
        }
        i2 = 8;
        while (i2 < 16) {
            this.bits_to_bytes(header, 3, bytes[i2]);
            ++i2;
        }
        header[2] = (byte)(header[2] >> 1);
        header[3] = (byte)(header[3] >> 1);
        header[2] = (byte)(header[2] & 0x7E);
        header[3] = (byte)(header[3] & 0x7E);
    }

    public void set_head2_length_buffer(String id, byte[] msg_field) {
    	System.out.println("[id]"+id);
    	System.out.println("[msg_field]"+msg_field);
        int length = id.length();
        int buffer = 0;
        buffer = length + 2;
        while (buffer % 4 != 0) {
            ++buffer;
        }
        msg_field = new byte[buffer];
    }

    public void set_head3_length_buffer(String id, byte[] msg_field) {
        int length = id.length();
        int buffer = 0;
        buffer = length + 3;
        while (buffer % 4 != 0) {
            ++buffer;
        }
        msg_field = new byte[buffer];
    }
}

