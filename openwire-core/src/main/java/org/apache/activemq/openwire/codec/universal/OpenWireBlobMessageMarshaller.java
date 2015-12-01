/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.openwire.codec.universal;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.activemq.openwire.codec.*;
import org.apache.activemq.openwire.commands.*;

/**
 * Marshalling code for Open Wire for OpenWireBlobMessage
 *
 * NOTE!: This file is auto generated - do not modify!
 *
 */
public class OpenWireBlobMessageMarshaller extends OpenWireMessageMarshaller {

    /**
     * Return the type of Data Structure handled by this Marshaler
     *
     * @return short representation of the type data structure
     */
    public byte getDataStructureType() {
        return OpenWireBlobMessage.DATA_STRUCTURE_TYPE;
    }
    
    /**
     * @return a new instance of the managed type.
     */
    public DataStructure createObject() {
        return new OpenWireBlobMessage();
    }

    /**
     * Un-marshal an object instance from the data input stream
     *
     * @param wireFormat the OpenWireFormat instance to use
     * @param target the object to un-marshal
     * @param dataIn the data input stream to build the object from
     * @param bs the boolean stream where the type's booleans were marshaled
     *
     * @throws IOException if an error occurs while reading the data
     */
    public void tightUnmarshal(OpenWireFormat wireFormat, Object target, DataInput dataIn, BooleanStream bs) throws IOException {
        super.tightUnmarshal(wireFormat, target, dataIn, bs);

        OpenWireBlobMessage info = (OpenWireBlobMessage) target;
        int version = wireFormat.getVersion();

        if (version >= 3) {
            info.setRemoteBlobUrl(tightUnmarshalString(dataIn, bs));
        }
        if (version >= 3) {
            info.setMimeType(tightUnmarshalString(dataIn, bs));
        }
        if (version >= 3) {
            info.setDeletedByBroker(bs.readBoolean());
        }
    }

    /**
     * Write the booleans that this object uses to a BooleanStream
     *
     * @param wireFormat the OpenWireFormat instance to use
     * @param source the object to marshal
     * @param bs the boolean stream where the type's booleans are written
     *
     * @throws IOException if an error occurs while writing the data
     */
    public int tightMarshal1(OpenWireFormat wireFormat, Object source, BooleanStream bs) throws IOException {
        OpenWireBlobMessage info = (OpenWireBlobMessage) source;
        int version = wireFormat.getVersion();

        int rc = super.tightMarshal1(wireFormat, source, bs);
        if (version >= 3) {
            rc += tightMarshalString1(info.getRemoteBlobUrl(), bs);
        }
        if (version >= 3) {
            rc += tightMarshalString1(info.getMimeType(), bs);
        }
        if (version >= 3) {
            bs.writeBoolean(info.isDeletedByBroker());
        }

        return rc + 0;
    }

    /**
     * Write a object instance to data output stream
     *
     * @param wireFormat the OpenWireFormat instance to use
     * @param source the object to marshal
     * @param dataOut the DataOut where the properties are written
     * @param bs the boolean stream where the type's booleans are written
     *
     * @throws IOException if an error occurs while writing the data
     */
    public void tightMarshal2(OpenWireFormat wireFormat, Object source, DataOutput dataOut, BooleanStream bs) throws IOException {
        super.tightMarshal2(wireFormat, source, dataOut, bs);

        OpenWireBlobMessage info = (OpenWireBlobMessage) source;
        int version = wireFormat.getVersion();

        if (version >= 3) {
            tightMarshalString2(info.getRemoteBlobUrl(), dataOut, bs);
        }
        if (version >= 3) {
            tightMarshalString2(info.getMimeType(), dataOut, bs);
        }
        if (version >= 3) {
            bs.readBoolean();
        }
    }

    /**
     * Write the object to the output using loose marshaling.
     *
     * @throws IOException if an error occurs while writing the data
     */
    public void looseMarshal(OpenWireFormat wireFormat, Object source, DataOutput dataOut) throws IOException {
        OpenWireBlobMessage info = (OpenWireBlobMessage) source;
        int version = wireFormat.getVersion();

        super.looseMarshal(wireFormat, source, dataOut);
        if (version >= 3) {
            looseMarshalString(info.getRemoteBlobUrl(), dataOut);
        }
        if (version >= 3) {
            looseMarshalString(info.getMimeType(), dataOut);
        }
        if (version >= 3) {
            dataOut.writeBoolean(info.isDeletedByBroker());
        }
    }

    /**
     * Un-marshal an object instance from the data input stream
     *
     * @param target the object to un-marshal
     * @param dataIn the data input stream to build the object from
     *
     * @throws IOException if an error occurs while writing the data
     */
    public void looseUnmarshal(OpenWireFormat wireFormat, Object target, DataInput dataIn) throws IOException {
        super.looseUnmarshal(wireFormat, target, dataIn);

        OpenWireBlobMessage info = (OpenWireBlobMessage) target;
        int version = wireFormat.getVersion();

        if (version >= 3) {
            info.setRemoteBlobUrl(looseUnmarshalString(dataIn));
        }
        if (version >= 3) {
            info.setMimeType(looseUnmarshalString(dataIn));
        }
        if (version >= 3) {
            info.setDeletedByBroker(dataIn.readBoolean());
        }
    }
}
