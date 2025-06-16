package org.nycu.mc.iwf.RTCP;


public class rtcp_acknowledgement
extends Rtcp {
    public static byte[] message;
    public byte[] Reason_code_field;
    public int Reason_code;

    public rtcp_acknowledgement() {
    }

    public rtcp_acknowledgement(long SSRC_ID, int reason) {
        message_tools tool = new message_tools();
        this.header = this.messageheader("acknowledgement", SSRC_ID, "MCPC");
        this.Reason_code_field = new byte[4];
        this.Reason_code_field = this.set_Reason_code_field(reason);
        message = new byte[this.header.length + this.Reason_code_field.length];
        tool.length_transf(this.header, message.length);
        this.writemessage();
    }

    public void writemessage() {
        int i2 = 0;
        while (i2 < this.header.length) {
            rtcp_acknowledgement.message[i2] = this.header[i2];
            ++i2;
        }
        i2 = this.header.length;
        int j2 = 0;
        while (j2 < this.Reason_code_field.length) {
            rtcp_acknowledgement.message[i2 + j2] = this.Reason_code_field[j2];
            ++j2;
        }
    }

    public byte[] set_Reason_code_field(int reason) {
        int buffer = 4;
        byte[] Reason_Code = new byte[buffer];
        Reason_Code[0] = 6;
        Reason_Code[1] = 2;
        Reason_Code[2] = 0;
        Reason_Code[3] = (byte)reason;
        return Reason_Code;
    }

    public void get_field(byte[] M) {
        int nexti = 12;
        this.Reason_code_field = new byte[4];
        int j2 = 0;
        while (j2 < 4) {
            this.Reason_code_field[j2] = M[nexti + j2];
            ++j2;
        }
    }

    public void get_Reason_code_field(byte[] M) {
        if (M[0] != 6) {
            System.out.println("Reason_code_field type error");
        } else {
            this.Reason_code = Byte.toUnsignedInt(M[3]);
            System.out.println("reason = " + this.Reason_code);
        }
    }

    public void parse_allfield(byte[] Data2) {
        this.get_field(Data2);
        this.get_header(Data2);
        this.get_Reason_code_field(this.Reason_code_field);
    }
}

