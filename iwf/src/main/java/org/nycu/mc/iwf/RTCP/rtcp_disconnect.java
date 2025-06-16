package org.nycu.mc.iwf.RTCP;

public class rtcp_disconnect
extends Rtcp {
    public byte[] message;
    public byte[] Session_id_field;
    public int Session_type;
    public String Session_id;

    public rtcp_disconnect() {
    }

    public rtcp_disconnect(long SSRC_ID, String session_identity) {
        message_tools tool = new message_tools();
        this.header = this.messageheader("disconnect", SSRC_ID, "MCPC");
        tool.set_head3_length_buffer(session_identity, this.Session_id_field);
        this.Session_id_field = this.set_Session_id_field(session_identity);
        this.message = new byte[this.header.length + this.Session_id_field.length];
        tool.length_transf(this.header, this.message.length);
        this.writemessage();
    }

    public void writemessage() {
        int i2 = 0;
        while (i2 < this.header.length) {
            this.message[i2] = this.header[i2];
            ++i2;
        }
        i2 = this.header.length;
        int j2 = 0;
        while (j2 < this.Session_id_field.length) {
            this.message[i2 + j2] = this.Session_id_field[j2];
            ++j2;
        }
    }

    public void get_field(byte[] M) {
        int nexti = 12;
        this.Session_id_field = new byte[M[nexti + 1] + 3];
        int j2 = 0;
        while (j2 < M[nexti + 1] + 3) {
            this.Session_id_field[j2] = M[nexti + j2];
            ++j2;
        }
    }

    public void get_Session_id_field(byte[] M) {
        if (M[0] != 1) {
            System.out.println("Session_id_field type error");
        } else {
            byte[] Tmp = new byte[M[1]];
            int nexti = 3;
            int i2 = 0;
            while (i2 < M[1]) {
                Tmp[i2] = M[nexti + i2];
                ++i2;
            }
            this.Session_type = Byte.toUnsignedInt(M[2]);
            this.Session_id = new String(Tmp);
        }
    }

    public void parse_all_field(byte[] data) {
        this.get_field(data);
        this.get_Session_id_field(this.Session_id_field);
    }
}

