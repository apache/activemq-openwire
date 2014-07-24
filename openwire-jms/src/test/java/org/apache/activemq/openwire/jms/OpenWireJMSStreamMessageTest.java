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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.jms.JMSException;
import javax.jms.MessageFormatException;
import javax.jms.MessageNotReadableException;
import javax.jms.MessageNotWriteableException;

import org.apache.activemq.openwire.jms.OpenWireJMSStreamMessage;
import org.junit.Test;

/**
 * Test for the OpenWireJMSStreamMessage facade class.
 */
public class OpenWireJMSStreamMessageTest {

    @Test
    public void testReadBoolean() throws Exception {
        OpenWireJMSStreamMessage msg = new OpenWireJMSStreamMessage();
        msg.writeBoolean(true);
        msg.reset();
        assertTrue(msg.readBoolean());
        msg.reset();
        assertTrue(msg.readString().equals("true"));
        msg.reset();
        try {
            msg.readByte();
            fail("Should have thrown exception");
        } catch (MessageFormatException mfe) {
        }
        msg.reset();
        try {
            msg.readShort();
            fail("Should have thrown exception");
        } catch (MessageFormatException mfe) {
        }
        msg.reset();
        try {
            msg.readInt();
            fail("Should have thrown exception");
        } catch (MessageFormatException mfe) {
        }
        msg.reset();
        try {
            msg.readLong();
            fail("Should have thrown exception");
        } catch (MessageFormatException mfe) {
        }
        msg.reset();
        try {
            msg.readFloat();
            fail("Should have thrown exception");
        } catch (MessageFormatException mfe) {
        }
        msg.reset();
        try {
            msg.readDouble();
            fail("Should have thrown exception");
        } catch (MessageFormatException mfe) {
        }
        msg.reset();
        try {
            msg.readChar();
            fail("Should have thrown exception");
        } catch (MessageFormatException mfe) {
        }
        msg.reset();
        try {
            msg.readBytes(new byte[1]);
            fail("Should have thrown exception");
        } catch (MessageFormatException mfe) {
        }
    }

    @Test
    public void testreadByte() throws Exception {
        OpenWireJMSStreamMessage msg = new OpenWireJMSStreamMessage();
        byte test = (byte) 4;
        msg.writeByte(test);
        msg.reset();
        assertTrue(msg.readByte() == test);
        msg.reset();
        assertTrue(msg.readShort() == test);
        msg.reset();
        assertTrue(msg.readInt() == test);
        msg.reset();
        assertTrue(msg.readLong() == test);
        msg.reset();
        assertTrue(msg.readString().equals(new Byte(test).toString()));
        msg.reset();
        try {
            msg.readBoolean();
            fail("Should have thrown exception");
        } catch (MessageFormatException mfe) {
        }
        msg.reset();
        try {
            msg.readFloat();
            fail("Should have thrown exception");
        } catch (MessageFormatException mfe) {
        }
        msg.reset();
        try {
            msg.readDouble();
            fail("Should have thrown exception");
        } catch (MessageFormatException mfe) {
        }
        msg.reset();
        try {
            msg.readChar();
            fail("Should have thrown exception");
        } catch (MessageFormatException mfe) {
        }
        msg.reset();
        try {
            msg.readBytes(new byte[1]);
            fail("Should have thrown exception");
        } catch (MessageFormatException mfe) {
        }
    }

    @Test
    public void testReadShort() throws Exception {
        OpenWireJMSStreamMessage msg = new OpenWireJMSStreamMessage();
        short test = (short) 4;
        msg.writeShort(test);
        msg.reset();
        assertTrue(msg.readShort() == test);
        msg.reset();
        assertTrue(msg.readInt() == test);
        msg.reset();
        assertTrue(msg.readLong() == test);
        msg.reset();
        assertTrue(msg.readString().equals(new Short(test).toString()));
        msg.reset();
        try {
            msg.readBoolean();
            fail("Should have thrown exception");
        } catch (MessageFormatException mfe) {
        }
        msg.reset();
        try {
            msg.readByte();
            fail("Should have thrown exception");
        } catch (MessageFormatException mfe) {
        }
        msg.reset();
        try {
            msg.readFloat();
            fail("Should have thrown exception");
        } catch (MessageFormatException mfe) {
        }
        msg.reset();
        try {
            msg.readDouble();
            fail("Should have thrown exception");
        } catch (MessageFormatException mfe) {
        }
        msg.reset();
        try {
            msg.readChar();
            fail("Should have thrown exception");
        } catch (MessageFormatException mfe) {
        }
        msg.reset();
        try {
            msg.readBytes(new byte[1]);
            fail("Should have thrown exception");
        } catch (MessageFormatException mfe) {
        }
    }

