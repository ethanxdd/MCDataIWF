package org.nycu.mc.iwf.RTCP;

public class rtcp_queue_position_request
extends Rtcp {
    public byte[] message;
    public byte[] User_ID_field;
    public String User_id;

    public rtcp_queue_position_request() {
    }

    public rtcp_queue_position_request(long SSRC_ID, String user_id) {
        message_tools tool = new message_tools();
        this.header = this.messageheader("queue_position_request", SSRC_ID, "MCPT");
        tool.set_head2_length_buffer(user_id, this.User_ID_field);
        this.User_ID_field = this.set_User_ID_field(user_id);
        this.message = new byte[this.header.length + this.User_ID_field.length];
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
        int nexti = 12;
        this.User_ID_field = new byte[M[nexti + 1] + 2];
        int j2 = 0;
        while (j2 < M[nexti + 1] + 2) {
            this.User_ID_field[j2] = M[nexti + j2];
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

    public void parse_allfield(byte[] Data2) {
        this.get_field(Data2);
        this.get_header(Data2);
        this.get_User_ID_field(this.User_ID_field);
    }
}

