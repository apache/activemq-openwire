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
package org.apache.activemq.openwire.jms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageFormatException;
import javax.jms.MessageNotWriteableException;

import org.apache.activemq.openwire.codec.OpenWireFormat;
import org.apache.activemq.openwire.commands.CommandTypes;
import org.apache.activemq.openwire.commands.CommandVisitor;
import org.apache.activemq.openwire.commands.OpenWireDestination;
import org.apache.activemq.openwire.commands.OpenWireTopic;
import org.apache.activemq.openwire.commands.Response;
import org.apache.activemq.openwire.jms.OpenWireJMSBytesMessage;
import org.apache.activemq.openwire.jms.OpenWireJMSMessage;
import org.fusesource.hawtbuf.Buffer;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests for the OpenWireJMSMessage wrapper class.
 */
public class OpenWireJMSMessageTest {

    private static final Logger LOG = LoggerFactory.getLogger(OpenWireJMSMessageTest.class);

    protected boolean readOnlyMessage;

    private String jmsMessageID;
    private String jmsCorrelationID;
    private OpenWireDestination jmsDestination;
    private OpenWireDestination jmsReplyTo;
    private int jmsDeliveryMode;
    private boolean jmsRedelivered;
    private String jmsType;
    private long jmsExpiration;
    private int jmsPriority;
    private long jmsTimestamp;
    private long[] consumerIDs;

    @Before
    public void setUp() throws Exception {
        this.jmsMessageID = "ID:TEST-ID:0:0:0:1";
        this.jmsCorrelationID = "testcorrelationid";
        this.jmsDestination = new OpenWireTopic("test.topic");
        this.jmsReplyTo = new OpenWireTopic("test.replyto.topic:001");
        this.jmsDeliveryMode = Message.DEFAULT_DELIVERY_MODE;
        this.jmsRedelivered = true;
        this.jmsType = "test type";
        this.jmsExpiration = 100000;
        this.jmsPriority = 5;
        this.jmsTimestamp = System.currentTimeMillis();
        this.readOnlyMessage = false;
        this.consumerIDs = new long[3];
        for (int i = 0; i < this.consumerIDs.length; i++) {
            this.consumerIDs[i] = i;
        }
    }

    @Test
    public void testGetDataStructureType() {
        OpenWireJMSMessage msg = new OpenWireJMSMessage();
        assertEquals(msg.getOpenWireMessage().getDataStructureType(), CommandTypes.OPENWIRE_MESSAGE);
    }

    @Test
    public void testHashCode() throws Exception {
        OpenWireJMSMessage msg = new OpenWireJMSMessage();
        msg.setJMSMessageID(this.jmsMessageID);
        assertTrue(msg.getJMSMessageID().hashCode() == jmsMessageID.hashCode());
    }

    @Test
    public void testSetReadOnly() {
        OpenWireJMSMessage msg = new OpenWireJMSMessage();
        msg.setReadOnlyProperties(true);
        boolean test = false;
        try {
            msg.setIntProperty("test", 1);
        } catch (MessageNotWriteableException me) {
            test = true;
        } catch (JMSException e) {
            e.printStackTrace(System.err);
            test = false;
        }
        assertTrue(test);
    }

    @Test
    public void testSetToForeignJMSID() throws Exception {
        OpenWireJMSMessage msg = new OpenWireJMSMessage();
        msg.setJMSMessageID("ID:EMS-SERVER.8B443C380083:429");
    }

    @Test
    public void testEqualsObject() throws Exception {
        OpenWireJMSMessage msg1 = new OpenWireJMSMessage();
        OpenWireJMSMessage msg2 = new OpenWireJMSMessage();
        msg1.setJMSMessageID(this.jmsMessageID);
        assertTrue(!msg1.equals(msg2));
        msg2.setJMSMessageID(this.jmsMessageID);
        assertTrue(msg1.equals(msg2));
    }

