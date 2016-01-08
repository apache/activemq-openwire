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
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import org.apache.activemq.openwire.utils.HexSupport;

/**
 * Wrapper for byte[] instances used to manage marshaled data
 */
public class Buffer implements Comparable<Buffer> {

    public byte[] data;
    public int offset;
    public int length;

    public Buffer(ByteBuffer other) {
        this(other.array(), other.arrayOffset()+other.position(), other.remaining());
    }

    public Buffer(Buffer other) {
        this(other.data, other.offset, other.length);
    }

    public Buffer(int size) {
        this(new byte[size]);
    }

    public Buffer(byte data[]) {
        this(data, 0, data.length);
    }

    public Buffer(byte data[], int offset, int length) {

        if (data == null) {
            throw new IllegalArgumentException("byte array value cannot by null");
        }

        if (offset + length > data.length) {
            throw new IndexOutOfBoundsException(
                String.format("offset %d + length %d must be <= the data.length %d", data, length, data.length));
        }

        this.data = data;
        this.offset = offset;
        this.length = length;
    }

    //-----Implementation ----------------------------------------------------//

    public byte[] getData() {
        return data;
    }

    public int getLength() {
        return length;
    }

    public int getOffset() {
        return offset;
    }

    final public boolean isEmpty() {
        return length == 0;
    }

    final public byte[] toByteArray() {
        byte[] data = this.data;
        int length = this.length;

        if (length != data.length) {
            byte t[] = new byte[length];
            System.arraycopy(data, offset, t, 0, length);
            data = t;
        }

        return data;
    }

    final public boolean equals(Buffer obj) {
        byte[] data = this.data;
        int offset = this.offset;
        int length = this.length;

        if (length != obj.length) {
            return false;
        }

        byte[] objData = obj.data;
        int objOffset = obj.offset;

        for (int i = 0; i < length; i++) {
            if (objData[objOffset + i] != data[offset + i]) {
                return false;
            }
        }

        return true;
    }

    //----- Platform overrides -----------------------------------------------//

    @Override
    public String toString() {
        int size = length;
        boolean asciiPrintable = true;

        for (int i = 0; i < size; i++) {
            int c = data[offset + i] & 0xFF;
            if (c > 126 || c < 32) { // not a printable char
                if (!(c == '\n' || c == '\r' | c == '\n' | c == 27)) {
                    // except these.
                    asciiPrintable = false;
                    break;
                }
            }
        }

        if (asciiPrintable) {
            char decoded[] = new char[length];
            for (int i = 0; i < size; i++) {
                decoded[i] = (char) (data[offset + i] & 0xFF);
            }
            return "ascii: " + new String(decoded);
        } else {
            return "hex: " + HexSupport.toHexFromBuffer(this);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj == null || obj.getClass() != Buffer.class) {
            return false;
        }

        return equals((Buffer) obj);
    }

    @Override
    public int hashCode() {
        byte[] data = this.data;
        int offset = this.offset;
        int length = this.length;

        byte[] target = new byte[4];
        for (int i = 0; i < length; i++) {
            target[i % 4] ^= data[offset + i];
        }

        return target[0] << 24 | target[1] << 16 | target[2] << 8 | target[3];
    }

    @Override
    public int compareTo(Buffer o) {
        if (this == o) {
            return 0;
        }

        byte[] data = this.data;
        int offset = this.offset;
        int length = this.length;

        int oLength = o.length;
        int oOffset = o.offset;
        byte[] oData = o.data;

        int minLength = Math.min(length, oLength);
        if (offset == oOffset) {
            int pos = offset;
            int limit = minLength + offset;
            while (pos < limit) {
                int b1 = 0xFF & data[pos];
                int b2 = 0xFF & oData[pos];
                if (b1 != b2) {
                    return b1 - b2;
                }
                pos++;
            }
        } else {
            int offset1 = offset;
            int offset2 = oOffset;
            while (minLength-- != 0) {
                int b1 = 0xFF & data[offset1++];
                int b2 = 0xFF & oData[offset2++];
                if (b1 != b2) {
                    return b1 - b2;
                }
            }
        }

        return length - oLength;
    }

    //----- Utility Stream write methods -------------------------------------//

    /**
     * same as out.write(data, offset, length);
     */
    public void writeTo(DataOutput out) throws IOException {
        out.write(data, offset, length);
    }

    /**
     * same as out.write(data, offset, length);
     */
    public void writeTo(OutputStream out) throws IOException {
        out.write(data, offset, length);
    }

    /**
     * same as in.readFully(data, offset, length);
     */
    public void readFrom(DataInput in) throws IOException {
        in.readFully(data, offset, length);
    }

    /**
     * same as in.read(data, offset, length);
     */
    public int readFrom(InputStream in) throws IOException {
        return in.read(data, offset, length);
    }
}
