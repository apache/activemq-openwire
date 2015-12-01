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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.jms.JMSException;

import org.apache.activemq.openwire.annotations.OpenWireType;
import org.apache.activemq.openwire.utils.ExceptionSupport;
import org.apache.activemq.openwire.utils.OpenWireMarshallingSupport;
import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.DataByteArrayInputStream;
import org.fusesource.hawtbuf.DataByteArrayOutputStream;

/**
 * openwire:marshaller code="27"
 */
@OpenWireType(typeCode = 27)
public class OpenWireStreamMessage extends OpenWireMessage {

    public static final byte DATA_STRUCTURE_TYPE = CommandTypes.OPENWIRE_STREAM_MESSAGE;

    @Override
    public OpenWireStreamMessage copy() {
        OpenWireStreamMessage copy = new OpenWireStreamMessage();
        copy(copy);
        return copy;
    }

    private void copy(OpenWireStreamMessage copy) {
        storeContent();
        super.copy(copy);
    }

    @Override
    public void onSend() throws JMSException {
        super.onSend();
        storeContent();
    }

    @Override
    public byte getDataStructureType() {
        return DATA_STRUCTURE_TYPE;
    }

    @Override
    public String getMimeType() {
        return "jms/stream-message";
    }

    /**
     * Reads the contents of the StreamMessage instances into a single List<Object> instance
     * and returns it.  The read starts from the current position of the message which implies
     * that the list might not be a complete view of the message if any prior read operations
     * were invoked.
     *
     * @return a List containing the objects store in this message starting from the current position.
     *
     * @throws JMSException if an error occurs while reading the message.
     */
    public List<Object> readStreamToList() throws JMSException {
        if (!hasContent()) {
            return Collections.emptyList();
        }

        Buffer payload = getPayload();
        DataByteArrayInputStream dataIn = new DataByteArrayInputStream(payload);

        List<Object> result = new ArrayList<Object>();
        while (true) {
            try {
                result.add(readNextElement(dataIn));
            } catch (EOFException ex) {
                break;
            } catch (IOException e) {
                throw ExceptionSupport.create(e);
            }
        }

        return result;
    }

    /**
     * Given a DataInput instance, attempt to read OpenWireStreamMessage formatted values
     * and returned the next element.
     *
     * @param input
     *        the input stream that contains the marshaled bytes.
     *
     * @return the next element encoded in the stream.
     *
     * @throws IOException if an error occurs while reading the next element from the stream
     * @throws EOFException
     */
    protected Object readNextElement(DataInput input) throws IOException {
        int type = input.readByte();
        if (type == -1) {
            throw new EOFException("Reached end of stream.");
        }

        if (type == OpenWireMarshallingSupport.NULL) {
            return null;
        } else if (type == OpenWireMarshallingSupport.BIG_STRING_TYPE) {
            return OpenWireMarshallingSupport.readUTF8(input);
        } else if (type == OpenWireMarshallingSupport.STRING_TYPE) {
            return input.readUTF();
        } else if (type == OpenWireMarshallingSupport.LONG_TYPE) {
            return Long.valueOf(input.readLong());
        } else if (type == OpenWireMarshallingSupport.INTEGER_TYPE) {
            return Integer.valueOf(input.readInt());
        } else if (type == OpenWireMarshallingSupport.SHORT_TYPE) {
            return Short.valueOf(input.readShort());
        } else if (type == OpenWireMarshallingSupport.BYTE_TYPE) {
            return Byte.valueOf(input.readByte());
        } else if (type == OpenWireMarshallingSupport.FLOAT_TYPE) {
            return new Float(input.readFloat());
        } else if (type == OpenWireMarshallingSupport.DOUBLE_TYPE) {
            return new Double(input.readDouble());
        } else if (type == OpenWireMarshallingSupport.BOOLEAN_TYPE) {
            return input.readBoolean() ? Boolean.TRUE : Boolean.FALSE;
        } else if (type == OpenWireMarshallingSupport.CHAR_TYPE) {
            return Character.valueOf(input.readChar());
        } else if (type == OpenWireMarshallingSupport.BYTE_ARRAY_TYPE) {
            int len = input.readInt();
            byte[] value = new byte[len];
            input.readFully(value);
            return value;
        } else {
            throw new IOException("unknown type read from encoded stream.");
        }
    }

    /**
     * Writes the given set of Objects to the messages stream.  The elements in the list
     * must adhere to the supported types of a JMS StreamMessage or an exception will be
     * thrown.
     *
     * @param elements
     *        the list of elements to store into the list.
     *
     * @throws JMSException if an error occurs while writing the elements to the message.
     */
    public void writeListToStream(List<Object> elements) throws JMSException {
        if (elements != null && !elements.isEmpty()) {
            DataByteArrayOutputStream output = new DataByteArrayOutputStream();
            for (Object value : elements) {
                try {
                    writeElement(value, output);
                } catch (IOException e) {
                    throw ExceptionSupport.create(e);
                }
            }
            try {
                output.close();
            } catch (IOException e) {
                throw ExceptionSupport.create(e);
            }

            setPayload(output.toBuffer());
        }
    }

    /**
     * Encodes the given object into the OpenWire marshaled form and writes it to the
     * given DataOutput instance.  Each element is written with a type identifier to
     * allow for easy unmarshaling.
     *
     * @param value
     * @param output
     * @throws IOException
     */
    protected void writeElement(Object value, DataOutput output) throws IOException {
        if (value == null) {
            OpenWireMarshallingSupport.marshalNull(output);
        } else if (value instanceof String) {
            OpenWireMarshallingSupport.marshalString(output, (String) value);
        } else if (value instanceof Character) {
            OpenWireMarshallingSupport.marshalChar(output, (Character) value);
        } else if (value instanceof Boolean) {
            OpenWireMarshallingSupport.marshalBoolean(output, (Boolean) value);
        } else if (value instanceof Byte) {
            OpenWireMarshallingSupport.marshalByte(output, (Byte) value);
        } else if (value instanceof Short) {
            OpenWireMarshallingSupport.marshalShort(output, (Short) value);
        } else if (value instanceof Integer) {
            OpenWireMarshallingSupport.marshalInt(output, (Integer) value);
        } else if (value instanceof Float) {
            OpenWireMarshallingSupport.marshalFloat(output, (Float) value);
        } else if (value instanceof Double) {
            OpenWireMarshallingSupport.marshalDouble(output, (Double) value);
        } else if (value instanceof byte[]) {
            OpenWireMarshallingSupport.marshalByteArray(output, (byte[]) value, 0, ((byte[]) value).length);
        } else if (value instanceof Long) {
            OpenWireMarshallingSupport.marshalLong(output, (Long) value);
        } else {
            throw new IOException("Unsupported Object type: " + value.getClass());
        }
    }

    @Override
    public void compress() throws IOException {
        storeContent();
        super.compress();
    }

    @Override
    public String toString() {
        return super.toString() + " OpenWireStreamMessage{}";
    }
}