    @Test
    public void testShallowCopy() throws Exception {
        OpenWireJMSMessage msg1 = new OpenWireJMSMessage();
        msg1.setJMSMessageID(jmsMessageID);
        OpenWireJMSMessage msg2 = msg1.copy();
        assertTrue(msg1 != msg2 && msg1.equals(msg2));
    }

    @Test
    public void testCopy() throws Exception {
        this.jmsMessageID = "testid";
        this.jmsCorrelationID = "testcorrelationid";
        this.jmsDestination = new OpenWireTopic("test.topic");
        this.jmsReplyTo = new OpenWireTopic("test.replyto.topic:001");
        this.jmsDeliveryMode = Message.DEFAULT_DELIVERY_MODE;
        this.jmsRedelivered = true;
        this.jmsType = "test type";
        this.jmsExpiration = 100000;
        this.jmsPriority = 5;
        this.jmsTimestamp = System.currentTimeMillis();
        this.readOnlyMessage = false;

        OpenWireJMSMessage msg1 = new OpenWireJMSMessage();
        msg1.setJMSMessageID(this.jmsMessageID);
        msg1.setJMSCorrelationID(this.jmsCorrelationID);
        msg1.setJMSDestination(this.jmsDestination);
        msg1.setJMSReplyTo(this.jmsReplyTo);
        msg1.setJMSDeliveryMode(this.jmsDeliveryMode);
        msg1.setJMSRedelivered(this.jmsRedelivered);
        msg1.setJMSType(this.jmsType);
        msg1.setJMSExpiration(this.jmsExpiration);
        msg1.setJMSPriority(this.jmsPriority);
        msg1.setJMSTimestamp(this.jmsTimestamp);
        msg1.setReadOnlyProperties(true);

        OpenWireJMSMessage msg2 = msg1.copy();

        assertEquals(msg1.getJMSMessageID(), msg2.getJMSMessageID());
        assertTrue(msg1.getJMSCorrelationID().equals(msg2.getJMSCorrelationID()));
        assertTrue(msg1.getJMSDestination().equals(msg2.getJMSDestination()));
        assertTrue(msg1.getJMSReplyTo().equals(msg2.getJMSReplyTo()));
        assertTrue(msg1.getJMSDeliveryMode() == msg2.getJMSDeliveryMode());
        assertTrue(msg1.getJMSRedelivered() == msg2.getJMSRedelivered());
        assertTrue(msg1.getJMSType().equals(msg2.getJMSType()));
        assertTrue(msg1.getJMSExpiration() == msg2.getJMSExpiration());
        assertTrue(msg1.getJMSPriority() == msg2.getJMSPriority());
        assertTrue(msg1.getJMSTimestamp() == msg2.getJMSTimestamp());

        LOG.info("Message is: {}", msg1);
    }

    @Test
    public void testGetAndSetJMSMessageID() throws Exception {
        OpenWireJMSMessage msg = new OpenWireJMSMessage();
        msg.setJMSMessageID(this.jmsMessageID);
        assertEquals(msg.getJMSMessageID(), this.jmsMessageID);
    }

    @Test
    public void testGetAndSetJMSTimestamp() throws JMSException {
        OpenWireJMSMessage msg = new OpenWireJMSMessage();
        msg.setJMSTimestamp(this.jmsTimestamp);
        assertTrue(msg.getJMSTimestamp() == this.jmsTimestamp);
    }

    @Test
    public void testGetJMSCorrelationIDAsBytes() throws Exception {
        OpenWireJMSMessage msg = new OpenWireJMSMessage();
        msg.setJMSCorrelationID(this.jmsCorrelationID);
        byte[] testbytes = msg.getJMSCorrelationIDAsBytes();
        String str2 = new String(testbytes);
        assertTrue(this.jmsCorrelationID.equals(str2));
    }

