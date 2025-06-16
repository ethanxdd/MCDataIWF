package org.nycu.mc.iwf.RTCP;

import java.nio.ByteBuffer;

public class rtcp_idle
extends Rtcp {
    public byte[] message;
    public byte[] Message_Sequence_Number_field;
    public byte[] Floor_Indicator_field;
    public short Message_Sequence_id;
    public short Floor_ind;

    public rtcp_idle() {
    }

    public rtcp_idle(long SSRC_ID, short message_sequence_id, short indicator) {
        message_tools tool = new message_tools();
        this.header = this.messageheader("idle", SSRC_ID, "MCPT");
        this.Message_Sequence_Number_field = new byte[4];
        this.Message_Sequence_Number_field = this.set_Message_Sequence_Number_field(message_sequence_id);
        this.Floor_Indicator_field = new byte[4];
        this.Floor_Indicator_field = this.set_Floor_Indicator_field(indicator);
        this.message = new byte[this.header.length + this.Message_Sequence_Number_field.length + this.Floor_Indicator_field.length];
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
        while (j2 < this.Message_Sequence_Number_field.length) {
            this.message[i2 + j2] = this.Message_Sequence_Number_field[j2];
            ++j2;
        }
        i2 = this.header.length + this.Message_Sequence_Number_field.length;
        j2 = 0;
        while (j2 < this.Floor_Indicator_field.length) {
            this.message[i2 + j2] = this.Floor_Indicator_field[j2];
            ++j2;
        }
    }

    public void get_field(byte[] M) {
        int nexti;
        int i2 = nexti = 12;
        this.Message_Sequence_Number_field = new byte[M[nexti + 1] + 2];
        int j2 = 0;
        while (j2 < M[nexti + 1] + 2) {
            this.Message_Sequence_Number_field[j2] = M[nexti + j2];
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

    public void get_Message_Sequence_Number_field(byte[] M) {
        if (M[0] != 8) {
            System.out.println("Message_Sequence_Number_field type error");
        } else {
            byte[] Tmp = new byte[4];
            int nexti = 2;
            int i2 = 0;
            while (i2 < M[1]) {
                Tmp[i2] = M[nexti + i2];
                ++i2;
            }
            ByteBuffer b2 = ByteBuffer.wrap(Tmp);
            this.Message_Sequence_id = b2.getShort();
        }
    }

    public void get_Floor_Indicator_field(byte[] M) {
        if (M[0] != 13) {
            System.out.println("Floor_Indicator_field type error");
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

    public void parse_all_field(byte[] data) {
        this.get_field(data);
        this.get_Floor_Indicator_field(this.Floor_Indicator_field);
        this.get_Message_Sequence_Number_field(this.Message_Sequence_Number_field);
    }
}

