package org.nycu.mc.iwf.RTCP;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Random;


public class Rtcp {
    public byte[] header;
    public byte[] databuffer;
    public byte connect = (byte)-112;
    public byte acknowledgement = (byte)-126;
    public byte disconnect = (byte)-111;
    public byte granted = (byte)-127;
    public byte taken = (byte)-126;
    public byte ack = (byte)-118;
    public byte deny = (byte)-125;
    public byte request = (byte)-128;
    public byte release = (byte)-124;
    public byte idle = (byte)-123;
    public byte revoke = (byte)-122;
    public byte queue_position_request = (byte)-120;
    public byte queue_position_info = (byte)-119;
    public byte unknown = 0;
    public static final int RTCP_UNKNOWN = -100;
    public static final int RTCP_MCPC_CONNECT = 16;
    public static final int RTCP_MCPC_DISCONNECT = 17;
    public static final int RTCP_MCPC_ACK = 2;
    public static final int RTCP_MCPT_REQUEST = 0;
    public static final int RTCP_MCPT_GRANTED = 1;
    public static final int RTCP_MCPT_TAKEN = 2;
    public static final int RTCP_MCPT_DENY = 3;
    public static final int RTCP_MCPT_RELEASE = 4;
    public static final int RTCP_MCPT_IDLE = 5;
    public static final int RTCP_MCPT_REVOKE = 6;
    public static final int RTCP_MCPT_QUEUE_POSITION_REQUEST = 8;
    public static final int RTCP_MCPT_QUEUE_POSITION_INFO = 9;
    public static final int RTCP_MCPT_ACK = 10;
    public int ptt_state = -100;
    public static final int DENY_REASON_ANOTHER_HAS_PERMISSION = 1;
    public static final int DENY_REASON_INTERNAL_ERROR = 2;
    public static final int DENY_REASON_ONLY_ONE_PARTICIPANT = 3;
    public static final int DENY_REASON_RETRY_AFTER_TIMER_HAS_NOT_EXPIRED = 4;
    public static final int DENY_REASON_RECEIVE_ONLY = 5;
    public static final int DENY_REASON_NO_RESOURCE_AVAILABLE = 6;
    public static final int DENY_REASON_QUEUE_FULL = 7;
    public static final int REVOKE_REASON_ONLY_ONE_CLIENT = 1;
    public static final int REVOKE_REASON_MEDIA_BURST_TOO_LONG = 2;
    public static final int REVOKE_REASON_NO_PERMISSION = 3;
    public static final int REVOKE_REASON_PREEMPTED = 4;
    public static final int REVOKE_REASON_NO_RESOURCE_AVAILABLE = 6;
    public static final int REVOKE_REASON_OTHER_REASON = 255;
    public byte V2;
    public byte P;
    public byte subtype;
    public short pt_app;
    public byte[] length_field;
    public short Message_length;
    public long SSRC;
    public String name;
    public byte[] msgbuf;
    private static short rtcp_sequence_num = 1;

    public Rtcp() {
        this.init();
    }

    private void init() {
        this.V2 = 0;
        this.P = 0;
        this.pt_app = 0;
        this.SSRC = this.gen_ssrc();
        this.ptt_state = this.unknown;
    }

    public Long gen_ssrc() {
        long ssrc = (long)Math.random();
        Random rand = new Random();
        ssrc = rand.nextLong();
        if (ssrc < 0L) {
            ssrc *= -1L;
        }
        return ssrc;
    }

    public byte[] messageheader(String ms_type, long SSRC_uid, String name) {
        this.header = new byte[12];
        this.set_typeinfo(ms_type);
        this.header[1] = -52;
        this.set_header_SSRC_field(SSRC_uid);
        this.set_name(name);
        return this.header;
    }

    public void set_header_SSRC_field(long id) {
        byte[] SSRC = this.set_SSRC(id);
        int i2 = 0;
        while (i2 < 4) {
            this.header[4 + i2] = SSRC[i2];
            ++i2;
        }
    }

