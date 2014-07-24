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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Map;

import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;

import org.apache.activemq.openwire.codec.OpenWireFormat;
import org.apache.activemq.openwire.commands.CommandTypes;
import org.apache.activemq.openwire.commands.OpenWireBytesMessage;
import org.apache.activemq.openwire.commands.OpenWireDestination;
import org.apache.activemq.openwire.commands.OpenWireMessage;
import org.apache.activemq.openwire.commands.OpenWireObjectMessage;
import org.apache.activemq.openwire.commands.OpenWireTempTopic;
import org.apache.activemq.openwire.commands.OpenWireTopic;
import org.fusesource.hawtbuf.Buffer;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenWireMessageTest {

    private static final Logger LOG = LoggerFactory.getLogger(OpenWireMessageTest.class);

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
        this.jmsReplyTo = new OpenWireTempTopic("test.replyto.topic:001");
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
        OpenWireMessage msg = new OpenWireMessage();
        assertEquals(msg.getDataStructureType(), CommandTypes.OPENWIRE_MESSAGE);
    }

//    @Test
//    public void testHashCode() throws Exception {
//        OpenWireMessage msg = new OpenWireMessage();
//        msg.setMessageId(this.jmsMessageID);
//        assertTrue(msg.getMessageId().hashCode() == jmsMessageID.hashCode());
//    }

    @Test
    public void testSetToForeignJMSID() throws Exception {
        OpenWireMessage msg = new OpenWireMessage();
        msg.setMessageId("ID:EMS-SERVER.8B443C380083:429");
    }

    @Test
    public void testEqualsObject() throws Exception {
        OpenWireMessage msg1 = new OpenWireMessage();
        OpenWireMessage msg2 = new OpenWireMessage();
        msg1.setMessageId(this.jmsMessageID);
        assertTrue(!msg1.equals(msg2));
        msg2.setMessageId(this.jmsMessageID);
        assertTrue(msg1.equals(msg2));
    }

    @Test
    public void testShallowCopy() throws Exception {
        OpenWireMessage msg1 = new OpenWireMessage();
        msg1.setMessageId(jmsMessageID);
        OpenWireMessage msg2 = msg1.copy();
        assertTrue(msg1 != msg2 && msg1.equals(msg2));
    }

