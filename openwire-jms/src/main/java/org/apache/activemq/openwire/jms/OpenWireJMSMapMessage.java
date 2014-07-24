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

import java.util.Enumeration;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageFormatException;

import org.apache.activemq.openwire.commands.OpenWireMapMessage;
import org.fusesource.hawtbuf.UTF8Buffer;

/**
 * Wrapper class that provides MapMessage compliant mappings to the OpenWireMapMessage
 */
public class OpenWireJMSMapMessage extends OpenWireJMSMessage implements MapMessage {

    private final OpenWireMapMessage message;

    /**
     * Creates a new instance that wraps a new OpenWireMessage instance.
     */
    public OpenWireJMSMapMessage() {
        this(new OpenWireMapMessage());
    }

    /**
     * Creates a new instance that wraps the given OpenWireMessage
     *
     * @param message
     *        the OpenWireMessage to wrap.
     */
    public OpenWireJMSMapMessage(OpenWireMapMessage message) {
        this.message = message;
    }

    @Override
    public OpenWireJMSMapMessage copy() throws JMSException {
        OpenWireJMSMapMessage other = new OpenWireJMSMapMessage(message.copy());
        return other;
    }

    @Override
    public void clearBody() throws JMSException {
        super.clearBody();
        message.clearBody();
    }

    @Override
    public boolean getBoolean(String name) throws JMSException {
        Object value = message.getObject(name);
        if (value == null) {
            return false;
        }
        if (value instanceof Boolean) {
            return ((Boolean)value).booleanValue();
        }
        if (value instanceof UTF8Buffer) {
            return Boolean.valueOf(value.toString()).booleanValue();
        }
        if (value instanceof String) {
            return Boolean.valueOf(value.toString()).booleanValue();
        } else {
            throw new MessageFormatException(" cannot read a boolean from " + value.getClass().getName());
        }
    }

    @Override
    public byte getByte(String name) throws JMSException {
        Object value = message.getObject(name);
        if (value == null) {
            return 0;
        } else if (value instanceof Byte) {
            return ((Byte)value).byteValue();
        } else if (value instanceof UTF8Buffer) {
            return Byte.valueOf(value.toString()).byteValue();
        } else if (value instanceof String) {
            return Byte.valueOf(value.toString()).byteValue();
        } else {
            throw new MessageFormatException(" cannot read a byte from " + value.getClass().getName());
        }
    }

    @Override
    public short getShort(String name) throws JMSException {
        Object value = message.getObject(name);
        if (value == null) {
            return 0;
        } else if (value instanceof Short) {
            return ((Short)value).shortValue();
        } else if (value instanceof Byte) {
            return ((Byte)value).shortValue();
        } else if (value instanceof UTF8Buffer) {
            return Short.valueOf(value.toString()).shortValue();
        } else if (value instanceof String) {
            return Short.valueOf(value.toString()).shortValue();
        } else {
            throw new MessageFormatException(" cannot read a short from " + value.getClass().getName());
        }
    }

    @Override
    public char getChar(String name) throws JMSException {
        Object value = message.getObject(name);
        if (value == null) {
            throw new NullPointerException();
        } else if (value instanceof Character) {
            return ((Character)value).charValue();
        } else {
            throw new MessageFormatException(" cannot read a short from " + value.getClass().getName());
        }
    }

    @Override
    public int getInt(String name) throws JMSException {
        Object value = message.getObject(name);
        if (value == null) {
            return 0;
        } else if (value instanceof Integer) {
            return ((Integer)value).intValue();
        } else if (value instanceof Short) {
            return ((Short)value).intValue();
        } else if (value instanceof Byte) {
            return ((Byte)value).intValue();
        } else if (value instanceof UTF8Buffer) {
            return Integer.valueOf(value.toString()).intValue();
        } else if (value instanceof String) {
            return Integer.valueOf(value.toString()).intValue();
        } else {
            throw new MessageFormatException(" cannot read an int from " + value.getClass().getName());
        }
    }

    @Override
    public long getLong(String name) throws JMSException {
        Object value = message.getObject(name);
        if (value == null) {
            return 0;
        } else if (value instanceof Long) {
            return ((Long)value).longValue();
        } else if (value instanceof Integer) {
            return ((Integer)value).longValue();
        } else if (value instanceof Short) {
            return ((Short)value).longValue();
        } else if (value instanceof Byte) {
            return ((Byte)value).longValue();
        } else if (value instanceof UTF8Buffer) {
            return Long.valueOf(value.toString()).longValue();
        } else if (value instanceof String) {
            return Long.valueOf(value.toString()).longValue();
        } else {
            throw new MessageFormatException(" cannot read a long from " + value.getClass().getName());
        }
    }