    public byte[] set_SSRC(long id) {
        byte[] SSRC = new byte[]{(byte)(id >> 24 & 0xFFL), (byte)(id >> 16 & 0xFFL), (byte)(id >> 8 & 0xFFL), (byte)(id >> 0 & 0xFFL)};
        return SSRC;
    }

    public void set_name(String str) {
        byte[] info = str.getBytes();
        int i2 = 0;
        while (i2 < 4) {
            this.header[8 + i2] = info[i2];
            ++i2;
        }
    }

    public void set_typeinfo(String str) {
        rtcp_types type = rtcp_types.valueOf(str);
        switch (type) {
            case connect: {
                this.header[0] = this.connect;
                break;
            }
            case granted: {
                this.header[0] = this.granted;
                break;
            }
            case taken: {
                this.header[0] = this.taken;
                break;
            }
            case ack: {
                this.header[0] = this.ack;
                break;
            }
            case acknowledgement: {
                this.header[0] = this.acknowledgement;
                break;
            }
            case disconnect: {
                this.header[0] = this.disconnect;
                break;
            }
            case deny: {
                this.header[0] = this.deny;
                break;
            }
            case request: {
                this.header[0] = this.request;
                break;
            }
            case release: {
                this.header[0] = this.release;
                break;
            }
            case idle: {
                this.header[0] = this.idle;
                break;
            }
            case revoke: {
                this.header[0] = this.revoke;
                break;
            }
            case queue_position_request: {
                this.header[0] = this.queue_position_request;
                break;
            }
            case queue_position_info: {
                this.header[0] = this.queue_position_info;
            }
        }
    }

    public void Rtcp_parse(byte[] Data2) {
        this.msgbuf = new byte[2048];
        System.arraycopy(Data2, 0, this.msgbuf, 0, Math.min(Data2.length, this.msgbuf.length));
        this.get_header(Data2);
        if (this.name.equals("MCPC")) {
            switch (this.subtype) {
                case 16: {
                    this.ptt_state = 16;
                    break;
                }
                case 17: {
                    this.ptt_state = 17;
                    break;
                }
                case 2: {
                    this.ptt_state = 2;
                    break;
                }
                default: {
                    System.out.println("\nV: ERROR state.\n");
                    break;
                }
            }
        } else if (this.name.equals("MCPT")) {
            switch (this.subtype) {
                case 0: {
                    this.ptt_state = 0;
                    break;
                }
                case 1: {
                    this.ptt_state = 1;
                    break;
                }
                case 2: {
                    this.ptt_state = 2;
                    break;
                }
                case 3: {
                    this.ptt_state = 3;
                    break;
                }
                case 4: {
                    this.ptt_state = 4;
                    break;
                }
                case 5: {
                    this.ptt_state = 5;
                    break;
                }
                case 6: {
                    this.ptt_state = 6;
                    break;
                }
                case 8: {
                    this.ptt_state = 8;
                    break;
                }
                case 9: {
                    this.ptt_state = 9;
                    break;
                }
                case 10: {
                    this.ptt_state = 10;
                    break;
                }
                default: {
                    System.out.println("\nV: ERROR state.\n");
                }
            }
        }
    }

    public void get_header(byte[] message) {
        byte t = message[0];
        byte[] Tmp = new byte[4];
        byte tmp = (byte)(t >> 6);
        this.V2 = tmp = (byte)(tmp & 3);
        tmp = (byte)(t >> 5);
        this.P = tmp = (byte)(tmp & 1);
        this.subtype = t = (byte)(t & 0x1F);
        this.pt_app = Rtcp.byteArrayToShort((byte)0, message[1]);
        this.Message_length = Rtcp.byteArrayToShort(message[2], message[3]);
        int i2 = 0;
        while (i2 < 4) {
            Tmp[i2] = message[4 + i2];
            ++i2;
        }
        byte[] body = new byte[8];
        byte[] SSRC_body = new byte[4];
        byte[] name_body = new byte[4];
        System.arraycopy(message, 4, body, 0, 8);
        System.arraycopy(body, 4, name_body, 0, 4);
        this.SSRC = body[0] & 0xFF;
        this.SSRC |= (long)(body[1] << 8 & 0xFF00);
        this.SSRC |= (long)(body[2] << 16 & 0xFF0000);
        this.SSRC |= (long)(body[3] << 24 & 0xFF000000);
        this.name = new String(name_body);
    }

