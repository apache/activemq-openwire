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
package org.apache.activemq.openwire.utils;

import java.io.DataOutput;
import java.io.IOException;

/**
 * Support functions for dealing with data in OpenWire.
 */
public class MarshallingSupport {

    public static void writeUTF8(DataOutput dataOut, String text) throws IOException {

        if (text != null) {
            int strlen = text.length();
            int utflen = 0;
            char[] charr = new char[strlen];
            int c = 0;
            int count = 0;

            text.getChars(0, strlen, charr, 0);

            for (int i = 0; i < strlen; i++) {
                c = charr[i];
                if ((c >= 0x0001) && (c <= 0x007F)) {
                    utflen++;
                } else if (c > 0x07FF) {
                    utflen += 3;
                } else {
                    utflen += 2;
                }
            }
            // TODO diff: Sun code - removed
            byte[] bytearr = new byte[utflen + 4]; // TODO diff: Sun code
            bytearr[count++] = (byte) ((utflen >>> 24) & 0xFF); // TODO diff:
            // Sun code
            bytearr[count++] = (byte) ((utflen >>> 16) & 0xFF); // TODO diff:
            // Sun code
            bytearr[count++] = (byte) ((utflen >>> 8) & 0xFF);
            bytearr[count++] = (byte) ((utflen >>> 0) & 0xFF);
            for (int i = 0; i < strlen; i++) {
                c = charr[i];
                if ((c >= 0x0001) && (c <= 0x007F)) {
                    bytearr[count++] = (byte) c;
                } else if (c > 0x07FF) {
                    bytearr[count++] = (byte) (0xE0 | ((c >> 12) & 0x0F));
                    bytearr[count++] = (byte) (0x80 | ((c >> 6) & 0x3F));
                    bytearr[count++] = (byte) (0x80 | ((c >> 0) & 0x3F));
                } else {
                    bytearr[count++] = (byte) (0xC0 | ((c >> 6) & 0x1F));
                    bytearr[count++] = (byte) (0x80 | ((c >> 0) & 0x3F));
                }
            }
            dataOut.write(bytearr);

        } else {
            dataOut.writeInt(-1);
        }
    }

}