    @Override
    public float getFloat(String name) throws JMSException {
        Object value = message.getObject(name);
        if (value == null) {
            return 0;
        } else if (value instanceof Float) {
            return ((Float)value).floatValue();
        } else if (value instanceof UTF8Buffer) {
            return Float.valueOf(value.toString()).floatValue();
        } else if (value instanceof String) {
            return Float.valueOf(value.toString()).floatValue();
        } else {
            throw new MessageFormatException(" cannot read a float from " + value.getClass().getName());
        }
    }

    @Override
    public double getDouble(String name) throws JMSException {
        Object value = message.getObject(name);
        if (value == null) {
            return 0;
        } else if (value instanceof Double) {
            return ((Double)value).doubleValue();
        } else if (value instanceof Float) {
            return ((Float)value).floatValue();
        } else if (value instanceof UTF8Buffer) {
            return Float.valueOf(value.toString()).floatValue();
        } else if (value instanceof String) {
            return Float.valueOf(value.toString()).floatValue();
        } else {
            throw new MessageFormatException(" cannot read a double from " + value.getClass().getName());
        }
    }

    @Override
    public String getString(String name) throws JMSException {
        Object value = message.getObject(name);
        if (value == null) {
            return null;
        } else if (value instanceof byte[]) {
            throw new MessageFormatException("Use getBytes to read a byte array");
        } else {
            return value.toString();
        }
    }

    @Override
    public byte[] getBytes(String name) throws JMSException {
        Object value = message.getObject(name);
        if (value instanceof byte[]) {
            return (byte[])value;
        } else {
            throw new MessageFormatException(" cannot read a byte[] from " + value.getClass().getName());
        }
    }

    @Override
    public Object getObject(String name) throws JMSException {
        Object value = message.getObject(name);
        if (value instanceof UTF8Buffer) {
            value = value.toString();
        }

        return value;
    }

    @Override
    public Enumeration<String> getMapNames() throws JMSException {
        return message.getMapNames();
    }

    @Override
    public void setBoolean(String name, boolean value) throws JMSException {
        checkReadOnlyBody();
        message.setObject(name, value ? Boolean.TRUE : Boolean.FALSE);
    }

    @Override
    public void setByte(String name, byte value) throws JMSException {
        checkReadOnlyBody();
        message.setObject(name, Byte.valueOf(value));
    }

    @Override
    public void setShort(String name, short value) throws JMSException {
        checkReadOnlyBody();
        message.setObject(name, Short.valueOf(value));
    }

    @Override
    public void setChar(String name, char value) throws JMSException {
        checkReadOnlyBody();
        message.setObject(name, Character.valueOf(value));
    }

    @Override
    public void setInt(String name, int value) throws JMSException {
        checkReadOnlyBody();
        message.setObject(name, Integer.valueOf(value));
    }

    @Override
    public void setLong(String name, long value) throws JMSException {
        checkReadOnlyBody();
        message.setObject(name, Long.valueOf(value));
    }

    @Override
    public void setFloat(String name, float value) throws JMSException {
        checkReadOnlyBody();
        message.setObject(name, Float.valueOf(value));
    }

    @Override
    public void setDouble(String name, double value) throws JMSException {
        checkReadOnlyBody();
        message.setObject(name, Double.valueOf(value));
    }

    @Override
    public void setString(String name, String value) throws JMSException {
        checkReadOnlyBody();
        message.setObject(name, value);
    }

    @Override
    public void setBytes(String name, byte[] value) throws JMSException {
        checkReadOnlyBody();
        if (value != null) {
            message.setObject(name, value);
        } else {
            message.removeObject(name);
        }
    }

    @Override
    public void setBytes(String name, byte[] value, int offset, int length) throws JMSException {
        checkReadOnlyBody();
        byte[] data = new byte[length];
        System.arraycopy(value, offset, data, 0, length);
        message.setObject(name, data);
    }

    @Override
    public void setObject(String name, Object value) throws JMSException {
        checkReadOnlyBody();
        message.setObject(name, value);
    }

    @Override
    public boolean itemExists(String name) throws JMSException {
        return message.itemExists(name);
    }
}
