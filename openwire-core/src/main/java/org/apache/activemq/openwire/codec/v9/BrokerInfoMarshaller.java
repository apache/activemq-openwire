/**
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
package org.apache.activemq.openwire.codec.v9;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.activemq.openwire.codec.BooleanStream;
import org.apache.activemq.openwire.codec.OpenWireFormat;
import org.apache.activemq.openwire.commands.BrokerId;
import org.apache.activemq.openwire.commands.BrokerInfo;
import org.apache.activemq.openwire.commands.DataStructure;

public class BrokerInfoMarshaller extends BaseCommandMarshaller {

    /**
     * Return the type of Data Structure we marshal
     *
     * @return short representation of the type data structure
     */
    @Override
    public byte getDataStructureType() {
        return BrokerInfo.DATA_STRUCTURE_TYPE;
    }

    /**
     * @return a new object instance
     */
    @Override
    public DataStructure createObject() {
        return new BrokerInfo();
    }

    /**
     * Un-marshal an object instance from the data input stream
     *
     * @param o
     *        the object to un-marshal
     * @param dataIn
     *        the data input stream to build the object from
     * @throws IOException
     */
    @Override
    public void tightUnmarshal(OpenWireFormat wireFormat, Object o, DataInput dataIn, BooleanStream bs) throws IOException {
        super.tightUnmarshal(wireFormat, o, dataIn, bs);

        BrokerInfo info = (BrokerInfo) o;
        info.setBrokerId((BrokerId) tightUnmarsalCachedObject(wireFormat, dataIn, bs));
        info.setBrokerURL(tightUnmarshalString(dataIn, bs));

        if (bs.readBoolean()) {
            short size = dataIn.readShort();
            BrokerInfo value[] = new BrokerInfo[size];
            for (int i = 0; i < size; i++) {
                value[i] = (BrokerInfo) tightUnmarsalNestedObject(wireFormat, dataIn, bs);
            }
            info.setPeerBrokerInfos(value);
        } else {
            info.setPeerBrokerInfos(null);
        }
        info.setBrokerName(tightUnmarshalString(dataIn, bs));
        info.setSlaveBroker(bs.readBoolean());
        info.setMasterBroker(bs.readBoolean());
        info.setFaultTolerantConfiguration(bs.readBoolean());
        info.setDuplexConnection(bs.readBoolean());
        info.setNetworkConnection(bs.readBoolean());
        info.setConnectionId(tightUnmarshalLong(wireFormat, dataIn, bs));
        info.setBrokerUploadUrl(tightUnmarshalString(dataIn, bs));
        info.setNetworkProperties(tightUnmarshalString(dataIn, bs));
    }

    /**
     * Write the booleans that this object uses to a BooleanStream
     */
    @Override
    public int tightMarshal1(OpenWireFormat wireFormat, Object o, BooleanStream bs) throws IOException {
        BrokerInfo info = (BrokerInfo) o;

        int rc = super.tightMarshal1(wireFormat, o, bs);
        rc += tightMarshalCachedObject1(wireFormat, info.getBrokerId(), bs);
        rc += tightMarshalString1(info.getBrokerURL(), bs);
        rc += tightMarshalObjectArray1(wireFormat, info.getPeerBrokerInfos(), bs);
        rc += tightMarshalString1(info.getBrokerName(), bs);
        bs.writeBoolean(info.isSlaveBroker());
        bs.writeBoolean(info.isMasterBroker());
        bs.writeBoolean(info.isFaultTolerantConfiguration());
        bs.writeBoolean(info.isDuplexConnection());
        bs.writeBoolean(info.isNetworkConnection());
        rc += tightMarshalLong1(wireFormat, info.getConnectionId(), bs);
        rc += tightMarshalString1(info.getBrokerUploadUrl(), bs);
        rc += tightMarshalString1(info.getNetworkProperties(), bs);

        return rc + 0;
    }

    /**
     * Write a object instance to data output stream
     *
     * @param o
     *        the instance to be marshaled
     * @param dataOut
     *        the output stream
     * @throws IOException
     *         thrown if an error occurs
     */
    @Override
    public void tightMarshal2(OpenWireFormat wireFormat, Object o, DataOutput dataOut, BooleanStream bs) throws IOException {
        super.tightMarshal2(wireFormat, o, dataOut, bs);

        BrokerInfo info = (BrokerInfo) o;
        tightMarshalCachedObject2(wireFormat, info.getBrokerId(), dataOut, bs);
        tightMarshalString2(info.getBrokerURL(), dataOut, bs);
        tightMarshalObjectArray2(wireFormat, info.getPeerBrokerInfos(), dataOut, bs);
        tightMarshalString2(info.getBrokerName(), dataOut, bs);
        bs.readBoolean();
        bs.readBoolean();
        bs.readBoolean();
        bs.readBoolean();
        bs.readBoolean();
        tightMarshalLong2(wireFormat, info.getConnectionId(), dataOut, bs);
        tightMarshalString2(info.getBrokerUploadUrl(), dataOut, bs);
        tightMarshalString2(info.getNetworkProperties(), dataOut, bs);
    }

    /**
     * Un-marshal an object instance from the data input stream
     *
     * @param o
     *        the object to un-marshal
     * @param dataIn
     *        the data input stream to build the object from
     * @throws IOException
     */
    @Override
    public void looseUnmarshal(OpenWireFormat wireFormat, Object o, DataInput dataIn) throws IOException {
        super.looseUnmarshal(wireFormat, o, dataIn);

        BrokerInfo info = (BrokerInfo) o;
        info.setBrokerId((BrokerId) looseUnmarsalCachedObject(wireFormat, dataIn));
        info.setBrokerURL(looseUnmarshalString(dataIn));

        if (dataIn.readBoolean()) {
            short size = dataIn.readShort();
            BrokerInfo value[] = new BrokerInfo[size];
            for (int i = 0; i < size; i++) {
                value[i] = (BrokerInfo) looseUnmarsalNestedObject(wireFormat, dataIn);
            }
            info.setPeerBrokerInfos(value);
        } else {
            info.setPeerBrokerInfos(null);
        }
        info.setBrokerName(looseUnmarshalString(dataIn));
        info.setSlaveBroker(dataIn.readBoolean());
        info.setMasterBroker(dataIn.readBoolean());
        info.setFaultTolerantConfiguration(dataIn.readBoolean());
        info.setDuplexConnection(dataIn.readBoolean());
        info.setNetworkConnection(dataIn.readBoolean());
        info.setConnectionId(looseUnmarshalLong(wireFormat, dataIn));
        info.setBrokerUploadUrl(looseUnmarshalString(dataIn));
        info.setNetworkProperties(looseUnmarshalString(dataIn));
    }

    /**
     * Write the booleans that this object uses to a BooleanStream
     */
    @Override
    public void looseMarshal(OpenWireFormat wireFormat, Object o, DataOutput dataOut) throws IOException {
        BrokerInfo info = (BrokerInfo) o;

        super.looseMarshal(wireFormat, o, dataOut);
        looseMarshalCachedObject(wireFormat, info.getBrokerId(), dataOut);
        looseMarshalString(info.getBrokerURL(), dataOut);
        looseMarshalObjectArray(wireFormat, info.getPeerBrokerInfos(), dataOut);
        looseMarshalString(info.getBrokerName(), dataOut);
        dataOut.writeBoolean(info.isSlaveBroker());
        dataOut.writeBoolean(info.isMasterBroker());
        dataOut.writeBoolean(info.isFaultTolerantConfiguration());
        dataOut.writeBoolean(info.isDuplexConnection());
        dataOut.writeBoolean(info.isNetworkConnection());
        looseMarshalLong(wireFormat, info.getConnectionId(), dataOut);
        looseMarshalString(info.getBrokerUploadUrl(), dataOut);
        looseMarshalString(info.getNetworkProperties(), dataOut);
    }
}