//    @Test
//    public void testCopy() throws Exception {
//        this.jmsMessageID = "testid";
//        this.jmsCorrelationID = "testcorrelationid";
//        this.jmsDestination = new OpenWireTopic("test.topic");
//        this.jmsReplyTo = new OpenWireTempTopic("test.replyto.topic:001");
//        this.jmsDeliveryMode = Message.DEFAULT_DELIVERY_MODE;
//        this.jmsRedelivered = true;
//        this.jmsType = "test type";
//        this.jmsExpiration = 100000;
//        this.jmsPriority = 5;
//        this.jmsTimestamp = System.currentTimeMillis();
//        this.readOnlyMessage = false;
//
//        OpenWireMessage msg1 = new OpenWireMessage();
//        msg1.setMessageId(this.jmsMessageID);
//        msg1.setCorrelationId(this.jmsCorrelationID);
//        msg1.setDestination(this.jmsDestination);
//        msg1.setReplyTo(this.jmsReplyTo);
//        msg1.setPersistent(this.jmsDeliveryMode == DeliveryMode.PERSISTENT);
//        msg1.setRedelivered(this.jmsRedelivered);
//        msg1.setType(this.jmsType);
//        msg1.setExpiration(this.jmsExpiration);
//        msg1.setPriority((byte) this.jmsPriority);
//        msg1.setTimestamp(this.jmsTimestamp);
//        msg1.setReadOnlyProperties(true);
//        OpenWireMessage msg2 = new OpenWireMessage();
//        msg1.copy(msg2);
//        assertEquals(msg1.getMessageId(), msg2.getMessageId());
//        assertTrue(msg1.getCorrelationId().equals(msg2.getCorrelationId()));
//        assertTrue(msg1.getDestination().equals(msg2.getDestination()));
//        assertTrue(msg1.getReplyTo().equals(msg2.getReplyTo()));
//        assertTrue(msg1.isPersistent() == msg2.isPersistent());
//        assertTrue(msg1.isRedelivered() == msg2.isRedelivered());
//        assertTrue(msg1.getType().equals(msg2.getType()));
//        assertTrue(msg1.getExpiration() == msg2.getExpiration());
//        assertTrue(msg1.getPriority() == msg2.getPriority());
//        assertTrue(msg1.getTimestamp() == msg2.getTimestamp());
//
//        LOG.info("Message is:  " + msg1);
//    }

    @Test
    public void testGetAndSetMessageId() throws Exception {
        OpenWireMessage msg = new OpenWireMessage();
        msg.setMessageId(this.jmsMessageID);
        assertEquals(msg.getMessageId().toString(), this.jmsMessageID);
    }

    @Test
    public void testGetAndSetTimestamp() {
        OpenWireMessage msg = new OpenWireMessage();
        msg.setTimestamp(this.jmsTimestamp);
        assertTrue(msg.getTimestamp() == this.jmsTimestamp);
    }

    @Test
    public void testGetCorrelationIDAsBytes() throws Exception {
        OpenWireMessage msg = new OpenWireMessage();
        msg.setCorrelationId(this.jmsCorrelationID);
        byte[] testbytes = msg.getCorrelationIdAsBytes();
        String str2 = new String(testbytes);
        assertTrue(this.jmsCorrelationID.equals(str2));
    }

    @Test
    public void testSetCorrelationIDAsBytes() throws Exception {
        OpenWireMessage msg = new OpenWireMessage();
        byte[] testbytes = this.jmsCorrelationID.getBytes();
        msg.setCorrelationIdAsBytes(testbytes);
        testbytes = msg.getCorrelationIdAsBytes();
        String str2 = new String(testbytes);
        assertTrue(this.jmsCorrelationID.equals(str2));
    }

    @Test
    public void testGetAndSetCorrelationID() {
        OpenWireMessage msg = new OpenWireMessage();
        msg.setCorrelationId(this.jmsCorrelationID);
        assertTrue(msg.getCorrelationId().equals(this.jmsCorrelationID));
    }

    @Test
    public void testGetAndSetJMSReplyTo() throws JMSException {
        OpenWireMessage msg = new OpenWireMessage();
        msg.setReplyTo(this.jmsReplyTo);
        assertTrue(msg.getReplyTo().equals(this.jmsReplyTo));
    }

    @Test
    public void testGetAndSetJMSDestination() throws Exception {
        OpenWireMessage msg = new OpenWireMessage();
        msg.setDestination(this.jmsDestination);
        assertTrue(msg.getDestination().equals(this.jmsDestination));
    }

    @Test
    public void testGetAndSetPersistentFlag() {
        OpenWireMessage msg = new OpenWireMessage();
        boolean persistent = this.jmsDeliveryMode == DeliveryMode.PERSISTENT;
        msg.setPersistent(persistent);
        assertTrue(msg.isPersistent() == persistent);
    }

    @Test
    public void testGetAndSetRedelivered() {
        OpenWireMessage msg = new OpenWireMessage();
        msg.setRedelivered(this.jmsRedelivered);
        assertTrue(msg.isRedelivered() == this.jmsRedelivered);
    }

    @Test
    public void testGetAndSetType() {
        OpenWireMessage msg = new OpenWireMessage();
        msg.setType(this.jmsType);
        assertTrue(msg.getType().equals(this.jmsType));
    }

    @Test
    public void testGetAndSetExpiration() {
        OpenWireMessage msg = new OpenWireMessage();
        msg.setExpiration(this.jmsExpiration);
        assertTrue(msg.getExpiration() == this.jmsExpiration);
    }

    @Test
    public void testGetAndSetPriority() {
        OpenWireMessage msg = new OpenWireMessage();
        msg.setPriority((byte) this.jmsPriority);
        assertTrue(msg.getPriority() == this.jmsPriority);

        msg.setPriority((byte) -90);
        assertEquals(0, msg.getPriority());

        msg.setPriority((byte) 90);
        assertEquals(9, msg.getPriority());
    }

    @Test
    public void testClearProperties() throws JMSException {
        OpenWireMessage msg = new OpenWireMessage();
        msg.setProperty("test", "test");
        msg.setContent(new Buffer(new byte[1], 0, 0));
        msg.setMessageId(this.jmsMessageID);
        msg.clearProperties();
        assertNull(msg.getProperty("test"));
        assertNotNull(msg.getMessageId());
        assertNotNull(msg.getContent());
    }

    @Test
    public void testPropertyExists() throws JMSException {
        OpenWireMessage msg = new OpenWireMessage();
        msg.setProperty("test", "test");
        assertTrue(msg.propertyExists("test"));

        msg.setProperty("JMSXDeliveryCount", 1);
        assertTrue(msg.propertyExists("JMSXDeliveryCount"));
    }

    @Test
    public void testGetBooleanProperty() throws JMSException {
        OpenWireMessage msg = new OpenWireMessage();
        String name = "booleanProperty";
        msg.setProperty(name, true);
        assertTrue((Boolean) msg.getProperty(name));
    }

    @Test
    public void testGetByteProperty() throws JMSException {
        OpenWireMessage msg = new OpenWireMessage();
        String name = "byteProperty";
        msg.setProperty(name, (byte) 1);
        assertTrue((Byte) msg.getProperty(name) == 1);
    }

    @Test
    public void testGetShortProperty() throws JMSException {
        OpenWireMessage msg = new OpenWireMessage();
        String name = "shortProperty";
        msg.setProperty(name, (short) 1);
        assertTrue((Short) msg.getProperty(name) == 1);
    }

    @Test
    public void testGetIntProperty() throws JMSException {
        OpenWireMessage msg = new OpenWireMessage();
        String name = "intProperty";
        msg.setProperty(name, 1);
        assertTrue((Integer) msg.getProperty(name) == 1);
    }

    @Test
    public void testGetLongProperty() throws JMSException {
        OpenWireMessage msg = new OpenWireMessage();
        String name = "longProperty";
        msg.setProperty(name, 1L);
        assertTrue((Long) msg.getProperty(name) == 1L);
    }

    @Test
    public void testGetFloatProperty() throws JMSException {
        OpenWireMessage msg = new OpenWireMessage();
        String name = "floatProperty";
        msg.setProperty(name, 1.3f);
        assertTrue((Float) msg.getProperty(name) == 1.3f);
    }

    @Test
    public void testGetDoubleProperty() throws JMSException {
        OpenWireMessage msg = new OpenWireMessage();
        String name = "doubleProperty";
        msg.setProperty(name, 1.3d);
        assertTrue((Double) msg.getProperty(name) == 1.3);
    }

    @Test
    public void testGetStringProperty() throws JMSException {
        OpenWireMessage msg = new OpenWireMessage();
        String name = "stringProperty";
        msg.setProperty(name, name);
        assertTrue(msg.getProperty(name).equals(name));
    }

    @Test
    public void testgetProperty() throws JMSException {
        OpenWireMessage msg = new OpenWireMessage();
        String name = "floatProperty";
        msg.setProperty(name, 1.3f);
        assertTrue(msg.getProperty(name) instanceof Float);
        assertTrue(((Float) msg.getProperty(name)).floatValue() == 1.3f);
    }

    @Test
    public void testPropertiesInt() throws Exception {
        OpenWireObjectMessage message = new OpenWireObjectMessage();
        message.setProperty("TestProp", 333);
        fakeUnmarshal(message);
        roundTripProperties(message);
    }

    @Test
    public void testPropertiesString() throws Exception {
        OpenWireObjectMessage message = new OpenWireObjectMessage();
        message.setProperty("TestProp", "Value");
        fakeUnmarshal(message);
        roundTripProperties(message);
    }

    @Test
    public void testPropertiesObject() throws Exception {
        OpenWireObjectMessage message = new OpenWireObjectMessage();
        message.setProperty("TestProp", "Value");
        fakeUnmarshal(message);
        roundTripProperties(message);
    }

    @Test
    public void testPropertiesObjectNoMarshalling() throws Exception {
        OpenWireObjectMessage message = new OpenWireObjectMessage();
        message.setProperty("TestProp", "Value");
        roundTripProperties(message);
    }

    private void roundTripProperties(OpenWireObjectMessage message) throws IOException, JMSException {
        OpenWireObjectMessage copy = new OpenWireObjectMessage();
        for (Map.Entry<String, Object> prop : message.getProperties().entrySet()) {
            LOG.debug("{} -> {}", prop.getKey(), prop.getValue().getClass());
            copy.setProperty(prop.getKey(), prop.getValue());
        }
    }

    private void fakeUnmarshal(OpenWireObjectMessage message) throws Exception {
        OpenWireFormat format = new OpenWireFormat(OpenWireFormat.DEFAULT_WIRE_VERSION);
        message.beforeMarshall(format);
        message.afterMarshall(format);

        Buffer seq = message.getMarshalledProperties();
        message.clearProperties();
        message.setMarshalledProperties(seq);
    }

    @Test
    public void testSetNullProperty() throws JMSException {
        OpenWireMessage msg = new OpenWireMessage();
        String name = "cheese";
        msg.setProperty(name, "Cheddar");
        assertEquals("Cheddar", msg.getProperty(name));

        msg.setProperty(name, null);
        assertEquals(null, msg.getProperty(name));
    }

    @Test
    public void testSetNullPropertyName() throws JMSException {
        OpenWireMessage msg = new OpenWireMessage();

        try {
            msg.setProperty(null, "Cheese");
            fail("Should have thrown exception");
        } catch (IllegalArgumentException e) {
            LOG.info("Worked, caught: " + e);
        }
    }

    @Test
    public void testSetEmptyPropertyName() throws JMSException {
        OpenWireMessage msg = new OpenWireMessage();

        try {
            msg.setProperty("", "Cheese");
            fail("Should have thrown exception");
        } catch (IllegalArgumentException e) {
            LOG.info("Worked, caught: " + e);
        }
    }

    @Test
    public void testGetAndSetDeliveryCount() throws JMSException {
        OpenWireMessage msg = new OpenWireMessage();
        msg.setRedeliveryCounter(1);
        int count = msg.getRedeliveryCounter();
        assertTrue("expected delivery count = 1 - got: " + count, count == 1);
    }

    @Test
    public void testClearBody() throws JMSException {
        OpenWireBytesMessage message = new OpenWireBytesMessage();
        message.clearBody();
        assertNull(message.getContent());
    }

    @Test
    public void testIsExpired() {
        OpenWireMessage msg = new OpenWireMessage();
        msg.setExpiration(System.currentTimeMillis() - 1);
        assertTrue(msg.isExpired());
        msg.setExpiration(System.currentTimeMillis() + 10000);
        assertFalse(msg.isExpired());
    }
}