    @Test
    public void testSetJMSCorrelationIDAsBytes() throws Exception {
        OpenWireJMSMessage msg = new OpenWireJMSMessage();
        byte[] testbytes = this.jmsCorrelationID.getBytes();
        msg.setJMSCorrelationIDAsBytes(testbytes);
        testbytes = msg.getJMSCorrelationIDAsBytes();
        String str2 = new String(testbytes);
        assertTrue(this.jmsCorrelationID.equals(str2));
    }

    @Test
    public void testGetAndSetJMSCorrelationID() throws JMSException {
        OpenWireJMSMessage msg = new OpenWireJMSMessage();
        msg.setJMSCorrelationID(this.jmsCorrelationID);
        assertTrue(msg.getJMSCorrelationID().equals(this.jmsCorrelationID));
    }

    @Test
    public void testGetAndSetJMSReplyTo() throws JMSException {
        OpenWireJMSMessage msg = new OpenWireJMSMessage();
        msg.setJMSReplyTo(this.jmsReplyTo);
        assertTrue(msg.getJMSReplyTo().equals(this.jmsReplyTo));
    }

    @Test
    public void testGetAndSetJMSDestination() throws Exception {
        OpenWireJMSMessage msg = new OpenWireJMSMessage();
        msg.setJMSDestination(this.jmsDestination);
        assertTrue(msg.getJMSDestination().equals(this.jmsDestination));
    }

    @Test
    public void testGetAndSetJMSDeliveryMode() throws JMSException {
        OpenWireJMSMessage msg = new OpenWireJMSMessage();
        msg.setJMSDeliveryMode(this.jmsDeliveryMode);
        assertTrue(msg.getJMSDeliveryMode() == this.jmsDeliveryMode);
    }

    @Test
    public void testGetAndSetMSRedelivered() throws JMSException {
        OpenWireJMSMessage msg = new OpenWireJMSMessage();
        msg.setJMSRedelivered(this.jmsRedelivered);
        assertTrue(msg.getJMSRedelivered() == this.jmsRedelivered);
    }

    @Test
    public void testGetAndSetJMSType() throws JMSException {
        OpenWireJMSMessage msg = new OpenWireJMSMessage();
        msg.setJMSType(this.jmsType);
        assertTrue(msg.getJMSType().equals(this.jmsType));
    }

    @Test
    public void testGetAndSetJMSExpiration() throws JMSException {
        OpenWireJMSMessage msg = new OpenWireJMSMessage();
        msg.setJMSExpiration(this.jmsExpiration);
        assertTrue(msg.getJMSExpiration() == this.jmsExpiration);
    }

    @Test
    public void testGetAndSetJMSPriority() throws JMSException {
        OpenWireJMSMessage msg = new OpenWireJMSMessage();
        msg.setJMSPriority(this.jmsPriority);
        assertTrue(msg.getJMSPriority() == this.jmsPriority);

        msg.setJMSPriority(-90);
        assertEquals(0, msg.getJMSPriority());

        msg.setJMSPriority(90);
        assertEquals(9, msg.getJMSPriority());
    }

    @Test
    public void testClearProperties() throws JMSException {
        OpenWireJMSMessage msg = new OpenWireJMSMessage();
        msg.setStringProperty("test", "test");
        msg.setJMSMessageID(this.jmsMessageID);
        msg.getOpenWireMessage().setContent(new Buffer(new byte[] {1, 0, 0}));
        msg.clearProperties();
        assertNull(msg.getStringProperty("test"));
        assertNotNull(msg.getJMSMessageID());
        assertNotNull(msg.getOpenWireMessage().getContent());
    }

    @Test
    public void testPropertyExists() throws JMSException {
        OpenWireJMSMessage msg = new OpenWireJMSMessage();
        msg.setStringProperty("test", "test");
        assertTrue(msg.propertyExists("test"));

        msg.setIntProperty("JMSXDeliveryCount", 1);
        assertTrue(msg.propertyExists("JMSXDeliveryCount"));
    }

