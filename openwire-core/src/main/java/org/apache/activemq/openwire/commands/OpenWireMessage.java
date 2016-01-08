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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.activemq.openwire.annotations.OpenWireExtension;
import org.apache.activemq.openwire.annotations.OpenWireType;
import org.apache.activemq.openwire.buffer.Buffer;
import org.apache.activemq.openwire.buffer.UTF8Buffer;
import org.apache.activemq.openwire.utils.IOExceptionSupport;

/**
 * Base implementation of a JMS Message object.
 *
 * openwire:marshaller code="23"
 */
@OpenWireType(typeCode = 23)
public class OpenWireMessage extends Message {

    public static final byte DATA_STRUCTURE_TYPE = CommandTypes.OPENWIRE_MESSAGE;

    @OpenWireExtension
    protected transient boolean useCompression;

    @OpenWireExtension
    protected transient boolean nestedMapAndListAllowed;

    @Override
    public byte getDataStructureType() {
        return DATA_STRUCTURE_TYPE;
    }

    /**
     * @return String value that represents the MIMI type for the OpenWireMessage type.
     */
    public String getMimeType() {
        return "jms/message";
    }

    @Override
    public OpenWireMessage copy() {
        OpenWireMessage copy = new OpenWireMessage();
        copy(copy);
        return copy;
    }

    protected void copy(OpenWireMessage copy) {
        copy.useCompression = useCompression;
        copy.nestedMapAndListAllowed = nestedMapAndListAllowed;

        super.copy(copy);
    }

    @Override
    public int hashCode() {
        MessageId id = getMessageId();
        if (id != null) {
            return id.hashCode();
        } else {
            return super.hashCode();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || o.getClass() != getClass()) {
            return false;
        }

        OpenWireMessage msg = (OpenWireMessage) o;
        MessageId oMsg = msg.getMessageId();
        MessageId thisMsg = this.getMessageId();
        return thisMsg != null && oMsg != null && oMsg.equals(thisMsg);
    }

    @Override
    public void clearBody() throws IOException {
        setContent(null);
    }

    public String getMessageIdAsString() {
        MessageId messageId = this.getMessageId();
        if (messageId == null) {
            return null;
        }
        return messageId.toString();
    }

    public byte[] getCorrelationIdAsBytes() throws IOException {
        return encodeString(this.getCorrelationId());
    }

    public void setCorrelationIdAsBytes(byte[] correlationId) throws IOException {
        this.setCorrelationId(decodeString(correlationId));
    }

    /**
     * @returns true if the message has data in its body, false if empty.
     */
    public boolean hasContent() {
        Buffer content = getContent();
        if (content == null || content.isEmpty()) {
            return false;
        }

        return true;
    }

    /**
     * Provides a fast way to read the message contents.
     *
     * This method, unlike the base class getContent method will perform any needed
     * decompression on a message that was received with a compressed payload.
     *
     * @return a the message contents, uncompressed as needed.
     *
     * @throws JMSException if an error occurs while accessing the message payload.
     */
    public Buffer getPayload() throws IOException {
        Buffer data = getContent();
        if (data == null) {
            data = new Buffer(new byte[] {}, 0, 0);
        } else if (isCompressed()) {
            try {
                return decompress();
            } catch (Exception e) {
                throw IOExceptionSupport.create(e);
            }
        }

        return data;
    }

    /**
     * Set the contents of this message.
     *
     * This method will, unlike the base class setContent method perform any
     * necessary compression of the given bytes if the message is configured for
     * message compression.
     *
     * @param bytes
     *        the new byte array to use to fill the message body.
     *
     * @throws IOException if an error occurs while accessing the message payload.
     */
    public void setPayload(byte[] bytes) throws IOException {
        setPayload(new Buffer(bytes));
    }

    /**
     * Set the contents of this message.
     *
     * This method will, unlike the base class setContent method perform any
     * necessary compression of the given bytes if the message is configured for
     * message compression.
     *
     * @param buffer
     *        the new byte Buffer to use to fill the message body.
     *
     * @throws JMSException if an error occurs while accessing the message payload.
     */
    public void setPayload(Buffer buffer) throws IOException {
        try {
            setContent(buffer);
            if (isUseCompression()) {
                doCompress();
            }
        } catch (Exception ex) {
            throw IOExceptionSupport.create(ex);
        }
    }

    /**
     * Seems to be invalid because the parameter doesn't initialize MessageId
     * instance variables ProducerId and ProducerSequenceId
     *
     * @param value
     *        the string Message ID value to assign to this message.
     *
     * @throws IOException if an error occurs while parsing the String to a MessageID
     */
    public void setMessageId(String value) throws IOException {
        if (value != null) {
            try {
                MessageId id = new MessageId(value);
                this.setMessageId(id);
            } catch (NumberFormatException e) {
                MessageId id = new MessageId();
                id.setTextView(value);
                this.setMessageId(id);
            }
        } else {
            this.setMessageId((MessageId)null);
        }
    }

