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
import java.util.Arrays;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import javax.jms.JMSException;
import javax.jms.MessageNotReadableException;

import org.apache.activemq.openwire.annotations.OpenWireType;
import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.BufferEditor;
import org.fusesource.hawtbuf.ByteArrayOutputStream;

/**
 * Provides an abstraction layer around the standard OpenWireMessage object for
 * implementation of a JMS style BytesMessage instance.  This class provides access
 * to the message body content via mechanism that make it easy to wrap this object
 * and adhere to the JMS BytesMessage interface.
 *
 * openwire:marshaller code=24
 */
@OpenWireType(typeCode = 24)
public class OpenWireBytesMessage extends OpenWireMessage {

    public static final byte DATA_STRUCTURE_TYPE = CommandTypes.OPENWIRE_BYTES_MESSAGE;

    @Override
    public OpenWireBytesMessage copy() {
        OpenWireBytesMessage copy = new OpenWireBytesMessage();
        copy(copy);
        return copy;
    }

    private void copy(OpenWireBytesMessage copy) {
        super.copy(copy);
    }

    @Override
    public byte getDataStructureType() {
        return DATA_STRUCTURE_TYPE;
    }

    @Override
    public String getMimeType() {
        return "jms/bytes-message";
    }

    /**
     * Gets the number of bytes of the message body when the message is in
     * read-only mode. The value returned can be used to allocate a byte array.
     * The value returned is the entire length of the message body, regardless
     * of where the pointer for reading the message is currently located.
     *
     * @return number of bytes in the message
     * @throws JMSException if the JMS provider fails to read the message due to
     *                 some internal error.
     * @throws MessageNotReadableException if the message is in write-only mode.
     * @since 1.1
     */
    public long getBodyLength() throws JMSException {
        if (compressed) {
            return getBodyBytes().length;
        } else if (content != null) {
            return content.length();
        } else {
            return 0;
        }
    }

    /**
     * Provides a fast way to read the message contents.
     *
     * This method, unlike the base class getContent method will perform any
     * needed decompression on a message that was received with a compressed
     * payload.  The complete message body will then be read and returned in
     * a byte array copy.  Changes to the returned byte array are not reflected
     * in the underlying message contents.
     *
     * @return a copy of the message contents, uncompressed as needed.
     *
     * @throws JMSException if an error occurs while accessing the message payload.
     */
    public byte[] getBodyBytes() throws JMSException {
        Buffer data = getPayload();
        if (data == null) {
            data = new Buffer(new byte[] {}, 0, 0);
        }

        return data.toByteArray();
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
     */
    public void setBodyBytes(byte[] bytes) {
        setBodyBytes(new Buffer(bytes));
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
     */
    public void setBodyBytes(Buffer buffer) {
        try {
            setPayload(buffer);
        } catch (Exception ioe) {
            throw new RuntimeException(ioe.getMessage(), ioe);
        }
    }

    @Override
    public String toString() {
        return "OpenWireBytesMessage";
    }

    @Override
    protected Buffer doDecompress() throws IOException {
        Buffer compressed = getContent();
        Inflater inflater = new Inflater();
        ByteArrayOutputStream decompressed = new ByteArrayOutputStream();
        try {
            BufferEditor editor = BufferEditor.big(compressed);
            int length = editor.readInt();
            compressed.offset = 0;
            byte[] data = Arrays.copyOfRange(compressed.getData(), 4, compressed.getLength());
            inflater.setInput(data);
            byte[] buffer = new byte[length];
            int count = inflater.inflate(buffer);
            decompressed.write(buffer, 0, count);
            return decompressed.toBuffer();
        } catch (Exception e) {
            throw new IOException(e);
        } finally {
            inflater.end();
            decompressed.close();
        }
    }

    @Override
    protected void doCompress() throws IOException {
        compressed = true;
        Buffer bytes = getContent();
        if (bytes != null) {
            int length = bytes.getLength();
            ByteArrayOutputStream compressed = new ByteArrayOutputStream();
            compressed.write(new byte[4]);
            Deflater deflater = new Deflater();
            try {
                deflater.setInput(bytes.data);
                deflater.finish();
                byte[] buffer = new byte[1024];
                while (!deflater.finished()) {
                    int count = deflater.deflate(buffer);
                    compressed.write(buffer, 0, count);
                }

                bytes = compressed.toBuffer();
                bytes.bigEndianEditor().writeInt(length);
                bytes.offset = 0;
                bytes.length += 4;
                setContent(bytes);
            } finally {
                deflater.end();
                compressed.close();
            }
        }
    }
}
