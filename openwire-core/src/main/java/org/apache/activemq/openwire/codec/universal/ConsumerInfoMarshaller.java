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
 * Marshalling code for Open Wire for ConsumerInfo
 *
 * NOTE!: This file is auto generated - do not modify!
 *
 */
public class ConsumerInfoMarshaller extends BaseCommandMarshaller {

    /**
     * Return the type of Data Structure handled by this Marshaler
     *
     * @return short representation of the type data structure
     */
    public byte getDataStructureType() {
        return ConsumerInfo.DATA_STRUCTURE_TYPE;
    }
    
    /**
     * @return a new instance of the managed type.
     */
    public DataStructure createObject() {
        return new ConsumerInfo();
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

        ConsumerInfo info = (ConsumerInfo) target;
        int version = wireFormat.getVersion();

        info.setConsumerId((ConsumerId) tightUnmarsalCachedObject(wireFormat, dataIn, bs));
        info.setBrowser(bs.readBoolean());
        info.setDestination((OpenWireDestination) tightUnmarsalCachedObject(wireFormat, dataIn, bs));
        info.setPrefetchSize(dataIn.readInt());
        info.setMaximumPendingMessageLimit(dataIn.readInt());
        info.setDispatchAsync(bs.readBoolean());
        info.setSelector(tightUnmarshalString(dataIn, bs));
        if (version >= 10) {
            info.setClientId(tightUnmarshalString(dataIn, bs));
        }
        info.setSubscriptionName(tightUnmarshalString(dataIn, bs));
        info.setNoLocal(bs.readBoolean());
        info.setExclusive(bs.readBoolean());
        info.setRetroactive(bs.readBoolean());
        info.setPriority(dataIn.readByte());
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
        info.setAdditionalPredicate((Object) tightUnmarsalNestedObject(wireFormat, dataIn, bs));
        info.setNetworkSubscription(bs.readBoolean());
        info.setOptimizedAcknowledge(bs.readBoolean());
        info.setNoRangeAcks(bs.readBoolean());
        if (version >= 4) {
            if (bs.readBoolean()) {
                short size = dataIn.readShort();
                ConsumerId value[] = new ConsumerId[size];
                for (int i = 0; i < size; i++) {
                    value[i] = (ConsumerId) tightUnmarsalNestedObject(wireFormat,dataIn, bs);
                }
                info.setNetworkConsumerPath(value);
            } else {
                info.setNetworkConsumerPath(null);
            }
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
        ConsumerInfo info = (ConsumerInfo) source;
        int version = wireFormat.getVersion();

        int rc = super.tightMarshal1(wireFormat, source, bs);
        rc += tightMarshalCachedObject1(wireFormat, (DataStructure)info.getConsumerId(), bs);
        bs.writeBoolean(info.isBrowser());
        rc += tightMarshalCachedObject1(wireFormat, (DataStructure)info.getDestination(), bs);
        bs.writeBoolean(info.isDispatchAsync());
        rc += tightMarshalString1(info.getSelector(), bs);
        if (version >= 10) {
            rc += tightMarshalString1(info.getClientId(), bs);
        }
        rc += tightMarshalString1(info.getSubscriptionName(), bs);
        bs.writeBoolean(info.isNoLocal());
        bs.writeBoolean(info.isExclusive());
        bs.writeBoolean(info.isRetroactive());
        rc += tightMarshalObjectArray1(wireFormat, info.getBrokerPath(), bs);
        rc += tightMarshalNestedObject1(wireFormat, (DataStructure)info.getAdditionalPredicate(), bs);
        bs.writeBoolean(info.isNetworkSubscription());
        bs.writeBoolean(info.isOptimizedAcknowledge());
        bs.writeBoolean(info.isNoRangeAcks());
        if (version >= 4) {
            rc += tightMarshalObjectArray1(wireFormat, info.getNetworkConsumerPath(), bs);
        }

        return rc + 9;
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

        ConsumerInfo info = (ConsumerInfo) source;
        int version = wireFormat.getVersion();

        tightMarshalCachedObject2(wireFormat, (DataStructure)info.getConsumerId(), dataOut, bs);
        bs.readBoolean();
        tightMarshalCachedObject2(wireFormat, (DataStructure)info.getDestination(), dataOut, bs);
        dataOut.writeInt(info.getPrefetchSize());
        dataOut.writeInt(info.getMaximumPendingMessageLimit());
        bs.readBoolean();
        tightMarshalString2(info.getSelector(), dataOut, bs);
        if (version >= 10) {
            tightMarshalString2(info.getClientId(), dataOut, bs);
        }
        tightMarshalString2(info.getSubscriptionName(), dataOut, bs);
        bs.readBoolean();
        bs.readBoolean();
        bs.readBoolean();
        dataOut.writeByte(info.getPriority());
        tightMarshalObjectArray2(wireFormat, info.getBrokerPath(), dataOut, bs);
        tightMarshalNestedObject2(wireFormat, (DataStructure)info.getAdditionalPredicate(), dataOut, bs);
        bs.readBoolean();
        bs.readBoolean();
        bs.readBoolean();
        if (version >= 4) {
            tightMarshalObjectArray2(wireFormat, info.getNetworkConsumerPath(), dataOut, bs);
        }
    }

    /**
     * Write the object to the output using loose marshaling.
     *
     * @throws IOException if an error occurs while writing the data
     */
    public void looseMarshal(OpenWireFormat wireFormat, Object source, DataOutput dataOut) throws IOException {
        ConsumerInfo info = (ConsumerInfo) source;
        int version = wireFormat.getVersion();

        super.looseMarshal(wireFormat, source, dataOut);
        looseMarshalCachedObject(wireFormat, (DataStructure)info.getConsumerId(), dataOut);
        dataOut.writeBoolean(info.isBrowser());
        looseMarshalCachedObject(wireFormat, (DataStructure)info.getDestination(), dataOut);
        dataOut.writeInt(info.getPrefetchSize());
        dataOut.writeInt(info.getMaximumPendingMessageLimit());
        dataOut.writeBoolean(info.isDispatchAsync());
        looseMarshalString(info.getSelector(), dataOut);
        if (version >= 10) {
            looseMarshalString(info.getClientId(), dataOut);
        }
        looseMarshalString(info.getSubscriptionName(), dataOut);
        dataOut.writeBoolean(info.isNoLocal());
        dataOut.writeBoolean(info.isExclusive());
        dataOut.writeBoolean(info.isRetroactive());
        dataOut.writeByte(info.getPriority());
        looseMarshalObjectArray(wireFormat, info.getBrokerPath(), dataOut);
        looseMarshalNestedObject(wireFormat, (DataStructure)info.getAdditionalPredicate(), dataOut);
        dataOut.writeBoolean(info.isNetworkSubscription());
        dataOut.writeBoolean(info.isOptimizedAcknowledge());
        dataOut.writeBoolean(info.isNoRangeAcks());
        if (version >= 4) {
            looseMarshalObjectArray(wireFormat, info.getNetworkConsumerPath(), dataOut);
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

        ConsumerInfo info = (ConsumerInfo) target;
        int version = wireFormat.getVersion();

        info.setConsumerId((ConsumerId) looseUnmarsalCachedObject(wireFormat, dataIn));
        info.setBrowser(dataIn.readBoolean());
        info.setDestination((OpenWireDestination) looseUnmarsalCachedObject(wireFormat, dataIn));
        info.setPrefetchSize(dataIn.readInt());
        info.setMaximumPendingMessageLimit(dataIn.readInt());
        info.setDispatchAsync(dataIn.readBoolean());
        info.setSelector(looseUnmarshalString(dataIn));
        if (version >= 10) {
            info.setClientId(looseUnmarshalString(dataIn));
        }
        info.setSubscriptionName(looseUnmarshalString(dataIn));
        info.setNoLocal(dataIn.readBoolean());
        info.setExclusive(dataIn.readBoolean());
        info.setRetroactive(dataIn.readBoolean());
        info.setPriority(dataIn.readByte());
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
        info.setAdditionalPredicate((Object) looseUnmarsalNestedObject(wireFormat, dataIn));
        info.setNetworkSubscription(dataIn.readBoolean());
        info.setOptimizedAcknowledge(dataIn.readBoolean());
        info.setNoRangeAcks(dataIn.readBoolean());
        if (version >= 4) {
            if (dataIn.readBoolean()) {
                short size = dataIn.readShort();
                ConsumerId value[] = new ConsumerId[size];
                for (int i = 0; i < size; i++) {
                    value[i] = (ConsumerId) looseUnmarsalNestedObject(wireFormat,dataIn);
                }
                info.setNetworkConsumerPath(value);
            } else {
                info.setNetworkConsumerPath(null);
            }
        }
    }
}
