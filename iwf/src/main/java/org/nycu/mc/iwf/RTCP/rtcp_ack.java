package org.nycu.mc.iwf.RTCP;


public class rtcp_ack
extends Rtcp {
    public static byte[] message;
    public byte[] Source_field;
    public byte[] Message_Type_field;
    public int Source_id;
    public int Message_id;

    public rtcp_ack() {
    }

    public rtcp_ack(long SSRC_ID, int source) {
        message_tools tool = new message_tools();
        this.header = this.messageheader("ack", SSRC_ID, "MCPT");
        this.Source_field = new byte[4];
        this.Source_field = this.set_Source_field(source);
        this.Message_Type_field = new byte[4];
        this.Message_Type_field = this.set_Message_Type_id_field();
        message = new byte[this.header.length + this.Source_field.length + this.Message_Type_field.length];
        tool.length_transf(this.header, message.length);
        this.writemessage();
    }

    public void writemessage() {
        int i2 = 0;
        while (i2 < this.header.length) {
            rtcp_ack.message[i2] = this.header[i2];
            ++i2;
        }
        i2 = this.header.length;
        int j2 = 0;
        while (j2 < this.Source_field.length) {
            rtcp_ack.message[i2 + j2] = this.Source_field[j2];
            ++j2;
        }
        i2 = this.header.length + this.Source_field.length;
        j2 = 0;
        while (j2 < this.Message_Type_field.length) {
            rtcp_ack.message[i2 + j2] = this.Message_Type_field[j2];
            ++j2;
        }
    }

    public void get_field(byte[] M) {
        int nexti;
        int i2 = nexti = 12;
        this.Source_field = new byte[4];
        int j2 = 0;
        while (j2 < 4) {
            this.Source_field[j2] = M[nexti + j2];
            ++i2;
            ++j2;
        }
        nexti = i2;
        while (nexti % 4 != 0) {
            ++nexti;
        }
        this.Message_Type_field = new byte[M[nexti + 1] + 2];
        j2 = 0;
        while (j2 < M[nexti + 1] + 2) {
            this.Message_Type_field[j2] = M[nexti + j2];
            ++i2;
            ++j2;
        }
    }

    public void get_Source_field(byte[] M) {
        if (M[0] != 10) {
            System.out.println("Source_field type error");
        } else {
            this.Source_id = Byte.toUnsignedInt(M[3]);
        }
    }

    public void get_Message_Type_field(byte[] M) {
        if (M[0] != 12) {
            System.out.println("Message_Type_field type error");
        } else {
            this.Message_id = Byte.toUnsignedInt(M[2]);
        }
    }

    public void parse_allfield(byte[] Data2) {
        this.get_field(Data2);
        this.get_header(Data2);
        this.get_Source_field(this.Source_field);
        this.get_Message_Type_field(this.Message_Type_field);
    }
}

