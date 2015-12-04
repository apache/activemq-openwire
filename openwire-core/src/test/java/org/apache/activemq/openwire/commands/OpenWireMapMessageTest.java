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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenWireMapMessageTest {

    private static final Logger LOG = LoggerFactory.getLogger(OpenWireMapMessageTest.class);

    @Rule
    public TestName name = new TestName();

    @Test
    public void testBytesConversion() throws Exception {
        OpenWireMapMessage msg = new OpenWireMapMessage();
        msg.setObject("boolean", true);
        msg.setObject("byte", (byte) 1);
        msg.setObject("bytes", new byte[1]);
        msg.setObject("char", 'a');
        msg.setObject("double", 1.5);
        msg.setObject("float", 1.5f);
        msg.setObject("int", 1);
        msg.setObject("long", 1L);
        msg.setObject("object", "stringObj");
        msg.setObject("short", (short) 1);
        msg.setObject("string", "string");

        // Test with a 1Meg String
        StringBuffer bigSB = new StringBuffer(1024 * 1024);
        for (int i = 0; i < 1024 * 1024; i++) {
            bigSB.append('a' + i % 26);
        }
        String bigString = bigSB.toString();

        msg.setObject("bigString", bigString);

        msg = msg.copy();

        assertEquals(msg.getObject("boolean"), true);
        assertEquals(msg.getObject("byte"), (byte) 1);
        assertEquals(((byte[]) msg.getObject("bytes")).length, 1);
        assertEquals(msg.getObject("char"), 'a');
        assertEquals((Double) msg.getObject("double"), 1.5, 0);
        assertEquals((Float) msg.getObject("float"), 1.5f, 0);
        assertEquals(msg.getObject("int"), 1);
        assertEquals(msg.getObject("long"), 1L);
        assertEquals(msg.getObject("object"), "stringObj");
        assertEquals(msg.getObject("short"), (short) 1);
        assertEquals(msg.getObject("string"), "string");
        assertEquals(msg.getObject("bigString"), bigString);
    }

    @Test
    public void testGetObject() throws Exception {
        OpenWireMapMessage msg = new OpenWireMapMessage();
        Boolean booleanValue = Boolean.TRUE;
        Byte byteValue = Byte.valueOf("1");
        byte[] bytesValue = new byte[3];
        Character charValue = new Character('a');
        Double doubleValue = Double.valueOf("1.5");
        Float floatValue = Float.valueOf("1.5");
        Integer intValue = Integer.valueOf("1");
        Long longValue = Long.valueOf("1");
        Short shortValue = Short.valueOf("1");
        String stringValue = "string";

        try {
            msg.setObject("boolean", booleanValue);
            msg.setObject("byte", byteValue);
            msg.setObject("bytes", bytesValue);
            msg.setObject("char", charValue);
            msg.setObject("double", doubleValue);
            msg.setObject("float", floatValue);
            msg.setObject("int", intValue);
            msg.setObject("long", longValue);
            msg.setObject("short", shortValue);
            msg.setObject("string", stringValue);
        } catch (IOException ioe) {
            LOG.warn("Caught: " + ioe);
            ioe.printStackTrace();
            fail("object formats should be correct");
        }

        msg = msg.copy();

        assertTrue(msg.getObject("boolean") instanceof Boolean);
        assertEquals(msg.getObject("boolean"), booleanValue);
        assertTrue(msg.getObject("byte") instanceof Byte);
        assertEquals(msg.getObject("byte"), byteValue);
        assertTrue(msg.getObject("bytes") instanceof byte[]);
        assertEquals(((byte[]) msg.getObject("bytes")).length, bytesValue.length);
        assertEquals(((byte[])msg.getObject("bytes")).length, bytesValue.length);
        assertTrue(msg.getObject("char") instanceof Character);
        assertEquals(msg.getObject("char"), charValue);
        assertTrue(msg.getObject("double") instanceof Double);
        assertEquals(msg.getObject("double"), doubleValue);
        assertTrue(msg.getObject("float") instanceof Float);
        assertEquals(msg.getObject("float"), floatValue);
        assertTrue(msg.getObject("int") instanceof Integer);
        assertEquals(msg.getObject("int"), intValue);
        assertTrue(msg.getObject("long") instanceof Long);
        assertEquals(msg.getObject("long"), longValue);
        assertTrue(msg.getObject("short") instanceof Short);
        assertEquals(msg.getObject("short"), shortValue);
        assertTrue(msg.getObject("string") instanceof String);
        assertEquals(msg.getObject("string"), stringValue);

        msg.clearBody();
        try {
            msg.setObject("object", new Object());
            fail("should have thrown exception");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testGetMapNames() throws Exception {
        OpenWireMapMessage msg = new OpenWireMapMessage();
        msg.setObject("boolean", true);
        msg.setObject("byte", (byte) 1);
        msg.setObject("bytes1", new byte[1]);
        msg.setObject("char", 'a');
        msg.setObject("double", 1.5);
        msg.setObject("float", 1.5f);
        msg.setObject("int", 1);
        msg.setObject("long", 1);
        msg.setObject("object", "stringObj");
        msg.setObject("short", (short) 1);
        msg.setObject("string", "string");

        msg = msg.copy();

        Enumeration<String> mapNamesEnum = msg.getMapNames();
        List<String> mapNamesList = Collections.list(mapNamesEnum);

        assertEquals(mapNamesList.size(), 11);
        assertTrue(mapNamesList.contains("boolean"));
        assertTrue(mapNamesList.contains("byte"));
        assertTrue(mapNamesList.contains("bytes1"));
        assertTrue(mapNamesList.contains("char"));
        assertTrue(mapNamesList.contains("double"));
        assertTrue(mapNamesList.contains("float"));
        assertTrue(mapNamesList.contains("int"));
        assertTrue(mapNamesList.contains("long"));
        assertTrue(mapNamesList.contains("object"));
        assertTrue(mapNamesList.contains("short"));
        assertTrue(mapNamesList.contains("string"));
    }

    @Test
    public void testItemExists() throws Exception {
        OpenWireMapMessage mapMessage = new OpenWireMapMessage();

        mapMessage.setObject("exists", "test");

        mapMessage = mapMessage.copy();

        assertTrue(mapMessage.itemExists("exists"));
        assertFalse(mapMessage.itemExists("doesntExist"));
    }

    @Test
    public void testClearBody() throws Exception {
        OpenWireMapMessage mapMessage = new OpenWireMapMessage();
        mapMessage.setObject("String", "String");
        mapMessage.clearBody();

        mapMessage.setContent(mapMessage.getContent());
        assertNull(mapMessage.getObject("String"));
        mapMessage.clearBody();
        mapMessage.setObject("String", "String");

        mapMessage = mapMessage.copy();

        mapMessage.getObject("String");
    }
}
