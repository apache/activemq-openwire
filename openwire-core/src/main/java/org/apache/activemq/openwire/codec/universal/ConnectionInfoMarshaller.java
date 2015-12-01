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
 * Marshalling code for Open Wire for ConnectionInfo
 *
 * NOTE!: This file is auto generated - do not modify!
 *
 */
public class ConnectionInfoMarshaller extends BaseCommandMarshaller {

    /**
     * Return the type of Data Structure handled by this Marshaler
     *
     * @return short representation of the type data structure
     */
    public byte getDataStructureType() {
        return ConnectionInfo.DATA_STRUCTURE_TYPE;
    }
    
    /**
     * @return a new instance of the managed type.
     */
    public DataStructure createObject() {
        return new ConnectionInfo();
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

        ConnectionInfo info = (ConnectionInfo) target;
        int version = wireFormat.getVersion();

        info.setConnectionId((ConnectionId) tightUnmarsalCachedObject(wireFormat, dataIn, bs));
        info.setClientId(tightUnmarshalString(dataIn, bs));
        info.setPassword(tightUnmarshalString(dataIn, bs));
        info.setUserName(tightUnmarshalString(dataIn, bs));
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
        info.setBrokerMasterConnector(bs.readBoolean());
        info.setManageable(bs.readBoolean());
        if (version >= 2) {
            info.setClientMaster(bs.readBoolean());
        }
        if (version >= 6) {
            info.setFaultTolerant(bs.readBoolean());
        }
        if (version >= 6) {
            info.setFailoverReconnect(bs.readBoolean());
        }
        if (version >= 8) {
            info.setClientIp(tightUnmarshalString(dataIn, bs));
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
        ConnectionInfo info = (ConnectionInfo) source;
        int version = wireFormat.getVersion();

        int rc = super.tightMarshal1(wireFormat, source, bs);
        rc += tightMarshalCachedObject1(wireFormat, (DataStructure)info.getConnectionId(), bs);
        rc += tightMarshalString1(info.getClientId(), bs);
        rc += tightMarshalString1(info.getPassword(), bs);
        rc += tightMarshalString1(info.getUserName(), bs);
        rc += tightMarshalObjectArray1(wireFormat, info.getBrokerPath(), bs);
        bs.writeBoolean(info.isBrokerMasterConnector());
        bs.writeBoolean(info.isManageable());
        if (version >= 2) {
            bs.writeBoolean(info.isClientMaster());
        }
        if (version >= 6) {
            bs.writeBoolean(info.isFaultTolerant());
        }
        if (version >= 6) {
            bs.writeBoolean(info.isFailoverReconnect());
        }
        if (version >= 8) {
            rc += tightMarshalString1(info.getClientIp(), bs);
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

        ConnectionInfo info = (ConnectionInfo) source;
        int version = wireFormat.getVersion();

        tightMarshalCachedObject2(wireFormat, (DataStructure)info.getConnectionId(), dataOut, bs);
        tightMarshalString2(info.getClientId(), dataOut, bs);
        tightMarshalString2(info.getPassword(), dataOut, bs);
        tightMarshalString2(info.getUserName(), dataOut, bs);
        tightMarshalObjectArray2(wireFormat, info.getBrokerPath(), dataOut, bs);
        bs.readBoolean();
        bs.readBoolean();
        if (version >= 2) {
            bs.readBoolean();
        }
        if (version >= 6) {
            bs.readBoolean();
        }
        if (version >= 6) {
            bs.readBoolean();
        }
        if (version >= 8) {
            tightMarshalString2(info.getClientIp(), dataOut, bs);
        }
    }

    /**
     * Write the object to the output using loose marshaling.
     *
     * @throws IOException if an error occurs while writing the data
     */
    public void looseMarshal(OpenWireFormat wireFormat, Object source, DataOutput dataOut) throws IOException {
        ConnectionInfo info = (ConnectionInfo) source;
        int version = wireFormat.getVersion();

        super.looseMarshal(wireFormat, source, dataOut);
        looseMarshalCachedObject(wireFormat, (DataStructure)info.getConnectionId(), dataOut);
        looseMarshalString(info.getClientId(), dataOut);
        looseMarshalString(info.getPassword(), dataOut);
        looseMarshalString(info.getUserName(), dataOut);
        looseMarshalObjectArray(wireFormat, info.getBrokerPath(), dataOut);
        dataOut.writeBoolean(info.isBrokerMasterConnector());
        dataOut.writeBoolean(info.isManageable());
        if (version >= 2) {
            dataOut.writeBoolean(info.isClientMaster());
        }
        if (version >= 6) {
            dataOut.writeBoolean(info.isFaultTolerant());
        }
        if (version >= 6) {
            dataOut.writeBoolean(info.isFailoverReconnect());
        }
        if (version >= 8) {
            looseMarshalString(info.getClientIp(), dataOut);
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

        ConnectionInfo info = (ConnectionInfo) target;
        int version = wireFormat.getVersion();

        info.setConnectionId((ConnectionId) looseUnmarsalCachedObject(wireFormat, dataIn));
        info.setClientId(looseUnmarshalString(dataIn));
        info.setPassword(looseUnmarshalString(dataIn));
        info.setUserName(looseUnmarshalString(dataIn));
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
        info.setBrokerMasterConnector(dataIn.readBoolean());
        info.setManageable(dataIn.readBoolean());
        if (version >= 2) {
            info.setClientMaster(dataIn.readBoolean());
        }
        if (version >= 6) {
            info.setFaultTolerant(dataIn.readBoolean());
        }
        if (version >= 6) {
            info.setFailoverReconnect(dataIn.readBoolean());
        }
        if (version >= 8) {
            info.setClientIp(looseUnmarshalString(dataIn));
        }
    }
}