    @Test
    public void testGetBooleanProperty() throws JMSException {
        OpenWireJMSMessage msg = new OpenWireJMSMessage();
        String name = "booleanProperty";
        msg.setBooleanProperty(name, true);
        assertTrue(msg.getBooleanProperty(name));
    }

    @Test
    public void testGetByteProperty() throws JMSException {
        OpenWireJMSMessage msg = new OpenWireJMSMessage();
        String name = "byteProperty";
        msg.setByteProperty(name, (byte) 1);
        assertTrue(msg.getByteProperty(name) == 1);
    }

    @Test
    public void testGetShortProperty() throws JMSException {
        OpenWireJMSMessage msg = new OpenWireJMSMessage();
        String name = "shortProperty";
        msg.setShortProperty(name, (short) 1);
        assertTrue(msg.getShortProperty(name) == 1);
    }

    @Test
    public void testGetIntProperty() throws JMSException {
        OpenWireJMSMessage msg = new OpenWireJMSMessage();
        String name = "intProperty";
        msg.setIntProperty(name, 1);
        assertTrue(msg.getIntProperty(name) == 1);
    }

    @Test
    public void testGetLongProperty() throws JMSException {
        OpenWireJMSMessage msg = new OpenWireJMSMessage();
        String name = "longProperty";
        msg.setLongProperty(name, 1);
        assertTrue(msg.getLongProperty(name) == 1);
    }

    @Test
    public void testGetFloatProperty() throws JMSException {
        OpenWireJMSMessage msg = new OpenWireJMSMessage();
        String name = "floatProperty";
        msg.setFloatProperty(name, 1.3f);
        assertTrue(msg.getFloatProperty(name) == 1.3f);
    }

    @Test
    public void testGetDoubleProperty() throws JMSException {
        OpenWireJMSMessage msg = new OpenWireJMSMessage();
        String name = "doubleProperty";
        msg.setDoubleProperty(name, 1.3d);
        assertTrue(msg.getDoubleProperty(name) == 1.3);
    }

    @Test
    public void testGetStringProperty() throws JMSException {
        OpenWireJMSMessage msg = new OpenWireJMSMessage();
        String name = "stringProperty";
        msg.setStringProperty(name, name);
        assertTrue(msg.getStringProperty(name).equals(name));
    }

    @Test
    public void testGetObjectProperty() throws JMSException {
        OpenWireJMSMessage msg = new OpenWireJMSMessage();
        String name = "floatProperty";
        msg.setFloatProperty(name, 1.3f);
        assertTrue(msg.getObjectProperty(name) instanceof Float);
        assertTrue(((Float) msg.getObjectProperty(name)).floatValue() == 1.3f);
    }

    @Test
    public void testGetPropertyNames() throws JMSException {
        OpenWireJMSMessage msg = new OpenWireJMSMessage();
        String name1 = "floatProperty";
        msg.setFloatProperty(name1, 1.3f);
        String name2 = "JMSXDeliveryCount";
        msg.setIntProperty(name2, 1);
        String name3 = "JMSRedelivered";
        msg.setBooleanProperty(name3, false);
        boolean found1 = false;
        boolean found2 = false;
        boolean found3 = false;
        for (Enumeration<?> iter = msg.getPropertyNames(); iter.hasMoreElements();) {
            Object element = iter.nextElement();
            found1 |= element.equals(name1);
            found2 |= element.equals(name2);
            found3 |= element.equals(name3);
        }
        assertTrue("prop name1 found", found1);
        // spec compliance, only non JMS (and JMSX) props returned
        assertFalse("prop name2 not found", found2);
        assertFalse("prop name4 not found", found3);
    }

