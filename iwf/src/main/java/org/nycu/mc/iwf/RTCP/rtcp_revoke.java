package org.nycu.mc.iwf.RTCP;

import java.nio.ByteBuffer;



public class rtcp_revoke
extends Rtcp {
    public byte[] message;
    public byte[] Reject_Cause_field;
    public byte[] Floor_Indicator_field;
    public int Source_id;
    public int Message_id;
    public int Reject_Cause_number;
    public String Reject_Phrase;
    public short Floor_Indicator_number;

    public rtcp_revoke() {
    }

    public rtcp_revoke(long SSRC_ID, short reject_cause, short indicator) {
        message_tools tool = new message_tools();
        this.header = this.messageheader("revoke", SSRC_ID, "MCPT");
        String phrase = this.get_reject_phrase(reject_cause);
        this.set_Reject_Cause_field_buffer(phrase, this.Reject_Cause_field);
        this.Reject_Cause_field = this.set_Reject_Cause_field(reject_cause, phrase);
        this.Floor_Indicator_field = new byte[4];
        this.Floor_Indicator_field = this.set_Floor_Indicator_field(indicator);
        this.message = new byte[this.header.length + this.Reject_Cause_field.length + this.Floor_Indicator_field.length];
        tool.length_transf(this.header, this.message.length);
        this.writemessage();
    }

    public byte[] set_Reject_Cause_field(short reject, String phrase) {
        int buffer = phrase.length() + 4;
        while (buffer % 4 != 0) {
            ++buffer;
        }
        byte[] reject_cause = new byte[buffer];
        ByteBuffer b2 = ByteBuffer.allocate(2);
        b2.putShort(reject);
        byte[] byteArr = b2.array();
        reject_cause[0] = 2;
        reject_cause[1] = (byte)(phrase.length() + 2);
        reject_cause[2] = byteArr[0];
        reject_cause[3] = byteArr[1];
        int i2 = 0;
        while (i2 < phrase.length()) {
            reject_cause[4 + i2] = (byte)phrase.charAt(i2);
            ++i2;
        }
        return reject_cause;
    }

    public String get_reject_phrase(short reject) {
        switch (reject) {
            case 1: {
                return " Only one MCPTT client";
            }
            case 2: {
                return "Media burst too long";
            }
            case 3: {
                return "No permission to send a Media Burst";
            }
            case 4: {
                return "Media Burst pre-empted";
            }
            case 6: {
                return "No resources available";
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
        while (j2 < this.Floor_Indicator_field.length) {
            this.message[i2 + j2] = this.Floor_Indicator_field[j2];
            ++j2;
        }
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
        this.Floor_Indicator_field = new byte[M[nexti + 1] + 2];
        j2 = 0;
        while (j2 < M[nexti + 1] + 2) {
            this.Floor_Indicator_field[j2] = M[nexti + j2];
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
            this.Reject_Cause_number = b2.getShort();
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
        this.get_Floor_Indicator_field(this.Floor_Indicator_field);
        this.get_Reject_Cause_field(this.Reject_Cause_field);
    }
}

