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
 * Marshalling code for Open Wire for BrokerInfo
 *
 * NOTE!: This file is auto generated - do not modify!
 *
 */
public class BrokerInfoMarshaller extends BaseCommandMarshaller {

    /**
     * Return the type of Data Structure handled by this Marshaler
     *
     * @return short representation of the type data structure
     */
    public byte getDataStructureType() {
        return BrokerInfo.DATA_STRUCTURE_TYPE;
    }
    
    /**
     * @return a new instance of the managed type.
     */
    public DataStructure createObject() {
        return new BrokerInfo();
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

        BrokerInfo info = (BrokerInfo) target;
        int version = wireFormat.getVersion();

        info.setBrokerId((BrokerId) tightUnmarsalCachedObject(wireFormat, dataIn, bs));
        info.setBrokerURL(tightUnmarshalString(dataIn, bs));
        if (bs.readBoolean()) {
            short size = dataIn.readShort();
            BrokerInfo value[] = new BrokerInfo[size];
            for (int i = 0; i < size; i++) {
                value[i] = (BrokerInfo) tightUnmarsalNestedObject(wireFormat,dataIn, bs);
            }
            info.setPeerBrokerInfos(value);
        } else {
            info.setPeerBrokerInfos(null);
        }
        info.setBrokerName(tightUnmarshalString(dataIn, bs));
        info.setSlaveBroker(bs.readBoolean());
        info.setMasterBroker(bs.readBoolean());
        info.setFaultTolerantConfiguration(bs.readBoolean());
        if (version >= 2) {
            info.setDuplexConnection(bs.readBoolean());
        }
        if (version >= 2) {
            info.setNetworkConnection(bs.readBoolean());
        }
        if (version >= 2) {
            info.setConnectionId(tightUnmarshalLong(wireFormat, dataIn, bs));
        }
        if (version >= 3) {
            info.setBrokerUploadUrl(tightUnmarshalString(dataIn, bs));
        }
        if (version >= 3) {
            info.setNetworkProperties(tightUnmarshalString(dataIn, bs));
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
        BrokerInfo info = (BrokerInfo) source;
        int version = wireFormat.getVersion();

        int rc = super.tightMarshal1(wireFormat, source, bs);
        rc += tightMarshalCachedObject1(wireFormat, (DataStructure)info.getBrokerId(), bs);
        rc += tightMarshalString1(info.getBrokerURL(), bs);
        rc += tightMarshalObjectArray1(wireFormat, info.getPeerBrokerInfos(), bs);
        rc += tightMarshalString1(info.getBrokerName(), bs);
        bs.writeBoolean(info.isSlaveBroker());
        bs.writeBoolean(info.isMasterBroker());
        bs.writeBoolean(info.isFaultTolerantConfiguration());
        if (version >= 2) {
            bs.writeBoolean(info.isDuplexConnection());
        }
        if (version >= 2) {
            bs.writeBoolean(info.isNetworkConnection());
        }
        if (version >= 2) {
            rc += tightMarshalLong1(wireFormat, info.getConnectionId(), bs);
        }
        if (version >= 3) {
            rc += tightMarshalString1(info.getBrokerUploadUrl(), bs);
        }
        if (version >= 3) {
            rc += tightMarshalString1(info.getNetworkProperties(), bs);
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

        BrokerInfo info = (BrokerInfo) source;
        int version = wireFormat.getVersion();

        tightMarshalCachedObject2(wireFormat, (DataStructure)info.getBrokerId(), dataOut, bs);
        tightMarshalString2(info.getBrokerURL(), dataOut, bs);
        tightMarshalObjectArray2(wireFormat, info.getPeerBrokerInfos(), dataOut, bs);
        tightMarshalString2(info.getBrokerName(), dataOut, bs);
        bs.readBoolean();
        bs.readBoolean();
        bs.readBoolean();
        if (version >= 2) {
            bs.readBoolean();
        }
        if (version >= 2) {
            bs.readBoolean();
        }
        if (version >= 2) {
            tightMarshalLong2(wireFormat, info.getConnectionId(), dataOut, bs);
        }
        if (version >= 3) {
            tightMarshalString2(info.getBrokerUploadUrl(), dataOut, bs);
        }
        if (version >= 3) {
            tightMarshalString2(info.getNetworkProperties(), dataOut, bs);
        }
    }

    /**
     * Write the object to the output using loose marshaling.
     *
     * @throws IOException if an error occurs while writing the data
     */
    public void looseMarshal(OpenWireFormat wireFormat, Object source, DataOutput dataOut) throws IOException {
        BrokerInfo info = (BrokerInfo) source;
        int version = wireFormat.getVersion();

        super.looseMarshal(wireFormat, source, dataOut);
        looseMarshalCachedObject(wireFormat, (DataStructure)info.getBrokerId(), dataOut);
        looseMarshalString(info.getBrokerURL(), dataOut);
        looseMarshalObjectArray(wireFormat, info.getPeerBrokerInfos(), dataOut);
        looseMarshalString(info.getBrokerName(), dataOut);
        dataOut.writeBoolean(info.isSlaveBroker());
        dataOut.writeBoolean(info.isMasterBroker());
        dataOut.writeBoolean(info.isFaultTolerantConfiguration());
        if (version >= 2) {
            dataOut.writeBoolean(info.isDuplexConnection());
        }
        if (version >= 2) {
            dataOut.writeBoolean(info.isNetworkConnection());
        }
        if (version >= 2) {
            looseMarshalLong(wireFormat, info.getConnectionId(), dataOut);
        }
        if (version >= 3) {
            looseMarshalString(info.getBrokerUploadUrl(), dataOut);
        }
        if (version >= 3) {
            looseMarshalString(info.getNetworkProperties(), dataOut);
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

        BrokerInfo info = (BrokerInfo) target;
        int version = wireFormat.getVersion();

        info.setBrokerId((BrokerId) looseUnmarsalCachedObject(wireFormat, dataIn));
        info.setBrokerURL(looseUnmarshalString(dataIn));
        if (dataIn.readBoolean()) {
            short size = dataIn.readShort();
            BrokerInfo value[] = new BrokerInfo[size];
            for (int i = 0; i < size; i++) {
                value[i] = (BrokerInfo) looseUnmarsalNestedObject(wireFormat,dataIn);
            }
            info.setPeerBrokerInfos(value);
        } else {
            info.setPeerBrokerInfos(null);
        }
        info.setBrokerName(looseUnmarshalString(dataIn));
        info.setSlaveBroker(dataIn.readBoolean());
        info.setMasterBroker(dataIn.readBoolean());
        info.setFaultTolerantConfiguration(dataIn.readBoolean());
        if (version >= 2) {
            info.setDuplexConnection(dataIn.readBoolean());
        }
        if (version >= 2) {
            info.setNetworkConnection(dataIn.readBoolean());
        }
        if (version >= 2) {
            info.setConnectionId(looseUnmarshalLong(wireFormat, dataIn));
        }
        if (version >= 3) {
            info.setBrokerUploadUrl(looseUnmarshalString(dataIn));
        }
        if (version >= 3) {
            info.setNetworkProperties(looseUnmarshalString(dataIn));
        }
    }
}
