/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.openwire.buffer;

import java.io.DataInput;
import java.io.IOException;
import java.io.InputStream;
import java.io.UTFDataFormatException;

/**
 * Optimized ByteArrayInputStream that can be used more than once
 */
public final class DataByteArrayInputStream extends InputStream implements DataInput {

    private byte[] buf;
    private int pos;
    private int offset;
    private int length;

    /**
     * Creates a <code>StoreByteArrayInputStream</code>.
     *
     * @param buf
     *        the input buffer.
     */
    public DataByteArrayInputStream(byte buf[]) {
        restart(buf);
    }

    /**
     * Creates a <code>StoreByteArrayInputStream</code>.
     *
     * @param buffer
     *        the input buffer.
     */
    public DataByteArrayInputStream(Buffer buffer) {
        restart(buffer);
    }

    /**
     * reset the <code>StoreByteArrayInputStream</code> to use an new Buffer
     *
     * @param buffer
     */
    public void restart(Buffer buffer) {
        this.buf = buffer.getData();
        this.offset = buffer.getOffset();
        this.pos = this.offset;
        this.length = buffer.getLength();
    }

    /**
     * re-start the input stream - reusing the current buffer
     *
     * @param size
     */
    public void restart(int size) {
        if (buf == null || buf.length < size) {
            buf = new byte[size];
        }
        restart(buf);
        this.length = size;
    }

    /**
     * Creates <code>WireByteArrayInputStream</code> with a minmalist byte array
     */
    public DataByteArrayInputStream() {
        this(new byte[0]);
    }

    /**
     * @return the size
     */
    public int size() {
        return pos - offset;
    }

    /**
     * @return the underlying data array
     */
    public byte[] getRawData() {
        return buf;
    }

    public Buffer readBuffer(int len) {
        int endpos = offset + length;
        if (pos > endpos) {
            return null;
        }
        if (pos + len > endpos) {
            len = length - pos;
        }
        Buffer rc = new Buffer(buf, pos, len);
        pos += len;
        return rc;
    }

    /**
     * reset the <code>StoreByteArrayInputStream</code> to use an new byte array
     *
     * @param newBuff
     */
    public void restart(byte[] newBuff) {
        buf = newBuff;
        pos = 0;
        length = newBuff.length;
    }