    public static short byteArrayToShort(byte l1, byte l2) {
        return (short)(l2 & 0xFF | (l1 & 0xFF) << 8);
    }

    public byte[] set_Invite_MCU_field(String id) {
        int buffer = id.length() + 2;
        while (buffer % 4 != 0) {
            ++buffer;
        }
        byte[] MCU = new byte[buffer];
        byte[] byteArr = id.getBytes();
        MCU[0] = 5;
        MCU[1] = (byte)id.length();
        int i2 = 0;
        while (i2 < id.length()) {
            MCU[i2 + 2] = byteArr[i2];
            ++i2;
        }
        return MCU;
    }

    public byte[] set_Session_id_field(String id) {
        int buffer = id.length() + 3;
        while (buffer % 4 != 0) {
            ++buffer;
        }
        byte[] Session_id = new byte[buffer];
        byte[] byteArr = id.getBytes();
        Session_id[0] = 1;
        Session_id[1] = (byte)id.length();
        Session_id[2] = 3;
        int i2 = 0;
        while (i2 < id.length()) {
            Session_id[i2 + 3] = byteArr[i2];
            ++i2;
        }
        return Session_id;
    }

    public byte[] set_Media_Streams_field(int media, int channel) {
        int buffer = 4;
        byte[] Media_Streams = new byte[buffer];
        Media_Streams[0] = 0;
        Media_Streams[1] = 2;
        Media_Streams[2] = (byte)media;
        Media_Streams[3] = (byte)channel;
        return Media_Streams;
    }

    public byte[] set_Answer_state_field(int answer) {
        int buffer = 4;
        byte[] Answer_state = new byte[buffer];
        Answer_state[0] = 4;
        Answer_state[1] = 2;
        Answer_state[2] = 0;
        Answer_state[3] = (byte)answer;
        return Answer_state;
    }

    public byte[] set_Source_field(int source) {
        int buffer = 4;
        byte[] Source = new byte[buffer];
        Source[0] = 10;
        Source[1] = 2;
        Source[2] = 0;
        Source[3] = (byte)source;
        return Source;
    }

    public byte[] set_Message_Type_id_field() {
        int buffer = 4;
        byte[] MsT = new byte[buffer];
        MsT[0] = 12;
        MsT[1] = 2;
        MsT[2] = 10;
        MsT[3] = 0;
        return MsT;
    }

    public byte[] set_Granted_Party_id_field(String id) {
        int buffer = id.length() + 2;
        while (buffer % 4 != 0) {
            ++buffer;
        }
        byte[] grant = new byte[buffer];
        byte[] byteArr = id.getBytes();
        grant[0] = 4;
        grant[1] = (byte)id.length();
        int i2 = 0;
        while (i2 < id.length()) {
            grant[i2 + 2] = byteArr[i2];
            ++i2;
        }
        return grant;
    }

    public byte[] set_Permission_to_Request_field(int permission) {
        int buffer = 4;
        byte[] P2r = new byte[buffer];
        P2r[0] = 5;
        P2r[1] = 2;
        P2r[2] = 0;
        P2r[3] = (byte)permission;
        return P2r;
    }

    public byte[] set_Message_Sequence_Number_field(short num) {
        int buffer = 4;
        ByteBuffer b2 = ByteBuffer.allocate(2);
        b2.putShort(num);
        byte[] byteArr = b2.array();
        byte[] MSn = new byte[buffer];
        MSn[0] = 8;
        MSn[1] = 2;
        MSn[2] = byteArr[0];
        MSn[3] = byteArr[1];
        return MSn;
    }