    @Test
    public void testReadChar() throws Exception {
        OpenWireJMSStreamMessage msg = new OpenWireJMSStreamMessage();
        char test = 'z';
        msg.writeChar(test);
        msg.reset();
        assertTrue(msg.readChar() == test);
        msg.reset();
        assertTrue(msg.readString().equals(new Character(test).toString()));
        msg.reset();
        try {
            msg.readBoolean();
            fail("Should have thrown exception");
        } catch (MessageFormatException mfe) {
        }
        msg.reset();
        try {
            msg.readByte();
            fail("Should have thrown exception");
        } catch (MessageFormatException mfe) {
        }
        msg.reset();
        try {
            msg.readShort();
            fail("Should have thrown exception");
        } catch (MessageFormatException mfe) {
        }
        msg.reset();
        try {
            msg.readInt();
            fail("Should have thrown exception");
        } catch (MessageFormatException mfe) {
        }
        msg.reset();
        try {
            msg.readLong();
            fail("Should have thrown exception");
        } catch (MessageFormatException mfe) {
        }
        msg.reset();
        try {
            msg.readFloat();
            fail("Should have thrown exception");
        } catch (MessageFormatException mfe) {
        }
        msg.reset();
        try {
            msg.readDouble();
            fail("Should have thrown exception");
        } catch (MessageFormatException mfe) {
        }
        msg.reset();
        try {
            msg.readBytes(new byte[1]);
            fail("Should have thrown exception");
        } catch (MessageFormatException mfe) {
        }
    }

    @Test
    public void testReadInt() throws Exception {
        OpenWireJMSStreamMessage msg = new OpenWireJMSStreamMessage();
        int test = 4;
        msg.writeInt(test);
        msg.reset();
        assertTrue(msg.readInt() == test);
        msg.reset();
        assertTrue(msg.readLong() == test);
        msg.reset();
        assertTrue(msg.readString().equals(new Integer(test).toString()));
        msg.reset();
        try {
            msg.readBoolean();
            fail("Should have thrown exception");
        } catch (MessageFormatException mfe) {
        }
        msg.reset();
        try {
            msg.readByte();
            fail("Should have thrown exception");
        } catch (MessageFormatException mfe) {
        }
        msg.reset();
        try {
            msg.readShort();
            fail("Should have thrown exception");
        } catch (MessageFormatException mfe) {
        }
        msg.reset();
        try {
            msg.readFloat();
            fail("Should have thrown exception");
        } catch (MessageFormatException mfe) {
        }
        msg.reset();
        try {
            msg.readDouble();
            fail("Should have thrown exception");
        } catch (MessageFormatException mfe) {
        }
        msg.reset();
        try {
            msg.readChar();
            fail("Should have thrown exception");
        } catch (MessageFormatException mfe) {
        }
        msg.reset();
        try {
            msg.readBytes(new byte[1]);
            fail("Should have thrown exception");
        } catch (MessageFormatException mfe) {
        }
    }

