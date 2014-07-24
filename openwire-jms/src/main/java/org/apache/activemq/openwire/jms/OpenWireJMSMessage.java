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
import java.util.Vector;
import java.util.concurrent.Callable;

import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageFormatException;
import javax.jms.MessageNotReadableException;
import javax.jms.MessageNotWriteableException;

import org.apache.activemq.openwire.codec.OpenWireConstants;
import org.apache.activemq.openwire.commands.OpenWireDestination;
import org.apache.activemq.openwire.commands.OpenWireMessage;
import org.apache.activemq.openwire.jms.utils.OpenWireMessagePropertyGetter;
import org.apache.activemq.openwire.jms.utils.OpenWireMessagePropertySetter;
import org.apache.activemq.openwire.jms.utils.TypeConversionSupport;
import org.apache.activemq.openwire.utils.ExceptionSupport;
import org.fusesource.hawtbuf.UTF8Buffer;

/**
 * A JMS Message implementation that wraps the basic OpenWireMessage instance
 * to enforce adherence to the JMS specification rules for the javax.jms.Message
 * type.
 */
public class OpenWireJMSMessage implements Message {

    private final OpenWireMessage message;

    private Callable<Void> acknowledgeCallback;
    private boolean readOnlyBody;
    private boolean readOnlyProperties;

    /**
     * Creates a new instance that wraps a new OpenWireMessage instance.
     */
    public OpenWireJMSMessage() {
        this(new OpenWireMessage());
    }

    /**
     * Creates a new instance that wraps the given OpenWireMessage
     *
     * @param message
     *        the OpenWireMessage to wrap.
     */
    public OpenWireJMSMessage(OpenWireMessage message) {
        this.message = message;
    }

    /**
     * Copies this message into a new OpenWireJMSMessage instance.
     *
     * @return a new copy of this message and it's contents.
     *
     * @throws JMSException if an error occurs during the copy operation.
     */
    public OpenWireJMSMessage copy() throws JMSException {
        OpenWireJMSMessage copy = new OpenWireJMSMessage(message.copy());
        copy(copy);
        return copy;
    }

    protected void copy(OpenWireJMSMessage copy) {
        copy.readOnlyBody = readOnlyBody;
        copy.readOnlyProperties = readOnlyProperties;
    }

    /**
     * @return the wrapped OpenWireMessage instance.
     */
    public OpenWireMessage getOpenWireMessage() {
        return this.message;
    }

    @Override
    public String getJMSMessageID() throws JMSException {
        return message.getMessageIdAsString();
    }

    @Override
    public void setJMSMessageID(String id) throws JMSException {
        message.setMessageId(id);
    }

    @Override
    public long getJMSTimestamp() throws JMSException {
        return message.getTimestamp();
    }

    @Override
    public void setJMSTimestamp(long timestamp) throws JMSException {
        message.setTimestamp(timestamp);
    }

    @Override
    public byte[] getJMSCorrelationIDAsBytes() throws JMSException {
        return message.getCorrelationIdAsBytes();
    }

    @Override
    public void setJMSCorrelationIDAsBytes(byte[] correlationID) throws JMSException {
        message.setCorrelationIdAsBytes(correlationID);
    }

    @Override
    public void setJMSCorrelationID(String correlationID) throws JMSException {
        message.setCorrelationId(correlationID);
    }

    @Override
    public String getJMSCorrelationID() throws JMSException {
        return message.getCorrelationId();
    }

    @Override
    public Destination getJMSReplyTo() throws JMSException {
        return message.getReplyTo();
    }

    @Override
    public void setJMSReplyTo(Destination replyTo) throws JMSException {
        message.setReplyTo(OpenWireDestination.transform(replyTo));
    }

    @Override
    public Destination getJMSDestination() throws JMSException {
        return message.getDestination();
    }

    @Override
    public void setJMSDestination(Destination destination) throws JMSException {
        message.setDestination(OpenWireDestination.transform(destination));
    }

    @Override
    public int getJMSDeliveryMode() throws JMSException {
        return message.isPersistent() ? DeliveryMode.PERSISTENT : DeliveryMode.NON_PERSISTENT;
    }

    @Override
    public void setJMSDeliveryMode(int deliveryMode) throws JMSException {
        message.setPersistent(deliveryMode == DeliveryMode.PERSISTENT);
    }

    @Override
    public boolean getJMSRedelivered() throws JMSException {
        return message.getRedeliveryCounter() > 0;
    }

    @Override
    public void setJMSRedelivered(boolean redelivered) throws JMSException {
        message.setRedelivered(true);
    }

    @Override
    public String getJMSType() throws JMSException {
        return message.getType();
    }

    @Override
    public void setJMSType(String type) throws JMSException {
        message.setType(type);
    }

    @Override
    public long getJMSExpiration() throws JMSException {
        return message.getExpiration();
    }

    @Override
    public void setJMSExpiration(long expiration) throws JMSException {
        message.setExpiration(expiration);
    }

    @Override
    public int getJMSPriority() throws JMSException {
        return message.getPriority();
    }

