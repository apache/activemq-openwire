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
 * Marshalling code for Open Wire for DestinationInfo
 *
 * NOTE!: This file is auto generated - do not modify!
 *
 */
public class DestinationInfoMarshaller extends BaseCommandMarshaller {

    /**
     * Return the type of Data Structure handled by this Marshaler
     *
     * @return short representation of the type data structure
     */
    public byte getDataStructureType() {
        return DestinationInfo.DATA_STRUCTURE_TYPE;
    }
    
    /**
     * @return a new instance of the managed type.
     */
    public DataStructure createObject() {
        return new DestinationInfo();
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

        DestinationInfo info = (DestinationInfo) target;

        info.setConnectionId((ConnectionId) tightUnmarsalCachedObject(wireFormat, dataIn, bs));
        info.setDestination((OpenWireDestination) tightUnmarsalCachedObject(wireFormat, dataIn, bs));
        info.setOperationType(dataIn.readByte());
        info.setTimeout(tightUnmarshalLong(wireFormat, dataIn, bs));
        if (bs.readBoolean()) {
            short size = dataIn.readShort();
            BrokerId value[] = new BrokerId[size];
            for (int i = 0; i < size; i++) {
                value[i] = (BrokerId) tightUnmarsalNestedObject(wireFormat,dataIn, bs);
            }
            info.setBrokerPath(value);
        } else {
            info.setBrokerPath(null);
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
        DestinationInfo info = (DestinationInfo) source;

        int rc = super.tightMarshal1(wireFormat, source, bs);
        rc += tightMarshalCachedObject1(wireFormat, (DataStructure)info.getConnectionId(), bs);
        rc += tightMarshalCachedObject1(wireFormat, (DataStructure)info.getDestination(), bs);
        rc += tightMarshalLong1(wireFormat, info.getTimeout(), bs);
        rc += tightMarshalObjectArray1(wireFormat, info.getBrokerPath(), bs);

        return rc + 1;
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

        DestinationInfo info = (DestinationInfo) source;

        tightMarshalCachedObject2(wireFormat, (DataStructure)info.getConnectionId(), dataOut, bs);
        tightMarshalCachedObject2(wireFormat, (DataStructure)info.getDestination(), dataOut, bs);
        dataOut.writeByte(info.getOperationType());
        tightMarshalLong2(wireFormat, info.getTimeout(), dataOut, bs);
        tightMarshalObjectArray2(wireFormat, info.getBrokerPath(), dataOut, bs);
    }

    /**
     * Write the object to the output using loose marshaling.
     *
     * @throws IOException if an error occurs while writing the data
     */
    public void looseMarshal(OpenWireFormat wireFormat, Object source, DataOutput dataOut) throws IOException {
        DestinationInfo info = (DestinationInfo) source;

        super.looseMarshal(wireFormat, source, dataOut);
        looseMarshalCachedObject(wireFormat, (DataStructure)info.getConnectionId(), dataOut);
        looseMarshalCachedObject(wireFormat, (DataStructure)info.getDestination(), dataOut);
        dataOut.writeByte(info.getOperationType());
        looseMarshalLong(wireFormat, info.getTimeout(), dataOut);
        looseMarshalObjectArray(wireFormat, info.getBrokerPath(), dataOut);
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

        DestinationInfo info = (DestinationInfo) target;

        info.setConnectionId((ConnectionId) looseUnmarsalCachedObject(wireFormat, dataIn));
        info.setDestination((OpenWireDestination) looseUnmarsalCachedObject(wireFormat, dataIn));
        info.setOperationType(dataIn.readByte());
        info.setTimeout(looseUnmarshalLong(wireFormat, dataIn));
        if (dataIn.readBoolean()) {
            short size = dataIn.readShort();
            BrokerId value[] = new BrokerId[size];
            for (int i = 0; i < size; i++) {
                value[i] = (BrokerId) looseUnmarsalNestedObject(wireFormat,dataIn);
            }
            info.setBrokerPath(value);
        } else {
            info.setBrokerPath(null);
        }
    }
}