    @Test
    public void testReadLong() throws Exception {
        OpenWireJMSStreamMessage msg = new OpenWireJMSStreamMessage();
        long test = 4L;
        msg.writeLong(test);
        msg.reset();
        assertTrue(msg.readLong() == test);
        msg.reset();
        assertTrue(msg.readString().equals(Long.valueOf(test).toString()));
        msg.reset();
        try {
            msg.readBoolean();
            fail("Should have thrown exception");
        } catch (MessageFormatException mfe) {
        }
        msg.reset();
        try {
            msg.readByte();
            fail("Should have thrown exception");
        } catch (MessageFormatException mfe) {
        }
        msg.reset();
        try {
            msg.readShort();
            fail("Should have thrown exception");
        } catch (MessageFormatException mfe) {
        }
        msg.reset();
        try {
            msg.readInt();
            fail("Should have thrown exception");
        } catch (MessageFormatException mfe) {
        }
        msg.reset();
        try {
            msg.readFloat();
            fail("Should have thrown exception");
        } catch (MessageFormatException mfe) {
        }
        msg.reset();
        try {
            msg.readDouble();
            fail("Should have thrown exception");
        } catch (MessageFormatException mfe) {
        }
        msg.reset();
        try {
            msg.readChar();
            fail("Should have thrown exception");
        } catch (MessageFormatException mfe) {
        }
        msg.reset();
        try {
            msg.readBytes(new byte[1]);
            fail("Should have thrown exception");
        } catch (MessageFormatException mfe) {
        }
        msg = new OpenWireJMSStreamMessage();
        msg.writeObject(new Long("1"));
        // reset so it's readable now
        msg.reset();
        assertEquals(new Long("1"), msg.readObject());
    }

    @Test
    public void testReadFloat() throws Exception {
        OpenWireJMSStreamMessage msg = new OpenWireJMSStreamMessage();
        float test = 4.4f;
        msg.writeFloat(test);
        msg.reset();
        assertTrue(msg.readFloat() == test);
        msg.reset();
        assertTrue(msg.readDouble() == test);
        msg.reset();
        assertTrue(msg.readString().equals(new Float(test).toString()));
        msg.reset();
        try {
            msg.readBoolean();
            fail("Should have thrown exception");
        } catch (MessageFormatException mfe) {
        }
        msg.reset();
        try {
            msg.readByte();
            fail("Should have thrown exception");
        } catch (MessageFormatException mfe) {
        }
        msg.reset();
        try {
            msg.readShort();
            fail("Should have thrown exception");
        } catch (MessageFormatException mfe) {
        }
        msg.reset();
        try {
            msg.readInt();
            fail("Should have thrown exception");
        } catch (MessageFormatException mfe) {
        }
        msg.reset();
        try {
            msg.readLong();
            fail("Should have thrown exception");
        } catch (MessageFormatException mfe) {
        }
        msg.reset();
        try {
            msg.readChar();
            fail("Should have thrown exception");
        } catch (MessageFormatException mfe) {
        }
        msg.reset();
        try {
            msg.readBytes(new byte[1]);
            fail("Should have thrown exception");
        } catch (MessageFormatException mfe) {
        }
    }

    @Test
    public void testReadDouble() throws Exception {
        OpenWireJMSStreamMessage msg = new OpenWireJMSStreamMessage();
        double test = 4.4d;
        msg.writeDouble(test);
        msg.reset();
        assertTrue(msg.readDouble() == test);
        msg.reset();
        assertTrue(msg.readString().equals(new Double(test).toString()));
        msg.reset();
        try {
            msg.readBoolean();
            fail("Should have thrown exception");
        } catch (MessageFormatException mfe) {
        }
        msg.reset();
        try {
            msg.readByte();
            fail("Should have thrown exception");
        } catch (MessageFormatException mfe) {
        }
        msg.reset();
        try {
            msg.readShort();
            fail("Should have thrown exception");
        } catch (MessageFormatException mfe) {
        }
        msg.reset();
        try {
            msg.readInt();
            fail("Should have thrown exception");
        } catch (MessageFormatException mfe) {
        }
        msg.reset();
        try {
            msg.readLong();
            fail("Should have thrown exception");
        } catch (MessageFormatException mfe) {
        }
        msg.reset();
        try {
            msg.readFloat();
            fail("Should have thrown exception");
        } catch (MessageFormatException mfe) {
        }
        msg.reset();
        try {
            msg.readChar();
            fail("Should have thrown exception");
        } catch (MessageFormatException mfe) {
        }
        msg.reset();
        try {
            msg.readBytes(new byte[1]);
            fail("Should have thrown exception");
        } catch (MessageFormatException mfe) {
        }
    }

