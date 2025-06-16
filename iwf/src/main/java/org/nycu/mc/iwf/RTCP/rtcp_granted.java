package org.nycu.mc.iwf.RTCP;

import java.nio.ByteBuffer;

public class rtcp_granted
extends Rtcp {
    public byte[] message;
    public byte[] Duration_field;
    public byte[] Floor_Priority_field;
    public byte[] Floor_Indicator_field;
    public byte[] SSRC_field;
    public short Duration_time;
    public int Priority_int;
    public short Floor_ind;
    public int SSRC_id;

    public rtcp_granted() {
    }

    public rtcp_granted(long SSRC_ID, long SSRC_ID2, short time, int priority, short indicator) {
        message_tools tool = new message_tools();
        this.header = this.messageheader("granted", SSRC_ID, "MCPT");
        this.Duration_field = new byte[4];
        this.Duration_field = this.set_Duration_field(time);
        this.SSRC_field = new byte[4];
        this.SSRC_field = this.set_SSRC_field(SSRC_ID2);
        this.Floor_Priority_field = new byte[4];
        this.Floor_Priority_field = this.set_Floor_Priority_field(priority);
        this.Floor_Indicator_field = new byte[4];
        this.Floor_Indicator_field = this.set_Floor_Indicator_field(indicator);
        this.message = new byte[this.header.length + this.Duration_field.length + this.SSRC_field.length + this.Floor_Priority_field.length + this.Floor_Indicator_field.length];
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
        while (j2 < this.Duration_field.length) {
            this.message[i2 + j2] = this.Duration_field[j2];
            ++j2;
        }
        i2 = this.header.length + this.Duration_field.length;
        j2 = 0;
        while (j2 < this.SSRC_field.length) {
            this.message[i2 + j2] = this.SSRC_field[j2];
            ++j2;
        }
        i2 = this.header.length + this.Duration_field.length + this.SSRC_field.length;
        j2 = 0;
        while (j2 < this.Floor_Priority_field.length) {
            this.message[i2 + j2] = this.Floor_Priority_field[j2];
            ++j2;
        }
        i2 = this.header.length + this.Duration_field.length + this.SSRC_field.length + this.Floor_Priority_field.length;
        j2 = 0;
        while (j2 < this.Floor_Indicator_field.length) {
            this.message[i2 + j2] = this.Floor_Indicator_field[j2];
            ++j2;
        }
    }

    public void get_field(byte[] M) {
        int nexti;
        int i2 = nexti = 12;
        this.Duration_field = new byte[4];
        int j2 = 0;
        while (j2 < 4) {
            this.Duration_field[j2] = M[nexti + j2];
            ++i2;
            ++j2;
        }
        nexti = i2;
        while (nexti % 4 != 0) {
            ++nexti;
        }
        this.SSRC_field = new byte[M[nexti + 1] + 2];
        j2 = 0;
        while (j2 < M[nexti + 1] + 2) {
            this.SSRC_field[j2] = M[nexti + j2];
            ++i2;
            ++j2;
        }
        nexti = i2;
        while (nexti % 4 != 0) {
            ++nexti;
        }
        this.Floor_Priority_field = new byte[4];
        j2 = 0;
        while (j2 < 4) {
            this.Floor_Priority_field[j2] = M[nexti + j2];
            ++i2;
            ++j2;
        }
        nexti = i2;
        while (nexti % 4 != 0) {
            ++nexti;
        }
        this.Floor_Indicator_field = new byte[M[nexti + 1] + 2];
        j2 = 0;
        while (j2 < M[nexti + 1] + 2) {
            this.Floor_Indicator_field[j2] = M[nexti + j2];
            ++i2;
            ++j2;
        }
    }

    public void get_Duration_field(byte[] M) {
        if (M[0] != 1) {
            System.out.println("[rtcp_granted] Duration_time type error");
        } else {
            byte[] Tmp = new byte[2];
            int nexti = 2;
            int i2 = 0;
            while (i2 < 2) {
                Tmp[i2] = M[i2 + nexti];
                ++i2;
            }
            ByteBuffer b2 = ByteBuffer.wrap(Tmp);
            this.Duration_time = b2.getShort();
        }
    }

    public void get_Floor_Priority_field(byte[] M) {
        if (M[0] != 0) {
            System.out.println("[rtcp_granted] Floor_Priority_field type error");
        } else {
            this.Priority_int = Byte.toUnsignedInt(M[2]);
        }
    }

    public void get_Floor_Indicator_field(byte[] M) {
        if (M[0] != 13) {
            System.out.println("[rtcp_granted] Floor_Indicator_field type error");
        } else {
            byte[] Tmp = new byte[4];
            int nexti = 2;
            int i2 = 0;
            while (i2 < 2) {
                Tmp[i2] = M[i2 + nexti];
                ++i2;
            }
            ByteBuffer b2 = ByteBuffer.wrap(Tmp);
            this.Floor_ind = b2.getShort();
        }
    }

    public void get_SSRC_field(byte[] M) {
        if (M[0] != 14) {
            System.out.println("[rtcp_granted] SSRC_field type error");
        } else {
            byte[] Tmp = new byte[4];
            int nexti = 2;
            int i2 = 0;
            while (i2 < 4) {
                Tmp[i2] = M[i2 + nexti];
                ++i2;
            }
            ByteBuffer b2 = ByteBuffer.wrap(Tmp);
            this.SSRC_id = b2.getInt();
        }
    }

    public void parse_all_field(byte[] data) {
        this.get_field(data);
        this.get_header(data);
        this.get_Duration_field(this.Duration_field);
        this.get_SSRC_field(this.SSRC_field);
        this.get_Floor_Indicator_field(this.Floor_Indicator_field);
        this.get_Floor_Priority_field(this.Floor_Priority_field);
    }
}

