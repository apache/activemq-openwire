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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.activemq.openwire.buffer.Buffer;
import org.junit.Before;
import org.junit.Test;

public class OpenWireStreamMessageTest {

    private final List<Object> elements = new ArrayList<Object>();

    @Before
    public void setUp() throws Exception {

        elements.add(Boolean.TRUE);
        elements.add(Byte.valueOf(Byte.MAX_VALUE));
        elements.add(Character.valueOf('a'));
        elements.add(Short.valueOf(Short.MAX_VALUE));
        elements.add(Integer.valueOf(Integer.MAX_VALUE));
        elements.add(Long.valueOf(Long.MAX_VALUE));
        elements.add(Float.valueOf(Float.MAX_VALUE));
        elements.add(Double.valueOf(Double.MAX_VALUE));
        elements.add("Test-String");
        elements.add(new byte[] { 1, 2, 3, 4, 5, 6 });
    }

    @Test
    public void testGetDataStructureType() {
        OpenWireStreamMessage msg = new OpenWireStreamMessage();
        assertEquals(msg.getDataStructureType(), CommandTypes.OPENWIRE_STREAM_MESSAGE);
    }

    @Test
    public void testWriteListToStream() throws Exception {
        OpenWireStreamMessage message = new OpenWireStreamMessage();

        message.writeListToStream(Collections.emptyList());
        assertNull(message.getContent());
        message.writeListToStream(elements);
        assertNotNull(message.getContent());
    }

    @Test
    public void testWriteListToStreamCompressed() throws Exception {
        OpenWireStreamMessage message = new OpenWireStreamMessage();
        message.setUseCompression(true);

        // Add something that should compress well.
        elements.add("Repeating String" + "Repeating String" + "Repeating String" + "Repeating String" +
                     "Repeating String" + "Repeating String" + "Repeating String" + "Repeating String" +
                     "Repeating String" + "Repeating String" + "Repeating String" + "Repeating String");

        message.writeListToStream(Collections.emptyList());
        assertNull(message.getContent());
        message.writeListToStream(elements);
        assertNotNull(message.getContent());

        Buffer rawContent = message.getContent();
        Buffer processedContent = message.getPayload();

        assertTrue(rawContent.getLength() < processedContent.getLength());
    }

    @Test
    public void testReadStreamToList() throws Exception {
        OpenWireStreamMessage message = new OpenWireStreamMessage();

        message.writeListToStream(elements);
        assertNotNull(message.getContent());

        List<Object> results = message.readStreamToList();
        assertEquals(elements.size(), results.size());

        for (int i = 0; i < results.size(); ++i) {

            Object element = elements.get(i);
            Object result = results.get(i);

            if (result instanceof byte[]) {
                assertTrue(Arrays.equals((byte[]) result, (byte[]) element));
            } else {
                assertEquals(element, result);
            }
        }
    }
}
