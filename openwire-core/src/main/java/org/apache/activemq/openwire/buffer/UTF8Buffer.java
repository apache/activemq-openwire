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

import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;

/**
 * Simple Buffer type class used to hold data that is know to be in UTF8
 * format.
 */
final public class UTF8Buffer extends Buffer {

    private SoftReference<String> value = new SoftReference<String>(null);
    private int hashCode;

    public UTF8Buffer(Buffer other) {
        super(other);
    }

    public UTF8Buffer(byte[] data, int offset, int length) {
        super(data, offset, length);
    }

    public UTF8Buffer(byte[] data) {
        super(data);
    }

    public UTF8Buffer(String input) {
        super(encode(input));
    }

    //----- Implementations --------------------------------------------------//

    @Override
    public int compareTo(Buffer other) {
        // Do a char comparison.. not a byte for byte comparison.
        return toString().compareTo(other.toString());
    }

    @Override
    public String toString() {
        String result = value.get();
        if (result == null) {
            result = decode(this);
            value = new SoftReference<String>(result);
        }

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj == null || obj.getClass() != UTF8Buffer.class) {
            return false;
        }

        return equals(obj);
    }

    @Override
    public int hashCode() {
        if (hashCode == 0) {
            hashCode = super.hashCode();
        }

        return hashCode;
    }

    //----- static convenience methods ---------------------------------------//

    public static final byte[] encode(String input) {
        try {
            return input.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("A UnsupportedEncodingException was thrown for teh UTF-8 encoding. (This should never happen)");
        }
    }

    static public String decode(Buffer buffer) {
        try {
            return new String(buffer.getData(), buffer.getOffset(), buffer.getLength(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("A UnsupportedEncodingException was thrown for teh UTF-8 encoding. (This should never happen)");
        }
    }
}
