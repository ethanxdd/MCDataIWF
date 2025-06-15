package org.nycu.mc.iwf.xml;

public class Character {
    private char[] ch;
    private int start;
    private int length;

    public Character(char[] ch, int start, int length) {
        this.ch = ch;
        this.start = start;
        this.length = length;
    }

    public char[] getCh() {
        return this.ch;
    }

    public int getStart() {
        return this.start;
    }

    public int getLength() {
        return this.length;
    }
}