    public byte[] set_Floor_Indicator_field(short type) {
        int buffer = 4;
        ByteBuffer b2 = ByteBuffer.allocate(2);
        b2.putShort(type);
        byte[] byteArr = b2.array();
        byte[] MSn = new byte[buffer];
        MSn[0] = 13;
        MSn[1] = 2;
        MSn[2] = byteArr[0];
        MSn[3] = byteArr[1];
        return MSn;
    }

    public byte[] set_SSRC_field(long id) {
        int buffer = 8;
        byte[] SSRC = new byte[buffer];
        SSRC[0] = 14;
        SSRC[1] = 4;
        SSRC[2] = (byte)(id >> 24 & 0xFFL);
        SSRC[3] = (byte)(id >> 16 & 0xFFL);
        SSRC[4] = (byte)(id >> 8 & 0xFFL);
        SSRC[5] = (byte)(id >> 0 & 0xFFL);
        return SSRC;
    }

    public byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(64);
        buffer.putLong(x);
        System.out.println(buffer);
        return buffer.array();
    }

    public static byte[] getBitsArray(byte b2) {
        byte[] array = new byte[8];
        int i2 = 7;
        while (i2 >= 0) {
            array[i2] = (byte)(b2 & 1);
            b2 = (byte)(b2 >> 1);
            --i2;
        }
        return array;
    }

    public byte[] set_Floor_Priority_field(int priority) {
        int buffer = 4;
        byte[] FP = new byte[buffer];
        FP[0] = 0;
        FP[1] = 2;
        FP[2] = (byte)priority;
        FP[3] = 0;
        return FP;
    }

    public byte[] set_Duration_field(short time) {
        int buffer = 4;
        byte[] Df = new byte[buffer];
        ByteBuffer b2 = ByteBuffer.allocate(2);
        b2.putShort(time);
        byte[] byteArr = b2.array();
        Df[0] = 1;
        Df[1] = 2;
        int i2 = 0;
        while (i2 < 2) {
            Df[i2 + 2] = byteArr[i2];
            ++i2;
        }
        return Df;
    }

    public void set_Queued_User_id_buffer(List<String> send_uri, byte[] Queued_User_id_field) {
        int total = 0;
        int length = 0;
        int buffer = 0;
        for (String str : send_uri) {
            ++total;
            length += str.length();
        }
        buffer = length + total + 3;
        while (buffer % 4 != 0) {
            ++buffer;
        }
        Queued_User_id_field = new byte[buffer];
    }

    public byte[] set_Queued_User_id_field(List<String> send_uri) {
        int total = 0;
        int leng = 0;
        int buffer = 0;
        for (String str : send_uri) {
            ++total;
            leng += str.length();
        }
        buffer = leng + total + 3;
        while (buffer % 4 != 0) {
            ++buffer;
        }
        byte[] Queued_id = new byte[buffer];
        Queued_id[0] = 2;
        Queued_id[1] = (byte)buffer;
        Queued_id[2] = (byte)total;
        int i2 = 3;
        for (String str : send_uri) {
            int limit = i2 + str.length();
            byte[] byteArr = str.getBytes();
            while (i2 < limit) {
                Queued_id[i2] = (byte)str.length();
                int j2 = 0;
                while (j2 < str.length()) {
                    Queued_id[i2 + j2 + 1] = byteArr[j2];
                    ++j2;
                }
                i2 = i2 + str.length() + 1;
            }
        }
        return Queued_id;
    }

    public static void increaseSequenceNum() {
        if ((rtcp_sequence_num = (short)(rtcp_sequence_num + 1)) == 0) {
            rtcp_sequence_num = 1;
        }
    }

    public static short get_sequence_num() {
        return rtcp_sequence_num;
    }

    static enum rtcp_types {
        connect,
        disconnect,
        acknowledgement,
        granted,
        taken,
        ack,
        deny,
        request,
        release,
        idle,
        revoke,
        queue_position_request,
        queue_position_info;

    }
}
