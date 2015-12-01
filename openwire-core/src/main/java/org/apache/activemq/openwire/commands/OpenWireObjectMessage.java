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
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import javax.jms.JMSException;

import org.apache.activemq.openwire.annotations.OpenWireType;
import org.apache.activemq.openwire.annotations.OpenWireExtension;
import org.apache.activemq.openwire.codec.OpenWireFormat;
import org.apache.activemq.openwire.utils.ExceptionSupport;
import org.apache.activemq.openwire.utils.ObjectMessageInputStream;
import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.ByteArrayInputStream;
import org.fusesource.hawtbuf.ByteArrayOutputStream;

/**
 * openwire:marshaller code="26"
 */
@OpenWireType(typeCode = 26)
public class OpenWireObjectMessage extends OpenWireMessage {

    public static final byte DATA_STRUCTURE_TYPE = CommandTypes.OPENWIRE_OBJECT_MESSAGE;
    static final ClassLoader ACTIVEMQ_CLASSLOADER = OpenWireObjectMessage.class.getClassLoader();

    @OpenWireExtension
    protected transient Serializable object;

    @Override
    public OpenWireObjectMessage copy() {
        OpenWireObjectMessage copy = new OpenWireObjectMessage();
        copy(copy);
        return copy;
    }

    private void copy(OpenWireObjectMessage copy) {
        storeContent();
        copy.object = null;
        super.copy(copy);
    }

    @Override
    public void storeContentAndClear() {
        storeContent();
        object = null;
    }

    @Override
    public void storeContent() {
        Buffer bodyAsBytes = getContent();
        if (bodyAsBytes == null && object != null) {
            try {
                ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
                OutputStream os = bytesOut;
                if (isUseCompression()) {
                    compressed = true;
                    os = new DeflaterOutputStream(os);
                }
                DataOutputStream dataOut = new DataOutputStream(os);
                ObjectOutputStream objOut = new ObjectOutputStream(dataOut);
                objOut.writeObject(object);
                objOut.flush();
                objOut.reset();
                objOut.close();
                setContent(bytesOut.toBuffer());
            } catch (IOException ioe) {
                throw new RuntimeException(ioe.getMessage(), ioe);
            }
        }
    }

    @Override
    public byte getDataStructureType() {
        return DATA_STRUCTURE_TYPE;
    }

    @Override
    public String getMimeType() {
        return "jms/object-message";
    }

    /**
     * Clears out the message body. Clearing a message's body does not clear its
     * header values or property entries. <p/>
     * <P>
     * If this message body was read-only, calling this method leaves the
     * message body in the same state as an empty body in a newly created
     * message.
     *
     * @throws JMSException if the JMS provider fails to clear the message body
     *                 due to some internal error.
     */
    @Override
    public void clearBody() throws JMSException {
        super.clearBody();
        this.object = null;
    }

    /**
     * Sets the serializable object containing this message's data. It is
     * important to note that an <CODE>ObjectMessage</CODE> contains a
     * snapshot of the object at the time <CODE>setObject()</CODE> is called;
     * subsequent modifications of the object will have no effect on the
     * <CODE>ObjectMessage</CODE> body.
     *
     * @param newObject the message's data
     * @throws JMSException if the JMS provider fails to set the object due to
     *                 some internal error.
     * @throws javax.jms.MessageFormatException if object serialization fails.
     * @throws javax.jms.MessageNotWriteableException if the message is in
     *                 read-only mode.
     */
    public void setObject(Serializable newObject) throws JMSException {
        this.object = newObject;
        setContent(null);
        storeContent();
    }

    /**
     * Gets the serializable object containing this message's data. The default
     * value is null.
     *
     * @return the serializable object containing this message's data
     * @throws JMSException
     */
    public Serializable getObject() throws JMSException {
        if (object == null && getContent() != null) {
            try {
                Buffer content = getContent();
                InputStream is = new ByteArrayInputStream(content);
                if (isCompressed()) {
                    is = new InflaterInputStream(is);
                }
                DataInputStream dataIn = new DataInputStream(is);
                ObjectMessageInputStream objIn = new ObjectMessageInputStream(dataIn);
                try {
                    object = (Serializable)objIn.readObject();
                } catch (ClassNotFoundException ce) {
                    throw ExceptionSupport.create("Failed to build body from content. Serializable class not available to broker. Reason: " + ce, ce);
                } finally {
                    dataIn.close();
                    objIn.close();
                }
            } catch (IOException e) {
                throw ExceptionSupport.create("Failed to build body from bytes. Reason: " + e, e);
            }
        }
        return this.object;
    }

    @Override
    public void beforeMarshall(OpenWireFormat wireFormat) throws IOException {
        super.beforeMarshall(wireFormat);
        storeContent();
    }

    @Override
    public void clearMarshalledState() throws JMSException {
        super.clearMarshalledState();
        this.object = null;
    }

    @Override
    public void compress() throws IOException {
        storeContent();
        super.compress();
    }

    @Override
    public String toString() {
        try {
            getObject();
        } catch (JMSException e) {
        }
        return super.toString();
    }
}