    @Test
    public void testReadString() throws Exception {
        OpenWireJMSStreamMessage msg = new OpenWireJMSStreamMessage();
        byte testByte = (byte) 2;
        msg.writeString(new Byte(testByte).toString());
        msg.reset();
        assertTrue(msg.readByte() == testByte);
        msg.clearBody();
        short testShort = 3;
        msg.writeString(new Short(testShort).toString());
        msg.reset();
        assertTrue(msg.readShort() == testShort);
        msg.clearBody();
        int testInt = 4;
        msg.writeString(new Integer(testInt).toString());
        msg.reset();
        assertTrue(msg.readInt() == testInt);
        msg.clearBody();
        long testLong = 6L;
        msg.writeString(new Long(testLong).toString());
        msg.reset();
        assertTrue(msg.readLong() == testLong);
        msg.clearBody();
        float testFloat = 6.6f;
        msg.writeString(new Float(testFloat).toString());
        msg.reset();
        assertTrue(msg.readFloat() == testFloat);
        msg.clearBody();
        double testDouble = 7.7d;
        msg.writeString(new Double(testDouble).toString());
        msg.reset();
        assertTrue(msg.readDouble() == testDouble);
        msg.clearBody();
        msg.writeString("true");
        msg.reset();
        assertTrue(msg.readBoolean());
        msg.clearBody();
        msg.writeString("a");
        msg.reset();
        try {
            msg.readChar();
            fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
        msg.clearBody();
        msg.writeString("777");
        msg.reset();
        try {
            msg.readBytes(new byte[3]);
            fail("Should have thrown exception");
        } catch (MessageFormatException e) {
        }
    }

    @Test
    public void testReadBigString() throws Exception {
        OpenWireJMSStreamMessage msg = new OpenWireJMSStreamMessage();
        try {
            // Test with a 1Meg String
            StringBuffer bigSB = new StringBuffer(1024 * 1024);
            for (int i = 0; i < 1024 * 1024; i++) {
                bigSB.append('a' + i % 26);
            }
            String bigString = bigSB.toString();

            msg.writeString(bigString);
            msg.reset();
            assertEquals(bigString, msg.readString());
        } catch (JMSException jmsEx) {
            jmsEx.printStackTrace();
            assertTrue(false);
        }
    }

    @Test
    public void testReadBytes() throws Exception {
        OpenWireJMSStreamMessage msg = new OpenWireJMSStreamMessage();
        try {
            byte[] test = new byte[50];
            for (int i = 0; i < test.length; i++) {
                test[i] = (byte) i;
            }
            msg.writeBytes(test);
            msg.reset();
            byte[] valid = new byte[test.length];
            msg.readBytes(valid);
            for (int i = 0; i < valid.length; i++) {
                assertTrue(valid[i] == test[i]);
            }
            msg.reset();
            try {
                msg.readByte();
                fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readShort();
                fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readInt();
                fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readLong();
                fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readFloat();
                fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readChar();
                fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
            msg.reset();
            try {
                msg.readString();
                fail("Should have thrown exception");
            } catch (MessageFormatException mfe) {
            }
        } catch (JMSException jmsEx) {
            jmsEx.printStackTrace();
            assertTrue(false);
        }
    }

    @Test
    public void testReadObject() throws Exception {
        OpenWireJMSStreamMessage msg = new OpenWireJMSStreamMessage();
        try {
            byte testByte = (byte) 2;
            msg.writeByte(testByte);
            msg.reset();
            assertTrue(((Byte) msg.readObject()).byteValue() == testByte);
            msg.clearBody();

            short testShort = 3;
            msg.writeShort(testShort);
            msg.reset();
            assertTrue(((Short) msg.readObject()).shortValue() == testShort);
            msg.clearBody();

            int testInt = 4;
            msg.writeInt(testInt);
            msg.reset();
            assertTrue(((Integer) msg.readObject()).intValue() == testInt);
            msg.clearBody();

            long testLong = 6L;
            msg.writeLong(testLong);
            msg.reset();
            assertTrue(((Long) msg.readObject()).longValue() == testLong);
            msg.clearBody();

            float testFloat = 6.6f;
            msg.writeFloat(testFloat);
            msg.reset();
            assertTrue(((Float) msg.readObject()).floatValue() == testFloat);
            msg.clearBody();

            double testDouble = 7.7d;
            msg.writeDouble(testDouble);
            msg.reset();
            assertTrue(((Double) msg.readObject()).doubleValue() == testDouble);
            msg.clearBody();

            char testChar = 'z';
            msg.writeChar(testChar);
            msg.reset();
            assertTrue(((Character) msg.readObject()).charValue() == testChar);
            msg.clearBody();

            byte[] data = new byte[50];
            for (int i = 0; i < data.length; i++) {
                data[i] = (byte) i;
            }
            msg.writeBytes(data);
            msg.reset();
            byte[] valid = (byte[]) msg.readObject();
            assertTrue(valid.length == data.length);
            for (int i = 0; i < valid.length; i++) {
                assertTrue(valid[i] == data[i]);
            }
            msg.clearBody();
            msg.writeBoolean(true);
            msg.reset();
            assertTrue(((Boolean) msg.readObject()).booleanValue());
        } catch (JMSException jmsEx) {
            jmsEx.printStackTrace();
            assertTrue(false);
        }
    }

    @Test
    public void testClearBody() throws JMSException {
        OpenWireJMSStreamMessage streamMessage = new OpenWireJMSStreamMessage();
        try {
            streamMessage.writeObject(new Long(2));
            streamMessage.clearBody();
            assertFalse(streamMessage.isReadOnlyBody());
            streamMessage.writeObject(new Long(2));
            streamMessage.readObject();
            fail("should throw exception");
        } catch (MessageNotReadableException mnwe) {
        } catch (MessageNotWriteableException mnwe) {
            fail("should be writeable");
        }
    }

    @Test
    public void testReset() throws JMSException {
        OpenWireJMSStreamMessage streamMessage = new OpenWireJMSStreamMessage();
        try {
            streamMessage.writeDouble(24.5);
            streamMessage.writeLong(311);
        } catch (MessageNotWriteableException mnwe) {
            fail("should be writeable");
        }
        streamMessage.reset();
        try {
            assertTrue(streamMessage.isReadOnlyBody());
            assertEquals(streamMessage.readDouble(), 24.5, 0);
            assertEquals(streamMessage.readLong(), 311);
        } catch (MessageNotReadableException mnre) {
            fail("should be readable");
        }
        try {
            streamMessage.writeInt(33);
            fail("should throw exception");
        } catch (MessageNotWriteableException mnwe) {
        }
    }

    @Test
    public void testReadOnlyBody() throws JMSException {
        OpenWireJMSStreamMessage message = new OpenWireJMSStreamMessage();
        try {
            message.writeBoolean(true);
            message.writeByte((byte) 1);
            message.writeBytes(new byte[1]);
            message.writeBytes(new byte[3], 0, 2);
            message.writeChar('a');
            message.writeDouble(1.5);
            message.writeFloat((float) 1.5);
            message.writeInt(1);
            message.writeLong(1);
            message.writeObject("stringobj");
            message.writeShort((short) 1);
            message.writeString("string");
        } catch (MessageNotWriteableException mnwe) {
            fail("Should be writeable");
        }
        message.reset();
        try {
            message.readBoolean();
            message.readByte();
            assertEquals(1, message.readBytes(new byte[10]));
            assertEquals(-1, message.readBytes(new byte[10]));
            assertEquals(2, message.readBytes(new byte[10]));
            assertEquals(-1, message.readBytes(new byte[10]));
            message.readChar();
            message.readDouble();
            message.readFloat();
            message.readInt();
            message.readLong();
            message.readString();
            message.readShort();
            message.readString();
        } catch (MessageNotReadableException mnwe) {
            fail("Should be readable");
        }
        try {
            message.writeBoolean(true);
            fail("Should have thrown exception");
        } catch (MessageNotWriteableException mnwe) {
        }
        try {
            message.writeByte((byte) 1);
            fail("Should have thrown exception");
        } catch (MessageNotWriteableException mnwe) {
        }
        try {
            message.writeBytes(new byte[1]);
            fail("Should have thrown exception");
        } catch (MessageNotWriteableException mnwe) {
        }
        try {
            message.writeBytes(new byte[3], 0, 2);
            fail("Should have thrown exception");
        } catch (MessageNotWriteableException mnwe) {
        }
        try {
            message.writeChar('a');
            fail("Should have thrown exception");
        } catch (MessageNotWriteableException mnwe) {
        }
        try {
            message.writeDouble(1.5);
            fail("Should have thrown exception");
        } catch (MessageNotWriteableException mnwe) {
        }
        try {
            message.writeFloat((float) 1.5);
            fail("Should have thrown exception");
        } catch (MessageNotWriteableException mnwe) {
        }
        try {
            message.writeInt(1);
            fail("Should have thrown exception");
        } catch (MessageNotWriteableException mnwe) {
        }
        try {
            message.writeLong(1);
            fail("Should have thrown exception");
        } catch (MessageNotWriteableException mnwe) {
        }
        try {
            message.writeObject("stringobj");
            fail("Should have thrown exception");
        } catch (MessageNotWriteableException mnwe) {
        }
        try {
            message.writeShort((short) 1);
            fail("Should have thrown exception");
        } catch (MessageNotWriteableException mnwe) {
        }
        try {
            message.writeString("string");
            fail("Should have thrown exception");
        } catch (MessageNotWriteableException mnwe) {
        }
    }

    @Test
    public void testWriteOnlyBody() throws JMSException {
        OpenWireJMSStreamMessage message = new OpenWireJMSStreamMessage();
        message.clearBody();
        try {
            message.writeBoolean(true);
            message.writeByte((byte) 1);
            message.writeBytes(new byte[1]);
            message.writeBytes(new byte[3], 0, 2);
            message.writeChar('a');
            message.writeDouble(1.5);
            message.writeFloat((float) 1.5);
            message.writeInt(1);
            message.writeLong(1);
            message.writeObject("stringobj");
            message.writeShort((short) 1);
            message.writeString("string");
        } catch (MessageNotWriteableException mnwe) {
            fail("Should be writeable");
        }
        try {
            message.readBoolean();
            fail("Should have thrown exception");
        } catch (MessageNotReadableException mnwe) {
        }
        try {
            message.readByte();
            fail("Should have thrown exception");
        } catch (MessageNotReadableException e) {
        }
        try {
            message.readBytes(new byte[1]);
            fail("Should have thrown exception");
        } catch (MessageNotReadableException e) {
        }
        try {
            message.readBytes(new byte[2]);
            fail("Should have thrown exception");
        } catch (MessageNotReadableException e) {
        }
        try {
            message.readChar();
            fail("Should have thrown exception");
        } catch (MessageNotReadableException e) {
        }
        try {
            message.readDouble();
            fail("Should have thrown exception");
        } catch (MessageNotReadableException e) {
        }
        try {
            message.readFloat();
            fail("Should have thrown exception");
        } catch (MessageNotReadableException e) {
        }
        try {
            message.readInt();
            fail("Should have thrown exception");
        } catch (MessageNotReadableException e) {
        }
        try {
            message.readLong();
            fail("Should have thrown exception");
        } catch (MessageNotReadableException e) {
        }
        try {
            message.readString();
            fail("Should have thrown exception");
        } catch (MessageNotReadableException e) {
        }
        try {
            message.readShort();
            fail("Should have thrown exception");
        } catch (MessageNotReadableException e) {
        }
        try {
            message.readString();
            fail("Should have thrown exception");
        } catch (MessageNotReadableException e) {
        }
    }

    @Test
    public void testWriteObject() throws Exception {
        try {
            OpenWireJMSStreamMessage message = new OpenWireJMSStreamMessage();
            message.clearBody();
            message.writeObject("test");
            message.writeObject(new Character('a'));
            message.writeObject(new Boolean(false));
            message.writeObject(new Byte((byte) 2));
            message.writeObject(new Short((short) 2));
            message.writeObject(new Integer(2));
            message.writeObject(new Long(2l));
            message.writeObject(new Float(2.0f));
            message.writeObject(new Double(2.0d));
        } catch (Exception e) {
            fail(e.getMessage());
        }
        try {
            OpenWireJMSStreamMessage message = new OpenWireJMSStreamMessage();
            message.clearBody();
            message.writeObject(new Object());
            fail("should throw an exception");
        } catch (MessageFormatException e) {
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