    @Override
    public void setJMSPriority(int priority) throws JMSException {
        message.setPriority((byte) priority);
    }

    @Override
    public void clearProperties() throws JMSException {
        message.clearProperties();
    }

    @Override
    public boolean propertyExists(String name) throws JMSException {
        try {
            return (message.propertyExists(name) || getObjectProperty(name)!= null);
        } catch (Exception e) {
            throw ExceptionSupport.create(e);
        }
    }

    @Override
    public boolean getBooleanProperty(String name) throws JMSException {
        Object value = getObjectProperty(name);

        if (value == null) {
            return false;
        }

        Boolean rc = (Boolean) TypeConversionSupport.convert(value, Boolean.class);
        if (rc == null) {
            throw new MessageFormatException("Property " + name + " was a " + value.getClass().getName() + " and cannot be read as a boolean");
        }

        return rc.booleanValue();
    }

    @Override
    public byte getByteProperty(String name) throws JMSException {
        Object value = getObjectProperty(name);
        if (value == null) {
            throw new NumberFormatException("property " + name + " was null");
        }
        Byte rc = (Byte) TypeConversionSupport.convert(value, Byte.class);
        if (rc == null) {
            throw new MessageFormatException("Property " + name + " was a " + value.getClass().getName() + " and cannot be read as a byte");
        }
        return rc.byteValue();
    }

    @Override
    public short getShortProperty(String name) throws JMSException {
        Object value = getObjectProperty(name);
        if (value == null) {
            throw new NumberFormatException("property " + name + " was null");
        }
        Short rc = (Short) TypeConversionSupport.convert(value, Short.class);
        if (rc == null) {
            throw new MessageFormatException("Property " + name + " was a " + value.getClass().getName() + " and cannot be read as a short");
        }
        return rc.shortValue();
    }

    @Override
    public int getIntProperty(String name) throws JMSException {
        Object value = getObjectProperty(name);
        if (value == null) {
            throw new NumberFormatException("property " + name + " was null");
        }
        Integer rc = (Integer) TypeConversionSupport.convert(value, Integer.class);
        if (rc == null) {
            throw new MessageFormatException("Property " + name + " was a " + value.getClass().getName() + " and cannot be read as an integer");
        }
        return rc.intValue();
    }

    @Override
    public long getLongProperty(String name) throws JMSException {
        Object value = getObjectProperty(name);
        if (value == null) {
            throw new NumberFormatException("property " + name + " was null");
        }
        Long rc = (Long) TypeConversionSupport.convert(value, Long.class);
        if (rc == null) {
            throw new MessageFormatException("Property " + name + " was a " + value.getClass().getName() + " and cannot be read as a long");
        }
        return rc.longValue();
    }

    @Override
    public float getFloatProperty(String name) throws JMSException {
        Object value = getObjectProperty(name);
        if (value == null) {
            throw new NullPointerException("property " + name + " was null");
        }
        Float rc = (Float) TypeConversionSupport.convert(value, Float.class);
        if (rc == null) {
            throw new MessageFormatException("Property " + name + " was a " + value.getClass().getName() + " and cannot be read as a float");
        }
        return rc.floatValue();
    }

    @Override
    public double getDoubleProperty(String name) throws JMSException {
        Object value = getObjectProperty(name);
        if (value == null) {
            throw new NullPointerException("property " + name + " was null");
        }
        Double rc = (Double) TypeConversionSupport.convert(value, Double.class);
        if (rc == null) {
            throw new MessageFormatException("Property " + name + " was a " + value.getClass().getName() + " and cannot be read as a double");
        }
        return rc.doubleValue();
    }

    @Override
    public String getStringProperty(String name) throws JMSException {
        Object value = null;

        // Always go first to the OpenWire Message field before checking in the
        // application properties for any other versions.
        if (name.equals("JMSXUserID")) {
            value = message.getUserId();
            if (value == null) {
                value = getObjectProperty(name);
            }
        } else {
            value = getObjectProperty(name);
        }
        if (value == null) {
            return null;
        }
        String rc = (String) TypeConversionSupport.convert(value, String.class);
        if (rc == null) {
            throw new MessageFormatException("Property " + name + " was a " + value.getClass().getName() + " and cannot be read as a String");
        }
        return rc;
    }

    @Override
    public Object getObjectProperty(String name) throws JMSException {
        if (name == null) {
            throw new NullPointerException("Property name cannot be null");
        }

        return OpenWireMessagePropertyGetter.getProperty(message, name);
    }

    @Override
    public Enumeration<?> getPropertyNames() throws JMSException {
        return message.getPropertyNames();
    }

    /**
     * return all property names, including standard JMS properties and JMSX properties
     * @return  Enumeration of all property names on this message
     * @throws JMSException
     */
    public Enumeration<String> getAllPropertyNames() throws JMSException {
        try {
            Vector<String> result = new Vector<String>(message.getProperties().keySet());
            result.addAll(OpenWireMessagePropertyGetter.getPropertyNames());
            return result.elements();
        } catch (Exception e) {
            throw ExceptionSupport.create(e);
        }
    }