    public void restart() {
        pos = 0;
        length = buf.length;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int skip(int n) {
        return skipBytes(n);
    }

    //----- InputStream implementation ---------------------------------------//

    /**
     * Reads the next byte of data from this input stream. The value byte is
     * returned as an <code>int</code> in the range <code>0</code> to
     * <code>255</code>. If no byte is available because the end of the stream
     * has been reached, the value <code>-1</code> is returned.
     * <p>
     * This <code>read</code> method cannot block.
     *
     * @return the next byte of data, or <code>-1</code> if the end of the
     *         stream has been reached.
     */
    @Override
    public int read() {
        return (pos < offset + length) ? (buf[pos++] & 0xff) : -1;
    }

    /**
     * Reads up to <code>len</code> bytes of data into an array of bytes from
     * this input stream.
     *
     * @param b
     *        the buffer into which the data is read.
     * @param off
     *        the start offset of the data.
     * @param len
     *        the maximum number of bytes read.
     * @return the total number of bytes read into the buffer, or
     *         <code>-1</code> if there is no more data because the end of the
     *         stream has been reached.
     */
    @Override
    public int read(byte b[], int off, int len) {
        if (b == null) {
            throw new NullPointerException();
        }

        int endpos = offset + length;
        if (pos >= endpos) {
            return -1;
        }
        if (pos + len > endpos) {
            len = length - pos;
        }
        if (len <= 0) {
            return 0;
        }

        System.arraycopy(buf, pos, b, off, len);
        pos += len;
        return len;
    }

    //----- DataInput Implementation -----------------------------------------//

    /**
     * @return the number of bytes that can be read from the input stream
     *         without blocking.
     */
    @Override
    public int available() {
        return offset + length - pos;
    }

    @Override
    public void readFully(byte[] b) {
        read(b, 0, b.length);
    }

    @Override
    public void readFully(byte[] b, int off, int len) {
        read(b, off, len);
    }

    @Override
    public int skipBytes(int n) {
        int endpos = offset + length;
        if (pos + n > endpos) {
            n = endpos - pos;
        }
        if (n < 0) {
            return 0;
        }
        pos += n;
        return n;
    }

    @Override
    public boolean readBoolean() {
        return read() != 0;
    }

    @Override
    public byte readByte() {
        return (byte) read();
    }

    @Override
    public int readUnsignedByte() {
        return read();
    }

    @Override
    public short readShort() {
        int ch1 = read();
        int ch2 = read();
        return (short) ((ch1 << 8) + (ch2 << 0));
    }

    @Override
    public int readUnsignedShort() {
        int ch1 = read();
        int ch2 = read();
        return (ch1 << 8) + (ch2 << 0);
    }

    @Override
    public char readChar() {
        int ch1 = read();
        int ch2 = read();
        return (char) ((ch1 << 8) + (ch2 << 0));
    }

    @Override
    public int readInt() {
        int ch1 = read();
        int ch2 = read();
        int ch3 = read();
        int ch4 = read();
        return (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0);
    }

    @Override
    public long readLong() {
        long rc = ((long) buf[pos++] << 56) + ((long) (buf[pos++] & 255) << 48) + ((long) (buf[pos++] & 255) << 40) + ((long) (buf[pos++] & 255) << 32);
        return rc + ((long) (buf[pos++] & 255) << 24) + ((buf[pos++] & 255) << 16) + ((buf[pos++] & 255) << 8) + ((buf[pos++] & 255) << 0);
    }

    @Override
    public float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt());
    }

    @Override
    public double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong());
    }

    @Override
    public String readLine() {
        int start = pos;
        while (pos < offset + length) {
            int c = read();
            if (c == '\n') {
                break;
            }
            if (c == '\r') {
                c = read();
                if (c != '\n' && c != -1) {
                    pos--;
                }
                break;
            }
        }
        return new String(buf, start, pos);
    }

    @Override
    public String readUTF() throws IOException {
        int length = readUnsignedShort();
        char[] characters = new char[length];
        int c;
        int c2;
        int c3;
        int count = 0;
        int total = pos + length;

        while (pos < total) {
            c = buf[pos] & 0xff;
            if (c > 127) {
                break;
            }
            pos++;
            characters[count++] = (char) c;
        }

        while (pos < total) {
            c = buf[pos] & 0xff;
            switch (c >> 4) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                    pos++;
                    characters[count++] = (char) c;
                    break;
                case 12:
                case 13:
                    pos += 2;
                    if (pos > total) {
                        throw new UTFDataFormatException("bad string");
                    }
                    c2 = buf[pos - 1];
                    if ((c2 & 0xC0) != 0x80) {
                        throw new UTFDataFormatException("bad string");
                    }
                    characters[count++] = (char) (((c & 0x1F) << 6) | (c2 & 0x3F));
                    break;
                case 14:
                    pos += 3;
                    if (pos > total) {
                        throw new UTFDataFormatException("bad string");
                    }
                    c2 = buf[pos - 2];
                    c3 = buf[pos - 1];
                    if (((c2 & 0xC0) != 0x80) || ((c3 & 0xC0) != 0x80)) {
                        throw new UTFDataFormatException("bad string");
                    }
                    characters[count++] = (char) (((c & 0x0F) << 12) | ((c2 & 0x3F) << 6) | ((c3 & 0x3F) << 0));
                    break;
                default:
                    throw new UTFDataFormatException("bad string");
            }
        }

        return new String(characters, 0, count);
    }
}