    @Test
    public void testGetAllPropertyNames() throws JMSException {
        OpenWireJMSMessage msg = new OpenWireJMSMessage();
        String name1 = "floatProperty";
        msg.setFloatProperty(name1, 1.3f);
        String name2 = "JMSXDeliveryCount";
        msg.setIntProperty(name2, 1);
        String name3 = "JMSRedelivered";
        msg.setBooleanProperty(name3, false);
        boolean found1 = false;
        boolean found2 = false;
        boolean found3 = false;
        for (Enumeration<?> iter = msg.getAllPropertyNames(); iter.hasMoreElements();) {
            Object element = iter.nextElement();
            found1 |= element.equals(name1);
            found2 |= element.equals(name2);
            found3 |= element.equals(name3);
        }
        assertTrue("prop name1 found", found1);
        assertTrue("prop name2 found", found2);
        assertTrue("prop name4 found", found3);
    }

    @Test
    public void testSetObjectProperty() throws JMSException {
        OpenWireJMSMessage msg = new OpenWireJMSMessage();
        String name = "property";

        try {
            msg.setObjectProperty(name, "string");
            msg.setObjectProperty(name, Byte.valueOf("1"));
            msg.setObjectProperty(name, Short.valueOf("1"));
            msg.setObjectProperty(name, Integer.valueOf("1"));
            msg.setObjectProperty(name, Long.valueOf("1"));
            msg.setObjectProperty(name, Float.valueOf("1.1f"));
            msg.setObjectProperty(name, Double.valueOf("1.1"));
            msg.setObjectProperty(name, Boolean.TRUE);
            msg.setObjectProperty(name, null);
        } catch (MessageFormatException e) {
            fail("should accept object primitives and String");
        }
        try {
            msg.setObjectProperty(name, new byte[5]);
            fail("should accept only object primitives and String");
        } catch (MessageFormatException e) {
        }
        try {
            msg.setObjectProperty(name, new Object());
            fail("should accept only object primitives and String");
        } catch (MessageFormatException e) {
        }
    }

    @Test
    public void testConvertProperties() throws Exception {
        org.apache.activemq.openwire.commands.Message msg = new org.apache.activemq.openwire.commands.Message() {
            @Override
            public org.apache.activemq.openwire.commands.Message copy() {
                return null;
            }

            @Override
            public void beforeMarshall(OpenWireFormat wireFormat) throws IOException {
                super.beforeMarshall(wireFormat);
            }

            @Override
            public byte getDataStructureType() {
                return 0;
            }

            @Override
            public Response visit(CommandVisitor visitor) throws Exception {
                return null;
            }

            @Override
            public void clearBody() throws JMSException {
            }

            @Override
            public void storeContent() {
            }

            @Override
            public void storeContentAndClear() {

            }
        };

        msg.setProperty("stringProperty", "string");
        msg.setProperty("byteProperty", Byte.valueOf("1"));
        msg.setProperty("shortProperty", Short.valueOf("1"));
        msg.setProperty("intProperty", Integer.valueOf("1"));
        msg.setProperty("longProperty", Long.valueOf("1"));
        msg.setProperty("floatProperty", Float.valueOf("1.1f"));
        msg.setProperty("doubleProperty", Double.valueOf("1.1"));
        msg.setProperty("booleanProperty", Boolean.TRUE);
        msg.setProperty("nullProperty", null);

        msg.beforeMarshall(new OpenWireFormat(OpenWireFormat.DEFAULT_WIRE_VERSION));

        Map<String, Object> properties = msg.getProperties();
        assertEquals(properties.get("stringProperty"), "string");
        assertEquals(((Byte) properties.get("byteProperty")).byteValue(), 1);
        assertEquals(((Short) properties.get("shortProperty")).shortValue(), 1);
        assertEquals(((Integer) properties.get("intProperty")).intValue(), 1);
        assertEquals(((Long) properties.get("longProperty")).longValue(), 1);
        assertEquals(((Float) properties.get("floatProperty")).floatValue(), 1.1f, 0);
        assertEquals(((Double) properties.get("doubleProperty")).doubleValue(), 1.1, 0);
        assertEquals(((Boolean) properties.get("booleanProperty")).booleanValue(), true);
        assertNull(properties.get("nullProperty"));
    }

