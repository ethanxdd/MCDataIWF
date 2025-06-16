package org.nycu.mc.iwf.RTCP;

import java.nio.ByteBuffer;


public class rtcp_taken
extends Rtcp {
    public byte[] message;
    public byte[] Granted_Party_id_field;
    public byte[] Permission_to_Request_field;
    public byte[] Message_Sequence_Number_field;
    public byte[] Floor_Indicator_field;
    public byte[] SSRC_field;
    public String Granted_id;
    public int permission_int;
    public short Floor_ind;
    public int SSRC_id;
    public short Message_Sequence_Number;

    public rtcp_taken() {
    }

    public rtcp_taken(long SSRC_ID, long SSRC_ID2, String granted_id, int permission, short MSN, short Floor) {
        message_tools tool = new message_tools();
        this.header = this.messageheader("taken", SSRC_ID, "MCPT");
        tool.set_head2_length_buffer(granted_id, this.Granted_Party_id_field);
        this.Granted_Party_id_field = this.set_Granted_Party_id_field(granted_id);
        this.Permission_to_Request_field = new byte[4];
        this.Permission_to_Request_field = this.set_Permission_to_Request_field(permission);
        this.Message_Sequence_Number_field = new byte[4];
        this.Message_Sequence_Number_field = this.set_Message_Sequence_Number_field(MSN);
        this.Floor_Indicator_field = new byte[4];
        this.Floor_Indicator_field = this.set_Floor_Indicator_field(Floor);
        this.SSRC_field = new byte[4];
        this.SSRC_field = this.set_SSRC_field(SSRC_ID2);
        this.message = new byte[this.header.length + this.Granted_Party_id_field.length + this.Permission_to_Request_field.length + this.Message_Sequence_Number_field.length + this.Floor_Indicator_field.length + this.SSRC_field.length];
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
        while (j2 < this.Granted_Party_id_field.length) {
            this.message[i2 + j2] = this.Granted_Party_id_field[j2];
            ++j2;
        }
        i2 = this.header.length + this.Granted_Party_id_field.length;
        j2 = 0;
        while (j2 < this.Permission_to_Request_field.length) {
            this.message[i2 + j2] = this.Permission_to_Request_field[j2];
            ++j2;
        }
        i2 = this.header.length + this.Granted_Party_id_field.length + this.Permission_to_Request_field.length;
        j2 = 0;
        while (j2 < this.Message_Sequence_Number_field.length) {
            this.message[i2 + j2] = this.Message_Sequence_Number_field[j2];
            ++j2;
        }
        i2 = this.header.length + this.Granted_Party_id_field.length + this.Permission_to_Request_field.length + this.Message_Sequence_Number_field.length;
        j2 = 0;
        while (j2 < this.Floor_Indicator_field.length) {
            this.message[i2 + j2] = this.Floor_Indicator_field[j2];
            ++j2;
        }
        i2 = this.header.length + this.Granted_Party_id_field.length + this.Permission_to_Request_field.length + this.Message_Sequence_Number_field.length + this.Floor_Indicator_field.length;
        j2 = 0;
        while (j2 < this.SSRC_field.length) {
            this.message[i2 + j2] = this.SSRC_field[j2];
            ++j2;
        }
    }

    public void get_field(byte[] M) {
        int nexti;
        int i2 = nexti = 12;
        this.Granted_Party_id_field = new byte[M[nexti + 1] + 2];
        int j2 = 0;
        while (j2 < M[nexti + 1] + 2) {
            this.Granted_Party_id_field[j2] = M[nexti + j2];
            ++i2;
            ++j2;
        }
        nexti = i2;
        while (nexti % 4 != 0) {
            ++nexti;
        }
        this.Permission_to_Request_field = new byte[4];
        j2 = 0;
        while (j2 < 4) {
            this.Permission_to_Request_field[j2] = M[nexti + j2];
            ++i2;
            ++j2;
        }
        nexti = i2;
        while (nexti % 4 != 0) {
            ++nexti;
        }
        this.Message_Sequence_Number_field = new byte[4];
        j2 = 0;
        while (j2 < 4) {
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
    }

    public String get_Granted_Party_id_field(byte[] M) {
        if (M[0] != 4) {
            System.out.println("[rtcp_taken] Granted_Party_id_field type error");
        } else {
            byte[] Tmp = new byte[M[1]];
            int nexti = 2;
            int i2 = 0;
            while (i2 < M[1]) {
                Tmp[i2] = M[nexti + i2];
                ++i2;
            }
            this.Granted_id = new String(Tmp);
        }
        String granted_id = this.Granted_id;
        return granted_id;
    }

    public void get_Permission_to_Request_field(byte[] M) {
        if (M[0] != 5) {
            System.out.println("[rtcp_taken] Permission_to_Request_field type error");
        } else {
            this.permission_int = Byte.toUnsignedInt(M[3]);
        }
    }

    public void get_Message_Sequence_Number_field(byte[] M) {
        if (M[0] != 8) {
            System.out.println("[rtcp_taken] Message_Sequence_Number_field type error");
        } else {
            byte[] Tmp = new byte[2];
            int nexti = 2;
            int i2 = 0;
            while (i2 < 2) {
                Tmp[i2] = M[i2 + nexti];
                ++i2;
            }
            ByteBuffer b2 = ByteBuffer.wrap(Tmp);
            this.Message_Sequence_Number = b2.getShort();
        }
    }

    public short get_Floor_Indicator_field(byte[] M) {
        if (M[0] != 13) {
            System.out.println("[rtcp_taken] Floor_Indicator_field type error");
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
        return this.Floor_ind;
    }

    public void get_SSRC_field(byte[] M) {
        if (M[0] != 14) {
            System.out.println("[rtcp_taken] SSRC_field type error");
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
        this.get_Floor_Indicator_field(this.Floor_Indicator_field);
        this.get_Granted_Party_id_field(this.Granted_Party_id_field);
        this.get_Message_Sequence_Number_field(this.Message_Sequence_Number_field);
        this.get_Permission_to_Request_field(this.Permission_to_Request_field);
        this.get_SSRC_field(this.SSRC_field);
    }
}

