package org.nycu.mc.iwf.RTCP;

public class rtcp_connect
extends Rtcp {
    public static byte[] message;
    public byte[] Session_id_field;
    public byte[] Media_Streams_field;
    public byte[] Answer_state_field;
    public byte[] Invite_MCU_field;
    public String MCS_ID;
    public String Session_id;
    public int Media_stream;
    public int control_channel;
    public int Session_type;
    public int answer_state;
    public String Invite_MCU;

    public rtcp_connect() {
    }

    public rtcp_connect(long SSRC_ID, String MCU_ID, String session_id, int media, int channel, int answer) {
        message_tools tool = new message_tools();
        this.header = this.messageheader("connect", SSRC_ID, "MCPC");
        tool.set_head3_length_buffer(session_id, this.Session_id_field);
        this.Session_id_field = this.set_Session_id_field(session_id);
        this.Media_Streams_field = new byte[4];
        this.Media_Streams_field = this.set_Media_Streams_field(media, channel);
        this.Answer_state_field = new byte[4];
        this.Answer_state_field = this.set_Answer_state_field(answer);
        tool.set_head2_length_buffer(MCU_ID, this.Invite_MCU_field);
        this.Invite_MCU_field = this.set_Invite_MCU_field(MCU_ID);
        message = new byte[this.header.length + this.Session_id_field.length + this.Media_Streams_field.length + this.Answer_state_field.length + this.Invite_MCU_field.length];
        tool.length_transf(this.header, message.length);
        this.writemessage();
    }

    public void writemessage() {
        int i2 = 0;
        while (i2 < this.header.length) {
            rtcp_connect.message[i2] = this.header[i2];
            ++i2;
        }
        i2 = this.header.length;
        int j2 = 0;
        while (j2 < this.Session_id_field.length) {
            rtcp_connect.message[i2 + j2] = this.Session_id_field[j2];
            ++j2;
        }
        i2 = this.header.length + this.Session_id_field.length;
        j2 = 0;
        while (j2 < this.Media_Streams_field.length) {
            rtcp_connect.message[i2 + j2] = this.Media_Streams_field[j2];
            ++j2;
        }
        i2 = this.header.length + this.Session_id_field.length + this.Media_Streams_field.length;
        j2 = 0;
        while (j2 < this.Answer_state_field.length) {
            rtcp_connect.message[i2 + j2] = this.Answer_state_field[j2];
            ++j2;
        }
        i2 = this.header.length + this.Session_id_field.length + this.Media_Streams_field.length + this.Answer_state_field.length;
        j2 = 0;
        while (j2 < this.Invite_MCU_field.length) {
            rtcp_connect.message[i2 + j2] = this.Invite_MCU_field[j2];
            ++j2;
        }
    }

    public void get_field(byte[] M) {
        int nexti;
        int i2 = nexti = 12;
        this.Session_id_field = new byte[M[nexti + 1] + 3];
        int j2 = 0;
        while (j2 < M[nexti + 1] + 3) {
            this.Session_id_field[j2] = M[nexti + j2];
            ++i2;
            ++j2;
        }
        nexti = i2;
        while (nexti % 4 != 0) {
            ++nexti;
        }
        this.Media_Streams_field = new byte[4];
        j2 = 0;
        while (j2 < 4) {
            this.Media_Streams_field[j2] = M[nexti + j2];
            ++i2;
            ++j2;
        }
        nexti = i2;
        while (nexti % 4 != 0) {
            ++nexti;
        }
        this.Answer_state_field = new byte[4];
        j2 = 0;
        while (j2 < 4) {
            this.Answer_state_field[j2] = M[nexti + j2];
            ++i2;
            ++j2;
        }
        nexti = i2;
        while (nexti % 4 != 0) {
            ++nexti;
        }
        this.Invite_MCU_field = new byte[M[nexti + 1] + 2];
        j2 = 0;
        while (j2 < M[nexti + 1] + 2) {
            this.Invite_MCU_field[j2] = M[nexti + j2];
            ++i2;
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
            this.Session_id = new String(Tmp);
        }
    }

    public void get_Media_Streams_field(byte[] M) {
        if (M[0] != 0) {
            System.out.println("Media_Streams_field type error");
        } else {
            this.Media_stream = Byte.toUnsignedInt(M[2]);
            this.control_channel = Byte.toUnsignedInt(M[3]);
        }
    }

    public void get_Answer_state_field(byte[] M) {
        if (M[0] != 4) {
            System.out.println("Answer_state_field type error");
        } else {
            this.answer_state = Byte.toUnsignedInt(M[3]);
        }
    }

    public void get_Invite_MCU_field(byte[] M) {
        if (M[0] != 5) {
            System.out.println("Invite_MCU_field type error");
        } else {
            byte[] Tmp = new byte[M[1]];
            int nexti = 2;
            int i2 = 0;
            while (i2 < M[1]) {
                Tmp[i2] = M[nexti + i2];
                ++i2;
            }
            this.Invite_MCU = new String(Tmp);
        }
    }

    public void parse_all_field(byte[] data) {
        this.get_field(data);
        this.get_Answer_state_field(this.Answer_state_field);
        this.get_Invite_MCU_field(this.Invite_MCU_field);
        this.get_Session_id_field(this.Session_id_field);
    }
}


