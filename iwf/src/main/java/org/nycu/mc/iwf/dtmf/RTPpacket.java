package org.nycu.mc.iwf.dtmf;

import java.util.Random;

public class RTPpacket {
    private static Random sRandom = new Random();
    private byte[] packet;
    private int packetLength;

    public RTPpacket(byte[] buffer, int event, boolean marker, boolean end, int duration) {
        this.packet = buffer;
        this.setVersion(2);
        if (marker) {
            this.setPayloadType(229, marker);
        } else {
            this.setPayloadType(101, marker);
        }
        this.setSequenceNumber(sRandom.nextInt());
        this.setSscr(sRandom.nextLong());
        if (end) {
            this.setDTMFevent(event, 1, 0, 10, duration);
        } else {
            this.setDTMFevent(event, 0, 0, 10, duration);
        }
    }

    public byte[] getRawPacket() {
        return this.packet;
    }

    public int getPacketLength() {
        return this.packetLength;
    }

    public int getHeaderLength() {
        return 12 + 4 * this.getCscrCount();
    }

    public int getPayloadLength() {
        return this.packetLength - this.getHeaderLength();
    }

    public void setPayloadLength(int length) {
        this.packetLength = this.getHeaderLength() + length;
    }

    public int getVersion() {
        return this.packet[0] >> 6 & 3;
    }

    public void setVersion(int v) {
        if (v > 3) {
            throw new RuntimeException("illegal version: " + v);
        }
        this.packet[0] = (byte)(this.packet[0] & 0x3F | (v & 3) << 6);
    }

    int getCscrCount() {
        return this.packet[0] & 0xF;
    }

    public int getPayloadType() {
        return this.packet[1] & 0x7F;
    }

    public void setPayloadType(int pt, boolean marker) {
        this.packet[1] = !marker ? (byte)(this.packet[1] & 0x80 | pt & 0x7F) : (byte)pt;
    }

    public int getSequenceNumber() {
        return (int)this.get(2, 2);
    }

    public void setSequenceNumber(int sn) {
        this.set(sn, 2, 2);
    }

    public long getTimestamp() {
        return this.get(4, 4);
    }

    public void setTimestamp(long timestamp) {
        this.set(timestamp, 4, 4);
    }

    void setSscr(long ssrc) {
        this.set(ssrc, 8, 4);
    }

    private long get(int begin, int length) {
        long n2 = 0L;
        int i2 = begin;
        int end = i2 + length;
        while (i2 < end) {
            n2 = n2 << 8 | (long)this.packet[i2] & 0xFFL;
            ++i2;
        }
        return n2;
    }

    private void set(long n2, int begin, int length) {
        int i2 = begin + length - 1;
        while (i2 >= begin) {
            this.packet[i2] = (byte)(n2 & 0xFFL);
            n2 >>= 8;
            --i2;
        }
    }

    public void setDTMFevent(int event, int E, int R, int volume, int duration) {
        this.set(event, 12, 1);
        this.packet[13] = (byte) (E != 0 ? -118 : 10);
        this.set(duration, 14, 2);
    }
}