    /**
     * This will create an object of MessageId. For it to be valid, the instance
     * variable ProducerId and producerSequenceId must be initialized.
     *
     * @param producerId
     *        the ProducerId of the producer that sends this message.
     * @param producerSequenceId
     *        the logical producer sequence Id of this message.
     *
     * @throws JMSException if an error occurs while setting this MessageId
     */
    public void setMessageId(ProducerId producerId, long producerSequenceId) throws IOException {
        MessageId id = null;
        try {
            id = new MessageId(producerId, producerSequenceId);
            this.setMessageId(id);
        } catch (Throwable e) {
            throw IOExceptionSupport.create("Invalid message id '" + id + "', reason: " + e.getMessage(), e);
        }
    }

    public boolean propertyExists(String name) throws IOException {
        try {
            return (this.getProperties().containsKey(name) || getProperty(name)!= null);
        } catch (Exception e) {
            throw IOExceptionSupport.create(e);
        }
    }

    @SuppressWarnings("rawtypes")
    public Enumeration getPropertyNames() throws IOException {
        try {
            Vector<String> result = new Vector<String>(this.getProperties().keySet());
            return result.elements();
        } catch (Exception e) {
            throw IOExceptionSupport.create(e);
        }
    }

    @Override
    public void setProperty(String name, Object value) throws IOException {
        setProperty(name, value, true);
    }

    /**
     * Allows for bulk set of Message properties.
     *
     * The base method does not attempt to intercept and map JMS specific properties
     * into the fields of the Message, this is left to any wrapper implementation that
     * wishes to apply JMS like behavior to the standard OpenWireMessage object.
     *
     * @param properties
     *        the properties to set on the Message instance.
     *
     * @throws JMSException if an error occurs while setting the properties.
     */
    public void setProperties(Map<String, ?> properties) throws IOException {
        for (Map.Entry<String, ?> entry : properties.entrySet()) {
            setProperty(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Allows for unchecked additions to the internal message properties if desired.
     *
     * This method is mainly useful for unit testing message types to ensure that get
     * method fail on conversions from bad types.
     *
     * @param name
     *        the name of the property to set
     * @param value
     *        the new value to assigned to the named property.
     * @param checkValid
     *        indicates if a type validity check should be performed on the given object.
     *
     * @throws IOException if an error occurs while attempting to set the property value.
     */
    public void setProperty(String name, Object value, boolean checkValid) throws IOException {
        if (name == null || name.equals("")) {
            throw new IllegalArgumentException("Property name cannot be empty or null");
        }

        if (value instanceof UTF8Buffer) {
            value = value.toString();
        }

        if (checkValid) {
            checkValidObject(value);
        }

        super.setProperty(name, value);
    }

    /**
     * @return whether the Message allows for Map and List elements in its properties.
     */
    public boolean isNestedMapAndListAllowed() {
        return nestedMapAndListAllowed;
    }

    /**
     * Sets whether the Message will allow for setting a Map or List instance in the
     * Message properties.  By default these elements are not allowed but can added if
     * this option is set to true.
     *
     * @param nestedMapAndListAllowed
     *        true if nested Map and List instances are allowed in Message properties.
     */
    public void setNestedMapAndListAllowed(boolean nestedMapAndListAllowed) {
        this.nestedMapAndListAllowed = nestedMapAndListAllowed;
    }

    /**
     * Sets whether the payload of the Message should be compressed.
     *
     * @param useCompression
     *        true if the binary payload should be compressed.
     */
    public void setUseCompression(boolean useCompression) {
        this.useCompression = useCompression;
    }

    /**
     * @return true if the Message will compress the byte payload.
     */
    public boolean isUseCompression() {
        return useCompression;
    }

    @Override
    public Response visit(CommandVisitor visitor) throws Exception {
        return visitor.processMessage(this);
    }

    @Override
    public void storeContent() {
    }

    @Override
    public void storeContentAndClear() {
        storeContent();
    }

    /**
     * Method that allows an application to inform the Message instance that it is
     * about to be sent and that it should prepare its internal state for dispatch.
     *
     * @throws IOException if an error occurs or Message state is invalid.
     */
    public void onSend() throws IOException {
    }

    protected void checkValidObject(Object value) throws IOException {

        // TODO - We can probably remove these nested enabled check, the provider should
        //        do this since we are just a codec.
        boolean valid = value instanceof Boolean || value instanceof Byte || value instanceof Short || value instanceof Integer || value instanceof Long;
        valid = valid || value instanceof Float || value instanceof Double || value instanceof Character || value instanceof String || value == null;

        if (!valid) {
            if (isNestedMapAndListAllowed()) {
                if (!(value instanceof Map || value instanceof List)) {
                    throw new IllegalArgumentException("Only objectified primitive objects, String, Map and List types are allowed but was: " + value + " type: " + value.getClass());
                }
            } else {
                throw new IllegalArgumentException("Only objectified primitive objects and String types are allowed but was: " + value + " type: " + value.getClass());
            }
        }
    }

    protected static String decodeString(byte[] data) throws IOException {
        try {
            if (data == null) {
                return null;
            }
            return new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw IOExceptionSupport.create("Invalid UTF-8 encoding: " + e.getMessage(), e);
        }
    }

    protected static byte[] encodeString(String data) throws IOException {
        try {
            if (data == null) {
                return null;
            }
            return data.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw IOExceptionSupport.create("Invalid UTF-8 encoding: " + e.getMessage(), e);
        }
    }
}
