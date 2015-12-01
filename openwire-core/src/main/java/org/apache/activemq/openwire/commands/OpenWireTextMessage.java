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
import java.io.OutputStream;

import javax.jms.JMSException;
import javax.jms.MessageNotWriteableException;

import org.apache.activemq.openwire.annotations.OpenWireType;
import org.apache.activemq.openwire.annotations.OpenWireExtension;
import org.apache.activemq.openwire.codec.OpenWireFormat;
import org.apache.activemq.openwire.utils.ExceptionSupport;
import org.apache.activemq.openwire.utils.OpenWireMarshallingSupport;
import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.ByteArrayInputStream;
import org.fusesource.hawtbuf.ByteArrayOutputStream;

/**
 * openwire:marshaller code="28"
 */
@OpenWireType(typeCode = 28)
public class OpenWireTextMessage extends OpenWireMessage {

    public static final byte DATA_STRUCTURE_TYPE = CommandTypes.OPENWIRE_TEXT_MESSAGE;

    @OpenWireExtension(serialized = true)
    protected String text;

    @Override
    public OpenWireTextMessage copy() {
        OpenWireTextMessage copy = new OpenWireTextMessage();
        copy(copy);
        return copy;
    }

    private void copy(OpenWireTextMessage copy) {
        super.copy(copy);
        copy.text = text;
    }

    @Override
    public byte getDataStructureType() {
        return DATA_STRUCTURE_TYPE;
    }

    @Override
    public String getMimeType() {
        return "jms/text-message";
    }

    public void setText(String text) throws MessageNotWriteableException {
        this.text = text;
        setContent(null);
    }

    public String getText() throws JMSException {
        if (text == null && getContent() != null) {
            text = decodeContent();
            setContent(null);
        }
        return text;
    }

    private String decodeContent() throws JMSException {
        String text = null;
        if (hasContent()) {
            InputStream is = null;
            try {
                is = new ByteArrayInputStream(getPayload());
                DataInputStream dataIn = new DataInputStream(is);
                text = OpenWireMarshallingSupport.readUTF8(dataIn);
                dataIn.close();
            } catch (IOException ioe) {
                throw ExceptionSupport.create(ioe);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        // ignore
                    }
                }
            }
        }
        return text;
    }

    @Override
    public void beforeMarshall(OpenWireFormat wireFormat) throws IOException {
        super.beforeMarshall(wireFormat);
        storeContent();
    }

    @Override
    public void storeContentAndClear() {
        storeContent();
        text = null;
    }

    @Override
    public void storeContent() {
        try {
            Buffer content = getContent();
            if (content == null && text != null) {
                ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
                OutputStream os = bytesOut;
                DataOutputStream dataOut = new DataOutputStream(os);
                OpenWireMarshallingSupport.writeUTF8(dataOut, this.text);
                dataOut.close();
                setPayload(bytesOut.toBuffer());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clearMarshalledState() throws JMSException {
        super.clearMarshalledState();
        this.text = null;
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
        this.text = null;
    }

    @Override
    public int getSize() {
        if (size == 0 && content == null && text != null) {
            size = DEFAULT_MINIMUM_MESSAGE_SIZE;
            if (marshalledProperties != null) {
                size += marshalledProperties.getLength();
            }
            size += text.length() * 2;
        }
        return super.getSize();
    }

    @Override
    public String toString() {
        String text = this.text;
        if( text == null ) {
            try {
                text = decodeContent();
            } catch (JMSException ex) {
            }
        }
        if (text != null) {
            text = OpenWireMarshallingSupport.truncate64(text);
            return getClass().getSimpleName() + " { text = " + text + " }";
        } else {
            return getClass().getSimpleName() + " { text = null }";
        }
    }
}
