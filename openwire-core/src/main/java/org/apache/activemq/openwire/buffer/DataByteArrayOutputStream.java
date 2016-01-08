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

import java.io.DataOutput;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UTFDataFormatException;

/**
 * Optimized ByteArrayOutputStream
 */
public final class DataByteArrayOutputStream extends OutputStream implements DataOutput {

    private static final int DEFAULT_SIZE = 2048;

    protected byte buf[];
    protected int pos;

    /**
     * Creates a new byte array output stream, with a buffer capacity of the
     * specified size, in bytes.
     *
     * @param size
     *        the initial size.
     * @exception IllegalArgumentException
     *            if size is negative.
     */
    public DataByteArrayOutputStream(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Invalid size: " + size);
        }
        buf = new byte[size];
    }

    public DataByteArrayOutputStream(byte buf[]) {
        if (buf == null || buf.length == 0) {
            throw new IllegalArgumentException("Invalid buffer");
        }
        this.buf = buf;
    }

    /**
     * Creates a new byte array output stream.
     */
    public DataByteArrayOutputStream() {
        this(DEFAULT_SIZE);
    }

    /**
     * start using a fresh byte array
     *
     * @param size
     */
    public void restart(int size) {
        buf = new byte[size];
        pos = 0;
    }

    /**
     * start using a fresh byte array
     */
    public void restart() {
        restart(DEFAULT_SIZE);
    }

    /**
     * Get a Buffer from the stream
     *
     * @return the byte sequence
     */
    public Buffer toBuffer() {
        return new Buffer(buf, 0, pos);
    }

    public void write(Buffer data) throws IOException {
        write(data.data, data.offset, data.length);
    }

    /**
     * @return the underlying byte[] buffer
     */
    public byte[] getData() {
        return buf;
    }

    /**
     * reset the output stream
     */
    public void reset() {
        pos = 0;
    }

    /**
     * Set the current position for writing
     *
     * @param offset
     * @throws IOException
     */
    public void position(int offset) throws IOException {
        ensureEnoughBuffer(offset);
        pos = offset;
    }

    public int position() {
        return pos;
    }

    public int size() {
        return pos;
    }

    public void skip(int size) throws IOException {
        ensureEnoughBuffer(pos + size);
        pos += size;
    }

    //----- Implementation of OutputStream -----------------------------------//

    /**
     * Writes the specified byte to this byte array output stream.
     *
     * @param b
     *        the byte to be written.
     * @throws IOException
     */
    @Override
    public void write(int b) throws IOException {
        int newcount = pos + 1;
        ensureEnoughBuffer(newcount);
        buf[pos] = (byte) b;
        pos = newcount;
    }

    /**
     * Writes <code>len</code> bytes from the specified byte array starting at
     * offset <code>off</code> to this byte array output stream.
     *
     * @param b
     *        the data.
     * @param off
     *        the start offset in the data.
     * @param len
     *        the number of bytes to write.
     * @throws IOException
     */
    @Override
    public void write(byte b[], int off, int len) throws IOException {
        if (len == 0) {
            return;
        }
        int newcount = pos + len;
        ensureEnoughBuffer(newcount);
        System.arraycopy(b, off, buf, pos, len);
        pos = newcount;
    }

    //----- Implementation of DataOutput -------------------------------------//

    @Override
    public void writeBoolean(boolean v) throws IOException {
        ensureEnoughBuffer(pos + 1);
        buf[pos++] = (byte) (v ? 1 : 0);
    }

    @Override
    public void writeByte(int v) throws IOException {
        ensureEnoughBuffer(pos + 1);
        buf[pos++] = (byte) (v >>> 0);
    }

    @Override
    public void writeShort(int v) throws IOException {
        ensureEnoughBuffer(pos + 2);
        buf[pos++] = (byte) (v >>> 8);
        buf[pos++] = (byte) (v >>> 0);
    }

    @Override
    public void writeChar(int v) throws IOException {
        ensureEnoughBuffer(pos + 2);
        buf[pos++] = (byte) (v >>> 8);
        buf[pos++] = (byte) (v >>> 0);
    }

    @Override
    public void writeInt(int v) throws IOException {
        ensureEnoughBuffer(pos + 4);
        buf[pos++] = (byte) (v >>> 24);
        buf[pos++] = (byte) (v >>> 16);
        buf[pos++] = (byte) (v >>> 8);
        buf[pos++] = (byte) (v >>> 0);
    }

    @Override
    public void writeLong(long v) throws IOException {
        ensureEnoughBuffer(pos + 8);
        buf[pos++] = (byte) (v >>> 56);
        buf[pos++] = (byte) (v >>> 48);
        buf[pos++] = (byte) (v >>> 40);
        buf[pos++] = (byte) (v >>> 32);
        buf[pos++] = (byte) (v >>> 24);
        buf[pos++] = (byte) (v >>> 16);
        buf[pos++] = (byte) (v >>> 8);
        buf[pos++] = (byte) (v >>> 0);
    }

    @Override
    public void writeFloat(float v) throws IOException {
        writeInt(Float.floatToIntBits(v));
    }

    @Override
    public void writeDouble(double v) throws IOException {
        writeLong(Double.doubleToLongBits(v));
    }

    @Override
    public void writeBytes(String s) throws IOException {
        int length = s.length();
        for (int i = 0; i < length; i++) {
            write((byte) s.charAt(i));
        }
    }

    @Override
    public void writeChars(String s) throws IOException {
        int length = s.length();
        for (int i = 0; i < length; i++) {
            int c = s.charAt(i);
            write((c >>> 8) & 0xFF);
            write((c >>> 0) & 0xFF);
        }
    }

    @Override
    public void writeUTF(String str) throws IOException {
        int strlen = str.length();
        int encodedsize = 0;
        int c;
        for (int i = 0; i < strlen; i++) {
            c = str.charAt(i);
            if ((c >= 0x0001) && (c <= 0x007F)) {
                encodedsize++;
            } else if (c > 0x07FF) {
                encodedsize += 3;
            } else {
                encodedsize += 2;
            }
        }
        if (encodedsize > 65535) {
            throw new UTFDataFormatException("encoded string too long: " + encodedsize + " bytes");
        }
        ensureEnoughBuffer(pos + encodedsize + 2);
        writeShort(encodedsize);
        int i = 0;
        for (i = 0; i < strlen; i++) {
            c = str.charAt(i);
            if (!((c >= 0x0001) && (c <= 0x007F))) {
                break;
            }
            buf[pos++] = (byte) c;
        }
        for (; i < strlen; i++) {
            c = str.charAt(i);
            if ((c >= 0x0001) && (c <= 0x007F)) {
                buf[pos++] = (byte) c;
            } else if (c > 0x07FF) {
                buf[pos++] = (byte) (0xE0 | ((c >> 12) & 0x0F));
                buf[pos++] = (byte) (0x80 | ((c >> 6) & 0x3F));
                buf[pos++] = (byte) (0x80 | ((c >> 0) & 0x3F));
            } else {
                buf[pos++] = (byte) (0xC0 | ((c >> 6) & 0x1F));
                buf[pos++] = (byte) (0x80 | ((c >> 0) & 0x3F));
            }
        }
    }

    //----- Indexed Write Operations -----------------------------------------//

    /**
     * Write the given int value starting at the given index in the internal
     * data buffer, if there is not enough space in the current buffer or the
     * index is beyond the current buffer capacity then the size of the buffer
     * is increased to fit the value.
     *
     * This method does not modify the tracked position for non-index writes
     * which means that a subsequent write operation can overwrite the value
     * written by this operation if the index given is beyond the current
     * write position.
     *
     * @param index
     * @param value
     * @throws IOException
     */
    public void writeInt(int index, int value) throws IOException {
        ensureEnoughBuffer(index + 4);
        buf[index++] = (byte) (value >>> 24);
        buf[index++] = (byte) (value >>> 16);
        buf[index++] = (byte) (value >>> 8);
        buf[index++] = (byte) (value >>> 0);
    }


    //----- Internal implementation ------------------------------------------//

    private void resize(int newcount) {
        byte newbuf[] = new byte[Math.max(buf.length << 1, newcount)];
        System.arraycopy(buf, 0, newbuf, 0, pos);
        buf = newbuf;
    }

    private void ensureEnoughBuffer(int newcount) {
        if (newcount > buf.length) {
            resize(newcount);
        }
    }
}