    @Test
    public void testSetNullProperty() throws JMSException {
        Message msg = new OpenWireJMSMessage();
        String name = "cheese";
        msg.setStringProperty(name, "Cheddar");
        assertEquals("Cheddar", msg.getStringProperty(name));

        msg.setStringProperty(name, null);
        assertEquals(null, msg.getStringProperty(name));
    }

    @Test
    public void testSetNullPropertyName() throws JMSException {
        Message msg = new OpenWireJMSMessage();

        try {
            msg.setStringProperty(null, "Cheese");
            fail("Should have thrown exception");
        } catch (IllegalArgumentException e) {
            LOG.info("Worked, caught: " + e);
        }
    }

    @Test
    public void testSetEmptyPropertyName() throws JMSException {
        Message msg = new OpenWireJMSMessage();

        try {
            msg.setStringProperty("", "Cheese");
            fail("Should have thrown exception");
        } catch (IllegalArgumentException e) {
            LOG.info("Worked, caught: " + e);
        }
    }

    @Test
    public void testGetAndSetJMSXDeliveryCount() throws JMSException {
        Message msg = new OpenWireJMSMessage();
        msg.setIntProperty("JMSXDeliveryCount", 1);
        int count = msg.getIntProperty("JMSXDeliveryCount");
        assertTrue("expected delivery count = 1 - got: " + count, count == 1);
    }

    @Test
    public void testClearBody() throws JMSException {
        OpenWireJMSBytesMessage message = new OpenWireJMSBytesMessage();
        message.clearBody();
        assertFalse(message.isReadOnlyBody());
        assertNull(message.getOpenWireMessage().getContent());
    }