    @Override
    public void setBooleanProperty(String name, boolean value) throws JMSException {
        setObjectProperty(name, Boolean.valueOf(value), true);
    }

    @Override
    public void setByteProperty(String name, byte value) throws JMSException {
        setObjectProperty(name, Byte.valueOf(value), true);
    }

    @Override
    public void setShortProperty(String name, short value) throws JMSException {
        setObjectProperty(name, Short.valueOf(value), true);
    }

    @Override
    public void setIntProperty(String name, int value) throws JMSException {
        setObjectProperty(name, Integer.valueOf(value), true);
    }

    @Override
    public void setLongProperty(String name, long value) throws JMSException {
        setObjectProperty(name, Long.valueOf(value), true);
    }

    @Override
    public void setFloatProperty(String name, float value) throws JMSException {
        setObjectProperty(name, Float.valueOf(value), true);
    }

    @Override
    public void setDoubleProperty(String name, double value) throws JMSException {
        setObjectProperty(name, Double.valueOf(value), true);
    }

    @Override
    public void setStringProperty(String name, String value) throws JMSException {
        setObjectProperty(name, value, true);
    }

    @Override
    public void setObjectProperty(String name, Object value) throws JMSException {
        setObjectProperty(name, value, true);
    }

    protected void setObjectProperty(String name, Object value, boolean checkReadOnly) throws JMSException {

        if (checkReadOnly) {
            checkReadOnlyProperties();
        }

        if (name == null || name.equals("")) {
            throw new IllegalArgumentException("Property name cannot be empty or null");
        }

        if (value instanceof UTF8Buffer) {
            value = value.toString();
        }

        // Ensure that message sent with scheduling options comply with the
        // expected property types for the settings values.
        value = convertScheduled(name, value);

        OpenWireMessagePropertySetter.setProperty(message, name, value);
    }

    @Override
    public void acknowledge() throws JMSException {
        if (acknowledgeCallback != null) {
            try {
                acknowledgeCallback.call();
            } catch (Exception e) {
                throw ExceptionSupport.create(e);
            }
        }
    }

    @Override
    public void clearBody() throws JMSException {
        setReadOnlyBody(false);
    }

    /**
     * @return the acknowledge callback instance set on this message.
     */
    public Callable<Void> getAcknowledgeCallback() {
        return acknowledgeCallback;
    }

    /**
     * Sets the Callable instance that is invoked when the client calls the JMS Message
     * acknowledge method.
     *
     * @param acknowledgeCallback
     *        the acknowledgeCallback to set on this message.
     */
    public void setAcknowledgeCallback(Callable<Void> acknowledgeCallback) {
        this.acknowledgeCallback = acknowledgeCallback;
    }

    /**
     * @return true if the body is in read only mode.
     */
    public boolean isReadOnlyBody() {
        return readOnlyBody;
    }

    /**
     * @param readOnlyBody
     *        sets if the message body is in read-only mode.
     */
    public void setReadOnlyBody(boolean readOnlyBody) {
        this.readOnlyBody = readOnlyBody;
    }

    /**
     * @return true if the message properties are in read-only mode.
     */
    public boolean isReadOnlyProperties() {
        return readOnlyProperties;
    }

    /**
     * @param readOnlyProperties
     *        sets if the message properties are in read-only mode.
     */
    public void setReadOnlyProperties(boolean readOnlyProperties) {
        this.readOnlyProperties = readOnlyProperties;
    }

    /**
     * @returns true if this message has expired.
     */
    public boolean isExpired() {
        return message.isExpired();
    }

    /**
     * @returns true if this message is an Advisory message instance.
     */
    public boolean isAdviory() {
        return this.message.isAdvisory();
    }

    @Override
    public int hashCode() {
        return message.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || other.getClass() != getClass()) {
            return false;
        }

        OpenWireJMSMessage jmsMessage = (OpenWireJMSMessage) other;

        return message.equals(jmsMessage.getOpenWireMessage());
    }

    private void checkReadOnlyProperties() throws MessageNotWriteableException {
        if (readOnlyProperties) {
            throw new MessageNotWriteableException("Message properties are read-only");
        }
    }

    protected void checkReadOnlyBody() throws MessageNotWriteableException {
        if (readOnlyBody) {
            throw new MessageNotWriteableException("Message body is read-only");
        }
    }

    protected void checkWriteOnlyBody() throws MessageNotReadableException {
        if (!readOnlyBody) {
            throw new MessageNotReadableException("Message body is write-only");
        }
    }

    protected Object convertScheduled(String name, Object value) throws MessageFormatException {
        Object result = value;
        if (OpenWireConstants.AMQ_SCHEDULED_DELAY.equals(name)){
            result = TypeConversionSupport.convert(value, Long.class);
        }
        else if (OpenWireConstants.AMQ_SCHEDULED_PERIOD.equals(name)){
            result = TypeConversionSupport.convert(value, Long.class);
        }
        else if (OpenWireConstants.AMQ_SCHEDULED_REPEAT.equals(name)){
            result = TypeConversionSupport.convert(value, Integer.class);
        }
        return result;
    }
}
