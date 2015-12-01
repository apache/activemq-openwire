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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectStreamException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import javax.jms.JMSException;
import javax.jms.MessageFormatException;
import javax.jms.MessageNotWriteableException;

import org.apache.activemq.openwire.annotations.OpenWireType;
import org.apache.activemq.openwire.annotations.OpenWireExtension;
import org.apache.activemq.openwire.codec.OpenWireFormat;
import org.apache.activemq.openwire.utils.ExceptionSupport;
import org.apache.activemq.openwire.utils.OpenWireMarshallingSupport;
import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.ByteArrayInputStream;
import org.fusesource.hawtbuf.ByteArrayOutputStream;
import org.fusesource.hawtbuf.UTF8Buffer;

/**
 * openwire:marshaller code="25"
 */
@OpenWireType(typeCode = 25)
public class OpenWireMapMessage extends OpenWireMessage {

    public static final byte DATA_STRUCTURE_TYPE = CommandTypes.OPENWIRE_MAP_MESSAGE;

    @OpenWireExtension
    protected transient Map<String, Object> map = new HashMap<String, Object>();

    private Object readResolve() throws ObjectStreamException {
        if (this.map == null) {
            this.map = new HashMap<String, Object>();
        }
        return this;
    }

    @Override
    public OpenWireMapMessage copy() {
        OpenWireMapMessage copy = new OpenWireMapMessage();
        copy(copy);
        return copy;
    }

    private void copy(OpenWireMapMessage copy) {
        storeContent();
        super.copy(copy);
    }

    // We only need to marshal the content if we are hitting the wire.
    @Override
    public void beforeMarshall(OpenWireFormat wireFormat) throws IOException {
        super.beforeMarshall(wireFormat);
        storeContent();
    }

    @Override
    public void clearMarshalledState() throws JMSException {
        super.clearMarshalledState();
        map.clear();
    }

    @Override
    public void storeContentAndClear() {
        storeContent();
        map.clear();
    }

    @Override
    public void storeContent() {
        try {
            if (getContent() == null && !map.isEmpty()) {
                ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
                OutputStream os = bytesOut;
                if (isUseCompression()) {
                    compressed = true;
                    os = new DeflaterOutputStream(os);
                }
                DataOutputStream dataOut = new DataOutputStream(os);
                OpenWireMarshallingSupport.marshalPrimitiveMap(map, dataOut);
                dataOut.close();
                setContent(bytesOut.toBuffer());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Builds the message body from data
     *
     * @throws JMSException
     * @throws IOException
     */
    private void loadContent() throws JMSException {
        try {
            if (getContent() != null && map.isEmpty()) {
                Buffer content = getContent();
                InputStream is = new ByteArrayInputStream(content);
                if (isCompressed()) {
                    is = new InflaterInputStream(is);
                }
                DataInputStream dataIn = new DataInputStream(is);
                map = OpenWireMarshallingSupport.unmarshalPrimitiveMap(dataIn);
                dataIn.close();
            }
        } catch (IOException e) {
            throw ExceptionSupport.create(e);
        }
    }

    @Override
    public byte getDataStructureType() {
        return DATA_STRUCTURE_TYPE;
    }

    @Override
    public String getMimeType() {
        return "jms/map-message";
    }

    /**
     * Clears out the message body. Clearing a message's body does not clear its
     * header values or property entries.
     * <P>
     * If this message body was read-only, calling this method leaves the
     * message body in the same state as an empty body in a newly created
     * message.
     */
    @Override
    public void clearBody() throws JMSException {
        super.clearBody();
        map.clear();
    }

    /**
     * Returns the value of the object with the specified name.
     * <P>
     * This method can be used to return, in objectified format, an object in
     * the Java programming language ("Java object") that had been stored in the
     * Map with the equivalent <CODE>setObject</CODE> method call, or its
     * equivalent primitive <CODE>set <I>type </I></CODE> method.
     * <P>
     * Note that byte values are returned as <CODE>byte[]</CODE>, not
     * <CODE>Byte[]</CODE>.
     *
     * @param name
     *        the name of the Java object
     *
     * @return a copy of the Java object value with the specified name, in
     *         objectified format (for example, if the object was set as an
     *         <CODE>int</CODE>, an <CODE>Integer</CODE> is returned); if
     *         there is no item by this name, a null value is returned
     * @throws JMSException if the JMS provider fails to read the message due to
     *                 some internal error.
     */
    public Object getObject(String name) throws JMSException {
        initializeReading();
        Object result = getContentMap().get(name);
        if (result instanceof UTF8Buffer) {
            result = result.toString();
        }

        return result;
    }

    /**
     * Sets an object value with the specified name into the Map.
     * <P>
     * This method works only for the objectified primitive object types
     * (<code>Integer</code>,<code>Double</code>,<code>Long</code> &nbsp;...),
     * <code>String</code> objects, and byte arrays.
     *
     * @param name
     *        the name of the Java object
     * @param value
     *        the Java object value to set in the Map
     * @throws JMSException if the JMS provider fails to write the message due
     *                      to some internal error.
     * @throws IllegalArgumentException if the name is null or if the name is an
     *                                  empty string.
     * @throws MessageFormatException if the object is invalid.
     */
    public void setObject(String name, Object value) throws JMSException {
        initializeWriting();
        if (value != null) {
            // byte[] not allowed on properties
            if (!(value instanceof byte[])) {
                checkValidObject(value);
            }
            put(name, value);
        } else {
            put(name, null);
        }
    }

    /**
     * Removes an object value with the specified name into the Map.
     *
     * @param name
     *        the name of the Java object
     *
     * @throws JMSException if the JMS provider fails to write the message due
     *                      to some internal error.
     * @throws IllegalArgumentException if the name is null or if the name is an
     *                                  empty string.
     */
    public void removeObject(String name) throws JMSException {
        initializeWriting();
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("map element name cannot be null or empty.");
        }

        this.map.remove(name);
    }

    /**
     * Returns an <CODE>Enumeration</CODE> of all the names in the
     * <CODE>MapMessage</CODE> object.
     *
     * @return an enumeration of all the names in this <CODE>MapMessage</CODE>
     * @throws JMSException
     */
    public Enumeration<String> getMapNames() throws JMSException {
        return Collections.enumeration(getContentMap().keySet());
    }

    /**
     * Indicates whether an item exists in this <CODE>MapMessage</CODE>
     * object.
     *
     * @param name the name of the item to test
     * @return true if the item exists
     * @throws JMSException if the JMS provider fails to determine if the item
     *                 exists due to some internal error.
     */
    public boolean itemExists(String name) throws JMSException {
        return getContentMap().containsKey(name);
    }

    private void initializeReading() throws JMSException {
        loadContent();
    }

    private void initializeWriting() throws MessageNotWriteableException {
        setContent(null);
    }

    @Override
    public void compress() throws IOException {
        storeContent();
        super.compress();
    }

    @Override
    public String toString() {
        return super.toString() + " OpenWireMapMessage{ " + "theTable = " + map + " }";
    }

    protected Map<String, Object> getContentMap() throws JMSException {
        initializeReading();
        return map;
    }

    protected void put(String name, Object value) throws JMSException {
        if (name == null) {
            throw new IllegalArgumentException("The name of the property cannot be null.");
        }
        if (name.length() == 0) {
            throw new IllegalArgumentException("The name of the property cannot be an emprty string.");
        }
        map.put(name, value);
    }
}
