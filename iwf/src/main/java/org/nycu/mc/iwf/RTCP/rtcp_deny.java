package org.nycu.mc.iwf.RTCP;

import java.nio.ByteBuffer;

public class rtcp_deny
extends Rtcp {
    public byte[] message;
    public byte[] header;
    public byte[] User_ID_field;
    public byte[] Floor_Indicator_field;
    public byte[] Reject_Cause_field;
    public String User_id;
    public int Floor_Indicator;
    public int Reject_Cause;
    public String Reject_Phrase;

    public rtcp_deny() {
    }

    public rtcp_deny(long SSRC_ID, int reject, String user_id, short Floor) {
        message_tools tool = new message_tools();
        this.header = this.messageheader("deny", SSRC_ID, "MCPT");
        String phrase = this.get_reject_phrase(reject);
        this.Reject_Cause_field = this.set_Reject_Cause_field(reject, phrase);
        this.set_Reject_Cause_field_buffer(phrase, this.Reject_Cause_field);
        tool.set_head2_length_buffer(user_id, this.User_ID_field);
        this.User_ID_field = this.set_User_ID_field(user_id);
        this.Floor_Indicator_field = new byte[4];
        this.Floor_Indicator_field = this.set_Floor_Indicator_field(Floor);
        this.message = new byte[this.header.length + this.Reject_Cause_field.length + this.User_ID_field.length + this.Floor_Indicator_field.length];
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
        while (j2 < this.Reject_Cause_field.length) {
            this.message[i2 + j2] = this.Reject_Cause_field[j2];
            ++j2;
        }
        i2 = this.header.length + this.Reject_Cause_field.length;
        j2 = 0;
        while (j2 < this.User_ID_field.length) {
            this.message[i2 + j2] = this.User_ID_field[j2];
            ++j2;
        }
        i2 = this.header.length + this.Reject_Cause_field.length + this.User_ID_field.length;
        j2 = 0;
        while (j2 < this.Floor_Indicator_field.length) {
            this.message[i2 + j2] = this.Floor_Indicator_field[j2];
            ++j2;
        }
    }

    public byte[] set_Reject_Cause_field(int reject, String phrase) {
        int buffer = phrase.length() + 4;
        while (buffer % 4 != 0) {
            ++buffer;
        }
        byte[] reject_cause = new byte[buffer];
        ByteBuffer b2 = ByteBuffer.allocate(4);
        b2.putInt(reject);
        byte[] byteArr = b2.array();
        reject_cause[0] = 2;
        reject_cause[1] = (byte)(phrase.length() + 2);
        reject_cause[2] = byteArr[2];
        reject_cause[3] = byteArr[3];
        int i2 = 0;
        while (i2 < phrase.length()) {
            reject_cause[4 + i2] = (byte)phrase.charAt(i2);
            ++i2;
        }
        return reject_cause;
    }

    public String get_reject_phrase(int reject) {
        switch (reject) {
            case 1: {
                return "Another MCPTT client has permission";
            }
            case 2: {
                return "Internal floor control server error";
            }
            case 3: {
                return "Only one participant";
            }
            case 4: {
                return "Retry-after timer has not expired";
            }
            case 5: {
                return "Receive only";
            }
            case 6: {
                return "No resources available";
            }
            case 7: {
                return "Queue full";
            }
            case 255: {
                return "Other reason";
            }
        }
        return "cannot find this reject phrase";
    }

    public void set_Reject_Cause_field_buffer(String phrase, byte[] reject_cause_field) {
        int length = phrase.length();
        int buffer = 0;
        buffer = length + 4;
        while (buffer % 4 != 0) {
            ++buffer;
        }
        reject_cause_field = new byte[buffer];
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
        this.Reject_Cause_field = new byte[M[nexti + 1] + 2];
        int j2 = 0;
        while (j2 < M[nexti + 1] + 2) {
            this.Reject_Cause_field[j2] = M[nexti + j2];
            ++i2;
            ++j2;
        }
        nexti = i2;
        while (nexti % 4 != 0) {
            ++nexti;
        }
        this.User_ID_field = new byte[M[nexti + 1] + 2];
        j2 = 0;
        while (j2 < M[nexti + 1] + 2) {
            this.User_ID_field[j2] = M[nexti + j2];
            ++i2;
            ++j2;
        }
        nexti = i2;
        while (nexti % 4 != 0) {
            ++nexti;
        }
        this.Floor_Indicator_field = new byte[M[nexti + 1] + 4];
        j2 = 0;
        while (j2 < M[nexti + 1] + 4) {
            this.Floor_Indicator_field[j2] = M[nexti + j2 + 4];
            ++i2;
            ++j2;
        }
    }

    public void get_Reject_Cause_field(byte[] M) {
        if (M[0] != 2) {
            System.out.println("Reject_Cause_field type error");
        } else {
            byte[] Tmp = new byte[4];
            int nexti = 2;
            int i2 = 0;
            while (i2 < 2) {
                Tmp[i2] = M[i2 + nexti];
                ++i2;
            }
            ByteBuffer b2 = ByteBuffer.wrap(Tmp);
            this.Reject_Cause = b2.getShort();
            nexti = 4;
            Tmp = new byte[M[1] - 2];
            int i3 = 0;
            while (i3 < M[1] - 2) {
                Tmp[i3] = M[nexti + i3];
                ++i3;
            }
            this.Reject_Phrase = new String(Tmp);
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
            this.Floor_Indicator = b2.getShort();
        }
    }

    public void parse_all_field(byte[] data) {
        this.get_field(data);
        this.get_Reject_Cause_field(this.Reject_Cause_field);
        this.get_User_ID_field(this.User_ID_field);
        this.get_Floor_Indicator_field(this.Floor_Indicator_field);
    }
}

