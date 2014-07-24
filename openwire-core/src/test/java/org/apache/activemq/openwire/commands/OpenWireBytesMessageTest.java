/**
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
package org.apache.activemq.openwire.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.activemq.openwire.commands.CommandTypes;
import org.apache.activemq.openwire.commands.OpenWireBytesMessage;
import org.junit.Test;

public class OpenWireBytesMessageTest {

    // The following text should compress well
    private static final String TEXT = "The quick red fox jumped over the lazy brown dog. "
            + "The quick red fox jumped over the lazy brown dog. " + "The quick red fox jumped over the lazy brown dog. "
            + "The quick red fox jumped over the lazy brown dog. " + "The quick red fox jumped over the lazy brown dog. "
            + "The quick red fox jumped over the lazy brown dog. " + "The quick red fox jumped over the lazy brown dog. "
            + "The quick red fox jumped over the lazy brown dog. " + "The quick red fox jumped over the lazy brown dog. "
            + "The quick red fox jumped over the lazy brown dog. " + "The quick red fox jumped over the lazy brown dog. "
            + "The quick red fox jumped over the lazy brown dog. " + "The quick red fox jumped over the lazy brown dog. "
            + "The quick red fox jumped over the lazy brown dog. " + "The quick red fox jumped over the lazy brown dog. "
            + "The quick red fox jumped over the lazy brown dog. " + "The quick red fox jumped over the lazy brown dog. ";

    @Test
    public void testGetDataStructureType() {
        OpenWireBytesMessage msg = new OpenWireBytesMessage();
        assertEquals(msg.getDataStructureType(), CommandTypes.OPENWIRE_BYTES_MESSAGE);
    }

    @Test
    public void testGetBodyLength() throws Exception {
        OpenWireBytesMessage msg = new OpenWireBytesMessage();

        byte[] data = new byte[80];

        for (byte i = 0; i < data.length; i++) {
            data[i] = i;
        }

        msg.setBodyBytes(data);

        assertTrue(msg.getBodyLength() == 80);
    }

    @Test
    public void testBodyCompression() throws Exception {
        OpenWireBytesMessage message = new OpenWireBytesMessage();
        message.setUseCompression(true);
        message.setBodyBytes(TEXT.getBytes("UTF8"));

        int compressedSize = message.getContent().getLength();
        byte[] bytes = message.getBodyBytes();

        assertTrue(compressedSize < bytes.length);

        String rcvString = new String(bytes, "UTF8");
        assertEquals(TEXT.length(), rcvString.length());
        assertEquals(TEXT, rcvString);
        assertTrue(message.isCompressed());
    }
}