    @Test
    public void testBooleanPropertyConversion() throws JMSException {
        OpenWireJMSMessage msg = new OpenWireJMSMessage();
        String propertyName = "property";
        msg.setBooleanProperty(propertyName, true);

        assertEquals(((Boolean) msg.getObjectProperty(propertyName)).booleanValue(), true);
        assertTrue(msg.getBooleanProperty(propertyName));
        assertEquals(msg.getStringProperty(propertyName), "true");
        try {
            msg.getByteProperty(propertyName);
            fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getShortProperty(propertyName);
            fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getIntProperty(propertyName);
            fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getLongProperty(propertyName);
            fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getFloatProperty(propertyName);
            fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getDoubleProperty(propertyName);
            fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
    }

    @Test
    public void testBytePropertyConversion() throws JMSException {
        OpenWireJMSMessage msg = new OpenWireJMSMessage();
        String propertyName = "property";
        msg.setByteProperty(propertyName, (byte) 1);

        assertEquals(((Byte) msg.getObjectProperty(propertyName)).byteValue(), 1);
        assertEquals(msg.getByteProperty(propertyName), 1);
        assertEquals(msg.getShortProperty(propertyName), 1);
        assertEquals(msg.getIntProperty(propertyName), 1);
        assertEquals(msg.getLongProperty(propertyName), 1);
        assertEquals(msg.getStringProperty(propertyName), "1");
        try {
            msg.getBooleanProperty(propertyName);
            fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getFloatProperty(propertyName);
            fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getDoubleProperty(propertyName);
            fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
    }

    @Test
    public void testShortPropertyConversion() throws JMSException {
        OpenWireJMSMessage msg = new OpenWireJMSMessage();
        String propertyName = "property";
        msg.setShortProperty(propertyName, (short) 1);

        assertEquals(((Short) msg.getObjectProperty(propertyName)).shortValue(), 1);
        assertEquals(msg.getShortProperty(propertyName), 1);
        assertEquals(msg.getIntProperty(propertyName), 1);
        assertEquals(msg.getLongProperty(propertyName), 1);
        assertEquals(msg.getStringProperty(propertyName), "1");
        try {
            msg.getBooleanProperty(propertyName);
            fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getByteProperty(propertyName);
            fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getFloatProperty(propertyName);
            fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getDoubleProperty(propertyName);
            fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
    }

    @Test
    public void testIntPropertyConversion() throws JMSException {
        OpenWireJMSMessage msg = new OpenWireJMSMessage();
        String propertyName = "property";
        msg.setIntProperty(propertyName, 1);

        assertEquals(((Integer) msg.getObjectProperty(propertyName)).intValue(), 1);
        assertEquals(msg.getIntProperty(propertyName), 1);
        assertEquals(msg.getLongProperty(propertyName), 1);
        assertEquals(msg.getStringProperty(propertyName), "1");
        try {
            msg.getBooleanProperty(propertyName);
            fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getByteProperty(propertyName);
            fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getShortProperty(propertyName);
            fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getFloatProperty(propertyName);
            fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getDoubleProperty(propertyName);
            fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
    }

    @Test
    public void testLongPropertyConversion() throws JMSException {
        OpenWireJMSMessage msg = new OpenWireJMSMessage();
        String propertyName = "property";
        msg.setLongProperty(propertyName, 1);

        assertEquals(((Long) msg.getObjectProperty(propertyName)).longValue(), 1);
        assertEquals(msg.getLongProperty(propertyName), 1);
        assertEquals(msg.getStringProperty(propertyName), "1");
        try {
            msg.getBooleanProperty(propertyName);
            fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getByteProperty(propertyName);
            fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getShortProperty(propertyName);
            fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getIntProperty(propertyName);
            fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getFloatProperty(propertyName);
            fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getDoubleProperty(propertyName);
            fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
    }

    @Test
    public void testFloatPropertyConversion() throws JMSException {
        OpenWireJMSMessage msg = new OpenWireJMSMessage();
        String propertyName = "property";
        msg.setFloatProperty(propertyName, (float) 1.5);
        assertEquals(((Float) msg.getObjectProperty(propertyName)).floatValue(), 1.5, 0);
        assertEquals(msg.getFloatProperty(propertyName), 1.5, 0);
        assertEquals(msg.getDoubleProperty(propertyName), 1.5, 0);
        assertEquals(msg.getStringProperty(propertyName), "1.5");
        try {
            msg.getBooleanProperty(propertyName);
            fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getByteProperty(propertyName);
            fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getShortProperty(propertyName);
            fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getIntProperty(propertyName);
            fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getLongProperty(propertyName);
            fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
    }

    @Test
    public void testDoublePropertyConversion() throws JMSException {
        OpenWireJMSMessage msg = new OpenWireJMSMessage();
        String propertyName = "property";
        msg.setDoubleProperty(propertyName, 1.5);
        assertEquals(((Double) msg.getObjectProperty(propertyName)).doubleValue(), 1.5, 0);
        assertEquals(msg.getDoubleProperty(propertyName), 1.5, 0);
        assertEquals(msg.getStringProperty(propertyName), "1.5");
        try {
            msg.getBooleanProperty(propertyName);
            fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getByteProperty(propertyName);
            fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getShortProperty(propertyName);
            fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getIntProperty(propertyName);
            fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getLongProperty(propertyName);
            fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getFloatProperty(propertyName);
            fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
    }

    @Test
    public void testStringPropertyConversion() throws JMSException {
        OpenWireJMSMessage msg = new OpenWireJMSMessage();
        String propertyName = "property";
        String stringValue = "true";
        msg.setStringProperty(propertyName, stringValue);
        assertEquals(msg.getStringProperty(propertyName), stringValue);
        assertEquals(msg.getObjectProperty(propertyName), stringValue);
        assertEquals(msg.getBooleanProperty(propertyName), true);

        stringValue = "1";
        msg.setStringProperty(propertyName, stringValue);
        assertEquals(msg.getByteProperty(propertyName), 1);
        assertEquals(msg.getShortProperty(propertyName), 1);
        assertEquals(msg.getIntProperty(propertyName), 1);
        assertEquals(msg.getLongProperty(propertyName), 1);

        stringValue = "1.5";
        msg.setStringProperty(propertyName, stringValue);
        assertEquals(msg.getFloatProperty(propertyName), 1.5, 0);
        assertEquals(msg.getDoubleProperty(propertyName), 1.5, 0);

        stringValue = "bad";
        msg.setStringProperty(propertyName, stringValue);
        try {
            msg.getByteProperty(propertyName);
            fail("Should have thrown exception");
        } catch (NumberFormatException e) {
        }
        try {
            msg.getShortProperty(propertyName);
            fail("Should have thrown exception");
        } catch (NumberFormatException e) {
        }
        try {
            msg.getIntProperty(propertyName);
            fail("Should have thrown exception");
        } catch (NumberFormatException e) {
        }
        try {
            msg.getLongProperty(propertyName);
            fail("Should have thrown exception");
        } catch (NumberFormatException e) {
        }
        try {
            msg.getFloatProperty(propertyName);
            fail("Should have thrown exception");
        } catch (NumberFormatException e) {
        }
        try {
            msg.getDoubleProperty(propertyName);
            fail("Should have thrown exception");
        } catch (NumberFormatException e) {
        }
        assertFalse(msg.getBooleanProperty(propertyName));
    }

    @Test
    public void testObjectPropertyConversion() throws JMSException {
        OpenWireJMSMessage msg = new OpenWireJMSMessage();
        String propertyName = "property";
        Object obj = new Object();
        try {
            // Bypass normal checks.
            msg.getOpenWireMessage().setProperty(propertyName, obj, false);
        } catch (Exception e) {
        }
        try {
            msg.getStringProperty(propertyName);
            fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getBooleanProperty(propertyName);
            fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getByteProperty(propertyName);
            fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getShortProperty(propertyName);
            fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getIntProperty(propertyName);
            fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getLongProperty(propertyName);
            fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getFloatProperty(propertyName);
            fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        try {
            msg.getDoubleProperty(propertyName);
            fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
    }

    @Test
    public void testReadOnlyProperties() throws JMSException {
        OpenWireJMSMessage msg = new OpenWireJMSMessage();
        String propertyName = "property";
        msg.setReadOnlyProperties(true);

        try {
            msg.setObjectProperty(propertyName, new Object());
            fail("Should have thrown exception");
        } catch (MessageNotWriteableException e) {
        }
        try {
            msg.setStringProperty(propertyName, "test");
            fail("Should have thrown exception");
        } catch (MessageNotWriteableException e) {
        }
        try {
            msg.setBooleanProperty(propertyName, true);
            fail("Should have thrown exception");
        } catch (MessageNotWriteableException e) {
        }
        try {
            msg.setByteProperty(propertyName, (byte) 1);
            fail("Should have thrown exception");
        } catch (MessageNotWriteableException e) {
        }
        try {
            msg.setShortProperty(propertyName, (short) 1);
            fail("Should have thrown exception");
        } catch (MessageNotWriteableException e) {
        }
        try {
            msg.setIntProperty(propertyName, 1);
            fail("Should have thrown exception");
        } catch (MessageNotWriteableException e) {
        }
        try {
            msg.setLongProperty(propertyName, 1);
            fail("Should have thrown exception");
        } catch (MessageNotWriteableException e) {
        }
        try {
            msg.setFloatProperty(propertyName, (float) 1.5);
            fail("Should have thrown exception");
        } catch (MessageNotWriteableException e) {
        }
        try {
            msg.setDoubleProperty(propertyName, 1.5);
            fail("Should have thrown exception");
        } catch (MessageNotWriteableException e) {
        }
    }

    @Test
    public void testIsExpired() throws JMSException {
        OpenWireJMSMessage msg = new OpenWireJMSMessage();
        msg.setJMSExpiration(System.currentTimeMillis() - 1);
        assertTrue(msg.isExpired());
        msg.setJMSExpiration(System.currentTimeMillis() + 10000);
        assertFalse(msg.isExpired());
    }
}
