package org.nycu.mc.iwf.RTCP;

import java.nio.ByteBuffer;

public class rtcp_queue_position_info
extends Rtcp {
    public byte[] message;
    public byte[] User_ID_field;
    public byte[] Queue_Info_field;
    public byte[] Floor_Indicator_field;
    public int Position_info;
    public int Priority_int;
    public short Floor_Indicator_number;
    public String User_id;

    public rtcp_queue_position_info() {
    }

    public rtcp_queue_position_info(long SSRC_ID, String user_id, int queue_position_info, int queue_priority, short indicator) {
        message_tools tool = new message_tools();
        this.header = this.messageheader("queue_position_info", SSRC_ID, "MCPT");
        tool.set_head2_length_buffer(user_id, this.User_ID_field);
        this.User_ID_field = this.set_User_ID_field(user_id);
        this.Queue_Info_field = new byte[4];
        this.Queue_Info_field = this.set_Queue_Info_field(queue_position_info, queue_priority);
        this.Floor_Indicator_field = new byte[4];
        this.Floor_Indicator_field = this.set_Floor_Indicator_field(indicator);
        this.message = new byte[this.header.length + this.User_ID_field.length + this.Queue_Info_field.length + this.Floor_Indicator_field.length];
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
        while (j2 < this.User_ID_field.length) {
            this.message[i2 + j2] = this.User_ID_field[j2];
            ++j2;
        }
        i2 = this.header.length + this.User_ID_field.length;
        j2 = 0;
        while (j2 < this.Queue_Info_field.length) {
            this.message[i2 + j2] = this.Queue_Info_field[j2];
            ++j2;
        }
        i2 = this.header.length + this.User_ID_field.length + this.Queue_Info_field.length;
        j2 = 0;
        while (j2 < this.Floor_Indicator_field.length) {
            this.message[i2 + j2] = this.Floor_Indicator_field[j2];
            ++j2;
        }
    }

    public byte[] set_Queue_Info_field(int position_info, int priority) {
        int buffer = 4;
        byte[] FP = new byte[buffer];
        FP[0] = 3;
        FP[1] = 2;
        FP[2] = (byte)position_info;
        FP[3] = (byte)priority;
        return FP;
    }

    public byte[] set_User_ID_field(String id) {
        int buffer = id.length() + 2;
        while (buffer % 4 != 0) {
            ++buffer;
        }
        byte[] user = new byte[buffer];
        byte[] byteArr = id.getBytes();
        user[0] = 6;
        user[1] = (byte)id.length();
        int i2 = 0;
        while (i2 < id.length()) {
            user[i2 + 2] = byteArr[i2];
            ++i2;
        }
        return user;
    }

    public void get_field(byte[] M) {
        int nexti;
        int i2 = nexti = 12;
        this.User_ID_field = new byte[M[nexti + 1] + 2];
        int j2 = 0;
        while (j2 < M[nexti + 1] + 2) {
            this.User_ID_field[j2] = M[nexti + j2];
            ++i2;
            ++j2;
        }
        nexti = i2;
        while (nexti % 4 != 0) {
            ++nexti;
        }
        this.Queue_Info_field = new byte[4];
        j2 = 0;
        while (j2 < 4) {
            this.Queue_Info_field[j2] = M[nexti + j2];
            ++i2;
            ++j2;
        }
        nexti = i2;
        while (nexti % 4 != 0) {
            ++nexti;
        }
        this.Floor_Indicator_field = new byte[4];
        j2 = 0;
        while (j2 < 4) {
            this.Floor_Indicator_field[j2] = M[nexti + j2];
            ++i2;
            ++j2;
        }
    }

    public void get_User_ID_field(byte[] M) {
        if (M[0] != 6) {
            System.out.println("User_id_field type error");
        } else {
            byte[] Tmp = new byte[M[1]];
            int nexti = 2;
            int i2 = 0;
            while (i2 < M[1]) {
                Tmp[i2] = M[nexti + i2];
                ++i2;
            }
            this.User_id = new String(Tmp);
            System.out.println("user uri = " + this.User_id);
        }
    }

    public void get_Queue_info_field(byte[] M) {
        int check = Byte.toUnsignedInt(M[0]);
        if (check != 3) {
            System.out.println("Floor_Priority_field type error");
        } else {
            this.Position_info = Byte.toUnsignedInt(M[2]);
            this.Priority_int = Byte.toUnsignedInt(M[3]);
        }
        this.Position_info = Byte.toUnsignedInt(M[2]);
        this.Priority_int = Byte.toUnsignedInt(M[3]);
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
            this.Floor_Indicator_number = b2.getShort();
        }
    }

    public void parse_all_field(byte[] data) {
        this.get_field(data);
        this.get_User_ID_field(this.User_ID_field);
        this.get_Queue_info_field(this.Queue_Info_field);
        this.get_Floor_Indicator_field(this.Floor_Indicator_field);
    }
}

